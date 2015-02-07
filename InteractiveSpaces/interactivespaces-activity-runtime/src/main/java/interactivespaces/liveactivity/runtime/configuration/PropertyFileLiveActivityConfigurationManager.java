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

package interactivespaces.liveactivity.runtime.configuration;

import interactivespaces.configuration.SimplePropertyFileSingleConfigurationStorageManager;
import interactivespaces.configuration.SingleConfigurationStorageManager;
import interactivespaces.evaluation.ExpressionEvaluator;
import interactivespaces.evaluation.ExpressionEvaluatorFactory;
import interactivespaces.liveactivity.runtime.InternalLiveActivityFilesystem;
import interactivespaces.system.InteractiveSpacesEnvironment;

import java.io.File;

/**
 * A configuration manager which uses Java property files.
 *
 * @author Keith M. Hughes
 */
public class PropertyFileLiveActivityConfigurationManager implements LiveActivityConfigurationManager {

  /**
   * File extension configuration files should have.
   */
  public static final String CONFIGURATION_FILE_EXTENSION = "conf";

  /**
   * The Interactive Spaces environment.
   */
  private final InteractiveSpacesEnvironment spaceEnvironment;

  /**
   * Factory for expression evaluators.
   */
  private final ExpressionEvaluatorFactory expressionEvaluatorFactory;

  /**
   * Construct a new configuration manager.
   *
   * @param expressionEvaluatorFactory
   *          the expression evaluator factory to use
   * @param spaceEnvironment
   *          the space environment to use
   */
  public PropertyFileLiveActivityConfigurationManager(ExpressionEvaluatorFactory expressionEvaluatorFactory,
      InteractiveSpacesEnvironment spaceEnvironment) {
    this.expressionEvaluatorFactory = expressionEvaluatorFactory;
    this.spaceEnvironment = spaceEnvironment;
  }

  @Override
  public StandardLiveActivityConfiguration newLiveActivityConfiguration(InternalLiveActivityFilesystem activityFilesystem) {
    ExpressionEvaluator expressionEvaluator = expressionEvaluatorFactory.newEvaluator();

    SingleConfigurationStorageManager baseConfigurationStorageManager =
        newConfiguration(activityFilesystem.getInstallFile(getConfigFileName(CONFIG_TYPE_BASE_ACTIVITY)), true,
            expressionEvaluator);

    SingleConfigurationStorageManager installedActivityConfigurationStorageManager =
        newConfiguration(activityFilesystem.getInternalFile(getConfigFileName(CONFIG_TYPE_LIVE_ACTIVITY)), false,
            expressionEvaluator);

    StandardLiveActivityConfiguration configuration =
        new StandardLiveActivityConfiguration(baseConfigurationStorageManager,
            installedActivityConfigurationStorageManager, expressionEvaluator,
            spaceEnvironment.getSystemConfiguration());
    expressionEvaluator.setEvaluationEnvironment(configuration);

    return configuration;
  }

  /**
   * Create a new configuration storage manager.
   *
   * @param configurationFile
   *          where the configuration file resides
   * @param required
   *          {@code true} if the configuration is required to run
   * @param expressionEvaluator
   *          expression evaluator to be given to the configuration
   *
   * @return a configuration storage manager
   */
  private SingleConfigurationStorageManager newConfiguration(File configurationFile, boolean required,
      ExpressionEvaluator expressionEvaluator) {
    return new SimplePropertyFileSingleConfigurationStorageManager(required, configurationFile, expressionEvaluator);
  }

  /**
   * Get the configuration file name from the configuration type.
   *
   * @param configType
   *          the type of configuration
   *
   * @return the configuration file name
   */
  private String getConfigFileName(String configType) {
    return configType + "." + CONFIGURATION_FILE_EXTENSION;
  }
}
