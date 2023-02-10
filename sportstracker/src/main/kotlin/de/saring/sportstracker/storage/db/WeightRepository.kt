package de.saring.sportstracker.storage.db

import de.saring.sportstracker.core.STException
import de.saring.sportstracker.core.STExceptionID
import de.saring.sportstracker.data.Weight
import de.saring.util.Date310Utils.dateToLocalDateTime
import java.sql.Connection
import java.sql.SQLException
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
) {

    @Throws(STException::class)
    fun readAllWeights(): List<Weight>? {
        LOGGER.info("Reading all Weights")
        val weights = ArrayList<Weight>()

        try {
            connection.prepareStatement("SELECT * FROM WEIGHT").use { statement ->
                val rs = statement.executeQuery()
                while (rs.next()) {
                    val weight = Weight(rs.getInt("ID"))
                    weight.dateTime = dateToLocalDateTime(rs.getDate("DATE_TIME"))
                    weight.value = rs.getFloat("VALUE")
                    weight.comment = rs.getString("COMMENT")
                    weights.add(weight)
                }
            }
        } catch (e: SQLException) {
            throw STException(STExceptionID.DBSTORAGE_READ_WEIGHTS, "Failed to read all Weights!", e)
        }
        return weights
    }

    companion object {
        private val LOGGER = Logger.getLogger(WeightRepository::class.java.name)
    }
}