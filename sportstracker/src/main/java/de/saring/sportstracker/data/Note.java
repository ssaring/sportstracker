package de.saring.sportstracker.data;

/**
 * This class defines a note for a specific date (e.g. for creating training plans in the calendar).
 *
 * @author Stefan Saring
 */
public final class Note extends Entry {

    /**
     * The sport type which this note is referring (optional).
     */
    private SportType sportType;

    /**
     * The equipment which this note is referring (optional).
     */
    private Equipment equipment;

    /**
     * Standard c'tor.
     *
     * @param id the ID of the object (null for new, not persisted objects)
     */
    public Note(Long id) {
        super(id);
    }

    public SportType getSportType() {
        return sportType;
    }

    public void setSportType(SportType sportType) {
        this.sportType = sportType;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    /**
     * Returns a complete clone of this Note object. All the attributes are the
     * same, but the ID of the clone is the specified one.
     *
     * @param cloneId ID of the cloned Note
     * @return the Note clone
     */
    public Note clone(Long cloneId) {
        Note clone = new Note(cloneId);
        clone.setDateTime(this.getDateTime());
        clone.setSportType(this.getSportType());
        clone.setEquipment(this.getEquipment());
        clone.setComment(this.getComment());
        return clone;
    }

    @Override
    public String toString() {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(this.getClass().getName()).append(":\n");
        sBuilder.append(" [id=").append(this.getId()).append("\n");
        sBuilder.append("  date=").append(this.getDateTime()).append("\n");
        sBuilder.append("  sportType=").append(this.sportType).append("\n");
        sBuilder.append("  equipment=").append(this.equipment).append("\n");
        sBuilder.append("  comment=").append(this.getComment()).append("]\n");
        return sBuilder.toString();
    }
}
