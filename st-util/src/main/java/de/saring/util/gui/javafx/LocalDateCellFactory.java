package de.saring.util.gui.javafx;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 * TableColumn cell factory implementation for displaying the LocalDate values as formatted
 * text inside table cells.
 *
 * @param <T> type of table model object
 * @author Stefan Saring
 */
public class LocalDateCellFactory<T> implements Callback<TableColumn<T, LocalDate>, TableCell<T, LocalDate>> {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);

    @Override
    public TableCell<T, LocalDate> call(final TableColumn<T, LocalDate> column) {
        return new TableCell<T, LocalDate>() {

            @Override
            protected void updateItem(final LocalDate value, final boolean empty) {
                super.updateItem(value, empty);

                if (empty || value == null) {
                    setText(null);
                } else {
                    final String strDate = value.format(dateTimeFormatter);
                    setText(strDate);
                }
            }
        };
    }
}
