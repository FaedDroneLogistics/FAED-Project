/*
 * Copyright (C) 2014 Google Inc.
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

package interactivespaces.service.control.opensoundcontrol;

import interactivespaces.service.SupportedService;

import org.apache.commons.logging.Log;

/**
 * A service for building a server for Open Sound Control.
 *
 * @author Keith M. Hughes
 */
public interface OpenSoundControlServerCommunicationEndpointService extends SupportedService {

  /**
   * The name of the service.
   */
  String SERVICE_NAME = "control.opensoundcontrol.server";

  /**
   * Create a new server endpoint that uses UDP.
   *
   * @param localPort
   *          the port on the local host for the OSC server
   * @param log
   *          the logger for this connection
   *
   * @return the new endpoint
   */
  OpenSoundControlServerCommunicationEndpoint newUdpEndpoint(int localPort, Log log);
}
