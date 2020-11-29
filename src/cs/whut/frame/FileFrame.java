package cs.whut.frame;

import cs.whut.common.DataProcessing;
import cs.whut.common.Doc;
import cs.whut.common.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.sql.SQLException;
import java.util.Enumeration;

/**
 * Created on 14:43 2020/9/29
 */
public class FileFrame extends JFrame {
    private JTabbedPane downloadTabbedPane;
    private JTabbedPane uploadTabbedPane;
    private JPanel fileFrame;
    private JPanel downloadPanel;
    private JPanel uploadPanel;
    private JTable downloadTable;
    private JScrollPane downloadScrollPane;
    private User user;
    private JButton downloadButton;
    private JButton cancelButton;
    private JLabel idPanel;
    private JTextField idTextField;
    private JTextField descriptionTextField;
    private JTextField pathTextField;
    private JButton browseButton;
    private JButton confirmButton;
    private JButton cancelButton1;
    static JFrame frame;

    public FileFrame(String name, String pane) {
        if ((user = DataProcessing.searchUser(name)) == null) {
            JOptionPane.showMessageDialog(null, "User doesn't exist.");
            return;
        }

        SetPane(pane);

        downloadButton.addActionListener(e -> {
            try {
                downloadActionPerformed();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
        cancelButton.addActionListener(e -> frame.dispose());

        constructTable();
        confirmButton.addActionListener(e -> {
            try {
                uploadActionPerformed();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        cancelButton1.addActionListener(e -> frame.dispose());
        browseButton.addActionListener(e -> {
            String path = getOpenPath();
            pathTextField.setText(path);
        });

        if (!user.getRole().equalsIgnoreCase("operator")) {
            idTextField.setEditable(false);
            idTextField.setEnabled(false);
            pathTextField.setEditable(false);
            pathTextField.setEnabled(false);
            descriptionTextField.setEnabled(false);
            descriptionTextField.setEditable(false);
            browseButton.setEnabled(false);
            confirmButton.setEnabled(false);
        }
    }

    private void SetPane(String value) {
        if (value.equals("0")) {
            downloadTabbedPane.setSelectedComponent(downloadPanel);
        } else {
            downloadTabbedPane.setSelectedComponent(uploadTabbedPane);
        }
    }

    private String getSavePath(String filename) {
        FileDialog fileDialog = new FileDialog(this, "Choose path");
        fileDialog.setFile(filename);
        fileDialog.setMode(FileDialog.SAVE);
        fileDialog.setVisible(true);
        return fileDialog.getDirectory();
    }

    private String getOpenPath(){
        FileDialog fileDialog = new FileDialog(this, "Choose path");
        fileDialog.setMode(FileDialog.LOAD);
        fileDialog.setVisible(true);
        return fileDialog.getDirectory() + fileDialog.getFile();
    }

    private void uploadActionPerformed() {
        String path = pathTextField.getText();
        String id = idTextField.getText();
        String description = descriptionTextField.getText();
        if(path.equals("")){
            JOptionPane.showMessageDialog(null,"No file has been selected.");
            return;
        }
        if(DataProcessing.uploadFile(path,user.getName(),id,description)){
            idTextField.setText("");
            pathTextField.setText("");
            descriptionTextField.setText("");
            JOptionPane.showMessageDialog(null, "Upload successfully.");
        } else {
            JOptionPane.showMessageDialog(null, "Upload failed.");
        }
        constructTable();
    }

    private void downloadActionPerformed() throws SQLException {
        int selectedRow = downloadTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "No file has been selected.");
        } else {
            String fileId = (String) downloadTable.getValueAt(selectedRow, 0);
            if (fileId.isEmpty()) {
                JOptionPane.showMessageDialog(null, "You choose a blink line.");
                return;
            }
            int value = JOptionPane.showConfirmDialog(null, "Are you sure to download this file?", "Confirm", JOptionPane.OK_CANCEL_OPTION);
            if (value == 0) {
                String path = this.getSavePath(DataProcessing.searchDoc(fileId).getFilename());
                if (path == null) {
                    return;
                }
                if (DataProcessing.downloadFile(fileId, path)) {
                    constructTable();
                    JOptionPane.showMessageDialog(null, "Download succeed.");
                } else {
                    JOptionPane.showMessageDialog(null, "File has existed.\nDownload failed.");
                }
            }
        }
    }

    private void constructTable() {
        String[] header = {"Document ID", "Creator", "Time", "Document name", "Description"};
        String[][] files = new String[50][5];

        Enumeration<Doc> docs = DataProcessing.getAllDocs();
        int row = 0;
        while (docs.hasMoreElements()) {
            Doc doc = docs.nextElement();
            files[row][0] = doc.getID();
            files[row][1] = doc.getCreator();
            files[row][2] = doc.getTimestamp().toString();
            files[row][3] = doc.getFilename();
            files[row][4] = doc.getDescription();
            row++;
        }

        downloadTable.setModel(new DefaultTableModel(files, header) {
            boolean[] columnEditable = new boolean[]{false, false, false, false, false};

            public boolean isCellEditable(int row, int column) {
                return columnEditable[column];
            }
        });
        downloadTable.getTableHeader().setReorderingAllowed(false);
        downloadScrollPane.setViewportView(downloadTable);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                frame = new JFrame("FileFrame");
                frame.setContentPane(new FileFrame(args[1], args[2]).fileFrame);
                frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}