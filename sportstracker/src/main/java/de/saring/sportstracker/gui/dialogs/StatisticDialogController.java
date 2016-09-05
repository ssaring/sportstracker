package de.saring.sportstracker.gui.dialogs;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import de.saring.sportstracker.data.EntryFilter;
import de.saring.sportstracker.data.EntryList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.stage.Window;

import javax.inject.Inject;
import javax.inject.Provider;

import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.data.statistic.StatisticCalculator;
import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STDocument;
import de.saring.util.StringUtils;

/**
 * Controller (MVC) class of the Statistic dialog.
 *
 * @author Stefan Saring
 */
public class StatisticDialogController extends AbstractDialogController {

    private final STDocument document;
    private Provider<FilterDialogController> prFilterDialogController;
    private Provider<StatisticResultDialogController> prStatisticResultDialogController;

    @FXML
    private Label laTimespanValue;
    @FXML
    private Label laSportTypeValue;
    @FXML
    private Label laSportSubtypeValue;
    @FXML
    private Label laIntensityValue;
    @FXML
    private Label laEquipmentValue;
    @FXML
    private Label laCommentValue;

    /** The entry filter used for statistic calculation. */
    private EntryFilter statisticFilter;


    /**
     * Standard c'tor for dependency injection.
     *
     * @param context the SportsTracker UI context
     * @param document the SportsTracker model/document
     * @param prFilterDialogController provider of the filter dialog controller
     * @param prStatisticResultDialogController provider of the statistics result dialog controller
     */
    @Inject
    public StatisticDialogController(final STContext context, final STDocument document,
                                     final Provider<FilterDialogController> prFilterDialogController,
                                     final Provider<StatisticResultDialogController> prStatisticResultDialogController) {
        super(context);
        this.document = document;
        this.prFilterDialogController = prFilterDialogController;
        this.prStatisticResultDialogController = prStatisticResultDialogController;
    }

    /**
     * Displays the Statistic dialog for the current Exercise filter.
     *
     * @param parent parent window of the dialog
     */
    public void show(final Window parent) {

        // start with current exercise filter criteria stored in document => user can change it
        // (create a new default exercise filter, if current filter is not for exercises)
        statisticFilter = document.getCurrentFilter();
        if (statisticFilter.getEntryType() != EntryFilter.EntryType.EXERCISE) {
            statisticFilter = EntryFilter.createDefaultExerciseFilter();
        }

        showInfoDialog("/fxml/dialogs/StatisticDialog.fxml", parent,
                context.getResources().getString("st.dlg.statistic.title"));
    }

    @Override
    protected void setupDialogControls() {
        // the controls are read only, so binding and view model is not needed here
        displayFilterValues();
    }

    @Override
    protected void addCustomButtons(final DialogPane dialogPane) {

        // add 'Calculate' button to button bar
        final ButtonType bTypeCalculate = new ButtonType(context.getResources().getString(
                "st.dlg.statistic.calculate.Action.text"), ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().add(bTypeCalculate);
        final Button buttonCalculate = (Button) dialogPane.lookupButton(bTypeCalculate);

        // set action event filter for this custom button
        // => the event must be consumed, otherwise the dialog will be closed
        buttonCalculate.addEventFilter(ActionEvent.ACTION, (event) -> {
            event.consume();
            onCalculate(event);
        });
    }

    /**
     * Displays the values of the current filter.
     */
    private void displayFilterValues() {

        // create string for filter timespan
        final DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
        final String strTimeSpan = statisticFilter.getDateStart().format(dateFormatter) +
                " - " + statisticFilter.getDateEnd().format(dateFormatter);

        // create strings for sport type, subtype, intensity, equipment
        final String strAll = context.getResources().getString("st.dlg.statistic.all.text");
        final String strSportType = statisticFilter.getSportType() == null ? strAll :
            statisticFilter.getSportType().getName();

        final String strSportSubtype = statisticFilter.getSportSubType() == null ? strAll :
            statisticFilter.getSportSubType().getName();

        final String strIntensity = statisticFilter.getIntensity() == null ? strAll :
            statisticFilter.getIntensity().toString();

        final String strEquipment = statisticFilter.getEquipment() == null ? strAll :
            statisticFilter.getEquipment().getName();

        // create comment string
        String strComment = context.getResources().getString("st.dlg.statistic.no_comment.text");
        if (StringUtils.getTrimmedTextOrNull(statisticFilter.getCommentSubString()) != null) {
            strComment = statisticFilter.getCommentSubString();
            if (statisticFilter.isRegularExpressionMode()) {
                strComment += " " + context.getResources().getString("st.dlg.statistic.reg_expression.text");
            }
        }

        // display created strings
        laTimespanValue.setText(strTimeSpan);
        laSportTypeValue.setText(strSportType);
        laSportSubtypeValue.setText(strSportSubtype);
        laIntensityValue.setText(strIntensity);
        laEquipmentValue.setText(strEquipment);
        laCommentValue.setText(strComment);
    }

    /**
     * Action for changing the exercise filter for the statistic calculation.
     */
    @FXML
    private void onChange(final ActionEvent event) {

        // show Filter dialog for current filter and use the selected filter afterwards
        final FilterDialogController controller = prFilterDialogController.get();
        controller.show(context.getPrimaryStage(), statisticFilter, false);

        controller.getSelectedFilter().ifPresent(selectedFilter -> {
            statisticFilter = selectedFilter;
            displayFilterValues();
        });
    }

    /**
     * Action for calculation of the statistic and displaying the result.
     */
    private void onCalculate(final ActionEvent event) {

        // search for exercises with the selected filter criteria
        final EntryList<Exercise> lFoundExercises =
                document.getExerciseList().getEntriesForFilter(statisticFilter);

        // make sure that at least one exercise was found
        if (lFoundExercises.size() == 0) {
            context.showMessageDialog(getWindow(laTimespanValue), Alert.AlertType.INFORMATION,
                    "common.info", "st.dlg.statistic.info.no_exercises_found");
            return;
        }

        // calculate statistic
        final StatisticCalculator statistic = new StatisticCalculator(lFoundExercises);

        // finally display results in dialog
        prStatisticResultDialogController.get().show(getWindow(laTimespanValue), statistic);
    }
}
