package de.saring.sportstracker.data;

import de.saring.util.data.IdDateObject;

/**
 * This class defines a note for a specific date (e.g. for creating training
 * plans in the calendar).
 *
 * @author Stefan Saring
 * @version 1.0
 */
public final class Note extends IdDateObject {

    /**
     * The text of the note.
     */
    private String text;

    /**
     * Standard c'tor.
     *
     * @param id the ID of the object
     */
    public Note(int id) {
        super(id);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    /**
     * Returns a complete clone of this Note object. All the attributes are the
     * same, but the ID of the clone is the specified one.
     *
     * @param cloneId ID of the cloned Note
     * @return the Note clone
     */
    public Note clone(int cloneId) {
        Note clone = new Note(cloneId);
        clone.setDate(this.getDate());
        clone.setText(this.getText());
        return clone;
    }

    @Override
    public String toString() {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(this.getClass().getName()).append(":\n");
        sBuilder.append(" [id=").append(this.getId()).append("\n");
        sBuilder.append("  date=").append(this.getDate()).append("\n");
        sBuilder.append("  text=").append(this.text).append("]\n");
        return sBuilder.toString();
    }
}
