package de.saring.sportstracker.storage.db

import de.saring.sportstracker.data.Weight
import de.saring.util.Date310Utils.dateToLocalDateTime
import java.sql.Connection
import java.sql.ResultSet
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

    override val entityName = "Weight"

    override val tableName = "WEIGHT"

    override val logger: Logger = Logger.getLogger(WeightRepository::class.java.name)

    override fun readFromResultSet(rs: ResultSet): Weight {
        val weight = Weight(rs.getLong("ID"))
        weight.dateTime = dateToLocalDateTime(rs.getDate("DATE_TIME"))
        weight.value = rs.getFloat("VALUE")
        weight.comment = rs.getString("COMMENT")
        return weight
    }

    override fun executeCreate(entry: Weight): Weight {
        connection.prepareStatement("INSERT INTO WEIGHT (DATE_TIME, VALUE, COMMENT) VALUES (?, ?, ?)",
            Statement.RETURN_GENERATED_KEYS
        ).use { statement ->
            statement.setString(1, RepositoryUtil.dateTimeToString(entry.dateTime))
            statement.setFloat(2, entry.value)
            statement.setString(3, entry.comment)
            statement.executeUpdate()

            val rs = statement.generatedKeys
            rs.next()
            val weightID = rs.getLong(1)
            return readById(weightID)
        }
    }

    override fun executeUpdate(entry: Weight) {
        connection.prepareStatement("UPDATE WEIGHT SET DATE_TIME = ?, VALUE = ?, COMMENT = ? WHERE ID = ?"
        ).use { statement ->
            statement.setString(1, RepositoryUtil.dateTimeToString(entry.dateTime))
            statement.setFloat(2, entry.value)
            statement.setString(3, entry.comment)
            statement.setLong(4, entry.id!!)
            statement.executeUpdate()
        }
    }
}