package de.saring.sportstracker.storage.db

import de.saring.sportstracker.core.STException
import de.saring.sportstracker.core.STExceptionID
import de.saring.sportstracker.data.Equipment
import de.saring.sportstracker.data.SportSubType
import de.saring.sportstracker.data.SportType
import de.saring.sportstracker.storage.db.RepositoryUtil.getIntegerOrNull
import de.saring.sportstracker.storage.db.RepositoryUtil.getSportTypeById
import de.saring.util.gui.javafx.ColorUtils
import de.saring.util.unitcalc.SpeedMode
import javafx.scene.paint.Color
import java.lang.UnsupportedOperationException
import java.sql.*
import java.util.logging.Logger


/**
 * Database repository for the SportType and related data.
 *
 * @property connection database connection
 *
 * @author Stefan Saring
 */
class SportTypeRepository(
    connection: Connection
) : AbstractRepository<SportType>(connection) {

    @Throws(STException::class)
    override fun readAll(): List<SportType> {
        val sportTypes = super.readAll()
        readAllSportSubTypes(sportTypes)
        readAllEquipments(sportTypes)
        return sportTypes
    }

    override val entityName = "SportType"

    override val tableName = "SPORT_TYPE"

    override val logger: Logger = Logger.getLogger(SportTypeRepository::class.java.name)

    override fun readFromResultSet(rs: ResultSet): SportType {
        val sportType = SportType(rs.getLong("ID"))
        sportType.setName(rs.getString("NAME"))
        sportType.isRecordDistance = rs.getBoolean("RECORD_DISTANCE")
        sportType.speedMode = SpeedMode.valueOf(rs.getString("SPEED_MODE"))
        sportType.color = Color.web(rs.getString("COLOR"))
        sportType.icon = rs.getString("ICON")
        sportType.fitId = getIntegerOrNull(rs, "FIT_ID")
        return sportType
    }

    private fun readAllSportSubTypes(sportTypes: List<SportType>) {
        try {
            connection.prepareStatement("SELECT * FROM SPORT_SUBTYPE").use { statement ->
                val rs = statement.executeQuery()
                while (rs.next()) {
                    val sportType = getSportTypeById(sportTypes, rs.getLong("SPORT_TYPE_ID"))
                    val sportSubType = SportSubType(rs.getLong("ID"))
                    sportSubType.setName(rs.getString("NAME"))
                    sportSubType.fitId = getIntegerOrNull(rs, "FIT_ID")
                    sportType.sportSubTypeList.set(sportSubType)
                }
            }
        } catch (e: SQLException) {
            throw STException(STExceptionID.DBSTORAGE_READ_ALL, "Failed to read all SportSubTypes!", e)
        }
    }

    private fun readAllEquipments(sportTypes: List<SportType>) {
        try {
            connection.prepareStatement("SELECT * FROM EQUIPMENT").use { statement ->
                val rs = statement.executeQuery()
                while (rs.next()) {
                    val sportType = getSportTypeById(sportTypes, rs.getLong("SPORT_TYPE_ID"))
                    val equipment = Equipment(rs.getLong("ID"))
                    equipment.setName(rs.getString("NAME"))
                    equipment.isNotInUse = rs.getBoolean("NOT_IN_USE")
                    sportType.equipmentList.set(equipment)
                }
            }
        } catch (e: SQLException) {
            throw STException(STExceptionID.DBSTORAGE_READ_ALL, "Failed to read all Equipments!", e)
        }
    }

    override fun executeUpdate(entry: SportType) {
        // TODO
        throw UnsupportedOperationException("TODO")
    }

    override fun executeCreate(entry: SportType): SportType {
        var sportType: SportType

        connection.prepareStatement(
            "INSERT INTO SPORT_TYPE " +
                    "(NAME, RECORD_DISTANCE, SPEED_MODE, COLOR, ICON, FIT_ID) VALUES (?, ?, ?, ?, ?, ?)",
            Statement.RETURN_GENERATED_KEYS
        ).use { statement ->
            statement.setString(1, entry.getName())
            statement.setBoolean(2, entry.isRecordDistance)
            statement.setString(3, entry.speedMode.name)
            statement.setString(4, if (entry.color == null) null else ColorUtils.toRGBCode(entry.color))
            statement.setString(5, entry.icon)
            statement.setObject(6, entry.fitId, Types.INTEGER);
            statement.executeUpdate()

            val rs = statement.generatedKeys
            rs.next()
            val sportTypeId = rs.getLong(1)
            sportType = readById(sportTypeId)
        }

        // persist also all new sport subtypes
        entry.sportSubTypeList.forEach { subType ->
            connection.prepareStatement(
                "INSERT INTO SPORT_SUBTYPE (SPORT_TYPE_ID, NAME, FIT_ID) VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
            ).use { statement ->
                statement.setLong(1, sportType.id!!)
                statement.setString(2, subType.getName())
                statement.setObject(3, subType.fitId, Types.INTEGER);
                statement.executeUpdate()
            }
        }

        // persist also all new equipments
        entry.equipmentList.forEach { equipment ->
            connection.prepareStatement(
                "INSERT INTO EQUIPMENT (SPORT_TYPE_ID, NAME, NOT_IN_USE) VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
            ).use { statement ->
                statement.setLong(1, sportType.id!!)
                statement.setString(2, equipment.getName())
                statement.setBoolean(3, equipment.isNotInUse);
                statement.executeUpdate()
            }
        }
        
        return sportType
    }

    override fun executeDelete(entryId: Long) {
        // SportType might be used in some Exercises, these need to be deleted before (confirmed by the user)
        connection.prepareStatement("DELETE FROM EXERCISE WHERE SPORT_TYPE_ID = ?").use { statement ->
            statement.setLong(1, entryId)
            statement.executeUpdate()
        }

        // delete all equipments of this sport type
        connection.prepareStatement("DELETE FROM EQUIPMENT WHERE SPORT_TYPE_ID = ?").use { statement ->
            statement.setLong(1, entryId)
            statement.executeUpdate()
        }
        // delete all sport subtypes of this sport type
        connection.prepareStatement("DELETE FROM SPORT_SUBTYPE WHERE SPORT_TYPE_ID = ?").use { statement ->
            statement.setLong(1, entryId)
            statement.executeUpdate()
        }

        super.executeDelete(entryId);
    }
}