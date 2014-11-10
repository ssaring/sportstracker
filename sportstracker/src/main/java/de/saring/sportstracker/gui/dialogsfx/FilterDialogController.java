package de.saring.sportstracker.gui.dialogsfx;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Window;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.controlsfx.validation.Validator;

import de.saring.sportstracker.data.Equipment;
import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.data.ExerciseFilter;
import de.saring.sportstracker.data.SportSubType;
import de.saring.sportstracker.data.SportType;
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
        setupBinding();
        setupValidation();

        // TODO setup choice boxes for sport type, subtype, intensity and equipment
    }

    /**
     * Setup of the validation of the UI controls.
     */
    private void setupValidation() {

        validationSupport.registerValidator(dpStart,
                Validator.createEmptyValidator(context.getFxResources().getString("st.dlg.filter.error.date")));
        validationSupport.registerValidator(dpEnd,
                Validator.createEmptyValidator(context.getFxResources().getString("st.dlg.filter.error.date")));

        // TODO add validation: end date must not be before start date
    }

    /**
     * Setup of the binding between view model and the UI controls.
     */
    private void setupBinding() {

        dpStart.valueProperty().bindBidirectional(filterViewModel.dateStart);
        dpEnd.valueProperty().bindBidirectional(filterViewModel.dateEnd);
        cbSportType.valueProperty().bindBidirectional(filterViewModel.sportType);
        cbSportSubtype.valueProperty().bindBidirectional(filterViewModel.sportSubtype);
        cbIntensity.valueProperty().bindBidirectional(filterViewModel.intensity);
        cbEquipment.valueProperty().bindBidirectional(filterViewModel.equipment);
        tfComment.textProperty().bindBidirectional(filterViewModel.commentSubString);
        cbRegExpression.selectedProperty().bindBidirectional(filterViewModel.regularExpressionMode);
    }

    @Override
    protected boolean validateAndStore() {

        // // TODO store the new ExerciseFilter, no further validation needed
        // final ExerciseFilter newFilter = filterViewModel.getExerciseFilter();
        // document.setCurrentFilter(newFilter);
        return true;
    }

    /**
     * Action for setting the filter time period for the current week.
     */
    @FXML
    private void onCurrentWeek(final ActionEvent event) {
        LocalDate now = LocalDate.now();

        // get first day of week, depending whether it's configured as sunday or monday
        LocalDate firstDayOfWeek = document.getOptions().isWeekStartSunday() ? now.with(TemporalAdjusters
                .previousOrSame(DayOfWeek.SUNDAY)) : now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate lastDayOfWeek = firstDayOfWeek.plusDays(6);

        filterViewModel.dateStart.set(firstDayOfWeek);
        filterViewModel.dateEnd.set(lastDayOfWeek);
    }

    /**
     * Action for setting the filter time period for the current month.
     */
    @FXML
    private void onCurrentMonth(final ActionEvent event) {
        LocalDate now = LocalDate.now();
        filterViewModel.dateStart.set(now.with(TemporalAdjusters.firstDayOfMonth()));
        filterViewModel.dateEnd.set(now.with(TemporalAdjusters.lastDayOfMonth()));
    }

    /**
     * Action for setting the filter time period for the current year.
     */
    @FXML
    private void onCurrentYear(final ActionEvent event) {
        LocalDate now = LocalDate.now();
        filterViewModel.dateStart.set(now.with(TemporalAdjusters.firstDayOfYear()));
        filterViewModel.dateEnd.set(now.with(TemporalAdjusters.lastDayOfYear()));
    }

    /**
     * Action for setting the filter time period for all the time.
     */
    @FXML
    private void onAllTime(final ActionEvent event) {
        filterViewModel.dateStart.set(LocalDate.of(1900, 1, 1));
        filterViewModel.dateEnd.set(LocalDate.of(2999, 12, 31));
    }
}
