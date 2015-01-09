package de.saring.sportstracker.gui.views.listview;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.saring.sportstracker.core.STOptions;
import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STController;
import de.saring.sportstracker.gui.STDocument;
import de.saring.util.StringUtils;
import de.saring.util.data.IdObject;
import de.saring.util.data.Nameable;
import de.saring.util.gui.javafx.FormattedNumberCellFactory;
import de.saring.util.gui.javafx.LocalDateCellFactory;
import de.saring.util.gui.javafx.NameableCellFactory;

/**
 * Controller class of the Exercise List View, which displays all the user exercises
 * (or a filtered list) in a table view.
 *
 * @author Stefan Saring
 */
@Singleton
public class ExerciseListViewController extends AbstractListViewController<Exercise> {

    @FXML
    private TableView<Exercise> tvExercises;

    @FXML
    private TableColumn<Exercise, LocalDateTime> tcDate;
    @FXML
    private TableColumn<Exercise, Nameable> tcSportType;
    @FXML
    private TableColumn<Exercise, Nameable> tcSportSubtype;
    @FXML
    private TableColumn<Exercise, Number> tcDuration;
    @FXML
    private TableColumn<Exercise, Object> tcIntensity;
    @FXML
    private TableColumn<Exercise, Number> tcDistance;
    @FXML
    private TableColumn<Exercise, Number> tcAvgSpeed;
    @FXML
    private TableColumn<Exercise, Number> tcAvgHeartrate;
    @FXML
    private TableColumn<Exercise, Number> tcAscent;
    @FXML
    private TableColumn<Exercise, Number> tcEnergy;
    @FXML
    private TableColumn<Exercise, Nameable> tcEquipment;
    @FXML
    private TableColumn<Exercise, Object> tcComment;

    /**
     * Standard c'tor for dependency injection.
     *
     * @param context the SportsTracker UI context
     * @param document the SportsTracker document / model
     * @param controller the SportsTracker UI controller
     */
    @Inject
    public ExerciseListViewController(final STContext context, final STDocument document, final STController controller) {
        super(context, document, controller);
    }

    @Override
    public ViewType getViewType() {
        return ViewType.EXERCISE_LIST;
    }

    @Override
    public void updateView() {

        // set visibility of optional columns as configured in preferences
        final STOptions options = getDocument().getOptions();
        tcAvgHeartrate.setVisible(options.isListViewShowAvgHeartrate());
        tcAscent.setVisible(options.isListViewShowAscent());
        tcEnergy.setVisible(options.isListViewShowEnergy());
        tcEquipment.setVisible(options.isListViewShowEquipment());
        tcComment.setVisible(options.isListViewShowComment());

        super.updateView();
    }

    @Override
    public int getSelectedExerciseCount() {
        return getSelectedEntryCount();
    }

    @Override
    public int[] getSelectedExerciseIDs() {
        return getSelectedEntryIDs();
    }

    @Override
    public void selectEntry(final IdObject entry) {
        if (entry != null && entry instanceof Exercise) {
            selectAndScrollToEntry((Exercise) entry);
        }
    }

    @Override
    protected String getFxmlFilename() {
        return "/fxml/views/ExerciseListView.fxml";
    }

    @Override
    protected TableView<Exercise> getTableView() {
        return tvExercises;
    }

    @Override
    protected void setupTableColumns() {

        // setup factories for providing cell values
        tcDate.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        tcSportType.setCellValueFactory(new PropertyValueFactory<>("sportType"));
        tcSportSubtype.setCellValueFactory(new PropertyValueFactory<>("sportSubType"));
        tcDuration.setCellValueFactory(new PropertyValueFactory<>("duration"));
        tcIntensity.setCellValueFactory(new PropertyValueFactory<>("intensity"));
        tcDistance.setCellValueFactory(new PropertyValueFactory<>("distance"));
        tcAvgSpeed.setCellValueFactory(new PropertyValueFactory<>("avgSpeed"));
        tcAvgHeartrate.setCellValueFactory(new PropertyValueFactory<>("avgHeartRate"));
        tcAscent.setCellValueFactory(new PropertyValueFactory<>("ascent"));
        tcEnergy.setCellValueFactory(new PropertyValueFactory<>("calories"));
        tcEquipment.setCellValueFactory(new PropertyValueFactory<>("equipment"));
        tcComment.setCellValueFactory(cellData -> new SimpleObjectProperty<>( //
                StringUtils.getFirstLineOfText(cellData.getValue().getComment())));

        // setup factories for displaying colored cell values in cells
        final ColoredNameableCellFactory coloredNameableCellFactory = new ColoredNameableCellFactory();
        final ColoredStringCellFactory coloredStringCellFactory = new ColoredStringCellFactory();

        // TODO selected cells / rows need to use white text color (see TaskListFX)

        tcDate.setCellFactory(new ColoredLocalDateCellFactory());
        tcSportType.setCellFactory(coloredNameableCellFactory);
        tcSportSubtype.setCellFactory(coloredNameableCellFactory);
        tcDuration.setCellFactory(new ColoredNumberCellFactory(value -> //
                value == null ? null : getContext().getFormatUtils().seconds2TimeString(value.intValue())));
        tcIntensity.setCellFactory(coloredStringCellFactory);
        tcDistance.setCellFactory(new ColoredNumberCellFactory(value -> //
                value == null ? null : getContext().getFormatUtils().distanceToString(value.doubleValue(), 3)));
        tcAvgSpeed.setCellFactory(new ColoredNumberCellFactory(value -> //
                value == null ? null : getContext().getFormatUtils().speedToString(value.floatValue(), 2)));
        tcAvgHeartrate.setCellFactory(new ColoredNumberCellFactory(value -> //
                value == null ? null : getContext().getFormatUtils().heartRateToString(value.intValue())));
        tcAscent.setCellFactory(new ColoredNumberCellFactory(value -> //
                value == null ? null : getContext().getFormatUtils().heightToString(value.intValue())));
        tcEnergy.setCellFactory(new ColoredNumberCellFactory(value -> //
                value == null ? null : getContext().getFormatUtils().caloriesToString(value.intValue())));
        tcEquipment.setCellFactory(coloredNameableCellFactory);
        tcComment.setCellFactory(coloredStringCellFactory);
    }

