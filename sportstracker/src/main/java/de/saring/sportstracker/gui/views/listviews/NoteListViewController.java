package de.saring.sportstracker.gui.views.listviews;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import de.saring.sportstracker.gui.views.ViewPrinter;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.saring.sportstracker.data.Note;
import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STDocument;
import de.saring.util.StringUtils;
import de.saring.util.data.IdObject;
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
    private TableColumn<Note, LocalDateTime> tcDate;
    @FXML
    private TableColumn<Note, String> tcText;

    /**
     * Standard c'tor for dependency injection.
     *
     * @param context the SportsTracker UI context
     * @param document the SportsTracker document / model
     * @param viewPrinter the printer of the SportsTracker views
     */
    @Inject
    public NoteListViewController(final STContext context, final STDocument document, final ViewPrinter viewPrinter) {
        super(context, document, viewPrinter);
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
        tcDate.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        tcText.setCellValueFactory(cellData -> new SimpleStringProperty( //
                StringUtils.getFirstLineOfText(cellData.getValue().getText())));

        // setup custom factories for displaying cells
        tcDate.setCellFactory(new LocalDateCellFactory<>());
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
