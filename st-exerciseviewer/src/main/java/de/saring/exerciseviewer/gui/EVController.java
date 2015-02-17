package de.saring.exerciseviewer.gui;

import java.io.IOException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import de.saring.exerciseviewer.gui.panels.DiagramPanelController;
import de.saring.exerciseviewer.gui.panels.LapPanelController;
import de.saring.exerciseviewer.gui.panels.MainPanelController;
import de.saring.exerciseviewer.gui.panels.OptionalPanelController;
import de.saring.exerciseviewer.gui.panels.SamplePanelController;
import de.saring.exerciseviewer.gui.panels.TrackPanelController;
import de.saring.util.gui.javafx.FxmlLoader;

/**
 * Main Controller (MVC) class of the ExerciseViewer dialog window.
 *
 * @author Stefan Saring
 */
public class EVController {

    private static final String FXML_FILE = "/fxml/ExerciseViewer.fxml";
    private final EVContext context;

    private final MainPanelController mainPanelController;
    private final OptionalPanelController optionalPanelController;
    private final LapPanelController lapPanelController;
    private final SamplePanelController samplePanelController;
    private final DiagramPanelController diagramPanelController;
    private final TrackPanelController trackPanelController;

    private Stage stage;

    @FXML
    private Tab tabMain;
    @FXML
    private Tab tabOptional;
    @FXML
    private Tab tabLaps;
    @FXML
    private Tab tabSamples;
    @FXML
    private Tab tabDiagram;
    @FXML
    private Tab tabTrack;

    /**
     * Standard c'tor for dependency injection.
     *
     * @param context the ExerciseViewer UI context
     * @param document the ExerciseViewer document / model
     */
    public EVController(final EVContext context, final EVDocument document) {
        this.context = context;

        // manual dependency injection, Guice can't be used here (see comments in EVMain)
        mainPanelController = new MainPanelController(context, document);
        optionalPanelController = new OptionalPanelController(context, document);
        lapPanelController = new LapPanelController(context, document);
        samplePanelController = new SamplePanelController(context, document);
        diagramPanelController = new DiagramPanelController(context, document);
        trackPanelController = new TrackPanelController(context, document);

        mainPanelController.setDiagramPanelController(diagramPanelController);
    }

    /**
     * Initializes and displays the ExerciseViewer dialog.
     *
     * @param stage the Stage to show the dialog in
     */
    public void show(final Stage stage) {
        this.stage = stage;

        // load dialog UI from FXML
        Parent root;
        try {
            root = FxmlLoader.load(EVController.class.getResource(FXML_FILE), context.getResources()
                    .getResourceBundle(), this);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load the FXML resource '" + FXML_FILE + "'!", e);
        }

        setupPanels();

        // register cleanup of JXMapViewer in TrackPanel on dialog close, otherwise there are memory leaks
        stage.addEventHandler(WindowEvent.WINDOW_HIDING, event -> trackPanelController.cleanupPanel());

        // create scene and show dialog
        final Scene scene = new Scene(root);
        setCloseOnEscape(scene);
        stage.setScene(scene);
        stage.show();
    }

    private void setupPanels() {
        // load and setup main panel immediately, this tab must be visible on startup
        tabMain.setContent(mainPanelController.loadAndSetupPanelContent());

        // load all other panels asynchronously, this reduces the startup time massively
        Platform.runLater(() -> {
            tabOptional.setContent(optionalPanelController.loadAndSetupPanelContent());
            tabLaps.setContent(lapPanelController.loadAndSetupPanelContent());
            tabSamples.setContent(samplePanelController.loadAndSetupPanelContent());
            tabDiagram.setContent(diagramPanelController.loadAndSetupPanelContent());
            tabTrack.setContent(trackPanelController.loadAndSetupPanelContent());

            // display exercise track not before the user wants to see it
            // (prevents layout problems and reduces startup time)
            tabTrack.setOnSelectionChanged(event -> {
                if (tabTrack.isSelected()) {
                    trackPanelController.showTrack();
                }
            });
        });
    }

    /**
     * Closes the dialog when the user presses the Escape key.
     *
     * @param scene dialog scene
     */
    private void setCloseOnEscape(final Scene scene) {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ESCAPE)) {
                onClose(null);
            }
        });
    }

    /**
     * Action handler for closing the dialog.
     */
    @FXML
    private void onClose(final ActionEvent event) {
        stage.close();
    }
}
