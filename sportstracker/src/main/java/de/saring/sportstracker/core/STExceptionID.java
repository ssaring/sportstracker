package de.saring.sportstracker.core;

/**
 * This is the list of possible exception ID's of the application.
 *
 * @author Stefan Saring
 * @version 1.0
 */
public enum STExceptionID {
    /**
     * schema validation error while parsing XML files
     */
    XMLSTORAGE_SCHEMA_VALIDATION,
    /**
     * failed to read sport type list from XML file
     */
    XMLSTORAGE_READ_SPORT_TYPE_LIST,
    /**
     * failed to write sport type list to XML file
     */
    XMLSTORAGE_STORE_SPORT_TYPE_LIST,
    /**
     * failed to read exercise list from XML file
     */
    XMLSTORAGE_READ_EXERCISE_LIST,
    /**
     * failed to write exercise list to XML file
     */
    XMLSTORAGE_STORE_EXERCISE_LIST,
    /**
     * failed to read note list from XML file
     */
    XMLSTORAGE_READ_NOTE_LIST,
    /**
     * failed to write note list to XML file
     */
    XMLSTORAGE_STORE_NOTE_LIST,
    /**
     * failed to read weight list from XML file
     */
    XMLSTORAGE_READ_WEIGHT_LIST,
    /**
     * failed to write weight list to XML file
     */
    XMLSTORAGE_STORE_WEIGHT_LIST,

    /**
     * failed to open SportsTracker SQLite database
     */
    DBSTORAGE_OPEN_DATABASE,
    /**
     * failed to create schema in new database
     */
    DBSTORAGE_CREATE_SCHEMA,
    /**
     * failed to close SportsTracker SQLite database
     */
    DBSTORAGE_CLOSE_DATABASE,
    /**
     * failed to validate database schema version
     */
    DBSTORAGE_INVALID_SCHEMA,
    /**
     * failed to commit all database changes
     */
    DBSTORAGE_COMMIT_CHANGES,
    /**
     * failed to read all specific entries from database
     */
    DBSTORAGE_READ_ALL,
    /**
     * failed to read a specific entry by ID from database
     */
    DBSTORAGE_READ_ENTRY,
    /**
     * failed to create a specific new entry in database
     */
    DBSTORAGE_CREATE_ENTRY,
    /**
     * failed to update a specific existing entry in database
     */
    DBSTORAGE_UPDATE_ENTRY,
    /**
     * failed to delete a specific entry from database
     */
    DBSTORAGE_DELETE_ENTRY,

    /**
     * failed to create application directory
     */
    DOCUMENT_CREATE_APP_DIRECTORY,

    /**
     * failed to export application data to SQLite
     */
    SQLITE_EXPORT,

    /**
     * failed to parse the distance entry in the exercise dialog
     */
    GUI_EXERCISEDIALOG_INVALID_DISTANCE,
    /**
     * failed to parse the AVG speed entry in the exercise dialog
     */
    GUI_EXERCISEDIALOG_INVALID_AVGSPEED,
    /**
     * failed to parse the duration entry in the exercise dialog
     */
    GUI_EXERCISEDIALOG_INVALID_DURATION,
}
