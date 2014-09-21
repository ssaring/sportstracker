package de.saring.sportstracker.gui.dialogsfx;

import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STDocument;
import de.saring.util.ValidationUtils;
import de.saring.util.gui.javafx.GuiceFxmlLoader;
import de.saring.util.unitcalc.FormatUtils.UnitSystem;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Window;
import javafx.util.converter.NumberStringConverter;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.Validator;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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

    /** ViewModel of the edited Exercise. */
    private ExerciseModel exerciseModel;

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
        this.exerciseModel = new ExerciseModel(exercise, document.getOptions().getUnitSystem());

        final boolean newExercise = document.getExerciseList().getByID(exercise.getId()) == null;
        final String dlgTitleKey = newExercise ? "st.dlg.exercise.title.add" : "st.dlg.exercise.title";
        final String dlgTitle = context.getFxResources().getString(dlgTitleKey);

        showEditDialog("/fxml/ExerciseDialog.fxml", parent, dlgTitle);
    }

    @Override
    protected void setupDialogControls() {

        // TODO laWeightUnit.setText(context.getFormatUtils().getWeightUnitName());

        // setup binding between view model and the UI controls
        dpDate.valueProperty().bindBidirectional(exerciseModel.date);
        tfHour.textProperty().bindBidirectional(exerciseModel.hour, new NumberStringConverter("00"));
        tfMinute.textProperty().bindBidirectional(exerciseModel.minute, new NumberStringConverter("00"));
        taComment.textProperty().bindBidirectional(exerciseModel.comment);

        // setup validation of the UI controls
        validationSupport.registerValidator(dpDate,
                Validator.createEmptyValidator(context.getFxResources().getString("st.dlg.weight.error.date")));
        validationSupport.registerValidator(tfHour, true, (Control control, String newValue) ->
                ValidationResult.fromErrorIf(tfHour, context.getFxResources().getString("st.dlg.weight.error.time"),
                        !ValidationUtils.isValueIntegerBetween(newValue, 0, 23)));
        validationSupport.registerValidator(tfMinute, true, (Control control, String newValue) ->
                ValidationResult.fromErrorIf(tfMinute, context.getFxResources().getString("st.dlg.weight.error.time"),
                        !ValidationUtils.isValueIntegerBetween(newValue, 0, 59)));
    }

    @Override
    protected boolean validateAndStore() {

        // store the new Exercise, no further validation needed
        final Exercise newExercise = exerciseModel.getExercise();
        document.getExerciseList().set(newExercise);
        return true;
    }

    /**
     * This ViewModel class provides JavaFX properties of all Exercise attributes to be edited in the dialog.
     * So they can be bound to the appropriate dialog view controls.
     */
    private static final class ExerciseModel {

        private final int id;
        private final UnitSystem unitSystem;

        private final ObjectProperty<LocalDate> date;
        private final IntegerProperty hour;
        private final IntegerProperty minute;
        private final StringProperty comment;

        // TODO add all missing attributes

        /**
         * Creates the ExerciseModel with JavaFX properties for the passed Exercise object.
         *
         * @param exercise Exercise to be edited
         * @param unitSystem the unit system currently used in the UI
         */
        public ExerciseModel(final Exercise exercise, final UnitSystem unitSystem) {
            this.id = exercise.getId();
            this.date = new SimpleObjectProperty(exercise.getDateTime().toLocalDate());
            this.hour = new SimpleIntegerProperty(exercise.getDateTime().getHour());
            this.minute = new SimpleIntegerProperty(exercise.getDateTime().getMinute());
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
            exercise.setComment(comment.getValue().trim());

            // ignore empty strings
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
