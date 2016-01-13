package de.saring.sportstracker.gui.views.listviews;

import java.util.List;

import de.saring.sportstracker.gui.views.ViewPrinter;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

import de.saring.sportstracker.gui.STContext;
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
     * @param viewPrinter the printer of the SportsTracker views
     */
    public AbstractListViewController(final STContext context, final STDocument document, final ViewPrinter viewPrinter) {
        super(context, document, viewPrinter);
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

        // The context menu is defined in FXML for the table view, but it needs to be shown for the
        // table rows. Otherwise it's displayed for empty rows or the table header. It can't be defined
        // for the table rows in FXML, so move it from the table view to the table rows here.
        final ContextMenu contextMenu = getTableView().getContextMenu();
        getTableView().setContextMenu(null);

        setupTableRowFactory(contextMenu);

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

    /**
     * Called whenever the selection or the item value of a TableRow or when the focus of the
     * TableView has been changed. This callback can be used to set custom table row colors.
     * The default implementation does nothing.
     *
     * @param tableRow table row
     */
    protected void updateTableRowColor(final TableRow<T> tableRow) {
    }

    private void setupTableRowFactory(final ContextMenu contextMenu) {
        getTableView().setRowFactory(tableView -> {
            final TableRow<T> tableRow = new TableRow<>();

            // update table row color when the item value, the selection or the focus has been changed
                tableRow.itemProperty().addListener((observable, oldValue, newValue) -> updateTableRowColor(tableRow));
                tableRow.selectedProperty().addListener( //
                        (observable, oldValue, newValue) -> updateTableRowColor(tableRow));
                getTableView().focusedProperty().addListener( //
                        (observable, oldValue, newValue) -> updateTableRowColor(tableRow));

                // bind context menu to row, but only when the row is not empty
                tableRow.contextMenuProperty().bind( //
                        Bindings.when(tableRow.emptyProperty()) //
                                .then((ContextMenu) null) //
                                .otherwise(contextMenu));

                // add listener for double clicks for editing the selected entry (ignore in empty rows)
                tableRow.setOnMouseClicked(event -> {
                    if (event.getClickCount() > 1 && getSelectedEntryCount() == 1 && !tableRow.isEmpty()) {
                        getController().onEditEntry(null);
                    }
                });

                return tableRow;
            });
    }

    /**
     * Event handler for the context menu item 'Add Exercise', it delegates the event to the STController.
     *
     * @param event ActionEvent
     */
    @FXML
    private void onAddExercise(final ActionEvent event) {
        getController().onAddExercise(event);
    }

    /**
     * Event handler for the context menu item 'Add Note', it delegates the event to the STController.
     *
     * @param event ActionEvent
     */
    @FXML
    private void onAddNote(final ActionEvent event) {
        getController().onAddNote(event);
    }

    /**
     * Event handler for the context menu item 'Add Weight', it delegates the event to the STController.
     *
     * @param event ActionEvent
     */
    @FXML
    private void onAddWeight(final ActionEvent event) {
        getController().onAddWeight(event);
    }

    /**
     * Event handler for the context menu item 'Edit Entry', it delegates the event to the STController.
     *
     * @param event ActionEvent
     */
    @FXML
    private void onEditEntry(final ActionEvent event) {
        getController().onEditEntry(event);
    }

    /**
     * Event handler for the context menu item 'Copy Entry', it delegates the event to the STController.
     *
     * @param event ActionEvent
     */
    @FXML
    private void onCopyEntry(final ActionEvent event) {
        getController().onCopyEntry(event);
    }

    /**
     * Event handler for the context menu item 'Delete Entry', it delegates the event to the STController.
     *
     * @param event ActionEvent
     */
    @FXML
    private void onDeleteEntry(final ActionEvent event) {
        getController().onDeleteEntry(event);
    }
}
