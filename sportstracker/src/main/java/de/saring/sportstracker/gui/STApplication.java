package de.saring.sportstracker.gui;

import java.text.MessageFormat;
import java.util.logging.Logger;

import de.saring.exerciseviewer.gui.EVContext;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import de.saring.util.gui.javafx.FxmlLoader;

/**
 * This is the main class of SportsTracker which starts the entire application.
 *
 * @author Stefan Saring
 */
public class STApplication extends Application {

    private static final Logger LOGGER = Logger.getLogger(STApplication.class.getName());

    private Injector injector;

    private Stage primaryStage;

    @Override
    public void init() throws Exception {

        // setup the Guice injector
        injector = Guice.createInjector(new AbstractModule() {
            @Override
            public void configure() {
                bind(STApplication.class).toInstance(STApplication.this);

                bind(STContext.class).to(STContextImpl.class);
                bind(EVContext.class).to(STContextImpl.class);
                bind(STDocument.class).to(STDocumentImpl.class);
                bind(STController.class).to(STControllerImpl.class);
            }
        });

        // initialize the document
        final STDocument document = injector.getInstance(STDocument.class);
        document.evaluateCommandLineParameters(getParameters().getRaw());
        document.loadOptions();
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        // load and start the main application window
        final STController controller = injector.getInstance(STController.class);
        final STContext context = injector.getInstance(STContext.class);

        // TODO move this to STController?
        Parent root = FxmlLoader.load(STController.class.getResource("/fxml/SportsTracker.fxml"), //
                context.getFxResources().getResourceBundle(), controller);

        primaryStage.setScene(new Scene(root));

        primaryStage.setTitle(MessageFormat.format(
                "{0} {1}", //
                context.getFxResources().getString("application.title"),
                context.getFxResources().getString("application.version")));

        primaryStage.getIcons().addAll( //
                new Image("icons/st-logo-512.png"), //
                new Image("icons/st-logo-256.png"), //
                new Image("icons/st-logo-128.png"), //
                new Image("icons/st-logo-64.png"), //
                new Image("icons/st-logo-48.png"), //
                new Image("icons/st-logo-32.png"), //
                new Image("icons/st-logo-24.png"));

        // register listener for window close / application exit
        primaryStage.setOnCloseRequest(event -> controller.onWindowCloseRequest(event));
        primaryStage.show();
    }

    /**
     * Returns the primary Stage of the JavaFX application.
     *
     * @return Stage
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Starts the SportsTracker application.
     *
     * @param args command line parameters
     */
    public static void main(final String args[]) {
        launch(args);
    }
}
