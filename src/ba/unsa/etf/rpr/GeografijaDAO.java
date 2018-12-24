package ba.unsa.etf.rpr;

import java.sql.*;
import java.util.ArrayList;

public class GeografijaDAO {
    private static GeografijaDAO instance = null;
    private static Connection conn;

    private PreparedStatement nadjiDrzavu1;
    private PreparedStatement nadjiDrzavu2;
    private PreparedStatement obrisiGradove1;
    private PreparedStatement obrisiGradove2;
    private PreparedStatement obrisiDrzavu;
    private PreparedStatement gradovi;
    private PreparedStatement dodajGrad1;
    private PreparedStatement dodajGrad2;
    private PreparedStatement dodajDrzavu1;
    private PreparedStatement dodajDrzavu2;
    private PreparedStatement izmijeniGrad1;
    private PreparedStatement izmijeniGrad2;

    private void pripremiUpite() throws SQLException {
        nadjiDrzavu1 = conn.prepareStatement("SELECT glavni_grad FROM drzava WHERE naziv = ?");
        nadjiDrzavu2 = conn.prepareStatement("SELECT naziv, broj_stanovnika FROM grad WHERE id = ?");
        obrisiGradove1 = conn.prepareStatement("SELECT id FROM drzava WHERE naziv = ?");
        obrisiGradove2 = conn.prepareStatement("DELETE * FROM grad WHERE drzava = ?");
        obrisiDrzavu = conn.prepareStatement("DELETE * FROM drzava WHERE naziv = ?");
        gradovi = conn.prepareStatement("SELECT grad.id, grad.naziv, grad.broj_stanovnika, grad.drzava, drzava.id, drzava.naziv, drzava.glavni_grad FROM grad, drzava WHERE grad.drzava = drzava.id ORDER BY broj_stanovnika DESC");
        dodajGrad1 = conn.prepareStatement("SELECT id FROM drzava WHERE naziv = ?");
        dodajGrad2 = conn.prepareStatement("INSERT INTO grad(naziv, broj_stanovnika, drzava) VALUES(?, ?, ?)");
        dodajDrzavu1 = conn.prepareStatement("SELECT id FROM grad WHERE naziv = ?");
        dodajDrzavu2 = conn.prepareStatement("INSERT INTO drzava(naziv, glavni_grad) VALUES(?, ?)");
        izmijeniGrad1 = conn.prepareStatement("SELECT id FROM drzava WHERE naziv = ?");
        izmijeniGrad2 = conn.prepareStatement("UPDATE grad SET naziv = ?, broj_stanovnika = ?, drzava = ? WHERE id = ?");
    }

    private static void initialize() throws SQLException {
        instance = new GeografijaDAO();
    }

    private GeografijaDAO() throws SQLException {
        String url = "jdbc:sqlite:baza.db";
        conn = DriverManager.getConnection(url);
        try {
            pripremiUpite();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
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

    public Drzava nadjiDrzavu(String drzava) throws SQLException {
        PreparedStatement stmt = nadjiDrzavu1;
        stmt.setString(1, drzava);
        ResultSet rs = stmt.executeQuery();
        if (rs.isClosed())
            return null;
        int id = rs.getInt(1);
        Drzava d = new Drzava();
        d.setNaziv(drzava);
        PreparedStatement stmt1 = nadjiDrzavu2;
        stmt1.setInt(1, id);
        ResultSet rs1 = stmt1.executeQuery();
        Grad g = new Grad();
        g.setNaziv(rs1.getString(1));
        g.setBrojStanovnika(rs1.getInt(2));
        d.setGlavniGrad(g);
        g.setDrzava(d);
        return d;
    }

    public Grad glavniGrad(String drzava) throws SQLException {
        Drzava d = nadjiDrzavu(drzava);
        if (d == null)
            return null;
        return d.getGlavniGrad();
    }

    public void obrisiGradoveUDrzavi(String drzava) throws SQLException {
        if (nadjiDrzavu(drzava) == null)
            return;
        PreparedStatement stmt = obrisiGradove1;
        stmt.setString(1, drzava);
        ResultSet rs = stmt.executeQuery();
        int id = rs.getInt(1);
        PreparedStatement stmt1 = obrisiGradove2;
        stmt1.setInt(1, id);
        stmt1.executeUpdate();
    }

    public void obrisiDrzavu(String drzava) throws SQLException {
        if (nadjiDrzavu(drzava) == null)
            return;
        obrisiGradoveUDrzavi(drzava);
        PreparedStatement stmt = obrisiDrzavu;
        stmt.setString(1, drzava);
        stmt.executeUpdate();
    }

    public ArrayList<Grad> gradovi() throws SQLException {
        ArrayList<Grad> result = new ArrayList<>();
        PreparedStatement stmt = gradovi;
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Grad g = new Grad();
            g.setNaziv(rs.getString(2));
            g.setBrojStanovnika(rs.getInt(3));
            Drzava d = new Drzava();
            d.setNaziv(rs.getString(6));
            g.setDrzava(d);
            if (rs.getInt(1) == rs.getInt(7))
                d.setGlavniGrad(g);
            result.add(g);
        }
        return result;
    }

    public void dodajGrad(Grad g) throws SQLException {
        PreparedStatement stmt = dodajGrad1;
        stmt.setString(1, g.getDrzava().getNaziv());
        ResultSet rs = stmt.executeQuery();
        int id = rs.getInt(1);
        PreparedStatement stmt1 = dodajGrad2;
        stmt1.setString(1, g.getNaziv());
        stmt1.setInt(2, g.getBrojStanovnika());
        stmt1.setInt(3, id);
        stmt1.executeUpdate();
    }

    public void dodajDrzavu(Drzava d) throws SQLException {
        PreparedStatement stmt = dodajDrzavu1;
        stmt.setString(1, d.getGlavniGrad().getNaziv());
        ResultSet rs = stmt.executeQuery();
        int id = rs.getInt(1);
        PreparedStatement stmt1 = dodajDrzavu2;
        stmt1.setString(1, d.getNaziv());
        stmt1.setInt(2, id);
        stmt1.executeUpdate();
    }

    public void izmijeniGrad(Grad g) throws SQLException {
        PreparedStatement stmt = izmijeniGrad1;
        stmt.setString(1, g.getDrzava().getNaziv());
        ResultSet rs = stmt.executeQuery();
        int id = rs.getInt(1);
        PreparedStatement stmt1 = izmijeniGrad2;
        stmt1.setString(1, g.getNaziv());
        stmt1.setInt(2, g.getBrojStanovnika());
        stmt1.setInt(3, id);
        stmt1.executeUpdate();
    }
}
