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


/**
 * Listener for events from a {@link TcpServerNetworkCommunicationEndpoint].
 *
 * @param <T>
 *
 * @author Keith M. Hughes
 */
public interface TcpServerNetworkCommunicationEndpointListener<T> {

  /**
   * A request has come in.
   *
   * @param endpoint
   *          endpoint the request came into
   * @param request
   *          the request which has been received
   */
  void onTcpRequest(TcpServerNetworkCommunicationEndpoint<T> endpoint, TcpServerRequest<T> request);
}
