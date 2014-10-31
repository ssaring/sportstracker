package de.saring.util.gui.javafx;

import de.saring.util.data.Nameable;
import javafx.scene.control.ListCell;

/**
 * Generic ListCell for JavaFX ListView controls which displays just the name of all classes which
 * implement the Nameable interface.
 *
 * @author Stefan Saring
 */
public class NameableListCell<T extends Nameable> extends ListCell<T> {

    @Override
    protected void updateItem(final T item, final boolean empty) {
        super.updateItem(item, empty);

        setText(item == null ? null : item.getName());
    }
}
