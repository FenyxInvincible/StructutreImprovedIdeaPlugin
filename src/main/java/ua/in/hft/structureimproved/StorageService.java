package ua.in.hft.structureimproved;

import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiEditorUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Arrays;

@Service
public final class StorageService {

    /**
     * Copy method section
     * @param e
     */
    public void copy(@NotNull AnActionEvent e) {
        @Nullable PsiElement psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);
        TextRange range = getRange(e, psiElement.getTextRange().getStartOffset());

        Editor editor = PsiEditorUtil.findEditor(e.getData(CommonDataKeys.PSI_ELEMENT));
        @NotNull Document document = editor.getDocument();
        int lineStartOffset = document.getLineStartOffset(document.getLineNumber(range.getStartOffset()));

        editor.getSelectionModel().setSelection(lineStartOffset, range.getEndOffset());
        copyToClipboard(editor.getSelectionModel().getSelectedText());
    }

    /**
     * Returns range for selected method
     * @param e
     * @param offset
     * @return
     */
    private TextRange getRange(@NotNull AnActionEvent e, int offset) {
        @Nullable PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);

        if (isInstance(psiFile.getLanguage().getClass(), Constants.CppLanguage)) {
            return findCppRange(psiFile, offset);
        } else if (isInstance(psiFile.getLanguage().getClass(), Constants.CSharpLanguage)) {
            return findCSharpRange(psiFile, offset);
        } else {
            //findDefaultRange(psiFile, offset);
            return e.getData(CommonDataKeys.PSI_ELEMENT).getTextRange();
        }
    }

    public void cut(@NotNull AnActionEvent e) {
        Editor editor = FileEditorManager.getInstance(e.getProject()).getSelectedTextEditor();

        copy(e);

        WriteCommandAction.runWriteCommandAction(e.getProject(), () ->
                editor.getDocument().replaceString(
                        editor.getSelectionModel().getSelectionStart(),
                        editor.getSelectionModel().getSelectionEnd(),
                        ""
                )
        );
    }

    public void paste(@NotNull AnActionEvent e, int position, String prefix, String suffix) {

        Editor editor = PsiEditorUtil.findEditor(e.getData(CommonDataKeys.PSI_ELEMENT));
        @NotNull Document document = editor.getDocument();

        int insertPlace = !suffix.isEmpty() ?
                document.getLineStartOffset(document.getLineNumber(position)) :
                getRange(e, position).getEndOffset();

        WriteCommandAction.runWriteCommandAction(e.getProject(), () ->
                editor.getDocument().insertString(
                        insertPlace,
                        prefix + obtainFromClipboard() + suffix)
        );
    }

    /**
     * Default implementation for Idea and similar
     * @param psiFile
     * @param textOffset
     * @return
     */
    private TextRange findDefaultRange(PsiFile psiFile, int textOffset) {
        @Nullable PsiElement psiElem = psiFile.findElementAt(textOffset);

        if (psiElem != null) {
            while (psiElem.getParent() != null) {
                if (isInstance(psiElem.getParent().getClass(), "com.intellij.psi.PsiMethod")) {
                    return psiElem.getParent().getTextRange();
                }
                psiElem = psiElem.getParent();
            }
        }

        return TextRange.create(0, 0);
    }

    /**
     * Implementation for Rider and C++
     * @param psiFile
     * @param textOffset
     * @return
     */
    private TextRange findCppRange(PsiFile psiFile, int textOffset) {
        @Nullable PsiElement base = psiFile.findElementAt(textOffset).getParent();
        TextRange range = base.getTextRange();

        while (base.getNextSibling() != null) {
            PsiElement sibling = base.getNextSibling();
            if (isInstance(sibling.getClass(), Constants.CppBlock)) {
                range = TextRange.create(range.getStartOffset(), sibling.getTextRange().getEndOffset());
                break;
            }
            if (isInstance(sibling.getClass(), Constants.CppCompositeElement)) {
                break;
            }
            range = TextRange.create(range.getStartOffset(), sibling.getTextRange().getEndOffset());
            base = sibling;
        }

        return range;
    }

    /**
     * Implementation for Rider and C++
     * @param psiFile
     * @param textOffset
     * @return
     */
    private TextRange findCSharpRange(PsiFile psiFile, int textOffset) {
        @Nullable PsiElement base = psiFile.findElementAt(textOffset).getParent();
        TextRange range = base.getTextRange();

        while (base.getNextSibling() != null) {
            PsiElement sibling = base.getNextSibling();
            if (isInstance(sibling.getClass(), Constants.CSharpBlock)) {
                range = TextRange.create(range.getStartOffset(), sibling.getTextRange().getEndOffset());
                break;
            }
            if (isInstance(sibling.getClass(), Constants.CSharpCompositeElement)) {
                break;
            }
            range = TextRange.create(range.getStartOffset(), sibling.getTextRange().getEndOffset());
            base = sibling;
        }

        return range;
    }

    /**
     * Dirty hack, because I want to build same lib for Rider and Idea,
     * but Idea (2022 and below at least) doesn't have Cpp lang
     * @param aClass
     * @param className
     * @return
     */
    private boolean isInstance(Class<?> aClass, String className) {
        if(aClass.getSuperclass() == null) {
            return false;
        }
        return aClass.getName().equals(className)
                || Arrays.stream(aClass.getInterfaces()).anyMatch(i -> i.getName().equals(className))
                || isInstance(aClass.getSuperclass(), className
        );
    }

    private void copyToClipboard(String selectedText) {
        Toolkit.getDefaultToolkit().getSystemClipboard()
                .setContents(new StringSelection(selectedText), null);
    }

    private String obtainFromClipboard() {
        try {
            return Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor).toString();
        } catch (UnsupportedFlavorException | IOException e) {
            return "";
        }
    }
}
