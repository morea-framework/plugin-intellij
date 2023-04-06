package com.newpageaction;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.WindowManager;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class NewPageAction extends AnAction {
    private static boolean fileExistsInDirectoryOrSubdirectories(File directory, String fileName) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        // recursively check in subdirectories
                        if (fileExistsInDirectoryOrSubdirectories(file, fileName)) {
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

    @Override
    public void actionPerformed(AnActionEvent e) {
        // options a user can choose
        String[] options = {"reading", "outcome", "assessment", "experience"};
        // set them as immutable option
        ComboBox<String> comboBox = new ComboBox<>(options);
        // create a window
        JComponent window = WindowManager.getInstance().getIdeFrame(null).getComponent();
        //constructor for the window
        DialogWrapper dialog = new DialogWrapper(window, true) {
            {
                init();
                setTitle("Select an option");
            }

            @Override
            protected @Nullable JComponent createCenterPanel() {
                return comboBox;
            }

            @Override
            protected void doOKAction() {
                close(DialogWrapper.OK_EXIT_CODE);
            }
        };

        dialog.show();
        // if the user has selected a type, and entered a valid name
        if (dialog.getExitCode() == DialogWrapper.OK_EXIT_CODE) {
            // input1 = type (reading, outcome, experience, assessment)
            String input1 = (String)comboBox.getSelectedItem();
            // input2 name for specific page
            String input2 = Messages.showInputDialog("Enter Morea Page Name:", "New Morea Page", Messages.getQuestionIcon(), "ExamplePage", new InputValidator() {
                @Override
                public boolean checkInput(String input) {
                    return !input.isEmpty();
                }

                @Override
                public boolean canClose(String input) {
                    return checkInput(input);
                }
            });
            // Check if name to create is not null
            if (input2 != null) {
                // Get the selected file in the project view
                VirtualFile selectedFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
                // Check if the selected file is not null
                if (selectedFile == null) {
                    return;
                }

                // Get the directory where the action is performed
                VirtualFile directory = selectedFile.isDirectory() ? selectedFile : selectedFile.getParent();
                // Construct the file name
                String fileName = input1 + "-" + input2 + ".md";
                // Check if the file already exists in the parent directory or any subdirectories
                if (fileExistsInDirectoryOrSubdirectories(new File(directory.getParent().getPath()), fileName)) {
                    Messages.showMessageDialog("File already exists.", "Error", Messages.getErrorIcon());
                    return;
                }

                // this is needed or else you can't perform actions on the plugin test
                WriteAction.run(() -> {
                    try {
                        // create a .md file as input1(type)-input2(name).md
                        VirtualFile newFile = directory.createChildData(this, input1 + "-" + input2 + ".md");
                        // template file for the different types of pages
                        try (OutputStream outputStream = newFile.getOutputStream(this)) {
                            String content = "---\n" +
                                    "title: \"" + input2 + "\"\n" +
                                    "published: true\n" +
                                    "morea_id: " + input1 + "-" + input2 + "\n" +
                                    "morea_type: " + input1 + "\n" +
                                    "morea_summary: example summary\n" +
                                    "morea_sort_order: \n" +
                                    "morea_start_date: \n" +
                                    "morea_labels: \n" +
                                    "---\n\n" +
                                    "## " + input2 + "\n\n" +
                                    "This is a sample content for the newly created .md file.";
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
    }

    @Override
    public void update(AnActionEvent e){
        super.update(e);
        VirtualFile selectedFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        if (selectedFile != null) {
            VirtualFile parent = selectedFile.getParent();
            e.getPresentation().setEnabledAndVisible(parent != null && "morea".equals(parent.getName()));
        } else {
            e.getPresentation().setEnabledAndVisible(false);
        }
    }
}
