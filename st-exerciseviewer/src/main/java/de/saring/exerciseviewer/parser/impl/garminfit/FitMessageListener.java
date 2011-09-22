package de.saring.exerciseviewer.parser.impl.garminfit;

import java.util.LinkedList;
import java.util.List;

import com.garmin.fit.LapMesg;
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
import de.saring.util.unitcalc.ConvertUtils;

/**
 * This message listener implementation creates the EVExercise object from
 * the FIT messages send by the Decoder (parser).
 */
class FitMessageListener implements MesgListener {
    
    /** The parsed exercise. */
    private EVExercise exercise = null;
    /** List of created laps (collected in a LinkedList and not in EVExercise array, much faster). */
    private List<FitLap> lFitLaps = new LinkedList<>();
    /** List of created exercise samples (collected in a LinkedList and not in EVExercise array, much faster). */
    private List<ExerciseSample> lSamples = new LinkedList<>();
    /** Flag for availability of temperature data. */
    private boolean temperatureAvailable = false;
    
    @Override
    public void onMesg(Mesg mesg) {

        // delegate interesting messages to appropriate handler methods
        switch(mesg.getNum()) {
            case MesgNum.SESSION:
                readSessionMessage(new SessionMesg(mesg));
                break;
            case MesgNum.LAP:
                readLapMessage(new LapMesg(mesg));
                break;
            case MesgNum.RECORD:
                readRecordMessage(new RecordMesg(mesg));
                break;
        }
    }

    /**
     * Reads exercise-level data from the specified Session message.
     * @param mesg Session message
     */
    private void readSessionMessage(SessionMesg mesg) {
        
        // read time data
        exercise = new EVExercise();
        exercise.setFileType(EVExercise.ExerciseFileType.GARMIN_FIT);
        exercise.setDate(mesg.getStartTime().getDate());
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
            exercise.setSpeed(new ExerciseSpeed());
            exercise.getSpeed().setDistance(Math.round(mesg.getTotalDistance()));
            exercise.getSpeed().setSpeedAVG(
                ConvertUtils.convertMeterPerSecond2KilometerPerHour(mesg.getAvgSpeed()));
            exercise.getSpeed().setSpeedMax(
                ConvertUtils.convertMeterPerSecond2KilometerPerHour(mesg.getMaxSpeed()));
        }
        
        // read optional speed data
        if (mesg.getStartPositionLat() != null && mesg.getStartPositionLong() != null) {
            exercise.getRecordingMode().setLocation(true);
        }
        
        // read optional ascent data
        if (mesg.getTotalAscent() != null) {
            exercise.getRecordingMode().setAltitude(true);
            exercise.setAltitude(new ExerciseAltitude());
            exercise.getAltitude().setAscent(mesg.getTotalAscent());
        }

