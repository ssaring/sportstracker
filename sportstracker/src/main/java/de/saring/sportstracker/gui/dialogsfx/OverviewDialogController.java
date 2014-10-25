package de.saring.sportstracker.gui.dialogsfx;

import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STDocument;
import de.saring.util.AppResources;
import de.saring.util.gui.javafx.GuiceFxmlLoader;
import javafx.beans.binding.Bindings;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Window;
import org.jfree.chart.ChartPanel;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.LocalDate;
import java.util.Arrays;

/**
 * Controller (MVC) class of the Overview dialog of the SportsTracker application.
 * This dialog contains a diagram which displays all the exercises or weight entries in
 * various diagram graph types. The user can select the displayed time range (e.g. all
 * months of the selected year or the last 10 years until the selected year).
 *
 * @author Stefan Saring
 */
@Singleton
public class OverviewDialogController extends AbstractDialogController {

    private final STDocument document;

    /** The panel containing the current chart. */
    private final ChartPanel chartPanel;

    @FXML
    private ChoiceBox<TimeRangeType> cbTimeRange;
    @FXML
    private ChoiceBox<Integer> cbYear;
    @FXML
    private ChoiceBox<ValueType> cbDisplay;
    @FXML
    private ChoiceBox<OverviewType> cbSportTypeMode;

    @FXML
    private Pane pDiagram;
    @FXML
    private SwingNode snDiagram;

    @FXML
    private Label laFor;

    /**
     * Standard c'tor for dependency injection.
     *
     * @param context the SportsTracker UI context
     * @param document the SportsTracker model/document
     * @param guiceFxmlLoader the Guice FXML loader
     */
    @Inject
    public OverviewDialogController(final STContext context, final STDocument document,
                                    final GuiceFxmlLoader guiceFxmlLoader) {
        super(context, guiceFxmlLoader);
        this.document = document;

        TimeRangeType.appResources = context.getFxResources();
        ValueType.appResources = context.getFxResources();
        OverviewType.appResources = context.getFxResources();

        chartPanel = new ChartPanel(null);
    }

    /**
     * Displays the Overview dialog.
     *
     * @param parent parent window of the dialog
     */
    public void show(final Window parent) {
        showInfoDialog("/fxml/OverviewDialog.fxml", parent, context.getFxResources().getString("st.dlg.overview.title"));
    }

    @Override
    protected void setupDialogControls() {
        setupChoiceBoxes();
        setupDiagram();
        updateDiagram();
    }

    private void setupChoiceBoxes() {
        cbTimeRange.getItems().addAll(Arrays.asList(TimeRangeType.values()));
        cbTimeRange.getSelectionModel().select(TimeRangeType.LAST_12_MONTHS);

        cbDisplay.getItems().addAll(Arrays.asList(ValueType.values()));
        cbDisplay.getSelectionModel().select(ValueType.DISTANCE);

        cbSportTypeMode.getItems().addAll(Arrays.asList(OverviewType.values()));
        cbSportTypeMode.getSelectionModel().select(OverviewType.EACH_SPLITTED);

        // init choicebox for year selection, must not be visible for time range type "last 12 months"
        // TODO use spinner control, will be available in JavaFX 9
        for (int i = 1950; i <= 2070; i++) {
            cbYear.getItems().addAll(i);
        }
        cbYear.getSelectionModel().select(Integer.valueOf(LocalDate.now().getYear()));
        cbYear.visibleProperty().bind(Bindings.notEqual(cbTimeRange.valueProperty(), TimeRangeType.LAST_12_MONTHS));

        // the sport type mode selection must not be visible for the ValueType WEIGHT
        cbSportTypeMode.visibleProperty().bind(Bindings.notEqual(cbDisplay.valueProperty(), ValueType.WEIGHT));
        laFor.visibleProperty().bind(cbSportTypeMode.visibleProperty());

        // set listeners for updating the diagram on selection changes
        cbTimeRange.addEventHandler(ActionEvent.ACTION, event -> updateDiagram());
        cbYear.addEventHandler(ActionEvent.ACTION, event -> updateDiagram());
        cbDisplay.addEventHandler(ActionEvent.ACTION, event -> updateDiagram());
        cbSportTypeMode.addEventHandler(ActionEvent.ACTION, event -> updateDiagram());
    }

    private void setupDiagram() {
        // TODO use JavaFX version of JFreeChart here, remove SwingNode snDiagram
        // => is not available in the Maven Central repo
        // => compile localle (see ReadMe) and deploy to repo at saring.de

        executeOnSwingThread(() -> {
            chartPanel.setMinimumSize(new java.awt.Dimension((int) pDiagram.getPrefWidth(), (int) pDiagram.getPrefHeight()));
            snDiagram.setContent(chartPanel);
            snDiagram.autosize();
        });
    }

    /**
     * Draws the Overview diagram according to the current selections.
     */
    private void updateDiagram() {
        // TODO
        System.out.println("updateDiagram()");
    }

    /**
     * This is the list of possible time ranges displayed in diagram.
     * This enum also provides the localized displayed enum names.
     */
    private enum TimeRangeType {
        /**
         * In total 13 months: current month and last 12 before (good
         * for compare current month and the one from year before).
         */
        LAST_12_MONTHS("st.dlg.overview.time_range.last_12_months.text"),
        MONTHS_OF_YEAR("st.dlg.overview.time_range.months_of_year.text"),
        WEEKS_OF_YEAR("st.dlg.overview.time_range.weeks_of_year.text"),
        LAST_10_YEARS("st.dlg.overview.time_range.ten_years.text");

        private static AppResources appResources;

        private String resourceKey;

        private TimeRangeType(final String resourceKey) {
            this.resourceKey = resourceKey;
        }

        @Override
        public String toString() {
            return appResources.getString(resourceKey);
        }
    }

    /**
     * This is the list of possible value types displayed in diagram.
     * This enum also provides the localized displayed enum names.
     */
    private enum ValueType {
        DISTANCE("st.dlg.overview.display.distance_sum.text"),
        DURATION("st.dlg.overview.display.duration_sum.text"),
        ASCENT("st.dlg.overview.display.ascent_sum.text"),
        CALORIES("st.dlg.overview.display.calorie_sum.text"),
        EXERCISES("st.dlg.overview.display.exercise_count.text"),
        AVG_SPEED("st.dlg.overview.display.avg_speed.text"),
        SPORTSUBTYPE("st.dlg.overview.display.sportsubtype_distance.text"),
        EQUIPMENT("st.dlg.overview.display.equipment_distance.text"),
        WEIGHT("st.dlg.overview.display.weight.text");

        private static AppResources appResources;

        private String resourceKey;

        private ValueType(final String resourceKey) {
            this.resourceKey = resourceKey;
        }

        @Override
        public String toString() {
            return appResources.getString(resourceKey);
        }
    }

    /**
     * This is the list of possible overview types to draw in diagram.
     * This enum also provides the localized displayed enum names.
     */
    private enum OverviewType {
        EACH_SPLITTED("st.dlg.overview.sport_type.each_splitted.text"),
        EACH_STACKED("st.dlg.overview.sport_type.each_stacked.text"),
        ALL_SUMMARY("st.dlg.overview.sport_type.all_summary.text");

        private static AppResources appResources;

        private String resourceKey;

        private OverviewType(final String resourceKey) {
            this.resourceKey = resourceKey;
        }

        @Override
        public String toString() {
            return appResources.getString(resourceKey);
        }
    }
}
