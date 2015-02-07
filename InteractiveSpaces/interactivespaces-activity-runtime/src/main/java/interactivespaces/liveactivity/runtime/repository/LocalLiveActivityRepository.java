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

package interactivespaces.liveactivity.runtime.repository;

import interactivespaces.liveactivity.runtime.domain.InstalledLiveActivity;
import interactivespaces.util.resource.ManagedResource;

import java.util.List;

/**
 * Repository for live activities.
 *
 * @author Keith M. Hughes
 */
public interface LocalLiveActivityRepository extends ManagedResource {

  /**
   * Get a new instance of a locally installed activity.
   *
   * @return a new installed activity
   */
  InstalledLiveActivity newInstalledLiveActivity();

  /**
   * Get all locally installed activities.
   *
   * @return all locally installed activities
   */
  List<InstalledLiveActivity> getAllInstalledLiveActivities();

  /**
   * Get a locally installed activity by UUID.
   *
   * @param uuid
   *          UUID of the activity.
   *
   * @return the requested activity, or {@code null} if no activity with that
   *         UUID
   */
  InstalledLiveActivity getInstalledLiveActivityByUuid(String uuid);

  /**
   * Save an activity.
   *
   * @param activity
   *          the activity to save
   *
   * @return the saved activity
   */
  InstalledLiveActivity saveInstalledLiveActivity(InstalledLiveActivity activity);

  /**
   * Delete an activity from the repository.
   *
   * @param activity
   *          the activity to delete
   */
  void deleteInstalledLiveActivity(InstalledLiveActivity activity);
}
