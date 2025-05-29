package backend.server.application_classes;

// currently works for only "now"; no forcasting
public class Report {

    Location location;
    Weather weather;
    String metar;
    // Image of fronts in the area

    public Report(Location location, Weather weather) {
        this.weather = weather;
        this.location = location;
    }

}

