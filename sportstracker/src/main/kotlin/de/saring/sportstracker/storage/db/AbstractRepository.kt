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
    protected val connection: Connection
) {

    @Throws(STException::class)
    open fun readAll(): List<T> {
        logger.info("Reading all $entityName entries")
        val entries = mutableListOf<T>()

        try {
            connection.prepareStatement("SELECT * FROM $tableName").use { statement ->
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
    open fun readById(entryId: Long): T {
        logger.info("Reading $entityName with ID '$entryId'")

        try {
            connection.prepareStatement("SELECT * FROM $tableName WHERE ID = ?").use { statement ->
                statement.setLong(1, entryId)
                val rs: ResultSet = statement.executeQuery()
                rs.next()
                return readFromResultSet(rs)
            }
        } catch (e: SQLException) {
            throw STException(STExceptionID.DBSTORAGE_READ_ENTRY, "Failed to read $entityName with ID '$entryId'!", e)
        }
    }

    @Throws(STException::class)
    fun create(entry: T): T {
        logger.info("Creating new $entityName")

        try {
            return executeCreate(entry)
        } catch (e: SQLException) {
            throw STException(STExceptionID.DBSTORAGE_CREATE_ENTRY, "Failed to create new $entityName!", e)
        }
    }

    @Throws(STException::class)
    fun update(entry: T) {
        logger.info("Updating $entityName with ID '${entry.id}'")

        try {
            executeUpdate(entry)
        } catch (e: SQLException) {
            throw STException(STExceptionID.DBSTORAGE_UPDATE_ENTRY, "Failed to update $entityName with ID '${entry.id}'!", e)
        }
    }

    @Throws(STException::class)
    fun delete(entryId: Long) {
        logger.info("Deleting $entityName with ID '$entryId'")

        try {
            connection.prepareStatement("DELETE FROM $tableName WHERE ID = ?").use { statement ->
                statement.setLong(1, entryId)
                statement.executeUpdate()
            }
        } catch (e: SQLException) {
            throw STException(STExceptionID.DBSTORAGE_DELETE_ENTRY, "Failed to delete $entityName with ID '$entryId'!", e)
        }
    }

    protected abstract val entityName: String
    protected abstract val tableName: String

    protected abstract val logger: Logger

    protected abstract fun readFromResultSet(rs: ResultSet): T

    protected abstract fun executeCreate(entry: T): T
    protected abstract fun executeUpdate(entry: T)
}