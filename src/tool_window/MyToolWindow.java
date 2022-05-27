package tool_window;

import sup.MyDialogWrapper;
import Proc.PredictResult;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;
import sup.Support;
import util.Util;

import javax.swing.*;
import java.util.List;

public class MyToolWindow {

    private JButton hideButton;

    private JLabel buttonLabel;

    private JLabel loadingLabel;

    private JPanel myToolWindowContent;

    private JButton analyseButton;

    private JScrollPane tablePane;

    private List<PredictResult> prList;

    public MyToolWindow(ToolWindow toolWindow) {

        init();

        hideButton.addActionListener(e -> toolWindow.hide(null));

        analyseButton.addActionListener(e -> {
            if(loadingLabel.isVisible()) {
                new MyDialogWrapper().show();
                return;
            }
            FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(false, true, false, false, false, false);
            fileChooserDescriptor.setShowFileSystemRoots(true);
            fileChooserDescriptor.setDescription("Select project directory");
            fileChooserDescriptor.setTitle("Select Your Project Directory");
            VirtualFile virtualFile = FileChooser.chooseFile(
                    fileChooserDescriptor,
                    null,
                    null
            );
            if(virtualFile == null) {
                //选择了cancel
                return;
            }
            try {
                tablePane = (JScrollPane) myToolWindowContent.getComponent(4);
            } catch (ArrayIndexOutOfBoundsException ignored) {
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
            ProgressManager.getInstance().run(new Task.Backgroundable(
                   null, "Predict") {
                @Override
                public void run(@NotNull ProgressIndicator progressIndicator) {
                    String project_path = virtualFile.getPath();
                    prList = Util.analyseProjectFiles(project_path);
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

        });
    }

    private void init() {
        buttonLabel = new JLabel();
        ImageIcon loading = new ImageIcon("D:\\Projects\\LogPredictPlugin\\resources\\loading.gif",
                "Loading");
        loadingLabel = new JLabel(loading);
        //loadingLabel.setPreferredSize(new Dimension(50, 50));
        loadingLabel.setVisible(false);
        hideButton = new JButton("最小化");
        analyseButton = new JButton("选择目录开始分析");
        myToolWindowContent = new JPanel();
        myToolWindowContent.add(buttonLabel);
        myToolWindowContent.add(analyseButton);
        myToolWindowContent.add(hideButton);
        myToolWindowContent.add(loadingLabel);

    }

    public JPanel getContent() {
        return myToolWindowContent;
    }

}

