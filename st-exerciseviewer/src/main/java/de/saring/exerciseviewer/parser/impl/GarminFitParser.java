package de.saring.exerciseviewer.parser.impl;

import com.garmin.fit.Decode;
import com.garmin.fit.LapMesg;
import com.garmin.fit.Mesg;
import com.garmin.fit.MesgListener;
import com.garmin.fit.MesgNum;
import com.garmin.fit.RecordMesg;
import com.garmin.fit.SessionMesg;
import de.saring.exerciseviewer.core.EVException;
import de.saring.exerciseviewer.data.ExerciseAltitude;
import de.saring.exerciseviewer.data.ExerciseCadence;
import de.saring.exerciseviewer.data.ExerciseSample;
import de.saring.exerciseviewer.data.ExerciseSpeed;
import de.saring.exerciseviewer.data.Lap;
import de.saring.exerciseviewer.data.EVExercise;
import de.saring.exerciseviewer.data.Position;
import de.saring.exerciseviewer.data.RecordingMode;
import de.saring.exerciseviewer.parser.AbstractExerciseParser;
import de.saring.exerciseviewer.parser.ExerciseParserInfo;
import de.saring.util.unitcalc.ConvertUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * This ExerciseParser implementation is for reading Garmin FIT files (binary
 * format) which contain activity (exercise) data.<br>
 * The parser should support all devices which store their data in FIT format,
 * but it's only tested with Garmin Edge 500 files.<br>
 * The parser uses the Java library "fit.jar" from the official Garmin FIT SDK
 * (open source) for accessing the FIT file content.
 * 
 * @author  Stefan Saring
 * @version 1.0
 */
public class GarminFitParser extends AbstractExerciseParser {
    
    /** Informations about this parser. */
    private final ExerciseParserInfo info = new ExerciseParserInfo("Garmin FIT", new String[] {"fit", "FIT"});



    /** {@inheritDoc} */
    @Override
    public ExerciseParserInfo getInfo() {
        return info;
    }
    
    /** {@inheritDoc} */
    @Override
    public EVExercise parseExercise(String filename) throws EVException {
        FitMesgListener mesgListener = new FitMesgListener();
        readFitFile(filename, mesgListener);
        return mesgListener.getExercise();
    }    

    /**
     * Reads the specified FIT file and creates the appropriate EVExcercise.
     * @param filename name of ther FIT file
     * @param mesgListener listener for creating the exercise from the messages
     */
    private void readFitFile(String filename, MesgListener mesgListener) throws EVException {
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(new File(filename));
            new Decode().read(fis, mesgListener);
        }
        catch (IOException ioe) {
            throw new EVException("Failed to read FIT file '" + filename + "'...", ioe);
        }
        finally {
            if (fis != null) {
                try {
                    fis.close();
                }
                catch (IOException e) {}
            }
        }
    }

    /**
     * This message listener implementation creates the EVExercise object from
     * the FIT messages send by the Decoder (parser).
     */
    private class FitMesgListener implements MesgListener {
        
        /** The created exercise. */
        private EVExercise exercise = new EVExercise();
        /** List of created laps (collected in a LinkedList and not in EVExercise array, much faster). */
        private List<Lap> lLaps = new LinkedList<Lap>();
        /** List of created exercise samples (collected in a LinkedList and not in EVExercise array, much faster). */
        private List<ExerciseSample> lSamples = new LinkedList<ExerciseSample>();

        /** {@inheritDoc} */
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
            // TODO
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
            
            // TODO: why is temperature not available or not shown
            if (mesg.getTemperature() != null) {
                sample.setTemperature(mesg.getTemperature());
            }

        }
        
        /**
         * Returns the EVExercise created from the received message. It sets
         * up all lap and sample data and calculates the missing data before.
         * @return
         */
        public EVExercise getExercise() {
            exercise.setFileType(EVExercise.ExerciseFileType.GARMIN_FIT);

            // store lap and sample data
            fixSampleTimestamps();
            exercise.setLapList(lLaps.toArray(new Lap[0]));
            exercise.setSampleList(lSamples.toArray(new ExerciseSample[0]));

            calculateAltitudeSummary();
            // TODO: compute min, max, avg temperature if available

            return exercise;
        }

        /**
         * Fix timestamps in all ExerciseSamples, it must be the offset from the start time.
         */
        private void fixSampleTimestamps() {
            long startTime = exercise.getDate().getTime();
            for (ExerciseSample sample : lSamples) {
                sample.setTimestamp(sample.getTimestamp() - startTime);
            }
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
    }
}
