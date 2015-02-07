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

package interactivespaces.util.io.directorywatcher;

import interactivespaces.system.InteractiveSpacesEnvironment;

import java.io.File;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Watch a directory for additions and deletions of files.
 *
 * @author Keith M. Hughes
 */
public interface DirectoryWatcher {

  /**
   * Start the watcher up.
   *
   * @param environment
   *          the spaces environment being run under
   * @param period
   *          how often the directories should be scanned
   * @param unit
   *          time unit for the scanning period
   */
  void startup(InteractiveSpacesEnvironment environment, long period, TimeUnit unit);

  /**
   * Start the watcher up and scan the directories immediately.
   *
   * <p>
   * The listeners will not be called with the files returned.
   *
   * @param environment
   *          the spaces environment being run under
   * @param period
   *          how often the directories should be scanned
   * @param unit
   *          time unit for the scanning period
   *
   * @return the initial set of files in all scanned folders
   */
  Set<File> startupWithScan(InteractiveSpacesEnvironment environment, long period, TimeUnit unit);

  /**
   * Shut down the watcher.
   */
  void shutdown();

  /**
   * Add a new directory to watch.
   *
   * @param directory
   *          the directory to watch
   *
   * @throws IllegalArgumentException
   *           the supplied file is either not a directory or is not readable
   */
  void addDirectory(File directory);

  /**
   * Add a new listener to the watcher.
   *
   * @param listener
   *          the listener to add
   */
  void addDirectoryWatcherListener(DirectoryWatcherListener listener);

  /**
   * Remove a listener from the watcher.
   *
   * <p>
   * Nothing is done if the listener was never added
   *
   * @param listener
   *          the listener to remove
   */
  void removeDirectoryWatcherListener(DirectoryWatcherListener listener);

  /**
   * Force a scan of the registered directories.
   */
  void scan();

  /**
   * Should directories should be emptied before they are added to the watcher?
   *
   * @param cleanFirst
   *          {@code true} if the directories should be cleaned first
   */
  void setCleanFirst(boolean cleanFirst);

  /**
   * Should the watcher stop if there is ever an exception while running?
   *
   * <p>
   * By default the watcher stops.
   *
   * @param stopOnException
   *          {@code true} if the watcher should stop on exception
   */
  void setStopOnException(boolean stopOnException);
}
