import javafx.application.Application;
import javafx.stage.Stage;
import vue.Vue;

public class TestVelib extends Application {
    public void start(Stage stage) {
        new Vue().afficher(stage, getHostServices());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
