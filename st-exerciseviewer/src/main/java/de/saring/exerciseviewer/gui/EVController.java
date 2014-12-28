package de.saring.exerciseviewer.gui;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import de.saring.exerciseviewer.gui.panelsfx.DiagramPanelController;
import de.saring.exerciseviewer.gui.panelsfx.LapPanelController;
import de.saring.exerciseviewer.gui.panelsfx.MainPanelController;
import de.saring.exerciseviewer.gui.panelsfx.OptionalPanelController;
import de.saring.exerciseviewer.gui.panelsfx.SamplePanelController;
import de.saring.exerciseviewer.gui.panelsfx.TrackPanelController;

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
     * @param document the ExerciseViewer model/document
     */
    public EVController(final EVContext context, final EVDocument document) {
        this.context = context;

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
            // Guice and so GuiceLoader can't be used here, see comments in EVMain
            // (otherwise there are multiple controller instances per view)
            final FXMLLoader loader = new FXMLLoader();
            loader.setLocation(EVController.class.getResource(FXML_FILE));
            loader.setResources(context.getFxResources().getResourceBundle());
            loader.setControllerFactory(controllerClass -> this);
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load the FXML resource '" + FXML_FILE + "'!", e);
        }

        setupPanels();

        // create scene and show dialog
        final Scene scene = new Scene(root);
        setCloseOnEscape(scene);
        stage.setScene(scene);
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
