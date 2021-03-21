package de.saring.util.gui.javafx;

import de.saring.util.data.Nameable;
import javafx.util.StringConverter;

/**
 * Generic StringConverter for JavaFX controls (e.g. choice boxes) which returns the
 * name of all classes which implement the Nameable interface.
 *
 * @author Stefan Saring
 */
public class NameableStringConverter<T extends Nameable> extends StringConverter<T> {

    @Override
    public String toString(final T nameable) {
        return nameable == null ? null : nameable.getName();
    }

    @Override
    public T fromString(final String string) {
        throw new UnsupportedOperationException();
    }
}
