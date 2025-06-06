package backend.server.application_classes;


/**
 * Stores all the data a client would need to function properly; including
 * location,
 * weather data, and
 * maps.
 */
public class Report {

    // Instance variables.

    Location location;
    Weather weather;
    Maps maps;

    // Might do metar if i want to later.

    String metar;

    // Constructor.

    public Report(Location location, Weather weather, Maps maps) {
        this.weather = weather;
        this.location = location;
        this.maps = maps;
    }

}

