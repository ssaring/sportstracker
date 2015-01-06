package de.saring.sportstracker.gui.views.listview;

import java.time.LocalDateTime;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STDocument;
import de.saring.sportstracker.gui.views.AbstractViewController;

/**
 * Controller class of the Exercise List View, which displays all the user exercises
 * (or a filtered list) in a table view.
 *
 * @author Stefan Saring
 */
@Singleton
public class ExerciseListViewController extends AbstractViewController {

    @FXML
    private TableView<Exercise> tvExercises;

    @FXML
    private TableColumn<Exercise, LocalDateTime> tcDate;

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
    protected String getFxmlFilename() {
        return "/fxml/views/ExerciseListView.fxml";
    }

    @Override
    protected void setupView() {

        // setup table columns
        tcDate.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        // TODO

        // TODO setup custom number cell factories for all table columns
        // final RecordingMode recordingMode = getDocument().getExercise().getRecordingMode();

        // tcTime.setCellFactory(new FormattedNumberCellFactory<>(value -> getContext().getFormatUtils()
        // .seconds2TimeString(value.intValue() / 1000)));

        // TODO set table data
        // final ExerciseSample[] samples = getDocument().getExercise().getSampleList();
        // tvSamples.setItems(FXCollections.observableArrayList(samples == null ? new ExerciseSample[0] : samples));

        // default sort is the time column
        // tvSamples.getSortOrder().add(tcTime);
    }
}
