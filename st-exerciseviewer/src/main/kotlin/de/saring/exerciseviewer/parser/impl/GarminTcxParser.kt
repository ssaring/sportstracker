package de.saring.exerciseviewer.parser.impl

import de.saring.exerciseviewer.core.EVException
import de.saring.exerciseviewer.data.EVExercise
import de.saring.exerciseviewer.data.ExerciseAltitude
import de.saring.exerciseviewer.data.ExerciseCadence
import de.saring.exerciseviewer.data.ExerciseSample
import de.saring.exerciseviewer.data.ExerciseSpeed
import de.saring.exerciseviewer.data.Lap
import de.saring.exerciseviewer.data.LapAltitude
import de.saring.exerciseviewer.data.LapSpeed
import de.saring.exerciseviewer.data.Position
import de.saring.exerciseviewer.data.RecordingMode
import de.saring.exerciseviewer.parser.AbstractExerciseParser
import de.saring.exerciseviewer.parser.ExerciseParserInfo
import de.saring.util.Date310Utils
import de.saring.util.unitcalc.CalculationUtils
import org.jdom2.Element
import org.jdom2.Namespace
import org.jdom2.input.SAXBuilder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * ExerciseParser implementation for reading Garmin TCX v2 exercise files (XML-based). Documentation about the format
 * can be found at the Garmin website ( http://developer.garmin.com/schemas/tcx/v2/ ).
 *
 * @author Stefan Saring
 */
class GarminTcxParser : AbstractExerciseParser() {

    private val namespace = Namespace.getNamespace("http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2")
    private val namespaceExt = Namespace.getNamespace("ext", "http://www.garmin.com/xmlschemas/ActivityExtension/v2")

    override
    val info = ExerciseParserInfo("Garmin TCX", listOf("tcx", "TCX"))

    override
    fun parseExercise(filename: String): EVExercise {

        try {
            // create a File of the filename string to avoid URL problems when filename contains spaces or umlauts
            val document = SAXBuilder().build(java.io.File(filename))
            return parseExerciseElement(document.rootElement)
        }
        catch (e: Exception) {
            throw EVException("Failed to read the Garmin TCX exercise file '$filename'!", e)
        }
    }

    /**
     * Parses the exercise data from the specified element (root element).
     *
     * @param eExercise exercise DOM element
     */
    private fun parseExerciseElement(eExercise: Element): EVExercise {

        // parse basic exercise data
        val exercise = EVExercise(EVExercise.ExerciseFileType.GARMIN_TCX)
        exercise.recordingInterval = EVExercise.DYNAMIC_RECORDING_INTERVAL
        exercise.recordingMode = RecordingMode()
        exercise.recordingMode.isSpeed = true
        exercise.duration = 0
        exercise.speed = ExerciseSpeed(0f, 0f, 0)
        exercise.energy = 0

        val eActivity = eExercise.getChild("Activities", namespace).getChild("Activity", namespace)
        val exerciseDateTime = parseDateTime(eActivity.getChildText("Id", namespace))
        exercise.dateTime = exerciseDateTime

        var trackpointCount: Int = 0
        var altitudeMetersTotal: Double = 0.0

        var cadenceCount: Int = 0
        var cadenceSum: Long = 0

        var totalTimeGapBetweenLaps: Long = 0
        var lastTrackpointTimestamp: Long = 0

        // no summary data, everything is stored in laps
        // parse each lap and create a ExerciseViewer Lap object
        for (eLap in eActivity.getChildren("Lap", namespace)) {
            val evLap = parseLapData(exercise, eLap)
            exercise.lapList.add(evLap)

            // compute the total time gap between all laps
            if (lastTrackpointTimestamp > 0) {
                val lapStart = parseDateTime(eLap.getAttributeValue("StartTime"))
                val lapStartMillis = Date310Utils.getMilliseconds(lapStart)
                totalTimeGapBetweenLaps += lapStartMillis - lastTrackpointTimestamp
            }

            var lapAltitude: Short? = null
            var lapAscentMeters: Double = 0.0
            var previousTrackpointTimestamp = Long.MIN_VALUE
            var previousTrackpointDistanceMeters = Double.MIN_VALUE
            var previousTrackpointAltitudeMeters = Double.MIN_VALUE
            val exerciseDateTimeMillis = Date310Utils.getMilliseconds(exerciseDateTime)

            // parse all Track elements
            for (eTrack in eLap.getChildren("Track", namespace)) {

                // parse all Trackpoint elements (= ExerciseSamples)
                for (eTrackpoint in eTrack.getChildren("Trackpoint", namespace)) {
                    trackpointCount++

                    val evSample = ExerciseSample()
                    exercise.sampleList.add(evSample)

                    // calculate sample timestamp (time gap between laps must be substracted here)
                    val tpDateTime = parseDateTime(eTrackpoint.getChildText("Time", namespace))
                    val tpMillis = Date310Utils.getMilliseconds(tpDateTime)
                    lastTrackpointTimestamp = tpMillis

                    evSample.timestamp = tpMillis - exerciseDateTimeMillis - totalTimeGapBetweenLaps

                    parseTrackpointPositionData(exercise, evSample, eTrackpoint)
                    parseTrackpointHeartRateData(exercise, evLap, evSample, eTrackpoint)

                    // get distance data (some trackpoints might not have distance data!)
                    val strDistanceMeters = eTrackpoint.getChildText("DistanceMeters", namespace)
                    if (strDistanceMeters != null) {
                        val tpDistanceMeters = strDistanceMeters.toDouble()
                        evSample.distance = Math.round(tpDistanceMeters).toInt()

                        // calculate speed between current and previous trackpoint
                        evSample.speed = 0f
                        if (previousTrackpointTimestamp > Long.MIN_VALUE) {
                            val tpTimestampDiff = tpMillis - previousTrackpointTimestamp
                            // sometimes computed difference is < 0 => impossible, use 0 instead
                            val tpDistanceDiff = Math.max(tpDistanceMeters - previousTrackpointDistanceMeters, 0.0)

                            evSample.speed = CalculationUtils.calculateAvgSpeed(
                                    tpDistanceDiff / 1000.0, Math.round(tpTimestampDiff / 1000.0).toInt()).toFloat()
                        }
                        previousTrackpointTimestamp = tpMillis
                        previousTrackpointDistanceMeters = tpDistanceMeters

                        evLap.speed!!.speedEnd = evSample.speed ?: 0f
                        exercise.speed!!.speedMax = Math.max(evSample.speed ?: 0f, exercise.speed!!.speedMax)
                    }

                    // get optional altitude data
                    val strAltitudeMeters = eTrackpoint.getChildText("AltitudeMeters", namespace)
                    if (strAltitudeMeters != null) {
                        val tpAltitude = strAltitudeMeters.toDouble()
                        evSample.altitude = Math.round(tpAltitude).toShort()
                        altitudeMetersTotal += Math.round(tpAltitude)

                        // create altitude objects for exercise and current lap if not done yet
                        if (exercise.altitude == null) {
                            exercise.recordingMode.isAltitude = true
                            exercise.altitude = ExerciseAltitude(
                                    altitudeMin = Short.MAX_VALUE,
                                    altitudeAvg = Math.round(altitudeMetersTotal / trackpointCount).toShort(),
                                    altitudeMax = Short.MIN_VALUE,
                                    ascent = 0,
                                    descent = 0)
                        }

                        lapAltitude = Math.round(tpAltitude).toShort()

                        exercise.altitude!!.altitudeMin = Math.min(tpAltitude.toInt(), exercise.altitude!!.altitudeMin.toInt()).toShort()
                        exercise.altitude!!.altitudeMax = Math.max(tpAltitude.toInt(), exercise.altitude!!.altitudeMax.toInt()).toShort()

                        // calculate lap ascent (need to use double precision here)
                        if (previousTrackpointAltitudeMeters > Double.MIN_VALUE &&
                                tpAltitude > previousTrackpointAltitudeMeters) {
                            val tpAscent = tpAltitude - previousTrackpointAltitudeMeters
                            lapAscentMeters += tpAscent
                        }
                        previousTrackpointAltitudeMeters = tpAltitude
                    }

                    // get optional cadence data
                    val cadence = getCadenceOfTrackpoint(eTrackpoint)
                    if (cadence != null) {
                        evSample.cadence = cadence
                        evLap.speed!!.cadence = evSample.cadence ?: 0

                        // create cadence object for exercise if not done yet
                        if (exercise.cadence == null) {
                            exercise.cadence = ExerciseCadence(0, 0)
                            exercise.recordingMode.isCadence = true
                        }

                        // compute max and average cadence if present
                        exercise.cadence!!.cadenceMax = Math.max(cadence.toInt(), exercise.cadence!!.cadenceMax.toInt()).toShort()
                        if (cadence > 0) {
                            cadenceSum += cadence
                            exercise.cadence!!.cadenceAvg = Math.round(cadenceSum / (++cadenceCount).toDouble()).toShort()
                        }
                    }
                }
            }

            if (lapAltitude != null) {
                evLap.altitude = LapAltitude(lapAltitude, Math.round(lapAscentMeters).toInt(), 0)
            }

            // store position of last sample as lap split position
            if (!exercise.sampleList.isEmpty()) {
                evLap.positionSplit = exercise.sampleList.last().position
            }
        }

        // parse device model name, it's always an Garmin
        exercise.deviceName = "Garmin ${eActivity.getChild("Creator", namespace).getChildText("Name", namespace)}"

        calculateAvgSpeed(exercise)
        calculateAvgHeartrate(exercise)
        calculateAvgAltitude(exercise, altitudeMetersTotal, trackpointCount)

        return exercise
    }

    private fun parseLapData(exercise: EVExercise, lapElement: Element): Lap {
        val evLap = Lap()

        // stored lap duration in XML is often wrong, needs to be calculated
        val lapDurationSeconds = calculateLapDuration(lapElement)
        val distanceMeters = lapElement.getChildText("DistanceMeters", namespace).toDouble()
        exercise.duration = exercise.duration!! + Math.round(lapDurationSeconds * 10).toInt()
        evLap.timeSplit = exercise.duration!!
        exercise.speed!!.distance += Math.round(distanceMeters).toInt()
        val lapSpeedDistance = exercise.speed!!.distance
        exercise.energy = exercise.energy!! + lapElement.getChildText("Calories", namespace).toInt()

        // stored maximum lap speed in XML is wrong, will be calculated

        // calculate average speed of lap
        val lapSpeedAVG = CalculationUtils.calculateAvgSpeed(
                distanceMeters / 1000.0,
                Math.round(lapDurationSeconds).toInt()).toFloat()

        evLap.speed = LapSpeed(0f, lapSpeedAVG, lapSpeedDistance)

        // parse optional heartrate data of lap
        parseLapHeartRateData(exercise, evLap, lapElement)
        return evLap
    }

    private fun parseLapHeartRateData(exercise: EVExercise, evLap: Lap, lapElement: Element) {
        val eAverageHeartRateBpm = lapElement.getChild("AverageHeartRateBpm", namespace)
        if (eAverageHeartRateBpm != null) {
            evLap.heartRateAVG = eAverageHeartRateBpm.getChildText("Value", namespace).toShort()
        }

        val eMaximumHeartRateBpm = lapElement.getChild("MaximumHeartRateBpm", namespace)
        if (eMaximumHeartRateBpm != null) {
            evLap.heartRateMax = eMaximumHeartRateBpm.getChildText("Value", namespace).toShort()
            exercise.heartRateMax = Math.max(evLap.heartRateMax!!.toInt(), exercise.heartRateMax?.toInt() ?: 0).toShort()
        }
    }

    private fun parseTrackpointPositionData(exercise: EVExercise, evSample: ExerciseSample, tpElement: Element) {
        val ePosition = tpElement.getChild("Position", namespace)
        if (ePosition != null) {
            exercise.recordingMode.isLocation = true
            val latitude = ePosition.getChildText("LatitudeDegrees", namespace).toDouble()
            val longitude = ePosition.getChildText("LongitudeDegrees", namespace).toDouble()
            evSample.position = Position(latitude, longitude)
        }
    }

    private fun parseTrackpointHeartRateData(evExercise: EVExercise, evLap: Lap, evSample: ExerciseSample, tpElement: Element) {
        val eHeartRateBpm = tpElement.getChild("HeartRateBpm", namespace)
        if (eHeartRateBpm != null) {
            val heartRate = eHeartRateBpm.getChildText("Value", namespace).toShort()
            evSample.heartRate = heartRate
            evLap.heartRateSplit = heartRate
            evExercise.recordingMode.isHeartRate = true
        }
    }

    /**
     * Calculates the duration of the specified lap in seconds:
     * "Last TrackPoint of Lap".Time - Lap.StartTime
     */
    private fun calculateLapDuration(lapElement: Element): Double {
        val lastTrack = lapElement.getChildren("Track", namespace).last()
        var lastTrackpoint = lastTrack.getChildren("Trackpoint", namespace).last()

        val lapStart = parseDateTime(lapElement.getAttributeValue("StartTime"))
        val lastTp = parseDateTime(lastTrackpoint.getChildText("Time", namespace))
        val lapStartMillis = Date310Utils.getMilliseconds(lapStart)
        val lastTpMillis = Date310Utils.getMilliseconds(lastTp)
        return (lastTpMillis - lapStartMillis) / 1000.0
    }

    private fun calculateAvgSpeed(exercise: EVExercise) {
        exercise.speed!!.speedAvg = CalculationUtils.calculateAvgSpeed(
                exercise.speed!!.distance / 1000.0, Math.round(exercise.duration!! / 10f)).toFloat()
    }

    /**
     * Returns the optional cadence value if present. It looks first for the cycling cadence. If not found it looks for
     * the run cadence extension.
     *
     * @param eTrackpoint element to check
     * @return cadence or null
     */
    private fun getCadenceOfTrackpoint(eTrackpoint: Element): Short? {

        val strCadence = eTrackpoint.getChildText("Cadence", namespace)
        if (strCadence != null) {
            return strCadence.toShort()
        }

        val strRunCadence = eTrackpoint.getChild("Extensions", namespace)
                ?.getChild("TPX", namespaceExt)
                ?.getChildText("RunCadence", namespaceExt)
        if (strRunCadence != null) {
            return strRunCadence.toShort()
        }

        return null
    }

    /**
     * Calculates the average heartrate for the specified exercise (if available). It's computed
     * as the average of all laps average heartrates. Laps without heartrate data will be ignored.
     */
    private fun calculateAvgHeartrate(exercise: EVExercise) {
        var totalHeartRateSum: Long = 0
        var previousLapTimeSplit: Int = 0
        var totalHeartRateDuration: Int = 0

        for (evLap in exercise.lapList) {
            val lapDuration = evLap.timeSplit - previousLapTimeSplit
            previousLapTimeSplit = evLap.timeSplit

            if (evLap.heartRateAVG != null) {
                totalHeartRateDuration += lapDuration
                totalHeartRateSum += evLap.heartRateAVG!! * lapDuration
            }
        }

        if (totalHeartRateSum > 0) {
            exercise.heartRateAVG = Math.round(totalHeartRateSum / totalHeartRateDuration.toDouble()).toShort()
        }
    }

    private fun calculateAvgAltitude(exercise: EVExercise, altitudeMetersTotal: Double, trackpointCount: Int) {
        // calculate average altitude and total ascent (if recorded)
        if (exercise.altitude != null) {
            exercise.altitude!!.altitudeAvg = Math.round(altitudeMetersTotal / trackpointCount.toDouble()).toShort()

            for (evLap in exercise.lapList) {
                if (evLap.altitude != null) {
                    exercise.altitude!!.ascent += evLap.altitude!!.ascent
                }
            }
        }
    }

    /**
     * Parses the date time in ISO format specified in the passed text and returns the appropriate LocalDateTime.
     */
    private fun parseDateTime(dateTimeText: String): LocalDateTime {
        // remove the suffix 'Z' if contained in the passed text, can't be ignored by ISO_LOCAL_DATE_TIME
        val dateTimeTextFixed = if (dateTimeText.endsWith('Z'))
            dateTimeText.substring(0, dateTimeText.length - 1)
        else
            dateTimeText
        return LocalDateTime.parse(dateTimeTextFixed, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }
}
