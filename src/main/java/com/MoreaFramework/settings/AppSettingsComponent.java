package com.MoreaFramework.settings;

import com.intellij.ui.components.JBCheckBox;
import com.intellij.util.ui.FormBuilder;

import javax.swing.*;

/**
 * Supports creating and managing a {@link JPanel} for the Settings Dialog.
 */
public class AppSettingsComponent {

    private final JPanel myMainPanel;

    private final JLabel header = new JLabel("[New Module Button Settings]: Controls files automatically created when making a new Module");
    private final JBCheckBox myOutcomeStatus = new JBCheckBox("Outcomes");
    private final JBCheckBox myReadingStatus = new JBCheckBox("Readings");
    private final JBCheckBox myExperienceStatus = new JBCheckBox("Experiences");
    private final JBCheckBox myAssessmentStatus = new JBCheckBox("Assessments");

    public AppSettingsComponent() {
        myMainPanel = FormBuilder.createFormBuilder()
                .addComponent(header, 1)
                .addComponent(myOutcomeStatus, 1)
                .addComponent(myReadingStatus, 1)
                .addComponent(myExperienceStatus, 1)
                .addComponent(myAssessmentStatus, 1)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    public JPanel getPanel() {
        return myMainPanel;
    }

    public boolean getOutcomeStates() {
        return myOutcomeStatus.isSelected();
    }

    public void setMyOutcomeStatus(boolean newStatus) {
        myOutcomeStatus.setSelected(newStatus);
    }

    public boolean getReadingStatus() {
        return myReadingStatus.isSelected();
    }

    public void setMyReadingStatus(boolean newStatus) {
        myReadingStatus.setSelected(newStatus);
    }

    public boolean getExperienceStatus() {
        return myExperienceStatus.isSelected();
    }

    public void setMyExperienceStatus(boolean newStatus) {
        myExperienceStatus.setSelected(newStatus);
    }

    public boolean getAssessmentStatus() {
        return myAssessmentStatus.isSelected();
    }

    public void setMyAssessmentStatus(boolean newStatus) {
        myAssessmentStatus.setSelected(newStatus);
    }

}
