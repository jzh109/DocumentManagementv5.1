package cs.whut.frame;

import cs.whut.common.DataProcessing;
import cs.whut.common.PrintMessage;
import cs.whut.common.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Enumeration;

/**
 * Created on 6:19 2020/9/29
 */
public class UserFrame extends JFrame {
    private JPanel userPanel;
    private JTabbedPane addTabbedPane;
    private JTabbedPane UpdateTabbedPane;
    private JTabbedPane deleteTabbedPane;
    private JPanel addPanel;
    private JPanel editPane;
    private JPanel deletePanel;
    private JTextField nameTextFieldInAdd;
    private JPasswordField passwordFieldInAdd;
    private JComboBox comboBoxInAdd;
    private JButton confirmButtonInAdd;
    private JButton cancelButtonInAdd;
    private JComboBox<String> nameComboBoxInUpdate;
    private JPasswordField passwordFieldInUpdate;
    private JComboBox comboBoxInUpdate;
    private JTable tableInDelete;
    private JScrollPane scrollPaneInDelete;
    private JButton confirmButtonInUpdate;
    private JButton cancelButtonInUpdate;
    private JButton deleteButtonInDelete;
    private JButton cancelButtonInDelete;
    static JFrame frame;
    private User thisUser;

    public UserFrame(String name, String pane) {
        SetPane(pane);
        if ((thisUser = DataProcessing.searchUser(name)) == null) {
            JOptionPane.showMessageDialog(null, "User doesn't exist.");
            return;
        }
        confirmButtonInAdd.addActionListener(e -> {
            try {
                addActionPerformed();
            } catch (IOException | ClassNotFoundException ex) {
                PrintMessage.print(ex.getLocalizedMessage());
            }
        });
        cancelButtonInAdd.addActionListener(this::cancelButtonActionPerformed);
        confirmButtonInUpdate.addActionListener(e -> {
            try {
                updateActionPerformed();
            } catch (IOException | ClassNotFoundException ex) {
                PrintMessage.print(ex.getLocalizedMessage());
            }
        });
        cancelButtonInUpdate.addActionListener(this::cancelButtonActionPerformed);
        deleteButtonInDelete.addActionListener(e -> deleteActionPerformed());
        cancelButtonInDelete.addActionListener(this::cancelButtonActionPerformed);
        constructUserTable();
        addUserToCombobox();
    }

    private void deleteActionPerformed() {
        int selectedRow = tableInDelete.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "No User has been selected!");
        } else {
            String username = (String) tableInDelete.getValueAt(selectedRow, 0);
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(null, "You choose a blink line.");
                return;
            }
            if (username.equals(thisUser.getName())) {
                JOptionPane.showMessageDialog(null, "You cannot delete yourself.");
                return;
            }
            int value = JOptionPane.showConfirmDialog(null, "Are you sure to delete this user?", "Confirm", JOptionPane.OK_CANCEL_OPTION);
            if (value == 0) {
                if (DataProcessing.delete(username)) {
                    constructUserTable();
                    addUserToCombobox();
                    JOptionPane.showMessageDialog(null, "Delete succeed.");
                } else {
                    JOptionPane.showMessageDialog(null, "Delete failed.");
                }
            } else if (value == 1) {
                JOptionPane.showMessageDialog(null, "Nothing changed.");
            }
        }
    }

    private void addUserToCombobox() {
        nameComboBoxInUpdate.removeAllItems();
        Enumeration<User> users = DataProcessing.getAllUser();
        while(users.hasMoreElements()){
            User user = users.nextElement();
            nameComboBoxInUpdate.addItem(user.getName());
        }
    }

    private void updateActionPerformed() throws IOException, ClassNotFoundException {
        String name = (String) nameComboBoxInUpdate.getSelectedItem();
        String password = new String(passwordFieldInUpdate.getPassword());
        String role = (String) comboBoxInUpdate.getSelectedItem();
        if (DataProcessing.update(name, password, role)) {
            constructUserTable();
            addUserToCombobox();
            passwordFieldInUpdate.setText("");
            JOptionPane.showMessageDialog(null, "Password has been changed successfully!");
        } else {
            JOptionPane.showMessageDialog(null, "User doesn't exist.");
        }
    }

    private void addActionPerformed() throws IOException, ClassNotFoundException {
        String name = nameTextFieldInAdd.getText();
        String password = new String(passwordFieldInAdd.getPassword());
        String role = (String) comboBoxInAdd.getSelectedItem();
        if (name.trim().isEmpty()) {
            frame.dispose();
            JOptionPane.showMessageDialog(null, "User name cannot be empty.");
            UserFrame.main(null);
        }
        if (DataProcessing.insert(name, password, role)) {
            constructUserTable();
            JOptionPane.showMessageDialog(null, "Add user successfully.");
            nameTextFieldInAdd.setText("");
            passwordFieldInAdd.setText("");
        } else {
            nameTextFieldInAdd.setText("");
            passwordFieldInAdd.setText("");
            JOptionPane.showMessageDialog(null, "User has existed.");
        }
        addUserToCombobox();
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        frame.dispose();
    }

    private void constructUserTable() {
        String[] header = {"Name", "Password", "Role"};
        String[][] usersData = new String[20][3];
        Enumeration<User> users = DataProcessing.getAllUser();
        int row = 0;
        while (users.hasMoreElements()) {
            User user = users.nextElement();
            usersData[row][0] = user.getName();
            usersData[row][1] = user.getPassword();
            usersData[row][2] = user.getRole();
            row++;
        }
        tableInDelete.setModel(new DefaultTableModel(usersData, header) {
            boolean[] columnEditable = new boolean[]{false, false, false};
            public boolean isCellEditable(int row, int column) {
                return columnEditable[column];
            }
        });
        tableInDelete.getTableHeader().setReorderingAllowed(false);
        scrollPaneInDelete.setViewportView(tableInDelete);
    }

    private void SetPane(String value) {
        switch (value) {
            case "0":
                addTabbedPane.setSelectedComponent(addPanel);
                break;
            case "1":
                addTabbedPane.setSelectedComponent(UpdateTabbedPane);
                break;
            case "2":
                addTabbedPane.setSelectedComponent(deleteTabbedPane);
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                String name = args[1];
                String pane = args[2];
                frame = new JFrame("UserFrame");
                frame.setContentPane(new UserFrame(name, pane).userPanel);
                frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                frame.setSize(500, 480);
                setToCenter(frame);
                frame.setVisible(true);
            } catch (Exception e) {
                frame.dispose();
                JOptionPane.showMessageDialog(null, e.getLocalizedMessage());
            }
        });
    }

    static void setToCenter(JFrame frame) {
        Toolkit toolkit = frame.getToolkit();
        Dimension dimension = toolkit.getScreenSize();
        int screenHeight = dimension.height;
        int screenWidth = dimension.width;
        int frmHeight = frame.getHeight();
        int frmWidth = frame.getWidth();
        frame.setLocation((screenWidth - frmWidth) / 2, (screenHeight - frmHeight) / 2);
    }
}
