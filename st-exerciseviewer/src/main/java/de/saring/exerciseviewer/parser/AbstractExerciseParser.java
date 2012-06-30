package de.saring.exerciseviewer.parser;

import de.saring.exerciseviewer.core.EVException;
import de.saring.exerciseviewer.data.Lap;
import de.saring.exerciseviewer.data.EVExercise;
import de.saring.util.unitcalc.CalculationUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * This abstract ExerciseParser implementation class contains the basic 
 * functionality which can be used by all ExerciseParser implementations.
 * 
 * @author  Stefan Saring
 * @version 1.0
 */
public abstract class AbstractExerciseParser implements ExerciseParser {
    
    /**
     * This is a helper method for all parser implementations, which reads
     * the specified binary exercise file into a int buffer.
     * The reason for integers in the buffer is: byte values are -128 to 127, 
     * int values are converted to 0 to 255, this makes parsing much easier.
     *
     * @param filename filename of exercise file to read
     * @return byte buffer with the file content
     * @throws EVException thrown on read problems
     */
    protected int[] readFileToByteArray (String filename) throws EVException {
        File file = new File (filename);
        
        // open exercise file and create buffer with same length
        try (FileInputStream fiStream = new FileInputStream (file)) {
            int fileLength = (int) file.length ();
            byte[] byteBuffer = new byte[fileLength];
            int[] intBuffer = new int[fileLength]; 

            // read all bytes to buffer
            if (fiStream.read (byteBuffer) != fileLength) {
                throw new Exception ("Failed to read complete file content ...");
            }
            
            // convert signed byte buffer to int buffer
            for (int i = 0; i < fileLength; i++) {
                intBuffer[i] = unsignedByteToInt (byteBuffer[i]);
            }
            return intBuffer;                
        }
        catch (Exception e) {
            throw new EVException ("Failed to read binary content from exercise file '" + filename + "' ...", e);
        }
    }

    /**
     * This is a helper method for all parser implementations, which reads
     * the specified text-based exercise file into an array of strings
     * (one string for each line).
     *
     * @param filename filename of exercise file to read
     * @return String array with the file content
     * @throws EVException thrown on read problems
     */
    protected String[] readFileToStringArray (String filename) throws EVException {
        
        try (BufferedReader bufReader = new BufferedReader (new FileReader (filename))) {
            List<String> lLines = new ArrayList<>();
            String strCurrentLine = null;
            
            // add all lines of file to temporary ArrayList
            while ((strCurrentLine = bufReader.readLine ()) != null)	{
                lLines.add (strCurrentLine);
            }
            
            // return a normal string array
            return lLines.toArray (new String[lLines.size ()]);
        } 
        catch (Exception e) {
            throw new EVException ("Failed to read text content from exercise file '" + filename + "' ...", e);
        }
    }
    
    /**
     * This helper method converts the unisgned byte value (0..255) to the
     * appropriate int value. It's usefull for reading binary file content.
     *
     * @param value unsigned byte value
     * @return appropriate int value
     */
    private int unsignedByteToInt (byte value)
    {
        return value & 0xff;
    }
    
    
    /**
     * This helper method calculates the average speed for all laps of the 
     * specified exercise. This needs to be done for many models because the
     * average lap speed is not part of the recorded data.
     *
     * @param exercise the exercise for calculation
     */
    protected void calculateAverageLapSpeed (EVExercise exercise)
    {
        // abort calculation when speed or lap data was not recorded
        if (!exercise.getRecordingMode ().isSpeed () || exercise.getLapList () == null) {
            return;
        }
        
        // calculate AVG speed for all laps
        int distanceBefore = 0;
        int timeSplitBefore = 0;
        for (Lap lap : exercise.getLapList ()) 
        {
            int lapDistance = lap.getSpeed ().getDistance () - distanceBefore;
            int lapDuration = lap.getTimeSplit () - timeSplitBefore;
            
            distanceBefore = lap.getSpeed ().getDistance ();
            timeSplitBefore = lap.getTimeSplit ();
            
            lap.getSpeed ().setSpeedAVG (CalculationUtils.calculateAvgSpeed (
                lapDistance / 1000f, (int) Math.round (lapDuration / 10f)));
        }
    }
}
