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
 * ExerciseParser implementation for reading Garmin TCX v2 exercise files (XML-based).
 * Documentation about the format can be found at the Garmin website
 * ( http://developer.garmin.com/schemas/tcx/v2/ ).
 *
 * @author Stefan Saring
 * @version 1.0
 */
class GarminTcxParser extends AbstractExerciseParser {

    /** Informations about this parser. */
    private def info = new ExerciseParserInfo('Garmin TCX', ["tcx", "TCX"] as String[])

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
            def path = new XmlSlurper().parse(new File(filename))
            return parseExercisePath(path)
        }
        catch (Exception e) {
            throw new EVException("Failed to read the Garmin TCX exercise file '${filename}' ...", e)
        }
    }

    /**
     * Parses the exercise data from the specified path (root element).
     */
    private EVExercise parseExercisePath(path) {

        // parse basic exercise data
        EVExercise exercise = new EVExercise()
        exercise.fileType = EVExercise.ExerciseFileType.GARMIN_TCX
        exercise.recordingInterval = EVExercise.DYNAMIC_RECORDING_INTERVAL
        exercise.recordingMode = new RecordingMode()
        exercise.recordingMode.speed = true
        exercise.speed = new ExerciseSpeed()

        def activity = path.Activities.Activity
        exercise.dateTime = parseDateTime(activity.Id.text())
        int trackpointCount = 0

        double altitudeMetersTotal = 0

        int cadenceCount = 0
        long cadenceSum = 0

        int totalTimeGapBetweenLaps = 0
        long lastTrackpointTimestamp = 0

        // no summary data, everything is stored in laps
        // parse each lap and create a ExerciseViewer Lap object
        def evLaps = []
        def evSamples = []

        for (lap in activity.Lap) {
            def evLap = parseLapData(exercise, lap)
            evLaps << evLap

            // compute the total time gap between all laps
            if (lastTrackpointTimestamp > 0) {
                LocalDateTime lapStart = parseDateTime(lap.@StartTime.text())
                long lapStartMillis = Date310Utils.getMilliseconds(lapStart)
                totalTimeGapBetweenLaps += lapStartMillis - lastTrackpointTimestamp
            }

            double lapAscentMeters = 0
            long previousTrackpointTimestamp = Long.MIN_VALUE
            double previousTrackpointDistanceMeters = Double.MIN_VALUE
            double previousTrackpointAltitudeMeters = Double.MIN_VALUE
            long exerciseDateTimeMillis = Date310Utils.getMilliseconds(exercise.dateTime)

            // parse all Track elements
            for (track in lap.Track) {

                // parse all Trackpoint elements (= ExerciseSamples)
                for (trackpoint in track.Trackpoint) {
                    trackpointCount++

                    def evSample = new ExerciseSample()
                    evSamples << evSample

                    // calculate sample timestamp (time gap between laps must be substracted here)
                    LocalDateTime tpDateTime = parseDateTime(trackpoint.Time.text())
                    long tpMillis = Date310Utils.getMilliseconds(tpDateTime)
                    lastTrackpointTimestamp = tpMillis

                    evSample.timestamp = tpMillis - exerciseDateTimeMillis - totalTimeGapBetweenLaps

                    parseTrackpointPositionData(exercise, evSample, trackpoint)
                    parseTrackpointHeartRateData(evLap, evSample, trackpoint)

                    // get distance data (some trackpoints might not have distance data!)                    
                    if (!trackpoint.DistanceMeters.isEmpty()) {
                        double tpDistanceMeters = trackpoint.DistanceMeters.toDouble()
                        evSample.distance = Math.round(tpDistanceMeters)

                        // calculate speed between current and previous trackpoint
                        evSample.speed = 0
                        if (previousTrackpointTimestamp > Long.MIN_VALUE) {
                            long tpTimestampDiff = tpMillis - previousTrackpointTimestamp
                            // sometimes computed difference is < 0 => impossible, use 0 instead
                            double tpDistanceDiff = Math.max(tpDistanceMeters - previousTrackpointDistanceMeters, 0d)

                            evSample.speed = CalculationUtils.calculateAvgSpeed(
                                    (float) (tpDistanceDiff / 1000f), (int) Math.round(tpTimestampDiff / 1000f))
                        }
                        previousTrackpointTimestamp = tpMillis
                        previousTrackpointDistanceMeters = tpDistanceMeters

                        evLap.speed.speedEnd = evSample.speed
                        exercise.speed.speedMax = Math.max(evSample.speed, exercise.speed.speedMax)
                    }

                    // get optional altitude data
                    if (!trackpoint.AltitudeMeters.isEmpty()) {
                        double tpAltitude = trackpoint.AltitudeMeters.toDouble()
                        evSample.altitude = Math.round(tpAltitude)
                        altitudeMetersTotal += Math.round(tpAltitude)

                        // create altitude objects for exercise and current lap if not done yet
                        if (exercise.altitude == null) {
                            exercise.altitude = new ExerciseAltitude()
                            exercise.recordingMode.altitude = true

                            exercise.altitude.altitudeMin = Short.MAX_VALUE
                            exercise.altitude.altitudeMax = Short.MIN_VALUE
                            exercise.altitude.altitudeAVG = Math.round(altitudeMetersTotal / trackpointCount)
                            exercise.altitude.ascent = 0
                        }

                        if (evLap.altitude == null) {
                            evLap.altitude = new LapAltitude()
                        }
                        evLap.altitude.altitude = Math.round(tpAltitude)

                        exercise.altitude.altitudeMin = Math.min(tpAltitude, exercise.altitude.altitudeMin)
                        exercise.altitude.altitudeMax = Math.max(tpAltitude, exercise.altitude.altitudeMax)

                        // calculate lap ascent (need to use double precision here)
                        if (previousTrackpointAltitudeMeters > Double.MIN_VALUE &&
                                tpAltitude > previousTrackpointAltitudeMeters) {
                            double tpAscent = tpAltitude - previousTrackpointAltitudeMeters
                            lapAscentMeters += tpAscent
                            evLap.altitude.ascent = Math.round(lapAscentMeters)
                        }
                        previousTrackpointAltitudeMeters = tpAltitude
                    }

                    // get optional cadence data
                    if (!trackpoint.Cadence.isEmpty()) {
                        evSample.cadence = trackpoint.Cadence.toInteger()
                        evLap.speed.cadence = evSample.cadence

                        // create cadence object for exercise if not done yet
                        if (exercise.cadence == null) {
                            exercise.cadence = new ExerciseCadence()
                            exercise.recordingMode.cadence = true
                        }

                        // compute max and average cadence
                        exercise.cadence.cadenceMax = Math.max(evSample.cadence, exercise.cadence.cadenceMax)
                        if (evSample.cadence > 0) {
                            cadenceSum += evSample.cadence
                            exercise.cadence.cadenceAVG = Math.round(cadenceSum / ++cadenceCount)
                        }
                    }
                }
            }

            // store position of last sample as lap split position
            if (!evSamples.isEmpty()) {
                evLap.positionSplit = evSamples.last().position
            }
        }

        // parse device model name, it's always an Garmin
        exercise.deviceName = "Garmin $activity.Creator.Name"

        exercise.lapList = evLaps as Lap[]
        exercise.sampleList = evSamples as ExerciseSample[]

        calculateAvgSpeed(exercise)
        calculateAvgHeartrate(exercise)
        calculateAvgAltitude(exercise, altitudeMetersTotal, trackpointCount)
        exercise
    }

    def parseLapData(exercise, lapElement) {
        def evLap = new Lap()
        evLap.speed = new LapSpeed()

        // stored lap duration in XML is often wrong, needs to be calculated
        double lapDurationSeconds = calculateLapDuration(lapElement)
        double distanceMeters = lapElement.DistanceMeters.toDouble()
        exercise.duration += Math.round(lapDurationSeconds * 10)
        evLap.timeSplit = exercise.duration
        exercise.speed.distance += Math.round(distanceMeters)
        evLap.speed.distance = exercise.speed.distance
        exercise.energy += lapElement.Calories.toInteger()

        // stored maximum lap speed in XML is wrong, will be calculated

        // calculate average speed of lap
        evLap.speed.speedAVG = CalculationUtils.calculateAvgSpeed(
                (float) (distanceMeters / 1000f),
                (int) Math.round(lapDurationSeconds))

        // parse optional heartrate data of lap
        parseLapHeartRateData(exercise, evLap, lapElement)
        evLap
    }

    /**
     * Calculates the duration of the specified lap in seconds: 
     * "Last TrackPoint of Lap".Time - Lap.StartTime   
     */
    def calculateLapDuration(lapElement) {
        def lastTrackpoint = null
        for (track in lapElement.Track) {
            for (trackpoint in track.Trackpoint) {
                lastTrackpoint = trackpoint
            }
        }

        LocalDateTime lapStart = parseDateTime(lapElement.@StartTime.text())
        LocalDateTime lastTp = parseDateTime(lastTrackpoint.Time.text())
        long lapStartMillis = Date310Utils.getMilliseconds(lapStart)
        long lastTpMillis = Date310Utils.getMilliseconds(lastTp)
        (lastTpMillis - lapStartMillis) / 1000
    }

    def parseLapHeartRateData(exercise, evLap, lapElement) {
        if (!lapElement.AverageHeartRateBpm.isEmpty()) {
            evLap.heartRateAVG = lapElement.AverageHeartRateBpm.Value.toInteger()
        }
        if (!lapElement.MaximumHeartRateBpm.isEmpty()) {
            evLap.heartRateMax = lapElement.MaximumHeartRateBpm.Value.toInteger()
            exercise.heartRateMax = Math.max(evLap.heartRateMax, exercise.heartRateMax)
        }
    }

    def parseTrackpointPositionData(exercise, evSample, tpElement) {
        if (!tpElement.Position.isEmpty()) {
            exercise.recordingMode.location = true
            def latitude = tpElement.Position.LatitudeDegrees.toDouble()
            def longitude = tpElement.Position.LongitudeDegrees.toDouble()
            evSample.position = new Position(latitude, longitude)
        }
    }

    def parseTrackpointHeartRateData(evLap, evSample, tpElement) {
        if (!tpElement.HeartRateBpm.isEmpty()) {
            evSample.heartRate = tpElement.HeartRateBpm.Value.toInteger()
            evLap.heartRateSplit = evSample.heartRate
        }
    }

    def calculateAvgSpeed(exercise) {
        exercise.speed.speedAVG = CalculationUtils.calculateAvgSpeed(
                (float) (exercise.speed.distance / 1000f), (int) Math.round(exercise.duration / 10f))
    }

    /**
     * Calculates the average heartrate for the specified exercise (if available). It's computed 
     * as the average of all laps average heartrates. Laps without heartrate data will be ignored.   
     */
    def calculateAvgHeartrate(exercise) {
        long totalHeartRateSum = 0
        int previousLapTimeSplit = 0
        int totalHeartRateDuration = 0

        for (evLap in exercise.lapList) {
            int lapDuration = evLap.timeSplit - previousLapTimeSplit
            previousLapTimeSplit = evLap.timeSplit

            if (evLap.heartRateAVG > 0) {
                totalHeartRateDuration += lapDuration
                totalHeartRateSum += evLap.heartRateAVG * lapDuration
            }
        }

        if (totalHeartRateSum > 0) {
            exercise.heartRateAVG = Math.round(totalHeartRateSum / totalHeartRateDuration)
        }
    }

    def calculateAvgAltitude(exercise, altitudeMetersTotal, trackpointCount) {
        // calculate average altitude and total ascent (if recorded)
        if (exercise.altitude != null) {
            exercise.altitude.altitudeAVG = Math.round(altitudeMetersTotal / trackpointCount)

            for (evLap in exercise.lapList) {
                if (evLap.altitude != null) {
                    exercise.altitude.ascent += evLap.altitude.ascent
                }
            }
        }
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
