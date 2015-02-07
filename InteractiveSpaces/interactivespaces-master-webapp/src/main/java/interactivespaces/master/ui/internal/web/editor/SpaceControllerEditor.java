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

package interactivespaces.master.ui.internal.web.editor;

import interactivespaces.domain.basic.SpaceController;
import interactivespaces.master.server.services.SpaceControllerRepository;

import java.beans.PropertyEditorSupport;

/**
 * A property editor for {@link SpaceController} instances.
 *
 * @author Keith M. Hughes
 */
public class SpaceControllerEditor extends PropertyEditorSupport {

  /**
   * Repository for space controllers.
   */
  private SpaceControllerRepository spaceControllerRepository;

  /**
   * Construct a new editor.
   */
  public SpaceControllerEditor() {
  }

  /**
   * Construct a new editor.
   *
   * @param spaceControllerRepository
   *          the space controller repository
   */
  public SpaceControllerEditor(SpaceControllerRepository spaceControllerRepository) {
    this.spaceControllerRepository = spaceControllerRepository;
  }

  @Override
  public String getAsText() {
    Object o = getValue();

    if (o != null) {
      return ((SpaceController) o).getId();
    } else {
      return null;
    }
  }

  @Override
  public void setAsText(String text) throws IllegalArgumentException {
    if (text != null && text.trim().length() > 0) {
      SpaceController group = spaceControllerRepository.getSpaceControllerById(text);
      if (group != null) {
        setValue(group);
      } else {
        throw new IllegalArgumentException("No space controller with ID " + text);
      }
    } else {
      setValue(null);
    }
  }

  /**
   * Set the space controller repository to use.
   *
   * @param spaceControllerRepository
   *          the space controller repository
   */
  public void setSpaceControllerRepository(SpaceControllerRepository spaceControllerRepository) {
    this.spaceControllerRepository = spaceControllerRepository;
  }
}
