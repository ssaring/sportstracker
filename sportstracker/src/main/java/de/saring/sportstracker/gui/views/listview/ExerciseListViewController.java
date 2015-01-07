package de.saring.sportstracker.gui.views.listview;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.saring.sportstracker.core.STException;
import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STDocument;
import de.saring.sportstracker.gui.views.AbstractEntryViewController;
import de.saring.util.StringUtils;
import de.saring.util.data.IdObject;
import de.saring.util.data.Nameable;
import de.saring.util.gui.javafx.FormattedNumberCellFactory;
import de.saring.util.gui.javafx.LocalDateCellFactory;
import de.saring.util.gui.javafx.NameableCellFactory;
import de.saring.util.unitcalc.FormatUtils;

/**
 * Controller class of the Exercise List View, which displays all the user exercises
 * (or a filtered list) in a table view.
 *
 * @author Stefan Saring
 */
@Singleton
public class ExerciseListViewController extends AbstractEntryViewController {

    @FXML
    private TableView<Exercise> tvExercises;

    @FXML
    private TableColumn<Exercise, LocalDate> tcDate;
    @FXML
    private TableColumn<Exercise, Nameable> tcSportType;
    @FXML
    private TableColumn<Exercise, Nameable> tcSportSubtype;
    @FXML
    private TableColumn<Exercise, Number> tcDuration;
    @FXML
    private TableColumn<Exercise, Exercise.IntensityType> tcIntensity;
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
    private TableColumn<Exercise, String> tcComment;

    /**
     * Standard c'tor for dependency injection.
     *
     * @param context the SportsTracker UI context
     * @param document the SportsTracker document / model
     */
    @Inject
    public ExerciseListViewController(final STContext context, final STDocument document) {
        super(context, document);
    }

    @Override
    public void updateView() {
        // TODO is there a better way to transfer or bind the filtered exercises to the table model?
        final List<Exercise> filteredExercises = getDocument().getFilterableExerciseList().stream() //
                .collect(Collectors.toList());
        tvExercises.getItems().setAll(filteredExercises);
        // re-sorting must be forced after updating table content
        tvExercises.sort();
    }

    @Override
    public void selectEntry(final IdObject entry) {
        tvExercises.getSelectionModel().select((Exercise) entry);
    }

    @Override
    public void removeSelection() {
        tvExercises.getSelectionModel().clearSelection();
    }

    @Override
    public void print() throws STException {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    protected String getFxmlFilename() {
        return "/fxml/views/ExerciseListView.fxml";
    }

    @Override
    protected void setupView() {
        setupTableColumns();

        // default sort order is by date descending
        tcDate.setSortType(TableColumn.SortType.DESCENDING);
        tvExercises.getSortOrder().addAll(tcDate);
    }

    private void setupTableColumns() {

        // setup factories for providing cell values
        tcDate.setCellValueFactory(cellData -> new SimpleObjectProperty<>( //
                cellData.getValue().getDateTime().toLocalDate()));
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
        tcComment.setCellValueFactory(cellData -> new SimpleStringProperty( //
                StringUtils.getFirstLineOfText(cellData.getValue().getComment())));

        // TODO use sport type colors
        // TODO test metric system ...

        // -> setup factories for displaying call values in cells
        final FormatUtils formatUtils = getContext().getFormatUtils();
        final NameableCellFactory<Exercise> nameableCellFactory = new NameableCellFactory<>();

        tcDate.setCellFactory(new LocalDateCellFactory<>());
        tcSportType.setCellFactory(nameableCellFactory);
        tcSportSubtype.setCellFactory(nameableCellFactory);
        tcDuration.setCellFactory(new FormattedNumberCellFactory<>(value -> //
                value == null ? null : formatUtils.seconds2TimeString(value.intValue())));
        tcDistance.setCellFactory(new FormattedNumberCellFactory<>(value -> //
                value == null ? null : formatUtils.distanceToString(value.doubleValue(), 3)));
        tcAvgSpeed.setCellFactory(new FormattedNumberCellFactory<>(value -> //
                value == null ? null : formatUtils.speedToString(value.floatValue(), 2)));
        tcAvgHeartrate.setCellFactory(new FormattedNumberCellFactory<>(value -> //
                value == null ? null : formatUtils.heartRateToString(value.intValue())));
        tcAscent.setCellFactory(new FormattedNumberCellFactory<>(value -> //
                value == null ? null : formatUtils.heightToString(value.intValue())));
        tcEnergy.setCellFactory(new FormattedNumberCellFactory<>(value -> //
                value == null ? null : formatUtils.caloriesToString(value.intValue())));
        tcEquipment.setCellFactory(nameableCellFactory);
    }
}
