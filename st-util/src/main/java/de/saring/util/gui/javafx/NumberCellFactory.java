package de.saring.util.gui.javafx;

import java.text.NumberFormat;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 * TableColumn cell factory implementation for displaying formatted number values inside table cells.
 * The number of displayed fraction digits is limited.
 *
 * @param <T> type of table model object
 * @author Stefan Saring
 */
public class NumberCellFactory<T> implements Callback<TableColumn<T, Number>, TableCell<T, Number>> {

    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance();
    static {
        NUMBER_FORMAT.setMaximumFractionDigits(3);
    }

    @Override
    public TableCell<T, Number> call(final TableColumn<T, Number> column) {
        return new TableCell<T, Number>() {

            @Override
            protected void updateItem(final Number value, final boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : NUMBER_FORMAT.format(value));
            }
        };
    }
}
