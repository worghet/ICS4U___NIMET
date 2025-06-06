package backend.server.application_classes;

/** This is the composed image object from the Unsplash API.
 *  Ideally, it should have title and author as well, but there's some additional
 *  stuff I would need to check (null/non-existent).
 */
public class BackgroundImage {

    // Instance variables.
    String url;

    // Currently don't work, because of how unsplash api handles non-existing fields.

    String title;
    String author;

    // Constructor.

    public BackgroundImage(String url) {
        this.url = url;
    }
}
