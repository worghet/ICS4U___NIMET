package backend.server;

// == IMPORTS ===========================
import com.sun.net.httpserver.HttpServer;
import java.net.ServerSocket;

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
            System.out.println("err -->" + e.getMessage());
        }

        // Start server.
        server.start();

    }


    // ----------------------------------------------------------------


    HttpServer nimetServer;
    Thread meteoDataRetriever;
    final int port = 8000; // ex 8000
    final String URL = "http://18.218.44.44:" + port; // just where to connect to


    // ----------------------------------------------------------------


    /**
     * The {@code initialize()} method initializes the custom server.
     * @throws Exception If the requested port (8000 by default) is in use.
     */
    void initialize() throws Exception {

        // Ensure port is not occupied.

        try (ServerSocket demoServer = new ServerSocket(port)){}
        catch (Exception e) { throw new Exception("port is taken"); }

        // Default server setup.

        nimetServer = HttpServer.create();
        setupAPIs();

        // Initialize the thread that gathers data.

        setupMeteoRetrieverThread();

    }

    /**
     * Starts both the server, and the hourly fetching retriever.
     */
    void start() {
//        nimetServer.start();
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
//        nimetServer.createContext()
//        nimetServer.createContext()
//        nimetServer.createContext()
//        nimetServer.createContext()
//        nimetServer.createContext()
    }

}
