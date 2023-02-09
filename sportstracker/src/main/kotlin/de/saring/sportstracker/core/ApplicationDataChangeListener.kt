package de.saring.sportstracker.core

import de.saring.util.data.IdObject

/**
 * Listener interface for observing changes of the SportsTracker application data.
 *
 * @author Stefan Saring
 */
interface ApplicationDataChangeListener {

    /**
     * This method will be called anytime when some application data (Exercises, Notes, etc) was modified. The passed
     * changedObject is the added or updated IdObject.
     *
     * @param changedObject the added / changed object (or null when object was removed or all objects were changed)
     */
    fun applicationDataChanged(changedObject: IdObject?)
}
