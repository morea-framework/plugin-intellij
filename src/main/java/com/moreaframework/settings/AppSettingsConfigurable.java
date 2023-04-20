package com.moreaframework.settings;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Provides controller functionality for application settings.
 */
public class AppSettingsConfigurable implements Configurable {

    private AppSettingsComponent mySettingsComponent;

    // A default constructor with no arguments is required because this implementation
    // is registered as an applicationConfigurable EP

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "MOREA settings";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        mySettingsComponent = new AppSettingsComponent();
        return mySettingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        AppSettingsState settings = AppSettingsState.getInstance();
        boolean modified = mySettingsComponent.getOutcomeStates() != settings.outcomeStatus;
        modified |= mySettingsComponent.getReadingStatus() != settings.readingStatus;
        modified |= mySettingsComponent.getExperienceStatus() != settings.experienceStatus;
        modified |= mySettingsComponent.getAssessmentStatus() != settings.assessmentStatus;
        return modified;
    }

    @Override
    public void apply() {
        AppSettingsState settings = AppSettingsState.getInstance();
        settings.outcomeStatus = mySettingsComponent.getOutcomeStates();
        settings.readingStatus = mySettingsComponent.getReadingStatus();
        settings.experienceStatus = mySettingsComponent.getExperienceStatus();
        settings.assessmentStatus = mySettingsComponent.getAssessmentStatus();

    }

    @Override
    public void reset() {
        AppSettingsState settings = AppSettingsState.getInstance();
        mySettingsComponent.setMyOutcomeStatus(settings.outcomeStatus);
        mySettingsComponent.setMyReadingStatus(settings.readingStatus);
        mySettingsComponent.setMyExperienceStatus(settings.experienceStatus);
        mySettingsComponent.setMyAssessmentStatus(settings.assessmentStatus);
    }

    @Override
    public void disposeUIResources() {
        mySettingsComponent = null;
    }

}
