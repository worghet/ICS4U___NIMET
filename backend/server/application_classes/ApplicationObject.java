package backend.server.application_classes;

/**
 * This is what will be sent through the GET request.
 */
public class ApplicationObject {

    // Instance variables.

    Report report;
    BackgroundImage backgroundImage;

    // Constructor.

    public ApplicationObject(Report report, BackgroundImage backgroundImage) {

        this.report = report;
        this.backgroundImage = backgroundImage;

    }

}
