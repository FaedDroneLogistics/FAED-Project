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

package interactivespaces.workbench.project;

/**
 * A deployment for project artifacts.
 *
 * @author Keith M. Hughes
 */
public class ProjectDeployment {

  /**
   * The type of the deployment.
   */
  private String type;

  /**
   * The method of the deployment.
   */
  private String method;

  /**
   * The location of the deployment.
   */
  private String location;

  public ProjectDeployment(String type, String method, String location) {
    this.type = type;
    this.method = method;
    this.location = location;
  }

  /**
   * @return the type
   */
  public String getType() {
    return type;
  }

  /**
   * @return the method
   */
  public String getMethod() {
    return method;
  }

  /**
   * @return the location
   */
  public String getLocation() {
    return location;
  }
}
