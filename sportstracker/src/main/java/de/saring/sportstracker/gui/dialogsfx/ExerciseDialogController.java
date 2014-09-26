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
import de.saring.util.unitcalc.FormatUtils.UnitSystem;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
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
    private TextArea taComment;

    @FXML
    private ChoiceBox<SportType> cbSportType;

    @FXML
    private ChoiceBox<SportSubType> cbSportSubtype;

    @FXML
    private ChoiceBox<IntensityType> cbIntensity;

    @FXML
    private ChoiceBox<Equipment> cbEquipment;

    @FXML
    private TextField tfHrmFile;

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

        // setup binding between view model and the UI controls
        dpDate.valueProperty().bindBidirectional(exerciseViewModel.date);
        tfHour.textProperty().bindBidirectional(exerciseViewModel.hour, new NumberStringConverter("00"));
        tfMinute.textProperty().bindBidirectional(exerciseViewModel.minute, new NumberStringConverter("00"));
        cbSportType.valueProperty().bindBidirectional(exerciseViewModel.sportType);
        cbSportSubtype.valueProperty().bindBidirectional(exerciseViewModel.sportSubType);
        cbIntensity.valueProperty().bindBidirectional(exerciseViewModel.intensity);
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

        // enable View and Import HRM buttons only when an HRM file is specified
        btViewHrmFile.disableProperty().bind(Bindings.isEmpty(tfHrmFile.textProperty()));
        btImportHrmFile.disableProperty().bind(Bindings.isEmpty(tfHrmFile.textProperty()));
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
     * This ViewModel class provides JavaFX properties of all Exercise attributes to be edited in the dialog.
     * So they can be bound to the appropriate dialog view controls.
     */
    private static final class ExerciseViewModel {

        private final int id;
        private final UnitSystem unitSystem;

        private final ObjectProperty<LocalDate> date;
        private final IntegerProperty hour;
        private final IntegerProperty minute;
        private final ObjectProperty<SportType> sportType;
        private final ObjectProperty<SportSubType> sportSubType;
        private final ObjectProperty<IntensityType> intensity;
        private final ObjectProperty<Equipment> equipment;
        private final StringProperty hrmFile;
        private final StringProperty comment;

        // TODO add all missing attributes

        /**
         * Creates the ExerciseViewModel with JavaFX properties for the passed Exercise object.
         *
         * @param exercise Exercise to be edited
         * @param unitSystem the unit system currently used in the UI
         */
        public ExerciseViewModel(final Exercise exercise, final UnitSystem unitSystem) {
            this.id = exercise.getId();
            this.date = new SimpleObjectProperty(exercise.getDateTime().toLocalDate());
            this.hour = new SimpleIntegerProperty(exercise.getDateTime().getHour());
            this.minute = new SimpleIntegerProperty(exercise.getDateTime().getMinute());
            this.sportType= new SimpleObjectProperty(exercise.getSportType());
            this.sportSubType = new SimpleObjectProperty(exercise.getSportSubType());
            this.intensity = new SimpleObjectProperty(exercise.getIntensity());
            this.equipment = new SimpleObjectProperty(exercise.getEquipment());
            // TODO create helper method getTextOrEmptyString()
            this.hrmFile = new SimpleStringProperty(exercise.getHrmFile() == null ? "" : exercise.getHrmFile());
            this.comment = new SimpleStringProperty(exercise.getComment() == null ? "" : exercise.getComment());

            // TODO convert weight value when english unit system is enabled
            this.unitSystem = unitSystem;
//            if (unitSystem == UnitSystem.English) {
//                this.value.set((float) ConvertUtils.convertKilogram2Lbs(weight.getValue()));
//            }
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
            exercise.setEquipment(equipment.getValue());
            exercise.setHrmFile(hrmFile.getValue().trim());
            exercise.setComment(comment.getValue().trim());

            // TODO create helper method getTrimmedTextOrNull()
            // ignore empty strings
            if (exercise.getHrmFile().length() == 0) {
                exercise.setHrmFile(null);
            }
            if (exercise.getComment().length() == 0) {
                exercise.setComment(null);
            }

            // TODO convert weight value when english unit system is enabled
//            if (unitSystem == UnitSystem.English) {
//                exercise.setValue((float) ConvertUtils.convertLbs2Kilogram(exercise.getValue()));
//            }
            return exercise;
        }
    }
}
