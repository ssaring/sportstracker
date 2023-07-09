package de.saring.exerciseviewer.parser.impl

import de.saring.exerciseviewer.core.EVException
import de.saring.exerciseviewer.data.EVExercise
import de.saring.exerciseviewer.data.ExerciseAltitude
import de.saring.exerciseviewer.data.ExerciseSample
import de.saring.exerciseviewer.data.ExerciseSpeed
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
import kotlin.math.roundToInt

/**
 * ExerciseParser implementation for reading TopoGrafix GPX v1.1 exercise files (XML-based). Documentation about the
 * format can be found at the TopoGrafix website ( http://www.topografix.com/gpx.asp ).
 *
 * @author Stefan Saring
 * @author Alex Wulms
 */
class TopoGrafixGpxParser : AbstractExerciseParser() {

    private val degreeToRadianDivider: Double = 57.29577951
    private val earthRadiosInMeter: Double = 6371000.0

    private val namespace = Namespace.getNamespace("http://www.topografix.com/GPX/1/1")
    private val namespaceExt = Namespace.getNamespace("http://www.garmin.com/xmlschemas/TrackPointExtension/v1")

    override
    val info = ExerciseParserInfo("TopoGrafix GPX", listOf("gpx", "GPX"))

    override
    fun parseExercise(filename: String): EVExercise {

        try {
            // create a File of the filename string to avoid URL problems when filename contains spaces or umlauts
            val document = SAXBuilder().build(java.io.File(filename))
            return parseExerciseElement(document.rootElement)
        }
        catch (e: Exception) {
            throw EVException("Failed to read the TopoGrafix GPX exercise file '$filename'!", e)
        }
    }

    /**
     * Parses the exercise data from the specified gpx (root) element.
     */
    private fun parseExerciseElement(eGpx: Element): EVExercise {

        val exercise = createExercise(eGpx)
        exercise.sampleList = parseSampleTrackpoints(eGpx, exercise)
        calculateDistanceAndSpeedPerPoint(exercise)
        exercise.speed = null;
        if (exercise.recordingMode.isAltitude) {
            calculateAltitudeSummary(exercise)
        }
        if (exercise.dateTime != null) {
            calculateDuration(exercise)
        }
        if (exercise.recordingMode.isSpeed) {
            calculateSpeedSummary(exercise)
        }
        if (exercise.recordingMode.isHeartRate) {
            calculateHeartRateSummary(exercise)
        }

        return exercise
    }

    /**
     * Creates the EVExercise with basic exercise data.
     */
    private fun createExercise(eGpx: Element): EVExercise {

        val exercise = EVExercise(EVExercise.ExerciseFileType.GPX)
        exercise.deviceName = "Garmin GPX"
        exercise.recordingInterval = EVExercise.DYNAMIC_RECORDING_INTERVAL
        exercise.recordingMode = RecordingMode()
        exercise.recordingMode.isLocation = true

        // get dateTime and time (optional)
        val strTime = eGpx.getChild("metadata")?.getChildText("time")
        if (strTime != null) {
            exercise.dateTime = parseDateTime(strTime)
        }

        return exercise
    }

    /**
     * Parses all trackpoints in all tracks and track segments under the "gpx" element and returns the exercise samples.
     */
    private fun parseSampleTrackpoints(eGpx: Element, exercise: EVExercise): MutableList<ExerciseSample> {
        val samples = mutableListOf<ExerciseSample>()

        for (eTrk in eGpx.getChildren("trk", namespace)) {
            for (eTrkSeg in eTrk.getChildren("trkseg", namespace)) {
                for (eTrkPt in eTrkSeg.getChildren("trkpt", namespace)) {

                    val sample = ExerciseSample()
                    samples.add(sample)

                    // get position
                    sample.position = Position(eTrkPt.getAttributeValue("lat").toDouble(), eTrkPt.getAttributeValue("lon").toDouble())

                    // get altitude (optional)
                    val strElevation = eTrkPt.getChildText("ele", namespace)
                    if (strElevation != null) {
                        exercise.recordingMode.isAltitude = true
                        sample.altitude = Math.round(strElevation.toDouble()).toShort()
                    }

                    // get timestamp and calculate sample time offset (optional)
                    val strTime = eTrkPt.getChildText("time", namespace)
                    if (strTime != null) {
                        val timestampSample = parseDateTime(strTime)

                        // store first timestamp as exercise start time when missing
                        // or when exercise timestamp larger then (first) track time stamp
                        // (In some GPX files track metadata is missing, while in some other
                        //  GPX file, the time stamp in the meta data is the time the track
                        //  was saved -thus after the exercise- and not the time the track
                        //  was started)
                        if (exercise.dateTime == null || exercise.dateTime!!.isAfter(timestampSample)) {
                            exercise.dateTime = timestampSample
                        }
                        sample.timestamp = Date310Utils.getMilliseconds(timestampSample) -
                                Date310Utils.getMilliseconds(exercise.dateTime!!)
                    }

                    // try to get heartrate in Garmin Oregon format if present
                    var strHeartrate = eTrkPt.getChild("extensions", namespace)?.getChild("TrackPointExtension", namespaceExt)?.getChildText("hr", namespaceExt)
                    // if not present, try to get heartrate in Holux FunTrek 130 pro format if present
                    if (strHeartrate == null) {
                        strHeartrate = eTrkPt.getChild("extensions", namespace)?.getChildText("bpm", namespace)
                    }
                    // if not present, try to get heartrate in GatdetBridge format if present (Android Bridge App)
                    if (strHeartrate == null) {
                        strHeartrate = eTrkPt.getChild("extensions", namespace)?.getChildText("hr", namespace)
                    }

                    if (strHeartrate != null) {
                        exercise.recordingMode.isHeartRate = true
                        sample.heartRate = strHeartrate.toShort()
                    }
                }
            }
        }

        return samples
    }

