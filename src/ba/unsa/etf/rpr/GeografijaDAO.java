package ba.unsa.etf.rpr;

import java.sql.*;
import java.util.ArrayList;

public class GeografijaDAO {
    private static GeografijaDAO instance = null;
    private static Connection conn;

    private void napraviTabele() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS gradovi (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	naziv text NOT NULL UNIQUE,\n"
                + " broj_stanovnika integer,\n"
                + " drzava integer,\n"
                + "	FOREIGN KEY(drzava) REFERENCES drzave(id) ON DELETE CASCADE\n"
                + ");";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.execute();
        sql = "CREATE TABLE IF NOT EXISTS drzave (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	naziv text NOT NULL UNIQUE,\n"
                + " glavni_grad integer,\n"
                + "	FOREIGN KEY(glavni_grad) REFERENCES gradovi(id) ON DELETE CASCADE\n"
                + ");";
        stmt = conn.prepareStatement(sql);
        stmt.execute();
        dodajPodatke();
    }

    private static void initialize() throws SQLException {
        instance = new GeografijaDAO();
    }

    private GeografijaDAO() throws SQLException {
        conn = null;
        try {
            String url = "jdbc:sqlite:baza.db";
            conn = DriverManager.getConnection(url);
            napraviTabele();
        } catch (SQLException e) {
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

    public Drzava nadjiDrzavu(String drzava) {
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM drzave WHERE naziv = ?");
            stmt.setString(1, drzava);
            ResultSet rs = stmt.executeQuery();
            if (rs.isClosed())
                return null;
            Drzava d = new Drzava();
            d.setId(rs.getInt(1));
            d.setNaziv(drzava);
            return d;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Grad nadjiGrad(String grad) {
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM gradovi WHERE naziv = ?");
            stmt.setString(1, grad);
            ResultSet rs = stmt.executeQuery();
            if (rs.isClosed())
                return null;
            Grad g = new Grad();
            g.setId(rs.getInt(1));
            g.setNaziv(rs.getString(2));
            g.setBrojStanovnika(rs.getInt(3));
            return g;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Grad glavniGrad(String drzava) {
        Grad g = new Grad();
        if (nadjiDrzavu(drzava) == null)
            return null;
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT gradovi.id, gradovi.naziv, broj_stanovnika, drzava, " +
                    "drzave.id as d_id, drzave.naziv as d_naziv, drzave.glavni_grad as d_gg FROM gradovi INNER JOIN drzave ON " +
                    "gradovi.drzava = drzave.id WHERE drzave.naziv = ?");
            stmt.setString(1, drzava);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                g.setId(rs.getInt(1));
                g.setNaziv(rs.getString(2));
                g.setBrojStanovnika(rs.getInt(3));
                Drzava d = new Drzava();
                d.setId(rs.getInt(5));
                d.setNaziv(rs.getString(6));
                d.setGlavniGrad(g);
                g.setDrzava(d);
                return g;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void obrisiGradoveUDrzavi(String drzava) {
        try {
            Drzava d = nadjiDrzavu(drzava);
            if (d == null)
                return;
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM gradovi WHERE drzava = ?");
            stmt.setInt(1, d.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
    }

    public void obrisiDrzavu(String drzava) {
        try {
            if (nadjiDrzavu(drzava) == null)
                return;
            obrisiGradoveUDrzavi(drzava);
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM drzave WHERE naziv = ?");
            stmt.setString(1, drzava);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Grad> gradovi() {
        ArrayList<Grad> rezultat = new ArrayList<Grad>();
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT gradovi.id, gradovi.naziv, broj_stanovnika, drzava, " +
                    "drzave.id as d_id, drzave.naziv as d_naziv, drzave.glavni_grad as d_gg FROM gradovi INNER JOIN drzave ON " +
                    "gradovi.drzava = drzave.id ORDER BY broj_stanovnika DESC");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Drzava d = new Drzava();
                Grad g = new Grad();
                g.setId(rs.getInt(1));
                g.setNaziv(rs.getString(2));
                g.setBrojStanovnika(rs.getInt(3));
                d.setId(rs.getInt(5));
                d.setNaziv(rs.getString(6));
                Grad gg = nadjiGradPoIDu(rs.getInt(7));
                d.setGlavniGrad(gg);
                g.setDrzava(d);
                rezultat.add(g);
            }
            return rezultat;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public Grad nadjiGradPoIDu(Integer id) {
        Grad g = new Grad();
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT id, naziv, broj_stanovnika, drzava FROM gradovi WHERE id=?");
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                g.setId(rs.getInt(1));
                g.setNaziv(rs.getString(2));
                g.setBrojStanovnika(rs.getInt(3));
                return g;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void dodajGrad(Grad g) {
        try {
            if (nadjiGrad(g.getNaziv()) != null)
                return;
            PreparedStatement stmt1 = conn.prepareStatement("INSERT OR REPLACE INTO gradovi(naziv, broj_stanovnika, drzava) VALUES(?, ?, ?)");
            stmt1.setString(1, g.getNaziv());
            stmt1.setInt(2, g.getBrojStanovnika());
            stmt1.setInt(3, g.getDrzava().getId());
            stmt1.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void dodajDrzavu(Drzava d) {
        try {
            if(nadjiDrzavu(d.getNaziv()) != null)
                return;
            PreparedStatement stmt1 = conn.prepareStatement("INSERT OR REPLACE INTO drzave(naziv, glavni_grad) VALUES(?, null)");
            stmt1.setString(1, d.getNaziv());
            stmt1.executeUpdate();
            Drzava drzava = nadjiDrzavu(d.getNaziv());
            d.getGlavniGrad().setDrzava(drzava);
            dodajGrad(d.getGlavniGrad());
            Grad g = nadjiGrad(d.getGlavniGrad().getNaziv());
            d.getGlavniGrad().setId(g.getId());
            d.setId(drzava.getId());
            g.setDrzava(d);
            izmijeniGrad(g);
            izmijeniDrzavu(d);
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
    }

    public void izmijeniGrad(Grad g) {
        try {
            Grad grad = nadjiGradPoIDu(g.getId());
            PreparedStatement stmt1 = conn.prepareStatement("UPDATE gradovi SET naziv = ?, broj_stanovnika = ?, drzava = ? WHERE id = ?");
            stmt1.setString(1, g.getNaziv());
            stmt1.setInt(2, g.getBrojStanovnika());
            stmt1.setInt(3, g.getDrzava().getId());
            stmt1.setInt(4, grad.getId());
            stmt1.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void izmijeniDrzavu(Drzava d) {
        try {
            if (nadjiDrzavu(d.getNaziv()) == null)
                return;
            PreparedStatement stmt = conn.prepareStatement("UPDATE drzave SET naziv = ?, glavni_grad = ? WHERE id = ?");
            stmt.setString(1, d.getNaziv());
            stmt.setInt(2, d.getGlavniGrad().getId());
            stmt.setInt(3, d.getId());
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            return;
        }
    }

    private void dodajPodatke() {
        Grad pariz = new Grad("Pariz", 2206488);
        Drzava francuska = new Drzava("Francuska", pariz);
        pariz.setDrzava(francuska);
        dodajDrzavu(francuska);
        dodajGrad(pariz);
        Grad london = new Grad("London", 8825000 );
        Drzava vb = new Drzava("Velika Britanija", london);
        london.setDrzava(vb);
        dodajDrzavu(vb);
        dodajGrad(london);
        Grad manchester = new Grad("Manchester", 545500);
        manchester.setDrzava(vb);
        dodajGrad(manchester);
        Grad bec = new Grad("Beč", 1899055);
        Drzava austrija = new Drzava("Austrija", bec);
        bec.setDrzava(austrija);
        dodajDrzavu(austrija);
        dodajGrad(bec);
        Grad graz = new Grad("Graz",280200);
        graz.setDrzava(austrija);
        dodajGrad(graz);
    }
}