package de.saring.sportstracker.storage.db

import de.saring.sportstracker.core.STException
import de.saring.sportstracker.core.STExceptionID
import de.saring.sportstracker.data.ExerciseList
import de.saring.sportstracker.data.NoteList
import de.saring.sportstracker.data.SportTypeList
import de.saring.sportstracker.data.WeightList
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Class for reading / storing of the application data from / to a SQLite database. It manages the database connection
 * and provides repository instances for the specific entities.
 *
 * @author Stefan Saring
 */
class DbStorage {

    lateinit var noteRepository: NoteRepository private set
    lateinit var weightRepository: WeightRepository private set
    lateinit var exerciseRepository: ExerciseRepository private set
    lateinit var sportTypeRepository: SportTypeRepository private set

    private lateinit var connection: Connection

    @Throws(STException::class)
    fun openDatabase(dbFilename: String) {
        val jdbcUrl = "jdbc:sqlite:$dbFilename"
        openDatabaseConnection(jdbcUrl)

        // create initial database schema if new database was created
        if (isNewDatabase()) {
            LOGGER.info("Creating initial database schema")
            executeSchemaFile(1)
        }

        // check schema version of new or existing database and execute updates when needed
        validateSchemaVersion()

        noteRepository = NoteRepository(connection)
        weightRepository = WeightRepository(connection)
        exerciseRepository = ExerciseRepository(connection)
        sportTypeRepository = SportTypeRepository(connection)
    }

    fun closeDatabase() {
        LOGGER.info("Closing database")

        // connection might be null on application exit when opening the database has failed
        connection.let {
            try {
                it.close()
            } catch (e: Exception) {
                LOGGER.log(Level.SEVERE, "Failed to close opened SQLite database!", e)
            }
        }
    }

    @Throws(STException::class)
    fun commitChanges() {
        LOGGER.info("Committing database changes")

        try {
            connection.commit()
        } catch (e: SQLException) {
            throw STException(STExceptionID.DBSTORAGE_COMMIT_CHANGES, "Failed to commit database changes!'", e)
        }
    }

    private fun openDatabaseConnection(jdbcUrl: String) {
        LOGGER.info("Opening database $jdbcUrl")

        try {
            // new database file will be created if it doesn't exist yet
            connection = DriverManager.getConnection(jdbcUrl)
            // AutoCommit is default for SQLite, use own TX management instead
            connection.autoCommit = false
            // enable foreign key support (disabled by default)
            connection.createStatement().use { statement ->
                statement.executeUpdate("PRAGMA foreign_keys = ON")
            }
        } catch (e: SQLException) {
            throw STException(STExceptionID.DBSTORAGE_OPEN_DATABASE, "Failed to open SQLite database '$jdbcUrl'!", e)
        }
    }

    private fun isNewDatabase(): Boolean {
        try {
            // check by existence of database table 'META'
            connection.prepareStatement("SELECT name FROM sqlite_master WHERE TYPE='table' AND NAME='META'").use { statement ->
                val rs = statement.executeQuery()
                return !rs.next()
            }
        } catch (e: SQLException) {
            throw STException(STExceptionID.DBSTORAGE_INVALID_SCHEMA, "Failed to check whether database is new!", e)
        }
    }

    private fun executeSchemaFile(schemaVersion: Int) {
        val schemaFilename = SCHEMA_FILE_PREFIX + String.format("%03d", schemaVersion) + ".sql"
        LOGGER.info("Executing database schema version $schemaVersion from file '$schemaFilename'")

        try {
            val schemaText = DbStorage::class.java.getResource(schemaFilename).readText()
            connection.createStatement().use { statement ->
                statement.executeUpdate(schemaText)
            }
            connection.commit()
        } catch (e: Exception) {
            throw STException(STExceptionID.DBSTORAGE_CREATE_SCHEMA, "Failed to execute database schema file!", e)
        }
    }

    private fun validateSchemaVersion() {
        LOGGER.info("Validating existing database schema")

        var currentSchemaVersion = getSchemaVersion();
        if (currentSchemaVersion > SCHEMA_VERSION) {
            throw STException(
                STExceptionID.DBSTORAGE_INVALID_SCHEMA,
                "DB schema version is too new! Expected version $SCHEMA_VERSION, found version $currentSchemaVersion."
            )
        }

        if (currentSchemaVersion < SCHEMA_VERSION) {
            LOGGER.info("Database schema needs to get updated from version $currentSchemaVersion to $SCHEMA_VERSION")
            while (currentSchemaVersion < SCHEMA_VERSION) {
                executeSchemaFile(++currentSchemaVersion);
            }
        }
    }

    private fun getSchemaVersion(): Int {
        try {
            connection.prepareStatement("SELECT SCHEMA_VERSION FROM META").use { statement ->
                val rs = statement.executeQuery()
                rs.next()
                return rs.getInt("SCHEMA_VERSION")
            }
        } catch (e: SQLException) {
            throw STException(STExceptionID.DBSTORAGE_INVALID_SCHEMA, "Failed to read DB schema version!", e)
        }
    }

    @Throws(STException::class)
    fun importExistingApplicationData(sportTypes: SportTypeList,
                                      exercises: ExerciseList,
                                      notes: NoteList,
                                      weights: WeightList) {
        LOGGER.info("Importing existing application data to database")
        val dbImporter = DbApplicationDataImporter(connection)
        dbImporter.importApplicationData(sportTypes, exercises, notes, weights)
    }

    companion object {
        /** Filename for opening the database in in-memory mode, useful for unit testing. */
        const val IN_MEMORY_FILENAME = ":memory:"

        private val LOGGER = Logger.getLogger(NoteRepository::class.java.name)

        private const val SCHEMA_FILE_PREFIX = "/sql/st-schema-"
        private const val SCHEMA_VERSION = 2
    }
}