package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 数据库连接与 SQL 执行类（SQL Server 版）。
 * 对应实验八 db.java 的 (1)~(5) 填空：
 * (1) import java.sql.*
 * (2) Class.forName(...)      加载驱动
 * (3) DriverManager.getConnection(...)  建立连接
 * (4) dbConn.createStatement()  创建语句对象
 * (5) statement.executeQuery(sql)  执行查询
 */
public class db {
    private Connection dbConn;
    private Statement stateMent;

    public db() {
        // SQL Server JDBC 驱动
        String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        // 数据库地址：本机 1433 端口，pay 库
        // encrypt=true 是新版驱动默认值；本地自签名证书要加 trustServerCertificate=true
        String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=pay"
                + ";encrypt=true;trustServerCertificate=true";
        String userName = "sa";
        String userPwd = "666";
        try {
            Class.forName(driverName);
            dbConn = DriverManager.getConnection(dbURL, userName, userPwd);
            System.out.println("Connection Successful!");
        } catch (Exception e) {
            System.out.println("Connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public int executeUpdate(String sql) throws SQLException {
        if (dbConn == null) {
            throw new SQLException("数据库连接未建立，无法执行 SQL");
        }
        stateMent = dbConn.createStatement();
        return stateMent.executeUpdate(sql);
    }

    /**
     * 实验九新增：创建预编译语句对象。
     * SQL 里的 ? 由调用方用 setXXX 填值，可以反复执行，也能防止 SQL 注入。
     */
    public PreparedStatement PrepareStatement(String sql) throws SQLException {
        if (dbConn == null) {
            throw new SQLException("数据库连接未建立，无法执行 SQL");
        }
        return dbConn.prepareStatement(sql);
    }

    public ResultSet executeQuery(String sql) throws SQLException {
        if (dbConn == null) {
            throw new SQLException("数据库连接未建立，无法执行 SQL");
        }
        stateMent = dbConn.createStatement();
        return stateMent.executeQuery(sql);
    }

    /**
     * 带参数的查询，参数依次填入 SQL 中的 ?。
     * 用 PreparedStatement 可以避免 SQL 注入，登录验证必须这样写。
     */
    public ResultSet executeQuery(String sql, Object... params) throws SQLException {
        if (dbConn == null) {
            throw new SQLException("数据库连接未建立，无法执行 SQL");
        }
        PreparedStatement ps = dbConn.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
        stateMent = ps;
        return ps.executeQuery();
    }

    public void closeConn() throws SQLException {
        if (dbConn != null) {
            dbConn.close();
            dbConn = null;
        }
    }
}
