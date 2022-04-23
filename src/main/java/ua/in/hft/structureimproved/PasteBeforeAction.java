package ua.in.hft.structureimproved;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;

public class PasteBeforeAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        TextRange range = e.getData(CommonDataKeys.PSI_ELEMENT).getTextRange();
        StorageService storage = ApplicationManager.getApplication()
                .getService(StorageService.class);
        storage.paste(e, range.getStartOffset(),"", "\n\n");
    }
}
