package de.saring.exerciseviewer.parser.impl

import de.saring.exerciseviewer.core.EVException
import de.saring.exerciseviewer.data.*
import de.saring.exerciseviewer.parser.AbstractExerciseParser
import de.saring.exerciseviewer.parser.ExerciseParserInfo

/**
 * This implementation of an ExerciseParser is for reading the XML-based 
 * exercise files of the Polar RS200SD devices.
 *
 * @author Stefan Saring (the C# version was done by Jacob Ilsoe Christensen)
 * @version 1.0
 */
class PolarRS200SDParser extends AbstractExerciseParser {

    /** Informations about this parser. */
    private def info = new ExerciseParserInfo('Polar RS200', ["xml", "XML"] as String[])

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
     * EVExercise object from it.
     *
     * @param filename name of exercise file to parse
     * @return the parsed EVExercise object
     * @throws EVException thrown on read/parse problems
     */
    @Override
    EVExercise parseExercise(String filename) throws EVException {
        try {
            // get GPathResult object by using the XML slurper parser
            def path = new XmlSlurper().parse(new File(filename))
            return parseExercisePath(path)
        }
        catch (Exception e) {
            throw new EVException("Failed to read the RS200SD exercise file '${filename}' ...", e)
        }
    }

    /**
     * Parses the exercise data from the specified rs200_session element.
     */
    private EVExercise parseExercisePath(path) {
        // parse basic exercise data
        EVExercise exercise = new EVExercise()
        exercise.fileType = EVExercise.ExerciseFileType.RS200SDRAW
        exercise.recordingMode = new RecordingMode()

        def calDate = Calendar.getInstance()
        calDate.set(
                path.session_data.year.toInteger(),
                path.session_data.month.toInteger() - 1,
                path.session_data.day.toInteger(),
                path.session_data.start_hour.toInteger(),
                path.session_data.start_minute.toInteger(),
                path.session_data.start_second.toInteger())
        exercise.date = calDate.time

        def summary = path.session_data.summary
        exercise.duration = summary.length.toFloat() * 10
        exercise.heartRateAVG = summary.avg_hr.toInteger()
        exercise.heartRateMax = summary.max_hr.toInteger()
        exercise.energy = summary.calories.toInteger()

        // parse heartrate limits
        def maxSetHr = summary.max_set_hr.toInteger()
        def sportzoneList = path.session_data.sportzones.sportzone
        exercise.heartRateLimits = new HeartRateLimit[sportzoneList.size()]

        for (i in 0..<sportzoneList.size()) {
            exercise.heartRateLimits[i] = new HeartRateLimit()
            exercise.heartRateLimits[i].lowerHeartRate = sportzoneList[i].low_percent.toInteger() * maxSetHr / 100
            exercise.heartRateLimits[i].upperHeartRate = sportzoneList[i].high_percent.toInteger() * maxSetHr / 100
            exercise.heartRateLimits[i].timeWithin = sportzoneList[i].time_on.toFloat()
        }

        // parse speed data when available
        def hasSpeedData = path.session_data.has_pace_data.toBoolean()
        def distance = summary.total_distance.toInteger()

        // "has_pace_data" can also be true, when no speed data was recorded and "total_distance" is 0
        // => then the speed needs to be disabled in exercise, ExerciseViewer will have problems to display
        //    the inconsistent data (see SourceForge bug #1524834)
        if (hasSpeedData && distance > 0) {
            exercise.speed = new ExerciseSpeed()
            exercise.speed.distance = distance
            exercise.speed.speedMax = convertSpeed(summary.max_pace.toFloat())
            exercise.speed.speedAVG = convertSpeed(summary.avg_pace.toFloat())
            exercise.recordingMode.speed = true
        } else {
            exercise.recordingMode.speed = false
        }

        // parse laps (they are in reverse order in XML)
        def lapElements = path.session_data.laps.lap
        def lapCount = lapElements.size()
        exercise.lapList = new Lap[lapCount]

        for (i in 0..<lapCount) {
            def lap = new Lap()
            exercise.lapList[lapCount - i - 1] = lap

            lap.timeSplit = lapElements[i].lap_end_time.toFloat() * 10
            lap.heartRateSplit = lapElements[i].end_hr.toInteger()
            lap.heartRateAVG = lapElements[i].avg_hr.toInteger()
            lap.heartRateMax = lapElements[i].max_hr.toInteger()

            if (hasSpeedData) {
                lap.speed = new LapSpeed()
                lap.speed.distance = lapElements[i].lap_length.toInteger()
                lap.speed.speedEnd = convertSpeed(lapElements[i].end_pace.toFloat())
                lap.speed.speedAVG = convertSpeed(lapElements[i].avg_pace.toFloat())
            }
        }

        // calculate distance for all laps
        if (hasSpeedData) {
            int accumulatedSpeed = 0;

            for (lap in exercise.lapList) {
                lap.speed.distance += accumulatedSpeed
                accumulatedSpeed = lap.speed.distance;
            }
        }

        return exercise
    }

    /**
     * Helper method for converting parsed speed to km/h.
     */
    private def convertSpeed(speed) {
        if (speed > 0) {
            return 3600 / speed
        }
        return 0
    }
}
