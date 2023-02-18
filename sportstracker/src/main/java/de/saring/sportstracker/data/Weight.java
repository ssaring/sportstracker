package de.saring.sportstracker.data;

/**
 * This class defines a body weight entry for a specific date.
 *
 * @author Stefan Saring
 */
public final class Weight extends Entry {

    /**
     * The weight value in kilograms.
     */
    private double value;

    /**
     * Standard c'tor.
     *
     * @param id the ID of the object
     */
    public Weight(Long id) {
        super(id);
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    /**
     * Returns a complete clone of this Weight object. All the attributes are
     * the same, but the ID of the clone is the specified one.
     *
     * @param cloneId ID of the cloned Weight
     * @return the Weight clone
     */
    public Weight clone(Long cloneId) {
        Weight clone = new Weight(cloneId);
        clone.setDateTime(this.getDateTime());
        clone.setValue(this.getValue());
        clone.setComment(this.getComment());
        return clone;
    }

    @Override
    public String toString() {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(this.getClass().getName()).append(":\n");
        sBuilder.append(" [id=").append(this.getId()).append("\n");
        sBuilder.append("  date=").append(this.getDateTime()).append("\n");
        sBuilder.append("  value=").append(this.value).append("\n");
        sBuilder.append("  comment=").append(this.getComment()).append("]\n");
        return sBuilder.toString();
    }
}
