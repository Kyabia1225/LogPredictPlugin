package action;

import Proc.PredictResult;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import sup.FocusDialogWrapper;
import sup.MyDialogWrapper;
import org.jetbrains.annotations.NotNull;
import sup.Support;
import util.Util;

import javax.swing.*;
import java.util.List;


public class AnalyseCurrentOpenFile extends AnAction {
    private JLabel buttonLabel;
    private JLabel loadingLabel;
    private JPanel myToolWindowContent;
    private JScrollPane tablePane;
    private List<PredictResult> prList;
    @Override
    public void actionPerformed(AnActionEvent e) {

        Project project = e.getProject();
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if(editor == null) {
            new FocusDialogWrapper().show();
            return;
        }
        Document currentDoc = editor.getDocument();
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(currentDoc);
        VirtualFile virtualFile = psiFile.getOriginalFile().getVirtualFile();
        String code_path = virtualFile.getPath();

        ToolWindow myWindow = ToolWindowManager.getInstance(project).getToolWindow("Log predict window");
        Content content = myWindow.getContentManager().getContent(0);
        if(content != null && content.getComponent() instanceof JPanel) {
            myToolWindowContent = (JPanel) content.getComponent();
            buttonLabel = (JLabel) content.getComponent().getComponent(0);
            loadingLabel = (JLabel) content.getComponent().getComponent(3);
            try {
                tablePane = (JScrollPane) content.getComponent().getComponent(4);
            } catch (ArrayIndexOutOfBoundsException ex) {
                tablePane = null;
            }

        } else {
            return;
        }

        if(loadingLabel.isVisible()) {
            new MyDialogWrapper().show();
            return;
        }
        buttonLabel.setText("");
        if(tablePane != null) {
            myToolWindowContent.remove(tablePane);
            tablePane = null;
            if(prList!=null)
                prList.clear();
            prList = null;
            myToolWindowContent.updateUI();
        }
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Predict") {
            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {
                prList = Util.analyseOpenFile(code_path);
                if(prList == null || prList.isEmpty()) {
                    buttonLabel.setText("请仔细检查选择的目录或python依赖");
                    loadingLabel.setVisible(false);
                    return;
                }
                Object[][] tableData = new Object[prList.size()][3];
                for(int i = 0;i<prList.size();i++) {
                    tableData[i][0] = prList.get(i).getMethodName();
                    tableData[i][1] = prList.get(i).getFileName();
                    tableData[i][2] = prList.get(i).getProbability();
                }
                String[] column = {"Method", "File", "Probability"};
                JTable table = Support.createTable(tableData, column);
                tablePane = new JBScrollPane(table);
                myToolWindowContent.add(tablePane);
                loadingLabel.setVisible(false);
                myToolWindowContent.updateUI();
            }
        });
        loadingLabel.setVisible(true);
    }
}
