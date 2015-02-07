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

package interactivespaces.liveactivity.runtime;

import interactivespaces.SimpleInteractiveSpacesException;
import interactivespaces.activity.ActivityFilesystem;
import interactivespaces.configuration.Configuration;
import interactivespaces.liveactivity.runtime.domain.ActivityInstallationStatus;
import interactivespaces.liveactivity.runtime.domain.InstalledLiveActivity;
import interactivespaces.liveactivity.runtime.installation.ActivityInstallationListener;
import interactivespaces.liveactivity.runtime.installation.ActivityInstallationManager;
import interactivespaces.liveactivity.runtime.repository.LocalLiveActivityRepository;
import interactivespaces.resource.Version;
import interactivespaces.system.InteractiveSpacesEnvironment;
import interactivespaces.util.io.FileSupport;
import interactivespaces.util.io.FileSupportImpl;
import interactivespaces.util.web.HttpClientHttpContentCopier;
import interactivespaces.util.web.HttpContentCopier;

import com.google.common.collect.Lists;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link ActivityInstallationManager}.
 *
 * @author Keith M. Hughes
 */
public class SimpleActivityInstallationManager implements ActivityInstallationManager {

  /**
   * Configuration property giving the location of the activity staging directory.
   */
  public static final String CONTROLLER_APPLICATION_STAGING_DIRECTORY_PROPERTY =
      "interactivespaces.controller.activity.staging.directory";

  /**
   * The default folder for staging activity installs.
   */
  private static final String CONTROLLER_APPLICATIONS_STAGING_DEFAULT = "controller/activities/staging";

  /**
   * Mapping from UUID to the temporary file for an install.
   */
  private final Map<String, File> uuidToTemporary = new HashMap<String, File>();

  /**
   * Base directory where files will be staged as they are copied in.
   */
  private File stagingBaseDirectory;

  /**
   * Copies files from the remote location.
   */
  private final HttpContentCopier remoteCopier = new HttpClientHttpContentCopier();

  /**
   * The Interactive Spaces environment.
   */
  private final InteractiveSpacesEnvironment spaceEnvironment;

  /**
   * Local repository of controller information.
   */
  private final LocalLiveActivityRepository controllerRepository;

  /**
   * The storage manager for activities.
   */
  private final LiveActivityStorageManager activityStorageManager;

  /**
   * The listeners for this installer.
   */
  private final List<ActivityInstallationListener> listeners = new ArrayList<ActivityInstallationListener>();

  /**
   * File support to use.
   */
  private final FileSupport fileSupport = FileSupportImpl.INSTANCE;

  /**
   * Construct an activity installation manager.
   *
   * @param controllerRepository
   *          the controller repository
   * @param activityStorageManager
   *          the activity storage manager
   * @param spaceEnvironment
   *          the space environment
   */
  public SimpleActivityInstallationManager(LocalLiveActivityRepository controllerRepository,
      LiveActivityStorageManager activityStorageManager, InteractiveSpacesEnvironment spaceEnvironment) {
    this.controllerRepository = controllerRepository;
    this.activityStorageManager = activityStorageManager;
    this.spaceEnvironment = spaceEnvironment;
  }

  @Override
  public void startup() {
    remoteCopier.startup();

    Configuration systemConfiguration = spaceEnvironment.getSystemConfiguration();
    stagingBaseDirectory =
        new File(spaceEnvironment.getFilesystem().getInstallDirectory(), systemConfiguration.getPropertyString(
            CONTROLLER_APPLICATION_STAGING_DIRECTORY_PROPERTY, CONTROLLER_APPLICATIONS_STAGING_DEFAULT));
  }

  @Override
  public void shutdown() {
    remoteCopier.shutdown();
  }

  @Override
  public void copyActivity(String uuid, String uri) {
    String fileName = uuid + ".zip";

    File stagedLocation = null;
    synchronized (uuidToTemporary) {
      stagedLocation = uuidToTemporary.get(uuid);
      if (stagedLocation != null) {
        throw new SimpleInteractiveSpacesException("Activity with UUID already being copied: " + uuid);
      }

      stagedLocation = new File(stagingBaseDirectory, fileName);
      uuidToTemporary.put(uuid, stagedLocation);
    }

    remoteCopier.copy(uri, stagedLocation);
  }

