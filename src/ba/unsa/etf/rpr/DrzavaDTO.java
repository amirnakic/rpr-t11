package ba.unsa.etf.rpr;

public class DrzavaDTO {
    private String naziv;
    private GradDTO glavniGrad;

    public DrzavaDTO() {
    }

    public DrzavaDTO(String naziv, GradDTO glavniGrad) {
        this.naziv = naziv;
        this.glavniGrad = glavniGrad;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public GradDTO getGlavniGrad() {
        return glavniGrad;
    }

    public void setGlavniGrad(GradDTO glavniGrad) {
        this.glavniGrad = glavniGrad;
    }
}
