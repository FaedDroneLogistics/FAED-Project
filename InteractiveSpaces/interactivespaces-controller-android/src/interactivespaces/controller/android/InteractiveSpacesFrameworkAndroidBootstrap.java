/*
 * Copyright (C) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package interactivespaces.controller.android;

import interactivespaces.controller.internal.osgi.OsgiControllerActivator;
import interactivespaces.service.androidos.AndroidOsService;
import interactivespaces.service.androidos.impl.SimpleAndroidOsService;
import interactivespaces.system.bootstrap.osgi.GeneralInteractiveSpacesSupportActivator;
import interactivespaces.system.core.configuration.ConfigurationProvider;
import interactivespaces.system.core.container.ContainerCustomizerProvider;
import interactivespaces.system.core.container.SimpleContainerCustomizerProvider;
import interactivespaces.system.core.logging.LoggingProvider;

import android.content.Context;
import android.content.res.AssetManager;
import android.preference.PreferenceManager;
import android.util.Log;
import org.apache.felix.framework.Felix;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

/**
 * A bootstrapper for Interactive Spaces on an Android device.
 *
 * @author Keith M. Hughes
 */
public class InteractiveSpacesFrameworkAndroidBootstrap {

  /**
   * Extensions on config files.
   */
  private static final String CONFIGURATION_FILES_EXTENSION = ".conf";

  /**
   * Subdirectory which will contain the bootstrap bundles.
   */
  public static final String BUNDLE_DIRECTORY_BOOTSTRAP = "bootstrap";

  /**
   * Where the config files are stored.
   */
  public static final String CONFIG_DIRECTORY = "config";

  /**
   * The OSGI framework which has been started.
   */
  private Framework framework;

  /**
   * All bundles installed.
   */
  private Set<Bundle> bundles = new HashSet<Bundle>();

  /**
   * The initial set of bundles to load.
   */
  private List<String> initialBundles;

  /**
   * The final set of bundles to load.
   */
  private List<String> finalBundles;

  /**
   * Mapping of JAR files to the bundles they are once loaded.
   */
  private Map<String, Bundle> jarToBundle = new HashMap<String, Bundle>();

  /**
   * Activator for the base Interactive Spaces services.
   */
  private GeneralInteractiveSpacesSupportActivator isSystemActivator;

  /**
   * Logging provider for the container.
   */
  private AndroidLoggingProvider loggingProvider;

  /**
   * Configuration provider for the container.
   */
  private ConfigurationProvider configurationProvider;

  /**
   * Container customizer provider for the container.
   */
  private SimpleContainerCustomizerProvider containerCustomizerProvider;

  /**
   * Start the framework up.
   *
   * @param context
   *          the Android context to start the framework up in.
   */
  public void startup(Map<String, String> config, Context context) {
    AssetManager assetManager = context.getAssets();

    try {
      createCoreServices(context);

      copyInitialBootstrapAssets(assetManager, context.getFilesDir());

      getBootstrapBundleJars(assetManager, BUNDLE_DIRECTORY_BOOTSTRAP);

      // If no bundle JAR files are in the directory, then exit.
      if (initialBundles.isEmpty() && finalBundles.isEmpty()) {
        System.out.println("No bundles to install.");
      }

      createAndStartFramework(config, context);

      registerCoreServices();

      List<Bundle> bundleList = new ArrayList<Bundle>();

      // Install bundle JAR files and remember the bundle objects.
      BundleContext ctxt = framework.getBundleContext();
      ctxt.addBundleListener(new BundleListener() {

        @Override
        public void bundleChanged(BundleEvent event) {
          bundleChangeEvent(event);
        }

      });

      isSystemActivator = new GeneralInteractiveSpacesSupportActivator();
      isSystemActivator.start(framework.getBundleContext());

      OsgiControllerActivator isControllerActivator = new OsgiControllerActivator();
      isControllerActivator.start(framework.getBundleContext());

      startBundles(assetManager, ctxt, bundleList, initialBundles, false);
      startBundles(assetManager, ctxt, bundleList, finalBundles, true);
    } catch (Exception ex) {
      System.err.println("Error starting framework: " + ex);
      ex.printStackTrace();
      System.exit(0);
    }
  }

