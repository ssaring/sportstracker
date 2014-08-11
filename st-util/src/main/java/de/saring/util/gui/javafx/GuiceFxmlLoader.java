package de.saring.util.gui.javafx;

import com.google.inject.Injector;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Enhances the JavaFX FXMLLoader for using Guice to inject the controller instances.
 *
 * @author Stefan Saring
 */
@Singleton
public class GuiceFxmlLoader {

    /** Guice Injector is needed for providing the controller instances. */
    private final Injector injector;

    /**
     * Standard c'tor.
     *
     * @param injector the Guice Injector
     */
    @Inject
    public GuiceFxmlLoader(final Injector injector) {
        this.injector = injector;
    }

    /**
     * Loads the scene object hierarchy from the specified FXML document. This is a wrapper
     * for the {@link FXMLLoader#load(URL, ResourceBundle)} method. The controller specified
     * in the FXML file will be provided by the Guice Injector.
     *
     * @param url URL of the FXML resource
     * @param resBundle the ResourceBundle to use for the FXML document
     * @return the loaded scene object hierarchy
     * @throws IOException
     */
    public Parent load(final URL url, final ResourceBundle resBundle) throws IOException {

        final FXMLLoader loader = new FXMLLoader();
        loader.setLocation(url);
        loader.setResources(resBundle);
        loader.setControllerFactory(controllerClass -> injector.getInstance(controllerClass));
        return (Parent) loader.load();
    }
}
