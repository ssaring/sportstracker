package de.saring.exerciseviewer.parser.impl

import de.saring.exerciseviewer.core.EVException
import de.saring.exerciseviewer.data.*
import de.saring.exerciseviewer.parser.AbstractExerciseParser
import de.saring.exerciseviewer.parser.ExerciseParserInfo
import de.saring.util.unitcalc.CalculationUtils
import de.saring.util.unitcalc.FormatUtils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * This implementation of an ExerciseParser is for reading XML files from
 * polarpersonaltrainer.com.
 * <br/>
 * Currently only the FT60 data was validated.
 * <br/>
 * The files are in XML format with an XSD available at:
 *      http://www.polarpersonaltrainer.com/schemas/ped-1.0.xsd
 *
 * The extension of the files should be .ped.
 *
 * @author Philippe Marzouk
 * @version 0.30
 */
class PolarPedParser extends AbstractExerciseParser {
    /** Informations about this parser. */
    private def info = new ExerciseParserInfo('Polar Personal Trainer Export Data', ["ped", "PED"] as String[])

    private static def CALENDAR_ITEM_PREFIX = "polar-exercise-data.calendar-items"

    private def int exerciseCount = 0

    private final def formatUtils = new FormatUtils(FormatUtils.UnitSystem.Metric, FormatUtils.SpeedView.DistancePerHour)

    private def path

    /**
     * Returns the informations about this parser implementation.
     * @return the parser informations
     */
    @Override
    ExerciseParserInfo getInfo() {
        info
    }

    /**
     * This method parses the specified exercise file and creates an
     * PVExercise object from it.
     *
     * For an importer, each subsequent call will get the following exercise
     * until there are none left. This is base on the assumption that the xml
     * file is sorted, first exercise data, then fitness-data as this is what
     * the export function of the polarpersonaltrainer.com website does.
     *
     * @param filename name of exercise file to parse
     * @return the parsed PVExercise object
     * @throws EVException thrown on read/parse problems
     */
    @Override
    EVExercise parseExercise(String filename) throws EVException {
        path = readPedFile(filename)

        if (exerciseCount >= 1) {
            return parseExercisePath(path, 0)
        } else {
            throw new EVException("No exercise in file '${filename}' ...")
        }
    }

    /**
     * This method parses the specified exercise file with a specific exerciseIdx
     * and creates an PVExercise object from it.
     *
     * It is only intended to be called by PedImporter after a first call to parseExercise(String)
     * to be able to get the ExerciseCount.
     *
     * @param filename name of exercise file to parse
     * @param exerciseIdx the exercise record index (starting with 0) in the file
     * @return the parsed PVExercise object
     * @throws EVException thrown on read/parse problems
     */
    public EVExercise parseExercise(String filename, Integer exerciseIdx) throws EVException {
        return parseExercisePath(path, exerciseIdx)
    }

    /**
     * reads intially the file from disk.
     */
    private Object readPedFile(String filename) throws EVException {

        try {
            // get GPathResult object by using the XML slurper parser
            def path = new XmlSlurper().parse(new File(filename))

            // we can't rely on the count attribute of calendar-items as
            // it counts other nodes like fitness-data.
            exerciseCount = path."calendar-items".exercise.size()

            return path
        } catch (Exception e) {
            throw new EVException("Failed to parse the Polar Personal Trainer exercise file '${filename}' ...", e)
        }
    }

    /**
     * Parses the exercise data.
     */
    private EVExercise parseExercisePath(path, Integer exerciseID) throws EVException {
        // parse basic exercise data
        EVExercise exercise = new EVExercise()
        exercise.fileType = EVExercise.ExerciseFileType.PED
        exercise.deviceName = 'Polar PED'

        if (exerciseID >= getExerciseCount()) {
            throw new EVException("There is no exercise '${exerciseID}' in current file")
        }

        // Exercise Date
        String dateTime = path."calendar-items".exercise[exerciseID].time.text()
        exercise.dateTime = LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern('yyyy-MM-dd HH:mm:ss.S'))

        exercise.recordingMode = new RecordingMode()
        exercise.recordingMode.speed = true
        exercise.recordingMode.cadence = false
        exercise.recordingMode.altitude = false
        exercise.recordingMode.power = false

        // Exercise Duration
        def duration = formatDuration(path."calendar-items".exercise[exerciseID].result.duration.text())
        def exerciseDuration = formatUtils.timeString2TotalSeconds(duration)
        exercise.duration = exerciseDuration * 10

        // Distance
        def speed = new ExerciseSpeed()
        def distance = path."calendar-items".exercise[exerciseID].result.distance.toBigDecimal()
        speed.distance = distance

        // calculate average speed
        if ((duration != null) && (distance != null)) {
            def averageSpeed = CalculationUtils.calculateAvgSpeed((float) (distance / 1000), (int) exerciseDuration)
            speed.speedAVG = averageSpeed
        }

        exercise.speed = speed

        // Wasted Energy
        exercise.energy = pathToInteger(path."calendar-items".exercise[exerciseID].result.calories)

        // Heart rate average
        exercise.heartRateAVG = pathToInteger(path."calendar-items".exercise[exerciseID].result."heart-rate".average)

        // Heart rate maximum
        exercise.heartRateMax = pathToInteger(path."calendar-items".exercise[exerciseID].result."heart-rate".maximum)

        // set an empty LapList and SampleList
        exercise.lapList = new Lap[0]
        exercise.setSampleList(new ExerciseSample[0])

        return exercise

    }

    private Integer pathToInteger(node) {
        def value = node.text()
        if (value != "") {
            return value.toInteger()
        } else {
            return 0;
        }
    }

    /**
     * returns the number of exercise records in the input file.
     *
     * You should first call parseExercise(filename) to get the first exercise
     * in the file before calling this method as it would return 0 otherwise.
     *
     * @return the number of exercise records in the file
     */
    public int getExerciseCount() {
        return exerciseCount
    }

    private String formatDuration(String duration) {

        // only hour is there
        if (duration.length() <= 2) {
            duration += ":00:00"
        }

        // only hour and minutes are there like 00:50 for 50 minutes
        if (duration.length() == 5) {
            duration += ":00"
        }

        return duration
    }
}