package de.saring.sportstracker.storage.db

import de.saring.sportstracker.core.STException
import de.saring.sportstracker.core.STExceptionID
import de.saring.sportstracker.data.Equipment
import de.saring.sportstracker.data.SportSubType
import de.saring.sportstracker.data.SportType
import de.saring.sportstracker.storage.db.RepositoryUtil.getIntegerOrNull
import de.saring.sportstracker.storage.db.RepositoryUtil.getSportTypeById
import de.saring.util.unitcalc.SpeedMode
import javafx.scene.paint.Color
import java.sql.Connection
import java.sql.SQLException
import java.util.logging.Logger


/**
 * Database repository for the SportType and related data.
 *
 * @property connection database connection
 *
 * @author Stefan Saring
 */
class SportTypeRepository(
    private val connection: Connection
) {

    @Throws(STException::class)
    fun readAllSportTypes(): List<SportType> {
        LOGGER.info("Reading all SportTypes")
        val sportTypes = ArrayList<SportType>()

        try {
            connection.prepareStatement("SELECT * FROM SPORT_TYPE").use { statement ->
                val rs = statement.executeQuery()
                while (rs.next()) {
                    val sportType = SportType(rs.getInt("ID"))
                    sportType.setName(rs.getString("NAME"))
                    sportType.isRecordDistance = rs.getBoolean("RECORD_DISTANCE")
                    sportType.speedMode = SpeedMode.valueOf(rs.getString("SPEED_MODE"))
                    sportType.color = Color.web(rs.getString("COLOR"))
                    sportType.icon = rs.getString("ICON")
                    sportType.fitId = getIntegerOrNull(rs, "FIT_ID")
                    sportTypes.add(sportType)
                }
            }
        } catch (e: SQLException) {
            throw STException(STExceptionID.DBSTORAGE_READ_SPORT_TYPES, "Failed to read all SportTypes!", e)
        }

        readAllSportSubTypes(sportTypes)
        readAllEquipments(sportTypes)
        return sportTypes
    }

    private fun readAllSportSubTypes(sportTypes: List<SportType>) {
        try {
            connection.prepareStatement("SELECT * FROM SPORT_SUBTYPE").use { statement ->
                val rs = statement.executeQuery()
                while (rs.next()) {
                    val sportType = getSportTypeById(sportTypes, rs.getInt("SPORT_TYPE_ID"))
                    val sportSubType = SportSubType(rs.getInt("ID"))
                    sportSubType.setName(rs.getString("NAME"))
                    sportSubType.fitId = getIntegerOrNull(rs, "FIT_ID")
                    sportType.sportSubTypeList.set(sportSubType)
                }
            }
        } catch (e: SQLException) {
            throw STException(STExceptionID.DBSTORAGE_READ_SPORT_SUBTYPES, "Failed to read all SportSubTypes!", e)
        }
    }

    private fun readAllEquipments(sportTypes: List<SportType>) {
        try {
            connection.prepareStatement("SELECT * FROM EQUIPMENT").use { statement ->
                val rs = statement.executeQuery()
                while (rs.next()) {
                    val sportType = getSportTypeById(sportTypes, rs.getInt("SPORT_TYPE_ID"))
                    val equipment = Equipment(rs.getInt("ID"))
                    equipment.setName(rs.getString("NAME"))
                    equipment.isNotInUse = rs.getBoolean("NOT_IN_USE")
                    sportType.equipmentList.set(equipment)
                }
            }
        } catch (e: SQLException) {
            throw STException(STExceptionID.DBSTORAGE_READ_EQUIPMENTS, "Failed to read all Equipments!", e)
        }
    }

    companion object {
        private val LOGGER = Logger.getLogger(SportTypeRepository::class.java.name)
    }
}