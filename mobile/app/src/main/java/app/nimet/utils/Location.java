package app.nimet.utils;

/**
 * This is just the location data-class; same as the one in backend, but simplified to only be parsed by {@code Gson}.
 * Stores key data like {@code city_name}, {@code region_name}, and {@code country_name}.
 */
public class Location {


    // Instance variables.


    String city_name, region_name, country_name;
    double longitude, latitude;


    // Getter methods.


    public String getCityName() {
        return city_name;
    }


    public String getCountryName() {
        return country_name;
    }


}