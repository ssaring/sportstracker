package de.saring.sportstracker.gui;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Dummy Application class for initialization of the JavaFX toolkit.
 * It's needed until the main SportsTracker window will be migrated to JavaFX,
 * then it will inherit the JavaFX Application class.
 *
 * @author Stefan Saring
 */
public class STFXApplication extends Application {

    public static void launchDummyApplication() {
        STFXApplication.launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        // the dummy JavaFX window should be as invisible as possible
        primaryStage.setMaxWidth(0);
        primaryStage.setMaxHeight(0);
        primaryStage.show();
    }
}
