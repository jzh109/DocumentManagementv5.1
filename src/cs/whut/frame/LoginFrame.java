package cs.whut.frame;

import cs.whut.common.DataProcessing;
import cs.whut.common.PrintMessage;
import cs.whut.common.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

/**
 * Created on 19:18 2020/9/28
 */
public class LoginFrame extends JFrame {
    private JLabel nameLabel;
    private JLabel passwordLabel;
    private JTextField nameTextField;
    private JPasswordField passwordField;
    private JButton confirmButton;
    private JButton cancelButton;
    private JPanel loginPanel;
    static JFrame frame;

    public LoginFrame() {
        nameTextField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode()==KeyEvent.VK_ENTER){
                    passwordField.requestFocus();
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {}
        });
        passwordField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode()==KeyEvent.VK_ENTER){
                    loginMethod();
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {}
        });
        confirmButton.addActionListener(e -> loginMethod());
        cancelButton.addActionListener(e -> {
            frame.dispose();
            DataProcessing.disconnectFromDatabase();
            PrintMessage.print("Successfully disconnected from database.");
            PrintMessage.print("System exit.");
            System.exit(0);
        });
    }

    private void loginMethod() {
        String name = nameTextField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (name.trim().equals("")) {
            frame.dispose();
            PrintMessage.print("User name is empty.");
            JOptionPane.showMessageDialog(null, "User name cannot be empty.");
            PrintMessage.print("Restart login.");
            LoginFrame.main(null);
        } else if (DataProcessing.searchUser(name) == null) {
            frame.dispose();
            JOptionPane.showMessageDialog(null, "User doesn't exist!");
            LoginFrame.main(null);
        } else {
            User user = DataProcessing.searchUser(name, password);
            if (user != null) {
                MainFrame mainFrame = new MainFrame(user);
                mainFrame.setVisible(true);
                PrintMessage.print(user.getRole() + " user [" + user.getName() + "] login successfully.");
            } else {
                frame.dispose();
                JOptionPane.showMessageDialog(null, "Username or password is incorrect.");
                PrintMessage.print("Username or password is incorrect.");
                LoginFrame.main(null);
            }
        }
        frame.dispose();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                frame = new JFrame("System Login");
                frame.setContentPane(new LoginFrame().loginPanel);
                frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                frame.setSize(380, 200);
                UserFrame.setToCenter(frame);
                frame.setAlwaysOnTop(false);
                frame.setResizable(false);
                frame.setVisible(true);
            } catch (Exception e) {
                PrintMessage.print(e.getLocalizedMessage());
            }
        });
    }
}
