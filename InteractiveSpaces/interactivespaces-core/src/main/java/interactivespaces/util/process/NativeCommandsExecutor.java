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

package interactivespaces.util.process;

import interactivespaces.InteractiveSpacesException;
import interactivespaces.SimpleInteractiveSpacesException;

import java.util.List;

/**
 * Execute a series of commands, stopping if any fail.
 *
 * @author Keith M. Hughes
 */
public class NativeCommandsExecutor {

  /**
   * Execute the set of native commands.
   *
   * @param commands
   *          a list of commands to run
   *
   * @throws InteractiveSpacesException
   *           one of the commands failed
   */
  public void executeCommands(List<List<String>> commands) throws InteractiveSpacesException {
    for (List<String> command : commands) {
      NativeCommandRunner runner = new NativeCommandRunner();
      runner.execute(command);

      if (!runner.isSuccess()) {
        throw new SimpleInteractiveSpacesException(String.format("Command failed: %s", command));
      }
    }
  }
}
