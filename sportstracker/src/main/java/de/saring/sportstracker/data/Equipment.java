package de.saring.sportstracker.data;

import de.saring.util.data.IdObject;

/**
 * This class defines one possible equipment of a sport type (e.g. the specific
 * road bike for cycling).
 * 
 * @author  Stefan Saring
 * @version 1.0
 */
public final class Equipment extends IdObject implements Cloneable {
    
    /** Name of the equipment. */
    private String name;

    /**
     * Standard c'tor.
     * @param id the ID of the object
     */
    public Equipment (int id) {
        super (id);
    }
    
    /***** BEGIN: Generated Getters and Setters *****/
    
    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    /***** END: Generated Getters and Setters *****/

    /** 
     * Returns a deep clone copy of this Equipment object.
     * @return clone of this object
     */
    @Override
    public Equipment clone () {
        try {
            // nothing more to do (contains only primitives and an immutable string)
            // (the exception can't happen)
            return (Equipment) super.clone ();
        }
        catch (CloneNotSupportedException ex) {
            throw new AssertionError ();
        }
    }
    
    @Override
    public String toString () {
        StringBuilder sBuilder = new StringBuilder ();
        sBuilder.append (this.getClass ().getName () + ":\n");
        sBuilder.append (" [id=" + this.getId () + "\n");
        sBuilder.append ("  name=" + this.name + "]\n");
        return sBuilder.toString ();
    }
}