    /**
     * Calculates the distance and speed for each sample point, based on the GPS coordinates and timestamp.
     * Speed and distance tags do not seem to be part of GPX standard. Some GPS devices do log for example the speed
     * but they don't indicate the unit used, like km/h our mile/hour and as such, those speed data are useless anyway.
     */
    private fun calculateDistanceAndSpeedPerPoint(exercise: EVExercise) {
        var totalDistanceInMeter = 0.0
        var prevPosition: Position? = null
        var prevTimestamp: Long? = null

        for (sample in exercise.sampleList) {
            var distanceInMeter = 0.0
            if (prevPosition != null) {
                // Calculate distance based on GPS coordinates, using haversine formula
                val dLat = (sample.position!!.latitude - prevPosition.latitude) / degreeToRadianDivider
                val dLon = (sample.position!!.longitude - prevPosition.longitude) / degreeToRadianDivider
                val prevLat = prevPosition.latitude / degreeToRadianDivider
                val currLat = sample.position!!.latitude / degreeToRadianDivider
                val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                        Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(prevLat) * Math.cos(currLat)
                distanceInMeter = earthRadiosInMeter * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
                totalDistanceInMeter += distanceInMeter;
            }
            sample.distance = Math.round(totalDistanceInMeter).toInt()
            prevPosition = sample.position

            if (prevTimestamp != null) {
                // Calculate speed. Don't use CalculateUtils.calculateAvgSpeed, because
                // that one gives 'infinity' when rounded time-difference is 0
                // (e.g. when two timestamps are less then 500 milliseconds apart)
                // Note that timestamps are in milliseconds
                // Note that speed is in km/h
                val deltaTime = sample.timestamp!! - prevTimestamp
                // Note that deltaTime can be 0, either when GPX file contains two
                // consecutive points with same timestamp or when it does not contain
                // any timestamps at all. In both cases, speed will be set to 0 for
                // the sample
                if (deltaTime != 0L) {
                    exercise.recordingMode.isSpeed = true
                    sample.speed = (3600 * distanceInMeter / deltaTime).toFloat()
                } else {
                    sample.speed = 0f
                }
            } else {
                // First sample point; speed not known yet. Assume person did not start
                // the training yet and is standing still
                sample.speed = 0f
            }
            prevTimestamp = sample.timestamp
        }

        // speed and distance data in samples will be 0 if no timestamps were available => set them to null
        if (!exercise.recordingMode.isSpeed) {
            exercise.sampleList.forEach {
                it.speed = null
                it.distance = null
            }
        }
    }

    /**
     * Calculates the min, avg and max altitude and the ascent of the exercise.
     */
    private fun calculateAltitudeSummary(exercise: EVExercise) {

        var altitudeMin = Int.MAX_VALUE
        var altitudeMax = Int.MIN_VALUE
        var ascent = 0
        var altitudeSum = 0L

        val samplesWithAltitude = exercise.sampleList.filter { it.altitude != null }.toList()
        var previousAltitude:Short = samplesWithAltitude[0].altitude!!

        for (sample in samplesWithAltitude) {
            val sampleAltitude = sample.altitude!!

            altitudeMin = Math.min(sampleAltitude.toInt(), altitudeMin)
            altitudeMax = Math.max(sampleAltitude.toInt(), altitudeMax)
            altitudeSum += sample.altitude!!

            if (previousAltitude < sampleAltitude) {
                ascent += sampleAltitude - previousAltitude
            }
            previousAltitude = sampleAltitude
        }

        exercise.altitude = ExerciseAltitude(
                altitudeMin = altitudeMin.toShort(),
                altitudeAvg = Math.round(altitudeSum / samplesWithAltitude.size.toDouble()).toShort(),
                altitudeMax = altitudeMax.toShort(),
                ascent = ascent,
                descent = 0)
    }

    /**
     * Calculates the exercise duration (only when samples contain timestamps).
     */
    private fun calculateDuration(exercise: EVExercise) {
        val sampleCount = exercise.sampleList.size

        if (sampleCount > 0) {
            val lastSampleTimestamp = exercise.sampleList[sampleCount - 1].timestamp!!
            if (lastSampleTimestamp > 0) {
                exercise.duration = (lastSampleTimestamp / 100).toInt()
            }
        }
    }

    /**
     * Calculates the speed summary (only when samples contain timestamps, from which speed is derived).
     */
    private fun calculateSpeedSummary(exercise: EVExercise) {
        if (!exercise.sampleList.isEmpty()) {

            val speedMax = exercise.sampleList
                    .map { it.speed!! }
                    .maxOrNull()!!

            val lastSample = exercise.sampleList[exercise.sampleList.size - 1]
            val distance = lastSample.distance!!
            val speedAvg = CalculationUtils.calculateAvgSpeed(
                    distance / 1000.0, Math.round(lastSample.timestamp!! / 1000f)).toFloat()

            exercise.speed = ExerciseSpeed(speedAvg, speedMax, distance)
        }
    }

    /**
     * Calculates heart rate summary data of the exercise (only when samples contain heart rate data).
     */
    private fun calculateHeartRateSummary(exercise: EVExercise) {

        val sampleHeartrates:List<Short> = exercise.sampleList
                .mapNotNull { it.heartRate }

        if (!sampleHeartrates.isEmpty()) {
            exercise.heartRateAVG = sampleHeartrates.average().roundToInt().toShort()
            exercise.heartRateMax = sampleHeartrates.maxOrNull()
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
