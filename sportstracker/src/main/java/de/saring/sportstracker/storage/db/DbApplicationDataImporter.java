package de.saring.sportstracker.storage.db;

import de.saring.sportstracker.core.STException;
import de.saring.sportstracker.core.STExceptionID;
import de.saring.sportstracker.data.Equipment;
import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.data.ExerciseList;
import de.saring.sportstracker.data.Note;
import de.saring.sportstracker.data.NoteList;
import de.saring.sportstracker.data.SportSubType;
import de.saring.sportstracker.data.SportType;
import de.saring.sportstracker.data.SportTypeList;
import de.saring.sportstracker.data.Weight;
import de.saring.sportstracker.data.WeightList;
import de.saring.util.StringUtils;
import de.saring.util.data.IdObject;
import de.saring.util.gui.javafx.ColorUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

/**
 * Importer for the SportsTracker application data to the SQLite application database. It's used for data migration
 * from the previous XML files to the new SQLite database storage.
 *
 * @author Stefan Saring
 */
public class DbApplicationDataImporter {

    private static final DateTimeFormatter SQLITE_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Connection connection;

    private HashMap <String, Long> sportSubTypePrimaryKeyMap;
    private HashMap <String, Long> equipmentPrimaryKeyMap;

    /**
     * C'tor for dependency injection
     *
     * @param connection JDBC database connection
     */
    public DbApplicationDataImporter(final Connection connection) {
        this.connection = connection;
    }

    /**
     * Imports the specified application data to the SQLite database storage. The database needs to be created
     * (incl. schema) before and has to contain no application data yet.
     *
     * @param sportTypes list of sport types to be imported
     * @param exercises list of exercises to be imported
     * @param notes list of notes to be imported
     * @param weights weights of exercises to be imported
     * @throws STException on import errors
     */
    public void importApplicationData(
            SportTypeList sportTypes,
            ExerciseList exercises,
            NoteList notes,
            WeightList weights) throws STException {

        sportSubTypePrimaryKeyMap = new HashMap<>();
        equipmentPrimaryKeyMap = new HashMap<>();

        try {
            exportSportTypes(sportTypes);
            exportExercises(exercises);
            exportNotes(notes);
            exportWeights(weights);
        } catch (SQLException e) {
            throw new STException(STExceptionID.DBSTORAGE_IMPORT_APPLICATION_DATA, "Failed to import application data to SQLite database!", e);
        }
    }

    private void exportSportTypes(SportTypeList sportTypes) throws SQLException {

        final PreparedStatement statement = connection.prepareStatement( //
                "INSERT INTO SPORT_TYPE (ID, NAME, RECORD_DISTANCE, SPEED_MODE, COLOR, ICON, FIT_ID) VALUES (?, ?, ?, ?, ?, ?, ?)");

        for (SportType sportType : sportTypes) {
            statement.clearParameters();

            statement.setLong(1, sportType.getId());
            statement.setString(2, sportType.getName());
            statement.setInt(3, sportType.isRecordDistance() ? 1 : 0);
            statement.setString(4, String.valueOf(sportType.getSpeedMode()));
            statement.setString(5, sportType.getColor() == null ? null : ColorUtils.toRGBCode(sportType.getColor()));
            if (!StringUtils.isNullOrEmpty(sportType.getIcon())) {
                statement.setString(6, sportType.getIcon());
            }
            if (sportType.getFitId() != null) {
                statement.setInt(7, sportType.getFitId());
            }
            statement.executeUpdate();

            exportSportSubTypes(sportType);
            exportEquipments(sportType);
        }
    }

