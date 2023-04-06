package com.newpageaction;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class UpdateModule extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        // Get the current project
        Project project = e.getProject();
        if (project == null) return;

        // Get the directory that the user is currently right-clicked on
        VirtualFile folder = e.getData(CommonDataKeys.VIRTUAL_FILE);
        if (folder == null || !folder.isDirectory()) return;

        // Get the list of files in the directory
        List<VirtualFile> files = new ArrayList<>();
        folder.refresh(false, true);
        for (VirtualFile file : folder.getChildren()) {
            if (!file.isDirectory() && file.getExtension().equalsIgnoreCase("md")) {
                files.add(file);
            }
        }
        String mainMarkdownFileName ="module" + "-" + folder.getName() + ".md";

        // Create a map to store the updated sections
        Map<String, List<String>> updatedSections = new HashMap<>();
        updatedSections.put("morea_outcomes", new ArrayList<>());
        updatedSections.put("morea_readings", new ArrayList<>());
        updatedSections.put("morea_experiences", new ArrayList<>());
        updatedSections.put("morea_assessments", new ArrayList<>());

// Update the sections based on the morea_id value in the YAML front matter
        Yaml otheryaml = new Yaml();
        for (VirtualFile mdFile : files) {
            if (!mdFile.getName().equals(mainMarkdownFileName)) { // Ignore main markdown file
                String fileContent = null;
                try {
                    fileContent = new String(mdFile.contentsToByteArray());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                String yamlFrontMatter = fileContent.split("---")[1];

                Map<String, Object> yamlMap = otheryaml.load(yamlFrontMatter);
                if (yamlMap.containsKey("morea_id")) {
                    String moreaId = (String) yamlMap.get("morea_id");
                    String type = (String) yamlMap.get("morea_type");
                    System.out.println("Processing file: " + mdFile.getName()); // Debug
                    System.out.println("morea_id: " + moreaId); // Debug
                        String section = "morea_" + type + "s";
                        System.out.println("Section: " + section); // Debug
                        if (updatedSections.containsKey(section)) {
                            updatedSections.get(section).add(moreaId);
                        }
                }
            }
        }
        // Parse the YAML front matter of the main Markdown file
        for (VirtualFile file : files) {
            if (file.getName().equalsIgnoreCase(mainMarkdownFileName)) {
                Document document = FileDocumentManager.getInstance().getDocument(file);
                if (document == null) continue;

                // Parse the YAML front matter using SnakeYAML
                String content = document.getText();
                int frontMatterEnd = content.indexOf("---", 3);
                if (frontMatterEnd < 0) continue;

                int frontMatterStart = content.indexOf("---") + 3;
                String yamlFrontMatter = content.substring(frontMatterStart, frontMatterEnd);
                String remainingContent = content.substring(frontMatterEnd + 3);

                Yaml yaml = new Yaml();
                Map<String, Object> frontMatterData = yaml.load(yamlFrontMatter);

                // Update the YAML front matter with the updated sections
                frontMatterData.putAll(updatedSections);

                // Write the updated YAML front matter back to the file
                DumperOptions options = new DumperOptions();
                options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
                options.setPrettyFlow(true);

                Yaml newYaml = new Yaml(new CustomRepresenter());
                String updatedYamlFrontMatter = newYaml.dump(frontMatterData).trim();
                String updatedContent = "---\n" + updatedYamlFrontMatter + "\n---" + remainingContent;

                ApplicationManager.getApplication().runWriteAction(() -> {
                    document.setText(updatedContent);
                });

                break;
            }
        }
    }
}
