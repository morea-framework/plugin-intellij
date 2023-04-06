package com.moreaframework;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;

public class NewOutcomesAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        MoreaUtils morea = new MoreaUtils();
        String type = "outcome";

        String input = Messages.showInputDialog("Enter Outcomes Page Name:", "New Outcomes Page", Messages.getQuestionIcon(), "outcome-example", new InputValidator() {
            @Override
            public boolean checkInput(String inputString) {
                return !inputString.isEmpty();
            }

            @Override
            public boolean canClose(String inputString) {
                return checkInput(inputString);
            }
        });

        // Create the page
        morea.createPage(input, type, e);
    }

    @Override
    public void update(AnActionEvent e) {
        MoreaUtils morea = new MoreaUtils();
        morea.moreaVisibility(e);
    }
}
