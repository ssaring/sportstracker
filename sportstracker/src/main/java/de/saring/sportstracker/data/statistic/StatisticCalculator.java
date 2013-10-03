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

        double totalAvgSpeed = 0;
        long totalHeartRate = 0;

        int numberOfExerciseWithDistance = 0;
        int numberOfExercisesWithHeartRate = 0;
        int numberOfExercisesWithCalories = 0;

        // start with 1st exercise for minimum values
        Exercise firstExercise = exercises.getAt(0);
        minDistance = firstExercise.getDistance();
        minAvgSpeed = firstExercise.getAvgSpeed();
        minDuration = firstExercise.getDuration();
        minAscent = firstExercise.getAscent();
        minAvgHeartRate = firstExercise.getAvgHeartRate();

        // process all exercises
        for (Exercise currExercise : exercises) {

            // count number of exercises with recorded distance
            boolean fDistanceRecorded = currExercise.getDistance() > 0 && currExercise.getAvgSpeed() > 0;
            if (fDistanceRecorded) {
                numberOfExerciseWithDistance++;
            }

            // calculate total values
            totalDistance += currExercise.getDistance();
            totalAvgSpeed += currExercise.getAvgSpeed();
            totalDuration += currExercise.getDuration();
            totalAscent += currExercise.getAscent();

            // include heartrate in statistic only when specified
            if (currExercise.getAvgHeartRate() > 0) {
                totalHeartRate += currExercise.getAvgHeartRate();
                numberOfExercisesWithHeartRate++;
            }

            // include calories in statistic only when specified
            if (currExercise.getCalories() > 0) {
                totalCalories += currExercise.getCalories();
                numberOfExercisesWithCalories++;
            }

            // check for minimum values
            if (currExercise.getDistance() < minDistance) {
                minDistance = currExercise.getDistance();
            }

            if (currExercise.getAvgSpeed() < minAvgSpeed) {
                minAvgSpeed = currExercise.getAvgSpeed();
            }

            if (currExercise.getDuration() < minDuration) {
                minDuration = currExercise.getDuration();
            }

            if (currExercise.getAscent() < minAscent) {
                minAscent = currExercise.getAscent();
            }

            // avg heartrate value '0' needs to be ignored, it's not entered by user
            if ((minAvgHeartRate <= 0) ||
                    ((currExercise.getAvgHeartRate() > 0) &&
                            (currExercise.getAvgHeartRate() < minAvgHeartRate))) {
                minAvgHeartRate = currExercise.getAvgHeartRate();
            }

            // calories value '0' needs to be ignored, it's not entered by user
            if ((minCalories <= 0) ||
                    ((currExercise.getCalories() > 0) &&
                            (currExercise.getCalories() < minCalories))) {
                minCalories = currExercise.getCalories();
            }

            // check for maximum values
            if (currExercise.getDistance() > maxDistance) {
                maxDistance = currExercise.getDistance();
            }

            if (currExercise.getAvgSpeed() > maxAvgSpeed) {
                maxAvgSpeed = currExercise.getAvgSpeed();
            }

            if (currExercise.getDuration() > maxDuration) {
                maxDuration = currExercise.getDuration();
            }

            if (currExercise.getAscent() > maxAscent) {
                maxAscent = currExercise.getAscent();
            }

            if (currExercise.getAvgHeartRate() > maxAvgHeartRate) {
                maxAvgHeartRate = currExercise.getAvgHeartRate();
            }

            if (currExercise.getCalories() > maxCalories) {
                maxCalories = currExercise.getCalories();
            }
        }

        // compute AVG distance values when such exercises were in the list
        if (numberOfExerciseWithDistance > 0) {
            avgDistance = (float) (totalDistance / numberOfExerciseWithDistance);
            avgSpeed = (float) (totalAvgSpeed / numberOfExerciseWithDistance);
        } else {
            avgDistance = 0;
            avgSpeed = 0;
        }

        // compute AVG duration and ascent
        avgDuration = totalDuration / exerciseCount;
        avgAscent = totalAscent / exerciseCount;

        // compute AVG heartrate only when it was specified in at least one exercise
        if (numberOfExercisesWithHeartRate > 0) {
            avgHeartRate = (int) (totalHeartRate / numberOfExercisesWithHeartRate);
        }

        // compute average calories only when it was specified in at least one exercise
        if (numberOfExercisesWithCalories > 0) {
            avgCalories = totalCalories / numberOfExercisesWithCalories;
        }
    }

    /**
     * ** BEGIN: Generated Getters and Setters ****
     */

    public int getAvgAscent() {
        return avgAscent;
    }

    public void setAvgAscent(int avgAscent) {
        this.avgAscent = avgAscent;
    }

    public int getAvgCalories() {
        return avgCalories;
    }

    public void setAvgCalories(int avgCalories) {
        this.avgCalories = avgCalories;
    }

    public float getAvgDistance() {
        return avgDistance;
    }

    public void setAvgDistance(float avgDistance) {
        this.avgDistance = avgDistance;
    }

    public int getAvgDuration() {
        return avgDuration;
    }

    public void setAvgDuration(int avgDuration) {
        this.avgDuration = avgDuration;
    }

    public int getAvgHeartRate() {
        return avgHeartRate;
    }

    public void setAvgHeartRate(int avgHeartRate) {
        this.avgHeartRate = avgHeartRate;
    }

    public float getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(float avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public int getExerciseCount() {
        return exerciseCount;
    }

    public void setExerciseCount(int exerciseCount) {
        this.exerciseCount = exerciseCount;
    }

    public int getMaxAscent() {
        return maxAscent;
    }

    public void setMaxAscent(int maxAscent) {
        this.maxAscent = maxAscent;
    }

    public int getMaxAvgHeartRate() {
        return maxAvgHeartRate;
    }

    public void setMaxAvgHeartRate(int maxAvgHeartRate) {
        this.maxAvgHeartRate = maxAvgHeartRate;
    }

    public float getMaxAvgSpeed() {
        return maxAvgSpeed;
    }

    public void setMaxAvgSpeed(float maxAvgSpeed) {
        this.maxAvgSpeed = maxAvgSpeed;
    }

    public int getMaxCalories() {
        return maxCalories;
    }

    public void setMaxCalories(int maxCalories) {
        this.maxCalories = maxCalories;
    }

    public float getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(float maxDistance) {
        this.maxDistance = maxDistance;
    }

    public int getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(int maxDuration) {
        this.maxDuration = maxDuration;
    }

    public int getMinAscent() {
        return minAscent;
    }

    public void setMinAscent(int minAscent) {
        this.minAscent = minAscent;
    }

    public int getMinAvgHeartRate() {
        return minAvgHeartRate;
    }

    public void setMinAvgHeartRate(int minAvgHeartRate) {
        this.minAvgHeartRate = minAvgHeartRate;
    }

    public float getMinAvgSpeed() {
        return minAvgSpeed;
    }

    public void setMinAvgSpeed(float minAvgSpeed) {
        this.minAvgSpeed = minAvgSpeed;
    }

    public int getMinCalories() {
        return minCalories;
    }

    public void setMinCalories(int minCalories) {
        this.minCalories = minCalories;
    }

    public float getMinDistance() {
        return minDistance;
    }

    public void setMinDistance(float minDistance) {
        this.minDistance = minDistance;
    }

    public int getMinDuration() {
        return minDuration;
    }

    public void setMinDuration(int minDuration) {
        this.minDuration = minDuration;
    }

    public int getTotalAscent() {
        return totalAscent;
    }

    public void setTotalAscent(int totalAscent) {
        this.totalAscent = totalAscent;
    }

    public int getTotalCalories() {
        return totalCalories;
    }

    public void setTotalCalories(int totalCalories) {
        this.totalCalories = totalCalories;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public int getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(int totalDuration) {
        this.totalDuration = totalDuration;
    }

    /***** END: Generated Getters and Setters *****/
}