package de.saring.exerciseviewer.gui

import java.io.IOException

import de.saring.util.SystemUtils
import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Tab
import javafx.stage.Stage

import de.saring.exerciseviewer.gui.panels.DiagramPanelController
import de.saring.exerciseviewer.gui.panels.LapPanelController
import de.saring.exerciseviewer.gui.panels.MainPanelController
import de.saring.exerciseviewer.gui.panels.OptionalPanelController
import de.saring.exerciseviewer.gui.panels.SamplePanelController
import de.saring.exerciseviewer.gui.panels.TrackPanelController
import de.saring.util.gui.javafx.FxmlLoader

/**
 * Main Controller (MVC) class of the ExerciseViewer dialog window.
 *
 * @constructor constructor for dependency injection
 * @param context the ExerciseViewer UI context
 * @param document the ExerciseViewer document / model
 *
 * @author Stefan Saring
 */
class EVController(
        private val context: EVContext,
        document: EVDocument) {

    private val fxmlFilename = "/fxml/ExerciseViewer.fxml"

    // manual dependency injection, Guice can't be used here (see comments in EVMain)
    private val mainPanelController = MainPanelController(context, document)
    private val optionalPanelController = OptionalPanelController(context, document)
    private val lapPanelController = LapPanelController(context, document)
    private val samplePanelController = SamplePanelController(context, document)
    private val diagramPanelController = DiagramPanelController(context, document)
    private val trackPanelController = TrackPanelController(context, document)

    private lateinit var stage: Stage

    @FXML
    private lateinit var tabMain: Tab
    @FXML
    private lateinit var tabOptional: Tab
    @FXML
    private lateinit var tabLaps: Tab
    @FXML
    private lateinit var tabSamples: Tab
    @FXML
    private lateinit var tabDiagram: Tab
    @FXML
    private lateinit var tabTrack: Tab

    /**
     * Initializes and displays the ExerciseViewer dialog.
     *
     * @param stage the Stage to show the dialog in
     */
    fun show(stage: Stage) {
        this.stage = stage

        // load dialog UI from FXML
        val root: Parent = try {
            FxmlLoader.load(EVController::class.java.getResource(fxmlFilename),
                    context.resources.resourceBundle, this)
        } catch (e: IOException) {
            throw RuntimeException("Failed to load the FXML resource '$fxmlFilename'!", e)
        }

        setupPanels()

        // create scene and show dialog
        val scene = Scene(root)
        stage.scene = scene
        stage.showAndWait()

        // trigger a garbage collection when EV has been closed to avoid allocation of additional heap space
        SystemUtils.triggerGC()
    }

    private fun setupPanels() {

        // load and setup main panel immediately, this tab must be visible on startup
        mainPanelController.diagramPanelController = diagramPanelController
        tabMain.content = mainPanelController.loadAndSetupPanelContent()

        // load all other panels asynchronously, this reduces the startup time massively
        Platform.runLater {
            tabOptional.content = optionalPanelController.loadAndSetupPanelContent()
            tabLaps.content = lapPanelController.loadAndSetupPanelContent()
            tabSamples.content = samplePanelController.loadAndSetupPanelContent()
            tabDiagram.content = diagramPanelController.loadAndSetupPanelContent()
            tabTrack.content = trackPanelController.loadAndSetupPanelContent()

            // display map and exercise track not before the user wants to see it (reduces startup time)
            tabTrack.setOnSelectionChanged { _ ->
                if (tabTrack.isSelected) {
                    trackPanelController.showMapAndTrack()
                }
            }
        }
    }

    /**
     * Action handler for closing the dialog.
     */
    @FXML
    private fun onClose(event: ActionEvent) = stage.close()
}
