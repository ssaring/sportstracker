package de.saring.exerciseviewer.parser.impl;

import java.time.LocalDateTime;
import java.util.List;

import de.saring.exerciseviewer.core.EVException;
import de.saring.exerciseviewer.data.EVExercise;
import de.saring.exerciseviewer.data.HeartRateLimit;
import de.saring.exerciseviewer.data.RecordingMode;
import de.saring.exerciseviewer.parser.AbstractExerciseParser;
import de.saring.exerciseviewer.parser.ExerciseParserInfo;

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
 * @author Roland Hostettler
 * @version 1.1
 */
public class PolarF6RawParser extends AbstractExerciseParser {

    /**
     * The exercise file length for F6 watches
     */
    private static final int F6_EXERCISE_FILE_LENGTH = 49;

    /**
     * The exercise file length for F11 watches
     */
    private static final int F11_EXERCISE_FILE_LENGTH = 50;

    /**
     * Informations about this parser.
     */
    private final ExerciseParserInfo info = new ExerciseParserInfo("Polar F6/F11", List.of("frd", "FRD"));

    /**
     * The binary data of the exercise file.
     */
    private int[] fileContent;

    @Override
    public ExerciseParserInfo getInfo() {
        return info;
    }

    @Override
    public EVExercise parseExercise(String filename) throws EVException {
        // read binary file content to array
        fileContent = readFileToByteArray(filename);

        // create an PVExercise object from this data and set file type
        EVExercise exercise = new EVExercise(EVExercise.ExerciseFileType.F6RAW);
        exercise.setDeviceName("Polar F Series");

        // check wheter the read data fits the expected data length
        if (fileContent.length != F6_EXERCISE_FILE_LENGTH && fileContent.length != F11_EXERCISE_FILE_LENGTH) {
            throw new EVException("The exercise file is not valid, the file length is not correct ...");
        }

        // read the exercise name
        StringBuilder exerciseName = new StringBuilder();
        int i = 0;
        while ((fileContent[i] & 0x80) != 0x80 && i < 8) {
            exerciseName.append(decodeChar(fileContent[i]));
            i++;
        }
        exerciseName.append(String.valueOf(fileContent[i] - 0x80));
        exercise.setSportType(exerciseName.toString());

        // read the exercise date
        int dateDay = fileContent[8];
        int dateMonth = fileContent[9];
        int dateYear = 2000 + fileContent[10];
        int dateSeconds = decodeBCD(fileContent[11]);
        int dateMinutes = decodeBCD(fileContent[12]);
        int dateHours = decodeBCD(fileContent[13]);

        // add exercise to the calendar
        exercise.setDateTime(LocalDateTime.of(dateYear, dateMonth, dateDay, dateHours, dateMinutes, dateSeconds));

        // get duration
        int durationSeconds = decodeBCD(fileContent[14]);
        int durationMinutes = decodeBCD(fileContent[15]);
        int durationHours = decodeBCD(fileContent[16]);
        exercise.setDuration((durationHours * 60 * 60 * 10) + (durationMinutes * 60 * 10) + durationSeconds * 10);

        // get heartrate data
        exercise.setHeartRateAVG((short) fileContent[17]);
        exercise.setHeartRateMax((short) fileContent[18]);

        // decode recording mode (heartrate is always recorded)
        // (not available in F6/F11 files)
        RecordingMode recMode = new RecordingMode();
        recMode.setHeartRate(true);
        exercise.setRecordingMode(recMode);

        // Note: The HR limits 1, 2 and 3 represent the zones "light", 
        // "moderate" and "hard". With the actual knowledge, it's not possible 
        // to get the bpm limits out of the watch for these limits. Therefore,
        // the percental heartrate representation is used
        // get the heartrate limit data (3 zones + the selected zone)

        HeartRateLimit heartRateLimit0 = decodeHeartRateLimit(35, 23); // in-zone
        exercise.getHeartRateLimits().add(heartRateLimit0);

        HeartRateLimit heartRateLimit1 = decodeHeartRateLimit(37, 26); // light
        heartRateLimit1.setLowerHeartRate((short) 60);
        heartRateLimit1.setUpperHeartRate((short) 70);
        heartRateLimit1.setAbsoluteRange(false);
        exercise.getHeartRateLimits().add(heartRateLimit1);

        HeartRateLimit heartRateLimit2 = decodeHeartRateLimit(39, 29); // moderate
        heartRateLimit2.setLowerHeartRate((short) 71);
        heartRateLimit2.setUpperHeartRate((short) 80);
        heartRateLimit2.setAbsoluteRange(false);
        exercise.getHeartRateLimits().add(heartRateLimit2);

        HeartRateLimit heartRateLimit3 = decodeHeartRateLimit(41, 32); // hard
        heartRateLimit3.setLowerHeartRate((short) 81);
        heartRateLimit3.setUpperHeartRate((short) 90);
        heartRateLimit3.setAbsoluteRange(false);
        exercise.getHeartRateLimits().add(heartRateLimit3);

        // get energy (in kCal)
        int energyLowByte = fileContent[19];
        int energyHighByte = fileContent[20];
        exercise.setEnergy(energyLowByte + (energyHighByte << 8));

        // Note: the following data is appended from the totals section to each
        // exercise by the f6-split-tool to enhance the ExerciseViewer display

        // set the offset if the file is a 50 byte F11 file
        int offset = 0;
        if (fileContent.length == F11_EXERCISE_FILE_LENGTH) {
            offset = 1;
        }

        // get the total exercise time
        int cumWorkoutSeconds = decodeBCD(fileContent[43 + offset]);
        int cumWorkoutMinutes = decodeBCD(fileContent[44 + offset]);
        int cumWorkoutHours = decodeBCD(fileContent[45 + offset]);
        exercise.setSumExerciseTime((cumWorkoutHours * 60) + cumWorkoutMinutes + (cumWorkoutSeconds / 60));

        // get total energy
        int energyTotalPart1 = decodeBCD(fileContent[46 + offset]);
        int energyTotalPart2 = decodeBCD(fileContent[47 + offset]);
        int energyTotalPart3 = decodeBCD(fileContent[48 + offset]);
        exercise.setEnergyTotal((energyTotalPart3 * 10000) + (energyTotalPart2 * 100) + energyTotalPart1);

        return exercise;
    }

