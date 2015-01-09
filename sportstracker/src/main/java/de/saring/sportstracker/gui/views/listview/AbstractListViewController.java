package de.saring.sportstracker.gui.views.listview;

import java.util.List;

import javafx.collections.ListChangeListener;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;

import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STController;
import de.saring.sportstracker.gui.STDocument;
import de.saring.sportstracker.gui.views.AbstractEntryViewController;
import de.saring.util.data.IdObject;

/**
 * Abstract controller base class of all List Views which are displaying SportsTracker entries in a table.
 *
 * @param <T> type of list entry
 * @author Stefan Saring
 */
public abstract class AbstractListViewController<T extends IdObject> extends AbstractEntryViewController {

    /**
     * Standard c'tor for dependency injection.
     *
     * @param context the SportsTracker UI context
     * @param document the SportsTracker document / model
     * @param controller the SportsTracker UI controller
     */
    public AbstractListViewController(final STContext context, final STDocument document, final STController controller) {
        super(context, document, controller);
    }

    @Override
    public void updateView() {
        getTableView().getItems().setAll(getTableEntries());

        // re-sorting must be forced after updating table content
        getTableView().sort();
    }

    @Override
    public void removeSelection() {
        getTableView().getSelectionModel().clearSelection();
    }

    /**
     * Returns the TableView control of this list view.
     *
     * @return TableView
     */
    protected abstract TableView<T> getTableView();

    @Override
    protected void setupView() {
        setupTableColumns();
        setupDefaultSorting();

        // user can select multiple entries
        getTableView().getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // update controller-actions and the status bar on selection changes
        getTableView().getSelectionModel().getSelectedIndices().addListener( //
                (ListChangeListener<Integer>) change -> getController().updateActionsAndStatusBar());
    }

    /**
     * Sets up all the columns of the table. This method is called only once on list view creation.
     */
    protected abstract void setupTableColumns();

    /**
     * Sets up the default sorting of the table. This method is called only once on list view creation.
     */
    protected abstract void setupDefaultSorting();

    /**
     * Returns list of entries to be displayed in the table. This method is called each time
     * the view needs to be updated.
     *
     * @return list of entries
     */
    protected abstract List<T> getTableEntries();

    /**
     * Returns the number of selected table entries.
     *
     * @return number of selected entries
     */
    protected int getSelectedEntryCount() {
        return getTableView().getSelectionModel().getSelectedItems().size();
    }

    /**
     * Returns the list of all selected entry IDs.
     *
     * @return array of IDs (can be empty)
     */
    protected int[] getSelectedEntryIDs() {
        final List<T> selectedEntries = getTableView().getSelectionModel().getSelectedItems();
        final int[] selectedEntryIds = new int[selectedEntries.size()];

        for (int i = 0; i < selectedEntryIds.length; i++) {
            selectedEntryIds[i] = selectedEntries.get(i).getId();
        }
        return selectedEntryIds;
    }

    /**
     * Selects the specified entry and ensures its visibility.
     *
     * @param entry entry, must not be null
     */
    protected void selectAndScrollToEntry(final T entry) {
        getTableView().getSelectionModel().select(entry);
        getTableView().scrollTo(entry);
    }
}
