package de.saring.sportstracker.gui.dialogsfx;

import de.saring.sportstracker.data.Equipment;
import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.data.SportSubType;
import de.saring.sportstracker.data.SportType;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Window;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.saring.sportstracker.data.ExerciseFilter;
import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STDocument;
import de.saring.util.gui.javafx.GuiceFxmlLoader;

/**
 * Controller (MVC) class of the Filter dialog for setting filter criteria to be used
 * in the exercise list.
 *
 * @author Stefan Saring
 */
@Singleton
public class FilterDialogController extends AbstractDialogController {

    private final STDocument document;

    @FXML
    private DatePicker dpStart;
    @FXML
    private DatePicker dpEnd;

    @FXML
    private ChoiceBox<SportType> cbSportType;
    @FXML
    private ChoiceBox<SportSubType> cbSportSubtype;
    @FXML
    private ChoiceBox<Exercise.IntensityType> cbIntensity;
    @FXML
    private ChoiceBox<Equipment> cbEquipment;

    @FXML
    private TextField tfComment;

    @FXML
    private CheckBox cbRegExpression;

    /** ViewModel of the edited Filter. */
    private FilterViewModel filterViewModel;

    /**
     * Standard c'tor for dependency injection.
     *
     * @param context the SportsTracker UI context
     * @param document the SportsTracker model/document
     * @param guiceFxmlLoader the Guice FXML loader
     */
    @Inject
    public FilterDialogController(final STContext context, final STDocument document,
            final GuiceFxmlLoader guiceFxmlLoader) {
        super(context, guiceFxmlLoader);
        this.document = document;
    }

    /**
     * Displays the Filter dialog for the passed ExerciseFilter instance.
     *
     * @param parent parent window of the dialog
     * @param filter Filter to be edited
     */
    public void show(final Window parent, final ExerciseFilter filter) {
        this.filterViewModel = new FilterViewModel(filter);
        showEditDialog("/fxml/FilterDialog.fxml", parent, context.getFxResources().getString("st.dlg.filter.title"));
    }

    @Override
    protected void setupDialogControls() {

        // // TODO setup binding between view model and the UI controls
        // dpDate.valueProperty().bindBidirectional(noteViewModel.date);
        // tfHour.textProperty().bindBidirectional(noteViewModel.hour, new NumberStringConverter("00"));
        // tfMinute.textProperty().bindBidirectional(noteViewModel.minute, new NumberStringConverter("00"));
        // taText.textProperty().bindBidirectional(noteViewModel.text);
        //
        // // setup validation of the UI controls
        // validationSupport.registerValidator(dpDate,
        // Validator.createEmptyValidator(context.getFxResources().getString("st.dlg.note.error.date")));
        // validationSupport.registerValidator(tfHour, true, (Control control, String newValue) ->
        // ValidationResult.fromErrorIf(tfHour, context.getFxResources().getString("st.dlg.note.error.time"),
        // !ValidationUtils.isValueIntegerBetween(newValue, 0, 23)));
        // validationSupport.registerValidator(tfMinute, true, (Control control, String newValue) ->
        // ValidationResult.fromErrorIf(tfMinute, context.getFxResources().getString("st.dlg.note.error.time"),
        // !ValidationUtils.isValueIntegerBetween(newValue, 0, 59)));
        // validationSupport.registerValidator(taText,
        // Validator.createEmptyValidator(context.getFxResources().getString("st.dlg.note.error.no_text")));
    }

    @Override
    protected boolean validateAndStore() {

        // // TODO store the new ExerciseFilter, no further validation needed
        // final ExerciseFilter newFilter = filterViewModel.getExerciseFilter();
        // document.setCurrentFilter(newFilter);
        return true;
    }
}
