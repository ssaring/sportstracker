package de.saring.sportstracker.gui.dialogs;

import java.io.File;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.saring.sportstracker.core.STException;
import jakarta.inject.Inject;

import de.saring.util.gui.javafx.FxWorkarounds;
import de.saring.util.unitcalc.UnitSystem;
import javafx.scene.control.*;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.Validator;

import de.saring.exerciseviewer.data.EVExercise;
import de.saring.exerciseviewer.parser.ExerciseParser;
import de.saring.exerciseviewer.parser.ExerciseParserFactory;
import de.saring.sportstracker.data.Equipment;
import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.data.Exercise.IntensityType;
import de.saring.sportstracker.data.SportSubType;
import de.saring.sportstracker.data.SportType;
import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STDocument;
import de.saring.util.StringUtils;
import de.saring.util.ValidationUtils;
import de.saring.util.gui.javafx.NameableStringConverter;
import de.saring.util.gui.javafx.TimeInSecondsToStringConverter;
import de.saring.util.gui.javafx.TimeToStringConverter;
import de.saring.util.unitcalc.ConvertUtils;
import de.saring.util.unitcalc.SpeedMode;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Window;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;

/**
 * Controller (MVC) class of the Exercise dialog for editing / adding Exercise entries.
 *
 * @author Stefan Saring
 */
public class ExerciseDialogController extends AbstractDialogController {

    private static final Logger LOGGER = Logger.getLogger(ExerciseDialogController.class.getName());

    private final STDocument document;
    private final DialogProvider dialogProvider;

    @FXML
    private DatePicker dpDate;

    @FXML
    private TextField tfTime;

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
    private TextField tfDescent;

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
    private Label laDescent;

    @FXML
    private Button btViewHrmFile;

    @FXML
    private Button btImportHrmFile;

    /** ViewModel of the edited Exercise. */
    private ExerciseViewModel exerciseViewModel;

    /** Flag whether the HRM file specified in the passed exercise needs to be imported when starting the dialog. */
    private boolean importHrmFileOnStart = false;

    /** Equipment for selection "none", same for all sport types. */
    private final Equipment equipmentNone;

    /**
     * Index of the previous similar exercise in the exercise list from which
     * the last comment has been copied. It's null when it was not used yet.
     */
    private Integer previousExerciseIndex = null;


    /**
     * Standard c'tor for dependency injection.
     *
     * @param context the SportsTracker UI context
     * @param document the SportsTracker model/document
     * @param dialogProvider provider for the dialogs
     */
    @Inject
    public ExerciseDialogController(final STContext context, final STDocument document, final DialogProvider dialogProvider) {
        super(context);
        this.document = document;
        this.dialogProvider = dialogProvider;

        equipmentNone = new Equipment(Long.MAX_VALUE);
        equipmentNone.setName(context.getResources().getString("st.dlg.exercise.equipment.none.text"));
    }

    /**
     * Displays the Exercise dialog for the passed Exercise instance.
     *
     * @param parent parent window of the dialog
     * @param exercise Exercise to be edited
     * @param importHrmFileOnStart flag for importing the HRM file specified in the exercise
     *                             (e.g. when dropping exercise files to main window)
     */
    public void show(final Window parent, final Exercise exercise, boolean importHrmFileOnStart) {
        this.exerciseViewModel = new ExerciseViewModel(
                exercise, context.getFormatUtils(), document.getOptions().getPreferredSpeedMode());
        this.importHrmFileOnStart = importHrmFileOnStart;

        // if no equipment is specified for exercise => use dummy equipment "none"
        if (exerciseViewModel.equipment.get() == null) {
            exerciseViewModel.equipment.set(equipmentNone);
        }

        final String dlgTitleKey = exercise.getId() == null ? "st.dlg.exercise.title.add" : "st.dlg.exercise.title";
        final String dlgTitle = context.getResources().getString(dlgTitleKey);

        showEditDialog("/fxml/dialogs/ExerciseDialog.fxml", parent, dlgTitle);
    }

