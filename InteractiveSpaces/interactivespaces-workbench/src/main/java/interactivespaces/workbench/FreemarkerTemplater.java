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

package interactivespaces.workbench;

import interactivespaces.InteractiveSpacesException;
import interactivespaces.SimpleInteractiveSpacesException;
import interactivespaces.util.io.FileSupport;
import interactivespaces.util.io.FileSupportImpl;
import interactivespaces.util.resource.ManagedResource;

import com.google.common.collect.Lists;
import com.google.common.io.Closeables;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

/**
 * A templater using Freemarker.
 *
 * <p>
 * This implementation supports the concept of multiple evaluation passes. This is useful when cascading definitions
 * need to be resolved in the output. Say, for a project definition, there is a concept of {@code packageName=$
 * directoryName}.${className}} and then the template itself references {@code $ packageName} , then the first pass will
 * resolve packageName, and the second pass will resolve ${directoryName} and ${className}. Since this is a templating
 * language, evaluations are not recursive, and so this is necessary to properly handle the output.
 *
 * @author Keith M. Hughes
 */
public class FreemarkerTemplater implements ManagedResource {

  /**
   * Base directory where templates are kept.
   */
  public static final File TEMPLATE_LOCATION = new File("templates");

  /**
   * File support instance to use.
   */
  private FileSupport fileSupport = FileSupportImpl.INSTANCE;

  /**
   * The configuration used by Freemarker.
   */
  private Configuration freemarkerConfig;

  @Override
  public synchronized void startup() {
    try {
      freemarkerConfig = new Configuration();
      freemarkerConfig.setDirectoryForTemplateLoading(TEMPLATE_LOCATION);
      // Specify how templates will see the data-model. This is an
      // advanced topic... but just use this:
      freemarkerConfig.setObjectWrapper(new DefaultObjectWrapper());
    } catch (IOException e) {
      throw new InteractiveSpacesException("Cannot initialize activity project creator", e);
    }
  }

  @Override
  public synchronized void shutdown() {
    freemarkerConfig = null;
  }

  /**
   * Get the configuration to use, and also check that the system has been started.
   *
   * @return freemarker configuration
   */
  private synchronized Configuration getConfiguration() {
    if (freemarkerConfig == null) {
      throw new SimpleInteractiveSpacesException("Templater has not been started");
    }
    return freemarkerConfig;
  }

  /**
   * Process a string template.
   *
   * @param data
   *          data for template
   * @param templateContent
   *          string template to process
   * @param defineResult
   *          target value to define with new value, or {@code null} if no definition should take place
   * @param evaluationPasses
   *          number of evaluation passes to perform on the processing
   *
   * @return processed template
   */
  public String processStringTemplate(Map<String, Object> data, String templateContent, String defineResult,
      int evaluationPasses) {
    for (int passesRemaining = evaluationPasses; passesRemaining > 0; passesRemaining--) {
      templateContent = processStringTemplate(data, templateContent);
      if (defineResult != null) {
        data.put(defineResult, templateContent);
      }
    }
    return templateContent;
  }

  /**
   * Process a string template.
   *
   * @param data
   *          data for template
   * @param templateContent
   *          string template to process
   *
   * @return processed template
   */
  public String processStringTemplate(Map<String, Object> data, String templateContent) {
    try {
      Template temp =
          new Template("generator for " + templateContent, new StringReader(templateContent), getConfiguration());
      StringWriter stringWriter = new StringWriter();
      temp.process(data, stringWriter);
      return stringWriter.toString();
    } catch (Exception e) {
      throw new InteractiveSpacesException(String.format("Could not instantiate string template %s", templateContent),
          e);
    }
  }

  /**
   * Write out the template.
   *
   * @param data
   *          data for the template
   * @param outputFile
   *          file where the template will be written
   * @param template
   *          which template to use
   * @param evaluationPasses
   *          number of evaluation passes to perform on the processing
   */
  public void writeTemplate(Map<String, Object> data, File outputFile, String template, int evaluationPasses) {
    fileSupport.directoryExists(outputFile.getParentFile());
    List<File> deleteList = Lists.newArrayList();
    File tempFile = new File(String.format("%s.%d", outputFile.getAbsolutePath(), evaluationPasses));
    deleteList.add(tempFile);
    File inputFile = new File(template);
    if (inputFile.isAbsolute()) {
      fileSupport.copyFile(inputFile, tempFile);
    }
    for (int passesRemaining = evaluationPasses; passesRemaining > 0; passesRemaining--) {
      tempFile = new File(String.format("%s.%d", outputFile.getAbsolutePath(), passesRemaining - 1));
      deleteList.add(tempFile);
      if (passesRemaining == 1) {
        writeTemplate(data, outputFile, template);
      } else {
        writeTemplate(data, tempFile, template);
        template = tempFile.getAbsolutePath();
      }
    }

    // By design. if there are any errors processing the templates, these files will remain.
    for (File toDelete : deleteList) {
      fileSupport.delete(toDelete);
    }
  }

  /**
   * Write out the template.
   *
   * @param data
   *          data for the template
   * @param outputFile
   *          file where the template will be written
   * @param template
   *          which template to use
   */
  public void writeTemplate(Map<String, Object> data, File outputFile, String template) {
    fileSupport.directoryExists(outputFile.getParentFile());

    Writer out = null;
    Reader in = null;
    try {
      Template temp;
      if (template.startsWith("/")) {
        in = new FileReader(template);
        temp = new Template(template, in, getConfiguration());
      } else {
        temp = getConfiguration().getTemplate(template);
      }

      out = new FileWriter(outputFile);
      temp.process(data, out);
    } catch (Exception e) {
      throw new SimpleInteractiveSpacesException(String.format("Could not instantiate template %s to %s", template,
          outputFile.getAbsolutePath()), e);
    } finally {
      Closeables.closeQuietly(in);

      fileSupport.close(out, true);
    }
  }
}
