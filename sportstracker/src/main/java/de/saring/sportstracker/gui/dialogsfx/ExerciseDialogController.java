package de.saring.sportstracker.gui.dialogsfx;

import de.saring.sportstracker.data.Equipment;
import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.data.Exercise.IntensityType;
import de.saring.sportstracker.data.SportSubType;
import de.saring.sportstracker.data.SportType;
import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STDocument;
import de.saring.util.ValidationUtils;
import de.saring.util.gui.javafx.GuiceFxmlLoader;
import de.saring.util.gui.javafx.SpeedToStringConverter;
import de.saring.util.gui.javafx.TimeInSecondsToStringConverter;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Window;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.Validator;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;

/**
 * Controller (MVC) class of the Wieght dialog for editing / adding Exercise entries.
 *
 * @author Stefan Saring
 */
@Singleton
public class ExerciseDialogController extends AbstractDialogController {

    private final STDocument document;

    @FXML
    private DatePicker dpDate;

    // TODO use formatted TextField, will be introduced in JavaFX 8u40
    @FXML
    private TextField tfHour;

    // TODO use formatted TextField, will be introduced in JavaFX 8u40
    @FXML
    private TextField tfMinute;

    @FXML
    private ChoiceBox<SportType> cbSportType;

    @FXML
    private ChoiceBox<SportSubType> cbSportSubtype;

    @FXML
    private ChoiceBox<IntensityType> cbIntensity;

    @FXML
    private ChoiceBox<Equipment> cbEquipment;

    @FXML
    private TextField tfDistance;

    @FXML
    private TextField tfAvgSpeed;

    @FXML
    private TextField tfDuration;

    @FXML
    private RadioButton rbAutoCalcDistance;

    @FXML
    private RadioButton rbAutoCalcAvgSpeed;

    @FXML
    private RadioButton rbAutoCalcDuration;

    @FXML
    private TextField tfAscent;

    @FXML
    private TextField tfAvgHeartrate;

    @FXML
    private TextField tfCalories;

    @FXML
    private TextField tfHrmFile;

    @FXML
    private TextArea taComment;

    @FXML
    private Label laDistance;

    @FXML
    private Label laAvgSpeed;

    @FXML
    private Label laAscent;

    @FXML
    private Button btViewHrmFile;

    @FXML
    private Button btImportHrmFile;


    /** ViewModel of the edited Exercise. */
    private ExerciseViewModel exerciseViewModel;

    /**
     * Standard c'tor for dependency injection.
     *
     * @param context the SportsTracker UI context
     * @param guiceFxmlLoader the Guice FXML loader
     */
    @Inject
    public ExerciseDialogController(final STContext context, final STDocument document,
                                    final GuiceFxmlLoader guiceFxmlLoader) {
        super(context, guiceFxmlLoader);
        this.document = document;
    }

    /**
     * Displays the Exercise dialog for the passed Exercise instance.
     *
     * @param parent parent window of the dialog
     * @param exercise Exercise to be edited
     */
    public void show(final Window parent, final Exercise exercise) {
        this.exerciseViewModel = new ExerciseViewModel(exercise, document.getOptions().getUnitSystem());

        final boolean newExercise = document.getExerciseList().getByID(exercise.getId()) == null;
        final String dlgTitleKey = newExercise ? "st.dlg.exercise.title.add" : "st.dlg.exercise.title";
        final String dlgTitle = context.getFxResources().getString(dlgTitleKey);

        showEditDialog("/fxml/ExerciseDialog.fxml", parent, dlgTitle);
    }

    @Override
    protected void setupDialogControls() {

        // insert unit names in input labels with placeholders
        laDistance.setText(String.format(laDistance.getText(), context.getFormatUtils().getDistanceUnitName()));
        laAvgSpeed.setText(String.format(laAvgSpeed.getText(), context.getFormatUtils().getSpeedUnitName()));
        laAscent.setText(String.format(laAscent.getText(), context.getFormatUtils().getAltitudeUnitName()));

        setupChoiceBoxes();
        fillSportTypeDependentChoiceBoxes();

        setupBinding();
        setupValidation();
        setupAutoCalculation();

        // enable View and Import HRM buttons only when an HRM file is specified
        btViewHrmFile.disableProperty().bind(Bindings.isEmpty(tfHrmFile.textProperty()));
        btImportHrmFile.disableProperty().bind(Bindings.isEmpty(tfHrmFile.textProperty()));

        // don't display value '0' for optional inputs when no data available
        if (exerciseViewModel.ascent.get() == 0) {
            tfAscent.setText("");
        }
        if (exerciseViewModel.avgHeartRate.get() == 0) {
            tfAvgHeartrate.setText("");
        }
        if (exerciseViewModel.calories.get() == 0) {
            tfCalories.setText("");
        }

        // TODO add behavior for copy the comment from last similar exercise
    }

    @Override
    protected boolean validateAndStore() {

        // store the new Exercise, no further validation needed
        final Exercise newExercise = exerciseViewModel.getExercise();
        document.getExerciseList().set(newExercise);
        return true;
    }