    private void exportSportSubTypes(SportType sportType) throws SQLException {

        final PreparedStatement statement = connection.prepareStatement( //
                "INSERT INTO SPORT_SUBTYPE (SPORT_TYPE_ID, NAME, FIT_ID) VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS);

        for (SportSubType sportSubType : sportType.getSportSubTypeList()) {
            statement.clearParameters();

            statement.setLong(1, sportType.getId());
            statement.setString(2, sportSubType.getName());
            if (sportSubType.getFitId() != null) {
                statement.setInt(3, sportSubType.getFitId());
            }
            statement.executeUpdate();

            var primaryKey = getGeneratedPrimaryKey(statement);
            storeSportSubTypePrimaryKey(sportSubType, sportType, primaryKey);
        }
    }

    private void exportEquipments(SportType sportType) throws SQLException {

        final PreparedStatement statement = connection.prepareStatement( //
                "INSERT INTO EQUIPMENT (SPORT_TYPE_ID, NAME, NOT_IN_USE) VALUES (?, ?, ?)");

        for (Equipment equipment : sportType.getEquipmentList()) {
            statement.clearParameters();

            statement.setLong(1, sportType.getId());
            statement.setString(2, equipment.getName());
            statement.setInt(3, equipment.isNotInUse() ? 1 : 0);
            statement.executeUpdate();

            var primaryKey = getGeneratedPrimaryKey(statement);
            storeEquipmentPrimaryKey(equipment, sportType, primaryKey);
        }
    }

    private void exportExercises(ExerciseList exercises) throws SQLException {

        final PreparedStatement statement = connection.prepareStatement("""
                INSERT INTO EXERCISE (ID, DATE_TIME, SPORT_TYPE_ID, SPORT_SUBTYPE_ID, INTENSITY, DURATION, DISTANCE, 
                AVG_SPEED, AVG_HEARTRATE, ASCENT, DESCENT, CALORIES, HRM_FILE, EQUIPMENT_ID, COMMENT) 
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""");

        for (Exercise exercise : exercises) {
            statement.clearParameters();

            statement.setLong(1, exercise.getId());
            statement.setString(2, exercise.getDateTime().format(SQLITE_DATETIME_FORMATTER));
            statement.setLong(3, exercise.getSportType().getId());
            statement.setLong(4, getPrimaryKeyForSportType(exercise.getSportSubType(), exercise.getSportType()));
            statement.setString(5, String.valueOf(exercise.getIntensity()));
            statement.setInt(6, exercise.getDuration());
            statement.setDouble(7, exercise.getDistance());
            statement.setDouble(8, exercise.getAvgSpeed());
            if (exercise.getAvgHeartRate() != null) {
                statement.setInt(9, exercise.getAvgHeartRate());
            }
            if (exercise.getAscent() != null) {
                statement.setInt(10, exercise.getAscent());
            }
            if (exercise.getDescent() != null) {
                statement.setInt(11, exercise.getDescent());
            }
            if (exercise.getCalories() != null) {
                statement.setInt(12, exercise.getCalories());
            }
            if (!StringUtils.isNullOrEmpty(exercise.getHrmFile())) {
                statement.setString(13, exercise.getHrmFile());
            }
            if (exercise.getEquipment() != null) {
                statement.setLong(14, getPrimaryKeyForEquipment(exercise.getEquipment(), exercise.getSportType()));
            }
            if (!StringUtils.isNullOrEmpty(exercise.getComment())) {
                statement.setString(15, exercise.getComment());
            }
            statement.executeUpdate();
        }
    }

    private void exportNotes(NoteList notes) throws SQLException {

        final PreparedStatement statement = connection.prepareStatement( //
                "INSERT INTO NOTE (ID, DATE_TIME, COMMENT) VALUES (?, ?, ?)");

        for (Note note : notes) {
            statement.clearParameters();

            statement.setLong(1, note.getId());
            statement.setString(2, note.getDateTime().format(SQLITE_DATETIME_FORMATTER));
            statement.setString(3, note.getComment());
            statement.executeUpdate();
        }
    }

    private void exportWeights(WeightList weights) throws SQLException {

        final PreparedStatement statement = connection.prepareStatement( //
                "INSERT INTO WEIGHT (ID, DATE_TIME, VALUE, COMMENT) VALUES (?, ?, ?, ?)");

        for (Weight weight : weights) {
            statement.clearParameters();

            statement.setLong(1, weight.getId());
            statement.setString(2, weight.getDateTime().format(SQLITE_DATETIME_FORMATTER));
            statement.setDouble(3, weight.getValue());
            if (!StringUtils.isNullOrEmpty(weight.getComment())) {
                statement.setString(4, weight.getComment());
            }
            statement.executeUpdate();
        }
    }

    private long getGeneratedPrimaryKey(Statement statement) throws SQLException {
        var resultSet = statement.getGeneratedKeys();
        if (resultSet.next()) {
            return resultSet.getLong(1);
        } else {
            throw new SQLException("Generated primary key was not returned!");
        }
    }

    private void storeSportSubTypePrimaryKey(SportSubType sportSubType, SportType sportType, long primaryKey) {
        var identifier = getSportTypeRelatedIdentifier(sportSubType, sportType);
        sportSubTypePrimaryKeyMap.put(identifier, primaryKey);
    }

    private void storeEquipmentPrimaryKey(Equipment equipment, SportType sportType, long primaryKey) {
        var identifier = getSportTypeRelatedIdentifier(equipment, sportType);
        equipmentPrimaryKeyMap.put(identifier, primaryKey);
    }

    private String getSportTypeRelatedIdentifier(IdObject idObject, SportType sportType) {
        return idObject.getId() + "-" + sportType.getId();
    }

    private long getPrimaryKeyForSportType(SportSubType sportSubType, SportType sportType) {
        var identifier = getSportTypeRelatedIdentifier(sportSubType, sportType);
        var primaryKey = sportSubTypePrimaryKeyMap.get(identifier);
        if (primaryKey == null) {
            throw new IllegalStateException("Failed to find primary key for SportSubType: " + sportSubType + "!");
        }
        return primaryKey;
    }

    private long getPrimaryKeyForEquipment(Equipment equipment, SportType sportType) {
        var identifier = getSportTypeRelatedIdentifier(equipment, sportType);
        var primaryKey = equipmentPrimaryKeyMap.get(identifier);
        if (primaryKey == null) {
            throw new IllegalStateException("Failed to find primary key for Equipment: " + equipment + "!");
        }
        return primaryKey;
    }
}
