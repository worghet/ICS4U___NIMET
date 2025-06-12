package backend.server;

// == IMPORTS ===========================
import backend.server.application_classes.*;
import com.sun.net.httpserver.HttpServer;
import com.google.gson.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Random;

// == NIMET SERVER =======
public class NimetServer {


    // ----------------------------------------------------------------


    // put this into a main class?
    // gonna run this on aws vm
    public static void main(String[] args) {

        // Create custom server object.
        NimetServer server = new NimetServer();

        // Initialize and start the server.

        try {

            // Initialize.
            System.out.print("Initializing NIMET server.. ");
            server.initialize();
            System.out.println("OK");

            // Start.
            System.out.print("Starting server.. ");
            server.start();
            System.out.println("OK");

        }
        catch (Exception e) {
            System.out.println("err --> " + e.getMessage());
        }


    }


    // ----------------------------------------------------------------


    HttpServer nimetServer; // actual httpserver
    final String LOCATION_DATA_ENDPOINT = "/weather"; // endpoint to get data for client
    final String CLOUD_MODEL_ENDPOINT = "/cloud_prediction";
    final String DATA_KEY = "886ffde491f64aff9fb135114252005"; // yes yes dont yell at me; i know that my api key is public.. actually..
    final String MAPS_KEY = "c40397ad2c12ec9c407793d6bd255171"; // for open meteo
    final int PORT = 8000; // ex 8000
    final String SERVER_SERVER_URL = "http://18.218.44.44:" + PORT; // just where to connect to (not using rn but will need to)
    final HttpClient CLIENT = HttpClient.newHttpClient(); // client that we use to get api data
    final Gson gson = new Gson(); // my favourite <3 --> used for jsonifying and dejsonifying stuff


    // ----------------------------------------------------------------


    /**
     * The {@code initialize()} method initializes the custom server.
     * @throws Exception If the requested port (8000 by default) is in use.
     */
    void initialize() throws Exception {

        // Ensure port is not occupied.

        try (ServerSocket demoServer = new ServerSocket(PORT)){}
        catch (Exception e) { throw new Exception("port is taken"); }

        // Default server setup.

        nimetServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        setupAPIs();

    }

    /**
     * Starts both the server, and the hourly fetching retriever.
     */
    void start() {
        nimetServer.start();
        System.out.println("go here: " + SERVER_SERVER_URL + "\nadd query of \"?location={whatever u want}\" at the end\n\n\n\n\nserver logs:\n");
    }

