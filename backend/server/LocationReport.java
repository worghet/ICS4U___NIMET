package backend.server;

public class LocationReport {

    Location location;
    WeatherReport weather;
    String metar;
    // Image of fronts in the area

}

class WeatherReport {

    double deg_c;
    double deg_f;

    double feels_like_c;
    double feels_like_f;

    WeatherReport exportInImperial() {
        return new WeatherReport();
    }

    WeatherReport exportInMetric() {
        return new WeatherReport();
    }

}

class Location {
    double longitude;
    double latitude;
}
