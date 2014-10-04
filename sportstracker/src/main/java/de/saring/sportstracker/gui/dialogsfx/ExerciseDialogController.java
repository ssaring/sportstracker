package de.saring.sportstracker.gui.dialogsfx;

import de.saring.sportstracker.data.Equipment;
import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.data.Exercise.IntensityType;
import de.saring.sportstracker.data.SportSubType;
import de.saring.sportstracker.data.SportType;
import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STDocument;
import de.saring.util.StringUtils;
import de.saring.util.ValidationUtils;
import de.saring.util.gui.javafx.GuiceFxmlLoader;
import de.saring.util.gui.javafx.TimeInSecondsToStringConverter;
import de.saring.util.unitcalc.CalculationUtils;
import de.saring.util.unitcalc.ConvertUtils;
import de.saring.util.unitcalc.FormatUtils.UnitSystem;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

        // TODO are inputs if "0" for duration, distance, avg speed valid?

        // setup binding between view model and the UI controls
        dpDate.valueProperty().bindBidirectional(exerciseViewModel.date);
        tfHour.textProperty().bindBidirectional(exerciseViewModel.hour, new NumberStringConverter("00"));
        tfMinute.textProperty().bindBidirectional(exerciseViewModel.minute, new NumberStringConverter("00"));
        cbSportType.valueProperty().bindBidirectional(exerciseViewModel.sportType);
        cbSportSubtype.valueProperty().bindBidirectional(exerciseViewModel.sportSubType);
        cbIntensity.valueProperty().bindBidirectional(exerciseViewModel.intensity);
        tfDistance.textProperty().bindBidirectional(exerciseViewModel.distance, new NumberStringConverter());
        // TODO test with other avg speed format setting !
        tfAvgSpeed.textProperty().bindBidirectional(exerciseViewModel.avgSpeed, new NumberStringConverter());
        tfDuration.textProperty().bindBidirectional(exerciseViewModel.duration,
                new TimeInSecondsToStringConverter(context.getFormatUtils()));

        tfAscent.textProperty().bindBidirectional(exerciseViewModel.ascent, new NumberStringConverter());
        tfAvgHeartrate.textProperty().bindBidirectional(exerciseViewModel.avgHeartRate, new NumberStringConverter());
        tfCalories.textProperty().bindBidirectional(exerciseViewModel.calories, new NumberStringConverter());
        cbEquipment.valueProperty().bindBidirectional(exerciseViewModel.equipment);
        tfHrmFile.textProperty().bindBidirectional(exerciseViewModel.hrmFile);
        taComment.textProperty().bindBidirectional(exerciseViewModel.comment);

        // setup validation of the UI controls
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
                        !ValidationUtils.isValueDoubleBetween(newValue, 0, Float.MAX_VALUE)));
        validationSupport.registerValidator(tfAvgSpeed, true, (Control control, String newValue) ->
                ValidationResult.fromErrorIf(tfAvgSpeed, context.getFxResources().getString("st.dlg.exercise.error.avg_speed"),
                        !ValidationUtils.isValueDoubleBetween(newValue, 0, Float.MAX_VALUE)));
        validationSupport.registerValidator(tfDuration, true, (Control control, String newValue) ->
                ValidationResult.fromErrorIf(tfDuration, context.getFxResources().getString("st.dlg.exercise.error.duration"),
                        !ValidationUtils.isValueTimeInSecondsBetween(newValue, 0, Integer.MAX_VALUE)));

        validationSupport.registerValidator(tfAscent, false, (Control control, String newValue) ->
                ValidationResult.fromErrorIf(tfAscent, context.getFxResources().getString("st.dlg.exercise.error.ascent"),
                        !ValidationUtils.isOptionalValueIntegerBetween(newValue, 0, Integer.MAX_VALUE)));
        validationSupport.registerValidator(tfAvgHeartrate, false, (Control control, String newValue) ->
                ValidationResult.fromErrorIf(tfAvgHeartrate, context.getFxResources().getString("st.dlg.exercise.error.avg_heartrate"),
                        !ValidationUtils.isOptionalValueIntegerBetween(newValue, 0, 299)));
        validationSupport.registerValidator(tfCalories, false, (Control control, String newValue) ->
                ValidationResult.fromErrorIf(tfCalories, context.getFxResources().getString("st.dlg.exercise.error.calories"),
                        !ValidationUtils.isOptionalValueIntegerBetween(newValue, 0, Integer.MAX_VALUE)));

        setupAutoCalculation();

        // don't display initial value "0" when optional inputs are not specified
        if (exerciseViewModel.ascent.getValue() == 0) {
            tfAscent.setText("");
        }
        if (exerciseViewModel.avgHeartRate.getValue() == 0) {
            tfAvgHeartrate.setText("");
        }
        if (exerciseViewModel.calories.getValue() == 0) {
            tfCalories.setText("");
        }

        // enable View and Import HRM buttons only when an HRM file is specified
        btViewHrmFile.disableProperty().bind(Bindings.isEmpty(tfHrmFile.textProperty()));
        btImportHrmFile.disableProperty().bind(Bindings.isEmpty(tfHrmFile.textProperty()));

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
     * Initializes all ChoiceBoxes by defining the String converters. ChoiceBoxes with fixed values
     * (SportType, Intensity) will be filled with possible values.
     */
    private void setupChoiceBoxes() {

        // TODO woud be good when SportType, SportSubType and Equipment would implement a Nameable interface
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

    /**
     * This ViewModel class provides JavaFX properties of all Exercise attributes to be edited in the dialog.
     * So they can be bound to the appropriate dialog view controls.
     */
    private static final class ExerciseViewModel {

        // TODO make ExerciseViewModel a public regular class and add unit tests for auto calculation

        private final int id;
        private final UnitSystem unitSystem;

        private final ObjectProperty<LocalDate> date;
        private final IntegerProperty hour;
        private final IntegerProperty minute;
        private final ObjectProperty<SportType> sportType;
        private final ObjectProperty<SportSubType> sportSubType;
        private final ObjectProperty<IntensityType> intensity;
        private final FloatProperty distance;
        private final FloatProperty avgSpeed;
        private final IntegerProperty duration;
        private final IntegerProperty avgHeartRate;
        private final IntegerProperty ascent;
        private final IntegerProperty calories;
        private final ObjectProperty<Equipment> equipment;
        private final StringProperty hrmFile;
        private final StringProperty comment;

        private final BooleanProperty sportTypeRecordDistance = new SimpleBooleanProperty(false);
        private final BooleanProperty autoCalcDistance = new SimpleBooleanProperty(false);
        private final BooleanProperty autoCalcAvgSpeed = new SimpleBooleanProperty(false);
        private final BooleanProperty autoCalcDuration = new SimpleBooleanProperty(false);

        /**
         * Creates the ExerciseViewModel with JavaFX properties for the passed Exercise object.
         *
         * @param exercise   Exercise to be edited
         * @param unitSystem the unit system currently used in the UI
         */
        public ExerciseViewModel(final Exercise exercise, final UnitSystem unitSystem) {
            this.id = exercise.getId();
            this.date = new SimpleObjectProperty(exercise.getDateTime().toLocalDate());
            this.hour = new SimpleIntegerProperty(exercise.getDateTime().getHour());
            this.minute = new SimpleIntegerProperty(exercise.getDateTime().getMinute());
            this.sportType = new SimpleObjectProperty(exercise.getSportType());
            this.sportSubType = new SimpleObjectProperty(exercise.getSportSubType());
            this.intensity = new SimpleObjectProperty(exercise.getIntensity());
            this.distance = new SimpleFloatProperty(exercise.getDistance());
            this.avgSpeed = new SimpleFloatProperty(exercise.getAvgSpeed());
            this.duration = new SimpleIntegerProperty(exercise.getDuration());
            this.equipment = new SimpleObjectProperty(exercise.getEquipment());
            this.avgHeartRate = new SimpleIntegerProperty(exercise.getAvgHeartRate());
            this.ascent = new SimpleIntegerProperty(exercise.getAscent());
            this.calories = new SimpleIntegerProperty(exercise.getCalories());
            this.hrmFile = new SimpleStringProperty(StringUtils.getTextOrEmptyString(exercise.getHrmFile()));
            this.comment = new SimpleStringProperty(StringUtils.getTextOrEmptyString(exercise.getComment()));

            // convert weight value when english unit system is enabled
            this.unitSystem = unitSystem;
            if (unitSystem == UnitSystem.English) {
                this.distance.set((float) ConvertUtils.convertKilometer2Miles(exercise.getDistance(), false));
                this.avgSpeed.set((float) ConvertUtils.convertKilometer2Miles(exercise.getAvgSpeed(), false));
                this.ascent.set(ConvertUtils.convertMeter2Feet(exercise.getAscent()));
            }

            setupSportTypeRecordDistance();
            setupChangeListenersForAutoCalculation(exercise, unitSystem);
        }

        /**
         * Creates a new Exercise domain object from the edited JavaFX properties.
         *
         * @return Exercise
         */
        public Exercise getExercise() {
            final Exercise exercise = new Exercise(id);
            exercise.setDateTime(LocalDateTime.of(date.get(), LocalTime.of(hour.getValue(), minute.getValue())));
            exercise.setSportType(sportType.getValue());
            exercise.setSportSubType(sportSubType.getValue());
            exercise.setIntensity(intensity.getValue());
            exercise.setDistance(distance.getValue());
            exercise.setAvgSpeed(avgSpeed.getValue());
            exercise.setDuration(duration.getValue());
            exercise.setAvgHeartRate(avgHeartRate.getValue());
            exercise.setAscent(ascent.getValue());
            exercise.setCalories(calories.getValue());
            exercise.setEquipment(equipment.getValue());
            // ignore empty text for optional inputs
            exercise.setHrmFile(StringUtils.getTrimmedTextOrNull(hrmFile.getValue()));
            exercise.setComment(StringUtils.getTrimmedTextOrNull(comment.getValue()));

            // convert weight value when english unit system is enabled
            if (unitSystem == UnitSystem.English) {
                exercise.setDistance((float) ConvertUtils.convertMiles2Kilometer(exercise.getDistance()));
                exercise.setAvgSpeed((float) ConvertUtils.convertMiles2Kilometer(exercise.getAvgSpeed()));
                exercise.setAscent(ConvertUtils.convertFeet2Meter(exercise.getAscent()));
            }
            return exercise;
        }

        /**
         * Setup of the sportTypeRecordDistance property. It will be updated every time the sport type
         * changes. When the new sport type does not record the distance, then the distance and avg speed
         * values will be set to 0.
         */
        private void setupSportTypeRecordDistance() {
            sportTypeRecordDistance.set(sportType.get() == null || sportType.get().isRecordDistance());

            sportType.addListener((observable, oldValue, newValue) -> {
                sportTypeRecordDistance.set(newValue.isRecordDistance());

                if (!sportTypeRecordDistance.get()) {
                    distance.set(0);
                    avgSpeed.set(0);
                }
            });
        }

        /**
         * Setup the value change listeners which perform the automatic calculation depending on the current
         * auto calculation mode.
         */
        private void setupChangeListenersForAutoCalculation(Exercise exercise, UnitSystem unitSystem) {
            distance.addListener((observable, oldValue, newValue) -> {
                if (!autoCalcDistance.get()) {
                    autoCalculate();
                }
            });

            avgSpeed.addListener((observable, oldValue, newValue) -> {
                if (!autoCalcAvgSpeed.get()) {
                    autoCalculate();
                }
            });

            duration.addListener((observable, oldValue, newValue) -> {
                if (!autoCalcDuration.get()) {
                    autoCalculate();
                }
            });
        }

        /**
         * Performs the calculation of the value which needs to be calculated automatically. The
         * calculated value will be 0 when one of the other values is 0 or not available.
         * The current unit system can be ignored here, all inputs are using the same.<br/>
         * The calculation will not be performed when the current sport type does not records
         * the distance!
         */
        private void autoCalculate() {
            if (sportTypeRecordDistance.get()) {
                if (autoCalcDistance.get()) {
                    if (avgSpeed.get() > 0 && duration.get() > 0) {
                        distance.set(CalculationUtils.calculateDistance(avgSpeed.get(), duration.get()));
                    } else {
                        distance.set(0);
                    }
                } else if (autoCalcAvgSpeed.get()) {
                    if (distance.get() > 0 && duration.get() > 0) {
                        avgSpeed.set(CalculationUtils.calculateAvgSpeed(distance.get(), duration.get()));
                    } else {
                        avgSpeed.set(0);
                    }
                } else if (autoCalcDuration.get()) {
                    if (distance.get() > 0 && avgSpeed.get() > 0) {
                        duration.set(CalculationUtils.calculateDuration(distance.get(), avgSpeed.get()));
                    } else {
                        duration.set(0);
                    }
                } else {
                    throw new IllegalStateException("Invalid auto calculation mode!");
                }
            }
        }
    }
}
