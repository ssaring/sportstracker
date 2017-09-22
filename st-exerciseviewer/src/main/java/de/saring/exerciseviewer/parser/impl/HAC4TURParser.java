package de.saring.exerciseviewer.parser.impl;

import de.saring.exerciseviewer.core.EVException;
import de.saring.exerciseviewer.data.*;
import de.saring.exerciseviewer.parser.AbstractExerciseParser;
import de.saring.exerciseviewer.parser.ExerciseParserInfo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class implements a parser for the .TUR files which are created with
 * CicloSport HACTronic software.
 *
 * @author Stefan Saring (the C# version was done by Ilja Booij)
 */
public class HAC4TURParser extends AbstractExerciseParser {

    /**
     * Informations about this parser.
     */
    private final ExerciseParserInfo info = new ExerciseParserInfo("HACTronic", Arrays.asList("tur", "TUR"));

    /**
     * String that's always on top of TUR files.
     */
    private static final String VERSION_HEADER_STRING = "HACtronic - Tour";

    /**
     * Most interesting values can be found on fixed lines (counting from 0.)
     */
    private interface FilePosition {

        public static final int VERSION_HEADER = 0;
        public static final int START_DATE = 7;
        public static final int START_TIME = 8;
        public static final int NR_OF_LINES_IN_NOTE = 9;
        public static final int DURATION = 11;
        public static final int AVERAGE_SPEED = 15;
        public static final int AVERAGE_HEART_RATE = 19;
        public static final int HR_LIMIT_UPPER = 35;
        public static final int HR_LIMIT_LOWER = 36;
        public static final int ODOMETER = 39;
        public static final int TOTAL_EXERCISE_TIME = 40;
        public static final int NR_SAMPLES = 53;
        public static final int BEGIN_SAMPLES = 54;
    }

    /**
     * Private inner class which is used for reading samples from the file.
     */
    private static class Sample {
        private int[] contents = new int[20];
        private int distance = 0;
        private int altitude;
        private int heartRate;
        private int cadence;
        private int temperature;
        private long time;

        /**
         * Construct a new sample with the sample bytes. This will initialize
         * all sample values.
         */
        public Sample(int[] contents) throws EVException {
            if (contents.length != 20) {
                throw new EVException("Length of a sample should be 20 bytes!");
            }
            this.contents = contents;
            initializeValues();
        }

        /**
         * distance in kilometers
         */
        public int getDistance() {
            return distance;
        }

        /**
         * altitude in meters
         */
        public int getAltitude() {
            return altitude;
        }

        /**
         * heart rate
         */
        public int getHeartRate() {
            return heartRate;
        }

        /**
         * cadence
         */
        public int getCadence() {
            return cadence;
        }

        /**
         * time
         */
        public long getTime() {
            return time;
        }

        /**
         * temperature in degrees celcius
         */
        public int getTemperature() {
            return temperature;
        }

        private void initializeValues() {
            this.distance = calculateDistance();
            this.altitude = calculateAltitude();
            this.heartRate = calculateHeartRate();
            this.cadence = calculateCadence();
            this.temperature = calculateTemperature();
            this.time = calculateTime();
        }

        private long calculateTime() {
            int[] timeBytes = Arrays.copyOfRange(contents, 0, 0 + 4);
            return (timeBytes[0] & 0xff)
                    + ((timeBytes[1] << 8) & 0xff00)
                    + ((timeBytes[2] << 16) & 0xff0000)
                    + ((timeBytes[3] << 24) & 0xff000000);
        }

        private int calculateDistance() {
            int[] distanceBytes = Arrays.copyOfRange(contents, 8, 8 + 4);
            int distance = 0;
            for (int theByte = 0; theByte < distanceBytes.length; theByte++) {
                distance += distanceBytes[theByte] * (int) Math.pow(2, theByte * 8);
            }
            return distance;
        }

        private int calculateAltitude() {
            int[] altitudeBytes = Arrays.copyOfRange(contents, 12, 12 + 2);
            int altitude = 0;
            for (int theByte = 0; theByte < altitudeBytes.length; theByte++) {
                altitude += altitudeBytes[theByte] * (int) Math.pow(2, theByte * 8);
            }
            return altitude;
        }

        private int calculateHeartRate() {
            return contents[14];
        }

        private int calculateCadence() {
            return contents[15];
        }

        private int calculateTemperature() {
            return contents[16];
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("distance = ").append(distance).append(", ");
            builder.append("altitude = ").append(altitude).append(", ");
            builder.append("HR = ").append(heartRate).append(", ");
            builder.append("Cadence = ").append(cadence).append(", ");
            builder.append("Temp = ").append(temperature).append(", ");
            builder.append("Time (seconds) = ").append(time).append(", ");
            return builder.toString();
        }
    }

    private String[] fileContents;
    private int[] fileContentsBytes;
    private int nrOfLinesInNote;
    private int sampleInterval;

    @Override
    public ExerciseParserInfo getInfo() {
        return info;
    }

    @Override
    public EVExercise parseExercise(String filename) throws EVException {

        // read file to array of strings and to array of bytes.
        // we'll need both as the tur file contains both text (header)
        // and binary (samples) information.
        fileContents = readFileToStringArray(filename);
        fileContentsBytes = readFileToByteArray(filename);

        // Create a new exercise file and give it the right type
        EVExercise exercise = new EVExercise(EVExercise.ExerciseFileType.HAC4TUR);

        // check the first line to see if we're really dealing with a HAC4 TUR file
        String strVersion = fileContents[FilePosition.VERSION_HEADER];
        if (strVersion == null || !strVersion.equals(VERSION_HEADER_STRING)) {
            throw new EVException("Failed to read HAC4 TUR File. Can't find correct header in file");
        }
        exercise.setDeviceName(strVersion);

        // get the nr of lines there are in the note. This is important,
        // as this decides were all the following data can be found.
        nrOfLinesInNote = readInteger(FilePosition.NR_OF_LINES_IN_NOTE);
        // set recording mode. For now, just set it to always having recorded
        // speed, cadence, altitude, but not Power.
        RecordingMode recMode = new RecordingMode();
        exercise.setRecordingMode(recMode);
        recMode.setSpeed(readFloat(FilePosition.AVERAGE_SPEED + nrOfLinesInNote) > 0.0);
        recMode.setAltitude(true);
        recMode.setPower(false);
        recMode.setTemperature(true);

        // get date and time
        String strDateAndTime = readLine(FilePosition.START_DATE) + "-" + readLine(FilePosition.START_TIME);
        try {
            exercise.setDateTime(LocalDateTime.parse(strDateAndTime, DateTimeFormatter.ofPattern("dd.MM.yyy-HH:mm")));
        } catch (Exception e) {
            throw new EVException("Failed to read exercise date and time from string '" + strDateAndTime + "'...", e);
        }

        // get duration of exercise. The duration is recorded in seconds, so this one is easy.
        exercise.setDuration(readInteger(FilePosition.DURATION + nrOfLinesInNote) * 10);

        // get average heart rate
        exercise.setHeartRateAVG((short) Math.round(readFloat(FilePosition.AVERAGE_HEART_RATE + nrOfLinesInNote)));

        // odometer
        exercise.setOdometer(readInteger(FilePosition.ODOMETER + nrOfLinesInNote));
        // total exercise time, the total exercise time is recorded in seconds, so this needs to be calculated to minutes.
        exercise.setSumExerciseTime(readInteger(FilePosition.TOTAL_EXERCISE_TIME + nrOfLinesInNote) / 60);

        // set recording interval
        sampleInterval = readSampleInterval(FilePosition.BEGIN_SAMPLES + nrOfLinesInNote);
        exercise.setRecordingInterval((short) sampleInterval);

        // read all samples
        exercise.setSampleList(readSamples(
                FilePosition.NR_SAMPLES + nrOfLinesInNote,
                FilePosition.BEGIN_SAMPLES + nrOfLinesInNote));

        // now that we have the samples, other values can be calculated.
        exercise.setAltitude(calculateAltitudes(exercise));
        exercise.setCadence(calculateCadence(exercise));
        recMode.setCadence(exercise.getCadence().getCadenceMax() > 0);
        exercise.setSpeed(calculateSpeed(exercise));
        exercise.setTemperature(calculateTemperature(exercise));

        // calculate heartrate limits (only one available in HAC files)
        exercise.getHeartRateLimits().add(calculateHeartRate(exercise));

        // get lap data
        exercise.setLapList(getLaps(exercise));

        // we're done :-)
        return exercise;
    }

    /**
     * Calculate the altitude information.
     */
    private ExerciseAltitude calculateAltitudes(EVExercise exercise) {

        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        int ascent = 0;
        long total = 0;
        short prevAlt = Short.MAX_VALUE;

        for (ExerciseSample sample : exercise.getSampleList()) {
            short alt = sample.getAltitude();

            // update values
            min = Math.min(alt, min);
            max = Math.max(alt, max);
            if (alt > prevAlt) {
                ascent += (alt - prevAlt);
            }
            prevAlt = alt;
            total += alt;
        }

        short altitudeAVG = (short) (total / exercise.getSampleList().size());
        return new ExerciseAltitude((short) min, altitudeAVG, (short) max, ascent);
    }

    /**
     * Calculates the cadence information.
     */
    private ExerciseCadence calculateCadence(EVExercise exercise) {
        int cadenceMax = Integer.MIN_VALUE;
        long total = 0;

        for (ExerciseSample sample : exercise.getSampleList()) {
            short cadence = sample.getCadence();
            cadenceMax = Math.max(cadence, cadenceMax);
            total += cadence;
        }

        short cadenceAvg = (short) (total / exercise.getSampleList().size());
        return new ExerciseCadence(cadenceAvg, (short) cadenceMax);
    }

    /**
     * Calculates the speed information.
     */
    private ExerciseSpeed calculateSpeed(EVExercise exercise) {

        float max = Float.MIN_VALUE;
        int nrMovingIntervals = 0; // nr of intervals bike was moving
        float speedAVG = 0.0f;
        int previousDistance = 0;

        for (ExerciseSample sample : exercise.getSampleList()) {
            if (sample.getDistance() > previousDistance) {
                nrMovingIntervals++;
                speedAVG = speedAVG + ((sample.getSpeed() - speedAVG) / nrMovingIntervals);
            }
            max = Math.max(max, sample.getSpeed());
            previousDistance = sample.getDistance();
        }

        int sampleCount = exercise.getSampleList().size();
        int distance = exercise.getSampleList().get(sampleCount - 1).getDistance();
        return new ExerciseSpeed(speedAVG, max, distance);
    }

    /**
     * Calculates the temperature information.
     */
    private ExerciseTemperature calculateTemperature(EVExercise exercise) {
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        long total = 0;

        for (ExerciseSample sample : exercise.getSampleList()) {
            min = Math.min(min, sample.getTemperature());
            max = Math.max(max, sample.getTemperature());
            total += sample.getTemperature();
        }

        short avg = (short) (total / exercise.getSampleList().size());
        return new ExerciseTemperature((short) min, avg, (short) max);
    }

    /**
     * Calculates the heartrate range information.
     */
    private HeartRateLimit calculateHeartRate(EVExercise exercise) throws EVException {

        short upper = (short) readInteger(FilePosition.HR_LIMIT_UPPER + nrOfLinesInNote);
        short lower = (short) readInteger(FilePosition.HR_LIMIT_LOWER + nrOfLinesInNote);
        int max = Integer.MIN_VALUE;

        int intervalsBelow = 0;
        int intervalsBetween = 0;
        int intervalsAbove = 0;

        for (ExerciseSample sample : exercise.getSampleList()) {
            short hr = sample.getHeartRate();
            max = Math.max(max, hr);

            if (hr < lower) {
                intervalsBelow += 1;
            } else if (hr >= lower && hr <= upper) {
                intervalsBetween += 1;
            } else {// above upper
                intervalsAbove += 1;
            }
        }
        exercise.setHeartRateMax((short) max);

        return new HeartRateLimit(lower, upper,
                intervalsBelow * sampleInterval,
                intervalsBetween * sampleInterval,
                intervalsAbove * sampleInterval,
                true);
    }

    /**
     * Gets the laps from the file. As I don't have any tur files with lap info,
     * I cannot do anything with this yet. Maybe later.. For now I'll just fill
     * a Lap object with values from the complete exercise.
     */
    private List<Lap> getLaps(EVExercise exercise) {
        ExerciseSample lastSample = exercise.getSampleList().get(exercise.getSampleList().size() - 1);

        Lap lap = new Lap();
        lap.setTimeSplit(exercise.getDuration());
        lap.setHeartRateSplit(lastSample.getHeartRate());
        lap.setHeartRateMax(exercise.getHeartRateMax());
        lap.setHeartRateAVG(exercise.getHeartRateAVG());

        lap.setAltitude(new LapAltitude(lastSample.getAltitude(), exercise.getAltitude().getAscent()));

        lap.setSpeed(new LapSpeed(
                lastSample.getSpeed(),
                exercise.getSpeed().getSpeedAvg(),
                exercise.getSpeed().getDistance(),
                lastSample.getCadence()));

        lap.setTemperature(new LapTemperature(lastSample.getTemperature()));

        return Arrays.asList(lap);
    }

    /**
     * Returns the sample interval in seconds.
     *
     * @param fpBeginSamples file position at which the sample data begins
     */
    private int readSampleInterval(int fpBeginSamples) throws EVException {

        // find length of all strings to this point
        int lengthUntilSamples = 0;
        for (int i = 0; i < fpBeginSamples; i++)
            lengthUntilSamples += fileContents[i].length() + 1;

        // start reading samples
        int[] firstSampleBytes = Arrays.copyOfRange(fileContentsBytes, lengthUntilSamples, lengthUntilSamples + 20);
        int[] secondSampleBytes = Arrays.copyOfRange(fileContentsBytes, lengthUntilSamples + 20, lengthUntilSamples + 40);
        Sample firstSample = new Sample(firstSampleBytes);
        Sample secondSample = new Sample(secondSampleBytes);
        return (int) (secondSample.getTime() - firstSample.getTime());
    }

    /**
     * Reads the raw sample data. This uses the private inner Sample class for
     * temporary storing the samples. It parses the number of samples and the sample
     * data from the specified file positions.
     *
     * @param fpNrSamples file position for the number of samples
     * @param fpBeginSamples file position for the begin of sample data
     * @return the created list of exercise samples
     */
    private List<ExerciseSample> readSamples(int fpNrSamples, int fpBeginSamples) throws EVException {

        int nrSamples = readInteger(fpNrSamples);
        // find length of all strings to this point
        int lengthUntilSamples = 0;
        for (int i = 0; i < fpBeginSamples; i++) {
            lengthUntilSamples += fileContents[i].length() + 1;
        }

        // start reading samples
        List<Sample> samples = new ArrayList<>();
        for (int i = 0; i < nrSamples; i++) {
            int startFrom = lengthUntilSamples + (i * 20);
            int[] sampleBytes = Arrays.copyOfRange(fileContentsBytes, startFrom, startFrom + 20);
            samples.add(new Sample(sampleBytes));
        }

        List<ExerciseSample> eSamples = new ArrayList<>();
        int previousDistance = 0;
        long firstTimestamp = nrSamples > 0 ? samples.get(0).getTime() : 0;

        for (int i = 0; i < nrSamples; i++) {
            Sample sample = samples.get(i);
            ExerciseSample eSample = new ExerciseSample();
            eSamples.add(eSample);
            eSample.setTimestamp((sample.getTime() - firstTimestamp) * 1000L);
            eSample.setHeartRate((short) sample.getHeartRate());
            eSample.setAltitude((short) sample.getAltitude());
            eSample.setCadence((short) sample.getCadence());
            int distanceDiff = sample.getDistance() - previousDistance;
            previousDistance = sample.getDistance();
            eSample.setDistance(sample.getDistance() * 10);
            eSample.setSpeed(((float) distanceDiff / (float) sampleInterval) * (float) 3.6 * 10f);
            eSample.setTemperature((short) sample.getTemperature());
        }

        return eSamples;
    }

    /**
     * Read a float from the file contents.
     */
    private float readFloat(int pos) throws EVException {
        try {
            return Float.parseFloat(readLine(pos));
        } catch (Exception e) {
            throw new EVException("Invalid value for float at position " + pos, e);
        }
    }

    /**
     * Read an integer from the file contents.
     */
    private int readInteger(int pos) throws EVException {
        try {
            return Integer.parseInt(readLine(pos));
        } catch (Exception e) {
            throw new EVException("Invalid value for integer at position " + pos, e);
        }
    }

    /**
     * Read a line from the file contents.
     */
    private String readLine(int pos) {
        return fileContents[pos];
    }
}
