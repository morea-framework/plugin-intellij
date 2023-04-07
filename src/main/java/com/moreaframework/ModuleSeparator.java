package com.moreaframework;

import com.esotericsoftware.kryo.NotNull;
import com.intellij.openapi.actionSystem.*;

public class ModuleSeparator extends DefaultActionGroup {
    @Override
    public void update(AnActionEvent e) {
        removeAll();
        addSeparator();
        addAction(ActionManager.getInstance().getAction("NewModuleAction"));
    }
}
