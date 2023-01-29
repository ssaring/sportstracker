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
import jakarta.inject.Singleton;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for reading / storing of the application data from / to a SQLite database.
 *
 * @author Stefan Saring
 */
@Singleton
public class DbStorage {

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
    public List<Exercise> readAllExercises() throws STException {
        var exercises = new ArrayList<Exercise>();

        try(var statement = connection.createStatement()) {
            var rs = statement.executeQuery("SELECT * FROM EXERCISE");
            while(rs.next())
            {
                var exercise = new Exercise(rs.getInt("ID"));
                exercise.setDateTime(Date310Utils.dateToLocalDateTime(rs.getDate("DATE_TIME")));
                // TODO map entities
                exercise.setSportType(new SportType(rs.getInt("SPORT_TYPE_ID")));
                exercise.setSportSubType(new SportSubType(rs.getInt("SPORT_SUBTYPE_ID")));
                exercise.setIntensity(Exercise.IntensityType.valueOf(rs.getString("INTENSITY")));
                exercise.setDuration(rs.getInt("DURATION"));
                exercise.setDistance(rs.getFloat("DISTANCE"));
                exercise.setAvgSpeed(rs.getFloat("AVG_SPEED"));
                exercise.setAvgHeartRate(rs.getInt("AVG_HEARTRATE"));
                exercise.setAscent(rs.getInt("ASCENT"));
                exercise.setDescent(rs.getInt("DESCENT"));
                exercise.setCalories(rs.getInt("CALORIES"));
                exercise.setHrmFile(rs.getString("HRM_FILE"));
                // TODO map entities
                exercise.setEquipment(new Equipment(rs.getInt("EQUIPMENT_ID")));
                exercise.setComment(rs.getString("COMMENT"));
                exercises.add(exercise);
            }
        } catch (SQLException e) {
            throw new STException(STExceptionID.DBSTORAGE_READ_EXERCISES, "Failed to read all Exercises!", e);
        }

        return exercises;
    }
}
