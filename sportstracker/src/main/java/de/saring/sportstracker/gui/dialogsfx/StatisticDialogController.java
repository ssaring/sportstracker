package de.saring.sportstracker.gui.dialogsfx;

import de.saring.sportstracker.data.ExerciseFilter;
import de.saring.util.StringUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Window;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STDocument;
import de.saring.util.gui.javafx.GuiceFxmlLoader;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 * Controller (MVC) class of the Statistic dialog.
 *
 * @author Stefan Saring
 */
@Singleton
public class StatisticDialogController extends AbstractDialogController {

    private final STDocument document;

    @Inject
    private Provider<FilterDialogController> prFilterDialogController;

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

    /** The exercise filter used for statistic calculation. */
    private ExerciseFilter statisticFilter;


    /**
     * Standard c'tor for dependency injection.
     *
     * @param context the SportsTracker UI context
     * @param document the SportsTracker model/document
     * @param guiceFxmlLoader the Guice FXML loader
     */
    @Inject
    public StatisticDialogController(final STContext context, final STDocument document,
            final GuiceFxmlLoader guiceFxmlLoader) {
        super(context, guiceFxmlLoader);
        this.document = document;
    }

    /**
     * Displays the Statistic dialog for the current Exercise filter.
     *
     * @param parent parent window of the dialog
     */
    public void show(final Window parent) {

        // TODO display calculate action button

        // start with current filter criteria stored in document => user can change it
        statisticFilter = document.getCurrentFilter();

        showInfoDialog("/fxml/StatisticDialog.fxml", parent,
                context.getFxResources().getString("st.dlg.statistic.title"));
    }

    @Override
    protected void setupDialogControls() {
        // the controls are read only, so binding and view model is not needed here
        displayFilterValues();
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
        final String strAll = context.getFxResources().getString("st.dlg.statistic.all.text");
        final String strSportType = statisticFilter.getSportType() == null ? strAll :
            statisticFilter.getSportType().getName();

        final String strSportSubtype = statisticFilter.getSportSubType() == null ? strAll :
            statisticFilter.getSportSubType().getName();

        final String strIntensity = statisticFilter.getIntensity() == null ? strAll :
            statisticFilter.getIntensity().toString();

        final String strEquipment = statisticFilter.getEquipment() == null ? strAll :
            statisticFilter.getEquipment().getName();

        // create comment string
        String strComment = context.getFxResources().getString("st.dlg.statistic.no_comment.text");
        if (StringUtils.getTrimmedTextOrNull(statisticFilter.getCommentSubString()) != null) {
            strComment = statisticFilter.getCommentSubString();
            if (statisticFilter.isRegularExpressionMode()) {
                strComment += " " + context.getFxResources().getString("st.dlg.statistic.reg_expression.text");
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
        controller.setParentIsSwingWindow(false);

        controller.setAfterCloseBehavior(() ->
            controller.getSelectedFilter().ifPresent(selectedFilter -> {
                statisticFilter = selectedFilter;
                displayFilterValues();
            }));

        controller.show(context.getPrimaryStage(), statisticFilter);
    }
}
