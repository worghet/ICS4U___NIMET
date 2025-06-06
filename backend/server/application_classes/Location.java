package backend.server.application_classes;


/**
 * Location is to identify WHERE we are looking at the weather:
 * both it's physical coordinates (via longitude and latitude), as
 * well as its names: city, region, and country.
 */
public class Location {

    // Instance variables.

    String city_name, region_name, country_name; // ex. Ottawa, Ontario, Canada.
    double longitude, latitude; // ex -2.231234, 45.23123

    // Constructor.

    public Location(String city_name, String region_name, String country_name, double latitude, double longitude) {
        this.city_name = city_name;
        this.region_name = region_name;
        this.country_name = country_name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Other methods (felt cute, might delete later)

    public String toString() {
        return city_name + ", " + region_name + ", " + country_name + " (" + latitude + ", " + longitude + ")";
    }

    public String getCityName() {
        return city_name;
    }
}
