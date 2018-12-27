package ba.unsa.etf.rpr;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.io.File;

public class Controller {
    public TextField textField;
    public Label glavniGradLbl;
    public GeografijaDAO dao;

    public Controller() {
        GeografijaDAO.removeInstance();
        File dbfile = new File("baza.db");
        dbfile.delete();
        dao = GeografijaDAO.getInstance();
    }

    public void clickOnButton(ActionEvent actionEvent) {
        Grad g = dao.glavniGrad(textField.getText());
        if (g == null) {
            glavniGradLbl.setText("ERROR!");
            return;
        }
        glavniGradLbl.setText(g.getNaziv().toUpperCase());
    }
}
