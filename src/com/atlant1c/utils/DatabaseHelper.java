package com.atlant1c.utils;

import com.atlant1c.model.Shell;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:sqlite:data.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        try {
            createDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void createDatabase() throws SQLException {
        String createShellTable = "CREATE TABLE IF NOT EXISTS shell (" +
                "id VARCHAR(32) PRIMARY KEY," +
                "url VARCHAR(255) NOT NULL," +
                "password VARCHAR(255) NOT NULL," +
                "payload VARCHAR(255) NOT NULL," +
                "encoding VARCHAR(50) DEFAULT 'UTF-8'," +
                "headers TEXT," +
                "connTimeout INT," +
                "readTimeout INT," +
                "createTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "updateTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";

        String createShellEnvTable = "CREATE TABLE IF NOT EXISTS shellENV (" +
                "id VARCHAR(32) PRIMARY KEY," +
                "value TEXT" +
                ");";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(createShellTable);
            stmt.execute(createShellEnvTable);
        }
    }

    public static List<Shell> getAllShells() throws SQLException {
        String sql = "SELECT * FROM shell";
        List<Shell> shellList = new ArrayList<>();

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Shell shell = new Shell();
                shell.setId(rs.getString("id"));
                shell.setUrl(rs.getString("url"));
                shell.setPassword(rs.getString("password"));
                shell.setPayload(rs.getString("payload"));
                shell.setEncoding(rs.getString("encoding"));
                shell.setHeaders(rs.getString("headers"));
                shell.setConnTimeout(rs.getInt("connTimeout"));
                shell.setReadTimeout(rs.getInt("readTimeout"));
                shell.setCreateTime(rs.getTimestamp("createTime"));
                shell.setUpdateTime(rs.getTimestamp("updateTime"));
                shellList.add(shell);
            }
        }
        return shellList;
    }

    // 添加插入 shell 的方法
    public static void insertShell(Shell shell) throws SQLException {
        String sql = "INSERT INTO shell (id, url, password, payload, encoding, headers, connTimeout, readTimeout, createTime, updateTime) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, shell.getId());
            pstmt.setString(2, shell.getUrl());
            pstmt.setString(3, shell.getPassword());
            pstmt.setString(4, shell.getPayload());
            pstmt.setString(5, shell.getEncoding());
            pstmt.setString(6, shell.getHeaders());
            pstmt.setInt(7, shell.getConnTimeout());
            pstmt.setInt(8, shell.getReadTimeout());
            pstmt.setTimestamp(9, new Timestamp(System.currentTimeMillis()));
            pstmt.setTimestamp(10, new Timestamp(System.currentTimeMillis()));
            pstmt.executeUpdate();
        }
    }

    // 添加更新 shell 的方法
    public static void updateShell(Shell shell) throws SQLException {
        String sql = "UPDATE shell SET url = ?, password = ?, payload = ?, encoding = ?, headers = ?, connTimeout = ?, readTimeout = ?, updateTime = ? WHERE id = ?";

        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, shell.getUrl());
            pstmt.setString(2, shell.getPassword());
            pstmt.setString(3, shell.getPayload());
            pstmt.setString(4, shell.getEncoding());
            pstmt.setString(5, shell.getHeaders());
            pstmt.setInt(6, shell.getConnTimeout());
            pstmt.setInt(7, shell.getReadTimeout());
            pstmt.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
            pstmt.setString(9, shell.getId());
            pstmt.executeUpdate();
        }
    }

    public static void deleteShell(String id) throws SQLException {
        String sql = "DELETE FROM shell WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        }
    }

    public static void insertShellENV(String id, String value) throws SQLException {
        // 插入 spring 环境信息的代码
        String sql = "INSERT INTO shellENV (id, value) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, value);
            pstmt.executeUpdate();
        }
    }

    public static String getShellENVValueById(String id) throws SQLException {
        String sql = "SELECT value FROM shellENV WHERE id = ?";
        String value = null;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    value = rs.getString("value");
                }
            }
        }

        return value;
    }

    public static String getShellById(String id) throws SQLException {
        String sql = "SELECT * FROM shell WHERE id = ?";
        StringBuilder result = new StringBuilder();

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    result.append("ID: ").append(rs.getString("id")).append("\n")
                            .append("URL: ").append(rs.getString("url")).append("\n")
                            .append("Password: ").append(rs.getString("password")).append("\n")
                            .append("Payload: ").append(rs.getString("payload")).append("\n")
                            .append("Encoding: ").append(rs.getString("encoding")).append("\n")
                            .append("Headers: ").append(rs.getString("headers")).append("\n")
                            .append("ConnTimeout: ").append(rs.getInt("connTimeout")).append("\n")
                            .append("ReadTimeout: ").append(rs.getInt("readTimeout")).append("\n")
                            .append("CreateTime: ").append(rs.getString("createTime")).append("\n")
                            .append("UpdateTime: ").append(rs.getString("updateTime"));
                }
            }
        }

        return result.toString();
    }
    // Other methods...
}
