package de.saring.util.data

import java.time.LocalDate
import java.util.stream.Collectors

/**
 * This list extends IdObjectList and contains unique entries of IdDateObject subclasses. It provides query methods for
 * getting entries in specific date ranges.
 *
 * @param <T> the object type to store in this list, must be a subclass of IdDateObject
 *
 * @author Stefan Saring
 */
open class IdDateObjectList<T : IdDateObject> : IdObjectList<T>() {

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
