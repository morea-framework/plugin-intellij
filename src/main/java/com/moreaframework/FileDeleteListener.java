package com.moreaframework;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.ex.AnActionListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.util.List;
import java.util.Map;


public class FileDeleteListener implements AnActionListener {
    @Override
    public void beforeActionPerformed(@NotNull AnAction action, @NotNull AnActionEvent event) {

        if (ActionManager.getInstance().getId(action).equals(IdeActions.ACTION_DELETE)) {
            // Get deleted file(s) and parent directory
            VirtualFile[] deletedFiles = event.getData(PlatformDataKeys.VIRTUAL_FILE_ARRAY);
            VirtualFile parentDir = deletedFiles[0].getParent();
            parentDir.refresh(false, true);
            String moduleName = "module-" + parentDir.getName() + ".md";

            // iterate through each deleted file
            for (VirtualFile deletedFile : deletedFiles) {
                // search for module file
                for (VirtualFile file : parentDir.getChildren()) {
                    if (file.getName().equalsIgnoreCase(moduleName)) {
                        Yaml deletedFileYaml = new Yaml();
                        Yaml moduleFileYaml = new Yaml();
                        Yaml newYaml = new Yaml(new CustomRepresenter());
                        DumperOptions options = new DumperOptions();

                        Document deletedDocument = FileDocumentManager.getInstance().getDocument(deletedFile);
                        Document moduleDocument = FileDocumentManager.getInstance().getDocument(file);

                        String deletedContent = deletedDocument.getText();
                        String moduleContent = moduleDocument.getText();

                        int moduleFrontMatterEnd = moduleContent.indexOf("---", 3);
                        int moduleFrontMatterStart = moduleContent.indexOf("---") + 3;

                        int deletedFrontMatterEnd = deletedContent.indexOf("---", 3);
                        int deletedFrontMatterStart = deletedContent.indexOf("---") + 3;

                        String deletedFrontMatter = deletedContent.substring(deletedFrontMatterStart, deletedFrontMatterEnd);

                        String moduleFrontMatter = moduleContent.substring(moduleFrontMatterStart, moduleFrontMatterEnd);
                        String moduleRemainingContent = moduleContent.substring(moduleFrontMatterEnd + 3);

                        Map<String, Object> deletedFrontMatterData = deletedFileYaml.load(deletedFrontMatter);
                        Object deletedId = deletedFrontMatterData.get("morea_id");

                        String deletedID = deletedId.toString();
                        int typePos = deletedID.indexOf("-");
                        String type = deletedID.substring(0,typePos);
                        type = "morea_" + type + "s";


                        Map<String, Object> moduleFrontMatterData = moduleFileYaml.load(moduleFrontMatter);
                        List<Map<String, Object>> moreaType = (List<Map<String, Object>>) moduleFrontMatterData.get(type);

                        moreaType.remove(deletedId);

                        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
                        options.setPrettyFlow(true);

                        String updatedYamlFrontMatter = newYaml.dump(moduleFrontMatterData).trim();
                        String updatedContent = "---\n" + updatedYamlFrontMatter + "\n---" + moduleRemainingContent;

                        ApplicationManager.getApplication().runWriteAction(() -> {
                            moduleDocument.setText(updatedContent);
                        });

                        break;
                    }
                }
            }
        }
    }
}
