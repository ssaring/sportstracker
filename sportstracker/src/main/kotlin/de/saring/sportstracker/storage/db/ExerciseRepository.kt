package de.saring.sportstracker.storage.db

import de.saring.sportstracker.core.STException
import de.saring.sportstracker.core.STExceptionID
import de.saring.sportstracker.data.*
import de.saring.sportstracker.storage.db.RepositoryUtil.getEquipmentById
import de.saring.sportstracker.storage.db.RepositoryUtil.getIntegerOrNull
import de.saring.sportstracker.storage.db.RepositoryUtil.getSportSubTypeById
import de.saring.sportstracker.storage.db.RepositoryUtil.getSportTypeById
import de.saring.util.Date310Utils.dateToLocalDateTime
import java.sql.Connection
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
    private val connection: Connection) {

    @Throws(STException::class)
    fun readAllExercises(sportTypes: List<SportType>): List<Exercise> {
        LOGGER.info("Reading all Exercises")
        val exercises = ArrayList<Exercise>()

        try {
            connection.prepareStatement("SELECT * FROM EXERCISE").use { statement ->
                val rs = statement.executeQuery()
                while (rs.next()) {
                    val sportType = getSportTypeById(sportTypes, rs.getInt("SPORT_TYPE_ID"))
                    val sportSubType = getSportSubTypeById(sportType, rs.getInt("SPORT_SUBTYPE_ID"))
                    val equipmentID = getIntegerOrNull(rs, "EQUIPMENT_ID")
                    val equipment = if (equipmentID == null) null else getEquipmentById(sportType, equipmentID)
                    val exercise = Exercise(rs.getInt("ID"))
                    exercise.dateTime = dateToLocalDateTime(rs.getDate("DATE_TIME"))
                    exercise.sportType = sportType
                    exercise.sportSubType = sportSubType
                    exercise.intensity = Exercise.IntensityType.valueOf(rs.getString("INTENSITY"))
                    exercise.duration = rs.getInt("DURATION")
                    exercise.distance = rs.getFloat("DISTANCE")
                    exercise.avgSpeed = rs.getFloat("AVG_SPEED")
                    exercise.avgHeartRate = rs.getInt("AVG_HEARTRATE")
                    exercise.ascent = rs.getInt("ASCENT")
                    exercise.descent = rs.getInt("DESCENT")
                    exercise.calories = rs.getInt("CALORIES")
                    exercise.hrmFile = rs.getString("HRM_FILE")
                    exercise.equipment = equipment
                    exercise.comment = rs.getString("COMMENT")
                    exercises.add(exercise)
                }
            }
        } catch (e: SQLException) {
            throw STException(STExceptionID.DBSTORAGE_READ_EXERCISES, "Failed to read all Exercises!", e)
        }
        return exercises
    }

    companion object {
        private val LOGGER = Logger.getLogger(ExerciseRepository::class.java.name)
    }
}