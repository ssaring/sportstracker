package de.saring.exerciseviewer.parser.impl.garminfit

import java.io.File
import java.io.FileInputStream
import java.io.IOException

import com.garmin.fit.Decode
import com.garmin.fit.MesgListener

import de.saring.exerciseviewer.core.EVException
import de.saring.exerciseviewer.data.EVExercise
import de.saring.exerciseviewer.parser.AbstractExerciseParser
import de.saring.exerciseviewer.parser.ExerciseParserInfo

/**
 * This ExerciseParser implementation is for reading Garmin FIT files (binary format) which contain activity
 * (exercise) data. The parser should support all devices which store their data in FIT format, it's tested with a lot
 * of different models (see README.txt)<br/>
 * The parser uses the Java library "fit.jar" from the official Garmin FIT SDK (open source) for accessing the FIT file
 * content.<br/>
 * There's many more interesting data in FIT files (e.g. device information, user information, heartrate zones, events),
 * but it can't be stored in EVExercises (not yet).
 *
 * @author Stefan Saring
 * @version 1.0
 */
class GarminFitParser : AbstractExerciseParser() {

    override val info = ExerciseParserInfo("Garmin FIT", listOf("fit", "FIT"))

    override fun parseExercise(filename: String): EVExercise {
        val mesgListener = FitMessageListener()
        readFitFile(filename, mesgListener)
        return mesgListener.getExercise()
    }

    /**
     * Reads the specified FIT file and creates the appropriate EVExcercise.
     *
     * @param filename name of ther FIT file
     * @param mesgListener listener for creating the exercise from the messages
     */
    private fun readFitFile(filename: String, mesgListener: MesgListener) {

        try {
            FileInputStream(File(filename)).use { fis -> Decode().read(fis, mesgListener) }
        } catch (ioe: IOException) {
            throw EVException("Failed to read FIT file '$filename'...", ioe)
        }
    }
}
