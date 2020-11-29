package cs.whut.frame;

import cs.whut.common.DataProcessing;
import cs.whut.common.PrintMessage;
import cs.whut.common.User;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.sql.Timestamp;

/**
 * Created on 20:41 2020/9/28
 */
public class SelfFrame extends JFrame {
    private JPanel UserPanel;
    private JPasswordField oldPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmNewPasswordField;
    private JLabel charactorLabel;
    private JLabel confirmLabel;
    private JLabel newLabel;
    private JLabel oldLabel;
    private JLabel nameLabel;
    private JButton confirmButton;
    private JButton cancelButton;
    private JTextField nameTextField;
    private JTextField roleTextField;
    static JFrame frame;
    private User user;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                frame = new JFrame("Self Frame");
                frame.setContentPane(new SelfFrame(args[1]).UserPanel);
                frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
            } catch (Exception e) {
                frame.dispose();
                JOptionPane.showMessageDialog(null, e.getLocalizedMessage());
                PrintMessage.print(e.getLocalizedMessage());
            }
        });
    }

    public SelfFrame(String name) {
        if((user = DataProcessing.searchUser(name))==null){
            frame.dispose();
            JOptionPane.showMessageDialog(null,"User is NOT exist.");
        }

        nameTextField.setText(name);
        nameTextField.setEditable(false);
        roleTextField.setText(user.getRole());
        roleTextField.setEditable(false);

        confirmButton.addActionListener(e -> {
            if (JOptionPane.showOptionDialog(null, "Are you sure to save?", "Notice",
                    JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null) == 1) {
                JOptionPane.showMessageDialog(null, "Change is NOT saved.");
                frame.dispose();
            }
            String name1 = nameTextField.getText().trim();
            String oldPassword = new String(oldPasswordField.getPassword());
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmNewPasswordField.getPassword());
            try {
                changeSelfInfo(name1, oldPassword, newPassword, confirmPassword, user.getRole());
            } catch (IOException | ClassNotFoundException ex) {
                PrintMessage.print(ex.getLocalizedMessage());
            }
        });
        cancelButton.addActionListener(e -> {
            frame.dispose();
            JOptionPane.showMessageDialog(null,"Change is NOT saved.");
        });
    }

    private void changeSelfInfo(String name, String oldPassword, String newPassword, String confirmPassword, String role) throws IOException, ClassNotFoundException {
        if (DataProcessing.searchUser(name) == null) {
            PrintMessage.print("Cannot find "+name);
            JOptionPane.showMessageDialog(null, "User doesn't exist.");
            return;
        }

        if(DataProcessing.searchUser(name,oldPassword)==null){
            PrintMessage.print("The previous password is incorrect.");
            JOptionPane.showMessageDialog(null,"Old Password is incorrect.");
            return;
        }

        if(!newPassword.equals(confirmPassword)){
            PrintMessage.print("Two new passwords are different.");
            oldPasswordField.setText("");
            newPasswordField.setText("");
            confirmNewPasswordField.setText("");
            JOptionPane.showMessageDialog(null,"New Passwords are different.");
            return ;
        }

        if (DataProcessing.update(name, newPassword, role)) {
            frame.dispose();
            JOptionPane.showMessageDialog(null, "Password has been changed successfully!");
        } else {
            frame.dispose();
            JOptionPane.showMessageDialog(null, "Password change fail.");
        }
    }
}
