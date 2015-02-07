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

import interactivespaces.InteractiveSpacesException;
import interactivespaces.configuration.Configuration;
import interactivespaces.liveactivity.runtime.activity.wrapper.ActivityWrapperFactory;
import interactivespaces.liveactivity.runtime.configuration.LiveActivityConfiguration;
import interactivespaces.liveactivity.runtime.domain.InstalledLiveActivity;

/**
 * A factory for {@link BasicLiveActivityRunner} instances.
 *
 * @author Keith M. Hughes
 */
public interface LiveActivityRunnerFactory {

  /**
   * Create alive activity runner for a given activity type.
   *
   * @param activityType
   *          the type of activity being created
   * @param installedLiveActivity
   *          the activity to be run
   * @param activityFilesystem
   *          the filesystem for the activity
   * @param configuration
   *          configuration for the activity
   * @param liveActivityRunnerListener
   *          the listener for live activity runner events
   * @param liveActivityRuntime
   *          the live activity runtime the runner will run in
    *
   * @return the live activity runner
   */
  LiveActivityRunner newLiveActivityRunner(String activityType, InstalledLiveActivity installedLiveActivity,
      InternalLiveActivityFilesystem activityFilesystem, LiveActivityConfiguration configuration,
      LiveActivityRunnerListener liveActivityRunnerListener, LiveActivityRuntime liveActivityRuntime);

  /**
   * Create an active controller activity for a given activity type.
   *
   * <p>
   * The activity type is determined from the {@code configuration} using the {@link #getConfiguredType(Configuration)}
   * method.
   *
   *
   * @param installedLiveActivity
   *          the activity to be run
   * @param activityFilesystem
   *          the activity's filesystem
   * @param configuration
   *          configuration for the activity
   * @param liveActivityRunnerListener
   *          the listener for live activity runner events
   * @param liveActivityRuntime
   *          the live activity runtime the runner will run in
   *
   * @return a runner for the activity
   */
  BasicLiveActivityRunner newLiveActivityRunner(InstalledLiveActivity installedLiveActivity,
      InternalLiveActivityFilesystem activityFilesystem, LiveActivityConfiguration configuration,
      LiveActivityRunnerListener liveActivityRunnerListener, LiveActivityRuntime liveActivityRuntime);

  /**
   * Get the activity type of the activity.
   *
   * @param configuration
   *          the configuration of the activity.
   *
   * @return the activity type
   *
   * @throws InteractiveSpacesException
   *           if can't determine the activity type
   */
  String getConfiguredType(Configuration configuration) throws InteractiveSpacesException;

  /**
   * Register an {@link ActivityWrapperFactory}.
   *
   * @param factory
   *          activity wrapper factory
   */
  void registerActivityWrapperFactory(ActivityWrapperFactory factory);

  /**
   * Unregister an {@link ActivityWrapperFactory}.
   *
   * <p>
   * Nothing happens if the factory was never registered.
   *
   * @param factory
   *          activity wrapper factory
   */
  void unregisterActivityWrapperFactory(ActivityWrapperFactory factory);
}
