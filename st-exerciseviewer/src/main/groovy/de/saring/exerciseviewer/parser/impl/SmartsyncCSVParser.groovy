package de.saring.exerciseviewer.parser.impl

import de.saring.exerciseviewer.core.EVException
import de.saring.exerciseviewer.data.*
import de.saring.exerciseviewer.parser.*
import de.saring.util.unitcalc.ConvertUtils

/**
 * This implementation of an ExerciseParser is for reading the CSV files 
 * exported from Oregon Scientific's Smartsync software, 
 * as supplied with the inexpensive WM-100 heartrate logger.
 * These files have the extension ".csv". 
 *
 * Some parts taken from Stefan Saring's PolarHRMParser.groovy (1.0).
 *
 * @author  Kai Pastor
 * @version 1.0
 */
class SmartsyncCSVParser extends AbstractExerciseParser {

    /** Informations about this parser. */
    private ExerciseParserInfo info = new ExerciseParserInfo ('Smartsync CSV', ["csv", "CSV"] as String[])

    /** The list of lines (Strings) of the exercise file. */
     private def fileContent

    /**
     * Returns the informations about this parser implementation.
     * @return the parser informations
     */
    @Override
    ExerciseParserInfo getInfo () {
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
    EVExercise parseExercise (String filename) throws EVException
    {
        try {
            fileContent = new File (filename).readLines ()
            return parseExerciseFromContent ()
        }
        catch (Exception e) {
            throw new EVException ("Failed to read the CSV activity file '${filename}' ...", e)
        }
    }
    
    /**
     * Parses the exercise data from the file content.
     */
    private EVExercise parseExerciseFromContent ()
    {
        // parse basic exercise data
        EVExercise exercise = new EVExercise ()
        exercise.fileType = EVExercise.ExerciseFileType.SSCSV // FIXME
        exercise.recordingMode = new RecordingMode ()
        exercise.lapList = []

        def calDate = Calendar.getInstance ()
        def exeYear = calDate.get (Calendar.YEAR)
        def exeMonth = calDate.get (Calendar.MONTH)
        def exeDay = calDate.get (Calendar.DAY_OF_MONTH)
        def exeHour = 12
        def exeMinute = 0
        def exeSecond = 0

        // create array of exercise sample
        def sampleList = []

        for (line in fileContent) {
            // most frequent element first
            if (line.startsWith (",") ) {
                ExerciseSample exeSample = new ExerciseSample()
                exeSample.heartRate = line.substring (1).toInteger ()
                sampleList.add (exeSample)
            }
            else if (line.startsWith ("Name,")) {
                // not supported in EVExercise
            }
            else if (line.startsWith ("Description,")) {
                // not supported in EVExercise
            }
            else if (line.startsWith ("Date,")) {
                // parse exercise date (mm/dd/yyyy)
                exeMonth = line.substring (5, 7).toInteger () - 1
                exeDay = line.substring (8, 10).toInteger ()
                exeYear = line.substring (11, 15).toInteger ()
            }
            else if (line.startsWith ("Time,")) {
                // parse exercise start time (can be either h:mm:ss or hh:mm:ss !)
                def startTimeSplitted = line.substring (5).tokenize (':.')
                if (startTimeSplitted.size () != 3) {
                    throw new EVException ("Failed to read CSV file, can't parse exercise start time (wrong format) ...");
                }

                // parse start time
                exeHour = startTimeSplitted[0].toInteger ()
                exeMinute = startTimeSplitted[1].toInteger ()
                exeSecond = startTimeSplitted[2].toInteger ()        
            }
            else if (line.startsWith ("SamplingRate,")) {
                exercise.recordingInterval = line.substring (13).toInteger ()
            }
            else if (line.equals ("HeartRate")) {
                // no value
            }
        }

        calDate.set (exeYear, exeMonth, exeDay, exeHour, exeMinute, exeSecond)
        exercise.date = calDate.time
            
        exercise.sampleList = sampleList

        exercise.duration = (sampleList.size-1) * exercise.recordingInterval * 10

        // compute average/maximum heartrate of exercise (not in HRM file)
        def avgHeartrateSum = 0
        exercise.heartRateMax = 0
        
        for (i in 0..<exercise.sampleList.size ())  {
            avgHeartrateSum += exercise.sampleList[i].heartRate
            exercise.heartRateMax = Math.max (exercise.sampleList[i].heartRate, exercise.heartRateMax)
        }
        
        // calculate AVG heartrate
        exercise.heartRateAVG = Math.round (avgHeartrateSum / (float) exercise.sampleList.size ())
  
        // compute timestamps for all recorded exercise samples
        for (i in 0..sampleList.size-1) {
            sampleList[i].timestamp = i * exercise.recordingInterval * 1000L
        }
        
        // done :-)
        return exercise
    }    

}
