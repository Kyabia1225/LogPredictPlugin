package sup;


import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class Support {
    public static JTable createTable(Object[][] tableData, String[] column) {
        JTable table = new JTable(tableData, column){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int row = table.rowAtPoint(e.getPoint());
                //int col = table.columnAtPoint(e.getPoint());

                String fileName = (String) table.getValueAt(row, 1);
                new OpenFileDescriptor(ProjectManager.getInstance().getOpenProjects()[0],
                        LocalFileSystem.getInstance().findFileByIoFile(new File(fileName))
                        )
                        .navigate(true);


            }
        });
        return table;
    }
}
