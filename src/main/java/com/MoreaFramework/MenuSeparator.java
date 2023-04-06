package com.MoreaFramework;

import com.intellij.openapi.actionSystem.*;
import org.jetbrains.annotations.NotNull;

public class MenuSeparator extends DefaultActionGroup {
    @Override
    public void update(@NotNull AnActionEvent e) {
        removeAll();
        addAction(ActionManager.getInstance().getAction("NewOutcomesAction"));
        addAction(ActionManager.getInstance().getAction("NewReadingsAction"));
        addAction(ActionManager.getInstance().getAction("NewExperiencesAction"));
        addAction(ActionManager.getInstance().getAction("NewAssessmentsAction"));
        addSeparator();
    }
}