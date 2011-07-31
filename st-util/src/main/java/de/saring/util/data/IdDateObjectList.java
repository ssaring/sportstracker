package de.saring.util.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This list extends IdObjectList and contains unique instances of IdDateObject
 * subclasses. It and will also never contain multiple instances with the same
 * ID. The list is always sorted by the date of the IdDateObject instances.
 * 
 * @param T the object type to store in this list, must be a subclass of IdDateObject
 * 
 * @author  Stefan Saring
 * @version 1.0
 */
public class IdDateObjectList<T extends IdDateObject> extends IdObjectList<T> {

    /**
     * Stores the specified IdDateObject object in list. It will be inserted
     * into the correct list position, so that all IdDateObject's are sorted 
     * ascending by date. When there's allready an IdDateObject object with the
     * same ID then the old IdDateObject will be removed from list before.
     * @param t IdDateObject instance to store (must not be null and must have a date)
     */
    @Override
    public void set (T t) {

        if (t == null) {
            throw new IllegalArgumentException ("Must not be null!");
        }
        if (t.getId () <= 0) {
            throw new IllegalArgumentException ("ID must be a positive integer > 0!");
        }
        if (t.getDate () == null) {
            throw new IllegalArgumentException ("Date must not be null!");
        }

        // remove the object in the list if it's allready stored (same ID)
        if (getIDObjects ().contains (t)) {
            getIDObjects ().remove (t);
        }

        try {
            // insert the object by date order (or add it to the end)
            for (int i = 0; i < getIDObjects ().size (); i++) {
                IdDateObject temp = getIDObjects ().get (i);

                if (t.getDate ().before (temp.getDate ())) {
                    getIDObjects ().add (i, t);
                    return;
                }
            }
            getIDObjects ().add (t);
        }
        finally {
            notifyAllListChangelisteners ();
        }
    }

    /**
     * Returns all IdDateObject entries of this list for which their date is in
     * the specified time range.
     * @param dtStart start date of the time range (inclusive)
     * @param dtEnd end date of the time range (inclusive)
     * @return list of entries in this time range
     */
    public List<T> getEntriesInTimeRange (Date dtStart, Date dtEnd) {
        if (dtStart == null || dtEnd == null) {
            throw new IllegalArgumentException ("Dates of the time range must not be null!");
        }
        
        if (dtStart.after (dtEnd)) {
            throw new IllegalArgumentException ("Start date is after end date!");
        }
        
        List<T> lFound = new ArrayList<> ();
        for (T t : this) {        
            if (!t.getDate ().before (dtStart) &&
                !t.getDate ().after (dtEnd)) {
                lFound.add (t);
            }
        }
        return lFound;
    }    
}
