package de.saring.sportstracker.storage.db

import de.saring.sportstracker.core.STException
import de.saring.sportstracker.core.STExceptionID
import de.saring.sportstracker.data.Weight
import de.saring.util.Date310Utils.dateToLocalDateTime
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import java.util.logging.Logger

/**
 * Database repository for the Weight data.
 *
 * @property connection database connection
 *
 * @author Stefan Saring
 */
class WeightRepository(
    connection: Connection
) : AbstractRepository<Weight>(connection) {

    // TODO refactoring: can create and update methods be unified or reuse parts?

    @Throws(STException::class)
    fun create(weight: Weight): Weight {
        logger.info("Creating new Weight")

        try {
            connection.prepareStatement("INSERT INTO WEIGHT (DATE_TIME, VALUE, COMMENT) VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
            ).use { statement ->
                statement.setString(1, RepositoryUtil.dateTimeToString(weight.dateTime))
                statement.setFloat(2, weight.value)
                // TODO test that persisted string is null in DB when no comment is given
                statement.setString(3, weight.comment)
                statement.executeUpdate()

                val rs = statement.generatedKeys
                rs.next()
                val weightID = rs.getInt(1)
                return readById(weightID)
            }
        } catch (e: SQLException) {
            throw STException(STExceptionID.DBSTORAGE_CREATE_ENTRY, "Failed to create new Weight!", e)
        }
    }

    @Throws(STException::class)
    fun update(weight: Weight) {
        logger.info("Updating Weight with ID '${weight.id}'")

        try {
            connection.prepareStatement("UPDATE WEIGHT SET DATE_TIME = ?, VALUE = ?, COMMENT = ? WHERE ID = ?"
            ).use { statement ->
                statement.setString(1, RepositoryUtil.dateTimeToString(weight.dateTime))
                statement.setFloat(2, weight.value)
                // TODO test that persisted string is null in DB when no comment is given
                statement.setString(3, weight.comment)
                statement.setInt(4, weight.id!!)
                statement.executeUpdate()
            }
        } catch (e: SQLException) {
            throw STException(STExceptionID.DBSTORAGE_UPDATE_ENTRY, "Failed to update Weight with ID '${weight.id}'!", e)
        }
    }

    override val entityName = "Weight"

    override val tableName = "WEIGHT"

    override val logger: Logger = Logger.getLogger(WeightRepository::class.java.name)

    override fun readFromResultSet(rs: ResultSet): Weight {
        val weight = Weight(rs.getInt("ID"))
        weight.dateTime = dateToLocalDateTime(rs.getDate("DATE_TIME"))
        weight.value = rs.getFloat("VALUE")
        weight.comment = rs.getString("COMMENT")
        return weight
    }
}