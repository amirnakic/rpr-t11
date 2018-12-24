package ba.unsa.etf.rpr;

import java.sql.*;

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

    public Drzava nadjiDrzavu(String drzava) {
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT drzava.naziv, grad.naziv, grad.broj_stanovnika FROM drzava, grad WHERE drzava.naziv = ? AND grad.id = drzava.id");
            stmt.setString(1, drzava);
            ResultSet rs = stmt.executeQuery();
            if (rs.isClosed())
                return null;
            Drzava d = new Drzava();
            d.setNaziv(rs.getString(1));
            Grad g = new Grad();
            g.setNaziv(rs.getString(2));
            g.setBrojStanovika(rs.getInt(3));
            d.setGlavniGrad(g);
            g.setDrzava(d);
            return d;
        }
        catch(SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
