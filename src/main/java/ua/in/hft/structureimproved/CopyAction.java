package ua.in.hft.structureimproved;

import com.intellij.ide.structureView.StructureView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.PostConstruct;

public class CopyAction extends AnAction {
    @PostConstruct
    public void init(){

    }
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        StorageService storage = ApplicationManager.getApplication()
                .getService(StorageService.class);
        storage.copy(e);
    }
}
