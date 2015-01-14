package de.saring.sportstracker.gui.views.calendarview;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import de.saring.sportstracker.core.STException;
import de.saring.sportstracker.gui.views.AbstractEntryViewController;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.saring.sportstracker.data.Note;
import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STController;
import de.saring.sportstracker.gui.STDocument;
import de.saring.sportstracker.gui.views.listviews.AbstractListViewController;
import de.saring.util.StringUtils;
import de.saring.util.data.IdObject;
import de.saring.util.gui.javafx.LocalDateCellFactory;

/**
 * Controller class of the Calendar View, which displays all (or a filtered list) exercises, notes and
 * weights of the selected month. It also contains all the navigation controls for selecting the month.
 *
 * @author Stefan Saring
 */
@Singleton
public class CalendarViewController extends AbstractEntryViewController {

    // @FXML
    // private TableView<Note> tvNotes;

    /**
     * Standard c'tor for dependency injection.
     *
     * @param context the SportsTracker UI context
     * @param document the SportsTracker document / model
     * @param controller the SportsTracker UI controller
     */
    @Inject
    public CalendarViewController(final STContext context, final STDocument document, final STController controller) {
        super(context, document, controller);
    }

    @Override
    public ViewType getViewType() {
        return ViewType.CALENDAR;
    }

    @Override
    public void updateView() {
        // TODO
    }

    @Override
    public void selectEntry(final IdObject entry) {
        // TODO
    }

    @Override
    public void removeSelection() {
        // TODO
    }

    @Override
    public void print() throws STException {
        // TODO
    }

    @Override
    protected String getFxmlFilename() {
        return "/fxml/views/CalendarView.fxml";
    }

    @Override
    protected void setupView() {
        // TODO
    }
}
