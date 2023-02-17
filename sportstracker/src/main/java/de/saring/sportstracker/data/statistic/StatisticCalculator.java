package de.saring.sportstracker.data.statistic;

import de.saring.sportstracker.data.Exercise;
import de.saring.util.data.IdObjectList;

/**
 * This class is for calculating exercise statistics.
 *
 * @author Stefan Saring
 * @version 1.0
 */
public class StatisticCalculator {

    /**
     * Number of exercises in this statistic.
     */
    private int exerciseCount = 0;

    /**
     * Total distance of the exercises in kilometers.
     */
    private double totalDistance = 0;

    /**
     * Total duration of the exercises in seconds.
     */
    private int totalDuration = 0;

    /**
     * Total ascent of the exercises in meters.
     */
    private int totalAscent = 0;

    /**
     * Total descent of the exercises in meters.
     */
    private int totalDescent = 0;

    /**
     * Total calorie consumption in kCal.
     */
    private int totalCalories = 0;

    /**
     * Average distance of the exercises in kilometers.
     */
    private float avgDistance = 0;

    /**
     * Average speed of the exercises in kilometers/hour.
     */
    private float avgSpeed = 0;

    /**
     * Average duration of the exercises in seconds.
     */
    private int avgDuration = 0;

    /**
     * Average ascent of the exercises in meters.
     */
    private int avgAscent = 0;

    /**
     * Average descent of the exercises in meters.
     */
    private int avgDescent = 0;

    /**
     * Average heartrate of the exercises in beats per minute.
     */
    private int avgHeartRate = 0;

    /**
     * Average calorie consumption in kCal.
     */
    private int avgCalories = 0;

    /**
     * Minimum distance of the exercises in kilometers.
     */
    private float minDistance = 0;

    /**
     * Minimum average speed of the exercises in kilometers/hour.
     */
    private float minAvgSpeed = 0;

    /**
     * Minimum duration of the exercises in seconds.
     */
    private int minDuration = 0;

    /**
     * Minimum ascent of the exercises in meters.
     */
    private int minAscent = 0;

    /**
     * Minimum descent of the exercises in meters.
     */
    private int minDescent = 0;

    /**
     * Minimum average heartrate of the exercises in beats per minute.
     */
    private int minAvgHeartRate = 0;

    /**
     * Minimum calorie consumption in kCal.
     */
    private int minCalories = 0;

    /**
     * Maximum distance of the exercises in kilometers.
     */
    private float maxDistance = 0;

    /**
     * Maximum average speed of the exercises in kilometers/hour.
     */
    private float maxAvgSpeed = 0;

    /**
     * Maximum duration of the exercises in seconds.
     */
    private int maxDuration = 0;

    /**
     * Maximum ascent of the exercises in meters.
     */
    private int maxAscent = 0;

    /**
     * Maximum descent of the exercises in meters.
     */
    private int maxDescent = 0;

    /**
     * Maximum average heartrate of the exercises in beats per minute.
     */
    private int maxAvgHeartRate = 0;

    /**
     * Maximum calorie consumption in kCal.
     */
    private int maxCalories = 0;


