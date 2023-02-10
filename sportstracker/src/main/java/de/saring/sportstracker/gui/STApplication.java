package de.saring.sportstracker.gui;

import java.util.logging.Level;
import java.util.logging.Logger;

import de.saring.exerciseviewer.core.EVOptions;
import de.saring.exerciseviewer.gui.EVContext;
import de.saring.sportstracker.core.STException;
import de.saring.sportstracker.gui.update.STUpdateChecker;
import de.saring.sportstracker.storage.IStorage;
import de.saring.sportstracker.storage.XMLStorage;
import de.saring.util.gui.javafx.WindowBoundsPersistence;
import de.saring.util.unitcalc.FormatUtils;
import eu.lestard.easydi.EasyDI;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

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
    private STUpdateChecker updateChecker;

    private Stage primaryStage;

    @Override
    public void init() throws Exception {

        // setup EasyDI for dependency injection
        var easyDI = new EasyDI();
        easyDI.bindInstance(STApplication.class, this);
        easyDI.bindInterface(IStorage.class, XMLStorage.class);
        easyDI.bindInterface(STContext.class, STContextImpl.class);
        easyDI.bindInterface(EVContext.class, STContextImpl.class);
        easyDI.bindInterface(STDocument.class, STDocumentImpl.class);
        easyDI.bindInterface(STController.class, STControllerImpl.class);

        // initialize the document and options
        document = easyDI.getInstance(STDocument.class);
        document.evaluateCommandLineParameters(getParameters().getRaw());
        document.loadOptions();

        var options = document.getOptions();
        easyDI.bindInstance(EVOptions.class, options);

        // initialize the context (set format utils for current configuration)
        context = easyDI.getInstance(STContext.class);
        context.setFormatUtils(new FormatUtils(options.getUnitSystem()));

        controller = easyDI.getInstance(STController.class);
        updateChecker = easyDI.getInstance(STUpdateChecker.class);
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
        document.getStorage().closeDatabase();
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

        triggerUpdateCheck();
    }

    /**
     * Triggers the check for SportsTracker application updates. The check is executed in background and waits a while
     * to avoid startup delays and user bothering.
     */
    private void triggerUpdateCheck() {

        final Thread updateCheckThread = new Thread(() -> {
            try {
                Thread.sleep(30 * 1000);
                updateChecker.checkForUpdates();
            }
            catch (InterruptedException ie) {
                LOGGER.log(Level.SEVERE, "Failed to sleep until executing update check!", ie);
            }
        });

        updateCheckThread.setDaemon(true);
        updateCheckThread.start();
    }

    /**
     * Starts the SportsTracker application.<br/>
     * Since JDK 11 the application can't be started directly with this class aynmore. Use the class
     * {@link de.saring.sportstracker.STMain} instead.
     *
     * @param args command line parameters
     */
    public static void main(final String args[]) {

        // set format for java.util.logging, a log statement must be printed to a single line
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$s %2$s %5$s %6$s%n");

        launch(args);
    }
}