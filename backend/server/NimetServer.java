package backend.server;

// == IMPORTS ===========================
import com.sun.net.httpserver.HttpServer;
import com.google.gson.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

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


    HttpServer nimetServer;
    Thread meteoDataRetriever; // TODO probably remove dis
    final String LOCATION_DATA_API = "/weather";
    final String WEATHER_API = "http://api.weatherapi.com/v1/current.json?key=&q="; // yes yes dont yell at me; i know that my api key is public.. actually..
    final int PORT = 8000; // ex 8000
    final String URL = "http://18.218.44.44:" + PORT; // just where to connect to
    final HttpClient CLIENT = HttpClient.newHttpClient();
    final Gson gson = new Gson();


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

        // Initialize the thread that gathers data.

        setupMeteoRetrieverThread();

    }

    /**
     * Starts both the server, and the hourly fetching retriever.
     */
    void start() {
        nimetServer.start();
        System.out.println("go here: " + URL + "\nadd query of \"?location={whatever u want}\" at the end\n\n\n\n\nserver logs:\n");
//        meteoDataRetriever.start();
    }

    /**
     * Sets up the data retriever. Relies on other APIs to do so.
     */
    void setupMeteoRetrieverThread() {
        meteoDataRetriever = new Thread(() -> {

            // CurrentWeather data is updated here by calling on bunch of other apis

        });
    }

    void setupAPIs() {
        nimetServer.createContext(LOCATION_DATA_API, exchange -> {

            // get query (q=

            String rawQuery = exchange.getRequestURI().getQuery(); // comes in the form of "q=[QUERY]"

            System.out.println(exchange.getRequestURI());
            // make sure
            if (rawQuery != null && rawQuery.startsWith("location=")) {
                String requestedLocation = rawQuery.substring(9);
                System.out.println("get me data from: " + requestedLocation);

                // ensure name is valid here?

                System.out.println("json object making");
                JsonObject weatherData = getWeatherJSON(requestedLocation);

                JsonObject currentWeatherData = weatherData.getAsJsonObject("current");
                System.out.println("temp in celcius = " + currentWeatherData.get("temp_c").getAsDouble());
                
                exchange.sendResponseHeaders(200, -1);
            }
            else {
                System.out.println("bad request twin");
                // return status 400 (bad request)
            }




        });
    }

    // will ensure city exists beforehand
    JsonObject getWeatherJSON(String cityName) {

        try {

            // build the request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://api.weatherapi.com/v1/current.json?key=886ffde491f64aff9fb135114252005&q=" + cityName))
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
}
