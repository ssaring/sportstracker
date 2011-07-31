package de.saring.util.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * This list contains unique instances of IdObject subclasses. It will never
 * contain multiple instances with the same ID. It also provides usefull
 * methods for getting and removing instances by ID or by their index and for
 * getting new unique IDs.
 * Its possible to register IdObjectListChangeListener which will be informed
 * each time the list content has changed.
 * 
 * @param T the object type to store in this list, must be a subclass of IdObject
 * 
 * @author  Stefan Saring
 * @version 1.0
 */
public class IdObjectList<T extends IdObject> implements Iterable<T>, Serializable {

    /** Generic list of subclasses of IdObject. */
    private List<T> lIdObjects = new ArrayList<> ();

    /** List of listeners which will be notified on each list content change. */
    private List<IdObjectListChangeListener> listChangelisteners = new ArrayList<> ();

    /**
     * Returns the IdObject with the specified ID.
     * @param id ID of IdObject
     * @return the IdObject object or null
     */
    public T getByID (int id) {
        for (T t : this.lIdObjects) {
            if (t.getId () == id) {
                return t;
            }
        }
        return null;
    }
    
    /**
     * Returns the IdObject at the specified index. Throws an
     * IndexOutOfBoundsException when the index is not valid.
     * @param index the index of the IdObject
     * @return the IdObject
     */
    public T getAt (int index) {
        return lIdObjects.get (index);
    }
    
    /**
     * Returns the index of the specified object in the list or -1 if it is not
     * contained.
     * @param t the object to lookup in the list
     * @return the index of the object or -1
     */
    public int indexOf (T t) {
        return lIdObjects.indexOf (t);
    }
    
    /**
     * Stores the specified IdObject in the list. If there is allready an
     * IDObject with that ID then the old object will be overwritten.
     * Otherwise the new one will be added to the end of the list.
     * @param t the IdObject to store (must not be null)
     */
    public void set (T t) {
        if (t == null) {
            throw new IllegalArgumentException ("Must not be null!");
        }
        if (t.getId () <= 0) {
            throw new IllegalArgumentException ("ID must be a positive integer > 0!");
        }

        try {
            // replace old IdObject if there is one with the ID of the new one
            for (int i = 0; i < this.lIdObjects.size (); i++) {
                T temp = this.lIdObjects.get (i);
                if (t.getId () == temp.getId ()) {
                    this.lIdObjects.set (i, t);
                    return;
                }
            }

            // the object has a new ID => add to end of list
            this.lIdObjects.add (t);
        }
        finally {
            notifyAllListChangelisteners ();
        }
    }
    
        
    /**
     * Removes the IdObject with the specified ID from the list.
     * @param id ID of IDObject to remove
     * @return true on success
     */
    public boolean removeByID (int id) {
        
        T t = getByID (id);
        if (t != null) {
            boolean removed = this.lIdObjects.remove (t);
            if (removed) {
                notifyAllListChangelisteners ();
            }
            return removed;
        }
        return false;
    }
    
    /** 
     * This method returns an unique ID, which is not in use yet. 
     * @return a new unused ID
     */
    public int getNewID () {
        
        HashSet<Integer> hsIDs = new HashSet<> ();
        for (T t : this.lIdObjects) {
            hsIDs.add (t.getId ());
        }

        // find first unused ID
        int newID = 1;
        while (hsIDs.contains (newID)) {
            newID++;
        }
        return newID;
    }
    
    /**
     * Returns the size of the list.
     * @return the size of the list
     */
    public int size () {
        return lIdObjects.size ();
    }
    
    /**
     * Returns an interator over the list elements in proper sequence.
     * @return interator over the list elements
     */
    @Override
    public Iterator<T> iterator () {
        return lIdObjects.iterator ();
    }

    /**
     * Returns the internal list of IdObject. Only subclasses can directly
     * access this list.
     * @return the internal list of IdObject
     */
    protected List<T> getIDObjects () {
        return lIdObjects;
    } 

    /**
     * Adds the specified IdObjectListChangeListener to the list of listeners
     * which will be notified on each list change.
     * @param listener the IdObjectListChangeListener to add
     */
    public void addListChangeListener (IdObjectListChangeListener listener) {
        listChangelisteners.add (listener);
    }

    /**
     * Notifies all registered listeners that the content of the list has been
     * changed.
     */
    protected void notifyAllListChangelisteners () {
        for (IdObjectListChangeListener listener : listChangelisteners) {
            listener.listChanged ();
        }
    }

    /** 
     * Returns a string representation of this object. 
     * @return string with object content
     */
    @Override
    public String toString () {        
        StringBuilder sBuilder = new StringBuilder ();
        sBuilder.append (this.getClass ().getName () + ":\n");
        
        if (this.lIdObjects != null) {
            for (T t : this.lIdObjects) {
                sBuilder.append (t);
            }
        }
        return sBuilder.toString ();
    }
}
