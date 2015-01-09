package de.saring.util.gui.javafx;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 * TableColumn cell factory implementation for displaying the date portion of LocalDateTime values
 * as formatted text inside table cells.
 *
 * @param <T> type of table model object
 * @author Stefan Saring
 */
public class LocalDateCellFactory<T> implements Callback<TableColumn<T, LocalDateTime>, TableCell<T, LocalDateTime>> {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);

    @Override
    public TableCell<T, LocalDateTime> call(final TableColumn<T, LocalDateTime> column) {
        return new TableCell<T, LocalDateTime>() {

            @Override
            protected void updateItem(final LocalDateTime value, final boolean empty) {
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
    protected String getCellText(final LocalDateTime value, final boolean empty) {
        return empty || value == null ? null : value.toLocalDate().format(dateTimeFormatter);
    }
}
