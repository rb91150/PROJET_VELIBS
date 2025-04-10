import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class TestJavaFX extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage stage) {
        Label label = new Label("JavaFX fonctionne !");
        Scene scene = new Scene(label, 300, 150);
        stage.setScene(scene);
        stage.setTitle("Test JavaFX");
        stage.show();
    }
}
