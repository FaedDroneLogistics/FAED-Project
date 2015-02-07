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

package interactivespaces.workbench.project.ide;

import interactivespaces.workbench.FreemarkerTemplater;
import interactivespaces.workbench.project.Project;
import interactivespaces.workbench.project.ProjectTaskContext;

import com.google.common.collect.Lists;

import java.util.Map;

/**
 * IDE specification for non-java projects.
 *
 * @author Keith M. Hughes
 */
public class NonJavaEclipseIdeProjectCreatorSpecification implements
    EclipseIdeProjectCreatorSpecification {

  /**
   * The Eclipse builder for non-Java only projects.
   */
  private static final String ECLIPSE_BUILDER_NON_JAVA =
      "org.eclipse.wst.common.project.facet.core.builder";

  /**
   * The Eclipse project nature for text only projects.
   */
  private static final String ECLIPSE_NATURE_NON_JAVA =
      "org.eclipse.wst.common.project.facet.core.nature";

  @Override
  public void addSpecificationData(Project project, ProjectTaskContext context,
      Map<String, Object> freemarkerContext) {
    freemarkerContext.put(ECLIPSE_PROJECT_FIELD_NATURES,
        Lists.newArrayList(ECLIPSE_NATURE_NON_JAVA));
    freemarkerContext.put(ECLIPSE_PROJECT_FIELD_BUILDER, ECLIPSE_BUILDER_NON_JAVA);
  }

  @Override
  public void writeAdditionalFiles(Project project, ProjectTaskContext context,
      Map<String, Object> freemarkerContext, FreemarkerTemplater templater) throws Exception {
    // Nothing to do
  }
}
