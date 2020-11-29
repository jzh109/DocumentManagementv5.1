package cs.whut.common;

import javax.swing.*;
import java.io.*;
import java.sql.*;
import java.util.Enumeration;
import java.util.Hashtable;

public class DataProcessing {
    private static Connection connection;
    private static Statement statement;
    private static ResultSet resultSet;
    private static boolean connectedToDatabase = false;
    private static Hashtable<String, Doc> docs;
    public static Hashtable<String, User> users;

    static {
        docs = new Hashtable<>();
        users = new Hashtable<>();
    }

    public static void connectToDatabase(String driverName, String url, String name, String password) throws SQLException {
        try {
            Class.forName(driverName);
            connection = DriverManager.getConnection(url, name, password);
            connectedToDatabase = true;
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\t" + "Successfully connect to Database.");
        } catch (ClassNotFoundException e) {
            System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\t" + "Data loading error.");
            System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\t" + e.getLocalizedMessage());
        } catch (SQLException e) {
            System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\t" + "SQL error.");
            System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\t" + e.getLocalizedMessage());
        }
        loadAllUsers();
        loadAllDoc();
    }

    public static void disconnectFromDatabase() {
        if (connectedToDatabase) {
            try {
                resultSet.close();
                statement.close();
                connection.close();
                System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\t" + "Successfully disconnected from database.");
            } catch (SQLException e) {
                System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\t" + "Failed in disconnecting from database.");
                System.out.println(new Timestamp(System.currentTimeMillis()).toString()+"\t\t"+e.getLocalizedMessage());
            } finally {
                connectedToDatabase = false;
            }
        }
    }

    public static Doc searchDoc(String id) throws SQLException {
        if (!connectedToDatabase) {
            throw new SQLException("Not connect to database.");
        }
        if (docs.containsKey(id)) {
            return docs.get(id);
        }
        return null;
    }

    private static void loadAllUsers() throws SQLException {
        users.clear();
        User temp;
        if (!connectedToDatabase) {
            throw new SQLException("Not connect to database.");
        }
        statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        String sql = "select * from user_info";
        resultSet = statement.executeQuery(sql);
        while (resultSet.next()) {
            String username = resultSet.getString("username");
            String password = resultSet.getString("password");
            String role = resultSet.getString("role");
            temp = new User(username, password, role) {};
            users.put(username, temp);
        }
    }

    private static void loadAllDoc() throws SQLException {
        docs.clear();
        Doc temp;
        if (!connectedToDatabase) {
            throw new SQLException("Not connect to database.");
        }
        statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        String sql = "select * from doc_info";
        resultSet = statement.executeQuery(sql);
        while (resultSet.next()) {
            String ID = resultSet.getString("Id");
            String creator = resultSet.getString("creator");
            Timestamp timestamp = resultSet.getTimestamp("timestamp");
            String description = resultSet.getString("description");
            String filename = resultSet.getString("filename");
            ID = String.format("%04d", Integer.valueOf(ID));
            temp = new Doc(ID, creator, timestamp, description, filename);
            docs.put(ID, temp);
        }
    }

    public static Enumeration<Doc> getAllDocs() {
        return docs.elements();
    }

    public static Enumeration<User> getAllUser() {
        return users.elements();
    }

    public static boolean uploadFile(String filePath, String userName, String id, String description) {
        File inFile = new File(filePath);
        if (docs.containsKey(id)) {
            System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\t" + "File id " + id + " has existed.");
            return false;
        } else {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\t" + "File " + id + " upload successfully.");
            try {
                System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\t" + "File " + id + " upload to database.");
                String sql = "INSERT INTO doc_info VALUES(?,?,?,?,?,?)";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, id);
                preparedStatement.setString(2, userName);
                preparedStatement.setString(3, timestamp.toString());
                preparedStatement.setString(4, description);
                preparedStatement.setString(5, inFile.getName());
                FileInputStream fileInputStream = new FileInputStream(inFile);
                preparedStatement.setBinaryStream(6, fileInputStream, inFile.length());
                preparedStatement.executeUpdate();
                preparedStatement.close();
                System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\t" + "File " + id + " upload successfully to database.");
                loadAllDoc();
            } catch (SQLException | FileNotFoundException e) {
                System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\t" + "File " + id + " upload ERROR.");
                System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\t" + e.getLocalizedMessage());
                return false;
            }
            return true;
        }
    }

    public static boolean downloadFile(String id, String path) {
        byte[] buffer = new byte[1024];
        FileOutputStream fileOutputStream;
        InputStream inputStream;
        try {
            String sql = "SELECT * FROM doc_info WHERE Id='" + id + "'";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            File file = new File(path + "\\" + resultSet.getString("filename"));
            if (!file.createNewFile()) {
                System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\t" + "File has existed.");
                JOptionPane.showMessageDialog(null, "File has existed.");
                return false;
            }
            fileOutputStream = new FileOutputStream(file);
            Blob blob = resultSet.getBlob("file");
            inputStream = blob.getBinaryStream();
            int size;
            while ((size = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, size);
                fileOutputStream.flush();
            }
            System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tSuccessfully download from database.");
            return true;
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static User searchUser(String name) {
        if (users.containsKey(name)) {
            System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\t" + "Search user " + name + "...");
            return users.get(name);
        }
        System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\t" + "User " + name + " is not found.");
        return null;
    }

    public static User searchUser(String name, String password) {
        if (users.containsKey(name)) {
            User temp = users.get(name);
            System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\t" + "Search user [" + name + "] with password [" + password + "]...");
            if ((temp.getPassword()).equals(password))
                return temp;
        }
        System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\t" + "User [" + name + "] with password [" + password + "] is not found.");
        return null;
    }

    public static boolean update(String name, String password, String role) {
        if (users.containsKey(name)) {

            try {
                String sql = "update user_info set password='" + password + "',role='" + role + "' where username='" + name + "'";
                statement.executeUpdate(sql);
                loadAllUsers();
                System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\t" + "Successfully changed [" + name + "]'s password to [" + password + "] in database.");
            } catch (SQLException e) {
                System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\t" + "Failed in changing [" + name + "]'s password to [" + password + "] in database.");
                System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\t" + e.getLocalizedMessage());
            }
            return true;
        } else {
            System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\t" + "User [" + name + "] does not exist.");
            return false;
        }
    }

    public static boolean insert(String name, String password, String role) {
        if (users.containsKey(name)) {
            System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\t" + "User [" + name + "] has existed. Add user failed.");
            return false;
        } else {
            try {
                String sql = "insert into user_info (username,password,role) values(?,?,?)";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, password);
                preparedStatement.setString(3, role);
                preparedStatement.executeUpdate();
                loadAllUsers();
                System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\t" + "Successfully add user name[" + name + "] password[" + password + "] role[" + role + "] into database.");
            } catch (SQLException e) {
                System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\t" + "Failed in adding user name[" + name + "] password[" + password + "] role[" + role + "] into database.");
                System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\t" + e.getLocalizedMessage());
            }
            return true;
        }
    }

    public static boolean delete(String name) {
        if (users.containsKey(name)) {
            try {
                String sql = "delete from user_info where username='" + name + "'";
                statement.executeUpdate(sql);
                loadAllUsers();
                System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\t" + "Successfully delete user [" + name + "] from database.");
            } catch (SQLException e) {
                System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\t" + "Failed in deleting user [" + name + "] from database.");
                System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\t" + e.getLocalizedMessage());
            }
            return true;
        }
        System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\t" + "User [" + name + "] does not exist. Delete failed.");
        return false;
    }
}