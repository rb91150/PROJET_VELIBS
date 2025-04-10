package vue;

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

public class Vue {

    private List<Station> toutesStations;
    private ListView<String> listView;
    private Station stationSelectionnee;

    public void afficher(Stage stage, List<Station> stations) {
        toutesStations = stations;

        Label titre = new Label("Stations Vélib (recherche, tri, export, Maps)");
        TextField champRecherche = new TextField();
        champRecherche.setPromptText("Rechercher une station...");

        CheckBox filtreVeloCheck = new CheckBox("Afficher seulement les stations avec +10 vélos dispo");
        Button boutonExport = new Button("Exporter en CSV");
        Button boutonMaps = new Button("Ouvrir la station sur Google Maps");

        listView = new ListView<>();
        majAffichageListView(toutesStations, "", false);

        champRecherche.textProperty().addListener((obs, oldVal, newVal) -> {
            majAffichageListView(toutesStations, newVal, filtreVeloCheck.isSelected());
        });

        filtreVeloCheck.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            majAffichageListView(toutesStations, champRecherche.getText(), isSelected);
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

        VBox root = new VBox(titre, champRecherche, filtreVeloCheck, boutonExport, listView, boutonMaps);
        Scene scene = new Scene(root, 700, 500);
        stage.setScene(scene);
        stage.setTitle("Stations Vélib (avec Google Maps)");
        stage.show();
    }

    private void majAffichageListView(List<Station> stations, String filtreTexte, boolean filtrePlusDe10Velos) {
        listView.getItems().clear();
        for (Station s : stations) {
            boolean nomOK = s.getNom().toLowerCase().contains(filtreTexte.toLowerCase());
            boolean veloOK = !filtrePlusDe10Velos || s.getNbVelosDispo() > 10;

            if (nomOK && veloOK) {
                String texte = s.getNom() + " - " + s.getStatut()
                        + " | Vélos : " + s.getNbVelosDispo()
                        + " | Bornes : " + s.getNbBornesDispo()
                        + " | (" + s.getLatitude() + ", " + s.getLongitude() + ")";
                listView.getItems().add(texte);
            }
        }

        if (listView.getItems().isEmpty()) {
            listView.getItems().add("Aucune station trouvée.");
        }
    }
}
