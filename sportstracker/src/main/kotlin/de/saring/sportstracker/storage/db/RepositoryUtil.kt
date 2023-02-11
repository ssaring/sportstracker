package de.saring.sportstracker.storage.db

import de.saring.sportstracker.data.Equipment
import de.saring.sportstracker.data.SportSubType
import de.saring.sportstracker.data.SportType
import java.sql.ResultSet
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Utility methods for the database repository implementations.
 *
 * @author Stefan Saring
 */
object RepositoryUtil {

    private val SQLITE_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    // TODO implement unit tests

    fun getIntegerOrNull(rs: ResultSet, columnName: String): Int? {
        val value = rs.getInt(columnName)
        return if (rs.wasNull()) null else value
    }

    fun getLongOrNull(rs: ResultSet, columnName: String): Long? {
        val value = rs.getLong(columnName)
        return if (rs.wasNull()) null else value
    }

    fun getSportTypeById(sportTypes: List<SportType>, sportTypeId: Long): SportType {
        return sportTypes.first { sportTypeId == it.id }
    }

    fun getSportSubTypeById(sportType: SportType, sportSubTypeId: Long): SportSubType {
        return sportType.sportSubTypeList.first { sportSubTypeId == it.id }
    }

    fun getEquipmentById(sportType: SportType, equipmentId: Long): Equipment {
        return sportType.equipmentList.first { equipmentId == it.id }
    }

    fun dateTimeToString(dateTime: LocalDateTime): String {
        return dateTime.format(SQLITE_DATE_TIME_FORMAT)
    }
}