package de.saring.sportstracker.storage.db;

import de.saring.sportstracker.data.Equipment;
import de.saring.sportstracker.data.SportSubType;
import de.saring.sportstracker.data.SportType;
import de.saring.sportstracker.storage.XMLUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Utility methods for the database repository implementations.
 *
 * @author Stefan Saring
 */
public class RepositoryUtil {

    private static final DateTimeFormatter SQLITE_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // TODO implement unit tests

    private RepositoryUtil() {
    }

    public static Integer getIntegerOrNull(ResultSet rs, String columnName) throws SQLException {
        int value = rs.getInt(columnName);
        return rs.wasNull() ? null : value;
    }


    public static SportType getSportTypeById(List<SportType> sportTypes, int sportTypeId) {
        return sportTypes.stream()
                .filter(st -> sportTypeId == st.getId())
                .findAny()
                .get();
    }

    public static SportSubType getSportSubTypeById(SportType sportType, int sportSubTypeId) {
        return sportType.getSportSubTypeList().stream()
                .filter(sst -> sportSubTypeId == sst.getId())
                .findAny()
                .get();
    }

    public static Equipment getEquipmentById(SportType sportType, int equipmentId) {
        return sportType.getEquipmentList().stream()
                .filter(eq -> equipmentId == eq.getId())
                .findAny()
                .orElse(null);
    }

    public static String dateTimeToString(LocalDateTime dateTime) {
        return dateTime.format(SQLITE_DATE_TIME_FORMAT);
    }
}
