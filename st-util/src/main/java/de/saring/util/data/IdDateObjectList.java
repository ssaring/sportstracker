package de.saring.util.data;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This list extends IdObjectList and contains unique instances of IdDateObject
 * subclasses. It and will also never contain multiple instances with the same
 * ID. The list is always sorted by the date of the IdDateObject instances.
 *
 * @param <T> the object type to store in this list, must be a subclass of
 *            IdDateObject
 * @author Stefan Saring
 * @version 1.0
 */
public class IdDateObjectList<T extends IdDateObject> extends IdObjectList<T> {

    /**
     * Stores the specified IdDateObject object in list. It will be inserted
     * into the correct list position, so that all IdDateObject's are sorted
     * ascending by date. When there's allready an IdDateObject object with the
     * same ID then the old IdDateObject will be removed from list before.
     *
     * @param t IdDateObject instance to store (must not be null and must have a
     *            date)
     */
    @Override
    public void set(T t) {
        validateEntry(t);

        // remove the object in the list if it's allready stored (same ID)
        getIDObjects().remove(t);

        try {
            // insert the object by date order (or add it to the end)
            for (int i = 0; i < getIDObjects().size(); i++) {
                IdDateObject temp = getIDObjects().get(i);

                if (t.getDateTime().isBefore(temp.getDateTime())) {
                    getIDObjects().add(i, t);
                    return;
                }
            }
            getIDObjects().add(t);
        } finally {
            notifyAllListChangelisteners(t);
        }
    }

    /**
     * Clears this IdDateObjectList and adds all IdDateObjects of the passed list.
     * This list will be sorted afterwards, ascending by date. Finally all registered
     * ChangeListeners will be notified.
     *
     * @param entries list of IdDateObjects to store (must not be null, entries must not be null and all
     *            entries and must have a valid ID and a date)
     */
    @Override
    public void clearAndAddAll(final List<T> entries) {
        Objects.requireNonNull(entries, "List of IdDateObjects must not be null!");
        entries.forEach(entry -> validateEntry(entry));

        getIDObjects().clear();
        getIDObjects().addAll(entries);
        getIDObjects().sort((entry1, entry2) -> entry1.getDateTime().compareTo(entry2.getDateTime()));

        notifyAllListChangelisteners(null);
    }

    /**
     * Returns all IdDateObject entries of this list for which their datetime is in
     * the specified date range.
     *
     *
     * @param dStart start date of the time range (inclusive)
     * @param dEnd end date of the time range (inclusive)
     * @return list of entries in this time range
     */
    public List<T> getEntriesInDateRange(LocalDate dStart, LocalDate dEnd) {
        Objects.requireNonNull(dStart, "Start date must not be null");
        Objects.requireNonNull(dEnd, "End date must not be null");

        if (dStart.isAfter(dEnd)) {
            throw new IllegalArgumentException("Start date is after end date!");
        }

        return stream().filter(dateObject -> {
            LocalDate doDate = dateObject.getDateTime().toLocalDate();
            return !doDate.isBefore(dStart) && !doDate.isAfter(dEnd);
        }).collect(Collectors.toList());
    }

    @Override
    protected void validateEntry(final T t) {
        super.validateEntry(t);
        Objects.requireNonNull(t.getDateTime(), "DateTime must not be null!");
    }
}