    @Override
    protected void setupDialogControls() {

        // insert unit names in input labels with placeholders
        laDistance.setText(String.format(laDistance.getText(), context.getFormatUtils().getDistanceUnitName()));
        laAvgSpeed.setText(String.format(laAvgSpeed.getText(),
                context.getFormatUtils().getSpeedUnitName(document.getOptions().getPreferredSpeedMode())));
        laAscent.setText(String.format(laAscent.getText(), context.getFormatUtils().getAltitudeUnitName()));
        laDescent.setText(String.format(laDescent.getText(), context.getFormatUtils().getAltitudeUnitName()));

        setupChoiceBoxes();
        fillSportTypeDependentControls();

        setupBinding();
        // due to a ControlsFX bug the validation setup must be executed after the dialog has been shown
        Platform.runLater(this::setupValidation);
        setupAutoCalculation();

        FxWorkarounds.fixDatePickerTextEntry(dpDate);

        // enable View and Import HRM buttons only when an HRM file is specified
        btViewHrmFile.disableProperty().bind(Bindings.isEmpty(tfHrmFile.textProperty()));
        btImportHrmFile.disableProperty().bind(Bindings.isEmpty(tfHrmFile.textProperty()));

        // don't display value '0' for optional inputs when no data available (IntegerProperty can't handle null values)
        if (exerciseViewModel.ascent.get() == 0) {
            tfAscent.setText("");
        }
        if (exerciseViewModel.descent.get() == 0) {
            tfDescent.setText("");
        }
        if (exerciseViewModel.avgHeartRate.get() == 0) {
            tfAvgHeartrate.setText("");
        }
        if (exerciseViewModel.calories.get() == 0) {
            tfCalories.setText("");
        }

        // import HRM file when requested at dialog startup
        if (importHrmFileOnStart && StringUtils.getTrimmedTextOrNull(exerciseViewModel.hrmFile.get()) != null) {
            Platform.runLater(() -> onImportHrmFile(null));
        }
    }

    @Override
    protected boolean validateAndStore() {
        Exercise newExercise = exerciseViewModel.getExercise();

        if (!checkForNoEquipmentSelection(newExercise)) {
            return false;
        }

        // store the new Exercise, no further validation needed
        try {
            if (newExercise.getId() == null) {
                newExercise = document.getStorage().getExerciseRepository().create(newExercise);
            } else {
                document.getStorage().getExerciseRepository().update(newExercise);
            }
            document.updateApplicationData(newExercise);
            return true;
        } catch (STException e) {
            LOGGER.log(Level.SEVERE, "Failed to store Exercise '" + newExercise.getId() + "'!", e);
            return false;
        }
    }

    private boolean checkForNoEquipmentSelection(Exercise exercise) {
        if (equipmentNone.equals(exercise.getEquipment())) {

            // when there are equipments for the selected sport type => ask user whether he wants to store without
            if (exercise.getSportType().getEquipmentList().size() > 0) {
                final Optional<ButtonType> resultNoEquipment = context.showConfirmationDialog(getWindow(cbEquipment),
                        "common.info", "st.dlg.exercise.info.no_equipment");
                if (!resultNoEquipment.isPresent() || resultNoEquipment.get() != ButtonType.OK) {
                    return false;
                }
            }

            // remove the "none" dummy selection when the user wants to store without equipment
            exercise.setEquipment(null);
        }
        return true;
    }

