package ba.unsa.etf.rpr;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.Scanner;
import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class Main extends Application {

    public static String ispisiGradove() {
        ArrayList<Grad> gradovi = GeografijaDAO.getInstance().gradovi();
        String result = "";
        for (int i = 0; i < gradovi.size(); i++)
            result += gradovi.get(i).getNaziv() + " (" + gradovi.get(i).getDrzava().getNaziv() + ") - " + gradovi.get(i).getBrojStanovnika() + "\n";
        return result;
    }

    public static void glavniGrad() {
        String drzava = "";
        Scanner ulaz = new Scanner(System.in);
        drzava = ulaz.nextLine();
        GeografijaDAO geo = GeografijaDAO.getInstance();
        if (geo.nadjiDrzavu(drzava) == null) {
            System.out.println("Nepostojeća država");
            return;
        }
        Grad g = geo.glavniGrad(drzava);
        System.out.println("Glavni grad države " + drzava + " je " + g.getNaziv());
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/geografija.fxml"));
        loader.setController(new Controller());
        Parent root = loader.load();
        primaryStage.setTitle("Geografija - RPR tutorijal 9");
        primaryStage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
