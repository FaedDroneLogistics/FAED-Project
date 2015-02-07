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

package interactivespaces.controller.resource.deployment;

import interactivespaces.InteractiveSpacesException;
import interactivespaces.container.resource.deployment.ContainerResourceDeploymentCommitRequest;
import interactivespaces.container.resource.deployment.ContainerResourceDeploymentCommitResponse;
import interactivespaces.container.resource.deployment.ContainerResourceDeploymentCommitResponse.ContainerResourceDeploymentCommitStatus;
import interactivespaces.container.resource.deployment.ContainerResourceDeploymentItem;
import interactivespaces.container.resource.deployment.ContainerResourceDeploymentQueryRequest;
import interactivespaces.container.resource.deployment.ContainerResourceDeploymentQueryResponse;
import interactivespaces.container.resource.deployment.ContainerResourceDeploymentQueryResponse.QueryResponseStatus;
import interactivespaces.resource.ResourceDependency;
import interactivespaces.system.InteractiveSpacesEnvironment;
import interactivespaces.system.resources.ContainerResource;
import interactivespaces.system.resources.ContainerResourceCollection;
import interactivespaces.system.resources.ContainerResourceManager;
import interactivespaces.util.web.HttpClientHttpContentCopier;
import interactivespaces.util.web.HttpContentCopier;

import java.io.File;
import java.util.List;

/**
 * @author Keith M. Hughes
 */
public class ControllerContainerResourceDeploymentManager implements ContainerResourceDeploymentManager {

  /**
   * The container's resource manager.
   */
  private final ContainerResourceManager containerResourceManager;

  /**
   * The content copier.
   */
  private HttpContentCopier contentCopier;

  /**
   * The space environment.
   */
  private final InteractiveSpacesEnvironment spaceEnvironment;

  /**
   * Construct a deployment manager.
   *
   * @param containerResourceManager
   *          the resource manager for the container
   * @param spaceEnvironment
   *          the space environment
   */
  public ControllerContainerResourceDeploymentManager(ContainerResourceManager containerResourceManager,
      InteractiveSpacesEnvironment spaceEnvironment) {
    this.containerResourceManager = containerResourceManager;
    this.spaceEnvironment = spaceEnvironment;
  }

  @Override
  public void startup() {
    contentCopier = new HttpClientHttpContentCopier();
    contentCopier.startup();
  }

  @Override
  public void shutdown() {
    if (contentCopier != null) {
      contentCopier.shutdown();
      contentCopier = null;
    }
  }

  @Override
  public ContainerResourceDeploymentQueryResponse
      queryResources(ContainerResourceDeploymentQueryRequest deploymentQuery) {
    ContainerResourceCollection currentResources = containerResourceManager.getResources();
    if (currentlySatisfiesQuery(deploymentQuery, currentResources)) {
      return new ContainerResourceDeploymentQueryResponse(deploymentQuery.getTransactionId(),
          QueryResponseStatus.SPECIFIC_QUERY_SATISFIED);
    } else {
      ContainerResourceDeploymentQueryResponse response =
          new ContainerResourceDeploymentQueryResponse(deploymentQuery.getTransactionId(),
              QueryResponseStatus.SPECIFIC_QUERY_NOT_SATISFIED);

      List<ContainerResource> allContainerResources = currentResources.getAllResources();
      // TODO(keith): place all resources into the response for dependency
      // calculations on the maaster.

      return response;
    }
  }

  /**
   * Query the container to see if a series of deployment requests are already
   * satisfied.
   *
   * @param deploymentQuery
   *          the query
   * @param currentResources
   *          the current resources in the container
   *
   * @return {@code true} if already satisfies the query
   */
  private boolean currentlySatisfiesQuery(ContainerResourceDeploymentQueryRequest deploymentQuery,
      ContainerResourceCollection currentResources) {
    for (ResourceDependency query : deploymentQuery.getQueries()) {
      ContainerResource resource = currentResources.getResource(query.getName(), query.getVersionRange());
      if (resource == null) {
        return false;
      }
    }

    return true;
  }

  @Override
  public ContainerResourceDeploymentCommitResponse commitResources(ContainerResourceDeploymentCommitRequest request) {
    boolean success = true;
    for (ContainerResourceDeploymentItem item : request.getItems()) {
      File resourceFile = null;
      try {
        resourceFile = File.createTempFile("interactivespaces-", ".resource");
        contentCopier.copy(item.getResourceSourceUri(), resourceFile);
        containerResourceManager.addResource(
            new ContainerResource(item.getName(), item.getVersion(), item.getLocation()), resourceFile);
      } catch (Exception e) {
        success = false;
        throw new InteractiveSpacesException(
            String.format("Could not install resource %s", item.getResourceSourceUri()), e);
      } finally {
        if (resourceFile != null && resourceFile.exists()) {
          resourceFile.delete();
        }
      }
    }

    return new ContainerResourceDeploymentCommitResponse(request.getTransactionId(),
        success ? ContainerResourceDeploymentCommitStatus.SUCCESS : ContainerResourceDeploymentCommitStatus.FAILURE);
  }
}
