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

package interactivespaces.master.resource.deployment;

import java.util.Set;

/**
 * A repository for ROS software.
 *
 * @author Keith M. Hughes
 */
public interface RemoteRepositoryMaster {

  /**
   * Configuration parameter giving the port for the ROS deployment server.
   */
  String CONFIGURATION_ROS_DEPLOYMENT_SERVER_PORT = "org.ros.deployment.server.port";

  /**
   * The default value for the server port for the ROS deployment server.
   */
  int CONFIGURATION_DEFAULT_ROS_DEPLOYMENT_SERVER_PORT = 8085;

  /**
   * Start up the master.
   */
  void startup();

  /**
   * Shut down the master.
   */
  void shutdown();

  /**
   * Take a collection of bundles and create URIs appropriate for accessing them
   * from this master.
   *
   * @param bundles
   *          the bundles requested
   *
   * @return the bundles rewritten to be URIs
   */
  Set<String> getBundleUris(Set<String> bundles);

}
