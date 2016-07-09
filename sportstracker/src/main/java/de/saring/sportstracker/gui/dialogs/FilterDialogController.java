package de.saring.sportstracker.gui.dialogs;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import de.saring.util.gui.javafx.BindingUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Window;

import javax.inject.Inject;

import org.controlsfx.validation.Validator;

import de.saring.sportstracker.data.Equipment;
import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.data.ExerciseFilter;
import de.saring.sportstracker.data.SportSubType;
import de.saring.sportstracker.data.SportType;
import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STDocument;
import de.saring.util.gui.javafx.NameableStringConverter;

/**
 * Controller (MVC) class of the Filter dialog for setting filter criteria to be used
 * in the exercise list.
 *
 * @author Stefan Saring
 */
public class FilterDialogController extends AbstractDialogController {

    private final STDocument document;

    @FXML
    private DatePicker dpStart;
    @FXML
    private DatePicker dpEnd;

    @FXML
    private RadioButton rbTypeExercise;
    @FXML
    private RadioButton rbTypeNote;
    @FXML
    private RadioButton rbTypeWeight;
    @FXML
    private ToggleGroup tgEntryType;

    @FXML
    private ChoiceBox<SportType> cbSportType;
    @FXML
    private ChoiceBox<SportSubType> cbSportSubtype;
    @FXML
    private ChoiceBox<FilterViewModel.IntensityItem> cbIntensity;
    @FXML
    private ChoiceBox<Equipment> cbEquipment;

    @FXML
    private TextField tfComment;

    @FXML
    private CheckBox cbRegExpression;

    /** SportType for selection "all". */
    private final SportType sportTypeAll;

    /** Sport subtype for selection "all", same for all sport types. */
    private final SportSubType sportSubtypeAll;

    /** Equipment for selection "all", same for all sport types. */
    private final Equipment equipmentAll;

    /** ViewModel of the edited Filter. */
    private FilterViewModel filterViewModel;

    private Optional<ExerciseFilter> selectedFilter = Optional.empty();


    /**
     * Standard c'tor for dependency injection.
     *
     * @param context the SportsTracker UI context
     * @param document the SportsTracker model/document
     */
    @Inject
    public FilterDialogController(final STContext context, final STDocument document) {
        super(context);
        this.document = document;

        final String resourceAll = context.getResources().getString("st.dlg.filter.all.text");
        sportTypeAll = new SportType(Integer.MAX_VALUE);
        sportTypeAll.setName(resourceAll);

        sportSubtypeAll = new SportSubType(Integer.MAX_VALUE);
        sportSubtypeAll.setName(resourceAll);

        equipmentAll = new Equipment(Integer.MAX_VALUE);
        equipmentAll.setName(resourceAll);

        FilterViewModel.IntensityItem.nameAll = resourceAll;
    }

    /**
     * Displays the Filter dialog for the passed ExerciseFilter instance.
     *
     * @param parent parent window of the dialog
     * @param filter Filter to be edited
     */
    public void show(final Window parent, final ExerciseFilter filter) {
        this.filterViewModel = new FilterViewModel(filter);
        this.selectedFilter = Optional.empty();

        showEditDialog("/fxml/dialogs/FilterDialog.fxml", parent, context.getResources().getString("st.dlg.filter.title"));
    }

    /**
     * Returns the exercise filter selected by the user when the dialog was closed with OK.
     * Otherwise an empty Optional will be returned.
     *
     * @return the selected exercise filter if available
     */
    public Optional<ExerciseFilter> getSelectedFilter() {
        return selectedFilter;
    }

    @Override
    protected void setupDialogControls() {
        setupChoiceBoxes();
        setupBinding();
        setupValidation();
    }

    /**
     * Setup of the validation of the UI controls.
     */
    private void setupValidation() {
        // => due to a ControlsFX bug the validation setup must be executed after the dialog has been shown
        // (see https://bitbucket.org/controlsfx/controlsfx/issues/539/multiple-dialog-fields-with-validation )
        Platform.runLater(() -> {
            validationSupport.registerValidator(dpStart,
                    Validator.createEmptyValidator(context.getResources().getString("st.dlg.filter.error.date")));
            validationSupport.registerValidator(dpEnd,
                    Validator.createEmptyValidator(context.getResources().getString("st.dlg.filter.error.date")));
        });
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

        rbTypeExercise.setUserData(ExerciseFilter.EntryType.EXERCISE);
        rbTypeNote.setUserData(ExerciseFilter.EntryType.NOTE);
        rbTypeWeight.setUserData(ExerciseFilter.EntryType.WEIGHT);
        BindingUtils.bindToggleGroupToProperty(tgEntryType, filterViewModel.entryType);
    }

