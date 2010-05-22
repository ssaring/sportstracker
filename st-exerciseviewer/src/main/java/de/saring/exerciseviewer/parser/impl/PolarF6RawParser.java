package de.saring.exerciseviewer.parser.impl;

import de.saring.exerciseviewer.parser.*;
import de.saring.exerciseviewer.core.EVException;
import de.saring.exerciseviewer.data.HeartRateLimit;
import de.saring.exerciseviewer.data.EVExercise;
import de.saring.exerciseviewer.data.RecordingMode;
import de.saring.exerciseviewer.data.Lap;
import de.saring.exerciseviewer.data.ExerciseSample;
import java.util.Calendar;

/**
 * This implementation of an ExerciseParser is for reading RAW files of the 
 * Polar F6 and F11 devices. These devices are limited HRMs only supporting 
 * exercise statistics but no sampled data.
 * <br/>
 * Currently only the F6 and F11 is supported due to the lack of other HRMs to 
 * test.
 * <br/>
 * Usually the exercise files have the extension ".frd". They can be read
 * by using the SonicLink from the Polar device with the tool RS-200 decoder
 * (available for free at http://sourceforge.net/projects/rs200-decoder) and 
 * converted using the f6-split-tool (http://toazter.ch/sites/F6SplitTool).
 * This is a little complicated way to go at the moment but a better solution is
 * planned.
 * <br/>
 * The parser is based on the PolarSRawParser by Stefan Saring.
 * 
 * @author  Roland Hostettler
 * @version 1.1
 */
public class PolarF6RawParser extends AbstractExerciseParser {
    
    /** Informations about this parser. */
    private final ExerciseParserInfo info = new ExerciseParserInfo ("Polar F6/F11", new String[] {"frd", "FRD"});
    
    /** The binary data of the exercise file. */
    private int[] fileContent;
    
    /** The exercise file length for F6 watches */
    private static int F6ExerciseFileLength = 49;
    
