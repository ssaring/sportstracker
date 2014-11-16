package de.saring.sportstracker.gui.dialogsfx;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Window;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.saring.sportstracker.data.statistic.StatisticCalculator;
import de.saring.sportstracker.gui.STContext;
import de.saring.util.gui.javafx.GuiceFxmlLoader;

/**
 * Controller (MVC) class of the dialog for displaying the results for the calculated exercise statistics.
 *
 * @author Stefan Saring
 */
@Singleton
public class StatisticResultDialogController extends AbstractDialogController {

    @FXML
    private Label laTotalExercisesValue;
    @FXML
    private Label laTotalDistanceValue;
    @FXML
    private Label laTotalDurationValue;
    @FXML
    private Label laTotalAscentValue;
    @FXML
    private Label laTotalEnergyValue;

    @FXML
    private Label laMinDistanceValue;
    @FXML
    private Label laMinAvgSpeedValue;
    @FXML
    private Label laMinDurationValue;
    @FXML
    private Label laMinAscentValue;
    @FXML
    private Label laMinAvgHeartrateValue;
    @FXML
    private Label laMinEnergyValue;

    @FXML
    private Label laAvgDistanceValue;
    @FXML
    private Label laAvgAvgSpeedValue;
    @FXML
    private Label laAvgDurationValue;
    @FXML
    private Label laAvgAscentValue;
    @FXML
    private Label laAvgAvgHeartrateValue;
    @FXML
    private Label laAvgEnergyValue;

    @FXML
    private Label laMaxDistanceValue;
    @FXML
    private Label laMaxAvgSpeedValue;
    @FXML
    private Label laMaxDurationValue;
    @FXML
    private Label laMaxAscentValue;
    @FXML
    private Label laMaxAvgHeartrateValue;
    @FXML
    private Label laMaxEnergyValue;

    /** The calculated statistic results to be displayed. */
    private StatisticCalculator statisticResult;


    /**
     * Standard c'tor for dependency injection.
     *
     * @param context the SportsTracker UI context
     * @param guiceFxmlLoader the Guice FXML loader
     */
    @Inject
    public StatisticResultDialogController(final STContext context, final GuiceFxmlLoader guiceFxmlLoader) {
        super(context, guiceFxmlLoader);
        setParentIsSwingWindow(false);
    }

    /**
     * Displays the Statistic dialog for the current Exercise filter.
     *
     * @param parent parent window of the dialog
     * @param statisticResult statistic results to display
     */
    public void show(final Window parent, final StatisticCalculator statisticResult) {
        this.statisticResult = statisticResult;

        showInfoDialog("/fxml/StatisticResultDialog.fxml", parent,
                context.getFxResources().getString("st.dlg.statistic_results.title"));
    }

    @Override
    protected void setupDialogControls() {
        // the controls are read only, so binding and view model is not needed here
        displayStatisticResultValues();
    }

    private void displayStatisticResultValues() {

        // TODO
    }
}
