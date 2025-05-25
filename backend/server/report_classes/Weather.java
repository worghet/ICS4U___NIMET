package backend.server.report_classes;

// can be immediate, can be forcasted weather?
public class Weather {

    // units constants
    final int METRIC = 0;
    final int IMPERIAL = 1;
//    final int NAUTICAL = 2;


    public void setTemperatures(double temp_c, double temp_f,
                                double feels_like_c, double feels_like_f,
                                double dew_point_c, double dew_point_f) {

        temperature[METRIC] = temp_c;
        temperature[IMPERIAL] = temp_f;

        feels_like[METRIC] = feels_like_c;
        feels_like[IMPERIAL] = feels_like_f;

        dew_point[METRIC] = dew_point_c;
        dew_point[IMPERIAL] = dew_point_f;

    }

    public void setAirProperties(double humidity,
                                 double pressure_mb, double pressure_hg) {

        this.humidity = humidity;
        air_pressure[METRIC] = pressure_mb;
        air_pressure[IMPERIAL] = pressure_hg;

    }

    public void setWind(double speed_kph, double speed_mph,
                        String heading, double degree) {

        wind_speed[METRIC] = speed_kph;
        wind_speed[IMPERIAL] = speed_mph;
        wind_heading = heading;
        wind_degree = degree;

    }

    public void setDayNight(int isDay) {
        this.isDay = (isDay == 1);
    }

    public void setConditions(double cloud_coverage,
                              String condition_text, String condition_icon_url,
                              double visibility_km, double visibility_mi) {

        this.cloud_coverage = cloud_coverage;
        condition = condition_text;
        icon_source = condition_icon_url;
        visibility[METRIC] = visibility_km;
        visibility[IMPERIAL] = visibility_mi;



    }//--> coverage, etc.. (maybe expected clouds?)

    boolean isDay;

    // temperature

    double[] temperature = new double[2];
    double[] feels_like = new double[2];
    double[] dew_point = new double[2];

    // winds

    double[] wind_speed = new double[2];
    String wind_heading;
    double wind_degree;

    // conditions ("cloudy", "sunny", "clear", etc)
    String condition;
    String icon_source;

    // pressure
    double[] air_pressure = new double[2];

    // humidity (%)
    double humidity;

    // cloud coverage (%)
    double cloud_coverage;

    // visibility
    double[] visibility = new double[2];
    // double[] ceiling = new double[2];

    public String toString() {
        return "DAY? " + isDay + "\n" +
                "TEMP --> " + temperature[METRIC] + "c (" + feels_like[METRIC] + ") || " + temperature[IMPERIAL] + "f (" + feels_like[IMPERIAL] + ")\n" +
                "DEWP --> " + dew_point[METRIC] + "c || " + dew_point[IMPERIAL] + "f\n" +
                "AIR  --> " + air_pressure[METRIC] + "mb || " + air_pressure[IMPERIAL] + "in_hg <> humidity " + humidity + "%\n" +
                "WIND --> " + wind_speed[METRIC] + "kph || " + wind_speed[IMPERIAL] + "mph --> " + wind_heading + " (" + wind_degree + "deg)\n" +
                "VIS  --> " + visibility[METRIC] + "km || " + visibility[IMPERIAL] + "mi\n" +
                "COND --> " + condition + " (icon source: " + icon_source + ")";
    }


}