    /** The exercise file length for F11 watches */
    private static int F11ExerciseFileLength = 50;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ExerciseParserInfo getInfo () {
        return info;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public EVExercise parseExercise (String filename) throws EVException
    {
        // read binary file content to array
        fileContent = readFileToByteArray (filename);
    
        // create an PVExercise object from this data and set file type
        EVExercise exercise = new EVExercise ();
        exercise.setFileType (EVExercise.ExerciseFileType.F6RAW);
        
        // check wheter the read data fits the expected data length
        if (fileContent.length != F6ExerciseFileLength && fileContent.length != F11ExerciseFileLength) {
            throw new EVException ("The exercise file is not valid, the file length is not correct ...");
        }
        
        // read the exercise name
        StringBuilder exerciseName = new StringBuilder ();
        int i = 0;
        while((fileContent[i] & 0x80) != 0x80 && i < 8) {
            exerciseName.append(decodeChar (fileContent[i]));
            i++;
        }
        exerciseName.append (String.valueOf (fileContent[i]-0x80));
        exercise.setTypeLabel (exerciseName.toString ());
        
        // read the exercise date
        int dateDay = fileContent[8];
        int dateMonth = fileContent[9];
        int dateYear = 2000+fileContent[10];
        int dateSeconds = decodeBCD (fileContent[11]);
        int dateMinutes = decodeBCD (fileContent[12]);
        int dateHours = decodeBCD (fileContent[13]);
 
        // add exercise to the calendar
        Calendar calDate = Calendar.getInstance ();
        calDate.set (dateYear, dateMonth-1, dateDay, dateHours, dateMinutes, dateSeconds);
        exercise.setDate (calDate.getTime ());

        // get duration
        int durationSeconds = decodeBCD (fileContent[14]);
        int durationMinutes = decodeBCD (fileContent[15]);
        int durationHours = decodeBCD (fileContent[16]);
        exercise.setDuration ((durationHours * 60 * 60 * 10) + (durationMinutes * 60 * 10) + durationSeconds * 10);
        
        // get heartrate data
        exercise.setHeartRateAVG ((short) fileContent[17]);
        exercise.setHeartRateMax ((short) fileContent[18]);

        // decode recording mode (heartrate is always recorded)
        // (not available in F6/F11 files)
        RecordingMode recMode = new RecordingMode ();
        exercise.setRecordingMode (recMode);

        // Note: The HR limits 1, 2 and 3 represent the zones "light", 
        // "moderate" and "hard". With the actual knowledge, it's not possible 
        // to get the bpm limits out of the watch for these limits. Therefore,
        // the percental heartrate representation is used
        // get the heartrate limit data (3 zones + the selected zone)
        exercise.setHeartRateLimits (new HeartRateLimit[4]);
        exercise.getHeartRateLimits ()[0] = decodeHeartRateLimit (35, 23); // in-zone
        exercise.getHeartRateLimits ()[1] = decodeHeartRateLimit (37, 26); // light
        exercise.getHeartRateLimits ()[1].setLowerHeartRate ((short) 60);
        exercise.getHeartRateLimits ()[1].setUpperHeartRate ((short) 70);
        exercise.getHeartRateLimits ()[1].setAbsoluteRange (false);
        exercise.getHeartRateLimits ()[2] = decodeHeartRateLimit (39, 29); // moderate
        exercise.getHeartRateLimits ()[2].setLowerHeartRate ((short) 71);
        exercise.getHeartRateLimits ()[2].setUpperHeartRate ((short) 80);
        exercise.getHeartRateLimits ()[2].setAbsoluteRange (false);
        exercise.getHeartRateLimits ()[3] = decodeHeartRateLimit (41, 32); // hard
        exercise.getHeartRateLimits ()[3].setLowerHeartRate ((short) 81);
        exercise.getHeartRateLimits ()[3].setUpperHeartRate ((short) 90);
        exercise.getHeartRateLimits ()[3].setAbsoluteRange (false);
        
        // get energy (in kCal)
        int energyLowByte = fileContent[19];
        int energyHighByte = fileContent[20];
        exercise.setEnergy (energyLowByte + (energyHighByte<<8));
        
        // set an empty LapList and SampleList
        exercise.setLapList(new Lap[0]);
        exercise.setSampleList(new ExerciseSample[0]);
        
        // Note: the following data is appended from the totals section to each
        // exercise by the f6-split-tool to enhance the ExerciseViewer display
        
        // set the offset if the file is a 50 byte F11 file
        int offset = 0;
        if(fileContent.length == F11ExerciseFileLength) {
            offset = 1;
        }

        // get the total exercise time
        int cumWorkoutSeconds = decodeBCD (fileContent[43+offset]);
        int cumWorkoutMinutes = decodeBCD (fileContent[44+offset]);
        int cumWorkoutHours = decodeBCD (fileContent[45+offset]);
        exercise.setSumExerciseTime ((cumWorkoutHours*60)+cumWorkoutMinutes+(cumWorkoutSeconds/60));

        // get total energy
        int energyTotalPart1 = decodeBCD (fileContent[46+offset]);
        int energyTotalPart2 = decodeBCD (fileContent[47+offset]);
        int energyTotalPart3 = decodeBCD (fileContent[48+offset]);
        exercise.setEnergyTotal ((energyTotalPart3*10000)+(energyTotalPart2*100)+energyTotalPart1);
        
        return exercise;
    }

    /**
     * Decodes a character in Polar format to normal character value.
     *
     * @param value value to decode
     * @return normal character value of Polar character value
     */
    private char decodeChar (int value) {
        char cDecoded = '?';
        
        switch ( value ) {
            case 0: case 1: case 2: case 3: case 4:
            case 5: case 6: case 7: case 8: case 9:
                cDecoded = (char) ('0' + value); break;
            case 10:
                cDecoded = ' '; break;
            case 11: case 12: case 13: case 14: case 15:
            case 16: case 17: case 18: case 19: case 20:
            case 21: case 22: case 23: case 24: case 25:
            case 26: case 27: case 28: case 29: case 30:
            case 31: case 32: case 33: case 34: case 35:
            case 36:
                cDecoded = (char) ('A' + value - 11); break;
            case 37: case 38: case 39: case 40: case 41:
            case 42: case 43: case 44: case 45: case 46:
            case 47: case 48: case 49: case 50: case 51:
            case 52: case 53: case 54: case 55: case 56:
            case 57: case 58: case 59: case 60: case 61:
            case 62:
                cDecoded = (char) ('a' + value - 37); break;
            case 63: cDecoded = '-'; break;
            case 64: cDecoded = '%'; break;
            case 65: cDecoded = '/'; break;
            case 66: cDecoded = '('; break;
            case 67: cDecoded = ')'; break;
            case 68: cDecoded = '*'; break;
            case 69: cDecoded = '+'; break;
            case 70: cDecoded = '.'; break;
            case 71: cDecoded = ':'; break;
            case 72: cDecoded = '?'; break;
            default: break;
        }
        
        return cDecoded;
    }
    
    /**
     * Decodes a BCD "byte" (in Polar format) to normal int value.
     *
     * @param value BCD "byte" to decode
     * @return normal int value of BCD value
     */
    private int decodeBCD (int value) {
        // (upper 4 bits * 10) + lower 4 bits
        return ((value >> 4) * 10) + (value & 0x0f);
    }
        
    /**
     * This method decodes the data for a HeartRateLimit object (limits and times 
     * below, within and above) and returns it.
     *
     * @param offsetLimits offset in fileContent, where the limit data starts
     * @param offsetTimes offset in fileContent, where the times data starts
     * @return the filled HeartRateLimit object
     */
    private HeartRateLimit decodeHeartRateLimit (int offsetLimits, int offsetTimes) {
        HeartRateLimit hrLimit = new HeartRateLimit ();
        hrLimit.setLowerHeartRate ((short) fileContent[offsetLimits + 0]);
        hrLimit.setUpperHeartRate ((short) fileContent[offsetLimits + 1]);

        int hrLimitWithinSecs = decodeBCD (fileContent[offsetTimes]);
        hrLimitWithinSecs += decodeBCD (fileContent[offsetTimes + 1]) * 60;
        hrLimitWithinSecs += decodeBCD (fileContent[offsetTimes + 2]) * 60 * 60;
        hrLimit.setTimeWithin (hrLimitWithinSecs);
        
        return hrLimit;
    }
}
