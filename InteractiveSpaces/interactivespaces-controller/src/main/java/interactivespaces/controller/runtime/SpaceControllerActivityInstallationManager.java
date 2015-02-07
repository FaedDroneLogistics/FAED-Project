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

package interactivespaces.controller.runtime;

import interactivespaces.activity.deployment.ActivityDeploymentRequest;
import interactivespaces.activity.deployment.LiveActivityDeploymentResponse;
import interactivespaces.util.resource.ManagedResource;

/**
 * The controller-side installation manager.
 *
 * @author Keith M. Hughes
 */
public interface SpaceControllerActivityInstallationManager extends ManagedResource {

  /**
   * Handle a deployment request and return a deployment status.
   *
   * @param request
   *          the deployment request
   *
   * @return the status for the deployment
   */
  LiveActivityDeploymentResponse handleDeploymentRequest(ActivityDeploymentRequest request);

  /**
   * Handle a delete request and return a delete status.
   *
   * @param request
   *          the delete request
   *
   * @return the status for the deletion
   */
  SpaceControllerLiveActivityDeleteStatus handleDeleteRequest(SpaceControllerLiveActivityDeleteRequest request);
}
