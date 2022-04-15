package de.saring.sportstracker.storage;

import de.saring.sportstracker.core.STException;
import de.saring.sportstracker.core.STExceptionID;
import de.saring.sportstracker.data.Equipment;
import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.data.Note;
import de.saring.sportstracker.data.SportSubType;
import de.saring.sportstracker.data.SportType;
import de.saring.sportstracker.data.Weight;
import de.saring.sportstracker.gui.STDocument;
import de.saring.util.StringUtils;
import de.saring.util.data.IdObject;
import de.saring.util.gui.javafx.ColorUtils;
import jakarta.inject.Singleton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

/**
 * Exporter for the SportsTracker application data to a SQLite database. The exporter uses the plain
 * JDBC API (no ORM) and the xerial/sqlite-jdbc library (contains the native SQLite libraries).
 *
 * @author Stefan Saring
 */
@Singleton
public class SQLiteExporter {

    private static final String SCHEMA_FILE = "/sql/st-export.sql";
    private static final String DATABASE_FILE = System.getProperty("user.home") + "/st-export.sqlite";
    private static final DateTimeFormatter SQLITE_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private STDocument document;

    private HashMap <String, Long> sportSubTypePrimaryKeyMap;
    private HashMap <String, Long> equipmentPrimaryKeyMap;

    /**
     * C'tor for dependency injection
     *
     * @param document SportsTracker document (model) instance
     */
    public SQLiteExporter(final STDocument document) {
        this.document = document;
    }

    /**
     * Returns the absolute Path of the created SQLite database.
     *
     * @return absolute database path
     */
    public Path getDatabasePath() {
        return Paths.get(DATABASE_FILE).toAbsolutePath();
    }