  /**
   * Shut the controller down.
   */
  public void shutdown() {
    if (framework != null) {
      try {
        framework.stop();
      } catch (BundleException e) {
        Log.e("interactivespaces", "Exception during Interactive Spaces OSGi shutdown", e);
      }
      framework = null;
    }
  }

  /**
   * Create the core services to the base bundle which are platform dependent.
   *
   * @param context
   *          Android context for the service
   */
  public void createCoreServices(Context context) {
    loggingProvider = new AndroidLoggingProvider();
    configurationProvider =
        new AndroidConfigurationProvider(PreferenceManager.getDefaultSharedPreferences(context));

    containerCustomizerProvider = new SimpleContainerCustomizerProvider();
    containerCustomizerProvider.addService(AndroidOsService.SERVICE_NAME,
        new SimpleAndroidOsService(context));
  }

  /**
   * Register all bootstrap core services with the container.
   */
  public void registerCoreServices() {
    BundleContext bundleContext = framework.getBundleContext();
    bundleContext.registerService(LoggingProvider.class.getName(), loggingProvider, null);
    bundleContext.registerService(ConfigurationProvider.class.getName(), configurationProvider,
        null);
    bundleContext.registerService(ContainerCustomizerProvider.class.getName(),
        containerCustomizerProvider, null);
  }

  /**
   * An event happened on one of the bundles in the container.
   *
   * @param event
   *          the OSGi bundle event
   */
  private void bundleChangeEvent(BundleEvent event) {
    System.out.format("Bundle %s changed state to %d\n", event.getBundle().getLocation(), event
        .getBundle().getState());
  }

