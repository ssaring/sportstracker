package de.saring.sportstracker.gui.dialogsfx;

import de.saring.sportstracker.data.Weight;
import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STDocument;
import de.saring.util.StringUtils;
import de.saring.util.ValidationUtils;
import de.saring.util.gui.javafx.GuiceFxmlLoader;
import de.saring.util.unitcalc.ConvertUtils;
import de.saring.util.unitcalc.FormatUtils.UnitSystem;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
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
 * Controller (MVC) class of the Wieght dialog for editing / adding Weight entries.
 *
 * @author Stefan Saring
 */
@Singleton
public class WeightDialogController extends AbstractDialogController {

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
    private TextField tfValue;

    @FXML
    private Label laWeightUnit;

    @FXML
    private TextArea taComment;

    /** ViewModel of the edited Weight. */
    private WeightViewModel weightViewModel;

    /**
     * Standard c'tor for dependency injection.
     *
     * @param context the SportsTracker UI context
     * @param guiceFxmlLoader the Guice FXML loader
     */
    @Inject
    public WeightDialogController(final STContext context, final STDocument document,
                                  final GuiceFxmlLoader guiceFxmlLoader) {
        super(context, guiceFxmlLoader);
        this.document = document;
    }

    /**
     * Displays the Weight dialog for the passed Weight instance.
     *
     * @param parent parent window of the dialog
     * @param weight Weight to be edited
     */
    public void show(final Window parent, final Weight weight) {
        this.weightViewModel = new WeightViewModel(weight, document.getOptions().getUnitSystem());

        final boolean newWeight = document.getWeightList().getByID(weight.getId()) == null;
        final String dlgTitleKey = newWeight ? "st.dlg.weight.title.add" : "st.dlg.weight.title";
        final String dlgTitle = context.getFxResources().getString(dlgTitleKey);

        showEditDialog("/fxml/WeightDialog.fxml", parent, dlgTitle);
    }

    @Override
    protected void setupDialogControls() {

        laWeightUnit.setText(context.getFormatUtils().getWeightUnitName());

        // setup binding between view model and the UI controls
        dpDate.valueProperty().bindBidirectional(weightViewModel.date);
        tfHour.textProperty().bindBidirectional(weightViewModel.hour, new NumberStringConverter("00"));
        tfMinute.textProperty().bindBidirectional(weightViewModel.minute, new NumberStringConverter("00"));
        tfValue.textProperty().bindBidirectional(weightViewModel.value, new NumberStringConverter());
        taComment.textProperty().bindBidirectional(weightViewModel.comment);

        // setup validation of the UI controls
        validationSupport.registerValidator(dpDate,
                Validator.createEmptyValidator(context.getFxResources().getString("st.dlg.weight.error.date")));
        validationSupport.registerValidator(tfHour, true, (Control control, String newValue) ->
                ValidationResult.fromErrorIf(tfHour, context.getFxResources().getString("st.dlg.weight.error.time"),
                        !ValidationUtils.isValueIntegerBetween(newValue, 0, 23)));
        validationSupport.registerValidator(tfMinute, true, (Control control, String newValue) ->
                ValidationResult.fromErrorIf(tfMinute, context.getFxResources().getString("st.dlg.weight.error.time"),
                        !ValidationUtils.isValueIntegerBetween(newValue, 0, 59)));
        validationSupport.registerValidator(tfValue, true, (Control control, String newValue) ->
                ValidationResult.fromErrorIf(tfValue, context.getFxResources().getString("st.dlg.weight.error.weight"),
                        !ValidationUtils.isValueDoubleBetween(newValue, 0.1d, 1000)));
    }

        @Override
        protected boolean validateAndStore() {

        // store the new Weight, no further validation needed
        final Weight newWeight = weightViewModel.getWeight();
        document.getWeightList().set(newWeight);
        return true;
    }

    /**
     * This ViewModel class provides JavaFX properties of all Weight attributes to be edited in the dialog.
     * So they can be bound to the appropriate dialog view controls.
     */
    private static final class WeightViewModel {

        private final int id;
        private final UnitSystem unitSystem;

        private final ObjectProperty<LocalDate> date;
        private final IntegerProperty hour;
        private final IntegerProperty minute;
        private final FloatProperty value;
        private final StringProperty comment;

        /**
         * Creates the WeightViewModel with JavaFX properties for the passed Weight object.
         *
         * @param weight Weight to be edited
         * @param unitSystem the unit system currently used in the UI
         */
        public WeightViewModel(final Weight weight, final UnitSystem unitSystem) {
            this.id = weight.getId();
            this.date = new SimpleObjectProperty(weight.getDateTime().toLocalDate());
            this.hour = new SimpleIntegerProperty(weight.getDateTime().getHour());
            this.minute = new SimpleIntegerProperty(weight.getDateTime().getMinute());
            this.value = new SimpleFloatProperty(weight.getValue());
            this.comment = new SimpleStringProperty(StringUtils.getTextOrEmptyString(weight.getComment()));

            // convert weight value when english unit system is enabled
            this.unitSystem = unitSystem;
            if (unitSystem == UnitSystem.English) {
                this.value.set((float) ConvertUtils.convertKilogram2Lbs(weight.getValue()));
            }
        }

        /**
         * Creates a new Weight domain object from the edited JavaFX properties.
         *
         * @return Weight
         */
        public Weight getWeight() {
            final Weight weight = new Weight(id);
            weight.setDateTime(LocalDateTime.of(date.get(), LocalTime.of(hour.getValue(), minute.getValue())));
            weight.setValue(value.get());
            // ignore empty text for optional inputs
            weight.setComment(StringUtils.getTrimmedTextOrNull(comment.getValue()));

            // convert weight value when english unit system is enabled
            if (unitSystem == UnitSystem.English) {
                weight.setValue((float) ConvertUtils.convertLbs2Kilogram(weight.getValue()));
            }
            return weight;
        }
    }
}
