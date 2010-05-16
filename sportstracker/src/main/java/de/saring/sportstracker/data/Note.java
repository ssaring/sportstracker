package de.saring.sportstracker.data;

import de.saring.util.data.IdDateObject;

/**
 * This class defines a note for a specific date (e.g. for creating training
 * plans in the calendar).
 * 
 * @author  Stefan Saring
 * @version 1.0
 */
public final class Note extends IdDateObject {
    
    /** The text of the note. */
    private String text;

    /**
     * Standard c'tor.
     * @param id the ID of the object
     */
    public Note (int id) {
        super (id);
    }
    
    /***** BEGIN: Generated Getters and Setters *****/
    
    public String getText () {
        return text;
    }

    public void setText (String text) {
        this.text = text;
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
        sBuilder.append ("  text=" + this. text + "]\n");
        return sBuilder.toString ();
    }
}
