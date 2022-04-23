package ua.in.hft.structureimproved;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;

import javax.annotation.PostConstruct;

public class CutAction extends AnAction {
    @PostConstruct
    public void init(){

    }
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        StorageService storage = ApplicationManager.getApplication()
                .getService(StorageService.class);
        storage.cut(e);
    }
}
