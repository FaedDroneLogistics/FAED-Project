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

package interactivespaces.liveactivity.runtime.activity.wrapper;

import interactivespaces.activity.Activity;
import interactivespaces.activity.execution.ActivityExecutionContext;

/**
 * A runner for activities.
 *
 * @author Keith M. Hughes
 */
public interface ActivityWrapper {

  /**
   * Create the wrapper's instance of the activity.
   *
   * <p>
   * This can be called again after {@link #destroy()} has been called.
   *
   * @return the new activity instance
   */
  Activity newInstance();

  /**
   * Create a new execution context for the activity.
   *
   * @return the new execution context
   */
  ActivityExecutionContext newExecutionContext();
}