  /**
   * @param ctxt
   * @param bundleList
   * @param jars
   * @throws BundleException
   */
  private void startBundles(AssetManager assetManager, BundleContext ctxt, List<Bundle> bundleList,
      List<String> jars, boolean start) throws BundleException {
    for (String bundleFile : jars) {
      try {
        Bundle b = ctxt.installBundle(bundleFile, assetManager.open(bundleFile));

        bundleList.add(b);
        bundles.add(b);
        jarToBundle.put(bundleFile.substring(bundleFile.indexOf('/') + 1), b);
        System.out.format("Added bundle file %s with ID %d\n", bundleFile, b.getBundleId());
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    // Start all installed non-fragment bundles.
    for (final Bundle bundle : bundleList) {
      if (!isFragment(bundle)) {
        if (start)
          startBundle(bundle);
      }
    }
  }

  /**
   * Start a particular bundle.
   *
   * @param bundle
   *          the bundle to start
   */
  private void startBundle(Bundle bundle) {
    boolean started = true;
    try {
      System.out.format("Starting %s\n", bundle.getLocation());
      bundle.start();
      System.out.format("Started %s\n", bundle.getLocation());
    } catch (Exception e) {
      started = false;
      System.err.println("Exception " + bundle.getLocation());
      e.printStackTrace();
    }
  }

  /**
   * Create, configure, and start an OSGi framework instance. OO
   *
   * @return
   * @throws IOException
   * @throws BundleException
   */
  private void createAndStartFramework(Map<String, String> bootstrapConfig, Context context)
      throws BundleException, IOException {
    Map<String, String> m = new HashMap<String, String>();

    File filesDir = context.getFilesDir();
    m.put("interactivespaces.rootdir", filesDir.getAbsolutePath());

    // m.putAll(System.getProperties());
    m.put(Constants.FRAMEWORK_STORAGE_CLEAN, Constants.FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT);

    String delegations = getClassloaderDelegations(filesDir);
    if (delegations != null) {
      m.put(Constants.FRAMEWORK_BOOTDELEGATION, delegations);
    }

    m.put(Constants.FRAMEWORK_SYSTEMPACKAGES, getSystemPackages());

    m.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA, "log4j.properties");

    File file = new File(filesDir, "plugins-cache");
    file.mkdirs();
    m.put(Constants.FRAMEWORK_STORAGE, file.getCanonicalPath());

    m.put("felix.service.urlhandlers", "false");

    loadPropertyFiles(CONFIG_DIRECTORY, m);

    m.putAll(bootstrapConfig);

    framework = new Felix(m);
    framework.start();
  }

  /**
   * Get all system packages.
   *
   * @return an OSGi formatted list of system packages.
   */
  public String getSystemPackages() {
    List<String> packages = new ArrayList<String>();
    packages.add("org.osgi.framework; version=1.5");
    packages.add("org.osgi.service.event");
    packages.add("org.osgi.service.startlevel");
    packages.add("org.osgi.service.log");
    packages.add("org.osgi.util.tracker");
    packages.add("org.apache.felix.service.command");
    packages.add("org.osgi.service.packageadmin; version=1.2.0");
    packages.add("javax.xml");
    packages.add("javax.xml.xpath");
    packages.add("javax.xml.transform.sax");
    packages.add("javax.net");
    packages.add("javax.net.ssl");
    packages.add("javax.xml.bind");
    packages.add("javax.crypto");
    packages.add("javax.management");
    packages.add("javax.script");
    packages.add("javax.xml.datatype");
    packages.add("javax.xml.namespace");
    packages.add("javax.xml.parsers");
    packages.add("javax.crypto.spec");
    packages.add("javax.security.auth.callback");
    packages.add("interactivespaces.system.core.logging");
    packages.add("com.google.common.collect, interactivespaces, interactivespaces.activity");
    packages.add("interactivespaces.configuration");
    packages.add("interactivespaces.controller");
    packages.add("interactivespaces.controller.activity.installation");
    packages.add("interactivespaces.domain.basic");
    packages.add("interactivespaces.domain.basic.pojo");
    packages.add("interactivespaces.evaluation");
    packages.add("interactivespaces.master.server.remote.client");
    packages.add("interactivespaces.master.server.remote.client.ros");
    packages.add("interactivespaces.system");
    packages.add("interactivespaces.time");
    packages.add("org.apache.commons.logging; version=1.1.1");
    packages.add("org.apache.commons.logging.impl; version=1.1.1");
    packages.add("interactivespaces.activity.execution");
    packages.add("interactivespaces.activity.impl");
    packages.add("interactivespaces.activity.impl.binary");
    packages.add("interactivespaces.activity.impl.ros");
    packages.add("interactivespaces.activity.impl.web");
    packages.add("interactivespaces.activity.binary");
    packages.add("interactivespaces.activity.component");
    packages.add("interactivespaces.activity.component.ros");
    packages.add("interactivespaces.activity.component.web");
    packages.add("interactivespaces.event");
    packages.add("interactivespaces.event.trigger");
    packages.add("interactivespaces.util");
    packages.add("interactivespaces.util.concurrency");
    packages.add("interactivespaces.util.data");
    packages.add("interactivespaces.util.data.persist");
    packages.add("interactivespaces.util.io");
    packages.add("interactivespaces.util.process.restart");
    packages.add("interactivespaces.util.ros");
    packages.add("interactivespaces.util.uuid");
    packages.add("interactivespaces.util.web");
    packages.add("interactivespaces.service");
    packages.add("interactivespaces.service.web.server");
    packages.add("org.ros.osgi.common");
    packages.add("org.ros.node");
    packages.add("org.ros.node.topic");
    packages.add("org.ros.message");
    packages.add("org.ros.message.interactivespaces_msgs; version=0.0.0");
    packages.add("interactivespaces.service.androidos");
    packages.add("android.content");
    packages.add("android.hardware");
    packages.add("android.net");
    packages.add("android.util");

    StringBuilder pkgs = new StringBuilder();
    String separator = "";
    for (String p : packages) {
      pkgs.append(separator).append(p);
      separator = ", ";
    }

    return pkgs.toString();
  }

  /**
   * Get all of the bundles from the bootstrap folder.
   *
   * @param assetManager
   *          the android asset manager for the application
   * @param bootstrapFolderPath
   *          The folder where the bootstrap folders are stored.
   *
   * @throws IOException
   */
  private void getBootstrapBundleJars(AssetManager assetManager, String bootstrapFolderPath)
      throws IOException {
    // Look in the specified bundle directory to create a list
    // of all JAR files to install.
    initialBundles = new ArrayList<String>();
    finalBundles = new ArrayList<String>();

    for (String filename : assetManager.list(bootstrapFolderPath)) {
      if (filename.endsWith(".jar")) {
        if (filename.startsWith("interactivespaces")) {
          finalBundles.add(bootstrapFolderPath + "/" + filename);
        } else {
          initialBundles.add(bootstrapFolderPath + "/" + filename);
        }
      }
    }
  }

  /**
   * Is the bundle a fragment host?
   *
   * @param bundle
   * @return
   */
  private boolean isFragment(Bundle bundle) {
    return bundle.getHeaders().get(Constants.FRAGMENT_HOST) != null;
  }

  /**
   * Load all config files from the configuration folder.
   *
   * @param configFolder
   *          the configuration
   * @param properties
   *          where the properties from the configurations will be placed
   */
  private void loadPropertyFiles(String configFolder, Map<String, String> properties) {
    // Look in the specified bundle directory to create a list
    // of all JAR files to install.
    File[] files = new File(configFolder).listFiles(new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {
        return name.toLowerCase().endsWith(CONFIGURATION_FILES_EXTENSION);
      }
    });
    if (files == null || files.length == 0) {
      System.err.format("Couldn't load config files from %s\n", configFolder);
      return;
    }

    for (File file : files) {
      Properties props = new Properties();
      try {
        props.load(new FileInputStream(file));
        for (Entry<Object, Object> p : props.entrySet()) {
          properties.put((String) p.getKey(), (String) p.getValue());
        }
      } catch (IOException e) {
        System.err.format("Couldn't load config file %s\n", file);
      }
    }
  }

