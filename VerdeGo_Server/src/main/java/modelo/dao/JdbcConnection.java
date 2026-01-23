package modelo.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JdbcConnection {
    private static final String DB_NAME = "verdego_db";
    private static final String USER = "root";
    private static final String PASS = "";         
    private static final String URL = "jdbc:mysql://localhost:3306/" + DB_NAME;

    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
            
                Class.forName("com.mysql.jdbc.Driver"); 
                connection = DriverManager.getConnection(URL, USER, PASS);
                System.out.println("Conexión a VerdeGo establecida con éxito.");
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error al conectar con la BD:");
            e.printStackTrace();
        }
        return connection;
    }
}