package sup;

import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;

public class FocusDialogWrapper extends DialogWrapper {

    public FocusDialogWrapper() {
        super(true); // use current window as parent
        init();
        setTitle("From LogPredictPlugin");
    }

    @NotNull
    @Override
    protected Action[] createActions() {
        return new Action[]{this.getOKAction()};
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("请聚焦一个Java文件");
        label.setPreferredSize(new Dimension(100, 100));
        dialogPanel.add(label, BorderLayout.CENTER);

        return dialogPanel;
    }
}