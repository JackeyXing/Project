package com.xxj.oa.DButils;

import java.sql.*;
import java.util.ResourceBundle;

public class DBUtil {
    //静态变量：在类加载时执行
    //并且是有顺序的
    private static ResourceBundle bundle = ResourceBundle.getBundle("resources.jdbc");
    private static String driver = bundle.getString("driver");
    private static String url = bundle.getString("url");
    private static String user = bundle.getString("user");
    private static String password = bundle.getString("password");
    static  {
        try {
            Class.forName(driver);
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 返回数据库连接对象
     * @return
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
            Connection conn = DriverManager.getConnection(url, user, password);
            return conn;
    }

    public static void close(Connection conn, Statement ps, ResultSet rs) {
        if(rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
