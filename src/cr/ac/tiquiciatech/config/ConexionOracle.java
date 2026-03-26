package cr.ac.tiquiciatech.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionOracle {

    private static final String USER = "TIQUICIATECH";
    private static final String PASSWORD = "123456789Proyecto";

    private static final String URL =
        "jdbc:oracle:thin:@lenguajebasedatos_medium?TNS_ADMIN=C:/Users/Joshua/Documents/Universidad/LENGUAJE_BD";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontró el driver JDBC de Oracle.", e);
        }

        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        conn.setAutoCommit(false);
        return conn;
    }
}