package de.saring.exerciseviewer.parser

import de.saring.exerciseviewer.core.EVException
import de.saring.exerciseviewer.data.EVExercise
import de.saring.util.unitcalc.CalculationUtils
import java.io.File

/**
 * This abstract ExerciseParser implementation class contains the basic functionality which can be used by all
 * ExerciseParser implementations.
 *
 * @author Stefan Saring
 */
abstract class AbstractExerciseParser : ExerciseParser {

    /**
     * Helper method for reading the specified binary exercise file into an Int array. The reason for integers is:
     * byte values are -128 to 127, int values are converted to 0 to 255, this makes parsing much easier.
     *
     * @param filename filename of exercise file to read
     * @return int buffer with the file content (one int for each byte)
     * @throws EVException thrown on read problems
     */
    @Throws(EVException::class)
    protected fun readFileToByteArray(filename: String): IntArray {

        try {
            return File(filename).readBytes()
                    .map { unsignedByteToInt(it) }
                    .toIntArray()
        } catch (e: Exception) {
            throw EVException("Failed to read binary content from exercise file '$filename' ...", e)
        }
    }

    /**
     * Helper method for reading the specified text-based exercise file into a list of strings, one for each line.
     *
     * @param filename filename of exercise file to read
     * @return String array with the file content
     * @throws EVException thrown on read problems
     */
    @Throws(EVException::class)
    protected fun readFileToStringList(filename: String): List<String> {

        try {
            return File(filename).readLines()
        } catch (e: Exception) {
            throw EVException("Failed to read text content from exercise file '$filename' ...", e)
        }
    }

    /**
     * Converts the unsigned Byte value (0..255) to the appropriate Int value.
     *
     * @param value unsigned Byte value
     * @return appropriate Int value
     */
    private fun unsignedByteToInt(value: Byte): Int = value.toInt().and(0xff)

    /**
     * Calculates the average speed for all laps of the specified exercise. This needs to be done for many models
     * because the average lap speed is not part of the recorded data.
     *
     * @param exercise the exercise for calculation
     */
    protected fun calculateAverageLapSpeed(exercise: EVExercise) {

        if (exercise.recordingMode.isSpeed) {
            var distanceBefore = 0
            var timeSplitBefore = 0

            exercise.lapList.forEach { lap ->
                val lapDistance = lap.speed!!.distance - distanceBefore
                val lapDuration = lap.timeSplit - timeSplitBefore

                distanceBefore = lap.speed!!.distance
                timeSplitBefore = lap.timeSplit

                lap.speed!!.speedAVG = CalculationUtils.calculateAvgSpeed(
                        lapDistance / 1000.0, Math.round(lapDuration / 10.0f)).toFloat()
            }
        }
    }
}
