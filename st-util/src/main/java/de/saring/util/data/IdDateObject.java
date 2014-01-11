package de.saring.util.data;

import java.time.LocalDateTime;

/**
 * Abstract base class for all objects which needs to have an ID for referencing
 * and contain a dateTime and a time.
 *
 * @author Stefan Saring
 * @version 1.0
 */
public abstract class IdDateObject extends IdObject {

    /**
     * The dateTime and time of this object.
     */
    private LocalDateTime dateTime;

    /**
     * Standard c'tor.
     *
     * @param id the ID of the object
     */
    public IdDateObject(int id) {
        super(id);
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime date) {
        this.dateTime = date;
    }

    /**
     * Returns a string representation of this object.
     *
     * @return string with object content
     */
    @Override
    public String toString() {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(this.getClass().getName()).append(":\n");
        sBuilder.append(" [id=").append(this.getId()).append("\n");
        sBuilder.append("  dateTime=").append(this.dateTime).append("]\n");
        return sBuilder.toString();
    }
}
