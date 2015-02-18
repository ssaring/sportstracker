package de.saring.util.data;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This list contains unique instances of IdObject subclasses. It will never
 * contain multiple instances with the same ID. It also provides usefull
 * methods for getting and removing instances by ID or by their index and for
 * getting new unique IDs.
 * Its possible to register IdObjectListChangeListener which will be informed
 * each time the list content has changed.
 *
 * @param <T> the object type to store in this list, must be a subclass of IdObject
 * @author Stefan Saring
 * @version 1.0
 */
public class IdObjectList<T extends IdObject> implements Iterable<T> {

    /**
     * Generic list of subclasses of IdObject.
     */
    private final List<T> lIdObjects = new ArrayList<>();

    /**
     * List of listeners which will be notified on each list content change.
     */
    private final List<IdObjectListChangeListener> listChangelisteners = new ArrayList<>();

    /**
     * Returns the IdObject with the specified ID.
     *
     * @param id ID of IdObject
     * @return the IdObject object or null
     */
    public T getByID(int id) {
        Optional<T> oIdObject = stream().filter(o -> o.getId() == id).findFirst();
        return oIdObject.orElse(null);
    }

    /**
     * Returns the IdObject at the specified index. Throws an
     * IndexOutOfBoundsException when the index is not valid.
     *
     * @param index the index of the IdObject
     * @return the IdObject
     */
    public T getAt(int index) {
        return lIdObjects.get(index);
    }

    /**
     * Returns the index of the specified object in the list or -1 if it is not
     * contained.
     *
     * @param t the object to lookup in the list
     * @return the index of the object or -1
     */
    public int indexOf(T t) {
        return lIdObjects.indexOf(t);
    }

    /**
     * Checks whether the specified object is contained in list.
     *
     * @param t the object to lookup in the list
     * @return true if the list contains the specified object
     */
    public boolean contains(T t) {
        return lIdObjects.contains(t);
    }

    /**
     * Stores the specified IdObject in the list. If there is already an
     * IDObject with that ID then the old object will be overwritten. Otherwise
     * the new one will be added to the end of the list.
     *
     * @param t the IdObject to store (must not be null)
     */
    public void set(T t) {
        validateEntry(t);

        try {
            int index = lIdObjects.indexOf(t);
            if (index >= 0) {
                // replace old IdObject if there is one with the ID of the new one
                this.lIdObjects.set(index, t);
            } else {
                // the object has a new ID => add to end of list
                this.lIdObjects.add(t);
            }
        } finally {
            notifyAllListChangelisteners(t);
        }
    }

    /**
     * Clears this IdObjectList and adds all IdObjects of the passed list.
     * Finally all registered ChangeListeners will be notified.
     *
     * @param entries list of IdObjects to store (must not be null, entries must not be null and all
     *            entries and must have a valid ID)
     */
    public void clearAndAddAll(final List<T> entries) {
        Objects.requireNonNull(entries, "List of IdObjects must not be null!");
        entries.forEach(entry -> validateEntry(entry));

        lIdObjects.clear();
        lIdObjects.addAll(entries);

        notifyAllListChangelisteners(null);
    }

    /**
     * Removes the IdObject with the specified ID from the list.
     *
     * @param id ID of IDObject to remove
     * @return true on success
     */
    public boolean removeByID(int id) {

        T t = getByID(id);
        if (t != null) {
            boolean removed = this.lIdObjects.remove(t);
            if (removed) {
                notifyAllListChangelisteners(null);
            }
            return removed;
        }
        return false;
    }

    /**
     * This method returns an unique ID, which is not in use yet.
     *
     * @return a new unused ID
     */
    public int getNewID() {
        Set<? super Integer> hsIDs = stream()
                .map(T::getId)
                .collect(Collectors.toSet());

        // find first unused ID
        int newID = 1;
        while (hsIDs.contains(newID)) {
            newID++;
        }
        return newID;
    }

    /**
     * Returns the size of the list.
     *
     * @return the size of the list
     */
    public int size() {
        return lIdObjects.size();
    }

    /**
     * Returns an interator over the list elements in proper sequence.
     *
     * @return interator over the list elements
     */
    @Override
    public Iterator<T> iterator() {
        return lIdObjects.iterator();
    }

    /**
     * Returns the Stream of the internal IDObject list for functional processing.
     *
     * @return the Stream of the internal IDObject list
     */
    public Stream<T> stream() {
        return lIdObjects.stream();
    }

    /**
     * Adds the specified IdObjectListChangeListener to the list of listeners
     * which will be notified on each list change.
     *
     * @param listener the IdObjectListChangeListener to add
     */
    public void addListChangeListener(IdObjectListChangeListener listener) {
        listChangelisteners.add(listener);
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
        lIdObjects.forEach(sBuilder::append);
        return sBuilder.toString();
    }

    /**
     * Notifies all registered listeners that the content of the list has been
     * changed.
     *
     * @param changedObject the added / changed object (or null when removed or all objects changed)
     */
    protected void notifyAllListChangelisteners(IdObject changedObject) {
        listChangelisteners.forEach(listener -> listener.listChanged(changedObject));
    }

    /**
     * Returns the internal list of IdObject. Only subclasses can directly
     * access this list.
     *
     * @return the internal list of IdObject
     */
    protected List<T> getIDObjects() {
        return lIdObjects;
    }

    /**
     * Validates the IdDateObject to be stored in this list. RuntimeExceptions will be thrown on errors.
     *
     * @param t entry to validate
     */
    protected void validateEntry(final T t) {
        Objects.requireNonNull(t, "IdObject must not be null!");

        if (t.getId() <= 0) {
            throw new IllegalArgumentException("ID must be a positive integer > 0!");
        }
    }
}
