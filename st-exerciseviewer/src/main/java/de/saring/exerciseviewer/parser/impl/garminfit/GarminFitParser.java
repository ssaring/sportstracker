package de.saring.exerciseviewer.parser.impl.garminfit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import com.garmin.fit.Decode;
import com.garmin.fit.MesgListener;

import de.saring.exerciseviewer.core.EVException;
import de.saring.exerciseviewer.data.EVExercise;
import de.saring.exerciseviewer.parser.AbstractExerciseParser;
import de.saring.exerciseviewer.parser.ExerciseParserInfo;

/**
 * This ExerciseParser implementation is for reading Garmin FIT files (binary
 * format) which contain activity (exercise) data.<br>
 * The parser should support all devices which store their data in FIT format,
 * but it's only tested with Garmin Edge 500 files.<br>
 * The parser uses the Java library "fit.jar" from the official Garmin FIT SDK
 * (open source) for accessing the FIT file content.<br>
 * There's many more interesting data in FIT files (e.g. device informations,
 * user informations, heartrate zones, events), but it can't be stored in
 * EVExercises (not yet).
 *
 * @author Stefan Saring
 * @version 1.0
 */
public class GarminFitParser extends AbstractExerciseParser {

    /**
     * Informations about this parser.
     */
    private final ExerciseParserInfo info = new ExerciseParserInfo("Garmin FIT", List.of("fit", "FIT"));


    @Override
    public ExerciseParserInfo getInfo() {
        return info;
    }

    @Override
    public EVExercise parseExercise(String filename) throws EVException {
        FitMessageListener mesgListener = new FitMessageListener();
        readFitFile(filename, mesgListener);
        return mesgListener.getExercise();
    }

    /**
     * Reads the specified FIT file and creates the appropriate EVExcercise.
     *
     * @param filename name of ther FIT file
     * @param mesgListener listener for creating the exercise from the messages
     */
    private void readFitFile(String filename, MesgListener mesgListener) throws EVException {

        try (FileInputStream fis = new FileInputStream(new File(filename))) {
            new Decode().read(fis, mesgListener);
        } catch (IOException ioe) {
            throw new EVException("Failed to read FIT file '" + filename + "'...", ioe);
        }
    }
}
