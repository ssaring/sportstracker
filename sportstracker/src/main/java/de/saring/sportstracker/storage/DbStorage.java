package de.saring.sportstracker.storage;

import de.saring.sportstracker.core.STException;
import de.saring.sportstracker.core.STExceptionID;
import de.saring.sportstracker.data.Equipment;
import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.data.Note;
import de.saring.sportstracker.data.SportSubType;
import de.saring.sportstracker.data.SportType;
import de.saring.sportstracker.data.Weight;
import de.saring.util.Date310Utils;
import de.saring.util.unitcalc.SpeedMode;
import jakarta.inject.Singleton;
import javafx.scene.paint.Color;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for reading / storing of the application data from / to a SQLite database.
 *
 * @author Stefan Saring
 */
@Singleton
public class DbStorage {

    // TODO: readInt etc. return 0 when the database value is NULL => to be checked in optional columns!

    private Connection connection;

    public void openDatabase(String dbFilename) throws STException {
        String jdbcUrl = "jdbc:sqlite:" + dbFilename;

        try {
            connection = DriverManager.getConnection(jdbcUrl);
        } catch (SQLException e) {
            throw new STException(STExceptionID.DBSTORAGE_OPEN_DATABASE,
                    "Failed to open SQLite database '" + jdbcUrl + "'!", e);
        }
    }