    /**
     * Initializes all ChoiceBoxes by defining the String converters. ChoiceBoxes with fixed values
     * (SportType, Intensity) will be filled with possible values.
     */
    private void setupChoiceBoxes() {

        cbSportType.setConverter(new NameableStringConverter<>());
        cbSportSubtype.setConverter(new NameableStringConverter<>());
        cbIntensity.setConverter(new NameableStringConverter<>());
        cbEquipment.setConverter(new NameableStringConverter<>());

        cbSportType.getItems().add(sportTypeAll);
        document.getSportTypeList().forEach(sportType -> cbSportType.getItems().add(sportType));

        cbIntensity.getItems().add(new FilterViewModel.IntensityItem(null));
        Stream.of(Exercise.IntensityType.values()).forEach(
                intensityType -> cbIntensity.getItems().add(new FilterViewModel.IntensityItem(intensityType)));

        // sport subtype and equipment items depend so sport type selection, just add "all" here
        cbSportSubtype.getItems().add(sportSubtypeAll);
        cbEquipment.getItems().add(equipmentAll);

        // update the sport type specific choiceboxes on each sport type selection change
        cbSportType.addEventHandler(ActionEvent.ACTION, event -> fillSportTypeDependentChoiceBoxes());

        // select dummy sport type "all" when the filter contains no sport type
        if (filterViewModel.sportType.get() == null) {
            filterViewModel.sportType.set(sportTypeAll);
        }
    }

    /**
     * Fills all ChoiceBoxes with values dependent on the selected sport type.
     */
    private void fillSportTypeDependentChoiceBoxes() {

        cbSportSubtype.getItems().clear();
        cbEquipment.getItems().clear();
        cbSportSubtype.getItems().add(sportSubtypeAll);
        cbEquipment.getItems().add(equipmentAll);

        final SportType selectedSportType = cbSportType.getValue();
        if (selectedSportType != null && !selectedSportType.equals(sportTypeAll)) {
            selectedSportType.getSportSubTypeList()
                    .forEach(sportSubType -> cbSportSubtype.getItems().add(sportSubType));
            selectedSportType.getEquipmentList().forEach(equipment -> cbEquipment.getItems().add(equipment));
        }

        // select sport subtype and equipment "all" when the filter contains no sport subtype and equipment
        if (filterViewModel.sportSubtype.get() == null) {
            filterViewModel.sportSubtype.set(sportSubtypeAll);
        }

        if (filterViewModel.equipment.get() == null) {
            filterViewModel.equipment.set(equipmentAll);
        }
    }

    @Override
    protected boolean validateAndStore() {
        final ExerciseFilter newFilter = filterViewModel.getExerciseFilter();

        // make sure that start date is before end date
        if (newFilter.getDateEnd().isBefore(newFilter.getDateStart())) {
            dpEnd.getEditor().selectAll();
            context.showMessageDialog(getWindow(dpEnd), Alert.AlertType.ERROR, "common.error",
                    "st.dlg.filter.error.start_after_end");
            dpEnd.getEditor().requestFocus();
            return false;
        }

        // if selected sport type, subtype or equipment is "all" -> remove this dummy selection
        if (sportTypeAll.equals(newFilter.getSportType())) {
            newFilter.setSportType(null);
        }
        if (sportSubtypeAll.equals(newFilter.getSportSubType())) {
            newFilter.setSportSubType(null);
        }
        if (equipmentAll.equals(newFilter.getEquipment())) {
            newFilter.setEquipment(null);
        }

        // check regular expression, when this mode is enabled
        if (newFilter.isRegularExpressionMode() && newFilter.getCommentSubString() != null) {
            try {
                Pattern.compile(newFilter.getCommentSubString());
            } catch (Exception e) {
                // syntax error in regular expression => the user has to correct it
                tfComment.selectAll();
                context.showMessageDialog(getWindow(tfComment), Alert.AlertType.ERROR,
                        "common.error", "st.dlg.filter.error.reg_expression_error");
                tfComment.requestFocus();
                return false;
            }
        }

        // everything is OK => store the created filter
        selectedFilter = Optional.of(newFilter);
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
