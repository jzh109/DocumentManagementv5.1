package cs.whut.frame;

import cs.whut.common.Administrator;
import cs.whut.common.DataProcessing;
import cs.whut.common.PrintMessage;
import cs.whut.common.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.sql.Timestamp;

public class MainFrame extends JFrame {
    private JMenuItem addUserMenuItem;
    private JMenuItem deleteUserMenuItem;
    private JMenuItem updateUserMenuItem;
    private JMenuItem uploadFileMenuItem;
    private JMenuItem downloadFileMenuItem;
    private JMenuItem changeSelfInfoMenuItem;
    private JMenuItem exitMenuItem;
    private User user;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                MainFrame frame = new MainFrame(new Administrator("Kate", "123", "administrator"));
                frame.setVisible(true);
            } catch (Exception e) {
                PrintMessage.print(e.getLocalizedMessage());
                JOptionPane.showMessageDialog(null, e.getLocalizedMessage());
            }
        });
    }

    public MainFrame(User user) {
        this.user = user;
        setResizable(false);
        this.setFrameTitle(user.getRole());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(400, 300, 700, 400);

        JPanel contentPanel = new JPanel();
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPanel);
        contentPanel.setLayout(null);

        JMenuBar menuBar = new JMenuBar();
        menuBar.setBounds(0, 0, 1165, 33);
        contentPanel.add(menuBar);

        JLabel infoLabel = new JLabel();
        infoLabel.setText("Please exit from \"Other\" \"exit\" or your data might get lost.");
        infoLabel.setVisible(true);
        infoLabel.setLocation(50, 100);
        infoLabel.setSize(new Dimension(1000, 80));
        infoLabel.setFont(new Font("Consolas", Font.PLAIN, 18));
        contentPanel.add(infoLabel);

        JMenu userManagerMenu = new JMenu("User Manage");
        userManagerMenu.setFont(new Font("Consolas", Font.PLAIN, 18));
        menuBar.add(userManagerMenu);

        addUserMenuItem = new JMenuItem("Add");
        addUserMenuItem.addActionListener(e -> addUserActionPerformed(user));
        addUserMenuItem.setFont(new Font("Consolas", Font.PLAIN, 16));
        userManagerMenu.add(addUserMenuItem);

        updateUserMenuItem = new JMenuItem("Update");
        updateUserMenuItem.addActionListener(e -> updateUserActionPerformed(user));
        updateUserMenuItem.setFont(new Font("Consolas", Font.PLAIN, 16));
        userManagerMenu.add(updateUserMenuItem);

        deleteUserMenuItem = new JMenuItem("Delete");
        deleteUserMenuItem.addActionListener(e -> delUserActionPerformed(user));
        deleteUserMenuItem.setFont(new Font("Consolas", Font.PLAIN, 16));
        userManagerMenu.add(deleteUserMenuItem);

        JMenu fileManageMenu = new JMenu("File Manager");
        fileManageMenu.setFont(new Font("Consolas", Font.PLAIN, 18));
        menuBar.add(fileManageMenu);

        downloadFileMenuItem = new JMenuItem("Download");
        downloadFileMenuItem.addActionListener(e -> downloadFileActionPerformed(user));
        downloadFileMenuItem.setFont(new Font("Consolas", Font.PLAIN, 16));
        fileManageMenu.add(downloadFileMenuItem);

        uploadFileMenuItem = new JMenuItem("Upload");
        uploadFileMenuItem.addActionListener(e -> uploadFileActionPerformed(user));
        uploadFileMenuItem.setFont(new Font("Consolas", Font.PLAIN, 16));
        fileManageMenu.add(uploadFileMenuItem);

        JMenu selfInfoMenu = new JMenu("Self information");
        selfInfoMenu.setFont(new Font("Consolas", Font.PLAIN, 18));
        menuBar.add(selfInfoMenu);

        changeSelfInfoMenuItem = new JMenuItem("Change Password");
        changeSelfInfoMenuItem.addActionListener(e -> changeSelfActionPerformed(user));
        changeSelfInfoMenuItem.setFont(new Font("Consolas", Font.PLAIN, 16));
        selfInfoMenu.add(changeSelfInfoMenuItem);

        JMenu otherMenu = new JMenu("Other");
        otherMenu.setFont(new Font("Consolas", Font.PLAIN, 18));
        menuBar.add(otherMenu);

        exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(e -> {
            try {
                exitActionPerformed();
            } catch (IOException ex) {
                PrintMessage.print(ex.getLocalizedMessage());
                JOptionPane.showMessageDialog(null, ex.getLocalizedMessage());
            }
        });
        exitMenuItem.setFont(new Font("Consolas", Font.PLAIN, 16));
        otherMenu.add(exitMenuItem);

        JMenuItem logoffMenuItem = new JMenuItem("Log out");
        logoffMenuItem.addActionListener(e -> logoutActionPerformed());
        logoffMenuItem.setFont(new Font("Consolas", Font.PLAIN, 16));
        otherMenu.add(logoffMenuItem);

        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                DataProcessing.disconnectFromDatabase();
            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });

        setRights(user.getRole());
    }

    private void logoutActionPerformed() {
        this.dispose();
        PrintMessage.print(user.getName() + " logout.");
        LoginFrame.main(null);
    }

    private void addUserActionPerformed(User user) {
        PrintMessage.print(this.user.getName() + " adds user.");
        String[] name = new String[3];
        name[1] = user.getName();
        name[2] = "0";
        UserFrame.main(name);
    }

    private void delUserActionPerformed(User user) {
        PrintMessage.print(this.user.getName() + " deletes user.");
        String[] name = new String[3];
        name[1] = user.getName();
        name[2] = "2";
        UserFrame.main(name);
    }

    private void updateUserActionPerformed(User user) {
        PrintMessage.print(this.user.getName() + " update user information.");
        String[] name = new String[3];
        name[1] = user.getName();
        name[2] = "1";
        UserFrame.main(name);
    }

    private void uploadFileActionPerformed(User user) {
        PrintMessage.print(this.user.getName() + "upload file.");
        String[] name = new String[3];
        name[1] = user.getName();
        name[2] = "1";
        FileFrame.main(name);
    }

    private void downloadFileActionPerformed(User user) {
        PrintMessage.print(this.user.getName() + " download file.");
        String[] name = new String[3];
        name[1] = user.getName();
        name[2] = "0";
        FileFrame.main(name);
    }

    private void changeSelfActionPerformed(User user) {
        PrintMessage.print(user.getName() + " change self password.");
        String[] name = new String[3];
        name[1] = user.getName();
        SelfFrame.main(name);
    }

    private void setFrameTitle(String role) {
        if (role.equalsIgnoreCase("administrator")) {
            setFrameTitle("Administrator " + user.getName() + " Pane");
        } else if (role.equalsIgnoreCase("browser")) {
            setFrameTitle("Browser " + user.getName() + " Pane");
        } else if (role.equalsIgnoreCase("operator")) {
            setFrameTitle("Operator " + user.getName() + " Pane");
        }
    }

    private void setRights(String role) {
        if (role.equalsIgnoreCase("administrator")) {
            System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\t" + "User is administrator.");
            addUserMenuItem.setEnabled(true);
            deleteUserMenuItem.setEnabled(true);
            updateUserMenuItem.setEnabled(true);
            downloadFileMenuItem.setEnabled(true);
            uploadFileMenuItem.setEnabled(false);
            changeSelfInfoMenuItem.setEnabled(true);
            exitMenuItem.setEnabled(true);
        } else if (role.equalsIgnoreCase("browser")) {
            System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\t" + "User is browser.");
            addUserMenuItem.setEnabled(false);
            deleteUserMenuItem.setEnabled(false);
            updateUserMenuItem.setEnabled(false);
            downloadFileMenuItem.setEnabled(true);
            uploadFileMenuItem.setEnabled(false);
            changeSelfInfoMenuItem.setEnabled(true);
            exitMenuItem.setEnabled(true);
        } else if (role.equalsIgnoreCase("operator")) {
            System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\t" + "User is operator.");
            addUserMenuItem.setEnabled(false);
            deleteUserMenuItem.setEnabled(false);
            updateUserMenuItem.setEnabled(false);
            downloadFileMenuItem.setEnabled(true);
            uploadFileMenuItem.setEnabled(true);
            changeSelfInfoMenuItem.setEnabled(true);
            exitMenuItem.setEnabled(true);
        }
    }

    private void exitActionPerformed() throws IOException {
        this.dispose();
        DataProcessing.disconnectFromDatabase();
        PrintMessage.print(user.getName() + " exit.");
        JOptionPane.showMessageDialog(null, "System exit. Thanks for utilizing.");
        user.exitSystem();
        LoginFrame.main(null);
    }
}