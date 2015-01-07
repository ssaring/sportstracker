package de.saring.util.gui.javafx;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 * TableColumn cell factory implementation for displaying custom formatted number values inside table cells.
 * The custom formatter is defined outside by the consumer and must be passed to the factory in the constructor.
 *
 * @param <T> type of table model object
 * @author Stefan Saring
 */
public class FormattedNumberCellFactory<T> implements Callback<TableColumn<T, Number>, TableCell<T, Number>> {

    private final Callback<Number, String> numberFormatter;

    /**
     * Creates a FormattedNumberCellFactory. The passed custom formatter will only be called when the
     * cell is not empty and the value to be formatted is not null. Otherwise the cell displays the
     * value null.
     *
     * @param numberFormatter custom formatter for number values
     */
    public FormattedNumberCellFactory(final Callback<Number, String> numberFormatter) {
        this.numberFormatter = numberFormatter;
    }

    @Override
    public TableCell<T, Number> call(final TableColumn<T, Number> column) {
        return new TableCell<T, Number>() {

            @Override
            protected void updateItem(final Number value, final boolean empty) {
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
    protected String getCellText(final Number value, final boolean empty) {
        return empty || value == null ? null : numberFormatter.call(value);
    }
}
