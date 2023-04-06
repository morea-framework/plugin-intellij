package com.moreaframework;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.moreaframework.settings.AppSettingsState;

import java.io.IOException;
import java.io.OutputStream;

public class NewModuleAction extends AnAction {
  @Override
    public void actionPerformed(AnActionEvent e) {
        String input = Messages.showInputDialog("Enter a name for the new Morea Module  :", "New Morea Module", Messages.getQuestionIcon(), "module-example", new InputValidator() {
            @Override
            public boolean checkInput(String input) {
                return !input.isEmpty();
            }

            @Override
            public boolean canClose(String input) {
                return checkInput(input);
            }
        });

        if (input.startsWith("module-")) {
            input = input.substring(7); // remove module-
        }

        MoreaUtils morea = new MoreaUtils();

        AppSettingsState settings = AppSettingsState.getInstance();
        boolean outcomes = settings.outcomeStatus;
        boolean assessments = settings.assessmentStatus;
        boolean experiences = settings.experienceStatus;
        boolean readings = settings.readingStatus;

        String outcomeString = "\n  - outcome-" + input;
        String assessmentString = "\n  - assessment-" + input;
        String experienceString = "\n  - experience-" + input;
        String readingString = "\n  - reading-" + input;

        if(outcomes == false){
          outcomeString = "";
        }
        if(assessments == false){ assessmentString = "";}
        if(experiences == false){ experienceString = "";}
        if(readings == false){ readingString = "";}

        if (input != null) {
            // Do something with the input, for example, create a new file with the specified name
            // in the current project directory
            VirtualFile folder = e.getData(CommonDataKeys.VIRTUAL_FILE);
            System.out.println("File Path: " + folder);
            // within the folder, insert a file
            // this is needed or else you can't perform actions on the plugin test
            String finalOutcomeString = outcomeString;
            String finalReadingString = readingString;
            String finalExperienceString = experienceString;
            String finalAssessmentString = assessmentString;
            String finalInput = input;

            WriteAction.run(() -> {
                try {
                    // create a directory with the specified name
                    VirtualFile newDir = folder.createChildDirectory(this, finalInput);

                    // create a .md file called module-input.md inside the new directory
                    VirtualFile newFile = newDir.createChildData(this, "module-" + finalInput + ".md");

                    // Create the file so it matches
                    try (OutputStream outputStream = newFile.getOutputStream(this)) {
                        String content = "---" + "\n" +
                                "title: \"" + "CHANGE ME" + "\"" + "\n" +
                                "published: true" + "\n" +
                                "morea_coming_soon: false" + "\n" +
                                "morea_id: module-" + finalInput + "\n" +
                                "morea_outcomes:"+ finalOutcomeString +"" + "\n" +
                                "morea_readings:"+ finalReadingString +"" + "\n" +
                                "morea_experiences:"+ finalExperienceString +"" +"\n" +
                                "morea_assessments:"+ finalAssessmentString +"" + "\n" +
                                "morea_type: module" + "\n" +
                                "morea_icon_url:" + "\n" +
                                "morea_start_date:" + "\n" +
                                "morea_end_date:" + "\n" +
                                "morea_labels:" + "\n" +
                                "morea_sort_order:" +
                                "\n" +
                                "---";
                        outputStream.write(content.getBytes());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    if(readings == true) {
                        String reading = "reading-" + finalInput;
                      // Create Reading.md Template
                      newFile = newDir.createChildData(this, reading + ".md");
                      // Create the file so it matches
                      try (OutputStream outputStream = newFile.getOutputStream(this)) {
                        String content = morea.pageFrontMatter(reading, "reading");
                        outputStream.write(content.getBytes());
                      } catch (IOException ex) {
                        ex.printStackTrace();
                      }
                    }

                    if(assessments == true) {
                        String assessment = "assessment-" + finalInput;
                      //Create Assessment.md Template
                      newFile = newDir.createChildData(this, assessment + ".md");
                      // Create the file so it matches
                      try (OutputStream outputStream = newFile.getOutputStream(this)) {
                          String content = morea.pageFrontMatter(assessment, "assessment");
                        outputStream.write(content.getBytes());
                      } catch (IOException ex) {
                        ex.printStackTrace();
                      }
                    }

                    if(outcomes == true) {
                        String outcome = "outcome-" + finalInput;
                      //Create Outcome.md Template
                      newFile = newDir.createChildData(this, outcome + ".md");
                      // Create the file so it matches
                      try (OutputStream outputStream = newFile.getOutputStream(this)) {
                          String content = morea.pageFrontMatter(outcome, "outcome");
                        outputStream.write(content.getBytes());
                      } catch (IOException ex) {
                        ex.printStackTrace();
                      }
                    }

                    if(experiences == true) {
                        String experience = "experience-" + finalInput;
                      //Create Experience.md Template
                      newFile = newDir.createChildData(this, experience + ".md");
                      // Create the file so it matches
                      try (OutputStream outputStream = newFile.getOutputStream(this)) {
                          String content = morea.pageFrontMatter(experience, "experience");
                        outputStream.write(content.getBytes());
                      } catch (IOException ex) {
                        ex.printStackTrace();
                      }
                    }

                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
        }
        else{
          System.out.println("Input is null");
        }
    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e);
        VirtualFile selectedFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        if (selectedFile != null) {
            e.getPresentation().setEnabledAndVisible("morea".equals(selectedFile.getName()));
        } else {
            e.getPresentation().setEnabledAndVisible(false);
        }
    }
}



