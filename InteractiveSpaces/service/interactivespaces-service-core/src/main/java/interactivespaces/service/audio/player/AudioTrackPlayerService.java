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

package interactivespaces.service.audio.player;

import interactivespaces.service.SupportedService;

import org.apache.commons.logging.Log;

/**
 * A factory for {@link AudioTrackPlayer} instances.
 *
 * @author Keith M. Hughes
 */
public interface AudioTrackPlayerService extends SupportedService {

  /**
   * Name for the service.
   */
  String SERVICE_NAME = "audio.player";

  /**
   * Get a new track player.
   *
   * @param log
   *          a log for logging information
   *
   * @return a track player ready to work.
   */
  AudioTrackPlayer newTrackPlayer(Log log);
}
