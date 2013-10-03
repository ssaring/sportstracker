package de.saring.util.data;

import java.util.Objects;

/**
 * Abstract base class for all objects which needs to have an ID for
 * referencing.
 *
 * @author Stefan Saring
 * @version 1.0
 */
public abstract class IdObject {

    /**
     * The ID of the object for referencing.
     */
    private int id;

    /**
     * Standard c'tor.
     *
     * @param id the ID of the object
     */
    public IdObject(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    /**
     * Compares the specified object with this object. The objects are equal
     * when they are of the same type and when they have the same ID.
     *
     * @param obj the object to compare with
     * @return true when same type and ID, false otherwise
     */
    @Override
    public final boolean equals(Object obj) {
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        } else if (this == obj) {
            return true;
        } else {
            IdObject other = (IdObject) obj;
            return this.id == other.id;
        }
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(this.id);
    }

    @Override
    public String toString() {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(this.getClass().getName()).append(":\n");
        sBuilder.append(" [id=").append(this.id).append("]\n");
        return sBuilder.toString();
    }
}