    public void closeDatabase() throws STException {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new STException(STExceptionID.DBSTORAGE_CLOSE_DATABASE,
                        "Failed to close opened SQLite database!", e);
            }
        }
    }

    // TODO move entity-related methods to a dedicated repository for each entity?
    public List<Note> readAllNotes() throws STException {
        var notes = new ArrayList<Note>();

        try(var statement = connection.createStatement()) {
            var rs = statement.executeQuery("SELECT * FROM NOTE");
            while(rs.next())
            {
                var note = new Note(rs.getInt("ID"));
                note.setDateTime(Date310Utils.dateToLocalDateTime(rs.getDate("DATE_TIME")));
                note.setComment(rs.getString("COMMENT"));
                notes.add(note);
            }
        } catch (SQLException e) {
            throw new STException(STExceptionID.DBSTORAGE_READ_NOTES, "Failed to read all Notes!", e);
        }

        return notes;
    }

    // TODO move entity-related methods to a dedicated repository for each entity?
    public List<Weight> readAllWeights() throws STException {
        var weights = new ArrayList<Weight>();

        try(var statement = connection.createStatement()) {
            var rs = statement.executeQuery("SELECT * FROM WEIGHT");
            while(rs.next())
            {
                var weight = new Weight(rs.getInt("ID"));
                weight.setDateTime(Date310Utils.dateToLocalDateTime(rs.getDate("DATE_TIME")));
                weight.setValue(rs.getFloat("VALUE"));
                weight.setComment(rs.getString("COMMENT"));
                weights.add(weight);
            }
        } catch (SQLException e) {
            throw new STException(STExceptionID.DBSTORAGE_READ_WEIGHTS, "Failed to read all Weights!", e);
        }

        return weights;
    }

    // TODO move entity-related methods to a dedicated repository for each entity?
    public List<Exercise> readAllExercises(List<SportType> sportTypes) throws STException {
        var exercises = new ArrayList<Exercise>();

        try(var statement = connection.createStatement()) {
            var rs = statement.executeQuery("SELECT * FROM EXERCISE");
            while(rs.next())
            {
                var sportType = getSportTypeById(sportTypes, rs.getInt("SPORT_TYPE_ID"));
                var sportSubType = getSportSubTypeById(sportType, rs.getInt("SPORT_SUBTYPE_ID"));
                var equipment = getEquipmentById(sportType, rs.getInt("EQUIPMENT_ID"));

                var exercise = new Exercise(rs.getInt("ID"));
                exercise.setDateTime(Date310Utils.dateToLocalDateTime(rs.getDate("DATE_TIME")));
                exercise.setSportType(sportType);
                exercise.setSportSubType(sportSubType);
                exercise.setIntensity(Exercise.IntensityType.valueOf(rs.getString("INTENSITY")));
                exercise.setDuration(rs.getInt("DURATION"));
                exercise.setDistance(rs.getFloat("DISTANCE"));
                exercise.setAvgSpeed(rs.getFloat("AVG_SPEED"));
                exercise.setAvgHeartRate(rs.getInt("AVG_HEARTRATE"));
                exercise.setAscent(rs.getInt("ASCENT"));
                exercise.setDescent(rs.getInt("DESCENT"));
                exercise.setCalories(rs.getInt("CALORIES"));
                exercise.setHrmFile(rs.getString("HRM_FILE"));
                exercise.setEquipment(equipment);
                exercise.setComment(rs.getString("COMMENT"));
                exercises.add(exercise);
            }
        } catch (SQLException e) {
            throw new STException(STExceptionID.DBSTORAGE_READ_EXERCISES, "Failed to read all Exercises!", e);
        }

        return exercises;
    }

    // TODO move entity-related methods to a dedicated repository for each entity?
    public List<SportType> readAllSportTypes() throws STException {
        var sportTypes = new ArrayList<SportType>();

        try(var statement = connection.createStatement()) {
            var rs = statement.executeQuery("SELECT * FROM SPORT_TYPE");
            while(rs.next())
            {
                var sportType = new SportType(rs.getInt("ID"));
                sportType.setName(rs.getString("NAME"));
                sportType.setRecordDistance(rs.getBoolean("RECORD_DISTANCE"));
                sportType.setSpeedMode(SpeedMode.valueOf(rs.getString("SPEED_MODE")));
                sportType.setColor(Color.web(rs.getString("COLOR")));
                sportType.setIcon(rs.getString("ICON"));
                sportType.setFitId(rs.getInt("FIT_ID"));
                sportTypes.add(sportType);
            }
        } catch (SQLException e) {
            throw new STException(STExceptionID.DBSTORAGE_READ_SPORT_TYPES, "Failed to read all SportTypes!", e);
        }

        readAllSportSubTypes(sportTypes);
        readAllEquipments(sportTypes);
        return sportTypes;
    }

    private void readAllSportSubTypes(List<SportType> sportTypes) throws STException {

        try(var statement = connection.createStatement()) {
            var rs = statement.executeQuery("SELECT * FROM SPORT_SUBTYPE");
            while(rs.next())
            {
                var sportType = getSportTypeById(sportTypes, rs.getInt("SPORT_TYPE_ID"));
                var sportSubType = new SportSubType(rs.getInt("ID"));
                sportSubType.setName(rs.getString("NAME"));
                sportSubType.setFitId(rs.getInt("FIT_ID"));
                sportType.getSportSubTypeList().set(sportSubType);
            }
        } catch (SQLException e) {
            throw new STException(STExceptionID.DBSTORAGE_READ_SPORT_SUBTYPES, "Failed to read all SportSubTypes!", e);
        }
    }

    private void readAllEquipments(List<SportType> sportTypes) throws STException {

        try(var statement = connection.createStatement()) {
            var rs = statement.executeQuery("SELECT * FROM EQUIPMENT");
            while(rs.next())
            {
                var sportType = getSportTypeById(sportTypes, rs.getInt("SPORT_TYPE_ID"));
                var equipment = new Equipment(rs.getInt("ID"));
                equipment.setName(rs.getString("NAME"));
                equipment.setNotInUse(rs.getBoolean("NOT_IN_USE"));
                sportType.getEquipmentList().set(equipment);
            }
        } catch (SQLException e) {
            throw new STException(STExceptionID.DBSTORAGE_READ_EQUIPMENTS, "Failed to read all Equipments!", e);
        }
    }

    private SportType getSportTypeById(List<SportType> sportTypes, int sportTypeId) {
        return sportTypes.stream()
                .filter(st -> sportTypeId == st.getId())
                .findAny()
                .get();
    }

    private SportSubType getSportSubTypeById(SportType sportType, int sportSubTypeId) {
        return sportType.getSportSubTypeList().stream()
                .filter(sst -> sportSubTypeId == sst.getId())
                .findAny()
                .get();
    }

    private Equipment getEquipmentById(SportType sportType, int equipmentId) {
        return sportType.getEquipmentList().stream()
                .filter(eq -> equipmentId == eq.getId())
                .findAny()
                .orElse(null);
    }
}
