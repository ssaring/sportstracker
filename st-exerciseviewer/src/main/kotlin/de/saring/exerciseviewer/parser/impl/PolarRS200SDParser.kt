package de.saring.exerciseviewer.parser.impl

import de.saring.exerciseviewer.core.EVException
import de.saring.exerciseviewer.data.EVExercise
import de.saring.exerciseviewer.data.ExerciseSpeed
import de.saring.exerciseviewer.data.HeartRateLimit
import de.saring.exerciseviewer.data.Lap
import de.saring.exerciseviewer.data.LapSpeed
import de.saring.exerciseviewer.data.RecordingMode
import de.saring.exerciseviewer.parser.AbstractExerciseParser
import de.saring.exerciseviewer.parser.ExerciseParserInfo
import org.jdom2.Element
import org.jdom2.input.SAXBuilder
import java.time.LocalDateTime

/**
 * This implementation of an ExerciseParser is for reading the XML-based exercise files of the Polar RS200SD devices.
 *
 * @author Stefan Saring (the C# version was done by Jacob Ilsoe Christensen)
 */
class PolarRS200SDParser : AbstractExerciseParser() {

    /** Information about this parser. */
    private val info = ExerciseParserInfo("Polar RS200", arrayOf("xml", "XML"))

    override
    fun getInfo(): ExerciseParserInfo = info

    override
    fun parseExercise(filename: String): EVExercise {

        try {
            val document = SAXBuilder().build(filename)
            return parseExerciseElement(document.rootElement)
        }
        catch (e: Exception) {
            throw EVException("Failed to read the RS200SD exercise file '$filename'!", e)
        }
    }

    /**
     * Parses the exercise data from the specified rs200_session element.
     */
    private fun parseExerciseElement(eSession: Element): EVExercise {

        // parse basic exercise data
        val exercise = EVExercise()
        exercise.fileType = EVExercise.ExerciseFileType.RS200SDRAW
        exercise.deviceName = "Polar RS200"
        exercise.recordingMode = RecordingMode()
        exercise.sampleList = arrayOf()

        val eSessionData = eSession.getChild("session_data")
        exercise.dateTime = LocalDateTime.of(
                eSessionData.getChildText("year").toInt(),
                eSessionData.getChildText("month").toInt(),
                eSessionData.getChildText("day").toInt(),
                eSessionData.getChildText("start_hour").toInt(),
                eSessionData.getChildText("start_minute").toInt(),
                eSessionData.getChildText("start_second").toInt())

        val eSummary = eSessionData.getChild("summary")
        exercise.duration = (eSummary.getChildText("length").toDouble() * 10.0).toInt()
        exercise.heartRateAVG = eSummary.getChildText("avg_hr").toShort()
        exercise.heartRateMax = eSummary.getChildText("max_hr").toShort()
        exercise.energy = eSummary.getChildText("calories").toInt()
        val maxSetHr = eSummary.getChildText("max_set_hr").toInt()

        // parse heartrate limits
        val eSortzoneList = eSessionData.getChild("sportzones").getChildren("sportzone")
        val heartRateLimits = mutableListOf<HeartRateLimit>()

        for (eSortzone in eSortzoneList) {
            val lowerHeartRate = (eSortzone.getChildText("low_percent").toInt() * maxSetHr / 100).toShort()
            val upperHeartRate = (eSortzone.getChildText("high_percent").toInt() * maxSetHr / 100).toShort()
            val timeWithin = eSortzone.getChildText("time_on").toDouble().toInt()

            heartRateLimits.add(HeartRateLimit(lowerHeartRate, upperHeartRate, null, timeWithin, null))
        }
        exercise.heartRateLimits = heartRateLimits.toTypedArray()

        // parse speed data when available
        val hasSpeedData = eSessionData.getChildText("has_pace_data").toBoolean()
        val distance = eSummary.getChildText("total_distance").toInt()

        // "has_pace_data" can also be true, when no speed data was recorded and "total_distance" is 0
        // => then the speed needs to be disabled in exercise, ExerciseViewer will have problems to display
        //    the inconsistent data (see SourceForge bug #1524834)
        if (hasSpeedData && distance > 0) {
            exercise.speed = ExerciseSpeed(
                    speedAvg = convertSpeed(eSummary.getChildText("avg_pace").toFloat()),
                    speedMax = convertSpeed(eSummary.getChildText("max_pace").toFloat()),
                    distance = distance)
            exercise.recordingMode.isSpeed = true
        } else {
            exercise.recordingMode.isSpeed = false
        }

        // parse laps (they are in reverse order in XML)
        val eLapList = eSessionData.getChild("laps").getChildren("lap")
        val laps = mutableListOf<Lap>()

        for (eLap in eLapList) {
            val lap = Lap()
            laps.add(lap)

            lap.timeSplit = (eLap.getChildText("lap_end_time").toDouble() * 10.0).toInt()
            lap.heartRateSplit = eLap.getChildText("end_hr").toShort()
            lap.heartRateAVG = eLap.getChildText("avg_hr").toShort()
            lap.heartRateMax = eLap.getChildText("max_hr").toShort()

            if (hasSpeedData) {
                val lapSpeedDistance = eLap.getChildText("lap_length").toInt()
                val lapSpeedEnd = convertSpeed(eLap.getChildText("end_pace").toFloat())
                val lapSpeedAVG = convertSpeed(eLap.getChildText("avg_pace").toFloat())
                lap.speed = LapSpeed(lapSpeedEnd, lapSpeedAVG, lapSpeedDistance)
            }
        }

        laps.reverse()
        exercise.lapList = laps.toTypedArray()

        // calculate distance for all laps
        if (hasSpeedData) {
            var accumulatedSpeed = 0

            for (lap in exercise.lapList) {
                lap.speed!!.distance += accumulatedSpeed
                accumulatedSpeed = lap.speed!!.distance
            }
        }

        return exercise
    }

    /**
     * Helper method for converting parsed speed to km/h.
     */
    private fun convertSpeed(speed: Float): Float {
        return if (speed > 0) 3600 / speed else 0f
    }
}
