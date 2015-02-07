/*
 * Copyright (C) 2014 Google Inc.
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

package interactivespaces.service.control.opensoundcontrol;

/**
 * An Open Sound Control method.
 *
 * @param <M>
 *          type of the incoming message
 *
 * @author Keith M. Hughes
 */
public interface OpenSoundControlMethod<M extends OpenSoundControlIncomingMessage> {

  /**
   * Invoke the method on an Open Sound Control message.
   *
   * @param message
   *          the message
   */
  void invoke(M message);
}
