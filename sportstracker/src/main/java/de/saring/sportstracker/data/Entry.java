package de.saring.sportstracker.data;

import de.saring.util.data.IdDateObject;

/**
 * This class defines the baseclass for all SportsTracker entries. Each entry has an ID, a date and time and a comment.
 *
 * @author Stefan Saring
 */
public abstract class Entry extends IdDateObject {

    /** Comment of the entry (optional). */
    private String comment;

    /**
     * Standard c'tor.
     *
     * @param id the ID of the object (null for new, not persisted objects)
     */
    public Entry(Long id) {
        super(id);
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(this.getClass().getName()).append(":\n");
        sBuilder.append(" [id=").append(this.getId()).append("\n");
        sBuilder.append("  dateTime=").append(this.getDateTime()).append("\n");
        sBuilder.append("  comment=").append(this.comment).append("]\n");
        return sBuilder.toString();
    }
}
