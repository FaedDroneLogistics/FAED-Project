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

import interactivespaces.InteractiveSpacesException;

import java.io.File;

/**
 * A reader for project files.
 *
 * @author Keith M. Hughes
 */
public interface ProjectReader {

  /**
   * XML namespace for the v 1.0.0 project format.
   */
  String XML_NAMESPACE_1_0_0 = "http://interactive-spaces.org/project.xsd";

  /**
   * Read the description input stream contents into the supplied
   * {@link Project} object.
   *
   * @param projectFile
   *          the project file
   *
   * @return the description of the project
   *
   * @throws InteractiveSpacesException
   *           an error happened during the project file processing
   */
  Project readProject(File projectFile) throws InteractiveSpacesException;
}
