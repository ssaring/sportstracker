package de.saring.sportstracker.storage.db;

import de.saring.util.data.IdObject;

/**
 * Listener interface for observing data changes for a specific repository.
 *
 * @author Stefan Saring
 */
public interface RepositoryChangeListener {

    /**
     * This method will be called anytime when the data maintained by this repository has been changed.
     *
     * @param changedObject the added / changed object (or null when removed or all objects changed)
     */
    void dataChanged(IdObject changedObject);
}