  private String getClassloaderDelegations(File filesDir) {
    File delegation = new File(filesDir, "lib/system/java/delegations.conf");
    if (delegation.exists()) {

      StringBuilder builder = new StringBuilder();
      String separator = "";

      BufferedReader reader = null;
      try {
        reader = new BufferedReader(new FileReader(delegation));

        String line;
        while ((line = reader.readLine()) != null) {
          builder.append(separator).append(line);
          separator = ",";
        }

        return builder.toString();
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        if (reader != null)
          try {
            reader.close();
          } catch (IOException e) {
            // Don't care. Closing.
          }
      }
    }

    return null;
  }

  /**
   * Copy all asserts needed for initial boot.
   *
   * @param assetManager
   *          the android asset manager for the application
   * @param filesDir
   *          the folder where items can be copied to
   */
  private void copyInitialBootstrapAssets(AssetManager assetManager, File filesDir) {
    try {
      File confDir = new File(filesDir, "config");
      confDir.mkdirs();
      File isConfigDir = new File(confDir, "interactivespaces");
      isConfigDir.mkdirs();
      File bootstrapDir = new File(filesDir, "bootstrap");
      bootstrapDir.mkdirs();
      File systemLibDir = new File(filesDir, "lib/system/java");
      systemLibDir.mkdirs();
      File logsDir = new File(filesDir, "logs");
      logsDir.mkdirs();
      File activitiesStagingDir = new File(filesDir, "controller/activities/staging");
      activitiesStagingDir.mkdirs();
      File activitiesInstalledDir = new File(filesDir, "controller/activities/installed");
      activitiesInstalledDir.mkdirs();

      copyAssetFile("config/container.conf", new File(confDir, "container.conf"), assetManager);

      String[] configFiles = assetManager.list("config/interactivespaces");
      for (String configFile : configFiles) {
        copyAssetFile("config/interactivespaces/" + configFile, new File(isConfigDir, configFile),
            assetManager);
      }

      copyAssetFile("lib/system/java/log4j.properties", new File(systemLibDir, "log4j.properties"),
          assetManager);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Copy an asset file.
   *
   * @param inputStream
   *          the input stream for the asset
   * @param outputFile
   *          where the file should be copied
   * @param assetManager
   *          TODO
   */
  private void copyAssetFile(String input, File outputFile, AssetManager assetManager) {

    System.out.format("Copying %s to %s\n", input, outputFile);

    byte buf[] = new byte[1024];
    int len;
    FileOutputStream fos = null;
    InputStream inputStream = null;
    try {
      inputStream = assetManager.open(input);
      fos = new FileOutputStream(outputFile);

      while ((len = inputStream.read(buf)) != -1) {
        fos.write(buf, 0, len);
      }
      fos.flush();
    } catch (IOException e) {

    } finally {
      if (fos != null) {
        try {
          fos.close();
        } catch (IOException e) {
          // Don't care
        }
      }

      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (IOException e) {
          // Don't care
        }
      }
    }
  }
}
