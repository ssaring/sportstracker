package de.saring.exerciseviewer.parser.impl.garminfit;

import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;

import com.garmin.fit.DateTime;
import com.garmin.fit.DeviceInfoMesg;
import com.garmin.fit.GarminProduct;
import com.garmin.fit.LapMesg;
import com.garmin.fit.LengthMesg;
import com.garmin.fit.Mesg;
import com.garmin.fit.MesgListener;
import com.garmin.fit.MesgNum;
import com.garmin.fit.RecordMesg;
import com.garmin.fit.SessionMesg;

import de.saring.exerciseviewer.core.EVException;
import de.saring.exerciseviewer.data.EVExercise;
import de.saring.exerciseviewer.data.ExerciseAltitude;
import de.saring.exerciseviewer.data.ExerciseCadence;
import de.saring.exerciseviewer.data.ExerciseSample;
import de.saring.exerciseviewer.data.ExerciseSpeed;
import de.saring.exerciseviewer.data.ExerciseTemperature;
import de.saring.exerciseviewer.data.Lap;
import de.saring.exerciseviewer.data.LapAltitude;
import de.saring.exerciseviewer.data.LapSpeed;
import de.saring.exerciseviewer.data.LapTemperature;
import de.saring.exerciseviewer.data.Position;
import de.saring.exerciseviewer.data.RecordingMode;
import de.saring.util.Date310Utils;
import de.saring.util.unitcalc.CalculationUtils;
import de.saring.util.unitcalc.ConvertUtils;

/**
 * This message listener implementation creates the EVExercise object from
 * the FIT messages send by the Decoder (parser).
 */
class FitMessageListener implements MesgListener {

    /**
     * The parsed exercise.
     */
    private EVExercise exercise = new EVExercise(EVExercise.ExerciseFileType.GARMIN_FIT);
    /**
     * List of created laps (collected in a LinkedList and not in EVExercise array, much faster).
     */
    private final List<FitLap> lFitLaps = new LinkedList<>();
    /**
     * List of created exercise samples (collected in a LinkedList and not in EVExercise array, much faster).
     */
    private final List<ExerciseSample> lSamples = new LinkedList<>();
    /**
     * Flag for availability of temperature data.
     */
    private boolean temperatureAvailable = false;

    @Override
    public void onMesg(Mesg mesg) {

        // delegate interesting messages to appropriate handler methods
        switch (mesg.getNum()) {
            case MesgNum.SESSION:
                readSessionMessage(new SessionMesg(mesg));
                break;
            case MesgNum.LAP:
                readLapMessage(new LapMesg(mesg));
                break;
            case MesgNum.RECORD:
                readRecordMessage(new RecordMesg(mesg));
                break;
            case MesgNum.LENGTH:
                readLengthMessage(new LengthMesg(mesg));
                break;
            case MesgNum.DEVICE_INFO:
                readDeviceInfoMessage(new DeviceInfoMesg(mesg));
                break;
        }
    }

    /**
     * Reads exercise-level data from the specified Session message.
     *
     * @param mesg Session message
     */
    private void readSessionMessage(SessionMesg mesg) {

        // read time data
        exercise.setDateTime(Date310Utils.dateToLocalDateTime(mesg.getStartTime().getDate()));
        exercise.setDuration(Math.round(mesg.getTotalTimerTime() * 10));
        exercise.setRecordingMode(new RecordingMode());

        // read optional heartrate data
        if (mesg.getAvgHeartRate() != null) {
            exercise.setHeartRateAVG(mesg.getAvgHeartRate());
        }
        if (mesg.getMaxHeartRate() != null) {
            exercise.setHeartRateMax(mesg.getMaxHeartRate());
        }
        if (mesg.getTotalCalories() != null) {
            exercise.setEnergy(mesg.getTotalCalories());
        }

        // read optional speed data
        if (mesg.getTotalDistance() != null) {
            exercise.getRecordingMode().setSpeed(true);

            int distance = Math.round(mesg.getTotalDistance());
            // AVG speed might be missing in Garmin Forerunner 910XT exercises, will be calculated afterwards
            Float avgSpeed = mesg.getAvgSpeed();
            if (avgSpeed != null) {
                avgSpeed = ConvertUtils.convertMeterPerSecond2KilometerPerHour(mesg.getAvgSpeed());
            } else {
                avgSpeed = 0f;
            }

            Float maxSpeed = mesg.getMaxSpeed();
            if (maxSpeed != null) {
            	maxSpeed = ConvertUtils.convertMeterPerSecond2KilometerPerHour(mesg.getMaxSpeed());
            } else {
                maxSpeed = 0f;
            }

            exercise.setSpeed(new ExerciseSpeed(avgSpeed, maxSpeed, distance));
        }

        // read optional speed data
        if (mesg.getStartPositionLat() != null && mesg.getStartPositionLong() != null) {
            exercise.getRecordingMode().setLocation(true);
        }

        // read optional ascent data
        if (mesg.getTotalAscent() != null) {
            exercise.getRecordingMode().setAltitude(true);
            exercise.setAltitude(new ExerciseAltitude((short) 0, (short) 0, (short) 0, mesg.getTotalAscent()));
        }

        // read optional cadence data
        if (mesg.getAvgCadence() != null) {
            exercise.getRecordingMode().setCadence(true);
            exercise.setCadence(new ExerciseCadence(mesg.getAvgCadence(), mesg.getMaxCadence()));
        }
    }

