package backend.server.application_classes;

// currently works for only "now"; no forcasting
public class Report {

    Location location;
    Weather weather;
    Maps maps;
    String metar;
    // Image of fronts in the area

    public Report(Location location, Weather weather, Maps maps) {
        this.weather = weather;
        this.location = location;
        this.maps = maps;
    }

}

