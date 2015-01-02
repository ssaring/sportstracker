package de.saring.util.gui.javafx;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/**
 * Enhances the JavaFX FXMLLoader for using Guice to inject the controller instances.
 *
 * @author Stefan Saring
 */
public final class GuiceFxmlLoader {

    private GuiceFxmlLoader() {
    }

    // TODO
    /**
     * Loads the scene object hierarchy from the specified FXML document. This is a wrapper
     * for the {@link FXMLLoader#load(URL, ResourceBundle)} method. The controller specified
     * in the FXML file will be provided by the Guice Injector.
     *
     * @param url URL of the FXML resource
     * @param resBundle the ResourceBundle to use for the FXML document
     *
     * @return the loaded scene object hierarchy
     * @throws IOException
     */
    public static Parent load(final URL url, final ResourceBundle resBundle, final Object controller) throws IOException {

        final FXMLLoader loader = new FXMLLoader();
        loader.setLocation(url);
        loader.setResources(resBundle);
        loader.setControllerFactory(controllerClass -> controller);
        return (Parent) loader.load();
    }
}
