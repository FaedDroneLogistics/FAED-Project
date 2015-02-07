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

package interactivespaces.configuration;

import java.util.Map;

/**
 * Manages storage for a single configuration.
 *
 * <p>
 * There will be one of these per configuration file being stored.
 *
 * @author Keith M. Hughes
 */
public interface SingleConfigurationStorageManager {

  /**
   * Get the configuration being managed by this manager.
   *
   * @return the managed configuration.
   */
  Configuration getConfiguration();

  /**
   * Load the most recently saved version of the configuration.
   *
   * @return the storage manager
   */
  SingleConfigurationStorageManager load();

  /**
   * Store the configuration in the store.
   *
   * @return the storage manager
   */
  SingleConfigurationStorageManager save();

  /**
   * Update the configuration.
   *
   * @param update
   *          a map of updates
   *
   * @return the storage manager
   */
  SingleConfigurationStorageManager update(Map<String, String> update);

  /**
   * Delete all configuration entries.
   *
   * @return the storage manager
   */
  SingleConfigurationStorageManager clear();
}