    /**
     * Setup of the binding between view model and the UI controls.
     */
    private void setupBinding() {

        dpDate.valueProperty().bindBidirectional(exerciseViewModel.date);
        tfHour.textProperty().bindBidirectional(exerciseViewModel.hour, new NumberStringConverter("00"));
        tfMinute.textProperty().bindBidirectional(exerciseViewModel.minute, new NumberStringConverter("00"));
        cbSportType.valueProperty().bindBidirectional(exerciseViewModel.sportType);
        cbSportSubtype.valueProperty().bindBidirectional(exerciseViewModel.sportSubType);
        cbIntensity.valueProperty().bindBidirectional(exerciseViewModel.intensity);
        tfDistance.textProperty().bindBidirectional(exerciseViewModel.distance, new NumberStringConverter());
        tfAvgSpeed.textProperty().bindBidirectional(exerciseViewModel.avgSpeed,
                new SpeedToStringConverter(context.getFormatUtils()));
        tfDuration.textProperty().bindBidirectional(exerciseViewModel.duration,
                new TimeInSecondsToStringConverter(context.getFormatUtils()));

        tfAscent.textProperty().bindBidirectional(exerciseViewModel.ascent, new NumberStringConverter());
        tfAvgHeartrate.textProperty().bindBidirectional(exerciseViewModel.avgHeartRate, new NumberStringConverter());
        tfCalories.textProperty().bindBidirectional(exerciseViewModel.calories, new NumberStringConverter());
        cbEquipment.valueProperty().bindBidirectional(exerciseViewModel.equipment);
        tfHrmFile.textProperty().bindBidirectional(exerciseViewModel.hrmFile);
        taComment.textProperty().bindBidirectional(exerciseViewModel.comment);
    }

    /**
     * Setup of the validation of the UI controls.
     */
    private void setupValidation() {

        validationSupport.registerValidator(dpDate,
                Validator.createEmptyValidator(context.getFxResources().getString("st.dlg.exercise.error.date")));
        validationSupport.registerValidator(tfHour, true, (Control control, String newValue) ->
                ValidationResult.fromErrorIf(tfHour, context.getFxResources().getString("st.dlg.exercise.error.time"),
                        !ValidationUtils.isValueIntegerBetween(newValue, 0, 23)));
        validationSupport.registerValidator(tfMinute, true, (Control control, String newValue) ->
                ValidationResult.fromErrorIf(tfMinute, context.getFxResources().getString("st.dlg.exercise.error.time"),
                        !ValidationUtils.isValueIntegerBetween(newValue, 0, 59)));
        validationSupport.registerValidator(cbSportType,
                Validator.createEmptyValidator(context.getFxResources().getString("st.dlg.exercise.error.no_sport_type")));
        validationSupport.registerValidator(cbSportSubtype,
                Validator.createEmptyValidator(context.getFxResources().getString("st.dlg.exercise.error.no_sport_subtype")));
        validationSupport.registerValidator(cbIntensity,
                Validator.createEmptyValidator(context.getFxResources().getString("st.dlg.exercise.error.no_intensity")));

        validationSupport.registerValidator(tfDistance, true, (Control control, String newValue) ->
                ValidationResult.fromErrorIf(tfDistance, context.getFxResources().getString("st.dlg.exercise.error.distance"),
                        !ValidationUtils.isValueDoubleBetween(newValue,
                                exerciseViewModel.sportTypeRecordDistance.get() ? 0.001f : 0, Float.MAX_VALUE)));
        validationSupport.registerValidator(tfAvgSpeed, true, (Control control, String newValue) ->
                ValidationResult.fromErrorIf(tfAvgSpeed, context.getFxResources().getString("st.dlg.exercise.error.avg_speed"),
                        !ValidationUtils.isValueDoubleBetween(newValue,
                                exerciseViewModel.sportTypeRecordDistance.get() ? 0.001f : 0, Float.MAX_VALUE)));
        validationSupport.registerValidator(tfDuration, true, (Control control, String newValue) ->
                ValidationResult.fromErrorIf(tfDuration, context.getFxResources().getString("st.dlg.exercise.error.duration"),
                        !ValidationUtils.isValueTimeInSecondsBetween(newValue, 1, Integer.MAX_VALUE)));

        validationSupport.registerValidator(tfAscent, false, (Control control, String newValue) ->
                ValidationResult.fromErrorIf(tfAscent, context.getFxResources().getString("st.dlg.exercise.error.ascent"),
                        !ValidationUtils.isOptionalValueIntegerBetween(newValue, 0, Integer.MAX_VALUE)));
        validationSupport.registerValidator(tfAvgHeartrate, false, (Control control, String newValue) ->
                ValidationResult.fromErrorIf(tfAvgHeartrate, context.getFxResources().getString("st.dlg.exercise.error.avg_heartrate"),
                        !ValidationUtils.isOptionalValueIntegerBetween(newValue, 0, 299)));
        validationSupport.registerValidator(tfCalories, false, (Control control, String newValue) ->
                ValidationResult.fromErrorIf(tfCalories, context.getFxResources().getString("st.dlg.exercise.error.calories"),
                        !ValidationUtils.isOptionalValueIntegerBetween(newValue, 0, Integer.MAX_VALUE)));
    }

