package de.saring.sportstracker.gui.dialogsfx;

import de.saring.util.gui.javafx.BindingUtils;
import de.saring.util.unitcalc.FormatUtils;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Window;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.saring.sportstracker.core.STOptions;
import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STDocument;
import de.saring.util.gui.javafx.GuiceFxmlLoader;

import java.util.stream.Stream;

/**
 * Controller (MVC) class of the Preferences dialog for editing the application preferences / options.
 *
 * @author Stefan Saring
 */
@Singleton
public class PreferencesDialogController extends AbstractDialogController {

    private final STDocument document;

    // tab "Main"
    @FXML
    private RadioButton rbInitialViewCalendar;
    @FXML
    private RadioButton rbInitialViewExerciseList;
    @FXML
    private ToggleGroup tgInitialView;
    @FXML
    private ChoiceBox<STOptions.AutoCalculation> cbAutomaticCalculation;
    @FXML
    private CheckBox cbSaveOnExit;

    // tab "Units"
    @FXML
    private RadioButton rbUnitsMetric;
    @FXML
    private RadioButton rbUnitsEnglish;
    @FXML
    private ToggleGroup tgUnitSystem;
    @FXML
    private RadioButton rbSpeedUnitDistance;
    @FXML
    private RadioButton rbSpeedUnitMinutes;
    @FXML
    private ToggleGroup tgSpeedView;
    @FXML
    private RadioButton rbWeekStartMonday;
    @FXML
    private RadioButton rbWeekStartSunday;

    // tab "List View"
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

    // tab "ExerciseViewer"
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
        setupSelectionControls();

        // setup binding between view model and the UI controls
        // (validation is not needed here)
        BindingUtils.bindToggleGroupToProperty(tgInitialView, preferencesViewModel.initialView);
        cbAutomaticCalculation.valueProperty().bindBidirectional(preferencesViewModel.defaultAutoCalculation);
        cbSaveOnExit.selectedProperty().bindBidirectional(preferencesViewModel.saveOnExit);

        BindingUtils.bindToggleGroupToProperty(tgUnitSystem, preferencesViewModel.unitSystem);
        BindingUtils.bindToggleGroupToProperty(tgSpeedView, preferencesViewModel.speedView);
        // TODO setup similar binding for Week start
        rbWeekStartMonday.selectedProperty().bindBidirectional(preferencesViewModel.weekStartMonday);

        cbOptionalAvgHeartrate.selectedProperty().bindBidirectional(preferencesViewModel.listViewShowAvgHeartrate);
        cbOptionalAscent.selectedProperty().bindBidirectional(preferencesViewModel.listViewShowAscent);
        cbOptionalEnergy.selectedProperty().bindBidirectional(preferencesViewModel.listViewShowEnergy);
        cbOptionalEquipment.selectedProperty().bindBidirectional(preferencesViewModel.listViewShowEquipment);
        cbOptionalComment.selectedProperty().bindBidirectional(preferencesViewModel.listViewShowComment);

        cbDiagramTwoGraphs.selectedProperty().bindBidirectional(preferencesViewModel.evDisplaySecondDiagram);
    }

    @Override
    protected boolean validateAndStore() {

        // store the new preferences, no further validation needed
        preferencesViewModel.storeInOptions(document.getOptions());
        document.storeOptions();
        return true;
    }

    private void setupSelectionControls() {

        // store selection objects as user data in radio buttons
        rbInitialViewCalendar.setUserData(STOptions.View.Calendar);
        rbInitialViewExerciseList.setUserData(STOptions.View.List);

        rbUnitsMetric.setUserData(FormatUtils.UnitSystem.Metric);
        rbUnitsEnglish.setUserData(FormatUtils.UnitSystem.English);

        rbSpeedUnitDistance.setUserData(FormatUtils.SpeedView.DistancePerHour);
        rbSpeedUnitMinutes.setUserData(FormatUtils.SpeedView.MinutesPerDistance);

        // TODO display readable and translated names for items
        Stream.of(STOptions.AutoCalculation.values()).forEach((autoCalculation) ->
                cbAutomaticCalculation.getItems().add(autoCalculation));
    }
}