    /**
     * Exports the application data to a new SQLite database, an already existing database will be overwritten.
     *
     * @throws STException on export errors
     */
    public void exportToSqlite() throws STException {

        deleteExistingDatabase();
        sportSubTypePrimaryKeyMap = new HashMap<>();
        equipmentPrimaryKeyMap = new HashMap<>();

        // create database connection
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_FILE)) {

            // create database schema
            final String dbSchema = readDatabaseSchema();
            final Statement statement = connection.createStatement();
            statement.setQueryTimeout(10);
            statement.executeUpdate(dbSchema);

            // export data
            exportSportTypes(connection);
            exportExercises(connection);
            exportNotes(connection);
            exportWeights(connection);
        } catch (SQLException e) {
            throw new STException(STExceptionID.SQLITE_EXPORT, "Failed to export application data to SQLite!", e);
        }
    }

    private void deleteExistingDatabase() throws STException {
        try {
            Files.deleteIfExists(Paths.get(DATABASE_FILE));
        } catch (IOException e) {
            throw new STException(STExceptionID.SQLITE_EXPORT, //
                    "Failed to delete the already existing database '" + DATABASE_FILE + "'!", e);
        }
    }

    private String readDatabaseSchema() throws STException {

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                this.getClass().getResourceAsStream(SCHEMA_FILE)))) {

            StringBuffer fileContent = new StringBuffer();
            String line;
            while((line = reader.readLine()) != null) {
                fileContent.append(line).append('\n');
            }

            return fileContent.toString();
        } catch (IOException e) {
            throw new STException(STExceptionID.SQLITE_EXPORT, //
                    "Failed to read the database schema file '" + SCHEMA_FILE + "'!", e);
        }
    }

    private void exportSportTypes(final Connection connection) throws SQLException {

        final PreparedStatement statement = connection.prepareStatement( //
                "INSERT INTO SPORT_TYPE (ID, NAME, RECORD_DISTANCE, SPEED_MODE, COLOR, ICON) VALUES (?, ?, ?, ?, ?, ?)");

        for (SportType sportType : document.getSportTypeList()) {
            statement.clearParameters();

            statement.setInt(1, sportType.getId());
            statement.setString(2, sportType.getName());
            statement.setInt(3, sportType.isRecordDistance() ? 1 : 0);
            statement.setString(4, String.valueOf(sportType.getSpeedMode()));
            statement.setString(5, sportType.getColor() == null ? null : ColorUtils.toRGBCode(sportType.getColor()));
            if (!StringUtils.isNullOrEmpty(sportType.getIcon())) {
                statement.setString(6, sportType.getIcon());
            }
            statement.executeUpdate();

            exportSportSubTypes(connection, sportType);
            exportEquipments(connection, sportType);
        }
    }

    private void exportSportSubTypes(final Connection connection, final SportType sportType) throws SQLException {

        final PreparedStatement statement = connection.prepareStatement( //
                "INSERT INTO SPORT_SUBTYPE (SPORT_TYPE_ID, NAME) VALUES (?, ?)",
                Statement.RETURN_GENERATED_KEYS);

        for (SportSubType sportSubType : sportType.getSportSubTypeList()) {
            statement.clearParameters();

            statement.setInt(1, sportType.getId());
            statement.setString(2, sportSubType.getName());
            statement.executeUpdate();

            var primaryKey = getGeneratedPrimaryKey(statement);
            storeSportSubTypePrimaryKey(sportSubType, sportType, primaryKey);
        }
    }

    private void exportEquipments(final Connection connection, final SportType sportType) throws SQLException {

        final PreparedStatement statement = connection.prepareStatement( //
                "INSERT INTO EQUIPMENT (SPORT_TYPE_ID, NAME, NOT_IN_USE) VALUES (?, ?, ?)");

        for (Equipment equipment : sportType.getEquipmentList()) {
            statement.clearParameters();

            statement.setInt(1, sportType.getId());
            statement.setString(2, equipment.getName());
            statement.setInt(3, equipment.isNotInUse() ? 1 : 0);
            statement.executeUpdate();

            var primaryKey = getGeneratedPrimaryKey(statement);
            storeEquipmentPrimaryKey(equipment, sportType, primaryKey);
        }
    }

    private void exportExercises(final Connection connection) throws SQLException {

        final PreparedStatement statement = connection.prepareStatement("""
                INSERT INTO EXERCISE (ID, DATE_TIME, SPORT_TYPE_ID, SPORT_SUBTYPE_ID, INTENSITY, DURATION, DISTANCE, 
                AVG_SPEED, AVG_HEARTRATE, ASCENT, DESCENT, CALORIES, HRM_FILE, EQUIPMENT_ID, COMMENT) 
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""");

        for (Exercise exercise : document.getExerciseList()) {
            statement.clearParameters();

            statement.setInt(1, exercise.getId());
            statement.setString(2, exercise.getDateTime().format(SQLITE_DATETIME_FORMATTER));
            statement.setInt(3, exercise.getSportType().getId());
            statement.setLong(4, getPrimaryKeyForSportType(exercise.getSportSubType(), exercise.getSportType()));
            statement.setString(5, String.valueOf(exercise.getIntensity()));
            statement.setInt(6, exercise.getDuration());
            statement.setFloat(7, exercise.getDistance());
            statement.setFloat(8, exercise.getAvgSpeed());
            statement.setInt(9, exercise.getAvgHeartRate());
            statement.setInt(10, exercise.getAscent());
            statement.setInt(11, exercise.getDescent());
            statement.setInt(12, exercise.getCalories());
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

    private void exportNotes(final Connection connection) throws SQLException {

        final PreparedStatement statement = connection.prepareStatement( //
                "INSERT INTO NOTE (ID, DATE_TIME, COMMENT) VALUES (?, ?, ?)");

        for (Note note : document.getNoteList()) {
            statement.clearParameters();

            statement.setInt(1, note.getId());
            statement.setString(2, note.getDateTime().format(SQLITE_DATETIME_FORMATTER));
            statement.setString(3, note.getComment());
            statement.executeUpdate();
        }
    }

    private void exportWeights(final Connection connection) throws SQLException {

        final PreparedStatement statement = connection.prepareStatement( //
                "INSERT INTO WEIGHT (ID, DATE_TIME, VALUE, COMMENT) VALUES (?, ?, ?, ?)");

        for (Weight weight : document.getWeightList()) {
            statement.clearParameters();

            statement.setInt(1, weight.getId());
            statement.setString(2, weight.getDateTime().format(SQLITE_DATETIME_FORMATTER));
            statement.setFloat(3, weight.getValue());
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
