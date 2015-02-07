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

import interactivespaces.configuration.Configuration;
import interactivespaces.controller.SpaceController;
import interactivespaces.domain.basic.pojo.SimpleSpaceController;
import interactivespaces.service.ServiceRegistry;
import interactivespaces.service.web.client.WebSocketClientService;
import interactivespaces.service.web.client.internal.netty.NettyWebSocketClientService;
import interactivespaces.service.web.server.WebServerService;
import interactivespaces.service.web.server.internal.netty.NettyWebServerService;
import interactivespaces.system.InteractiveSpacesEnvironment;

/**
 * Base implementation for a space controller.
 *
 * @author Trevor Pering
 */
public abstract class BaseSpaceController implements SpaceController {

  /**
   * Information about the controller.
   */
  private final SimpleSpaceController controllerInfo = new SimpleSpaceController();

  /**
   * The Interactive Spaces environment being run under.
   */
  private final InteractiveSpacesEnvironment spaceEnvironment;

  /**
   * The IS service for web servers.
   */
  private WebServerService webServerService;

  /**
   * The IS service for web socket clients.
   */
  private WebSocketClientService webSocketClientService;

  /**
   * Construct a controller with the given space environment.
   *
   * @param spaceEnvironment
   *          space environment to use
   */
  public BaseSpaceController(InteractiveSpacesEnvironment spaceEnvironment) {
    this.spaceEnvironment = spaceEnvironment;
  }

  @Override
  public void startup() {
    getSpaceEnvironment().getLog().info("Controller starting up");
    obtainControllerInfo();

    setEnvironmentValues();
  }

  @Override
  public void shutdown() {
    getSpaceEnvironment().getLog().info("Controller shutting down");
  }

  @Override
  public InteractiveSpacesEnvironment getSpaceEnvironment() {
    return spaceEnvironment;
  }

  /**
   * Get controller information from the configs.
   */
  private void obtainControllerInfo() {
    Configuration systemConfiguration = getSpaceEnvironment().getSystemConfiguration();

    controllerInfo.setUuid(systemConfiguration.getPropertyString(CONFIGURATION_CONTROLLER_UUID));
    controllerInfo
        .setName(systemConfiguration.getPropertyString(CONFIGURATION_CONTROLLER_NAME, ""));
    controllerInfo.setDescription(systemConfiguration.getPropertyString(
        CONFIGURATION_CONTROLLER_DESCRIPTION, ""));
    controllerInfo.setHostId(systemConfiguration
        .getRequiredPropertyString(InteractiveSpacesEnvironment.CONFIGURATION_HOSTID));
  }

  @Override
  public SimpleSpaceController getControllerInfo() {
    return controllerInfo;
  }

  /**
   * Start up the core services that all controllers provide.
   */
  protected void startupCoreControllerServices() {
    ServiceRegistry serviceRegistry = getSpaceEnvironment().getServiceRegistry();

    webServerService = new NettyWebServerService();
    serviceRegistry.registerService(webServerService);
    webServerService.startup();

    webSocketClientService = new NettyWebSocketClientService();
    serviceRegistry.registerService(webSocketClientService);
    webSocketClientService.startup();
  }

  /**
   * Set values in the space environment that the controller provides.
   */
  private void setEnvironmentValues() {
//    getSpaceEnvironment().setValue(ENVIRONMENT_CONTROLLER_NATIVE_RUNNER,
//        getNativeActivityRunnerFactory());
  }

  /**
   * Shutdown the core services provided by all controllers.
   */
  protected void shutdownCoreControllerServices() {
    ServiceRegistry serviceRegistry = getSpaceEnvironment().getServiceRegistry();

    serviceRegistry.unregisterService(webServerService);
    webServerService.shutdown();

    serviceRegistry.unregisterService(webSocketClientService);
    webSocketClientService.shutdown();
  }
}
