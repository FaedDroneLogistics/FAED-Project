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

package interactivespaces.system.core.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A very basic {@link ContainerCustomizerProvider}.
 *
 * @author Keith M. Hughes
 */
public class SimpleContainerCustomizerProvider implements ContainerCustomizerProvider {

  /**
   * The command line arguments from container startup.
   */
  private final List<String> commandLineArguments;

  /**
   * The services from the container.
   */
  private final Map<String, Object> services = new HashMap<String, Object>();

  /**
   * {@code true} if the container is controllable from files.
   */
  private final boolean fileControllable;

  /**
   * Create a customizer which has no command line arguments and is not file
   * controllable.
   */
  public SimpleContainerCustomizerProvider() {
    this(new ArrayList<String>(), false);
  }

  /**
   * Construct a customizer provider.
   *
   * @param commandLineArguments
   *          the command line arguments to use
   * @param fileControllable
   *          {@code true} if the container is file controllable
   */
  public SimpleContainerCustomizerProvider(List<String> commandLineArguments, boolean fileControllable) {
    this.commandLineArguments = commandLineArguments;
    this.fileControllable = fileControllable;
  }

  @Override
  public List<String> getCommandLineArguments() {
    return commandLineArguments;
  }

  @Override
  public Map<String, Object> getServices() {
    // Yes, they are getting the unadulterated map.
    return services;
  }

  /**
   * Add a new service to the customizer.
   *
   * @param name
   *          the name of the service
   * @param service
   *          the service
   */
  public void addService(String name, Object service) {
    services.put(name, service);
  }

  @Override
  public boolean isFileControllable() {
    return fileControllable;
  }
}
