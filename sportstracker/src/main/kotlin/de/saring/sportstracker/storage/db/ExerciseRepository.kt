package de.saring.sportstracker.storage.db

import de.saring.sportstracker.core.STException
import de.saring.sportstracker.core.STExceptionID
import de.saring.sportstracker.data.Exercise
import de.saring.sportstracker.data.SportType
import de.saring.sportstracker.storage.db.RepositoryUtil.getEquipmentById
import de.saring.sportstracker.storage.db.RepositoryUtil.getLongOrNull
import de.saring.sportstracker.storage.db.RepositoryUtil.getSportSubTypeById
import de.saring.sportstracker.storage.db.RepositoryUtil.getSportTypeById
import de.saring.util.Date310Utils.dateToLocalDateTime
import java.lang.UnsupportedOperationException
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import java.sql.Types
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

                    val sportType = getSportTypeById(sportTypes, rs.getLong("SPORT_TYPE_ID"))
                    exercise.sportType = sportType
                    exercise.sportSubType = getSportSubTypeById(sportType, rs.getLong("SPORT_SUBTYPE_ID"))

                    val equipmentID = getLongOrNull(rs, "EQUIPMENT_ID")
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

    override val entityName = "Exercise"

    override val tableName = "EXERCISE"

    override val logger: Logger = Logger.getLogger(ExerciseRepository::class.java.name)

    override fun readFromResultSet(rs: ResultSet): Exercise {
        val exercise = Exercise(rs.getLong("ID"))
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

    override fun executeCreate(entry: Exercise): Exercise {
        connection.prepareStatement("INSERT INTO EXERCISE " +
                "(DATE_TIME, SPORT_TYPE_ID, SPORT_SUBTYPE_ID, INTENSITY, DURATION, DISTANCE, AVG_SPEED, " +
                "AVG_HEARTRATE, ASCENT, DESCENT, CALORIES, HRM_FILE, EQUIPMENT_ID, COMMENT) VALUES " +
                "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            Statement.RETURN_GENERATED_KEYS
        ).use { statement ->
            statement.setString(1, RepositoryUtil.dateTimeToString(entry.dateTime))
            statement.setLong(2, entry.sportType.id!!)
            statement.setLong(3, entry.sportSubType.id!!)
            statement.setString(4, entry.intensity.name)
            statement.setInt(5, entry.duration)
            statement.setFloat(6, entry.distance)
            statement.setFloat(7, entry.avgSpeed)
            statement.setInt(8, entry.avgHeartRate)
            statement.setInt(9, entry.ascent)
            statement.setInt(10, entry.descent)
            statement.setInt(11, entry.calories)
            statement.setString(12, entry.hrmFile)
            statement.setObject(13, entry.equipment?.id, Types.INTEGER);
            statement.setString(14, entry.comment)
            statement.executeUpdate()

            val rs = statement.generatedKeys
            rs.next()
            val exerciseId = rs.getLong(1)
            return readById(exerciseId)
        }
    }

    override fun executeUpdate(entry: Exercise) {
        connection.prepareStatement("UPDATE EXERCISE SET " +
                "DATE_TIME = ?, SPORT_TYPE_ID = ?, SPORT_SUBTYPE_ID = ?, INTENSITY = ?, DURATION = ?, " +
                "DISTANCE = ?, AVG_SPEED = ?, AVG_HEARTRATE = ?, ASCENT = ?, DESCENT = ?, " +
                "CALORIES = ?, HRM_FILE = ?, EQUIPMENT_ID = ?, COMMENT = ? WHERE ID = ?"
        ).use { statement ->
            statement.setString(1, RepositoryUtil.dateTimeToString(entry.dateTime))
            statement.setLong(2, entry.sportType.id!!)
            statement.setLong(3, entry.sportSubType.id!!)
            statement.setString(4, entry.intensity.name)
            statement.setInt(5, entry.duration)
            statement.setFloat(6, entry.distance)
            statement.setFloat(7, entry.avgSpeed)
            statement.setInt(8, entry.avgHeartRate)
            statement.setInt(9, entry.ascent)
            statement.setInt(10, entry.descent)
            statement.setInt(11, entry.calories)
            statement.setString(12, entry.hrmFile)
            statement.setObject(13, entry.equipment?.id, Types.INTEGER);
            statement.setString(14, entry.comment)
            statement.setLong(15, entry.id!!)
            statement.executeUpdate()
        }
    }
}