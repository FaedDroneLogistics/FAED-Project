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

import interactivespaces.configuration.Configuration;
import interactivespaces.configuration.SimpleConfiguration;
import interactivespaces.configuration.SingleConfigurationStorageManager;
import interactivespaces.evaluation.EvaluationInteractiveSpacesException;
import interactivespaces.evaluation.ExpressionEvaluator;

import com.google.common.base.Preconditions;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A configuration specifically for activities.
 *
 * @author Keith M. Hughes
 */
public class StandardLiveActivityConfiguration implements LiveActivityConfiguration {

  /**
   * Storage manager for the base activity configuration.
   */
  private final SingleConfigurationStorageManager baseActivityConfigurationStorageManager;

  /**
   * Storage manager for the installed activity configuration.
   */
  private final SingleConfigurationStorageManager installedActivityConfigurationStorageManager;

  /**
   * The temporary configuration is the root. All delegated calls go to this
   * one.
   */
  private final SimpleConfiguration temporary;

  /**
   * The system configuration.
   */
  private final Configuration systemConfiguration;

  /**
   * Parent of this configuration.
   */
  private Configuration parent;

  /**
   * Construct a new configuration.
   *
   * @param baseActivityConfigurationStorageManager
   *          the storage manager for the base activity configuration
   * @param installedActivityStorageManager
   *          the storage manager for the installed activity
   * @param expressionEvaluator
   *          the expression evaluator for this configuration
   * @param systemConfiguration
   *          the system configuration
   */
  public StandardLiveActivityConfiguration(SingleConfigurationStorageManager baseActivityConfigurationStorageManager,
      SingleConfigurationStorageManager installedActivityStorageManager,
      ExpressionEvaluator expressionEvaluator, Configuration systemConfiguration) {
    this.baseActivityConfigurationStorageManager = baseActivityConfigurationStorageManager;
    this.installedActivityConfigurationStorageManager = installedActivityStorageManager;
    this.systemConfiguration = systemConfiguration;

    temporary = new SimpleConfiguration(expressionEvaluator);

    Configuration installedConfiguration = installedActivityStorageManager.getConfiguration();
    Configuration baseConfiguration = baseActivityConfigurationStorageManager.getConfiguration();
    baseConfiguration.setParent(systemConfiguration);
    installedConfiguration.setParent(baseConfiguration);
    temporary.setParent(installedConfiguration);
  }

  @Override
  public void load() {
    temporary.clear();
    baseActivityConfigurationStorageManager.load();
    installedActivityConfigurationStorageManager.load();
  }

  @Override
  public void update(Map<String, String> update) {
    installedActivityConfigurationStorageManager.clear();
    installedActivityConfigurationStorageManager.update(update);
    installedActivityConfigurationStorageManager.save();
  }

  @Override
  public boolean containsPropertyLocally(String property) {
    return temporary.containsProperty(property);
  }

  @Override
  public String findValueLocally(String property) {
    return temporary.findValue(property);
  }

  @Override
  public void setValue(String property, String value) {
    temporary.setValue(property, value);
  }

  @Override
  public void setValues(Map<String, String> values) {
    temporary.setValues(values);
  }

  @Override
  public void setParent(Configuration parent) {
    throw new UnsupportedOperationException("setParent not supported");
  }

  @Override
  public Configuration getParent() {
    Preconditions.checkState(parent == null);
    return parent;
  }

  @Override
  public String evaluate(String expression) {
    return temporary.evaluate(expression);
  }

  @Override
  public ExpressionEvaluator getExpressionEvaluator() {
    return temporary.getExpressionEvaluator();
  }

  @Override
  public String getPropertyString(String property) {
    return temporary.getPropertyString(property);
  }

  @Override
  public String getPropertyString(String property, String defaultValue) {
    return temporary.getPropertyString(property, defaultValue);
  }

  @Override
  public String getRequiredPropertyString(String property) {
    return temporary.getRequiredPropertyString(property);
  }

  @Override
  public Integer getPropertyInteger(String property, Integer defaultValue) {
    return temporary.getPropertyInteger(property, defaultValue);
  }

  @Override
  public Integer getRequiredPropertyInteger(String property) {
    return temporary.getRequiredPropertyInteger(property);
  }

  @Override
  public Long getPropertyLong(String property, Long defaultValue) {
    return temporary.getPropertyLong(property, defaultValue);
  }

  @Override
  public Long getRequiredPropertyLong(String property) {
    return temporary.getRequiredPropertyLong(property);
  }

  @Override
  public Double getPropertyDouble(String property, Double defaultValue) {
    return temporary.getPropertyDouble(property, defaultValue);
  }

  @Override
  public Double getRequiredPropertyDouble(String property) {
    return temporary.getRequiredPropertyDouble(property);
  }

  @Override
  public Boolean getPropertyBoolean(String property, Boolean defaultValue) {
    return temporary.getPropertyBoolean(property, defaultValue);
  }

  @Override
  public Boolean getRequiredPropertyBoolean(String property) {
    return temporary.getRequiredPropertyBoolean(property);
  }

  @Override
  public List<String> getPropertyStringList(String property, String delineator) {
    return temporary.getPropertyStringList(property, delineator);
  }

  @Override
  public Set<String> getPropertyStringSet(String property, String delineator) {
    return temporary.getPropertyStringSet(property, delineator);
  }

  @Override
  public boolean containsProperty(String property) {
    return temporary.containsProperty(property);
  }

  @Override
  public String findValue(String property) {
    return temporary.findValue(property);
  }

  @Override
  public String lookupVariableValue(String variable) throws EvaluationInteractiveSpacesException {
    return temporary.lookupVariableValue(variable);
  }

  @Override
  public void clear() {
    // Clear the entire chain except the system configuration.
    for (Configuration current = temporary; current != systemConfiguration; current =
        current.getParent()) {
      current.clear();
    }
  }

  @Override
  public Map<String, String> getCollapsedMap() {
    return temporary.getCollapsedMap();
  }

  @Override
  public void addCollapsedEntries(Map<String, String> map) {
    temporary.addCollapsedEntries(map);
  }
}
