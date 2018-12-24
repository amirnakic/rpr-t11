package ba.unsa.etf.rpr;

public class GradDTO {
    private String naziv;
    private int brojStanovika;
    private DrzavaDTO drzava;

    public GradDTO() {
    }

    public GradDTO(String naziv, int brojStanovika, DrzavaDTO drzava) {
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

    public DrzavaDTO getDrzava() {
        return drzava;
    }

    public void setDrzava(DrzavaDTO drzava) {
        this.drzava = drzava;
    }
}
