package de.saring.sportstracker.storage.db;

import de.saring.sportstracker.core.STException;
import de.saring.sportstracker.core.STExceptionID;
import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.data.SportType;
import de.saring.util.Date310Utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Database repository for the Exercise data.
 *
 * @author Stefan Saring
 */
public class ExerciseRepository {

    private final Connection connection;

    public ExerciseRepository(Connection connection) {
        this.connection = connection;
    }

    public List<Exercise> readAllExercises(List<SportType> sportTypes) throws STException {
        var exercises = new ArrayList<Exercise>();

        try(var statement = connection.prepareStatement("SELECT * FROM EXERCISE")) {
            var rs = statement.executeQuery();
            while(rs.next())
            {
                var sportType = RepositoryUtil.getSportTypeById(sportTypes, rs.getInt("SPORT_TYPE_ID"));
                var sportSubType = RepositoryUtil.getSportSubTypeById(sportType, rs.getInt("SPORT_SUBTYPE_ID"));
                var equipmentID = RepositoryUtil.getIntegerOrNull(rs, "EQUIPMENT_ID");
                var equipment = equipmentID == null ? null : RepositoryUtil.getEquipmentById(sportType, equipmentID);

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

}
