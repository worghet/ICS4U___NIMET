package backend.server.application_classes;

public class Location {

    String city_name, region_name, country_name;
    double longitude, latitude;

    public Location(String city_name, String region_name, String country_name, double latitude, double longitude) {
        this.city_name = city_name;
        this.region_name = region_name;
        this.country_name = country_name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String toString() {
        return city_name + ", " + region_name + ", " + country_name + " (" + latitude + ", " + longitude + ")";
    }

    public String getCityName() {
        return city_name;
    }
}