    /**
     * Decodes a character in Polar format to normal character value.
     *
     * @param value value to decode
     * @return normal character value of Polar character value
     */
    private char decodeChar(int value) {
        return switch (value) {
            case 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 -> (char) ('0' + value);
            case 10 -> ' ';
            case 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36 -> (char) ('A' + value - 11);
            case 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62 -> (char) ('a' + value - 37);
            case 63 -> '-';
            case 64 -> '%';
            case 65 -> '/';
            case 66 -> '(';
            case 67 -> ')';
            case 68 -> '*';
            case 69 -> '+';
            case 70 -> '.';
            case 71 -> ':';
            default -> '?';
        };
    }

    /**
     * Decodes a BCD "byte" (in Polar format) to normal int value.
     *
     * @param value BCD "byte" to decode
     * @return normal int value of BCD value
     */
    private int decodeBCD(int value) {
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
    private HeartRateLimit decodeHeartRateLimit(int offsetLimits, int offsetTimes) {
        short lowerHeartRate = (short) fileContent[offsetLimits + 0];
        short upperHeartRate = (short) fileContent[offsetLimits + 1];

        int hrLimitWithinSecs = decodeBCD(fileContent[offsetTimes]);
        hrLimitWithinSecs += decodeBCD(fileContent[offsetTimes + 1]) * 60;
        hrLimitWithinSecs += decodeBCD(fileContent[offsetTimes + 2]) * 60 * 60;

        return new HeartRateLimit(lowerHeartRate, upperHeartRate, null, hrLimitWithinSecs, null, true);
    }
}
