package vue;

import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import modele.Station;

import java.awt.Desktop;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class Vue {

    private List<Station> toutesStations;
    private ListView<String> listView;
    private Station stationSelectionnee;

    public void afficher(Stage stage, List<Station> stations) {
        toutesStations = stations;

        Label titre = new Label("Stations Vélib (filtre arrondissement, recherche, export, Maps)");
        TextField champRecherche = new TextField();
        champRecherche.setPromptText("Rechercher une station...");

        CheckBox filtreVeloCheck = new CheckBox("Afficher seulement les stations avec +10 vélos dispo");

        Set<String> arrondissements = new TreeSet<>();
        for (Station s : stations) {
            arrondissements.add(s.getArrondissement());
        }

        ComboBox<String> comboArr = new ComboBox<>(FXCollections.observableArrayList(arrondissements));
        comboArr.getItems().add(0, "Tous");
        comboArr.getSelectionModel().selectFirst();

        Button boutonExport = new Button("Exporter en CSV");
        Button boutonMaps = new Button("Ouvrir sur Google Maps");

        listView = new ListView<>();
        majAffichageListView("", false, "Tous");

        champRecherche.textProperty().addListener((obs, oldVal, newVal) -> {
            majAffichageListView(newVal, filtreVeloCheck.isSelected(), comboArr.getValue());
        });

        filtreVeloCheck.selectedProperty().addListener((obs, oldVal, newVal) -> {
            majAffichageListView(champRecherche.getText(), newVal, comboArr.getValue());
        });

        comboArr.setOnAction(e -> {
            majAffichageListView(champRecherche.getText(), filtreVeloCheck.isSelected(), comboArr.getValue());
        });

        boutonExport.setOnAction(e -> {
            try (FileWriter writer = new FileWriter("stations_export.csv")) {
                for (String line : listView.getItems()) {
                    writer.write(line + "\n");
                }
                System.out.println("Fichier CSV exporté !");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        listView.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            int index = newVal.intValue();
            if (index >= 0 && index < toutesStations.size()) {
                stationSelectionnee = toutesStations.get(index);
            }
        });

        boutonMaps.setOnAction(e -> {
            if (stationSelectionnee != null) {
                String url = "https://www.google.com/maps?q=" + stationSelectionnee.getLatitude() + "," + stationSelectionnee.getLongitude();
                try {
                    Desktop.getDesktop().browse(new URI(url));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        VBox root = new VBox(titre, champRecherche, filtreVeloCheck, comboArr, boutonExport, listView, boutonMaps);
        Scene scene = new Scene(root, 800, 550);
        stage.setScene(scene);
        stage.setTitle("Stations Vélib - Paris");
        stage.show();
    }

    private void majAffichageListView(String texte, boolean plusDe10, String arrondissementChoisi) {
        listView.getItems().clear();

        List<Station> resultat = toutesStations.stream()
            .filter(s -> s.getNom().toLowerCase().contains(texte.toLowerCase()))
            .filter(s -> !plusDe10 || s.getNbVelosDispo() > 10)
            .filter(s -> arrondissementChoisi.equals("Tous") || s.getArrondissement().equals(arrondissementChoisi))
            .collect(Collectors.toList());

        for (Station s : resultat) {
            String ligne = s.getNom() + " - " + s.getStatut()
                    + " | Vélos : " + s.getNbVelosDispo()
                    + " | Bornes : " + s.getNbBornesDispo()
                    + " | " + s.getArrondissement();
            listView.getItems().add(ligne);
        }

        if (listView.getItems().isEmpty()) {
            listView.getItems().add("Aucune station trouvée.");
        }
    }
}
