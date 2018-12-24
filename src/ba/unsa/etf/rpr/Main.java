package ba.unsa.etf.rpr;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static String ispisiGradove() {
        try {
            ArrayList<Grad> gradovi = GeografijaDAO.getInstance().gradovi();
            String result = "";
            for (int i = 0; i < gradovi.size(); i++)
                result += gradovi.get(i).getNaziv() + " (" + gradovi.get(i).getDrzava().getNaziv() + ") - " + gradovi.get(i).getBrojStanovnika() + "\n";
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void glavniGrad() {
        String drzava = "";
        Scanner ulaz = new Scanner(System.in);
        drzava = ulaz.nextLine();
        try {
            GeografijaDAO geo = GeografijaDAO.getInstance();
            if (geo.nadjiDrzavu(drzava) == null) {
                System.out.println("Nepostojeća država");
                return;
            }
            Grad g = geo.glavniGrad(drzava);
            System.out.println("Glavni grad države " + drzava + " je " + g.getNaziv());
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
    }

    public static void main(String[] args) {
        System.out.println("Gradovi su:\n" + ispisiGradove());
        glavniGrad();
    }
}
