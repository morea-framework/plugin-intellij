package com.moreaframework;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;


import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class MoreaUtils {

    public static boolean existsInDirOrSubdirs(File directory, String fileName, String moduleId) {
        // Check if the given path is a directory
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                // Iterate through all files and directories within the directory
                for (File file : files) {
                    // If the current path is a directory, perform a recursive call
                    if (file.isDirectory()) {
                        if (existsInDirOrSubdirs(file, fileName, moduleId)) {
                            return true;
                        }
                    } else {
                        // If the current file has the same name as the target file, return true
                        if (file.getName().equals(fileName)) {
                            return true;
                        } else {
                            try {
                                // Retrieve the module ID from the current file's YAML
                                String fileModuleId = getModuleIdFromYaml(file);
                                // If the module ID matches the target module ID, return true
                                if (fileModuleId != null && moduleId.equals(fileModuleId)) {
                                    return true;
                                }
                            } catch (IOException e) {
                                // Print any IOException that occurs during YAML parsing
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        // Return false if neither the file name nor the module ID match any file in the directory or subdirectories
        return false;
    }


    public static String getModuleIdFromYaml(File file) throws IOException {
        // Use try-with-resources to ensure the BufferedReader is closed automatically
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            StringBuilder yamlContent = new StringBuilder();
            boolean insideYamlFrontmatter = false;

            // Read through the file line by line
            while ((line = reader.readLine()) != null) {
                // Check if the line contains the YAML front matter delimiter
                if (line.trim().equals("---")) {
                    // Toggle the insideYamlFrontmatter flag when encountering a delimiter
                    insideYamlFrontmatter = !insideYamlFrontmatter;
                    // Break out of the loop when encountering the closing delimiter
                    if (!insideYamlFrontmatter) {
                        break;
                    }
                } else if (insideYamlFrontmatter) {
                    // Append the line to yamlContent if it is within the YAML front matter
                    yamlContent.append(line).append("\n");
                }
            }

            // Create a Yaml object and load the content as a map
            Yaml yaml = new Yaml();
            Map<String, Object> yamlMap = yaml.load(yamlContent.toString());

            // Return null if the YAML map is empty or does not contain the "module_id" key
            if (yamlMap == null || !yamlMap.containsKey("module_id")) {
                return null;
            }

            // Return the value of the "module_id" key
            return yamlMap.get("module_id").toString();
        }
    }


    public String toMoreaName(String input, String type) {
        input = input.toLowerCase(Locale.ROOT);
        if (!input.startsWith(type)) {
            input = type + "-" + input;
        }
        return input;
    }

    public VirtualFile checkDupes(AnActionEvent e, String moduleId) {
        VirtualFile selectedFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);

        String fileName = moduleId + ".md";
        VirtualFile directory = selectedFile.isDirectory() ? selectedFile : selectedFile.getParent();
        if (existsInDirOrSubdirs(new File(directory.getParent().getPath()), fileName, moduleId)) {
            Messages.showMessageDialog("File with the same name or module id already exists.", "Error", Messages.getErrorIcon());
            return null;
        }
        return directory;
    }

    public String pageFrontMatter(String input, String type) {
        String content = "---\n" +
                "title: \"CHANGE ME\"\n" +
                "published: true\n" +
                "morea_id: " + input + "\n" +
                "morea_type: " + type + "\n"+
                "morea_summary: \"CHANGE ME\"\n" +
                "morea_sort_order: \n" +
                "morea_start_date: \n" +
                "morea_labels: \n" +
                "---\n\n" +
                "## \"CHANGE ME\"\n\n" +
                "This is a sample content for the newly creating " + type + ".md file";
        return content;
    }

    public boolean createPage(String input, String type, AnActionEvent e) {
        if (input != null) {
            // Check input name matches MOREA naming convention
            input = toMoreaName(input, type);
            VirtualFile directory = checkDupes(e, input);

            // Only create the page if the directory is not null (no duplicate found)
            if (directory != null) {
                String finalInput = input;
                WriteAction.run(() -> {
                    try {
                        VirtualFile readingFile = directory.createChildData(this, finalInput + ".md");
                        try (OutputStream outputStream = readingFile.getOutputStream(this)) {
                            String content = pageFrontMatter(finalInput, type);
                            outputStream.write(content.getBytes());
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });
                return true;
            }
        }
        return false;
    }

    public void moreaVisibility(AnActionEvent e) {
        VirtualFile selectedFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        if (selectedFile != null) {
            VirtualFile parent = selectedFile.getParent();
            e.getPresentation().setEnabledAndVisible(parent != null && "morea".equals(parent.getName()));
        } else {
            e.getPresentation().setEnabledAndVisible(false);
        }
    }

    public void updateModule (String input, String type, AnActionEvent event) {
        String morea_id = toMoreaName(input, type);
        VirtualFile parentDir = event.getData(PlatformDataKeys.VIRTUAL_FILE);
        parentDir.refresh(false, true);
        String moduleName = "module-" + parentDir.getName() + ".md";

        for (VirtualFile file : parentDir.getChildren()) {
            if (file.getName().equalsIgnoreCase(moduleName)) {
                Yaml moduleFileYaml = new Yaml();
                Yaml repYaml = new Yaml(new CustomRepresenter());
                DumperOptions options = new DumperOptions();

                Document moduleDocument = FileDocumentManager.getInstance().getDocument(file);

                String moduleContent = moduleDocument.getText();

                int moduleFrontMatterEnd = moduleContent.indexOf("---", 3);
                int moduleFrontMatterStart = moduleContent.indexOf("---") + 3;

                String moduleFrontMatter = moduleContent.substring(moduleFrontMatterStart, moduleFrontMatterEnd);
                String moduleRemainingContent = moduleContent.substring(moduleFrontMatterEnd + 3);

                Map<String, Object> moduleFrontMatterData = moduleFileYaml.load(moduleFrontMatter);

                String category = "morea_" + type + "s";

                List<String> list = new ArrayList<>();
                list.add(morea_id);

                moduleFrontMatterData.put(category, list);

                options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
                options.setPrettyFlow(true);

                String updatedYamlFrontMatter = repYaml.dump(moduleFrontMatterData).trim();
                String updatedContent = "---\n" + updatedYamlFrontMatter + "\n---" + moduleRemainingContent;

                ApplicationManager.getApplication().runWriteAction(() -> {
                    moduleDocument.setText(updatedContent);
                });

                break;
            }
        }

    }

}
