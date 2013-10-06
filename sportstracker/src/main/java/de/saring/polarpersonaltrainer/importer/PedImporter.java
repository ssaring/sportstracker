package de.saring.polarpersonaltrainer.importer;

import de.saring.exerciseviewer.core.EVException;
import de.saring.exerciseviewer.data.EVExercise;
import de.saring.exerciseviewer.parser.impl.PolarPedParser;
import de.saring.sportstracker.core.STException;
import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.data.ExerciseList;
import de.saring.sportstracker.data.SportTypeList;
import de.saring.sportstracker.storage.IStorage;
import de.saring.sportstracker.storage.XMLStorage;
import org.apache.commons.cli.*;

import java.util.Iterator;

/**
 * This class in an importer of files exported from polarpersonaltrainer.com.
 * <p/>
 * It uses the PolarPedParser in the ExerciseViewer of SportsTracker.
 * <p/>
 * The extension of the files should be .ped.
 *
 * @author Philippe Marzouk
 * @version 1.0
 */
public class PedImporter {

    private static final String FILENAME_EXERCISE_LIST = "exercises.xml";
    private static final String FILENAME_SPORT_TYPE_LIST = "sport-types.xml";

    private static void printUsage(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("PedImporter", options);
    }

    /**
     * The directory where the application data of the user is stored.
     */
    private static String dataDirectory;
    private static boolean dryRun = false;
    private ExerciseList exerciseList;
    /**
     * The sport type list of the user.
     */
    private SportTypeList sportTypeList;
    private IStorage storage;
    private int addedExercises = 0;

    /**
     * Starts the importer.
     *
     * @param args the command line arguments
     */
    @SuppressWarnings("static-access")
    public static void main(String[] args) {

        Options options = new Options();
        options.addOption(OptionBuilder.withLongOpt("filename").hasArg().isRequired().withDescription("Input file exported from polarpersonaltrainer.com").create("f"));
        options.addOption(OptionBuilder.withLongOpt("datadir").hasArg().withDescription("Directory where the SportsTracker data are stored, default $HOME/.sportstracker").create("d"));
        options.addOption(OptionBuilder.withLongOpt("sportType").hasArg().withDescription("sport-type id from <datadir>/sport-types.xml, default 1").create());
        options.addOption(OptionBuilder.withLongOpt("sportSubType").hasArg().withDescription("sport-subtype id from <datadir>/sport-types.xml, default 1").create());
        options.addOption(OptionBuilder.withLongOpt("dry-run").withDescription("if this parameter is set, no data is written to disk").create("n"));

        CommandLineParser parser = new GnuParser();
        String filename;
        int sportTypeId = 1;
        int sportSubTypeId = 1;
        try {
            CommandLine line = parser.parse(options, args);
            filename = line.getOptionValue("f");

            if (line.hasOption("sportType")) {
                String sportType = line.getOptionValue("sportType");
                sportTypeId = Integer.parseInt(sportType);
            }

            if (line.hasOption("sportSubType")) {
                String sportSubType = line.getOptionValue("sportSubType");
                sportSubTypeId = Integer.parseInt(sportSubType);
            }

            if (line.hasOption("datadir")) {
                dataDirectory = line.getOptionValue("datadir");
            } else {
                dataDirectory = System.getProperty("user.home") + "/.sportstracker";
            }

            if (line.hasOption("n")) {
                dryRun = true;
            }
        } catch (ParseException exp) {
            System.out.println("Usage error: " + exp.getMessage());
            printUsage(options);
            return;
        }

        if (filename == null) {
            printUsage(options);
        }

        PedImporter app = new PedImporter();
        try {
            app.initApplication(filename, sportTypeId, sportSubTypeId);
        } catch (STException | EVException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void initApplication(String filename, int sportTypeId, int sportSubTypeId) throws STException, EVException {
        Boolean modified = false;

        storage = new XMLStorage();
        // directory where the data is stored

        final String exerciseFilename = dataDirectory + "/" + FILENAME_EXERCISE_LIST;

        sportTypeList = storage.readSportTypeList(dataDirectory + "/" + FILENAME_SPORT_TYPE_LIST);
        exerciseList = storage.readExerciseList(exerciseFilename, sportTypeList);

        try {
            checkSuppliedSportType(sportTypeId, sportSubTypeId);
        } catch (EVException ex) {
            System.out.println(ex.getMessage());
            return;
        }

        PolarPedParser parser = new PolarPedParser();

        // first read to get the exercise count
        parser.parseExercise(filename);
        int exerciseCount = parser.getExerciseCount();

        for (int exerciseIndex = 0; exerciseIndex < exerciseCount; exerciseIndex++) {
            try {
                EVExercise pedExercise = parser.parseExercise(filename, exerciseIndex);

                boolean found = false;
                for (Exercise exercise : exerciseList) {
                    if (pedExercise.getDate().equals(exercise.getDate())) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    // add Exercise to the list
                    Exercise newExercise = new Exercise(exerciseList.getNewID());
                    newExercise.setDate(pedExercise.getDate());
                    newExercise.setAvgSpeed(pedExercise.getSpeed().getSpeedAVG());
                    newExercise.setIntensity(Exercise.IntensityType.NORMAL);
                    newExercise.setAvgHeartRate(pedExercise.getHeartRateAVG());
                    newExercise.setCalories(pedExercise.getEnergy());
                    newExercise.setDistance(pedExercise.getSpeed().getDistance() / 1000f);
                    newExercise.setDuration(pedExercise.getDuration() / 10);

                    // arbritarily use the first sport from the list
                    newExercise.setSportType(sportTypeList.getByID(sportTypeId));
                    newExercise.setSportSubType(sportTypeList.getByID(sportTypeId).getSportSubTypeList().getByID(sportSubTypeId));

                    exerciseList.set(newExercise);
                    modified = true;
                    addedExercises++;
                }

            } catch (EVException ex) {
                break;
            }
        }

        storeExerciseList(exerciseList, exerciseFilename, modified);
    }

    private void storeExerciseList(ExerciseList exerciseList, String filename, Boolean modified) throws STException {

        if (dryRun) {
            System.out.println("Dry run, nothing written to disk");
        } else if (modified) {
            storage.storeExerciseList(exerciseList, filename);
        }
        if (addedExercises > 0) {
            System.out.println("added " + addedExercises + " exercise(s)");
        } else {
            System.out.println("no new exercise found in input file");
        }
    }

    private void checkSuppliedSportType(int sportTypeId, int sportSubTypeId) throws EVException {
        if (sportTypeList.getByID(sportTypeId) == null) {
            throw new EVException("sport-type id " + sportTypeId + " not found in " + dataDirectory + "/sport-types.xml");
        }

        if (sportTypeList.getByID(sportTypeId).getSportSubTypeList().getByID(sportSubTypeId) == null) {
            throw new EVException("sport-subtype id " + sportSubTypeId + " not found in  " + dataDirectory + "/sport-types.xml");
        }
    }
}
