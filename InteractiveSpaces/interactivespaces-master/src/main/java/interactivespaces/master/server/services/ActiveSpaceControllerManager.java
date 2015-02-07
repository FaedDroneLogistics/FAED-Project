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

package interactivespaces.master.server.services;

import interactivespaces.domain.basic.LiveActivity;
import interactivespaces.domain.basic.LiveActivityGroup;
import interactivespaces.domain.basic.SpaceController;
import interactivespaces.domain.space.Space;

import java.util.List;

/**
 * A manager for space controllers running on nodes on the network.
 *
 * @author Keith M. Hughes
 */
public interface ActiveSpaceControllerManager {

  /**
   * Connect to the specified controller.
   *
   * <p>
   * Most functions here will automatically connect to a controller. This is
   * useful if status updates are needed before doing anything else with the
   * controller.
   *
   * @param controller
   *          the controller to connect to
   */
  void connectSpaceController(SpaceController controller);

  /**
   * Disconnect from the specified controller.
   *
   * <p>
   * Most functions here will automatically connect to a controller. This is
   * useful if status updates are needed before doing anything else with the
   * controller.
   *
   * @param controller
   *          the controller to disconnect
   */
  void disconnectSpaceController(SpaceController controller);

  /**
   * Restart a controller.
   *
   * <p>
   * The controller will be brought up in a clean state.
   *
   * @param controller
   *          the controller to restart
   */
  void restartSpaceController(SpaceController controller);

  /**
   * Shutdown a controller.
   *
   * @param controller
   *          the controller to shutdown
   */
  void shutdownSpaceController(SpaceController controller);

  /**
   * Request a status from a controller.
   *
   * <p>
   * This will be a noop if the controller has never been connected or has been
   * disconnected.
   *
   * @param controller
   *          the controller
   */
  void statusSpaceController(SpaceController controller);

  /**
   * Force request a status from a controller.
   *
   * @param controller
   *          the controller
   */
  void forceStatusSpaceController(SpaceController controller);

  /**
   * Configure a space controller.
   *
   * @param spaceController
   *          the space controller to configure
   */
  void configureSpaceController(SpaceController spaceController);

  /**
   * Clean the temp data folder for the controller.
   *
   * @param controller
   *          controller to clean
   */
  void cleanSpaceControllerTempData(SpaceController controller);

  /**
   * Clean the permanent data folder for the controller.
   *
   * @param controller
   *          controller to clean
   */
  void cleanSpaceControllerPermanentData(SpaceController controller);

  /**
   * Clean the temp data folder for all live activities on the controller.
   *
   * @param controller
   *          controller to clean
   */
  void cleanSpaceControllerActivitiesTempData(SpaceController controller);

  /**
   * Clean the permanent data folder for all live activities on the controller.
   *
   * @param controller
   *          controller to clean
   */
  void cleanSpaceControllerActivitiesPermanentData(SpaceController controller);

  /**
   * Capture the data bundle from the controller.
   *
   * @param controller
   *          the controller
   */
  void captureSpaceControllerDataBundle(SpaceController controller);

  /**
   * Restore a previously captured data bundle to the controller.
   *
   * @param controller
   *          the controller
   */
  void restoreSpaceControllerDataBundle(SpaceController controller);

  /**
   * Shutdown all activities on a controller.
   *
   * @param controller
   *          the controller
   */
  void shutdownAllActivities(SpaceController controller);

  /**
   * Deploy an activity on a controller.
   *
   * @param activity
   *          the activity to deploy
   */
  void deployLiveActivity(LiveActivity activity);

  /**
   * Delete an activity on a controller.
   *
   * @param activity
   *          the activity to delete
   */
  void deleteLiveActivity(LiveActivity activity);

  /**
   * Configure an activity on a controller.
   *
   * @param activity
   *          the activity to configure
   */
  void configureLiveActivity(LiveActivity activity);

  /**
   * Start an activity on a controller.
   *
   * @param activity
   *          the activity to start
   */
  void startupLiveActivity(LiveActivity activity);

  /**
   * Activate an activity on a controller.
   *
   * @param activity
   *          the activity to activate
   */
  void activateLiveActivity(LiveActivity activity);

  /**
   * Deactivate an activity on a controller.
   *
   * @param activity
   *          the activity to deactivate
   */
  void deactivateLiveActivity(LiveActivity activity);

