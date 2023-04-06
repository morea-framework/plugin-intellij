package com.moreaframework;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;

public class MoreaUtils {
    public static boolean existsInDirOrSubdirs(File directory, String fileName) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        // recursively check in subdirectories
                        if (existsInDirOrSubdirs(file, fileName)) {
                            return true;
                        }
                    } else if (file.getName().equals(fileName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public String toMoreaName(String input, String type) {
        input = input.toLowerCase(Locale.ROOT);
        if (!input.startsWith(type)) {
            input = type + "-" + input;
        }
        return input;
    }

    public VirtualFile checkDupes(AnActionEvent e, String input) {
        VirtualFile selectedFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);

        String fileName = input + ".md";

        VirtualFile directory = selectedFile.isDirectory() ? selectedFile : selectedFile.getParent();
        if (existsInDirOrSubdirs(new File(directory.getParent().getPath()), fileName)) {
            Messages.showMessageDialog("File already exists.", "Error", Messages.getErrorIcon());
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

    public void createPage(String input, String type, AnActionEvent e) {
        if (input != null) {
            // Check input name matches MOREA naming convention
            input = toMoreaName(input, type);
            VirtualFile directory = checkDupes(e, input);

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
        }
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

}
