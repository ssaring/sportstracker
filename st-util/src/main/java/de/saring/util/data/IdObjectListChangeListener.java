package de.saring.util.data;

/**
 * Listener interface for observing changes in the IdObjectList.
 *
 * @author Stefan Saring
 * @version 1.0
 */
public interface IdObjectListChangeListener {

    /**
     * This method will be called anytime when the list content has been
     * modified (new objects were added or old were removed). It will not be
     * called when the objects in the list get modified. The passed
     * changedObject is the added or updated IdObject. It is null when an object
     * was removed.
     *
     * @param changedObject the added / changed object (or null when removed)
     */
    void listChanged(IdObject changedObject);
}
