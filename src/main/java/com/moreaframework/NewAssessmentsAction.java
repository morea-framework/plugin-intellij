package com.MoreaFramework;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.yaml.snakeyaml.Yaml;
import java.io.*;
import java.util.Arrays;
import java.util.Map;
import org.yaml.snakeyaml.DumperOptions;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class NewAssessmentsAction extends AnAction {

  //Refers to the Folder
  private VirtualFile selectedFile;
    @Override
    public void actionPerformed(AnActionEvent e) {
        MoreaUtils morea = new MoreaUtils();
        String input = Messages.showInputDialog("Enter Assessments Page Name:", "New Assessments Page", Messages.getQuestionIcon(), "assessment-example", new InputValidator() {
            @Override
            public boolean checkInput(String inputString) {
                return !inputString.isEmpty();
            }

            @Override
            public boolean canClose(String inputString) {
                return checkInput(inputString);
            }
        });

        // Check if name to create is not null
        if (input != null) {
            // Check input name matches MOREA naming convention
            input = morea.toMoreaName(input, "assessment");
            VirtualFile directory = morea.checkDupes(e, input);

            String finalInput = input;
            WriteAction.run(() -> {
                try {
                    VirtualFile readingFile = directory.createChildData(this, finalInput + ".md");
                    try (OutputStream outputStream = readingFile.getOutputStream(this)) {
                        String content = "---\n" +
                                "title: \"CHANGE ME\"\n" +
                                "published: true\n" +
                                "morea_id: " + finalInput + "\n" +
                                "morea_type: assessment\n" +
                                "morea_summary: \"CHANGE ME\"\n" +
                                "morea_sort_order: \n" +
                                "morea_start_date: \n" +
                                "morea_labels: \n" +
                                "---\n\n" +
                                "## \"CHANGE ME\"\n\n" +
                                "This is a sample content for the newly creating assessment.md file";
                        outputStream.write(content.getBytes());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                VirtualFile moduleFile = selectedFile.findChild("module-" + selectedFile.getName() + ".md");
                System.out.println("ModuleFile: " + moduleFile);
                System.out.println("ModuleFile getPath: " + moduleFile.getPath());

                //------Testing-------

              try {
                // Create a BufferedReader to read the Markdown file
                BufferedReader reader = new BufferedReader(new FileReader(moduleFile.getPath()));
                System.out.println("Error 1");
                // Read the YAML front matter into a string
                String yamlString = "";
                String line;
                int count = 0;
                while ((line = reader.readLine()) != null) {
                  if (line.equals("---")) {
                    if(count == 0){
                      count++;
                    }
                    else {
                      break;
                    }
                  }
                  yamlString += line + "\n";
                }
                System.out.println("Error 2");
                // Parse the YAML string using SnakeYAML
                DumperOptions options = new DumperOptions();
                options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

                Yaml yaml = new Yaml(options);
                Map<String, Object> yamlMap = yaml.load(yamlString);

                // Modify the title and author values
                System.out.println("yamlString: " + yamlString);

                yamlMap.put("morea_assessments", Arrays.asList(finalInput));


                // Serialize the YAML map back into a string
                String newYamlString = yaml.dump(yamlMap);

                // Print the modified YAML string
                System.out.println(newYamlString);
                // Change this to Overwrite the FILE!
                FileWriter writer = new FileWriter(moduleFile.getPath());
                writer.write("---\n");
                writer.write(newYamlString);
                writer.write("---\n");

                // Close the reader
                writer.close();
                reader.close();
              } catch (IOException e2) {
                System.out.println("An error occurred while reading the file.");
                e2.printStackTrace();
              }
              System.out.println("Error 5");



      //------Testing-------
            });
        }
    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e);
        selectedFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        if (selectedFile != null) {
            VirtualFile parent = selectedFile.getParent();
            e.getPresentation().setEnabledAndVisible(parent != null && "morea".equals(parent.getName()));
        } else {
            e.getPresentation().setEnabledAndVisible(false);
        }
    }
}
