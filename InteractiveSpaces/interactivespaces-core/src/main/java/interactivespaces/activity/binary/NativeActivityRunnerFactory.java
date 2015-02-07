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

package interactivespaces.activity.binary;

import org.apache.commons.logging.Log;

/**
 * A factory for creating native activity launchers.
 *
 * @author Keith M. Hughes
 */
public interface NativeActivityRunnerFactory {

  /**
   * Get a native app runner for the platform.
   *
   * @param log
   *          the log to use for the runner
   *
   * @return an appropriate activity runner for the current OS
   */
  NativeActivityRunner newPlatformNativeActivityRunner(Log log);
}
