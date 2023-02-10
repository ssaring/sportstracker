package de.saring.sportstracker.storage.db

import de.saring.sportstracker.core.STException
import de.saring.sportstracker.core.STExceptionID
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
        LOGGER.info("Opening database $dbFilename")
        val jdbcUrl = "jdbc:sqlite:$dbFilename"

        try {
            connection = DriverManager.getConnection(jdbcUrl)
            // AutoCommit is default for SQLite, use own TX management instead
            connection.autoCommit = false
        } catch (e: SQLException) {
            throw STException(STExceptionID.DBSTORAGE_OPEN_DATABASE, "Failed to open SQLite database '$jdbcUrl'!", e)
        }

        validateSchemaVersion()
        noteRepository = NoteRepository(connection)
        weightRepository = WeightRepository(connection)
        exerciseRepository = ExerciseRepository(connection)
        sportTypeRepository = SportTypeRepository(connection)
    }

    fun closeDatabase() {
        LOGGER.info("Closing database")

        // connection might be null on application exit when opening the database has failed
        connection?.let {
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

    private fun validateSchemaVersion() {
        try {
            connection.prepareStatement("SELECT SCHEMA_VERSION FROM META").use { statement ->
                val rs = statement.executeQuery()
                rs.next()
                val schemaVersion = rs.getInt("SCHEMA_VERSION")
                if (schemaVersion != SCHEMA_VERSION) {
                    throw STException(
                        STExceptionID.DBSTORAGE_INVALID_SCHEMA,
                        "DB schema version is invalid! Expected version $SCHEMA_VERSION, found version $schemaVersion."
                    )
                }
            }
        } catch (e: SQLException) {
            throw STException(STExceptionID.DBSTORAGE_INVALID_SCHEMA, "Failed to read DB schema version!", e)
        }
    }

    companion object {
        private val LOGGER = Logger.getLogger(NoteRepository::class.java.name)
        private const val SCHEMA_VERSION = 1
    }
}