  /**
   * Shut down an activity on a controller.
   *
   * <p>
   * The activity will be shut down even if it is running in several activity
   * groups.
   *
   * @param activity
   *          the activity to shut down
   */
  void shutdownLiveActivity(LiveActivity activity);

  /**
   * Status of an activity on its controller.
   *
   * @param activity
   *          the activity to get the status for
   */
  void statusLiveActivity(LiveActivity activity);

  /**
   * Clean the permanent data of an activity on its controller.
   *
   * @param activity
   *          the activity to clean
   */
  void cleanLiveActivityPermanentData(LiveActivity activity);

  /**
   * Clean the temp data of an activity on its controller.
   *
   * @param activity
   *          the activity to clean
   */
  void cleanLiveActivityTempData(LiveActivity activity);

  /**
   * Deploy an activity group on a controller.
   *
   * @param activityGroup
   *          the activity group to deploy
   */
  void deployLiveActivityGroup(LiveActivityGroup activityGroup);

  /**
   * Configure an activity group on a controller.
   *
   * @param activityGroup
   *          The activity group to deploy.
   */
  void configureLiveActivityGroup(LiveActivityGroup activityGroup);

  /**
   * Start an activity group on a controller.
   *
   * @param activityGroup
   *          The activity group to start.
   */
  void startupLiveActivityGroup(LiveActivityGroup activityGroup);

  /**
   * Activate an activity group on a controller.
   *
   * @param activityGroup
   *          The activity group to activate.
   */
  void activateLiveActivityGroup(LiveActivityGroup activityGroup);

  /**
   * Deactivate an activity group on a controller.
   *
   * @param activityGroup
   *          The activity group to deactivate.
   */
  void deactivateLiveActivityGroup(LiveActivityGroup activityGroup);

  /**
   * Shut down an activity group on a controller.
   *
   * <p>
   * The individual activities in the group will only be actually shut down if
   * they have been shut down in all activity groups which started the activity
   * up.
   *
   * @param activityGroup
   *          the activity group to shut down
   */
  void shutdownLiveActivityGroup(LiveActivityGroup activityGroup);

  /**
   * Get the active activity associated with a given activity.
   *
   * @param activity
   *          the live activity
   *
   * @return the active live activity for the live activity
   */
  ActiveLiveActivity getActiveLiveActivity(LiveActivity activity);

  /**
   * Get the active activities associated with the given activities.
   *
   * @param activities
   *          the live activities
   *
   * @return the active activities for the activities
   */
  List<ActiveLiveActivity> getActiveLiveActivities(List<LiveActivity> activities);

  /**
   * Deploy all assets for the space.
   *
   * @param space
   *          the space
   */
  void deploySpace(Space space);

  /**
   * Configure all assets in the space.
   *
   * @param space
   *          the space
   */
  void configureSpace(Space space);

  /**
   * Start the given space. This will start all required activities needed by
   * the space. Child spaces will be started first.
   *
   * @param space
   *          the space to start
   */
  void startupSpace(Space space);

  /**
   * Shut a given space down. Child spaces will be shut down first.
   *
   * @param space
   *          the space to shut down
   */
  void shutdownSpace(Space space);

  /**
   * Activate a space. Child spaces will be activated first.
   *
   * @param space
   *          the space to activate
   */
  void activateSpace(Space space);

  /**
   * Deactivate a space. Child spaces will be deactivated first.
   *
   * @param space
   *          the space to deactivate
   */
  void deactivateSpace(Space space);

  /**
   * Get the active controller associated with a given controller.
   *
   * @param controller
   *          the space controller
   *
   * @return the active space controller for the space controller
   */
  ActiveSpaceController getActiveSpaceController(SpaceController controller);

  /**
   * Get the active controllers associated with the given controllers.
   *
   * @param controllers
   *          the controllers
   *
   * @return the active controllers for the controllers
   */
  List<ActiveSpaceController> getActiveSpaceControllers(List<SpaceController> controllers);

  /**
   * Add in a new controller listener.
   *
   * @param listener
   *          the new listener
   */
  void addSpaceControllerListener(SpaceControllerListener listener);

  /**
   * Remove a controller listener.
   *
   * <p>
   * Nothing will happen if the listener was not in.
   *
   * @param listener
   *          the listener to remove
   */
  void removeSpaceControllerListener(SpaceControllerListener listener);
}
