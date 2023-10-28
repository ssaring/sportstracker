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

    override fun executeCreate(entry: SportType): SportType {
        var sportType: SportType

        connection.prepareStatement(
            "INSERT INTO SPORT_TYPE " +
                    "(NAME, RECORD_DISTANCE, SPEED_MODE, COLOR, ICON, FIT_ID) VALUES (?, ?, ?, ?, ?, ?) RETURNING ID",
            Statement.RETURN_GENERATED_KEYS
        ).use { statement ->
            statement.setString(1, entry.getName())
            statement.setBoolean(2, entry.isRecordDistance)
            statement.setString(3, entry.speedMode.name)
            statement.setString(4, if (entry.color == null) null else ColorUtils.toRGBCode(entry.color))
            statement.setString(5, entry.icon)
            statement.setObject(6, entry.fitId, Types.INTEGER);
            statement.execute()

            val sportTypeId = statement.resultSet.getLong(1)
            sportType = readById(sportTypeId)
        }

        // persist also all new sport subtypes and equipments
        entry.sportSubTypeList.forEach { createSportSubType(it, sportType) }
        entry.equipmentList.forEach { createEquipment(it, sportType) }
        return sportType
    }

    override fun executeUpdate(entry: SportType) {
        connection.prepareStatement("UPDATE SPORT_TYPE SET " +
                "NAME = ?, RECORD_DISTANCE = ?, SPEED_MODE = ?, COLOR = ?, ICON = ?, FIT_ID = ? WHERE ID = ?"
            ).use { statement ->
            statement.setString(1, entry.getName())
            statement.setBoolean(2, entry.isRecordDistance)
            statement.setString(3, entry.speedMode.name)
            statement.setString(4, if (entry.color == null) null else ColorUtils.toRGBCode(entry.color))
            statement.setString(5, entry.icon)
            statement.setObject(6, entry.fitId, Types.INTEGER);
            statement.setLong(7, entry.id!!);
            statement.executeUpdate()
        }

        persistSportSubTypesOfExistingSportType(entry)
        persistEquipmentsOfExistingSportType(entry)
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

    private fun createSportSubType(sportSubType: SportSubType, sportType: SportType) {
        connection.prepareStatement(
            "INSERT INTO SPORT_SUBTYPE (SPORT_TYPE_ID, NAME, FIT_ID) VALUES (?, ?, ?)",
            Statement.RETURN_GENERATED_KEYS
        ).use { statement ->
            statement.setLong(1, sportType.id!!)
            statement.setString(2, sportSubType.getName())
            statement.setObject(3, sportSubType.fitId, Types.INTEGER);
            statement.executeUpdate()
        }
    }

    private fun updateSportSubType(sportSubType: SportSubType) {
        connection.prepareStatement(
            "UPDATE SPORT_SUBTYPE SET " +
                    "NAME = ?, FIT_ID = ? WHERE ID = ?"
        ).use { statement ->
            statement.setString(1, sportSubType.getName())
            statement.setObject(2, sportSubType.fitId, Types.INTEGER);
            statement.setLong(3, sportSubType.id!!);
            statement.executeUpdate()
        }
    }

    private fun createEquipment(equipment: Equipment, sportType: SportType) {
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

    private fun updateEquipment(equipment: Equipment) {
        connection.prepareStatement(
            "UPDATE EQUIPMENT SET " +
                    "NAME = ?, NOT_IN_USE = ? WHERE ID = ?"
        ).use { statement ->
            statement.setString(1, equipment.getName())
            statement.setBoolean(2, equipment.isNotInUse);
            statement.setLong(3, equipment.id!!);
            statement.executeUpdate()
        }
    }

    private fun persistSportSubTypesOfExistingSportType(sportType: SportType) {
        // create list of IDs of all contained sport subtypes which were already created before (have an ID)
        val stillExistingSportSubTypedIds = sportType.sportSubTypeList
            .filter { it.id != null }
            .map { it.id }

        // delete all exercises which are using sport subtypes which are not in the edited sport type anymore (confirmed by the user)
        val sqlParamsStillExistingSportSubtyTypeIds = stillExistingSportSubTypedIds
            .map { "?" }
            .joinToString(", ")

        connection.prepareStatement("DELETE FROM EXERCISE WHERE SPORT_SUBTYPE_ID IN (" +
                "SELECT ID FROM SPORT_SUBTYPE WHERE SPORT_TYPE_ID = ? AND ID NOT IN ($sqlParamsStillExistingSportSubtyTypeIds))"
        ).use { statement ->
            statement.setLong(1, sportType.id!!);
            var paramIndex = 2
            stillExistingSportSubTypedIds.forEach { statement.setLong(paramIndex++, it!!) }
            statement.executeUpdate()
        }

        // delete all sport subtypes of this sport type which are not in the edited sport type anymore
        connection.prepareStatement("DELETE FROM SPORT_SUBTYPE WHERE SPORT_TYPE_ID = ? AND ID NOT IN ($sqlParamsStillExistingSportSubtyTypeIds)"
        ).use { statement ->
            statement.setLong(1, sportType.id!!);
            var paramIndex = 2
            stillExistingSportSubTypedIds.forEach { statement.setLong(paramIndex++, it!!) }
            statement.executeUpdate()
        }

        // persist all contained sport subtypes (create new ones or update existing ones)
        sportType.sportSubTypeList.forEach { sportSubType ->
            if (sportSubType.id == null) {
                createSportSubType(sportSubType, sportType)
            } else {
                updateSportSubType(sportSubType)
            }
        }
    }

    private fun persistEquipmentsOfExistingSportType(sportType: SportType) {
        // create list of IDs of all contained equipments which were already created before (have an ID)
        val stillExistingEquipmentIds = sportType.equipmentList
            .filter { it.id != null }
            .map { it.id }

        // delete usage of equipments in all exercises which are not in the edited sport type anymore (confirmed by the user)
        val sqlParamsStillExistingEquipmentIds = stillExistingEquipmentIds
            .map { "?" }
            .joinToString(", ")

        connection.prepareStatement("UPDATE EXERCISE SET EQUIPMENT_ID = NULL WHERE EQUIPMENT_ID IN (" +
                "SELECT ID FROM EQUIPMENT WHERE SPORT_TYPE_ID = ? AND ID NOT IN ($sqlParamsStillExistingEquipmentIds))"
        ).use { statement ->
            statement.setLong(1, sportType.id!!);
            var paramIndex = 2
            stillExistingEquipmentIds.forEach { statement.setLong(paramIndex++, it!!) }
            statement.executeUpdate()
        }

        // delete all equipments of this sport type which are not in the edited sport type anymore
        connection.prepareStatement("DELETE FROM EQUIPMENT WHERE SPORT_TYPE_ID = ? AND ID NOT IN ($sqlParamsStillExistingEquipmentIds)"
        ).use { statement ->
            statement.setLong(1, sportType.id!!);
            var paramIndex = 2
            stillExistingEquipmentIds.forEach { statement.setLong(paramIndex++, it!!) }
            statement.executeUpdate()
        }

        // persist all contained equipments (create new ones or update existing ones)
        sportType.equipmentList.forEach { equipment ->
            if (equipment.id == null) {
                createEquipment(equipment, sportType)
            } else {
                updateEquipment(equipment)
            }
        }
    }
}