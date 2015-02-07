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

package interactivespaces.activity.component;

/**
 * A factory for {@link ActivityComponent} instances.
 *
 * @author Keith M. Hughes
 */
public interface ActivityComponentFactory {

  /**
   * Register a component class with the factory.
   *
   * @param componentName
   *          the name of the component
   * @param componentClass
   *          the class of the component
   */
  void register(String componentName, Class<? extends ActivityComponent> componentClass);

  /**
   * Get a new instance of the component type.
   *
   * @param componentName
   *          the component name of the component
   * @param <T>
   *          the type of the component
   *
   * @return a new instance of the requested component type
   */
  <T extends ActivityComponent> T newComponent(String componentName);
}