    /**
     * Setup of the binding between view model and the UI controls.
     */
    private void setupBinding() {

        dpDate.valueProperty().bindBidirectional(exerciseViewModel.date);

        // use comment formatter for time values => makes sure that the value is also valid
        final TextFormatter<LocalTime> timeTextFormatter = new TextFormatter<>(new TimeToStringConverter());
        timeTextFormatter.valueProperty().bindBidirectional(exerciseViewModel.time);
        tfTime.setTextFormatter(timeTextFormatter);

        cbSportType.valueProperty().bindBidirectional(exerciseViewModel.sportType);
        cbSportSubtype.valueProperty().bindBidirectional(exerciseViewModel.sportSubType);
        cbIntensity.valueProperty().bindBidirectional(exerciseViewModel.intensity);
        tfDistance.textProperty().bindBidirectional(exerciseViewModel.distance, new NumberStringConverter());
        tfAvgSpeed.textProperty().bindBidirectional(exerciseViewModel.avgSpeed);
        tfDuration.textProperty().bindBidirectional(exerciseViewModel.duration, new TimeInSecondsToStringConverter());

        tfAscent.textProperty().bindBidirectional(exerciseViewModel.ascent, new NumberStringConverter());
        tfDescent.textProperty().bindBidirectional(exerciseViewModel.descent, new NumberStringConverter());
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

        validationSupport.registerValidator(dpDate, //
                Validator.createEmptyValidator(context.getResources().getString("st.dlg.exercise.error.date")));
        validationSupport.registerValidator(tfTime, //
                Validator.createEmptyValidator(context.getResources().getString("st.dlg.exercise.error.time")));
        validationSupport.registerValidator(cbSportType, //
                Validator.createEmptyValidator(context.getResources().getString("st.dlg.exercise.error.no_sport_type")));
        validationSupport.registerValidator(cbSportSubtype, //
                Validator.createEmptyValidator(context.getResources().getString("st.dlg.exercise.error.no_sport_subtype")));
        validationSupport.registerValidator(cbIntensity, //
                Validator.createEmptyValidator(context.getResources().getString("st.dlg.exercise.error.no_intensity")));

        validationSupport.registerValidator(tfDistance, true, (Control control, String newValue) ->
                ValidationResult.fromErrorIf(tfDistance, context.getResources().getString("st.dlg.exercise.error.distance"),
                        !ValidationUtils.isValueDoubleBetween(newValue,
                                exerciseViewModel.sportTypeRecordDistance.get() ? 0.001f : 0, Float.MAX_VALUE)));
        validationSupport.registerValidator(tfAvgSpeed, true, (Control control, String newValue) ->
                ValidationResult.fromErrorIf(tfAvgSpeed, context.getResources().getString("st.dlg.exercise.error.avg_speed"),
                        !ValidationUtils.isValueSpeed(newValue, exerciseViewModel.getSpeedConverter(),
                                exerciseViewModel.sportTypeRecordDistance.get())));
        validationSupport.registerValidator(tfDuration, true, (Control control, String newValue) ->
                ValidationResult.fromErrorIf(tfDuration, context.getResources().getString("st.dlg.exercise.error.duration"),
                        !ValidationUtils.isValueTimeInSecondsBetween(newValue, 1, Integer.MAX_VALUE)));

        validationSupport.registerValidator(tfAscent, false, (Control control, String newValue) ->
                ValidationResult.fromErrorIf(tfAscent, context.getResources().getString("st.dlg.exercise.error.ascent"),
                        !ValidationUtils.isOptionalValueIntegerBetween(newValue, 0, Integer.MAX_VALUE)));
        validationSupport.registerValidator(tfDescent, false, (Control control, String newValue) ->
                ValidationResult.fromErrorIf(tfDescent, context.getResources().getString("st.dlg.exercise.error.descent"),
                        !ValidationUtils.isOptionalValueIntegerBetween(newValue, 0, Integer.MAX_VALUE)));
        validationSupport.registerValidator(tfAvgHeartrate, false, (Control control, String newValue) ->
                ValidationResult.fromErrorIf(tfAvgHeartrate, context.getResources().getString("st.dlg.exercise.error.avg_heartrate"),
                        !ValidationUtils.isOptionalValueIntegerBetween(newValue, 0, 299)));
        validationSupport.registerValidator(tfCalories, false, (Control control, String newValue) ->
                ValidationResult.fromErrorIf(tfCalories, context.getResources().getString("st.dlg.exercise.error.calories"),
                        !ValidationUtils.isOptionalValueIntegerBetween(newValue, 0, Integer.MAX_VALUE)));
    }

    /**
     * Initializes all ChoiceBoxes by defining the String converters. ChoiceBoxes with fixed values
     * (SportType, Intensity) will be filled with possible values.
     */
    private void setupChoiceBoxes() {

        cbSportType.setConverter(new NameableStringConverter<>());
        cbSportSubtype.setConverter(new NameableStringConverter<>());
        cbEquipment.setConverter(new NameableStringConverter<>());

        document.getSportTypeList().forEach(sportType -> cbSportType.getItems().add(sportType));

        cbIntensity.setConverter(new IntensityToStringConverter());
        cbIntensity.getItems().addAll(List.of(IntensityType.values()));

        // update the sport type dependent controls on each sport type selection change
        cbSportType.addEventHandler(ActionEvent.ACTION, event -> fillSportTypeDependentControls());

        // reset the previous exercise index for copying the comment on sport type / subtype changes
        cbSportSubtype.addEventHandler(ActionEvent.ACTION, event -> previousExerciseIndex = null);
    }

