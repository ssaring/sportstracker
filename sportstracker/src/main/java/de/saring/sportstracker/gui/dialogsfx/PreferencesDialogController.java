package de.saring.sportstracker.gui.dialogsfx;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.stage.Window;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.saring.sportstracker.core.STOptions;
import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STDocument;
import de.saring.util.gui.javafx.GuiceFxmlLoader;

/**
 * Controller (MVC) class of the Preferences dialog for editing the application preferences / options.
 *
 * @author Stefan Saring
 */
@Singleton
public class PreferencesDialogController extends AbstractDialogController {

    private final STDocument document;

    @FXML
    private RadioButton rbInitialViewCalendar;
    @FXML
    private RadioButton rbInitialViewExerciseList;
    @FXML
    // TODO specify type
    private ChoiceBox<?> cbAutomaticCalculation;
    @FXML
    private CheckBox cbSaveOnExit;

    @FXML
    private RadioButton rbUnitsMetric;
    @FXML
    private RadioButton rbUnitsEnglish;
    @FXML
    private RadioButton rbSpeedUnitDistance;
    @FXML
    private RadioButton rbSpeedUnitMinutes;
    @FXML
    private RadioButton rbWeekStartMonday;
    @FXML
    private RadioButton rbWeekStartSunday;

    @FXML
    private CheckBox cbOptionalAvgHeartrate;
    @FXML
    private CheckBox cbOptionalAscent;
    @FXML
    private CheckBox cbOptionalEnergy;
    @FXML
    private CheckBox cbOptionalEquipment;
    @FXML
    private CheckBox cbOptionalComment;

    @FXML
    private CheckBox cbDiagramTwoGraphs;

    /** ViewModel of the edited options. */
    private PreferencesViewModel preferencesViewModel;

    /**
     * Standard c'tor for dependency injection.
     *
     * @param context the SportsTracker UI context
     * @param document the SportsTracker model/document
     * @param guiceFxmlLoader the Guice FXML loader
     */
    @Inject
    public PreferencesDialogController(final STContext context, final STDocument document,
            final GuiceFxmlLoader guiceFxmlLoader) {
        super(context, guiceFxmlLoader);
        this.document = document;
    }

    /**
     * Displays the Preferences dialog.
     *
     * @param parent parent window of the dialog
     */
    public void show(final Window parent) {
        this.preferencesViewModel = new PreferencesViewModel(document.getOptions());

        showEditDialog("/fxml/PreferencesDialog.fxml", parent,
                context.getFxResources().getString("st.dlg.options.title"));
    }

    @Override
    protected void setupDialogControls() {

        // setup binding between view model and the UI controls
        // TODO dpDate.valueProperty().bindBidirectional(noteViewModel.date);

        // validation is not needed here
    }

    @Override
    protected boolean validateAndStore() {

        // store the new preferences, no further validation needed
        final STOptions options = preferencesViewModel.getOptions();
        // TODO document.setOptions(options);
        document.storeOptions();
        return true;
    }
}
