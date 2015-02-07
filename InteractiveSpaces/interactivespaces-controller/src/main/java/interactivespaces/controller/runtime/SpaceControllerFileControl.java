/*
 * Copyright (C) 2013 Google Inc.
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

package interactivespaces.controller.runtime;

import interactivespaces.system.InteractiveSpacesEnvironment;
import interactivespaces.system.InteractiveSpacesSystemControl;
import interactivespaces.util.io.directorywatcher.DirectoryWatcher;
import interactivespaces.util.io.directorywatcher.DirectoryWatcherListener;
import interactivespaces.util.io.directorywatcher.SimpleDirectoryWatcher;
import interactivespaces.util.resource.ManagedResource;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * Handle control of the controller by using the filesystem.
 *
 * <p>
 * These command work by looking at the {@link #FOLDER_RUN_CONTROL} folder in the controller directory. Any files with
 * the names given are immediately deleted and then the name of the file is executed as a command.
 *
 * @author Keith M. Hughes
 */
public class SpaceControllerFileControl implements DirectoryWatcherListener, ManagedResource {

  /**
   * Default period for the watcher scans, in seconds.
   */
  public static final int WATCHER_PERIOD_DEFAULT = 10;

  /**
   * The subfolder of the container installation being watched for system control.
   */
  private static final String FOLDER_RUN_CONTROL = "run/control";

  /**
   * The command for shutting the entire container down.
   */
  public static final String COMMAND_SHUTDOWN = "shutdown";

  /**
   * The space environment to run in.
   */
  private InteractiveSpacesEnvironment spaceEnvironment;

  /**
   * The space controller control.
   */
  private SpaceControllerControl spaceControllerControl;

  /**
   * Full system control of the container.
   */
  private InteractiveSpacesSystemControl spaceSystemControl;

  /**
   * The directory watcher watching the directory for control files.
   */
  private DirectoryWatcher watcher;

  /**
   * How often the watcher will be run, in seconds.
   */
  private int watcherPeriod = WATCHER_PERIOD_DEFAULT;

  /**
   * Construct the control.
   *
   * @param spaceControllerControl
   *          the space controller control
   * @param spaceSystemControl
   *          system control for the container
   * @param spaceEnvironment
   *          the space environment
   */
  public SpaceControllerFileControl(SpaceControllerControl spaceControllerControl,
      InteractiveSpacesSystemControl spaceSystemControl, InteractiveSpacesEnvironment spaceEnvironment) {
    this.spaceControllerControl = spaceControllerControl;
    this.spaceSystemControl = spaceSystemControl;
    this.spaceEnvironment = spaceEnvironment;
  }

  @Override
  public void startup() {
    File controlDirectory = new File(spaceEnvironment.getFilesystem().getInstallDirectory(), FOLDER_RUN_CONTROL);
    watcher = new SimpleDirectoryWatcher(true);
    watcher.addDirectory(controlDirectory);
    watcher.addDirectoryWatcherListener(this);

    // The trains must keep running
    watcher.setStopOnException(false);

    watcher.startup(spaceEnvironment, watcherPeriod, TimeUnit.SECONDS);

    spaceEnvironment.getLog().info("File control of space controller started");
  }

  @Override
  public void shutdown() {
    if (watcher != null) {
      watcher.shutdown();
      watcher = null;
    }
  }

  @Override
  public void onFileAdded(File file) {
    // Immediately delete file
    file.delete();

    handleCommand(file.getName());
  }

  @Override
  public void onFileRemoved(File file) {
    // Don't care.
  }

  /**
   * Handle the command coming in.
   *
   * @param command
   *          the command to be executed
   */
  void handleCommand(String command) {
    if (COMMAND_SHUTDOWN.equalsIgnoreCase(command)) {
      spaceSystemControl.shutdown();
    } else {
      spaceEnvironment.getLog().warn(String.format("Unknown command to controller file control %s", command));
    }
  }
}
