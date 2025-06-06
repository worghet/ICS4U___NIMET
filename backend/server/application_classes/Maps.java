package backend.server.application_classes;

import java.util.HashMap;
import java.util.Map;


/**
 * Maps class handles the storage of all the maps.
 */
public class Maps {

    // Instance variables.

    Map<String, String> weatherMaps = new HashMap<>(); // currently just single 256x256px images
    // overlay image

    // saves the urls with a exit code (just json stuff.. should be fine: u003?)


    // will make this into a constructor later

    public void initializeMaps(String cloudyUrl, String temperatureUrl, String precipitationUrl, String pressureUrl) {
        weatherMaps.put("CLOUDS", cloudyUrl);
        weatherMaps.put("TEMPERATURE", temperatureUrl);
        weatherMaps.put("PRECIPITATION", precipitationUrl);
        weatherMaps.put("PRESSURE", pressureUrl);
    }

    // consider making "getMaps" static?

}
