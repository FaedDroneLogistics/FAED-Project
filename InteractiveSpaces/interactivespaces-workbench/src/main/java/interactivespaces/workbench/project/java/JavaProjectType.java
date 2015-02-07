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

package interactivespaces.workbench.project.java;

import interactivespaces.SimpleInteractiveSpacesException;
import interactivespaces.resource.NamedVersionedResourceCollection;
import interactivespaces.resource.NamedVersionedResourceWithData;
import interactivespaces.resource.analysis.OsgiResourceAnalyzer;
import interactivespaces.system.core.container.ContainerFilesystemLayout;
import interactivespaces.util.io.FileSupport;
import interactivespaces.util.io.FileSupportImpl;
import interactivespaces.workbench.project.ProjectDependency;
import interactivespaces.workbench.project.ProjectTaskContext;
import interactivespaces.workbench.project.activity.type.ProjectType;
import interactivespaces.workbench.tasks.WorkbenchTaskContext;

import com.google.common.collect.Sets;

import java.io.File;
import java.util.List;
import java.util.Set;

/**
 * Useful constants and methods for working with Java projects.
 *
 * @author Keith M. Hughes
 */
public abstract class JavaProjectType implements ProjectType {

  /**
   * The file support to use.
   */
  private FileSupport fileSupport = FileSupportImpl.INSTANCE;

  /**
   * Source location for the Java source files.
   */
  public static final String SOURCE_MAIN_JAVA = "src/main/java";

  /**
   * Source location for tests.
   */
  public static final String SOURCE_MAIN_TESTS = "src/test/java";

  /**
   * The extras component for testing support.
   */
  public static final String TESTING_EXTRAS_COMPONENT = "testing";

  /**
   * Get a classpath that would be used at runtime for the project.
   *
   * @param needsDynamicArtifacts
   *          {@code true} if needs artifacts from the dynamic projects
   * @param projectTaskContext
   *          the project build context
   * @param classpath
   *          the classpath list to add to
   * @param extension
   *          any Java extension, can be {@code null}
   * @param workbenchTaskContext
   *          the workbench task context
   */
  public void getRuntimeClasspath(boolean needsDynamicArtifacts, ProjectTaskContext projectTaskContext,
      List<File> classpath, JavaProjectExtension extension, WorkbenchTaskContext workbenchTaskContext) {
    classpath.addAll(workbenchTaskContext.getControllerSystemBootstrapClasspath());

    addDependenciesFromDynamicProjectTaskContexts(projectTaskContext, classpath);

    addDependenciesFromUserBootstrap(needsDynamicArtifacts, projectTaskContext, classpath, workbenchTaskContext);

    if (extension != null) {
      extension.addToClasspath(classpath, projectTaskContext);
    }
  }

  /**
   * Add all generated artifacts from all dynamic dependencies to the classpath.
   *
   * @param projectTaskContext
   *          context for the project the classpath is needed for
   * @param classpath
   *          the classpath to add to
   */
  private void
      addDependenciesFromDynamicProjectTaskContexts(ProjectTaskContext projectTaskContext, List<File> classpath) {
    Set<File> filesToAdd = Sets.newHashSet();
    for (ProjectTaskContext dynamicProjectTaskContext : projectTaskContext.getDynamicProjectDependencyContexts()) {
      filesToAdd.addAll(dynamicProjectTaskContext.getGeneratedArtifacts());
    }

    classpath.addAll(filesToAdd);
  }

  /**
   * Add dependencies to the classpath if they are found in the user bootstrap folder of the controller.
   *
   * @param needsDynamicArtifacts
   *          {@code true} if needs artifacts from the dynamic projects
   * @param projectTaskContext
   *          the project build context
   * @param classpath
   *          the classpath list
   * @param wokbenchTaskContext
   *          the workbench task context
   */
  private void addDependenciesFromUserBootstrap(boolean needsDynamicArtifacts, ProjectTaskContext projectTaskContext,
      List<File> classpath, WorkbenchTaskContext wokbenchTaskContext) {
    NamedVersionedResourceCollection<NamedVersionedResourceWithData<String>> startupResources =
        new OsgiResourceAnalyzer(wokbenchTaskContext.getWorkbench().getLog()).getResourceCollection(fileSupport
            .newFile(wokbenchTaskContext.getControllerDirectory(), ContainerFilesystemLayout.FOLDER_USER_BOOTSTRAP));
    for (ProjectDependency dependency : projectTaskContext.getProject().getDependencies()) {
      // Skip the dependency if a dynamic project that exists on the workbench project path.
      if (dependency.isDynamic() && wokbenchTaskContext.getDynamicProjectFromProjectPath(dependency) != null) {
        continue;
      }

      NamedVersionedResourceWithData<String> dependencyProvider =
          startupResources.getResource(dependency.getIdentifyingName(), dependency.getVersion());
      if (dependencyProvider != null) {
        classpath.add(fileSupport.newFile(dependencyProvider.getData()));
      } else {
        // TODO(keith): Collect all missing and put into a single exception.
        throw new SimpleInteractiveSpacesException(String.format(
            "Project has listed dependency that isn't available %s:%s", dependency.getIdentifyingName(),
            dependency.getVersion()));

      }
    }
  }

  /**
   * Get a classpath that would be used as part of the project for the project.
   *
   * <p>
   * This includes runtime classes.
   *
   * @param needsDynamicArtifacts
   *          {@code true} if needs artifacts from the dynamic projects
   * @param projectTaskContext
   *          the project build context
   * @param classpath
   *          the classpath to add to
   * @param extension
   *          any Java extension, can be {@code null}
   * @param wokbenchTaskContext
   *          the workbench task context
   */
  public void getProjectClasspath(boolean needsDynamicArtifacts, ProjectTaskContext projectTaskContext,
      List<File> classpath, JavaProjectExtension extension, WorkbenchTaskContext wokbenchTaskContext) {
    getRuntimeClasspath(needsDynamicArtifacts, projectTaskContext, classpath, extension, wokbenchTaskContext);

    projectTaskContext.getWorkbenchTaskContext().addExtrasControllerExtensionsClasspath(classpath,
        TESTING_EXTRAS_COMPONENT);
  }
}