    /**
     * Fills all controls (choice boxes and labels) with values dependent on the selected sport type.
     */
    private void fillSportTypeDependentControls() {
        cbSportSubtype.getItems().clear();
        cbEquipment.getItems().clear();
        cbEquipment.getItems().add(equipmentNone);

        final SportType selectedSportType = cbSportType.getValue();
        if (selectedSportType != null) {

            selectedSportType.getSportSubTypeList().forEach(sportSubType ->
                    cbSportSubtype.getItems().add(sportSubType));

            // add all active equipments or if the equipment is set in the exercise (even if notInUse has been set)
            Equipment selectedEquipment = exerciseViewModel.equipment.get();
            cbEquipment.getItems().addAll(selectedSportType.getEquipmentList().stream()
                    .filter(equipment -> !equipment.isNotInUse() || equipment.equals(selectedEquipment))
                    .toList());

            // label for average speed needs to show the unit for the sport types speed mode
            laAvgSpeed.setText(context.getResources().getString("st.dlg.exercise.avg_speed.text",
                    context.getFormatUtils().getSpeedUnitName(selectedSportType.getSpeedMode())));
        }

        // select equipment "none" when exercise contains no equipment
        if (exerciseViewModel.equipment.get() == null) {
            exerciseViewModel.equipment.set(equipmentNone);
        }

        // disable equipment choice box when no sport type selected
        cbEquipment.setDisable(selectedSportType == null);
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
     * Action handler for selecting an HRM exercise file in the FileChooser.
     */
    @FXML
    private void onBrowseHrmFile(final ActionEvent event) {

        // do we have an initial HRM file for the dialog?
        final String hrmFile = StringUtils.getTrimmedTextOrNull(exerciseViewModel.hrmFile.get());

        // show file open dialog and display selected filename
        final File selectedFile = dialogProvider.prHRMFileOpenDialog.get().selectHRMFile(
                getWindow(tfHrmFile), document.getOptions(), hrmFile);
        if (selectedFile != null) {
            exerciseViewModel.hrmFile.set(selectedFile.getAbsolutePath());
        }
    }

    /**
     * Action handler for viewing the selected HRM. It starts the ExerciseViewer sub-application, which
     * parses the HRM file and displays the data.
     */
    @FXML
    private void onViewHrmFile(final ActionEvent event) {

        final String hrmFile = StringUtils.getTrimmedTextOrNull(exerciseViewModel.hrmFile.getValue());
        if (hrmFile != null) {

            final SportType selectedSportType = exerciseViewModel.sportType.get();
            final SpeedMode speedMode = selectedSportType == null ?
                    document.getOptions().getPreferredSpeedMode() : selectedSportType.getSpeedMode();
            dialogProvider.prExerciseViewer.get().showExercise(hrmFile, context.getPrimaryStage(), true, speedMode);
        }
    }

    /**
     * Action handler for importing the exercise data from the selected HRM file.
     */
    @FXML
    private void onImportHrmFile(final ActionEvent event) {

        final String hrmFile = StringUtils.getTrimmedTextOrNull(exerciseViewModel.hrmFile.get());
        if (hrmFile == null) {
            return;
        }

        // parse exercise file
        EVExercise evExercise = null;
        try {
            ExerciseParser parser = ExerciseParserFactory.INSTANCE.getParser(hrmFile);
            evExercise = parser.parseExercise(hrmFile);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to parse exercise file!", e);
            context.showMessageDialog(getWindow(tfHrmFile), Alert.AlertType.ERROR, "common.error",
                    "st.dlg.exercise.error.import_console", hrmFile);
            return;
        }

        // fill dialog widgets with values from parsed HRM exercise
        final LocalDateTime pvExerciseDateTime = evExercise.getDateTime();
        if (pvExerciseDateTime != null) {
            exerciseViewModel.date.setValue(pvExerciseDateTime.toLocalDate());
            exerciseViewModel.time.set(pvExerciseDateTime.toLocalTime());
        }

        exerciseViewModel.avgHeartRate.setValue(evExercise.getHeartRateAVG());
        exerciseViewModel.calories.setValue(evExercise.getEnergy());

        if (evExercise.getDuration() != null) {
            int importedDuration = evExercise.getDuration() / 10;

            // fill speed-related values if available and recorded for the selected sport type,
            // otherwise import just the duration
            if (exerciseViewModel.sportTypeRecordDistance.get() && evExercise.getSpeed() != null) {
                exerciseViewModel.setAutoCalcFields(
                        evExercise.getSpeed().getDistance() / 1000f,
                        evExercise.getSpeed().getSpeedAvg(),
                        importedDuration);
            } else {
                exerciseViewModel.distance.set(0f);
                exerciseViewModel.setAvgSpeedFloatValue(0f);
                exerciseViewModel.duration.set(importedDuration);
            }
        }
        else {
            exerciseViewModel.distance.set(0f);
            exerciseViewModel.setAvgSpeedFloatValue(0f);
            exerciseViewModel.duration.set(0);
        }

        // fill ascent- and descent-related values
        if (evExercise.getAltitude() != null) {
            exerciseViewModel.ascent.set(evExercise.getAltitude().getAscent());
            exerciseViewModel.descent.set(evExercise.getAltitude().getDescent());

            if (document.getOptions().getUnitSystem() == UnitSystem.ENGLISH) {
                exerciseViewModel.ascent.set(ConvertUtils.convertMeter2Feet(exerciseViewModel.ascent.get()));
                exerciseViewModel.descent.set(ConvertUtils.convertMeter2Feet(exerciseViewModel.descent.get()));
            }
        }

        mapFitToStSportTypes(evExercise);
    }

    /**
     * Maps the optional Garmin FIT sport types and subtypes to the SportsTracker equivalents and selects
     * them in the choice-boxes.
     *
     * @param evExercise exercise
     */
    private void mapFitToStSportTypes(EVExercise evExercise) {
        if (evExercise.getSportTypeFit() != null) {

            var sportTypeFit = evExercise.getSportTypeFit();
            var fitSportTypeId = sportTypeFit.getSportTypeId();

            var oMappedSportType = cbSportType.getItems().stream()
                    .filter(sportType -> sportType.getFitId() != null)
                    .filter(sportType -> sportType.getFitId().shortValue() == fitSportTypeId)
                    .findFirst();

            if (oMappedSportType.isPresent()) {
                cbSportType.getSelectionModel().select(oMappedSportType.get());

                var fitSportSubTypeId = sportTypeFit.getSportSubTypeId();
                if (fitSportSubTypeId != null) {
                    cbSportSubtype.getItems().stream()
                            .filter(subType -> subType.getFitId() != null)
                            .filter(subType -> subType.getFitId().equals(fitSportSubTypeId))
                            .limit(1)
                            .forEach(subType -> cbSportSubtype.getSelectionModel().select(subType));
                }
            }
        }
    }

    /**
     * Action handler for copying the comment from the previous similar exercise.
     */
    @FXML
    private void onCopyComment(final ActionEvent event) {

        // get selected sport type and subtype (must be selected)
        final SportType selectedSportType = exerciseViewModel.sportType.get();
        final SportSubType selectedSportSubType = exerciseViewModel.sportSubType.get();

        if (selectedSportType == null || selectedSportSubType == null) {
            context.showMessageDialog(getWindow(taComment), Alert.AlertType.ERROR,
                    "common.error", "st.dlg.exercise.error.no_sport_and_subtype");
            return;
        }

        // find the start index for searching the previous exercise when not done yet
        // => get index of current exercise or use the last index when not found
        if (previousExerciseIndex == null) {
            int indexCurrentExercise =
                    document.getExerciseList().indexOf(exerciseViewModel.getExercise());
            if (indexCurrentExercise == -1) {
                indexCurrentExercise = document.getExerciseList().size();
            }
            previousExerciseIndex = indexCurrentExercise;
        }

        // check all exercises before the previous exercise
        int currentSearchIndex = previousExerciseIndex - 1;
        while (currentSearchIndex >= 0) {

            Exercise tempExercise = document.getExerciseList().getAt(currentSearchIndex);

            // has the current checked exercise the same sport type and subtype?
            if ((tempExercise.getSportType().getId() == selectedSportType.getId()) &&
                    (tempExercise.getSportSubType().getId() == selectedSportSubType.getId())) {

                // the current checked exercise is similar => copy comment when there is one
                if (tempExercise.getComment() != null && tempExercise.getComment().length() > 0) {
                    exerciseViewModel.comment.set(tempExercise.getComment());
                    previousExerciseIndex = currentSearchIndex;
                    return;
                }
            }
            currentSearchIndex--;
        }

        // nothing found => delete previous index, so searching will start from the end again
        previousExerciseIndex = null;
        context.showMessageDialog(getWindow(taComment), Alert.AlertType.INFORMATION,
                "common.info", "st.dlg.exercise.error.no_previous_exercise");
    }

    /**
     * Custom JavaFX StringConverter class for converting a exercise intensity to a localized string.
     */
    public class IntensityToStringConverter extends StringConverter<IntensityType> {

        @Override
        public String toString(final IntensityType iValue) {
            return iValue == null ? "" : context.getResources().getString(iValue.getResourceKey());
        }

        @Override
        public IntensityType fromString(final String strValue) {
            throw new UnsupportedOperationException();
        }
    }
}
