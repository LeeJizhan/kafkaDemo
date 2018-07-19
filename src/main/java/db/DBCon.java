package db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by Asus- on 2018/7/13.
 */
public class DBCon {

    private volatile static DBCon dbCon = null;

    private String driver;
    private String url;
    private String user;
    private String password;

    public Connection getConnection() {
        return connection;
    }

    private Connection connection;

    private DBCon() {
        Properties properties = new Properties();

        InputStream inputStream = DBCon.class.getResourceAsStream("/db.properties");

        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        driver = "com.mysql.jdbc.Driver";
        url = properties.getProperty("url");
        user = properties.getProperty("user");
        password = properties.getProperty("password");

        try {
            Class.forName(driver);
            try {
                connection = DriverManager.getConnection(url, user, password);
                if (!connection.isClosed()) {
                    System.out.println("数据库连接成功!");
                }
            } catch (SQLException e) {
                System.out.println("数据库连接失败!");
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            System.out.println("数据库没有安装驱动!");
            e.printStackTrace();
        }

    }

    public static DBCon getInstance() {
        if (dbCon == null) {
            synchronized (DBCon.class) {
                if (dbCon == null) {
                    dbCon = new DBCon();
                }
            }
        }
        return dbCon;
    }

}

