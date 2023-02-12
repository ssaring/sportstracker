package de.saring.util.data

import java.util.Objects

/**
 * Abstract base class for all objects which needs to have an ID for referencing. The ID can't be changed.
 *
 * @property id the ID of the object (null for new, not persisted objects)
 *
 * @author Stefan Saring
 */
abstract class IdObject(val id: Long?) {

    /**
     * Compares the specified object with this object. The objects are equal when they are of the same instance or when
     * of same type and they have the same ID.
     *
     * @param other the object to compare with
     * @return true when same type and ID, false otherwise
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        return when(other) {
            null -> false
            is IdObject -> {
                if (this.javaClass == other.javaClass) {
                    return if (this.id == null && other.id == null) {
                        this === other
                    } else {
                        this.id == other.id
                    }
                }
                return false
            }
            else -> this === other
        }
    }

    override fun hashCode(): Int = Objects.hashCode(this.id)

    override fun toString(): String = "${this.javaClass.name}: id=$id"
}
