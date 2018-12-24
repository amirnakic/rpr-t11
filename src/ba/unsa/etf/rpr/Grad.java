package ba.unsa.etf.rpr;

public class Grad {
    private String naziv;
    private int brojStanovnika;
    private Drzava drzava;

    public Grad() {
    }

    public Grad(String naziv, int brojStanovika, Drzava drzava) {
        this.naziv = naziv;
        this.brojStanovnika = brojStanovika;
        this.drzava = drzava;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public int getBrojStanovnika() {
        return brojStanovnika;
    }

    public void setBrojStanovnika(int brojStanovnika) {
        this.brojStanovnika = brojStanovnika;
    }

    public Drzava getDrzava() {
        return drzava;
    }

    public void setDrzava(Drzava drzava) {
        this.drzava = drzava;
    }
}
