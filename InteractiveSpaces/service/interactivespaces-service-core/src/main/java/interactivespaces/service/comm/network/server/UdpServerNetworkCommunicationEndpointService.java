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

package interactivespaces.service.comm.network.server;

import interactivespaces.service.SupportedService;

import org.apache.commons.logging.Log;

import java.nio.ByteOrder;

/**
 * A communication endpoint service for UDP servers.
 *
 * @author Keith M. Hughes
 */
public interface UdpServerNetworkCommunicationEndpointService extends SupportedService {

  /**
   * Name for the service.
   */
  String SERVICE_NAME = "comm.network.udp.server";

  /**
   * Name for the service.
   *
   * @deprecated Use {@link #SERVICE_NAME}.
   */
  @Deprecated
  String NAME = SERVICE_NAME;

  /**
   * Create a new UDP server endpoint.
   *
   * <p>
   * Packets will be big-endian.
   *
   * @param serverPort
   *          port the server will listen to
   * @param log
   *          the logger to use
   *
   * @return the communication endpoint
   */
  UdpServerNetworkCommunicationEndpoint newServer(int serverPort, Log log);

  /**
   * Create a new UDP server endpoint.
   *
   * @param serverPort
   *          port the server will listen to
   * @param byteOrder
   *          byte ordering for packets
   * @param log
   *          the logger to use
   *
   * @return the communication endpoint
   */
  UdpServerNetworkCommunicationEndpoint newServer(int serverPort, ByteOrder byteOrder, Log log);
}
