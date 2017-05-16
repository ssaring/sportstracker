package de.saring.exerciseviewer.parser.impl

import de.saring.exerciseviewer.core.EVException
import de.saring.exerciseviewer.data.*
import de.saring.exerciseviewer.parser.AbstractExerciseParser
import de.saring.exerciseviewer.parser.ExerciseParserInfo
import java.io.File
import java.time.LocalDateTime

/**
 * This implementation of an ExerciseParser is for reading the CSV files exported from Oregon Scientific's Smartsync
 * software, as supplied with the inexpensive WM-100 heartrate logger. These files have the extension ".csv".
 *
 * Some parts taken from Stefan Saring's PolarHRMParser.groovy (1.0).
 *
 * @author Stefan Saring (Kotlin port without refactoring)
 * @author Kai Pastor
 */
class SmartsyncCSVParser : AbstractExerciseParser() {

    /** Information about this parser. */
    private val info = ExerciseParserInfo("Smartsync CSV", arrayOf("csv", "CSV"))

    override
    fun getInfo(): ExerciseParserInfo = info

    override
    fun parseExercise(filename: String): EVExercise {

        try {
            val fileContent = File(filename).readLines()
            return parseExerciseFromContent(fileContent)
        }
        catch (e: Exception) {
            throw EVException("Failed to read the Smartsync CSV exercise file '$filename'!", e)
        }
    }

    private fun parseExerciseFromContent(fileContent: List<String>): EVExercise {

        // parse basic exercise data
        val exercise = EVExercise()
        exercise.fileType = EVExercise.ExerciseFileType.SSCSV // FIXME
        exercise.deviceName = "Oregon Scientific Smartsync"
        exercise.recordingMode = RecordingMode()
        exercise.lapList = arrayOf()

        val now = LocalDateTime.now()
        var exeYear = now.year
        var exeMonth = now.monthValue
        var exeDay = now.dayOfMonth
        var exeHour = 12
        var exeMinute = 0
        var exeSecond = 0

        val sampleList = mutableListOf<ExerciseSample>()

        for (line in fileContent) {
            // most frequent element first
            if (line.startsWith(",")) {
                val exeSample = ExerciseSample()
                exeSample.heartRate = line.substring(1).toShort()
                sampleList.add(exeSample)
            } else if (line.startsWith("Name,")) {
                // not supported in EVExercise
            } else if (line.startsWith("Description,")) {
                // not supported in EVExercise
            } else if (line.startsWith("Date,")) {
                // parse exercise dateTime (mm/dd/yyyy)
                exeMonth = line.substring(5, 7).toInt()
                exeDay = line.substring(8, 10).toInt()
                exeYear = line.substring(11, 15).toInt()
            } else if (line.startsWith("Time,")) {
                // parse exercise start time (can be either h:mm:ss or hh:mm:ss !)
                val startTimeSplitted = line.substring(5).split(":")
                if (startTimeSplitted.size != 3) {
                    throw EVException ("Failed to read CSV file, can't parse exercise start time (wrong format) ...");
                }

                // parse start time
                exeHour = startTimeSplitted[0].toInt()
                exeMinute = startTimeSplitted[1].toInt()
                exeSecond = startTimeSplitted[2].toInt()
            } else if (line.startsWith("SamplingRate,")) {
                exercise.recordingInterval = line.substring(13).toShort()
            } else if (line.equals("HeartRate")) {
                // no value
            }
        }

        exercise.dateTime = LocalDateTime.of(exeYear, exeMonth, exeDay, exeHour, exeMinute, exeSecond)

        exercise.sampleList = sampleList.toTypedArray()

        exercise.duration = (sampleList.size - 1) * exercise.recordingInterval * 10

        // compute average/maximum heartrate of exercise (not in HRM file)
        exercise.heartRateAVG = Math.round(sampleList
                .map { it.heartRate }
                .average()).toShort()

        exercise.heartRateMax = sampleList
                .map { it.heartRate }
                .max() ?: 0

        // compute timestamps for all recorded exercise samples
        for (i in 0..sampleList.size - 1) {
            sampleList[i].timestamp = i * exercise.recordingInterval * 1000L
        }

        // done :-)
        return exercise
    }
}