  @Override
  public Date installActivity(String uuid, String activityIdentifyingName, Version version) {
    File stagedLocation = null;
    synchronized (uuidToTemporary) {
      stagedLocation = uuidToTemporary.get(uuid);
      if (stagedLocation == null) {
        throw new SimpleInteractiveSpacesException("No staged activity file with given UUID: " + uuid);
      }
    }

    ActivityFilesystem activityFilesystem = activityStorageManager.getActivityFilesystem(uuid);

    File installDirectory = activityFilesystem.getInstallDirectory();
    fileSupport.deleteDirectoryContents(installDirectory);
    fileSupport.unzip(stagedLocation, installDirectory);

    Date installedDate =
        persistInstallation(uuid, activityIdentifyingName, version,
            activityStorageManager.getBaseActivityLocation(uuid));

    spaceEnvironment.getLog().info(
        String.format("Activity %s version %s installed with uuid %s", activityIdentifyingName, version, uuid));

    notifyInstalledActivity(uuid);

    return installedDate;
  }

  /**
   * Persist information about the installation.
   *
   * @param uuid
   *          UUID of the installed activity
   * @param identifyingName
   *          identifying name of the installed activity
   * @param version
   *          version of the installed activity
   * @param baseInstallationLocation
   *          the root folder of the installation
   *
   * @return the date of the installation
   */
  private Date persistInstallation(String uuid, String identifyingName, Version version, File baseInstallationLocation) {
    // Make sure the app is only stored once.
    InstalledLiveActivity activity = controllerRepository.getInstalledLiveActivityByUuid(uuid);
    if (activity == null) {
      activity = controllerRepository.newInstalledLiveActivity();
    }
    Date installedDate = new Date(spaceEnvironment.getTimeProvider().getCurrentTime());

    activity.setUuid(uuid);
    activity.setIdentifyingName(identifyingName);
    activity.setVersion(version);
    activity.setBaseInstallationLocation(baseInstallationLocation.getAbsolutePath());
    activity.setLastDeployedDate(installedDate);
    activity.setInstallationStatus(ActivityInstallationStatus.OK);

    controllerRepository.saveInstalledLiveActivity(activity);

    return installedDate;
  }

  @Override
  public void removePackedActivity(String uuid) {
    File stagedLocation = null;
    synchronized (uuidToTemporary) {
      stagedLocation = uuidToTemporary.remove(uuid);
    }

    if (stagedLocation != null) {
      if (!stagedLocation.delete()) {
        spaceEnvironment.getLog().warn(
            String.format("Could not delete staged file %s for UUID %s", stagedLocation, uuid));
      }
    } else {
      spaceEnvironment.getLog().warn(String.format("No staged file with UUID %s", uuid));
    }
  }

  @Override
  public RemoveActivityResult removeActivity(String uuid) {
    // TODO(keith): Move this elsewhere
    RemoveActivityResult result;
    InstalledLiveActivity activity = controllerRepository.getInstalledLiveActivityByUuid(uuid);
    if (activity != null) {
      controllerRepository.deleteInstalledLiveActivity(activity);

      activityStorageManager.removeActivityLocation(uuid);

      result = RemoveActivityResult.SUCCESS;
    } else {
      result = RemoveActivityResult.DOESNT_EXIST;
    }

    notifyRemovedActivity(uuid, result);

    return result;
  }

  /**
   * Notify everyone who needs to know that an activity has been installed.
   *
   * @param uuid
   *          UUID of the installed activity.
   */
  private void notifyInstalledActivity(String uuid) {
    for (ActivityInstallationListener listener : getListeners()) {
      listener.onActivityInstall(uuid);
    }
  }

  /**
   * Notify everyone who needs to know that an activity has been removed.
   *
   * @param uuid
   *          UUID of the removed activity
   * @param result
   *          result of the removal
   */
  private void notifyRemovedActivity(String uuid, RemoveActivityResult result) {
    for (ActivityInstallationListener listener : getListeners()) {
      listener.onActivityRemove(uuid, result);
    }
  }

  @Override
  public void addActivityInstallationListener(ActivityInstallationListener listener) {
    synchronized (listeners) {
      listeners.add(listener);
    }
  }

  @Override
  public void removeActivityInstallationListener(ActivityInstallationListener listener) {
    synchronized (listeners) {
      listeners.remove(listener);
    }
  }

  /**
   * Obtain a fresh copy of the listener list in a thread-safe manner.
   *
   * @return the listeners
   */
  private List<ActivityInstallationListener> getListeners() {
    synchronized (listeners) {
      return Lists.newArrayList(listeners);
    }
  }
}
