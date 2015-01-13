package de.saring.sportstracker.gui;

import java.util.logging.Level;
import java.util.logging.Logger;

import de.saring.util.gui.javafx.WindowBoundsPersistence;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import de.saring.exerciseviewer.gui.EVContext;
import de.saring.sportstracker.core.STException;
import de.saring.sportstracker.core.STOptions;
import de.saring.util.unitcalc.FormatUtils;

/**
 * This is the main class of SportsTracker which starts the entire application.
 *
 * @author Stefan Saring
 */
public class STApplication extends Application {

    private static final Logger LOGGER = Logger.getLogger(STApplication.class.getName());

    private STDocument document;
    private STContext context;
    private STController controller;

    private Stage primaryStage;

    @Override
    public void init() throws Exception {

        // setup the Guice injector
        final Injector injector = Guice.createInjector(new AbstractModule() {
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
        document = injector.getInstance(STDocument.class);
        document.evaluateCommandLineParameters(getParameters().getRaw());
        document.loadOptions();

        // initialize the context (set format utils for current configuration)
        context = injector.getInstance(STContext.class);
        final STOptions options = document.getOptions();
        context.setFormatUtils(new FormatUtils(options.getUnitSystem(), options.getSpeedView()));

        controller = injector.getInstance(STController.class);
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        WindowBoundsPersistence.addWindowBoundsPersistence(primaryStage, "SportsTracker");

        // initialize and start the main application window
        controller.initApplicationWindow();

        primaryStage.setOnShown(this::onShown);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        document.storeOptions();
        LOGGER.info("Exiting application...");
        super.stop();
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
     * This method is called after the application window has been shown, final UI setup can be done here.
     *
     * @param event window event
     */
    private void onShown(final WindowEvent event) {

        // create application directory
        try {
            document.createApplicationDirectory();
        } catch (STException se) {
            LOGGER.log(Level.SEVERE, "Failed to create the application directory!", se);
            context.showMessageDialog(primaryStage, Alert.AlertType.ERROR, "common.error", "st.main.error.create_dir");
        }

        // load application data (executed in background)
        controller.loadApplicationData();
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