    /**
     * Creates a new StatisticCalculator instance. The statistic will be
     * calculated immediately for the specified exercises. The results can be
     * retrieved from the class properties.
     *
     * @param exercises list of Exercise objects for statistic calculation
     */
    public StatisticCalculator(IdObjectList<Exercise> exercises) {

        exerciseCount = exercises.size();
        if (exerciseCount == 0) {
            return;
        }

        // compute distance statistics
        totalDistance = exercises.stream()
                .mapToDouble(it -> it.getDistance())
                .sum();

        minDistance = (float) exercises.stream()
                .mapToDouble(it -> it.getDistance())
                .min()
                .orElse(0);

        maxDistance = (float) exercises.stream()
                .mapToDouble(it -> it.getDistance())
                .max()
                .orElse(0);

        avgDistance = (float) exercises.stream()
                .mapToDouble(it -> it.getDistance())
                .average()
                .orElse(0);

        // compute AVG speed statistics
        minAvgSpeed = (float) exercises.stream()
                .mapToDouble(it -> it.getAvgSpeed())
                .min()
                .orElse(0);

        maxAvgSpeed = (float) exercises.stream()
                .mapToDouble(it -> it.getAvgSpeed())
                .max()
                .orElse(0);

        avgSpeed = (float) exercises.stream()
                .mapToDouble(it -> it.getAvgSpeed())
                .average()
                .orElse(0);

        // compute duration statistics
        totalDuration = exercises.stream()
                .mapToInt(it -> it.getDuration())
                .sum();

        minDuration = exercises.stream()
                .mapToInt(it -> it.getDuration())
                .min()
                .orElse(0);

        maxDuration = exercises.stream()
                .mapToInt(it -> it.getDuration())
                .max()
                .orElse(0);

        avgDuration = (int) exercises.stream()
                .mapToInt(it -> it.getDuration())
                .average()
                .orElse(0);

        // compute ascent statistics (optional values)
        totalAscent = exercises.stream()
                .filter(it -> it.getAscent() != null)
                .mapToInt(it -> it.getAscent())
                .sum();

        minAscent = exercises.stream()
                .filter(it -> it.getAscent() != null)
                .mapToInt(it -> it.getAscent())
                .min()
                .orElse(0);

        maxAscent = exercises.stream()
                .filter(it -> it.getAscent() != null)
                .mapToInt(it -> it.getAscent())
                .max()
                .orElse(0);

        avgAscent = (int) exercises.stream()
                .filter(it -> it.getAscent() != null)
                .mapToInt(it -> it.getAscent())
                .average()
                .orElse(0);

        // compute descent statistics (optional values)
        totalDescent = exercises.stream()
                .filter(it -> it.getDescent() != null)
                .mapToInt(it -> it.getDescent())
                .sum();

        minDescent = exercises.stream()
                .filter(it -> it.getDescent() != null)
                .mapToInt(it -> it.getDescent())
                .min()
                .orElse(0);

        maxDescent = exercises.stream()
                .filter(it -> it.getDescent() != null)
                .mapToInt(it -> it.getDescent())
                .max()
                .orElse(0);

        avgDescent = (int) exercises.stream()
                .filter(it -> it.getDescent() != null)
                .mapToInt(it -> it.getDescent())
                .average()
                .orElse(0);

        // compute AVG heartrate statistics (optional values)
        minAvgHeartRate = exercises.stream()
                .filter(it -> it.getAvgHeartRate() != null)
                .mapToInt(it -> it.getAvgHeartRate())
                .min()
                .orElse(0);

        maxAvgHeartRate = exercises.stream()
                .filter(it -> it.getAvgHeartRate() != null)
                .mapToInt(it -> it.getAvgHeartRate())
                .max()
                .orElse(0);

        avgHeartRate = (int) exercises.stream()
                .filter(it -> it.getAvgHeartRate() != null)
                .mapToInt(it -> it.getAvgHeartRate())
                .average()
                .orElse(0);

        // compute calories statistics (optional values)
        totalCalories = exercises.stream()
                .filter(it -> it.getCalories() != null)
                .mapToInt(it -> it.getCalories())
                .sum();

        minCalories = exercises.stream()
                .filter(it -> it.getCalories() != null)
                .mapToInt(it -> it.getCalories())
                .min()
                .orElse(0);

        maxCalories = exercises.stream()
                .filter(it -> it.getCalories() != null)
                .mapToInt(it -> it.getCalories())
                .max()
                .orElse(0);

        avgCalories = (int) exercises.stream()
                .filter(it -> it.getCalories() != null)
                .mapToInt(it -> it.getCalories())
                .average()
                .orElse(0);
    }

    public int getAvgAscent() {
        return avgAscent;
    }

    public int getAvgDescent() {
        return avgDescent;
    }

    public int getAvgCalories() {
        return avgCalories;
    }

    public float getAvgDistance() {
        return avgDistance;
    }

    public int getAvgDuration() {
        return avgDuration;
    }

    public int getAvgHeartRate() {
        return avgHeartRate;
    }

    public float getAvgSpeed() {
        return avgSpeed;
    }

    public int getExerciseCount() {
        return exerciseCount;
    }

    public int getMaxAscent() {
        return maxAscent;
    }

    public int getMaxDescent() {
        return maxDescent;
    }

    public int getMaxAvgHeartRate() {
        return maxAvgHeartRate;
    }

    public float getMaxAvgSpeed() {
        return maxAvgSpeed;
    }

    public int getMaxCalories() {
        return maxCalories;
    }

    public float getMaxDistance() {
        return maxDistance;
    }

    public int getMaxDuration() {
        return maxDuration;
    }

    public int getMinAscent() {
        return minAscent;
    }

    public int getMinDescent() {
        return minDescent;
    }

    public int getMinAvgHeartRate() {
        return minAvgHeartRate;
    }

    public float getMinAvgSpeed() {
        return minAvgSpeed;
    }

    public int getMinCalories() {
        return minCalories;
    }

    public float getMinDistance() {
        return minDistance;
    }

    public int getMinDuration() {
        return minDuration;
    }

    public int getTotalAscent() {
        return totalAscent;
    }

    public int getTotalDescent() {
        return totalDescent;
    }

    public int getTotalCalories() {
        return totalCalories;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public int getTotalDuration() {
        return totalDuration;
    }
}