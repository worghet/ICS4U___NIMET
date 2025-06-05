package backend.server.application_classes;

import java.util.HashMap;
import java.util.Map;

public class Maps {

    Map<String, String> weatherMaps = new HashMap<>(); // currently just single 256x256px images
    // overlay image

    // saves the urls with a exit code (just json stuff.. should be fine: u003?)

    public void initializeMaps(String cloudyUrl, String temperatureUrl, String precipitationUrl, String pressureUrl) {
        weatherMaps.put("CLOUDS", cloudyUrl);
        weatherMaps.put("TEMPERATURE", temperatureUrl);
        weatherMaps.put("PRECIPITATION", precipitationUrl);
        weatherMaps.put("PRESSURE", pressureUrl);
    }


}
