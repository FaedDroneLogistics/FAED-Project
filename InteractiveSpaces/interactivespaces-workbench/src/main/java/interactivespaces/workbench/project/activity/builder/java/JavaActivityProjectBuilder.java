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

package interactivespaces.workbench.project.activity.builder.java;

import interactivespaces.util.io.FileSupport;
import interactivespaces.util.io.FileSupportImpl;
import interactivespaces.workbench.project.ProjectTaskContext;
import interactivespaces.workbench.project.activity.ActivityProject;
import interactivespaces.workbench.project.activity.builder.BaseActivityProjectBuilder;
import interactivespaces.workbench.project.java.JavaJarCompiler;
import interactivespaces.workbench.project.java.JavaProjectExtension;
import interactivespaces.workbench.project.java.JavaxJavaJarCompiler;
import interactivespaces.workbench.project.java.ContainerInfo;
import interactivespaces.workbench.project.java.ProjectJavaCompiler;
import interactivespaces.workbench.project.test.JavaTestRunner;

import java.io.File;

/**
 * A {@link ProjectBuilder} for Java-base activity projects.
 *
 * @author Keith M. Hughes
 */
public class JavaActivityProjectBuilder extends BaseActivityProjectBuilder {

  /**
   * File extension to give the build artifact.
   */
  private static final String JAR_FILE_EXTENSION = "jar";

  /**
   * The extensions for this builder.
   */
  private final JavaProjectExtension extensions;

  /**
   * The compiler for Java JARs.
   */
  private final JavaJarCompiler compiler = new JavaxJavaJarCompiler();

  /**
   * File support to use.
   */
  private final FileSupport fileSupport = FileSupportImpl.INSTANCE;

  /**
   * Construct a builder with no extensions.
   */
  public JavaActivityProjectBuilder() {
    this(null);
  }

  /**
   * Construct a builder with the given extensions.
   *
   * @param extensions
   *          the extensions to use, can be {@code null}
   */
  public JavaActivityProjectBuilder(JavaProjectExtension extensions) {
    this.extensions = extensions;
  }

  @Override
  public boolean onBuild(ActivityProject project, ProjectTaskContext context, File stagingDirectory) {
    try {
      File buildDirectory = context.getBuildDirectory();
      File compilationDirectory = getCompilationOutputDirectory(buildDirectory);
      File jarDestinationFile = getBuildDestinationFile(project, stagingDirectory, JAR_FILE_EXTENSION);
      project.setActivityExecutable(jarDestinationFile.getName());

      if (compiler.buildJar(jarDestinationFile, compilationDirectory, extensions, new ContainerInfo(), context)) {
        return runTests(jarDestinationFile, context);
      }

      return false;
    } catch (Exception e) {
      context.getWorkbenchTaskContext().handleError("Error while building java activity project", e);

      return false;
    }
  }

  /**
   * Run any tests for the project.
   *
   * @param jarDestinationFile
   *          the destination file for the built project
   * @param context
   *          the project build context
   *
   * @return {@code true} if all tests succeeded
   */
  private boolean runTests(File jarDestinationFile, ProjectTaskContext context) {
    JavaTestRunner runner = new JavaTestRunner();

    return runner.runTests(jarDestinationFile, extensions, context);
  }

  /**
   * Create the output directory for the activity compilation.
   *
   * @param buildDirectory
   *          the root of the build folder
   *
   * @return the output directory for building
   */
  private File getCompilationOutputDirectory(File buildDirectory) {
    File outputDirectory =
        new File(buildDirectory, ProjectJavaCompiler.BUILD_DIRECTORY_CLASSES_MAIN);
    fileSupport.directoryExists(outputDirectory);

    return outputDirectory;
  }
}
