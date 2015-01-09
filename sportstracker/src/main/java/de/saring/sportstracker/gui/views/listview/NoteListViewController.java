package de.saring.sportstracker.gui.views.listview;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import de.saring.util.data.IdObject;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.saring.sportstracker.core.STException;
import de.saring.sportstracker.data.Note;
import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STController;
import de.saring.sportstracker.gui.STDocument;
import de.saring.util.StringUtils;
import de.saring.util.gui.javafx.LocalDateCellFactory;

/**
 * Controller class of the Note List View, which displays all the user notes in a table view.
 *
 * @author Stefan Saring
 */
@Singleton
public class NoteListViewController extends AbstractListViewController<Note> {

    @FXML
    private TableView<Note> tvNotes;

    @FXML
    private TableColumn<Note, LocalDate> tcDate;
    @FXML
    private TableColumn<Note, String> tcText;

    /**
     * Standard c'tor for dependency injection.
     *
     * @param context the SportsTracker UI context
     * @param document the SportsTracker document / model
     * @param controller the SportsTracker UI controller
     */
    @Inject
    public NoteListViewController(final STContext context, final STDocument document, final STController controller) {
        super(context, document, controller);
    }

    @Override
    public ViewType getViewType() {
        return ViewType.NOTE_LIST;
    }

    @Override
    public int getSelectedNoteCount() {
        return getSelectedEntryCount();
    }

    @Override
    public int[] getSelectedNoteIDs() {
        return getSelectedEntryIDs();
    }

    @Override
    public void selectEntry(final IdObject entry) {
        if (entry != null && entry instanceof Note) {
            selectAndScrollToEntry((Note) entry);
        }
    }

    @Override
    public void print() throws STException {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    protected String getFxmlFilename() {
        return "/fxml/views/NoteListView.fxml";
    }

    @Override
    protected TableView<Note> getTableView() {
        return tvNotes;
    }

    @Override
    protected void setupTableColumns() {

        // setup factories for providing cell values
        tcDate.setCellValueFactory(cellData -> new SimpleObjectProperty<>( //
                cellData.getValue().getDateTime().toLocalDate()));
        tcText.setCellValueFactory(cellData -> new SimpleObjectProperty<>( //
                StringUtils.getFirstLineOfText(cellData.getValue().getText())));

        // setup factories for displaying cells
        tcDate.setCellFactory(new LocalDateCellFactory<>());
        // tcText uses the default factory
    }

    @Override
    protected void setupDefaultSorting() {
        // default sort order is by date descending
        tcDate.setSortType(TableColumn.SortType.DESCENDING);
        tvNotes.getSortOrder().addAll(tcDate);
    }

    @Override
    protected List<Note> getTableEntries() {
        return getDocument().getNoteList().stream().collect(Collectors.toList());
    }
}
