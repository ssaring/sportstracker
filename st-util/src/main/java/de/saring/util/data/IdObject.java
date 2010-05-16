package de.saring.util.data;

import java.io.Serializable;

/**
 * Abstract base class for all objects which needs to have an ID for
 * referencing.
 * 
 * @author  Stefan Saring
 * @version 1.0
 */
public abstract class IdObject implements Serializable {

    /** The ID of the object for referencing. */
    private int id;

    /**
     * Standard c'tor.
     * @param id the ID of the object
     */
    public IdObject (int id) {
        this.id = id;
    }

    public int getId () {
        return id;
    }

    /**
     * Compares the specified object with this object. The objects are equal 
     * when they are of the same type and when they have the same ID.
     * @param obj the object to compare with
     * @return true when same type and ID, false otherwise
     */
    @Override
    public final boolean equals (Object obj) {
        if (obj == null || this.getClass () != obj.getClass ()) {
            return false;
        }
        else if (this == obj) {
            return true;
        }
        else {
            IdObject other = (IdObject) obj;
            return this.id == other.id;
        }
    }

    /**
     * Returns the hascode of this instance.
     * @return the hashcode
     */
    @Override
    public final int hashCode () {
        int hash = 5;
        hash = 13 * hash + this.id;
        return hash;
    }

    /** 
     * Returns a string representation of this object. 
     * @return string with object content
     */
    @Override
    public String toString () {
        StringBuilder sBuilder = new StringBuilder ();
        sBuilder.append (this.getClass ().getName () + ":\n");
        sBuilder.append (" [id=" + this.id + "]\n");
        return sBuilder.toString ();
    }
}