    /**
     * Reads lap-level data from the specified Lap message.
     *
     * @param mesg Lap message
     */
    private void readLapMessage(LapMesg mesg) {
        Lap lap = new Lap();

        // read optional heartrate data
        if (mesg.getAvgHeartRate() != null) {
            lap.setHeartRateAVG(mesg.getAvgHeartRate());
        }
        if (mesg.getMaxHeartRate() != null) {
            lap.setHeartRateMax(mesg.getMaxHeartRate());
        }

        // read optional speed data
        if (mesg.getTotalDistance() != null) {
            int lapSpeedDistance = Math.round(mesg.getTotalDistance());
            // AVG speed might be missing in Garmin Forerunner 910XT exercises, will be calculated afterwards
            Float lapSpeedAVG = mesg.getAvgSpeed();
            if (lapSpeedAVG != null) {
                lapSpeedAVG = ConvertUtils.convertMeterPerSecond2KilometerPerHour(lapSpeedAVG);
            } else {
                lapSpeedAVG = 0f;
            }
            lap.setSpeed(new LapSpeed(0f, lapSpeedAVG, lapSpeedDistance, null));
        }

        // read optional ascent data
        if (mesg.getTotalAscent() != null) {
            lap.setAltitude(new LapAltitude((short) 0, mesg.getTotalAscent()));
        }

        // read optional position data
        if (mesg.getEndPositionLat() != null && mesg.getEndPositionLong() != null) {
            lap.setPositionSplit(new Position(
                    ConvertUtils.convertSemicircle2Degree(mesg.getEndPositionLat()),
                    ConvertUtils.convertSemicircle2Degree(mesg.getEndPositionLong())));
        }

        lFitLaps.add(new FitLap(lap, Date310Utils.dateToLocalDateTime(mesg.getTimestamp().getDate())));
    }

    /**
     * Reads sample-level data from the specified Record message.
     *
     * @param mesg Record message
     */
    private void readRecordMessage(RecordMesg mesg) {

        ExerciseSample sample = new ExerciseSample();
        lSamples.add(sample);

        // sample timestamp must be the offset from start time, will be corrected later
        // (in some cases the timestamp is missing and will be read from the next Length message)
        DateTime timestamp = mesg.getTimestamp();
        if (timestamp != null) {
            sample.setTimestamp(timestamp.getDate().getTime());
        }

        if (mesg.getHeartRate() != null) {
            sample.setHeartRate(mesg.getHeartRate());
        }
        if (mesg.getDistance() != null) {
            sample.setDistance(Math.round(mesg.getDistance()));
        }
        if (mesg.getSpeed() != null) {
            sample.setSpeed(
                    ConvertUtils.convertMeterPerSecond2KilometerPerHour(mesg.getSpeed()));
        }
        if (mesg.getAltitude() != null) {
            sample.setAltitude((short) Math.round(mesg.getAltitude()));
        }
        if (mesg.getCadence() != null) {
            sample.setCadence(mesg.getCadence());
        }

        if (mesg.getPositionLat() != null && mesg.getPositionLong() != null) {
            sample.setPosition(new Position(
                    ConvertUtils.convertSemicircle2Degree(mesg.getPositionLat()),
                    ConvertUtils.convertSemicircle2Degree(mesg.getPositionLong())));
        }

        if (mesg.getTemperature() != null) {
            temperatureAvailable = true;
            sample.setTemperature(mesg.getTemperature().shortValue());
        }
    }

    /**
     * Special handling for Garmin Forerunner 910XT exercise files: Length messages are stored mostly in swimming
     * exercises, they contain informations for a "length". For some reason the previous read Record message does
     * not contain any timing informations, these are contained in the Length messages. So the end timestamp must
     * be read and assigned to the last read sample record here.
     *
     * @param mesg Length message
     */
    private void readLengthMessage(LengthMesg mesg) {

        long startTimestamp = mesg.getStartTime().getDate().getTime();
        long totalElapsedTime = Math.round(mesg.getTotalElapsedTime().doubleValue() * 1000d);
        long endTimestamp = startTimestamp + totalElapsedTime;

        ExerciseSample lastSample = lSamples.get(lSamples.size() - 1);
        lastSample.setTimestamp(endTimestamp);
    }

