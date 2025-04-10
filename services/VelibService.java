package services;

import modele.Station;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

public class VelibService {

    public static ArrayList<Station> getStations() {
        ArrayList<Station> stations = new ArrayList<>();
        try {
            // Appel à l'API Vélib avec plus de résultats
            URL url = new URL("https://opendata.paris.fr/api/records/1.0/search/?dataset=velib-disponibilite-en-temps-reel&rows=1000");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            // Debug : affiche le JSON brut dans le terminal
            // System.out.println(content.toString());

            JSONObject json = new JSONObject(content.toString());
            JSONArray records = json.getJSONArray("records");

            for (int i = 0; i < records.length(); i++) {
                JSONObject record = records.getJSONObject(i);
                if (!record.has("fields")) continue;

                JSONObject fields = record.getJSONObject("fields");

                String nom = fields.optString("nom_station", "Nom inconnu");
                String statut = fields.optString("station_state", "N/A");
                int nbVelos = fields.optInt("nbvelosdisponibles", fields.optInt("nbvelosdispo", 0));
                int nbBornes = fields.optInt("nbplacesdisponibles", fields.optInt("nbplacesdispo", 0));

                // Si les noms sont vides dans certains cas
                if (nom.equals("")) nom = "Nom inconnu";

                stations.add(new Station(nom, statut, nbVelos, nbBornes));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stations;
    }
}
