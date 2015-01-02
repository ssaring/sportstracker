package de.saring.exerciseviewer.gui;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import de.saring.exerciseviewer.gui.panels.DiagramPanelController;
import de.saring.exerciseviewer.gui.panels.LapPanelController;
import de.saring.exerciseviewer.gui.panels.MainPanelController;
import de.saring.exerciseviewer.gui.panels.OptionalPanelController;
import de.saring.exerciseviewer.gui.panels.SamplePanelController;
import de.saring.exerciseviewer.gui.panels.TrackPanelController;
import de.saring.util.gui.javafx.FxmlLoader;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Main Controller (MVC) class of the ExerciseViewer dialog window.
 *
 * @author Stefan Saring
 */
@Singleton
public class EVController {

    private static final String FXML_FILE = "/fxml/ExerciseViewer.fxml";

    private final EVContext context;

    @Inject
    private MainPanelController mainPanelController;
    @Inject
    private OptionalPanelController optionalPanelController;
    @Inject
    private LapPanelController lapPanelController;
    @Inject
    private SamplePanelController samplePanelController;
    @Inject
    private DiagramPanelController diagramPanelController;
    @Inject
    private TrackPanelController trackPanelController;

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
     */
    @Inject
    public EVController(final EVContext context) {
        this.context = context;
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
            root = FxmlLoader.load(EVController.class.getResource(FXML_FILE), context.getFxResources()
                    .getResourceBundle(), this);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load the FXML resource '" + FXML_FILE + "'!", e);
        }

        setupPanels();

        // create scene and show dialog
        final Scene scene = new Scene(root);
        setCloseOnEscape(scene);
        stage.setScene(scene);

        // set minimum ExerciseViewer window size to the computed preferred size
        stage.setOnShown(event -> {
            stage.setMinWidth(stage.getWidth());
            stage.setMinHeight(stage.getHeight());
        });

        stage.show();
    }

    private void setupPanels() {

        tabMain.setContent(mainPanelController.loadAndSetupPanelContent());
        tabOptional.setContent(optionalPanelController.loadAndSetupPanelContent());
        tabLaps.setContent(lapPanelController.loadAndSetupPanelContent());
        tabSamples.setContent(samplePanelController.loadAndSetupPanelContent());
        tabDiagram.setContent(diagramPanelController.loadAndSetupPanelContent());
        tabTrack.setContent(trackPanelController.loadAndSetupPanelContent());

        // display exercise track not before the user wants to see it (prevent long startup delays)
        tabTrack.setOnSelectionChanged(event -> {
            if (tabTrack.isSelected()) {
                trackPanelController.showTrack();
            }
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
