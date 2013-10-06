package de.saring.util.data;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This list extends IdObjectList and contains unique instances of IdDateObject
 * subclasses. It and will also never contain multiple instances with the same
 * ID. The list is always sorted by the date of the IdDateObject instances.
 *
 * @param <T> the object type to store in this list, must be a subclass of
 * IdDateObject
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
     * date)
     */
    @Override
    public void set(T t) {
        Objects.requireNonNull(t, "IdDateObject must not be null");
        Objects.requireNonNull(t.getDate(), "Date must not be null!");

        if (t.getId() <= 0) {
            throw new IllegalArgumentException("ID must be a positive integer > 0!");
        }

        // remove the object in the list if it's allready stored (same ID)
        getIDObjects().remove(t);

        try {
            // insert the object by date order (or add it to the end)
            for (int i = 0; i < getIDObjects().size(); i++) {
                IdDateObject temp = getIDObjects().get(i);

                if (t.getDate().before(temp.getDate())) {
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
     * Returns all IdDateObject entries of this list for which their date is in
     * the specified time range.
     *
     *
     * @param dtStart start date of the time range (inclusive)
     * @param dtEnd end date of the time range (inclusive)
     * @return list of entries in this time range
     */
    public List<T> getEntriesInTimeRange(Date dtStart, Date dtEnd) {
        Objects.requireNonNull(dtStart, "Start date must not be null");
        Objects.requireNonNull(dtEnd, "End date must not be null");

        if (dtStart.after(dtEnd)) {
            throw new IllegalArgumentException("Start date is after end date!");
        }

        return getIDObjects().stream()
                .filter(dateObject ->
                        !dateObject.getDate().before(dtStart) && !dateObject.getDate().after(dtEnd))
                .collect(Collectors.toList());
    }
}
