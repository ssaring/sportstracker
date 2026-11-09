package de.saring.exerciseviewer.gui.panels

import de.saring.exerciseviewer.gui.EVContext
import de.saring.exerciseviewer.gui.EVDocument
import de.saring.util.gui.javafx.FxmlLoader
import java.io.IOException
import javafx.scene.Parent

/**
 * Abstract Controller (MVC) base class of for all ExerciseViewer panels.
 *
 * @param context the ExerciseViewer UI context
 * @param document the ExerciseViewer document / model
 * @constructor constructor for dependency injection
 * @author Stefan Saring
 */
abstract class AbstractPanelController(
    protected val context: EVContext,
    protected val document: EVDocument,
) {

  /**
   * Loads the panel content from FXML layout file and set up all the controls to shows the exercise data.
   *
   * @return the loaded and initialized panel content
   */
  fun loadAndSetupPanelContent(): Parent {
    val fxmlFilename = fxmlFilename

    val root: Parent =
        try {
          FxmlLoader.load(this.javaClass.getResource(fxmlFilename), context.resources.resourceBundle, this)
        } catch (e: IOException) {
          throw RuntimeException("Failed to load the FXML resource '$fxmlFilename'!", e)
        }

    setupPanel()
    return root
  }

  /**
   * Returns the name of the FXML file which contains the panel UI definition.
   *
   * @return FXML filename
   */
  protected abstract val fxmlFilename: String

  /**
   * Sets up all the panel controls and displays the exercise data. Will be called after the UI has been loaded from
   * FXML.
   */
  protected abstract fun setupPanel()
}
