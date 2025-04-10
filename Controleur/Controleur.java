package controleur;

import modele.Station;
import services.VelibService;

import java.util.List;

public class Controleur {

    public List<Station> chargerStations() {
        return VelibService.getStations();
    }
}
