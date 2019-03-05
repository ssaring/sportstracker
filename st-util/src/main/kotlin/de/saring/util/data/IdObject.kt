package de.saring.util.data

import java.util.Objects

/**
 * Abstract base class for all objects which needs to have an ID for referencing. The ID can't be changed.
 *
 * @property id the ID of the object
 *
 * @author Stefan Saring
 */
abstract class IdObject(val id: Int) {

    /**
     * Compares the specified object with this object. The objects are equal when they are of the same type and when
     * they have the same ID.
     *
     * @param other the object to compare with
     * @return true when same type and ID, false otherwise
     */
    override fun equals(other: Any?): Boolean {
        return when(other) {
            null -> false
            is IdObject -> this.javaClass == other.javaClass && this.id == other.id
            else -> this === other
        }
    }

    override fun hashCode(): Int = Objects.hashCode(this.id)

    override fun toString(): String = "${this.javaClass.name}: id=$id"
}