    @Override
    protected void setupDefaultSorting() {
        // default sort order is by date descending
        tcDate.setSortType(TableColumn.SortType.DESCENDING);
        tvExercises.getSortOrder().addAll(tcDate);
    }

    @Override
    protected List<Exercise> getTableEntries() {
        return getDocument().getFilterableExerciseList().stream().collect(Collectors.toList());
    }

    /**
     * Helper methods for setting the foreground text color of the specified table cell to the
     * color of the exercise sport type of the appropriate table row
     *
     * @param tableCell the table cell
     */
    private static void setTableCellTextColorOfSportType(final TableCell<Exercise, ?> tableCell) {
        final TableRow<Exercise> tableRow = tableCell.getTableRow();
        final Exercise exercise = tableRow == null ? null : tableRow.getItem();
        if (exercise != null) {
            tableCell.setTextFill(exercise.getSportType().getColor());
        }
    }

    /**
     * Extension of the NameableCellFactory, which creates table cells with the text color
     * of the sport type displayed in the current table row.
     */
    private static class ColoredNameableCellFactory extends NameableCellFactory<Exercise> {

        @Override
        public TableCell<Exercise, Nameable> call(final TableColumn<Exercise, Nameable> column) {
            return new TableCell<Exercise, Nameable>() {

                @Override
                protected void updateItem(final Nameable value, final boolean empty) {
                    super.updateItem(value, empty);
                    setText(getCellText(value, empty));
                    setTableCellTextColorOfSportType(this);
                }
            };
        }
    }

    /**
     * Extension of the LocalDateCellFactory, which creates table cells with the text color
     * of the sport type displayed in the current table row.
     */
    private static class ColoredLocalDateCellFactory extends LocalDateCellFactory<Exercise> {

        @Override
        public TableCell<Exercise, LocalDateTime> call(final TableColumn<Exercise, LocalDateTime> column) {
            return new TableCell<Exercise, LocalDateTime>() {

                @Override
                protected void updateItem(final LocalDateTime value, final boolean empty) {
                    super.updateItem(value, empty);
                    setText(getCellText(value, empty));
                    setTableCellTextColorOfSportType(this);
                }
            };
        }
    }

    /**
     * Extension of the FormattedNumberCellFactory, which creates table cells with the text color
     * of the sport type displayed in the current table row.
     */
    private static class ColoredNumberCellFactory extends FormattedNumberCellFactory<Exercise> {

        /**
         * @see de.saring.util.gui.javafx.FormattedNumberCellFactory#FormattedNumberCellFactory(javafx.util.Callback)
         */
        public ColoredNumberCellFactory(final Callback<Number, String> numberFormatter) {
            super(numberFormatter);
        }

        @Override
        public TableCell<Exercise, Number> call(final TableColumn<Exercise, Number> column) {
            return new TableCell<Exercise, Number>() {

                @Override
                protected void updateItem(final Number value, final boolean empty) {
                    super.updateItem(value, empty);
                    setText(getCellText(value, empty));
                    setTableCellTextColorOfSportType(this);
                }
            };
        }
    }

    /**
     * Custom factory for creating table cells with string values of any object. It uses {@link Object#toString()} for
     * getting the string value of the object.
     * The table cell uses the text color of the sport type displayed in the current table row.
     */
    private static class ColoredStringCellFactory implements //
            Callback<TableColumn<Exercise, Object>, TableCell<Exercise, Object>> {

        @Override
        public TableCell<Exercise, Object> call(final TableColumn<Exercise, Object> column) {
            return new TableCell<Exercise, Object>() {

                @Override
                protected void updateItem(final Object value, final boolean empty) {
                    super.updateItem(value, empty);
                    setText(empty || value == null ? null : value.toString());
                    setTableCellTextColorOfSportType(this);
                }
            };
        }
    }
}
