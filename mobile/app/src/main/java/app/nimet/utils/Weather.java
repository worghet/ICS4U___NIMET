package app.nimet.utils;

/**
 * Weather data-class; same as the one in backend, but simplified to only be parsed by {@code Gson}.
 * Stores key data like {@code temperatures}, {@code air properties}, etc.
 */
public class Weather {


    // Class constants.


    // Unit indexes.
    final public static int METRIC = 0;
    final public static int IMPERIAL = 1;

    // This is used in other classes to format the data correctly.
    // Could have used a hashmap of sorts, but it was 2 in the morning I was lazy.
    final public static String[][] UNIT_SYNTAX = {{"°C", "mb", "kmh", "km"}, {"°F", "inHg", "mph", "mi"}};

    // Property indexes.
    final public static int TEMP = 0;
    final public static int PRESSURE = 1;
    final public static int SPEED = 2;
    final public static int DISTANCE = 3;


    // Instance variables.


    // Is it day?
    boolean isDay;


    // The different temperatures.
    double[] temperature = new double[2];
    double[] feels_like = new double[2];
    double[] dew_point = new double[2];


    // Wind properties.
    double[] wind_speed = new double[2];
    String wind_heading;
    double wind_degree;


    // Conditions ("cloudy", "sunny", "clear", etc).
    String condition;
    String icon_source;


    // Air pressure.
    double[] air_pressure = new double[2];


    // Humidity (%).
    double humidity;


    // Cloud coverage (%)
    double cloud_coverage;

    // Visibility.
    double[] visibility = new double[2];
    // double[] ceiling = new double[2]; // Something I wanted to add..


    // Getter methods.


    public String getCondition() {
        return condition;
    }

    public double[] getTemperature() {
        return temperature;
    }

    public double[] getFeels_like() {
        return feels_like;
    }

    public double[] getDew_point() {
        return dew_point;
    }

    public double[] getWind_speed() {
        return wind_speed;
    }

    public String getWind_heading() {
        return wind_heading;
    }

    public double getWind_degree() {
        return wind_degree;
    }

    public String getIcon_source() {
        return icon_source;
    }

    public double[] getAir_pressure() {
        return air_pressure;
    }

    public double getHumidity() {
        return humidity;
    }

    public double getCloud_coverage() {
        return cloud_coverage;
    }

    public double[] getVisibility() {
        return visibility;
    }


}