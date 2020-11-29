package cs.whut.common;

import cs.whut.frame.LoginFrame;

import java.awt.*;
import java.sql.Timestamp;

/**
 * Created on 16:37 2020/9/29
 */
public class MainGUI {
    private static String driverName = "com.mysql.cj.jdbc.Driver";
    private static String url = "jdbc:mysql://localhost:3306/document";
    private static String user = "root";
    private static String password = "123456";

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\t" + "Initial system...");
                System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\t" + "User login.");
                DataProcessing.connectToDatabase(driverName, url, user, password);
                LoginFrame.main(args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
