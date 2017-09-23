package de.saring.exerciseviewer.parser.impl

import de.saring.exerciseviewer.core.EVException
import de.saring.exerciseviewer.data.EVExercise
import de.saring.exerciseviewer.data.ExerciseSpeed
import de.saring.exerciseviewer.data.RecordingMode
import de.saring.exerciseviewer.parser.AbstractExerciseParser
import de.saring.exerciseviewer.parser.ExerciseParserInfo
import de.saring.util.unitcalc.CalculationUtils
import de.saring.util.unitcalc.FormatUtils
import org.jdom2.Element
import org.jdom2.Namespace
import org.jdom2.input.SAXBuilder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * This implementation of an ExerciseParser is for reading XML files from polarpersonaltrainer.com.
 * Currently only the FT60 data was validated.
 * The files are in XML format with an XSD available at: http://www.polarpersonaltrainer.com/schemas/ped-1.0.xsd
 * The extension of the files should be .ped.
 *
 * @author Stefan Saring (Kotlin port with some refactoring)
 * @author Philippe Marzouk
 */
class PolarPedParser : AbstractExerciseParser() {

    private val formatUtils = FormatUtils(FormatUtils.UnitSystem.Metric, FormatUtils.SpeedView.DistancePerHour)

    private val namespace = Namespace.getNamespace("http://www.polarpersonaltrainer.com")

    override
    val info = ExerciseParserInfo("Polar Personal Trainer Export Data", listOf("ped", "PED"))

    override
    fun parseExercise(filename: String): EVExercise {

        val eRoot = readPedFile(filename)
        val eCalendarItems = eRoot.getChild("calendar-items", namespace)

        val exercise = parseFirstExercisesElement(eCalendarItems)
        return exercise ?: throw EVException("No exercise found in file '$filename'!")
    }

    private fun readPedFile(filename: String): Element {
        try {
            val document = SAXBuilder().build(filename)
            return document.rootElement
        } catch (e: Exception) {
            throw EVException("Failed to parse the Polar Personal Trainer exercise file '$filename' ...", e)
        }
    }

    private fun parseFirstExercisesElement(eCalendarItems: Element): EVExercise? {

        val eExercise = eCalendarItems.getChild("exercise", namespace)
        if (eExercise == null) {
            return null
        }

        // parse basic exercise data
        val exercise = EVExercise(EVExercise.ExerciseFileType.PED)
        exercise.deviceName = "Polar PED"

        // Exercise Date
        val eResult = eExercise.getChild("result", namespace)
        val dateTime = eExercise.getChildText("time", namespace)
        exercise.dateTime = LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"))

        exercise.recordingMode = RecordingMode()
        exercise.recordingMode.isSpeed = true
        exercise.recordingMode.isCadence = false
        exercise.recordingMode.isAltitude = false
        exercise.recordingMode.isPower = false

        // Exercise Duration
        val duration = formatDuration(eResult.getChildText("duration", namespace))
        val exerciseDuration = formatUtils.timeString2TotalSeconds(duration)
        exercise.duration = exerciseDuration * 10

        // Distance
        val distance = eResult.getChildText("distance", namespace).toDouble().toInt()

        // calculate average speed
        val speedAvg = if (exerciseDuration > 0) {
            CalculationUtils.calculateAvgSpeed((distance / 1000.0).toFloat(), exerciseDuration)
        } else 0f

        exercise.speed = ExerciseSpeed(speedAvg, 0f, distance)

        // Wasted Energy
        exercise.energy = eResult.getChildText("calories", namespace)?.toInt() ?: 0

        // Heart rate average
        exercise.heartRateAVG =
                eResult.getChild("heart-rate", namespace)?.getChildText("average", namespace)?.toShort() ?: 0

        // Heart rate maximum
        exercise.heartRateMax =
                eResult.getChild("heart-rate", namespace)?.getChildText("maximum", namespace)?.toShort() ?: 0

        return exercise
    }

    private fun formatDuration(duration: String): String {
        return if (duration.length <= 2) {
            duration + ":00:00"
        } else if (duration.length == 5) {
            duration + ":00"
        } else {
            duration
        }
    }
}
