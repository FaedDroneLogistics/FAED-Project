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

package interactivespaces.service.image.gesture;

import java.util.Map;

/**
 * A listener for hand events from the gesture camera.
 *
 * @author Keith M. Hughes
 */
public interface GestureHandListener {

  /**
   * A new set of hands have come in.
   *
   * <p>
   * The map is not modifiable.
   *
   * @param hands
   *          the hands obtained indexed by their ID.
   */
  void onGestureHands(Map<String, GestureHand> hands);
}
