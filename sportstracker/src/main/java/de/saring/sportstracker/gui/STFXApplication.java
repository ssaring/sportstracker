package de.saring.sportstracker.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Dummy Application class for initialization of the JavaFX toolkit.
 * It's needed until the main SportsTracker window will be migrated to JavaFX,
 * then it will inherit the JavaFX Application class.
 *
 * @author Stefan Saring
 */
public class STFXApplication extends Application {

    private static STFXApplication instance;

    private Stage primaryStage;

    /**
     * Launches the dummy JavaFX Application. It must be executed asynchronously,
     * the method does not returns until the application is exited.
     */
    static void launchDummyApplication() {
        STFXApplication.launch();
    }

    /**
     * Returns the instance of the dummy JavaFX Application class.
     *
     * @return the Application instance
     */
    static STFXApplication getInstance() {
        return instance;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        STFXApplication.instance = this;
        this.primaryStage = primaryStage;

        // the dummy JavaFX window should be as invisible as possible
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setMaxWidth(1);
        primaryStage.setMaxHeight(1);
        primaryStage.centerOnScreen();

        // place dummy stage in center of screen, then the dialogs will be placed there too
        Platform.runLater(() -> primaryStage.centerOnScreen());

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
}
