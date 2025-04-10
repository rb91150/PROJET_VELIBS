package vue;

import javafx.application.HostServices;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import modele.Station;
import services.VelibService;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Vue {
    private List<Station> stations = VelibService.getStations();

    public void afficher(Stage stage, HostServices hostServices) {
        Label titre = new Label("Stations Vélib (avec recherche, tri et export)");

        TextField searchField = new TextField();
        searchField.setPromptText("Rechercher une station...");

        ComboBox<String> arrondissementComboBox = new ComboBox<>();
        arrondissementComboBox.getItems().add("Tous");
        stations.stream()
                .map(Station::getArrondissement)
                .distinct()
                .sorted()
                .forEach(arrondissementComboBox.getItems()::add);
        arrondissementComboBox.getSelectionModel().selectFirst();

        CheckBox checkBox = new CheckBox("Afficher seulement les stations avec +10 vélos dispo");

        Button exportButton = new Button("Exporter en CSV");

        ListView<Station> listView = new ListView<>();

        Runnable miseAJour = () -> {
            String filtreTexte = searchField.getText().toLowerCase();
            String arrondissementChoisi = arrondissementComboBox.getValue();
            boolean filtrePlusDe10Velo = checkBox.isSelected();

            List<Station> resultat = stations.stream()
                    .filter(s -> s.getNom().toLowerCase().contains(filtreTexte))
                    .filter(s -> arrondissementChoisi.equals("Tous") || s.getArrondissement().equals(arrondissementChoisi))
                    .filter(s -> !filtrePlusDe10Velo || s.getNbVelosDispo() > 10)
                    .collect(Collectors.toList());

            listView.getItems().setAll(resultat);
        };

        searchField.textProperty().addListener((obs, old, niou) -> miseAJour.run());
        arrondissementComboBox.setOnAction(e -> miseAJour.run());
        checkBox.setOnAction(e -> miseAJour.run());

        listView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2 && listView.getSelectionModel().getSelectedItem() != null) {
                Station s = listView.getSelectionModel().getSelectedItem();
                hostServices.showDocument(s.getGoogleMapsLink());
            }
        });

        exportButton.setOnAction(e -> {
            try (FileWriter fw = new FileWriter("stations_export.csv")) {
                fw.write("Nom,Statut,Vélos,Bornes,Arrondissement,Latitude,Longitude\n");
                for (Station s : listView.getItems()) {
                    fw.write(String.format("\"%s\",%s,%d,%d,%s,%.6f,%.6f\n",
                            s.getNom(), s.getStatut(), s.getNbVelosDispo(), s.getNbBornesDispo(),
                            s.getArrondissement(), s.getLatitude(), s.getLongitude()));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        VBox root = new VBox(10, titre, searchField, arrondissementComboBox, checkBox, exportButton, listView);
        Scene scene = new Scene(root, 500, 600);
        stage.setScene(scene);
        stage.setTitle("Stations Vélib (MVC + Recherche + Tri + Export)");
        stage.show();

        miseAJour.run();
    }
}
