package de.saring.util.gui.javafx;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/**
 * Util class with helper methods for loading FXML layouts.
 *
 * @author Stefan Saring
 */
public final class FxmlLoader {

    private FxmlLoader() {
    }

    /**
     * Loads the scene object hierarchy from the specified FXML document. This is a wrapper
     * for the {@link FXMLLoader#load(URL, ResourceBundle)} method. It uses the passed controller
     * instead of creating a new controller class specified in the FXML file. The passed controller
     * needs to be an instance of the same class.
     *
     * @param url URL of the FXML resource
     * @param resBundle the ResourceBundle to use for the FXML document
     * @param controller controller to be used for the loaded hierarchy
     *
     * @return the loaded scene object hierarchy
     * @throws IOException
     */
    public static Parent load(final URL url, final ResourceBundle resBundle, final Object controller)
            throws IOException {

        Objects.requireNonNull(url);
        Objects.requireNonNull(controller);

        final FXMLLoader loader = new FXMLLoader();
        loader.setLocation(url);
        loader.setResources(resBundle);

        // setController() can't be used here, because the controller class is already specified in FXML
        // (declaration in FXML is very helpful for validation and event handler selection in SceneBuilder)
        loader.setControllerFactory(controllerClass -> {
            if (controllerClass != null && !controllerClass.isInstance(controller)) {
                throw new IllegalArgumentException("Invalid controller instance, expecting instance of class '" +
                        controllerClass.getName() + "'!");
            }
            return controller;
        });

        return (Parent) loader.load();
    }
}
