package de.saring.sportstracker.storage.db

import de.saring.sportstracker.data.Weight
import de.saring.util.Date310Utils.dateToLocalDateTime
import java.sql.Connection
import java.sql.ResultSet
import java.util.logging.Logger

/**
 * Database repository for the Weight data.
 *
 * @property connection database connection
 *
 * @author Stefan Saring
 */
class WeightRepository(
    private val connection: Connection
) : AbstractRepository<Weight>(connection) {

    override val entityName = "Weight"

    override val logger: Logger = Logger.getLogger(WeightRepository::class.java.name)

    override fun readFromResultSet(rs: ResultSet): Weight {
        val weight = Weight(rs.getInt("ID"))
        weight.dateTime = dateToLocalDateTime(rs.getDate("DATE_TIME"))
        weight.value = rs.getFloat("VALUE")
        weight.comment = rs.getString("COMMENT")
        return weight
    }
}