        // read optional cadence data
        if (mesg.getAvgCadence() != null) {
            exercise.getRecordingMode().setCadence(true);
            exercise.setCadence(new ExerciseCadence());
            exercise.getCadence().setCadenceAVG(mesg.getAvgCadence());
            exercise.getCadence().setCadenceMax(mesg.getMaxCadence());
        }
    }

    /**
     * Reads lap-level data from the specified Lap message.
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
            lap.setSpeed(new LapSpeed());
            lap.getSpeed().setDistance(Math.round(mesg.getTotalDistance()));
            lap.getSpeed().setSpeedAVG(
                ConvertUtils.convertMeterPerSecond2KilometerPerHour(mesg.getAvgSpeed()));
        }
        
        // read optional ascent data
        if (mesg.getTotalAscent() != null) {
            lap.setAltitude(new LapAltitude());
            lap.getAltitude().setAscent(mesg.getTotalAscent());
        }

        // read optional position data
        if (mesg.getEndPositionLat() != null && mesg.getEndPositionLong() != null) {
            lap.setPositionSplit(new Position(
                ConvertUtils.convertSemicircle2Degree(mesg.getEndPositionLat()), 
                ConvertUtils.convertSemicircle2Degree(mesg.getEndPositionLong())));
        }
        
        lFitLaps.add(new FitLap(lap, mesg.getTimestamp().getDate()));
    }
    
    /**
     * Reads sample-level data from the specified Record message.
     * @param mesg Record message
     */
    private void readRecordMessage(RecordMesg mesg) {
        
        ExerciseSample sample = new ExerciseSample();
        lSamples.add(sample);

        // sample timestamp must be the offset from start time, will be corrected later 
        sample.setTimestamp(mesg.getTimestamp().getDate().getTime());

        if (mesg.getHeartRate() != null) {
            sample.setHeartRate(mesg.getHeartRate());
        }
        if (mesg.getDistance() != null) {
            sample.setDistance(Math.round(mesg.getDistance()));
        }
        if (mesg.getSpeed () != null) {
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
            sample.setTemperature(mesg.getTemperature());
        }
    }
    
    /**
     * Returns the EVExercise created from the received message. It sets
     * up all lap and sample data and calculates the missing data before.
     * @return
     */
    public EVExercise getExercise() throws EVException {

        // has activity data been found in the FIT file? (FIT files may contain other data too)
        if (exercise == null) {
            throw new EVException("The FIT file does not contain any exercise (activity) data...");
        }
        
        storeSamples();
        storeLaps();

        calculateAltitudeSummary();
        calculateTemperatureSummary();
        return exercise;
    }

    /**
     * Stores the sample data in the exercise. It also fixes the timestamps in all 
     * ExerciseSamples, it must be the offset from the start time.
     */
    private void storeSamples() {            
        long startTime = exercise.getDate().getTime();
        for (ExerciseSample sample : lSamples) {
            sample.setTimestamp(sample.getTimestamp() - startTime);
        }
        exercise.setSampleList(lSamples.toArray(new ExerciseSample[0]));
    }

    /**
     * Stores the lap data in the exercise and calculate the missing values. 
     */
    private void storeLaps() {
        int lapDistanceSum = 0;
        
        // convert FitLap to Lap objects
        List<Lap> lLaps = new LinkedList<>();            
        long startTime = exercise.getDate().getTime();
        
        for (FitLap fitLap : lFitLaps) {
            Lap lap = fitLap.getLap();
            lLaps.add(lap);
            
            // fix the split time in all Laps, it must be the offset from the start time
            lap.setTimeSplit((int) ((fitLap.getSplitTime().getTime() - startTime) / 100));
            
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
                lap.getAltitude().setAltitude(sampleAtLapEnd.getAltitude());
            }
            
            if (temperatureAvailable) {
                lap.setTemperature(new LapTemperature());
                lap.getTemperature().setTemperature(sampleAtLapEnd.getTemperature());
            }
        }

        exercise.setLapList(lLaps.toArray(new Lap[0]));
    }

    /**
     * Returns the closest ExerciseSample for the lap end time.
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
            exercise.getSampleList().length > 0) {

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
            exercise.getAltitude().setAltitudeAVG(
                (short) (Math.round(altitudeSum / (double) exercise.getSampleList().length)));
        }
    }
    
    /**
     * Calculates the min, max and average temperature (if available) from the sample data.
     */
    private void calculateTemperatureSummary() {
        if (temperatureAvailable) {
            exercise.getRecordingMode().setTemperature(true);
            exercise.setTemperature(new ExerciseTemperature());

            short tempMin = Short.MAX_VALUE;
            short tempMax = Short.MIN_VALUE;
            int temperatureSum = 0;

            for (ExerciseSample sample : exercise.getSampleList()) {
                tempMin = (short) Math.min(sample.getTemperature(), tempMin);
                tempMax = (short) Math.max(sample.getTemperature(), tempMax);
                temperatureSum += sample.getTemperature();
            }

            exercise.getTemperature().setTemperatureMin(tempMin);
            exercise.getTemperature().setTemperatureMax(tempMax);
            exercise.getTemperature().setTemperatureAVG(
                (short) (Math.round(temperatureSum / (double) exercise.getSampleList().length)));
        }
    }        
}
