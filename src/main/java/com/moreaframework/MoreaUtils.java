package com.moreaframework;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;
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

}
