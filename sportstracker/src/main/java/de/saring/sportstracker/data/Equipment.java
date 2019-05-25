package de.saring.sportstracker.data;

import de.saring.util.data.IdObject;
import de.saring.util.data.Nameable;

/**
 * This class defines one possible equipment of a sport type (e.g. the specific
 * road bike for cycling).
 *
 * @author Stefan Saring
 * @version 1.0
 */
public final class Equipment extends IdObject implements Nameable, Cloneable {

    /**
     * Name of the equipment.
     */
    private String name;

    /**
     * Flag whether this equipment is not in use anymore (e.g. worn out shoes).
     */
    private boolean notInUse = false;

    /**
     * Standard c'tor.
     *
     * @param id the ID of the object
     */
    public Equipment(int id) {
        super(id);
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isNotInUse() {
        return notInUse;
    }

    public void setNotInUse(boolean notInUse) {
        this.notInUse = notInUse;
    }

    @Override
    public String toString() {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(this.getClass().getName()).append(":\n");
        sBuilder.append(" [id=").append(this.getId()).append("\n");
        sBuilder.append("  name=").append(this.name).append("\n");
        sBuilder.append("  notInUse=").append(this.notInUse).append("]\n");
        return sBuilder.toString();
    }

    /**
     * Returns a deep clone copy of this Equipment object.
     *
     * @return clone of this object
     */
    @Override
    public Equipment clone() {
        try {
            // nothing more to do (contains only primitives and an immutable string)
            // (the exception can't happen)
            return (Equipment) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError();
        }
    }
}
