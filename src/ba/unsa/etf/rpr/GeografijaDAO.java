package ba.unsa.etf.rpr;

import java.sql.*;
import java.util.ArrayList;

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
            g.setBrojStanovnika(rs.getInt(3));
            d.setGlavniGrad(g);
            g.setDrzava(d);
            return d;
        }
        catch(SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Grad glavniGrad(String drzava) {
        Drzava d = nadjiDrzavu(drzava);
        if (d == null)
            return null;
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT grad.naziv, grad.broj_stanovnika FROM grad WHERE drzava.naziv = ? AND drzava.id = grad.id");
            stmt.setString(1, drzava);
            ResultSet rs = stmt.executeQuery();
            if (rs.isClosed())
                return null;
            Grad g = new Grad();
            g.setDrzava(d);
            g.setNaziv(rs.getString(1));
            g.setBrojStanovnika(rs.getInt(2));
            return g;
        }
        catch(SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void obrisiGradoveUDrzavi(String drzava) throws SQLException {
        int id;
        PreparedStatement stmt = conn.prepareStatement("SELECT id FROM drzava WHERE naziv = ?");
        stmt.setString(1, drzava);
        ResultSet rs = stmt.executeQuery();
        id = rs.getInt(1);
        PreparedStatement stmt1 = conn.prepareStatement("DELETE * FROM grad WHERE drzava = ?");
        stmt1.setInt(1, id);
        stmt1.executeUpdate();
    }

    public void obrisiDrzavu(String drzava) throws SQLException {
        if (nadjiDrzavu(drzava) == null)
            return;
        obrisiGradoveUDrzavi(drzava);
        PreparedStatement stmt = conn.prepareStatement("DELETE * FROM drzava WHERE naziv = ?");
        stmt.setString(1, drzava);
        stmt.executeUpdate();
    }

    public ArrayList<Grad> gradovi() throws SQLException {
        ArrayList<Grad> result  = new ArrayList<>();
        PreparedStatement stmt = conn.prepareStatement("SELECT grad.id, grad.naziv, grad.broj_stanovnika, grad.drzava, drzava.id, drzava.naziv FROM grad, drzava WHERE drzava.id = grad.drzava ORDER BY broj_stanovnika DESC");
        ResultSet rs = stmt.executeQuery();
        while(rs.next()) {
            Grad g = new Grad();
            g.setNaziv(rs.getString(2));
            g.setBrojStanovnika(rs.getInt(3));
            Drzava d = new Drzava();
            d.setNaziv(rs.getString(6));
            g.setDrzava(d);
            if (rs.getInt(1) == rs.getInt(5))
                d.setGlavniGrad(g);
            result.add(g);
        }
        return result;
    }

    public void dodajGrad(Grad g) throws SQLException {
        int id;
        PreparedStatement stmt = conn.prepareStatement("SELECT id FROM drzava WHERE naziv = ?");
        stmt.setString(1, g.getDrzava().getNaziv());
        ResultSet rs = stmt.executeQuery();
        id = rs.getInt(1);
        PreparedStatement stmt1 = conn.prepareStatement("INSERT INTO grad(naziv, broj_stanovnika, drzava) VALUES(?, ?, ?) ");
        stmt1.setString(1, g.getNaziv());
        stmt1.setInt(2, g.getBrojStanovnika());
        stmt1.setInt(3, id);
        stmt1.executeUpdate();
    }

    public void dodajDrzavu(Drzava d) throws SQLException {
        int id;
        PreparedStatement stmt = conn.prepareStatement("SELECT id FROM grad WHERE naziv = ?");
        stmt.setString(1, d.getGlavniGrad().getNaziv());
        ResultSet rs = stmt.executeQuery();
        id = rs.getInt(1);
        PreparedStatement stmt1 = conn.prepareStatement("INSERT INTO drzava(naziv, glavni_grad) VALUES(?, ?)");
        stmt1.setString(1, d.getNaziv());
        stmt1.setInt(2, id);
        stmt1.executeUpdate();
    }

    public void izmijeniGrad(Grad g) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT id FROM drzava WHERE naziv = ?");
        stmt.setString(1, g.getDrzava().getNaziv());
        ResultSet rs = stmt.executeQuery();
        int id = rs.getInt(1);
        PreparedStatement stmt1 = conn.prepareStatement("UPDATE grad SET naziv = ? , broj_stanovnika = ? , drzava = ? WHERE id = ?");
        stmt1.setString(1, g.getNaziv());
        stmt1.setInt(2, g.getBrojStanovnika());
        stmt1.setInt(3, id);
        stmt1.executeUpdate();
    }
}
