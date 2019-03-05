package de.saring.util.data

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Abstract base class for all objects which needs to have an ID for referencing and contain a date  and a time.
 *
 * @property id the ID of the object
 *
 * @author Stefan Saring
 */
abstract class IdDateObject (id: Int) : IdObject(id) {

    /**
     * The date and time of this object. The initial value is today, 12:00:00.
     */
    var dateTime: LocalDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(12, 0, 0))

    override fun toString(): String = "${this.javaClass.name}: id=$id, dateTime=$dateTime"
}