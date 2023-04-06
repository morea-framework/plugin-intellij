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

        AppSettingsState settings = AppSettingsState.getInstance();
        boolean outcomes = settings.outcomeStatus;
        boolean assessments = settings.assessmentStatus;
        boolean experiences = settings.experienceStatus;
        boolean readings = settings.readingStatus;

        String outcomeString = "\n  -"+ " " + "outcome-" + input;
        String assessmentString = "\n  -"+ " " +"assessment-" + input;
        String experienceString = "\n  -"+ " " +"experience-" + input;
        String readingString = "\n  -"+ " " + "reading-" + input;

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
          WriteAction.run(() -> {
                try {
                    // create a directory with the specified name
                    VirtualFile newDir;
                    if (input.startsWith("module-")) {
                        String dirName = input.substring(7); // remove "module-" prefix
                        newDir = folder.createChildDirectory(this, dirName);
                    } else {
                        newDir = folder.createChildDirectory(this, input);
                    }
                    // create a .md file called module-input.md inside the new directory
                    VirtualFile newFile;
                    if (input.startsWith("module-")) {
                        newFile = newDir.createChildData(this, input + ".md");
                    } else {
                        newFile = newDir.createChildData(this, "module-" + input + ".md");
                    }
                    // Create the file so it matches
                    try (OutputStream outputStream = newFile.getOutputStream(this)) {
                        String content = "---" + "\n" +
                                "title: \"" + "CHANGE ME" + "\"" + "\n" +
                                "published: true" + "\n" +
                                "morea_coming_soon: false" + "\n" +
                                "morea_id: " + input+ "\n" +
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
                      //Create Reading.md Template
                      newFile = newDir.createChildData(this, "reading" + "-" + input + ".md");
                      // Create the file so it matches
                      try (OutputStream outputStream = newFile.getOutputStream(this)) {
                        String content = "---" + "\n" +
                                "title: \"" + "CHANGE ME" + "\"" + "\n" +
                                "published: true" + "\n" +
                                "morea_id: " + " " +"reading" + "-" + input+ "\n" +
                                "morea_type: reading" + "\n" +
                                "morea_summary: sample morea summary text " + "\n" +
                                "morea_sort_order: " + "\n" +
                                "morea_labels:" + "\n" +
                                "---\n\n" +
                                "## \"CHANGE ME\"\n\n" ;
                        outputStream.write(content.getBytes());
                      } catch (IOException ex) {
                        ex.printStackTrace();
                      }
                    }

                    if(assessments == true) {
                      //Create Assessment.md Template
                      newFile = newDir.createChildData(this, "assessment" + "-" + input + ".md");
                      // Create the file so it matches
                      try (OutputStream outputStream = newFile.getOutputStream(this)) {
                          String content = "---" + "\n" +
                                  "title: \"" + "CHANGE ME" + "\"" + "\n" +
                                  "published: true" + "\n" +
                                  "morea_id: " + " " +"assessment" + "-" + input+ "\n" +
                                  "morea_type: assessment" + "\n" +
                                  "morea_summary: CHANGE ME " + "\n" +
                                  "morea_sort_order: " + "\n" +
                                  "morea_labels:" + "\n" +
                                  "---\n\n" +
                                  "## \"CHANGE ME\"\n\n" ;
                        outputStream.write(content.getBytes());
                      } catch (IOException ex) {
                        ex.printStackTrace();
                      }
                    }

                    if(outcomes == true) {
                      //Create Outcome.md Template
                      newFile = newDir.createChildData(this, "outcome" + "-" + input + ".md");
                      // Create the file so it matches
                      try (OutputStream outputStream = newFile.getOutputStream(this)) {
                          String content = "---" + "\n" +
                                  "title: \"" + "CHANGE ME" + "\"" + "\n" +
                                  "published: true" + "\n" +
                                  "morea_id: " + " " +"outcome" + "-" + input+ "\n" +
                                  "morea_type: outcome" + "\n" +
                                  "morea_summary: " + "\n" +
                                  "morea_sort_order: " + "\n" +
                                  "morea_labels:" + "\n" +
                                  "---\n\n" +
                                  "## \"CHANGE ME\"\n\n" ;
                        outputStream.write(content.getBytes());
                      } catch (IOException ex) {
                        ex.printStackTrace();
                      }
                    }

                    if(experiences == true) {
                      //Create Experience.md Template
                      newFile = newDir.createChildData(this, "experience" + "-" + input + ".md");
                      // Create the file so it matches
                      try (OutputStream outputStream = newFile.getOutputStream(this)) {
                          String content = "---" + "\n" +
                                  "title: \"" + "CHANGE ME" + "\"" + "\n" +
                                  "published: true" + "\n" +
                                  "morea_id: " + " " +"experience" + "-" + input+ "\n" +
                                  "morea_type: experience" + "\n" +
                                  "morea_summary: sample morea summary text " + "\n" +
                                  "morea_sort_order: 2" + "\n" +
                                  "morea_labels:" + "\n" +
                                  "---\n\n" +
                                  "## \"CHANGE ME\"\n\n" ;
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



