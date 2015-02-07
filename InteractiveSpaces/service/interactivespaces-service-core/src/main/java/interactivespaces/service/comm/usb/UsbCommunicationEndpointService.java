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

package interactivespaces.service.comm.usb;

import interactivespaces.service.SupportedService;

/**
 * Manage USB bus device communication endpoints.
 *
 * @author Keith M. Hughes
 */
public interface UsbCommunicationEndpointService extends SupportedService {

  /**
   * The name of the service.
   */
  String SERVICE_NAME = "comm.usb";

  /**
   * Get a new USB endpoint.
   *
   * @param vendor
   *          the vendor ID for the device
   * @param product
   *          the product ID for the device
   *
   * @return the USB communication endpoint
   */
  UsbCommunicationEndpoint newEndpoint(String vendor, String product);
}
