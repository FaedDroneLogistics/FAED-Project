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

package interactivespaces.workbench.ui;

import interactivespaces.workbench.project.source.Source;

/**
 * The window manager for source windows.
 *
 * @author Keith M. Hughes
 */
public interface SourceWindowManager {

  /**
   * Add in a new source window.
   *
   * @param programSource
   *          the source for the window
   */
  void addNewSourceWindow(Source programSource);

  /**
   * Close all open windows.
   */
  void closeAllWindows();

  /**
   * Are the programmer windows visible?
   *
   * @return {@code true} if the windows are visible
   */
  boolean areWindowsVisible();

  /**
   * Set all programmer windows visible.
   *
   * @param visible
   *          {@code true} if the windows are to be visible,
   */
  void setWindowsVisible(boolean visible);

  /**
   * Add in a new code editor listener to be attached to all code editors.
   *
   * @param editorListener
   *          the editor listener to add
   */
  void addSourceEditorListener(SourceEditorListener editorListener);

  /**
   * Are there modified windows?
   *
   * @return {@code true} if there are modified windows
   */
  boolean hasModifiedWindows();

  /**
   * Save the contents of all code windows that need to be saved.
   */
  void saveAll();

  /**
   * Save the contents of the current window.
   */
  void saveCurrentWindow();

  /**
   * Get the path of the currently open programmer window.
   *
   * @return the path of the currently open programmer window, {@code null} if none
   */
  String getCurrentWindowPath();

  /**
   * Remove the source window for the specified path.
   *
   * <p>
   * A no-op if there is no window associated with the given path.
   *
   * @param sourceFilePath
   *        path for the source file
   */
  void removeSourceWindow(String sourceFilePath);

  /**
   * Revert the current window.
   *
   * <p>
   * A no-op if there is no current window.
   */
  void revertCurrentWindow();

  /**
   * Undo the edit of the the current window.
   *
   * <p>
   * A no-op if there is no current window.
   */
  void undoEditCurrentWindow();

  /**
   * Redo the edit of the the current window.
   *
   * <p>
   * A no-op if there is no current window.
   */
  void redoEditCurrentWindow();

  /**
   * Set the user interface factory to be used by the source window manager.
   *
   * @param userInterfaceFactory
   *          the factory to use
   */
  void setUserInterfaceFactory(UserInterfaceFactory userInterfaceFactory);
}
