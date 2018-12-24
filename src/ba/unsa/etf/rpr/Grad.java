package ba.unsa.etf.rpr;

public class Grad {
    private String naziv;
    private int brojStanovika;
    private Drzava drzava;

    public Grad() {
    }

    public Grad(String naziv, int brojStanovika, Drzava drzava) {
        this.naziv = naziv;
        this.brojStanovika = brojStanovika;
        this.drzava = drzava;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public int getBrojStanovika() {
        return brojStanovika;
    }

    public void setBrojStanovika(int brojStanovika) {
        this.brojStanovika = brojStanovika;
    }

    public Drzava getDrzava() {
        return drzava;
    }

    public void setDrzava(Drzava drzava) {
        this.drzava = drzava;
    }
}
