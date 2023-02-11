package de.saring.util.data

import java.util.stream.Collectors
import java.util.stream.Stream

/**
 * This list contains unique instances of IdObject subclasses. It will never contain multiple instances with  the same
 * ID. It also provides useful methods for getting and removing instances by ID or by their index and for getting new
 * unique IDs.
 * Its possible to register IdObjectListChangeListener which will be informed each time the list content has changed.
 *
 * @param <T> the object type to store in this list, must be a subclass of IdObject
 *
 * @author Stefan Saring
 */
open class IdObjectList<T : IdObject> : Iterable<T> {

    /**
     * Generic list of subclasses of IdObject. Only subclasses can directly access this list.
     */
    protected val idObjects = mutableListOf<T>()

    /**
     * List of listeners which will be notified on each list content change.
     */
    private val listChangeListeners = mutableListOf<IdObjectListChangeListener>()

    /**
     * This method returns an unique ID, which is not in use yet.
     *
     * @return a new unused ID
     */
    fun getNewId(): Long {

        val hsIDsInUse = stream()
                .map { it.id }
                .collect(Collectors.toSet())

        var newID = 1L
        while (hsIDsInUse.contains(newID)) {
            newID++
        }
        return newID
    }

    /**
     * Returns the IdObject with the specified ID.
     *
     * @param id ID of IdObject
     * @return the IdObject object or null
     */
    fun getByID(id: Long): T? {
        return stream()
                .filter { o -> o.id == id }
                .findFirst()
                .orElse(null)
    }

    /**
     * Returns the IdObject at the specified index. Throws an  IndexOutOfBoundsException when the index is not valid.
     *
     * @param index the index of the IdObject
     * @return the IdObject
     */
    fun getAt(index: Int): T = idObjects[index]
  
    /**
     * Returns the index of the specified object in the list or -1 if it is not contained.
     *
     * @param t the object to lookup in the list
     * @return the index of the object or -1
     */
    fun indexOf(t: T): Int = idObjects.indexOf(t)

    /**
     * Checks whether the specified object is contained in list.
     *
     * @param t the object to lookup in the list
     * @return true if the list contains the specified object
     */
    fun contains(t: T): Boolean = idObjects.contains(t)

    /**
     * Stores the specified IdObject in the list. If there is already an IDObject with that ID then the old object will
     * be overwritten. Otherwise the new one will be added to the end of the list.
     *
     * @param t the IdObject to store
     */
    open fun set(t: T) {

        try {
            val index = idObjects.indexOf(t)
            if (index >= 0) {
                // replace old IdObject if there is one with the ID of the new one
                this.idObjects[index] = t
            } else {
                // the object has a new ID => add to end of list
                this.idObjects.add(t)
            }
        } finally {
            notifyAllListChangelisteners(t)
        }
    }

    /**
     * Clears this IdObjectList and adds all IdObjects of the passed list. Finally all registered ChangeListeners will
     * be notified.
     *
     * @param entries list of IdObjects to store (must not be null, entries must not be null and all entries and must have a valid ID)
     */
    open fun clearAndAddAll(entries: List<T>) {

        idObjects.clear()
        idObjects.addAll(entries)
        notifyAllListChangelisteners(null)
    }

    /**
     * Removes the IdObject with the specified ID from the list.
     *
     * @param id ID of IdObject to remove
     * @return true on success
     */
    fun removeByID(id: Long): Boolean {
        var removed = false

        val idObject = getByID(id)
        if (idObject != null) {
            removed = this.idObjects.remove(idObject)
        }

        if (removed) {
            notifyAllListChangelisteners(null)
        }
        return removed
    }

    /**
     * Returns the size of the list.
     *
     * @return the size of the list
     */
    fun size(): Int = idObjects.size

    /**
     * Returns an iterator over the list elements in proper sequence.
     *
     * @return iterator over the list elements
     */
    override fun iterator(): Iterator<T> = idObjects.iterator()

    /**
     * Returns the Stream of the internal IdObject list for functional processing.
     *
     * @return the Stream of the internal IdObject list
     */
    fun stream(): Stream<T> = idObjects.stream()

    /**
     * Adds the specified IdObjectListChangeListener to the list of listeners which will be notified on each list
     * change.
     *
     * @param listener the IdObjectListChangeListener to add
     */
    fun addListChangeListener(listener: IdObjectListChangeListener) {
        listChangeListeners.add(listener)
    }

    /**
     * Returns a string representation of this object.
     *
     * @return string with object content
     */
    override fun toString(): String = "${this.javaClass.name}: size=${idObjects.size}"

    /**
     * Notifies all registered listeners that the content of the list has been changed.
     *
     * @param changedObject the added / changed object (or null when removed or all objects changed)
     */
    protected fun notifyAllListChangelisteners(changedObject: IdObject?) {
        listChangeListeners.forEach { it.listChanged(changedObject) }
    }
}
