package de.saring.util.gui.javafx;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import de.saring.util.data.Nameable;

/**
 * TableColumn cell factory implementation for displaying the name of Nameable objects inside table cells.
 *
 * @param <T> type of table model object
 * @author Stefan Saring
 */
public class NameableCellFactory<T> implements Callback<TableColumn<T, Nameable>, TableCell<T, Nameable>> {

    @Override
    public TableCell<T, Nameable> call(final TableColumn<T, Nameable> column) {
        return new TableCell<T, Nameable>() {

            @Override
            protected void updateItem(final Nameable value, final boolean empty) {
                super.updateItem(value, empty);
                setText(getCellText(value, empty));
            }
        };
    }

    /**
     * Returns the text to be displayed in the table cell.
     *
     * @param value value of cell
     * @param empty flag for an empty cell
     * @return text
     */
    protected String getCellText(final Nameable value, final boolean empty) {
        return empty || value == null ? null : value.getName();
    }
}
