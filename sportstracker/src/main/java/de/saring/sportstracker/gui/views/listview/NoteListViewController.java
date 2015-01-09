package de.saring.sportstracker.gui.views.listview;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.saring.sportstracker.core.STException;
import de.saring.sportstracker.data.Note;
import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STController;
import de.saring.sportstracker.gui.STDocument;
import de.saring.sportstracker.gui.views.AbstractEntryViewController;
import de.saring.util.StringUtils;
import de.saring.util.data.IdObject;
import de.saring.util.gui.javafx.LocalDateCellFactory;

/**
 * Controller class of the Note List View, which displays all the user notes in a table view.
 *
 * @author Stefan Saring
 */
@Singleton
public class NoteListViewController extends AbstractEntryViewController {

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
    public void updateView() {

        final List<Note> noteList = getDocument().getNoteList().stream().collect(Collectors.toList());
        tvNotes.getItems().setAll(noteList);

        // re-sorting must be forced after updating table content
        tvNotes.sort();
    }

    @Override
    public int getSelectedNoteCount() {
        return tvNotes.getSelectionModel().getSelectedItems().size();
    }

    @Override
    public int[] getSelectedNoteIDs() {
        // TODO move to base class?
        final List<Note> selectedNotes = tvNotes.getSelectionModel().getSelectedItems();
        final int[] selectedNoteIds = new int[selectedNotes.size()];

        for (int i = 0; i < selectedNoteIds.length; i++) {
            selectedNoteIds[i] = selectedNotes.get(i).getId();
        }
        return selectedNoteIds;
    }

    @Override
    public void selectEntry(final IdObject entry) {
        // TODO move to base class?
        if (entry instanceof Note) {
            final Note note = (Note) entry;
            tvNotes.getSelectionModel().select(note);
            tvNotes.scrollTo(note);
        }
    }

    @Override
    // TODO move to base class?
    public void removeSelection() {
        tvNotes.getSelectionModel().clearSelection();
    }

    @Override
    // TODO move to base class?
    public void print() throws STException {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    protected String getFxmlFilename() {
        return "/fxml/views/NoteListView.fxml";
    }

    @Override
    protected void setupView() {
        // TODO move to base class?
        setupTableColumns();

        // user can select multiple notes
        tvNotes.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // update controller-actions and the status bar on selection changes
        tvNotes.getSelectionModel().getSelectedIndices().addListener( //
                (ListChangeListener<Integer>) change -> getController().updateActionsAndStatusBar());

        // default sort order is by date descending
        tcDate.setSortType(TableColumn.SortType.DESCENDING);
        tvNotes.getSortOrder().addAll(tcDate);
    }

    private void setupTableColumns() {

        // setup factories for providing cell values
        tcDate.setCellValueFactory(cellData -> new SimpleObjectProperty<>( //
                cellData.getValue().getDateTime().toLocalDate()));
        tcText.setCellValueFactory(cellData -> new SimpleObjectProperty<>( //
                StringUtils.getFirstLineOfText(cellData.getValue().getText())));

        // setup factories for displaying cells
        tcDate.setCellFactory(new LocalDateCellFactory<>());
        // tcText uses the default factory
    }
}