    void setupAPIs() {

        // make an endpoint for LOCATION_DATA_ENDPOINT (which is /weather) !!! This is a GET request
        nimetServer.createContext(LOCATION_DATA_ENDPOINT, exchange -> {


            // if GET .equals exchange.getRequestMethod()
            // get query (q="")

            String rawQuery = exchange.getRequestURI().getQuery(); // comes in the form of "q=[QUERY]"

            // assert that the value entered the query is done so properly to respect of url

            if ((rawQuery == null) || !rawQuery.startsWith("location=")) {

                System.out.println("bad request twin");
                // return status 400 (bad request)
            }

            // get the actual query

            String requestedLocation = rawQuery.substring(9);
            System.out.println("get me data from: " + requestedLocation);


            // 1. get and formate the DATA

            // get the data object (pure extracted data)
            JsonObject WEATHER_DATA = getWeatherJSON(requestedLocation);


            // save the locationdata, and use it location data to build a location object
            JsonObject locationData = WEATHER_DATA.getAsJsonObject("location");
            Location location = new Location(locationData.get("name").getAsString(),
                    locationData.get("region").getAsString(),
                    locationData.get("country").getAsString(),
                    locationData.get("lat").getAsDouble(),
                    locationData.get("lon").getAsDouble());


            // Save the currentweather data as its own object (easier to work with)
            JsonObject currentWeatherData = WEATHER_DATA.getAsJsonObject("current");

            // create new weather object
            Weather weather = new Weather();

            // set temperatures
            weather.setTemperatures(currentWeatherData.get("temp_c").getAsDouble(), currentWeatherData.get("temp_f").getAsDouble(),
                    currentWeatherData.get("feelslike_c").getAsDouble(), currentWeatherData.get("feelslike_f").getAsDouble(),
                    currentWeatherData.get("dewpoint_c").getAsDouble(), currentWeatherData.get("dewpoint_f").getAsDouble());

            // set air properties
            weather.setAirProperties(currentWeatherData.get("humidity").getAsDouble(),
                    currentWeatherData.get("pressure_mb").getAsDouble(), currentWeatherData.get("pressure_in").getAsDouble());
//
             // set wind properties
            weather.setWind(currentWeatherData.get("wind_kph").getAsDouble(), currentWeatherData.get("wind_mph").getAsDouble(),
                    currentWeatherData.get("wind_dir").getAsString(), currentWeatherData.get("wind_degree").getAsDouble());
//
             // affirm whether its day or night
            weather.setDayNight(currentWeatherData.get("is_day").getAsInt());
//
             // set the conditions
            weather.setConditions(currentWeatherData.get("cloud").getAsDouble(),
                    currentWeatherData.getAsJsonObject("condition").get("text").getAsString(), currentWeatherData.getAsJsonObject("condition").get("icon").getAsString(),
                    currentWeatherData.get("vis_km").getAsDouble(), currentWeatherData.get("vis_miles").getAsDouble());

            // make the maps, and make it as a
            Maps maps = getMaps(requestedLocation);

            // testing, will remove later
            System.out.println("we are reporting -----\n" + location);
            System.out.println("dis weather -----\n"+weather);

            Report report = new Report(location, weather, maps);





            // 2. fetch an image to use as a background from Unplash


            // get raw data
            JsonObject IMAGE_DATA = getImageFromUnsplashAPI(location.getCityName());

            // save just the url (might do title and user later)
            BackgroundImage backgroundImage = new BackgroundImage(IMAGE_DATA.getAsJsonObject("urls").get("full").getAsString());
            System.out.println("bg image made");






            //3. assemble the response

            ApplicationObject assembledResponse = new ApplicationObject(report, backgroundImage);

            // build response (Jsonify appobject)

            String response = gson.toJson(assembledResponse);
            System.out.println("Response: " + response); // Log the response for testing

            exchange.getResponseHeaders().add("Content-Type", "text/plain");
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.sendResponseHeaders(200, response.getBytes().length); //response.getBytes().length
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();



        });


        // make an endpoint for CLOUD_RECONGNITION_ENDPOINT (which is /prediction) !!! This is a POST request
        nimetServer.createContext(CLOUD_MODEL_ENDPOINT, exchange -> {

            // get the image (encoded in base64)
            InputStream inputStream = exchange.getRequestBody();

            // this is in the form of a json.
            String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            String imageBase64 = gson.fromJson(requestBody, JsonObject.class).get("encoded_image").getAsString();

            // decode base64



            //------------------------------------

            // put it through the feeder

            // return the String of the response. (if cloud, then with a small description? Or just an index (0, 1, 2, etc))


        });


    }

    // will ensure city exists beforehand
    JsonObject getWeatherJSON(String cityName) {

        try {

            // build the request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://api.weatherapi.com/v1/current.json?key=" + DATA_KEY +  "&q=" + cityName))
                    .GET()
                    .build();

            // send the request
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            // if all good, return the content
            if (response.statusCode() == 200) {
                return JsonParser.parseString(response.body()).getAsJsonObject();
            }

        }
        catch (Exception e) {}

        return null;

    }
    JsonObject getImageFromUnsplashAPI(String query) {
        try {
            query = query.replaceAll(" ", "%20");

            String url = "https://api.unsplash.com/search/photos?page=1&per_page=5&query=" + query + "&client_id=0OZ18srl8waYPbUfdk824oOdxpfDSYUFwEzQ5sYMiJQ";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
                JsonArray results = root.getAsJsonArray("results");

                if (results != null && !results.isEmpty()) {
                    int count = Math.min(results.size(), 15);
                    int randomIndex = new Random().nextInt(count);
                    return results.get(randomIndex).getAsJsonObject();
                }
            }

        } catch (Exception e) {
            System.out.println("Error fetching image from Unsplash: " + e.getMessage());
        }

        return null;
    }

    // TODO
    Maps getMaps(String cityName) {

        Maps fetchedMaps = new Maps();

        // === Tile coordinates ** will configure to the coordinates of user.
        int z = 5;       // Zoom level
        int x = 10;      // Tile X
        int y = 12;      // Tile Y

        String base = "https://tile.openweathermap.org/map/";
        String appid = "?appid=" + MAPS_KEY;

        // === URLs for each layer ===
        String clouds = base + "clouds_new/" + z + "/" + x + "/" + y + ".png" + appid;
        String temp = base + "temp_new/" + z + "/" + x + "/" + y + ".png" + appid;
        String precip = base + "precipitation_new/" + z + "/" + x + "/" + y + ".png" + appid;
        String pressure = base + "pressure_new/" + z + "/" + x + "/" + y + ".png" + appid;

        // === Initialize maps ===
        fetchedMaps.initializeMaps(clouds, temp, precip, pressure);

        return fetchedMaps;
    }


}
