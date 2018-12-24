package ba.unsa.etf.rpr;

import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.DelayQueue;

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

    private void napraviTabele() throws SQLException {
        Statement stmt = conn.createStatement();
        String generirajDrzave = "CREATE TABLE \"drzava\" ( `id` INTEGER, `naziv` TEXT, `glavni_grad` INTEGER," +
                "FOREIGN KEY(`glavni_grad`) REFERENCES `grad`, PRIMARY KEY(`id`) )";
        try {
            stmt.executeQuery(generirajDrzave);
        } catch (SQLException e) {
            // Drzave vec postoje
        }
        String generirajGradove = "CREATE TABLE \"grad\" ( `id` INTEGER, `naziv` TEXT, `broj_stanovnika` INTEGER," +
                " `drzava` INTEGER, PRIMARY KEY(`id`), FOREIGN KEY(`drzava`) REFERENCES `drzava` )";
        try {
            stmt.executeQuery(generirajGradove);
        } catch (SQLException e) {
            // Gradovi vec postoje
        }
        dodajPodatke();
    }

    private static void initialize() throws SQLException {
        instance = new GeografijaDAO();
    }

    private GeografijaDAO() throws SQLException {
        String url = "jdbc:sqlite:baza.db";
        conn = DriverManager.getConnection(url);
        try {
            pripremiUpite();
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

    public Grad glavniGrad(String drzava) {
        try {
            Drzava d = nadjiDrzavu(drzava);
            if (d == null)
                return null;
            return d.getGlavniGrad();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void obrisiGradoveUDrzavi(String drzava) {
        try {
            if (nadjiDrzavu(drzava) == null)
                return;
            PreparedStatement stmt = obrisiGradove1;
            stmt.setString(1, drzava);
            ResultSet rs = stmt.executeQuery();
            int id = rs.getInt(1);
            PreparedStatement stmt1 = obrisiGradove2;
            stmt1.setInt(1, id);
            stmt1.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void obrisiDrzavu(String drzava) {
        try {
            if (nadjiDrzavu(drzava) == null)
                return;
            obrisiGradoveUDrzavi(drzava);
            PreparedStatement stmt = obrisiDrzavu;
            stmt.setString(1, drzava);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Grad> gradovi() {
        try {
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
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void dodajGrad(Grad g) {
        try {
            PreparedStatement stmt = dodajGrad1;
            stmt.setString(1, g.getDrzava().getNaziv());
            ResultSet rs = stmt.executeQuery();
            int id = rs.getInt(1);
            PreparedStatement stmt1 = dodajGrad2;
            stmt1.setString(1, g.getNaziv());
            stmt1.setInt(2, g.getBrojStanovnika());
            stmt1.setInt(3, id);
            stmt1.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void dodajDrzavu(Drzava d) {
        try {
            PreparedStatement stmt = dodajDrzavu1;
            stmt.setString(1, d.getGlavniGrad().getNaziv());
            ResultSet rs = stmt.executeQuery();
            int id = rs.getInt(1);
            PreparedStatement stmt1 = dodajDrzavu2;
            stmt1.setString(1, d.getNaziv());
            stmt1.setInt(2, id);
            stmt1.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void izmijeniGrad(Grad g) {
        try {
            PreparedStatement stmt = izmijeniGrad1;
            stmt.setString(1, g.getDrzava().getNaziv());
            ResultSet rs = stmt.executeQuery();
            int id = rs.getInt(1);
            PreparedStatement stmt1 = izmijeniGrad2;
            stmt1.setString(1, g.getNaziv());
            stmt1.setInt(2, g.getBrojStanovnika());
            stmt1.setInt(3, id);
            stmt1.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void dodajPodatke() {
        Grad pariz = new Grad();
        pariz.setNaziv("Pariz");
        pariz.setBrojStanovnika(2200000);
        Drzava francuska = new Drzava();
        francuska.setNaziv("Francuska");
        francuska.setGlavniGrad(pariz);
        pariz.setDrzava(francuska);
        dodajDrzavu(francuska);
        dodajGrad(pariz);

        Grad london = new Grad();
        london.setNaziv("London");
        london.setBrojStanovnika(8136000);
        Drzava velikaBritanija = new Drzava();
        velikaBritanija.setNaziv("Velika Britanija");
        velikaBritanija.setGlavniGrad(london);
        london.setDrzava(velikaBritanija);
        dodajDrzavu(velikaBritanija);
        dodajGrad(london);

        Grad bec = new Grad();
        bec.setNaziv("Beč");
        bec.setBrojStanovnika(1867000);
        Drzava austrija = new Drzava();
        austrija.setNaziv("Austrija");
        austrija.setGlavniGrad(bec);
        bec.setDrzava(austrija);
        dodajDrzavu(austrija);
        dodajGrad(bec);

        Grad mancester = new Grad();
        mancester.setNaziv("Manchester");
        mancester.setBrojStanovnika(510746);
        mancester.setDrzava(velikaBritanija);
        dodajGrad(mancester);

        Grad graz = new Grad();
        graz.setNaziv("Graz");
        graz.setBrojStanovnika(283869);
        graz.setDrzava(austrija);
        dodajGrad(graz);
    }
}
