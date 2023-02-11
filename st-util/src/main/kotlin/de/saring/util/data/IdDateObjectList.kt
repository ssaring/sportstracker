package de.saring.util.data

import java.time.LocalDate
import java.util.stream.Collectors

/**
 * This list extends IdObjectList and contains unique instances of IdDateObject subclasses. It and will also never
 * contain multiple instances with the same ID. The list is always sorted by the date of the IdDateObject instances.
 *
 * @param <T> the object type to store in this list, must be a subclass of IdDateObject
 *
 * @author Stefan Saring
 */
open class IdDateObjectList<T : IdDateObject> : IdObjectList<T>() {

    /**
     * Stores the specified IdDateObject object in the list. It will be inserted into the correct list position, so that
     * all IdDateObject's are sorted ascending by date. When there's already an IdDateObject object with the same ID
     * then the old IdDateObject will be removed from list before.
     *
     * @param t IdDateObject instance to store
     */
    override fun set(t: T) {

        // remove the object in the list if it's already stored (same ID)
        idObjects.remove(t)

        // insert the object by date order (or add it to the end)
        for (i in 0 until idObjects.size) {
            if (t.dateTime.isBefore(idObjects[i].dateTime)) {
                idObjects.add(i, t)
                return
            }
        }
        idObjects.add(t)
    }

    /**
     * Clears this IdDateObjectList and adds all IdDateObjects of the passed list. This list will be sorted afterwards,
     * ascending by date. Finally all registered ChangeListeners will be notified.
     *
     * @param entries list of IdDateObjects to store (must not be null, entries must not be null and all entries and
     * must have a valid ID and a date)
     */
    override fun clearAndAddAll(entries: List<T>) {
        idObjects.clear()
        idObjects.addAll(entries)
        idObjects.sortBy { it.dateTime }
    }

    /**
     * Returns all IdDateObject entries of this list for which their datetime is in the specified date range.
     *
     *
     * @param dStart start date of the time range (inclusive)
     * @param dEnd end date of the time range (inclusive)
     * @return list of entries in this time range
     */
    fun getEntriesInDateRange(dStart: LocalDate, dEnd: LocalDate): List<T> {

        if (dStart.isAfter(dEnd)) {
            throw IllegalArgumentException("Start date is after end date!")
        }

        return stream()
                .filter { !it.dateTime.toLocalDate().isBefore(dStart) && !it.dateTime.toLocalDate().isAfter(dEnd) }
                .collect(Collectors.toList())
    }
}
