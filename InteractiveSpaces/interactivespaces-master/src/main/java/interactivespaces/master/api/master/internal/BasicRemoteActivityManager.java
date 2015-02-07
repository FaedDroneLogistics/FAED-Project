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

package interactivespaces.master.api.master.internal;

import interactivespaces.master.api.master.RemoteActivityManager;
import interactivespaces.master.server.services.RemoteSpaceControllerClient;

/**
 * A simple remote activity manager.
 *
 * @author Keith M. Hughes
 */
public class BasicRemoteActivityManager implements RemoteActivityManager {

  /**
   * Handle operations on remote controllers.
   */
  private RemoteSpaceControllerClient remoteControllerClient;

  /**
   * @param remoteControllerClient
   *          the remoteControllerClient to set
   */
  public void setRemoteControllerClient(RemoteSpaceControllerClient remoteControllerClient) {
    this.remoteControllerClient = remoteControllerClient;
  }

}
