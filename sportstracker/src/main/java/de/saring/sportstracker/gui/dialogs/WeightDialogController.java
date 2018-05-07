package de.saring.sportstracker.gui.dialogs;

import java.time.LocalTime;

import de.saring.util.gui.javafx.FxWorkarounds;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Window;
import javafx.util.converter.NumberStringConverter;

import javax.inject.Inject;

import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.Validator;

import de.saring.sportstracker.data.Weight;
import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STDocument;
import de.saring.util.ValidationUtils;
import de.saring.util.gui.javafx.TimeToStringConverter;

/**
 * Controller (MVC) class of the Weight dialog for editing / adding Weight entries.
 *
 * @author Stefan Saring
 */
public class WeightDialogController extends AbstractDialogController {

    private final STDocument document;

    @FXML
    private DatePicker dpDate;

    @FXML
    private TextField tfTime;

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
     * @param document the SportsTracker model/document
     */
    @Inject
    public WeightDialogController(final STContext context, final STDocument document) {
        super(context);
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
        final String dlgTitle = context.getResources().getString(dlgTitleKey);

        showEditDialog("/fxml/dialogs/WeightDialog.fxml", parent, dlgTitle);
    }

    @Override
    protected void setupDialogControls() {

        laWeightUnit.setText(context.getFormatUtils().getWeightUnitName());

        // setup binding between view model and the UI controls
        dpDate.valueProperty().bindBidirectional(weightViewModel.date);

        // use text formatter for time values => makes sure that the value is also valid
        final TextFormatter<LocalTime> timeTextFormatter = new TextFormatter<>(new TimeToStringConverter());
        timeTextFormatter.valueProperty().bindBidirectional(weightViewModel.time);
        tfTime.setTextFormatter(timeTextFormatter);

        tfValue.textProperty().bindBidirectional(weightViewModel.value, new NumberStringConverter());
        taComment.textProperty().bindBidirectional(weightViewModel.comment);

        FxWorkarounds.fixDatePickerTextEntry(dpDate);

        // setup validation of the UI controls
        // => due to a ControlsFX bug the validation setup must be executed after the dialog has been shown
        // (see https://bitbucket.org/controlsfx/controlsfx/issues/539/multiple-dialog-fields-with-validation )
        Platform.runLater(() -> {
            validationSupport.registerValidator(dpDate,
                    Validator.createEmptyValidator(context.getResources().getString("st.dlg.weight.error.date")));
            validationSupport.registerValidator(tfTime, //
                    Validator.createEmptyValidator(context.getResources().getString("st.dlg.weight.error.time")));
            validationSupport.registerValidator(tfValue, true, (Control control, String newValue) -> ValidationResult
                    .fromErrorIf(tfValue, context.getResources().getString("st.dlg.weight.error.weight"),
                            !ValidationUtils.isValueDoubleBetween(newValue, 0.1d, 1000)));
        });
    }

    @Override
    protected boolean validateAndStore() {

        // store the new Weight, no further validation needed
        final Weight newWeight = weightViewModel.getWeight();
        document.getWeightList().set(newWeight);
        return true;
    }
}
