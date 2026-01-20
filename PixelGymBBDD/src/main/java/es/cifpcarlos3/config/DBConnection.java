package es.cifpcarlos3.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
    static final String url = "jdbc:postgresql://ep-twilight-tooth-ag1qsth0-pooler.c-2.eu-central-1.aws.neon.tech:5432/neondb?sslmode=require&sslfactory=org.postgresql.ssl.NonValidatingFactory";    private static final String PASS = "npg_jct0JreifpH4";
    private static final String USER = "neondb_owner";
    public static Connection getConnection() throws SQLException {
        Properties props = new Properties();
        props.setProperty("user", USER);
        props.setProperty("password", PASS);
        props.setProperty("ssl", "true"); // Obligatorio para Neon
        return DriverManager.getConnection(url, props);
    }
}
