package de.saring.sportstracker.storage.db

import de.saring.sportstracker.core.STException
import de.saring.sportstracker.core.STExceptionID
import de.saring.util.data.IdObject
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.util.logging.Logger

/**
 * Abstract base class for all IdObject based entity repositories.
 *
 * @property connection database connection
 *
 * @author Stefan Saring
 */
abstract class AbstractRepository<T : IdObject>(
    private val connection: Connection
) {

    @Throws(STException::class)
    fun readAll(): List<T> {
        logger.info("Reading all $entityName entries")
        val entries = mutableListOf<T>()

        try {
            connection.prepareStatement("SELECT * FROM $entityName").use { statement ->
                val rs: ResultSet = statement.executeQuery()
                while (rs.next()) {
                    entries.add(readFromResultSet(rs))
                }
            }
        } catch (e: SQLException) {
            throw STException(STExceptionID.DBSTORAGE_READ_ALL, "Failed to read all $entityName entries!", e)
        }
        return entries
    }

    @Throws(STException::class)
    fun readById(entryId: Int): T {
        logger.info("Reading $entityName with ID '$entryId'")

        try {
            connection.prepareStatement("SELECT * FROM $entityName WHERE ID = ?").use { statement ->
                statement.setInt(1, entryId)
                val rs: ResultSet = statement.executeQuery()
                rs.next()
                return readFromResultSet(rs)
            }
        } catch (e: SQLException) {
            throw STException(STExceptionID.DBSTORAGE_READ_ENTRY, "Failed to read $entityName with ID '$entryId'!", e)
        }
    }

    @Throws(STException::class)
    fun delete(entry: T) {
        logger.info("Deleting $entityName with ID '${entry.id}'")

        try {
            connection.prepareStatement("DELETE FROM $entityName WHERE ID = ?").use { statement ->
                statement.setInt(1, (entry.id)!!)
                statement.executeUpdate()
            }
        } catch (e: SQLException) {
            throw STException(STExceptionID.DBSTORAGE_DELETE_ENTRY, "Failed to delete $entityName with ID '${entry.id}'!", e)
        }
    }

    /**
     * The name of the entity class, needs to be the same for the database table.
     */
    protected abstract val entityName: String

    // TODO why is the Logger name always 'AbstractRepository' when using the logger from the base class?
    protected abstract val logger: Logger

    protected abstract fun readFromResultSet(rs: ResultSet): T
}