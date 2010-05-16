package de.saring.sportstracker.data;

import de.saring.util.data.IdDateObject;

/**
 * This class defines a body weight entry for a specific date.
 * 
 * @author  Stefan Saring
 * @version 1.0
 */
public final class Weight extends IdDateObject {

    /** The weight value in kilograms. */
    private float value;

    /** Comment for the weight entry. */
    private String comment;

    /**
     * Standard c'tor.
     * @param id the ID of the object
     */
    public Weight (int id) {
        super (id);
    }

    /***** BEGIN: Generated Getters and Setters *****/

    public float getValue () {
        return value;
    }

    public void setValue (float value) {
        this.value = value;
    }

    public String getComment () {
        return comment;
    }

    public void setComment (String comment) {
        this.comment = comment;
    }

    /***** END: Generated Getters and Setters *****/

    /** 
     * Returns a string representation of this object. 
     * @return string with object content
     */
    @Override
    public String toString () {
        StringBuilder sBuilder = new StringBuilder ();
        sBuilder.append (this.getClass ().getName () + ":\n");
        sBuilder.append (" [id=" + this.getId () + "\n");
        sBuilder.append ("  date=" + this.getDate () + "\n");
        sBuilder.append ("  value=" + this.value + "\n");
        sBuilder.append ("  comment=" + this.comment + "]\n");
        return sBuilder.toString ();
    }
}
