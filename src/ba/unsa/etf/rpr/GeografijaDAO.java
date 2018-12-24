package ba.unsa.etf.rpr;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class GeografijaDAO {
    private static GeografijaDAO instance = null;
    private static Connection conn;

    private static void initialize() throws SQLException {
        instance = new GeografijaDAO();
    }

    private GeografijaDAO() throws SQLException {
        String url = "jdbc:sqlite:baza.db";
        conn = DriverManager.getConnection(url);
    }

    public static GeografijaDAO getInstance() {
        if (instance == null) {
            try {
                initialize();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    public static void removeInstance() {
        if (conn == null) return;
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        conn = null;
        instance = null;
    }
}
