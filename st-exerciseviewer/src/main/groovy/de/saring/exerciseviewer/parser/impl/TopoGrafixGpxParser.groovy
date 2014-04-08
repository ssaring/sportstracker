package de.saring.exerciseviewer.parser.impl

import de.saring.exerciseviewer.core.EVException
import de.saring.exerciseviewer.data.*
import de.saring.exerciseviewer.parser.AbstractExerciseParser
import de.saring.exerciseviewer.parser.ExerciseParserInfo
import de.saring.util.Date310Utils
import de.saring.util.unitcalc.CalculationUtils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * ExerciseParser implementation for reading TopoGrafix GPX v1.1 exercise files (XML-based).
 * Documentation about the format can be found at the TopoGrafix website
 * ( http://www.topografix.com/gpx.asp ).
 *
 * @author Stefan Saring
 * @author Alex Wulms
 * @version 2.0
 */
class TopoGrafixGpxParser extends AbstractExerciseParser {

    /** Informations about this parser. */
    private def info = new ExerciseParserInfo('TopoGrafix GPX', ["gpx", "GPX"] as String[])

    private final double DEGREE_TO_RADIAN_DIVIDER = 57.29577951d
    private final double EARTH_RADIUS_IN_METER = 6371000d

    /** {@inheritDoc} */
    @Override
    ExerciseParserInfo getInfo() {
        info
    }

    /** {@inheritDoc} */
    @Override
    EVExercise parseExercise(String filename) throws EVException {

        try {
            // get GPathResult object by using the XmlSlurper parser
            def gpx = new XmlSlurper().parse(new File(filename))
            return parseExercisePath(gpx)
        }
        catch (Exception e) {
            throw new EVException("Failed to read the TopoGrafix GPX exercise file '${filename}' ...", e)
        }
    }

    /**
     * Parses the exercise data from the specified gpx (root) element.
     */
    private EVExercise parseExercisePath(gpx) {

        EVExercise exercise = createExercise(gpx)
        exercise.sampleList = parseSampleTrackpoints(gpx, exercise)
        calculateDistanceAndSpeedPerPoint(exercise)
        exercise.speed = null;
        if (exercise.recordingMode.altitude) {
            calculateAltitudeSummary(exercise)
        }
        if (exercise.dateTime) {
            calculateDuration(exercise)
        }
        if (exercise.recordingMode.speed) {
            calculateSpeedSummary(exercise)
        }
        if (exercise.recordingMode.heartRate) {
            calculateHeartRateSummary(exercise)
        }
        exercise
    }

    /**
     * Creates the EVExercise with basic exercise data.	
     */
    private def createExercise(gpx) {

        EVExercise exercise = new EVExercise()
        exercise.fileType = EVExercise.ExerciseFileType.GPX
        exercise.recordingInterval = EVExercise.DYNAMIC_RECORDING_INTERVAL
        exercise.recordingMode = new RecordingMode()
        exercise.recordingMode.location = true

        exercise.heartRateLimits = new HeartRateLimit[0]
        exercise.lapList = new Lap[0]

        // get dateTime and time (optional)
        if (!gpx.metadata.time.isEmpty()) {
            exercise.dateTime = parseDateTime(gpx.metadata.time.text())
        }

        exercise
    }

    /**
     * Parses all trackpoints in all tracks and track segments under the "gpx" element.
     *
     * @return Array of ExerciseSample objects for each trackpoint
     */
    private def parseSampleTrackpoints(gpx, exercise) {
        def eSamples = []

        gpx.trk.each { trk ->
            trk.trkseg.each { trkseg ->
                trkseg.trkpt.each { trkpt ->

                    def sample = new ExerciseSample()
                    eSamples << sample

                    // get position
                    sample.position = new Position(trkpt.@lat.text().toDouble(), trkpt.@lon.text().toDouble())

                    // get altitude (optional)
                    if (!trkpt.ele.isEmpty()) {
                        exercise.recordingMode.altitude = true
                        sample.altitude = Math.round(trkpt.ele.text().toDouble())
                    }

                    // get timestamp and calculate sample time offset (optional)
                    if (!trkpt.time.isEmpty()) {
                        LocalDateTime timestampSample = parseDateTime(trkpt.time.text())

                        // store first timestamp as exercise start time when missing 
                        // or when exercise timestamp larger then (first) track time stamp
                        // (In some GPX files track metadata is missing, while in some other
                        //  GPX file, the time stamp in the meta data is the time the track
                        //  was saved -thus after the exercise- and not the time the track
                        //  was started)
                        if (!exercise.dateTime || exercise.dateTime.isAfter(timestampSample)) {
                            exercise.dateTime = timestampSample
                        }
                        sample.timestamp = Date310Utils.getMilliseconds(timestampSample) -
                                Date310Utils.getMilliseconds(exercise.dateTime)
                    }

                    // get heartrate in Garmin Oregon format if present
                    if (!trkpt.extensions.TrackPointExtension.hr.isEmpty()) {
                        exercise.recordingMode.heartRate = true
                        sample.heartRate = trkpt.extensions.TrackPointExtension.hr.text().toShort();
                    }

                    // get heartrate in Holux FunTrek 130 pro format if present
                    if (!trkpt.extensions.bpm.isEmpty()) {
                        exercise.recordingMode.heartRate = true
                        sample.heartRate = trkpt.extensions.bpm.text().toShort();
                    }
                }
            }
        }
        eSamples as ExerciseSample[]
    }

    /**
     * Calculates the distance and speed for each sample point,
     * based on the GPS coordinates and timestamp
     *
     * Speed and distance tags do not seem to be part of GPX standard.
     * Some GPS devices do log for example the speed but they don't
     * indicate the unit used, like km/h our mile/hour and as such,
     * those speed data are useless anyway.
     */
    def calculateDistanceAndSpeedPerPoint(exercise) {
        float totalDistanceInMeter = 0f
        Position prevPosition = null
        long prevTimestamp = -1

        exercise.sampleList.each { sample ->
            double distanceInMeter
            if (prevPosition != null) {
                // Calculate distance based on GPS coordinates, using haversine formula
                double dLat = (sample.position.latitude - prevPosition.latitude) / DEGREE_TO_RADIAN_DIVIDER
                double dLon = (sample.position.longitude - prevPosition.longitude) / DEGREE_TO_RADIAN_DIVIDER
                double prevLat = prevPosition.latitude / DEGREE_TO_RADIAN_DIVIDER
                double currLat = sample.position.latitude / DEGREE_TO_RADIAN_DIVIDER
                double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                        Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(prevLat) * Math.cos(currLat)
                distanceInMeter = EARTH_RADIUS_IN_METER * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
                totalDistanceInMeter += distanceInMeter;
            }
            sample.distance = totalDistanceInMeter
            prevPosition = sample.position

            if (prevTimestamp != -1) {
                // Calculate speed. Don't use CalculateUtils.calculateAvgSpeed, because
                // that one gives 'infinity' when rounded time-difference is 0
                // (e.g. when two timestamps are less then 500 milliseconds apart)
                // Note that timestamps are in milliseconds
                // Note that speed is in km/h
                long deltaTime = sample.timestamp - prevTimestamp
                // Note that deltaTime can be 0, either when GPX file contains two
                // consecutive points with same timestamp or when it does not contain
                // any timestamps at all. In both cases, speed will be set to 0 for
                // the sample
                if (deltaTime != 0) {
                    exercise.recordingMode.speed = true
                    sample.speed = 3600 * distanceInMeter / deltaTime
                } else {
                    sample.speed = 0
                }
            } else {
                // First sample point; speed not known yet. Assume person did not start
                // the training yet and is standing still
                sample.speed = 0
            }
            prevTimestamp = sample.timestamp
        }
    }

    /**
     * Calculates the min, avg and max altitude and the ascent of the exercise.
     */
    def calculateAltitudeSummary(exercise) {
        def altitude = new ExerciseAltitude()

        exercise.altitude = altitude
        altitude.altitudeMin = Short.MAX_VALUE
        altitude.altitudeMax = Short.MIN_VALUE

        long altitudeSum = 0
        short previousAltitude = exercise.sampleList[0].altitude

        exercise.sampleList.each { sample ->

            altitude.altitudeMin = Math.min(sample.altitude, altitude.altitudeMin)
            altitude.altitudeMax = Math.max(sample.altitude, altitude.altitudeMax)
            altitudeSum += sample.altitude

            if (previousAltitude < sample.altitude) {
                altitude.ascent += sample.altitude - previousAltitude
            }
            previousAltitude = sample.altitude
        }

        altitude.altitudeAVG = (short) Math.round(altitudeSum / exercise.sampleList.size())
    }

    /**
     * Calculates the exercise duration
     * (only when samples contain timestamps).
     */
    def calculateDuration(exercise) {
        def sampleCount = exercise.sampleList.size()

        if (sampleCount > 0) {
            def lastSampleTimestamp = exercise.sampleList[sampleCount - 1].timestamp
            if (lastSampleTimestamp > 0) {
                exercise.duration = lastSampleTimestamp / 100
            }
        }
    }

    /**
     * Calculates the speed summary
     * (only when samples contain timestamps, from which speed is derived)
     */
    def calculateSpeedSummary(exercise) {
        exercise.speed = new ExerciseSpeed()

        // Determine maximum speed
        exercise.speed.speedMax = 0;
        exercise.sampleList.each { sample ->
            if (sample.speed > exercise.speed.speedMax) {
                exercise.speed.speedMax = sample.speed
            }
        }

        // Determine total duration and average speed
        def sampleCount = exercise.sampleList.size()
        if (sampleCount > 0) {
            def lastSample = exercise.sampleList[sampleCount - 1]
            exercise.speed.distance = Math.round(lastSample.distance)
            exercise.speed.speedAVG = CalculationUtils.calculateAvgSpeed(
                    (float) (exercise.speed.distance / 1000f), (int) Math.round(lastSample.timestamp / 1000f)
            )
        } else {
            exercise.speed.distance = 0;
            exercise.speed.speedAVG = 0;
        }

    }

    /**
     * Calculates heart rate summary data of the exercise 
     * (only when samples contain heart rate data).
     */
    def calculateHeartRateSummary(exercise) {
        long heartRateSum = 0
        exercise.heartRateMax = Short.MIN_VALUE

        exercise.sampleList.each { sample ->
            heartRateSum += sample.heartRate
            if (sample.heartRate > exercise.heartRateMax) {
                exercise.heartRateMax = sample.heartRate
            }
        }

        exercise.heartRateAVG = (short) Math.round(heartRateSum / exercise.sampleList.size())
    }

    /**
     * Parses the date time in ISO format specified in the passed text and returns the appropriate LocalDateTime.
     */
    def parseDateTime(dateTimeText) {
        // remove the suffix 'Z' if contained in the passed text, can't be ignored by ISO_LOCAL_DATE_TIME
        def dateTimeTextFixed = dateTimeText.endsWith('Z') ? dateTimeText[0..-2] : dateTimeText
        LocalDateTime.parse(dateTimeTextFixed, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }
}
