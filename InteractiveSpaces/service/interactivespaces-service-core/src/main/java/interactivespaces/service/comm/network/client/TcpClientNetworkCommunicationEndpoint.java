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

package interactivespaces.service.comm.network.client;

import interactivespaces.util.resource.ManagedResource;

import java.net.InetAddress;

/**
 * Communication endpoint for a TCP based client.
 *
 * @param <T>
 *          the type of the messages being handled
 *
 * @author Keith M. Hughes
 */
public interface TcpClientNetworkCommunicationEndpoint<T> extends ManagedResource {

  /**
   * Get the remote host being connected to.
   *
   * @return the remote port being connected to
   */
  InetAddress getRemoteHost();

  /**
   * Get the remote port being connected.
   *
   * @return the remote port being connected to
   */
  int getRemotePort();

  /**
   * Add a listener to the endpoint.
   *
   * @param listener
   *          the listener to add
   */
  void addListener(TcpClientNetworkCommunicationEndpointListener<T> listener);

  /**
   * Remove a listener from the endpoint.
   *
   * <p>
   * A noop if the listener wasn't previously added.
   *
   * @param listener
   *          the listener to remove
   */
  void removeListener(TcpClientNetworkCommunicationEndpointListener<T> listener);

  /**
   * Send a message to the remote client.
   *
   * @param message
   *          the message to send
   */
  void write(T message);

  /**
   * Set the connection timeout.
   *
   * @param connectionTimeout
   *          the connection timeout, in milliseconds
   */
  void setConnectionTimeout(long connectionTimeout);
}