    /**
     * Reads HRM device information from the specified DeviceInfoMesg message.
     *
     * @param mesg device info message
     */
    private void readDeviceInfoMessage(DeviceInfoMesg mesg) {

        final Integer garminProductId = mesg.getGarminProduct();
        if (garminProductId != null && garminProductId.intValue() > 100) {
            String productName = getGarminProductConstantName(garminProductId);
            if (productName != null) {
                exercise.setDeviceName("Garmin " + productName);
            }
        }
    }

    /**
     * Returns the EVExercise created from the received message. It sets
     * up all lap and sample data and calculates the missing data before.
     *
     * @return exercise
     */
    public EVExercise getExercise() throws EVException {

        // has activity data been found in the FIT file? (FIT files may contain other data too)
        if (exercise.getDateTime() == null) {
            throw new EVException("The FIT file does not contain any exercise (activity) data...");
        }

        storeSamples();
        storeLaps();

        calculateAltitudeSummary();
        calculateTemperatureSummary();
        calculateMissingAverageSpeed();
        calculateMissingMaxSpeed();
        calculateMissingHeartRateAVG();
        calculateMissingHeartRateMax();
        return exercise;
    }

	/**
     * Stores the sample data in the exercise. It also fixes the timestamps in all
     * ExerciseSamples, it must be the offset from the start time.
     */
    private void storeSamples() {
        long startTime = Date310Utils.getMilliseconds(exercise.getDateTime());
        for (ExerciseSample sample : lSamples) {
            sample.setTimestamp(sample.getTimestamp() - startTime);
        }
        exercise.getSampleList().addAll(lSamples);
    }

    /**
     * Stores the lap data in the exercise and calculate the missing values.
     */
    private void storeLaps() {
        int lapDistanceSum = 0;

        // convert FitLap to Lap objects
        List<Lap> lLaps = new LinkedList<>();
        long startTime = Date310Utils.getMilliseconds(exercise.getDateTime());

        for (FitLap fitLap : lFitLaps) {
            Lap lap = fitLap.getLap();
            lLaps.add(lap);

            // fix the split time in all Laps, it must be the offset from the start time
            long lapSplitDateTimeMillis = Date310Utils.getMilliseconds(fitLap.getSplitDatTime());
            lap.setTimeSplit((int) ((lapSplitDateTimeMillis - startTime) / 100));

            // get all the missing lap data from the sample at lap end time
            ExerciseSample sampleAtLapEnd = getExerciseSampleForLapEnd(lap);
            lap.setHeartRateSplit(sampleAtLapEnd.getHeartRate());

            if (lap.getSpeed() != null) {
                // fix lap distance, it must be the distance from exercise start (FIT stores from Lap start)
                lapDistanceSum += lap.getSpeed().getDistance();
                lap.getSpeed().setDistance(lapDistanceSum);

                lap.getSpeed().setSpeedEnd(sampleAtLapEnd.getSpeed());
                lap.getSpeed().setCadence(sampleAtLapEnd.getCadence());
            }

            if (lap.getAltitude() != null) {
                lap.setAltitude(lap.getAltitude().copy(
                        (short) sampleAtLapEnd.getAltitude(), lap.getAltitude().getAscent()));
            }

            if (temperatureAvailable) {
                lap.setTemperature(new LapTemperature(sampleAtLapEnd.getTemperature()));
            }
        }

        exercise.getLapList().addAll(lLaps);
    }

    /**
     * Returns the closest ExerciseSample for the lap end time.
     *
     * @param lap the lap for search
     * @return the closest ExerciseSample
     */
    private ExerciseSample getExerciseSampleForLapEnd(Lap lap) {
        long lapSplitTimestamp = lap.getTimeSplit() * 100L;
        ExerciseSample closestSample = null;
        long closestTimeDistance = Long.MAX_VALUE;

        for (ExerciseSample sample : exercise.getSampleList()) {
            long timeDistance = Math.abs(sample.getTimestamp() - lapSplitTimestamp);
            if (timeDistance < closestTimeDistance) {
                closestTimeDistance = timeDistance;
                closestSample = sample;
            }
        }
        return closestSample;
    }

