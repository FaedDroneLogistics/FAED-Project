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

package interactivespaces.workbench.ui.wizard;

import interactivespaces.workbench.ui.ImagePanel;
import interactivespaces.workbench.ui.validation.ValidationMessageDisplay;
import interactivespaces.workbench.ui.validation.ValidationMessageType;
import interactivespaces.workbench.ui.validation.ValidationResult;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * A base class for creating wizard classes.
 *
 * @author Keith M. Hughes
 */
public class JWizardDialog extends JDialog implements ActionListener, ValidationMessageDisplay {

  /**
   * Where the message icon will go.
   */
  private ImagePanel messageIcon;

  /**
   * There is a message on display
   */
  protected boolean hasMessage;

  /**
   * There is an error.
   */
  protected boolean hasErrorMessage;

  /**
   * Where the messages will go.
   */
  private JTextArea messageLabel;

  /**
   * The button for canceling the wizard.
   */
  private JButton cancelButton;

  /**
   * The button for proceeding the wizard.
   */
  private JButton proceedButton;

  /**
   * The button for moving to the previous wizard component.
   */
  private JButton previousButton;

  /**
   * The wizard for this dialog.
   */
  private Wizard wizard;

  /**
   * The current wizard component visible in the dialog
   */
  private JComponent currentComponent;

  private JPanel infoPanel;

  public JWizardDialog(String title, Frame parent, Wizard wizard) {
    super(parent, title, true);

    this.wizard = wizard;
    wizard.setValidationMessageDisplay(this);
    wizard.initializeWizard();

    cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(this);

    previousButton = new JButton("Previous");
    previousButton.addActionListener(this);

    proceedButton = new JButton();
    proceedButton.addActionListener(this);

    updateButtons();

    // Put the buttons in a flow panel so that they get centered and are
    // next to each other
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.add(cancelButton);
    buttonPanel.add(previousButton);
    buttonPanel.add(proceedButton);
    getContentPane().add(buttonPanel, BorderLayout.SOUTH);

    infoPanel = new JPanel(new GridBagLayout());
    GridBagConstraints infoConstraints = new GridBagConstraints();
    infoConstraints.anchor = GridBagConstraints.FIRST_LINE_START;

    messageIcon = new ImagePanel();
    infoConstraints.gridx = 0;
    infoConstraints.gridy = 0;
    infoConstraints.fill = GridBagConstraints.HORIZONTAL;
    infoPanel.add(messageIcon, infoConstraints);

    messageLabel = new JTextArea();
    messageLabel.setRows(3);
    messageLabel.setEditable(false);
    messageLabel.setOpaque(false);
    infoConstraints.gridx++;
    infoConstraints.fill = GridBagConstraints.BOTH;
    infoPanel.add(messageLabel, infoConstraints);
    getContentPane().add(infoPanel, BorderLayout.NORTH);

    setWizardJComponent();

    pack();
  }

  /**
   * Make the wizard's current jcomponent visible.
   */
  private void setWizardJComponent() {
    Container contentPane = getContentPane();
    if (currentComponent != null) {
      contentPane.remove(currentComponent);
      currentComponent.setVisible(false);
    }

    currentComponent = wizard.getCurrentJComponent();
    contentPane.add(currentComponent, BorderLayout.CENTER);
    currentComponent.setVisible(true);
  }

  @Override
  public void actionPerformed(ActionEvent ae) {
    Object command = ae.getSource();

    if (command.equals(proceedButton)) {
      proceedButtonPressed();
    } else if (command.equals(previousButton)) {
      previousButtonPressed();
    } else if (command.equals(cancelButton)) {
      setVisible(false);
      dispose();
    }
  }

  /**
   * The proceed button has been pressed.
   */
  private void proceedButtonPressed() {
    if (wizard.hasNext()) {
      wizard.moveNext();

      setWizardJComponent();

      updateButtons();

      validate();
      repaint();
    } else {
      if (wizard.validateCurrentWizard(true) != ValidationResult.ERRORS) {
        wizardDone();
      } else {
        updateButtons();
      }
    }
  }

  /**
   * The previous button was pressed.
   */
  private void previousButtonPressed() {
    // Assuming we only see a button press if the wizard could move
    wizard.movePrevious();

    setWizardJComponent();

    updateButtons();
  }

  /**
   * Update the buttons on the wizard dialog.
   */
  private void updateButtons() {
    proceedButton.setText(wizard.hasNext() ? "Next" : "Finish");

    previousButton.setEnabled(wizard.hasPrevious());
  }

  /**
   * The wizard is done.
   */
  private void wizardDone() {
    wizard.completeWizard();

    setVisible(false);
    dispose();
  }

  protected String getDefaultMessage() {
    return "   ";
  }

  @Override
  public void showValidationMessage(ValidationMessageType type, String message) {
    hasMessage = true;
    switch (type) {
      case ERROR:
        messageIcon.setImage(ImagePanel.loadImage("/images/WizardError.gif"));
        hasErrorMessage = true;
        break;
      case INFO:
        messageIcon.setImage(ImagePanel.loadImage("/images/WizardInfo.gif"));
        hasErrorMessage = false;
        break;
      case WARNING:
        messageIcon.setImage(ImagePanel.loadImage("/images/WizardWarning.gif"));
        hasErrorMessage = false;
        break;
      default:
        messageIcon.setImage(null);
    }

    messageLabel.setText(message);
  }

  @Override
  public void clearValidationMessage() {
    messageIcon.setImage(null);
    messageLabel.setText("");

    hasMessage = false;
    hasErrorMessage = false;
  }
}
