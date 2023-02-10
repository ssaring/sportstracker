package de.saring.sportstracker.storage.db

import de.saring.sportstracker.core.STException
import de.saring.sportstracker.core.STExceptionID
import de.saring.sportstracker.data.Exercise
import de.saring.sportstracker.data.SportType
import de.saring.sportstracker.storage.db.RepositoryUtil.getEquipmentById
import de.saring.sportstracker.storage.db.RepositoryUtil.getIntegerOrNull
import de.saring.sportstracker.storage.db.RepositoryUtil.getSportSubTypeById
import de.saring.sportstracker.storage.db.RepositoryUtil.getSportTypeById
import de.saring.util.Date310Utils.dateToLocalDateTime
import java.lang.UnsupportedOperationException
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.util.logging.Logger

/**
 * Database repository for the Exercise data.
 *
 * @property connection database connection
 *
 * @author Stefan Saring
 */
class ExerciseRepository(
    connection: Connection
) : AbstractRepository<Exercise>(connection) {

    @Throws(STException::class)
    fun readAll(sportTypes: List<SportType>): List<Exercise> {
        logger.info("Reading all Exercises")
        val exercises = ArrayList<Exercise>()

        try {
            connection.prepareStatement("SELECT * FROM EXERCISE").use { statement ->
                val rs = statement.executeQuery()
                while (rs.next()) {
                    val exercise = readFromResultSet(rs)

                    val sportType = getSportTypeById(sportTypes, rs.getInt("SPORT_TYPE_ID"))
                    exercise.sportType = sportType
                    exercise.sportSubType = getSportSubTypeById(sportType, rs.getInt("SPORT_SUBTYPE_ID"))

                    val equipmentID = getIntegerOrNull(rs, "EQUIPMENT_ID")
                    exercise.equipment = if (equipmentID == null) null else getEquipmentById(sportType, equipmentID)
                    exercises.add(exercise)
                }
            }
        } catch (e: SQLException) {
            throw STException(STExceptionID.DBSTORAGE_READ_ALL, "Failed to read all Exercises!", e)
        }
        return exercises
    }

    override fun readAll(): List<Exercise> {
        throw UnsupportedOperationException("Use readAll(List<SportType>) for reading all Exercises!")
    }

    override fun readById(entryId: Int): Exercise {
        throw UnsupportedOperationException("Use readById(Int, List<SportType>) for reading an Exercise!")
    }

    override val entityName = "Exercise"

    override val tableName = "EXERCISE"

    override val logger: Logger = Logger.getLogger(ExerciseRepository::class.java.name)

    override fun readFromResultSet(rs: ResultSet): Exercise {
        val exercise = Exercise(rs.getInt("ID"))
        exercise.dateTime = dateToLocalDateTime(rs.getDate("DATE_TIME"))
        exercise.intensity = Exercise.IntensityType.valueOf(rs.getString("INTENSITY"))
        exercise.duration = rs.getInt("DURATION")
        exercise.distance = rs.getFloat("DISTANCE")
        exercise.avgSpeed = rs.getFloat("AVG_SPEED")
        exercise.avgHeartRate = rs.getInt("AVG_HEARTRATE")
        exercise.ascent = rs.getInt("ASCENT")
        exercise.descent = rs.getInt("DESCENT")
        exercise.calories = rs.getInt("CALORIES")
        exercise.hrmFile = rs.getString("HRM_FILE")
        exercise.comment = rs.getString("COMMENT")
        return exercise
    }
}