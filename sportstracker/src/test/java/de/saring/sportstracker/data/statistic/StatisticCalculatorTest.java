package de.saring.sportstracker.data.statistic;

import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.data.SportSubType;
import de.saring.sportstracker.data.SportType;
import de.saring.util.data.IdObjectList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This class contains all unit tests for the StatisticCalculator class.
 *
 * @author Stefan Saring
 * @version 1.0
 */
public class StatisticCalculatorTest {

    private IdObjectList<Exercise> lExercises;

    /**
     * This method initializes the environment for testing.
     */
    @BeforeEach
    public void setUp() {

        // create one sport type with one subtype
        SportType sportType1 = new SportType(1);
        SportSubType sportSubType1 = new SportSubType(1);
        sportType1.getSportSubTypeList().set(sportSubType1);

        // create some exercises for statistic calculation
        lExercises = new IdObjectList<>();

        Exercise tempExercise = new Exercise(1);
        tempExercise.setSportType(sportType1);
        tempExercise.setSportSubType(sportSubType1);
        tempExercise.setDateTime(LocalDateTime.now());
        tempExercise.setIntensity(Exercise.IntensityType.NORMAL);
        tempExercise.setDistance(20);
        tempExercise.setAvgSpeed(20);
        tempExercise.setDuration(3600);
        tempExercise.setAscent(350);
        tempExercise.setDescent(330);
        tempExercise.setAvgHeartRate(138);
        lExercises.set(tempExercise);

        tempExercise = new Exercise(2);
        tempExercise.setSportType(sportType1);
        tempExercise.setSportSubType(sportSubType1);
        tempExercise.setDateTime(LocalDateTime.now());
        tempExercise.setIntensity(Exercise.IntensityType.NORMAL);
        tempExercise.setDistance(6);
        tempExercise.setAvgSpeed(8.2f);
        tempExercise.setDuration(2634);
        tempExercise.setAvgHeartRate(140);
        tempExercise.setCalories(890);
        lExercises.set(tempExercise);

        tempExercise = new Exercise(3);
        tempExercise.setSportType(sportType1);
        tempExercise.setSportSubType(sportSubType1);
        tempExercise.setDateTime(LocalDateTime.now());
        tempExercise.setIntensity(Exercise.IntensityType.NORMAL);
        tempExercise.setDistance(31);
        tempExercise.setAvgSpeed(19);
        tempExercise.setDuration(5873);
        tempExercise.setAscent(310);
        tempExercise.setDescent(320);
        tempExercise.setCalories(620);
        lExercises.set(tempExercise);

        tempExercise = new Exercise(4);
        tempExercise.setSportType(sportType1);
        tempExercise.setSportSubType(sportSubType1);
        tempExercise.setDateTime(LocalDateTime.now());
        tempExercise.setIntensity(Exercise.IntensityType.LOW);
        tempExercise.setDistance(0);
        tempExercise.setAvgSpeed(0);
        tempExercise.setDuration(7200);
        tempExercise.setAscent(0);
        tempExercise.setDescent(0);
        tempExercise.setCalories(830);
        lExercises.set(tempExercise);
    }

    /**
     * Tests the calculation.
     */
    @Test
    public void testStatisticCalculator() {
        // calculate statistics
        StatisticCalculator calculator = new StatisticCalculator(lExercises);

        // check statistic results
        assertEquals(4, calculator.getExerciseCount());
        assertEquals(57d, calculator.getTotalDistance(), 0.01f);
        assertEquals(19307, calculator.getTotalDuration());
        assertEquals(660, calculator.getTotalAscent());
        assertEquals(2340, calculator.getTotalCalories());

        assertEquals(57 / 3f, calculator.getAvgDistance(), 0.01f);
        assertEquals(47.2f / 3, calculator.getAvgSpeed(), 0.01f);
        assertEquals(19307 / 4, calculator.getAvgDuration());
        assertEquals(660 / 4, calculator.getAvgAscent());
        assertEquals(650 / 4, calculator.getAvgDescent());
        assertEquals(278 / 2, calculator.getAvgHeartRate());
        assertEquals(2340 / 3, calculator.getAvgCalories());

        assertEquals(0f, calculator.getMinDistance(), 0.001f);
        assertEquals(0f, calculator.getMinAvgSpeed(), 0.001f);
        assertEquals(2634, calculator.getMinDuration());
        assertEquals(0, calculator.getMinAscent());
        assertEquals(0, calculator.getMinDescent());
        assertEquals(138, calculator.getMinAvgHeartRate());
        assertEquals(620, calculator.getMinCalories());

        assertEquals(31f, calculator.getMaxDistance(), 0.01f);
        assertEquals(20f, calculator.getMaxAvgSpeed(), 0.01f);
        assertEquals(7200, calculator.getMaxDuration());
        assertEquals(350, calculator.getMaxAscent());
        assertEquals(330, calculator.getMaxDescent());
        assertEquals(140, calculator.getMaxAvgHeartRate());
        assertEquals(890, calculator.getMaxCalories());
    }
}