    /**
     * Calculates the min, max and average altitude (if available) from the sample data.
     */
    private void calculateAltitudeSummary() {
        if (exercise.getRecordingMode().isAltitude() &&
                exercise.getSampleList().size() > 0) {

            short altMin = Short.MAX_VALUE;
            short altMax = Short.MIN_VALUE;
            int altitudeSum = 0;

            for (ExerciseSample sample : exercise.getSampleList()) {
                altMin = (short) Math.min(sample.getAltitude(), altMin);
                altMax = (short) Math.max(sample.getAltitude(), altMax);
                altitudeSum += sample.getAltitude();
            }

            exercise.getAltitude().setAltitudeMin(altMin);
            exercise.getAltitude().setAltitudeMax(altMax);
            exercise.getAltitude().setAltitudeAvg(
                    (short) (Math.round(altitudeSum / (double) exercise.getSampleList().size())));
        }
    }

    /**
     * Calculates the min, max and average temperature (if available) from the sample data.
     */
    private void calculateTemperatureSummary() {
        if (temperatureAvailable) {
            exercise.getRecordingMode().setTemperature(true);

            short tempMin = Short.MAX_VALUE;
            short tempMax = Short.MIN_VALUE;
            int temperatureSum = 0;

            for (ExerciseSample sample : exercise.getSampleList()) {
                tempMin = (short) Math.min(sample.getTemperature(), tempMin);
                tempMax = (short) Math.max(sample.getTemperature(), tempMax);
                temperatureSum += sample.getTemperature();
            }

            short tempAvg = (short) (Math.round(temperatureSum / (double) exercise.getSampleList().size()));
            exercise.setTemperature(new ExerciseTemperature(tempMin, tempAvg, tempMax));
        }
    }

    /**
     * Workaround for Garmin Forerunner 910XT exercise files: the AVG speed is often not available
     * for the parsed laps and for the exercise (for unknown reasons). So the AVG speed needs to
     * be calculated after parsing.
     * TODO This workaround works only for exercise files without transitions between sport types
     * (then there are negative timestamps for some strange reasons).
     */
    private void calculateMissingAverageSpeed() {

        ExerciseSpeed exerciseSpeed = exercise.getSpeed();
        if (exerciseSpeed != null && exerciseSpeed.getSpeedAvg() == 0f) {
            exerciseSpeed.setSpeedAvg(CalculationUtils.calculateAvgSpeed(
                    exerciseSpeed.getDistance() / 1000f, Math.round(exercise.getDuration() / 10f)));
        }

        for (Lap lap : exercise.getLapList()) {
            LapSpeed lapSpeed = lap.getSpeed();
            if (lapSpeed != null && lapSpeed.getSpeedAVG() == 0f) {
                lapSpeed.setSpeedAVG(CalculationUtils.calculateAvgSpeed(
                        lapSpeed.getDistance() / 1000f, Math.round(lap.getTimeSplit() / 10f)));
            }
        }
    }

    /**
     * Gets the name of the Garmin product ID constant from class GarminProduct for the specified product ID
     * (done via Reflection).
     *
     * @param constantValue Garmin product ID
     * @return constant / device name or null if not found
     */
    private String getGarminProductConstantName(final Integer constantValue) {
        final Class<GarminProduct> gcClass = GarminProduct.class;
        for (java.lang.reflect.Field field : gcClass.getDeclaredFields()) {
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers) && Modifier.isFinal(modifiers)) {
                try {
                    if (constantValue.equals(field.get(null))) {
                        return field.getName();
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * Calculates the max speed of the exercise, if missing (e.g. in Fenix exercise files).
     */
	private void calculateMissingMaxSpeed() {
        if (exercise.getSpeed().getSpeedMax() < 0.01) {

            exercise.getSampleList().stream() //
                .mapToDouble(sample -> sample.getSpeed()) //
                .max() //
                .ifPresent(maxSpeed -> exercise.getSpeed().setSpeedMax((float) maxSpeed));
        }
	}

    /**
     * Calculates the average heartrate of the exercise, if missing (e.g. in Fenix exercise files).
     */
    private void calculateMissingHeartRateAVG() {
		if (exercise.getHeartRateAVG() == null) {

            exercise.getSampleList().stream() //
                    .mapToDouble(sample -> sample.getHeartRate()) //
                    .average() //
                    .ifPresent(avgHeartRate -> exercise.setHeartRateAVG((short) Math.round(avgHeartRate)));
		}
    }

    /**
     * Calculates the maximum heartrate of the exercise, if missing (e.g. in Fenix exercise files).
     */
	private void calculateMissingHeartRateMax() {
        if (exercise.getHeartRateMax() == null) {

            exercise.getSampleList().stream() //
                    .mapToInt(sample -> sample.getHeartRate()) //
                    .max() //
                    .ifPresent(maxHeartRate -> exercise.setHeartRateMax((short) maxHeartRate));
        }
    }
}
