package de.saring.sportstracker.data;

/**
 * This class defines a note for a specific date (e.g. for creating training
 * plans in the calendar).
 *
 * @author Stefan Saring
 * @version 1.0
 */
public final class Note extends Entry {

    /**
     * Standard c'tor.
     *
     * @param id the ID of the object (null for new, not persisted objects)
     */
    public Note(Integer id) {
        super(id);
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
        clone.setDateTime(this.getDateTime());
        clone.setComment(this.getComment());
        return clone;
    }

    @Override
    public String toString() {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(this.getClass().getName()).append(":\n");
        sBuilder.append(" [id=").append(this.getId()).append("\n");
        sBuilder.append("  date=").append(this.getDateTime()).append("\n");
        sBuilder.append("  comment=").append(this.getComment()).append("]\n");
        return sBuilder.toString();
    }
}