    /**
     * Initializes all ChoiceBoxes by defining the String converters. ChoiceBoxes with fixed values
     * (SportType, Intensity) will be filled with possible values.
     */
    private void setupChoiceBoxes() {

        // TODO would be good when SportType, SportSubType and Equipment would implement a Nameable interface
        // => then a generic converter would be sufficient for all choice boxes
        cbSportType.setConverter(new StringConverter<SportType>() {
            @Override
            public String toString(final SportType sportType) {
                return sportType.getName();
            }

            @Override
            public SportType fromString(final String string) {
                throw new UnsupportedOperationException();
            }
        });

        cbSportSubtype.setConverter(new StringConverter<SportSubType>() {
            @Override
            public String toString(final SportSubType sportSubType) {
                return sportSubType.getName();
            }

            @Override
            public SportSubType fromString(final String string) {
                throw new UnsupportedOperationException();
            }
        });

        cbEquipment.setConverter(new StringConverter<Equipment>() {
            @Override
            public String toString(final Equipment equipment) {
                return equipment.getName();
            }

            @Override
            public Equipment fromString(final String string) {
                throw new UnsupportedOperationException();
            }
        });

        document.getSportTypeList().forEach(sportType -> cbSportType.getItems().add(sportType));
        cbIntensity.getItems().addAll(Arrays.asList(IntensityType.values()));

        // update the sport type dependent choiceboxes on each sport type selection change
        cbSportType.addEventHandler(ActionEvent.ACTION, event -> fillSportTypeDependentChoiceBoxes());
    }

    /**
     * Fills all ChoiceBoxes with values dependent on the selected sport type.
     */
    private void fillSportTypeDependentChoiceBoxes() {
        cbSportSubtype.getItems().clear();
        cbEquipment.getItems().clear();

        final SportType selectedSportType = cbSportType.getValue();
        if (selectedSportType != null) {
            selectedSportType.getSportSubTypeList().forEach(sportSubType ->
                    cbSportSubtype.getItems().add(sportSubType));
            selectedSportType.getEquipmentList().forEach(equipment ->
                    cbEquipment.getItems().add(equipment));
        }

        // disable equipment ChoiceBox when no sport type selected or no equipments defined
        cbEquipment.setDisable(selectedSportType == null || selectedSportType.getEquipmentList().size() == 0);
    }

    /**
     * Setup of the automatic calculation of distance, avg speed or duration depending on the
     * user preferences. The selected auto calculation textfield will get disabled. The user can
     * also select another field for auto calculation.<br/>
     * The automatic calculation selection will be disabled when the current sport type does not
     * record the distance.
     */
    private void setupAutoCalculation() {

        // disable the textfields for distance and avg speed when the value is calculated automatically
        // or when the current sport type does not record the distance
        tfDistance.disableProperty().bind(Bindings.or(
                rbAutoCalcDistance.selectedProperty(),
                exerciseViewModel.sportTypeRecordDistance.not()));
        tfAvgSpeed.disableProperty().bind(Bindings.or(
                rbAutoCalcAvgSpeed.selectedProperty(),
                exerciseViewModel.sportTypeRecordDistance.not()));

        // disable the textfield for duration when the value is calculated automatically
        // and when the current sport type records the distance
        tfDuration.disableProperty().bind(Bindings.and(
                rbAutoCalcDuration.selectedProperty(),
                exerciseViewModel.sportTypeRecordDistance));

        rbAutoCalcDistance.selectedProperty().bindBidirectional(exerciseViewModel.autoCalcDistance);
        rbAutoCalcAvgSpeed.selectedProperty().bindBidirectional(exerciseViewModel.autoCalcAvgSpeed);
        rbAutoCalcDuration.selectedProperty().bindBidirectional(exerciseViewModel.autoCalcDuration);

        // disable automatic calculation radioboxes when the current sport type does not record the distance
        rbAutoCalcDistance.disableProperty().bind(exerciseViewModel.sportTypeRecordDistance.not());
        rbAutoCalcAvgSpeed.disableProperty().bind(exerciseViewModel.sportTypeRecordDistance.not());
        rbAutoCalcDuration.disableProperty().bind(exerciseViewModel.sportTypeRecordDistance.not());

        // set initial automatic calculation type from preferences
        switch (document.getOptions().getDefaultAutoCalcuation()) {
            case Distance:
                exerciseViewModel.autoCalcDistance.set(true);
                break;
            case AvgSpeed:
                exerciseViewModel.autoCalcAvgSpeed.set(true);
                break;
            default:
                exerciseViewModel.autoCalcDuration.set(true);
        }
    }
}
