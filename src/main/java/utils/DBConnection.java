package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:mysql://gateway01.us-east-1.prod.aws.tidbcloud.com:4000/test?"
            + "sslMode=VERIFY_IDENTITY"
            + "&enabledTLSProtocols=TLSv1.2,TLSv1.3"
            + "&serverTimezone=America/Lima";

    private static final String USER = "gPUQVZhzGhxrsdJ.root";
    private static final String PASS = "TofMmiVWs7NJNWuy";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver no encontrado: " + e.getMessage());
        }
    }
}