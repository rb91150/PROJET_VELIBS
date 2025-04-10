package modele;

public class Station {
    private String nom;
    private String statut;
    private int nbVelosDispo;
    private int nbBornesDispo;
    private String arrondissement;
    private double latitude;
    private double longitude;

    public Station(String nom, String statut, int nbVelosDispo, int nbBornesDispo, String arrondissement, double latitude, double longitude) {
        this.nom = nom;
        this.statut = statut;
        this.nbVelosDispo = nbVelosDispo;
        this.nbBornesDispo = nbBornesDispo;
        this.arrondissement = arrondissement;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getNom() { return nom; }
    public String getStatut() { return statut; }
    public int getNbVelosDispo() { return nbVelosDispo; }
    public int getNbBornesDispo() { return nbBornesDispo; }
    public String getArrondissement() { return arrondissement; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }

    public String getGoogleMapsLink() {
        return "https://www.google.com/maps/search/?api=1&query=" + latitude + "," + longitude;
    }

    @Override
    public String toString() {
        return nom + " - " + statut + " | Vélos : " + nbVelosDispo + " | Bornes : " + nbBornesDispo;
    }
}
