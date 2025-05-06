import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class VelibApp extends Application {

private List<Station> stations = new ArrayList<>();
private ListView<Station> listView;
private ComboBox<String> comboArrondissement;

public static void main(String[] args) {
launch(args);
}

@Override
public void start(Stage stage) {
stage.setTitle("Vélib – Liste des stations");

listView = new ListView<>();
listView.setOnMouseClicked(event -> {
if (event.getClickCount() == 2) {
Station station = listView.getSelectionModel().getSelectedItem();
if (station != null) {
showStationDetails(station);
}
}
});

comboArrondissement = new ComboBox<>();
comboArrondissement.getItems().add("Toutes les villes");
comboArrondissement.setOnAction(e -> filtrerParArrondissement());
comboArrondissement.getSelectionModel().selectFirst();

VBox root = new VBox(10, new Label("Stations Vélib : nom - arrondissement"), comboArrondissement, listView);
root.setPadding(new Insets(10));

Scene scene = new Scene(root, 600, 500);
stage.setScene(scene);
stage.show();

chargerStations();
}

private void showStationDetails(Station station) {
Alert alert = new Alert(Alert.AlertType.INFORMATION);
alert.setTitle("Détails de la station");
alert.setHeaderText(station.nom);
StringBuilder content = new StringBuilder();
content.append("Numéro : ").append(station.id).append("\n");
content.append("Ville : ").append(station.ville).append("\n");
content.append("Statut : ").append(station.statut).append("\n");
content.append("Borne de paiement : ").append(station.bornePaiement).append("\n");
content.append("Capacité : ").append(station.capacite).append("\n");
content.append("Vélos disponibles : ").append(station.velosDispo).append("\n");
content.append("Ports d'attache disponibles : ").append(station.portsDispo);
alert.setContentText(content.toString());
alert.showAndWait();
}

private void chargerStations() {
try {
String lien = "https://opendata.paris.fr/api/records/1.0/search/?dataset=velib-disponibilite-en-temps-reel&rows=2000";
HttpURLConnection conn = (HttpURLConnection) new URL(lien).openConnection();
conn.setRequestMethod("GET");

BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
String inputLine;
StringBuilder content = new StringBuilder();
while ((inputLine = in.readLine()) != null) content.append(inputLine);
in.close();

JSONObject json = new JSONObject(content.toString());
JSONArray records = json.getJSONArray("records");

Set<String> arrondissements = new TreeSet<>();
stations.clear();

for (int i = 0; i < records.length(); i++) {
JSONObject fields = records.getJSONObject(i).optJSONObject("fields");
if (fields == null) continue;

int id = fields.optInt("stationcode", 0);
String nom = fields.optString("name", "Inconnu");
String ville = fields.optString("nom_arrondissement_communes", "Inconnu");
String statut = fields.optString("status", "Inconnu");
String borne = fields.optString("banking", "Non renseignée");
int capacite = fields.optInt("capacity", -1);
int velos = fields.optInt("numbikesavailable", 0);
int ports = fields.optInt("numdocksavailable", 0);

Station station = new Station(id, nom, ville, statut, borne.equals("true") ? "Oui" : "Non", capacite >= 0 ? capacite : 0, velos, ports);
stations.add(station);
arrondissements.add(ville);
}

comboArrondissement.getItems().addAll(arrondissements);
updateListView(stations);

} catch (Exception e) {
e.printStackTrace();
}
}

private void filtrerParArrondissement() {
String selected = comboArrondissement.getValue();
if (selected == null || selected.equals("Toutes les villes")) {
updateListView(stations);
} else {
List<Station> filtered = new ArrayList<>();
for (Station s : stations) {
if (s.ville.equalsIgnoreCase(selected)) filtered.add(s);
}
updateListView(filtered);
}
}

private void updateListView(List<Station> list) {
listView.getItems().setAll(list);
}

static class Station {
int id;
String nom, ville, statut, bornePaiement;
int capacite, velosDispo, portsDispo;

Station(int id, String nom, String ville, String statut, String bornePaiement, int capacite, int velosDispo, int portsDispo) {
this.id = id;
this.nom = nom;
this.ville = ville;
this.statut = statut;
this.bornePaiement = bornePaiement;
this.capacite = capacite;
this.velosDispo = velosDispo;
this.portsDispo = portsDispo;
}

@Override
public String toString() {
return nom + " - " + ville;
}
}
}
