package de.saring.exerciseviewer.parser.impl;

import java.time.LocalDateTime;
import java.util.List;

import de.saring.exerciseviewer.core.EVException;
import de.saring.exerciseviewer.data.EVExercise;
import de.saring.exerciseviewer.data.ExerciseSample;
import de.saring.exerciseviewer.data.ExerciseSpeed;
import de.saring.exerciseviewer.data.HeartRateLimit;
import de.saring.exerciseviewer.data.Lap;
import de.saring.exerciseviewer.data.LapSpeed;
import de.saring.exerciseviewer.data.RecordingMode;
import de.saring.exerciseviewer.parser.AbstractExerciseParser;
import de.saring.exerciseviewer.parser.ExerciseParserInfo;
import de.saring.util.unitcalc.ConvertUtils;

/**
 * This implementation of an ExerciseParser is for reading RAW files of the
 * Polar S4- and S5-Series devices. The exercises of these devices are very
 * similar, better models are containing more informations, which can be
 * found on different indices, depending on the model.
 * <br/>
 * Currently only the S510 device is supported: Other models in the S4 and S5
 * series should not be too hard to add.
 * <br/>
 * It is assumed that the exercise files have the extension ".hsr". You can use
 * SonicRead (http://code.google.com/p/sonicread/) to extract the exercise data
 * from your Polar device to a hsr file.
 * <p/>
 * This file is based on PolarSRawParser.java by Stefan Saring
 *
 * @author Remco den Breeje
 * @version 1.0
 */
public class PolarHsrRawParser extends AbstractExerciseParser {

    /**
     * Informations about this parser.
     */
    private final ExerciseParserInfo info = new ExerciseParserInfo("Polar HSR", List.of("hsr", "HSR"));

    /**
     * The binary data of the exercise file.
     */
    private int[] fileContent;
    /**
     * section data
     */
    private int[][] s;


    @Override
    public ExerciseParserInfo getInfo() {
        return info;
    }

    private int sdata(int section, int index) throws EVException {
        section += index / 60;
        index %= 60;

        if (section >= s.length) {
            throw new EVException(String.format("Error! Section %d does not exist", section));
        }
        if (index >= s[section].length) {
            throw new EVException(String.format("Error! Byte %d in section %d does not exist (%d)",
                    index, section, s[section].length));
        }

        return s[section][index];
    }

    @Override
    public EVExercise parseExercise(String filename) throws EVException {
        // interval values used by Polar
        short[] interval = {5, 15, 30, 60, 120, 240, 300, 480};

        // read binary file content to array
        fileContent = readFileToByteArray(filename);

        // create an PVExercise object from this data and set file type
        // TODO - support S410 and S520
        EVExercise exercise = new EVExercise(EVExercise.ExerciseFileType.S510RAW);
        exercise.setDeviceName("Polar S4xx/S5xx Series");

        // get bytes in file
        int bytesInFile = (fileContent[1] * 0x100) + fileContent[0];
        if (bytesInFile != fileContent.length) {
            throw new EVException("The exercise file is not valid, the file length is not correct ...");
        }

        // get data bytes
        int bytesInData = bytesInFile - 2;
        int[] data = new int[bytesInData];
        System.arraycopy(fileContent, 2, data, 0, bytesInData);

        // process data
        int ii = 0;
        int sectionIx = 0;
        int sectionsInData = 0;
        s = new int[1][1];

        while (ii < bytesInData) {
            if (sectionIx == 0) {
                // get first section
                if (data[0] == 85) {
                    sectionsInData = data[3];

                    // alloc mem
                    s = new int[sectionsInData + 1][];

                    // ok, first section read, continue
                    sectionIx++;
                    ii += 8;
                    continue;
                } else {
                    throw new EVException("The exercise file is not valid, the first section could not be found");
                }
            } else {
                // find new section
                if (data[ii] == 85) {
                    // check section number
                    if (data[ii + 1] != sectionIx) {
                        throw new EVException("Wrong section index in file");
                    }

                    // allocate memory for this section
                    int sectionLength = data[ii + 2];
                    s[sectionIx] = new int[sectionLength];

                    // set data in sections array (s) 
                    System.arraycopy(data, ii + 3, s[sectionIx], 0, sectionLength);

                    //System.out.format(">>> new section #%d(%d) found at %d with %d bytes\n", 
                    //        sectionIx, sectionsInData, ii, sectionLength);

                    // ok, section read, continue
                    sectionIx++;
                    ii += sectionLength + 5; // +5 -> section header length
                    continue;
                }
            }

            // done. check for no-more-sections byte at the end of the file
            if ((sectionIx - 1) != sectionsInData) {
                throw new EVException("Could not find all sections");
            }
            if (data[ii] != 7) {
                throw new EVException("Could not find no-more-sections byte in file");
            }
            // ok, all set
            break;
        }

        // get the number of samples recorded.  floor of (seconds / hertz).
        // Watch seems to always record a last entry, even partial seconds: +1;
        int numberOfSamples = sdata(1, 0);

        // get recording interval
        int intix = sdata(1, 1) - 95;
        if (intix >= interval.length)
            throw new EVException("Recording interval is not valid ...");
        exercise.setRecordingInterval(interval[intix]);

        // TODO does the heartrate ranges are specified by absolute or percentual values?
        boolean fHeartRateRangeAbsolute = true; //(sdata(1,1) & 0x10) == 0;

        // get exercise type
        byte typeNr = (byte) sdata(1, 2);
        if (typeNr > 0) {
            // get exercise type label
            StringBuilder sbExerciseLabel = new StringBuilder();
            for (int i = 0; i < 7; i++) {
                sbExerciseLabel.append(decodeChar(sdata(1, i + 3)));
            }
            exercise.setSportType(sbExerciseLabel.toString());
        } else {
            exercise.setSportType("BasicUse");
        }

        // get exercise date
        int dateSeconds = decodeBCD(sdata(1, 10));
        int dateMinutes = decodeBCD(sdata(1, 11));
        int dateHours = decodeBCD(sdata(1, 12) & 0x7f);
        int dateDay = decodeBCD(sdata(1, 13) & 0x7f);
        int dateYear = (2000 + sdata(1, 14));
        int dateMonth = sdata(1, 15) & 0x0f;

        // when bit 7 of byte 12 is set, then the hour is in PM mode
        if (dateHours < 12 && (sdata(1, 12) & 0x80) == 0x80) {
            dateHours += 12;
        }

        exercise.setDateTime(LocalDateTime.of(dateYear, dateMonth, dateDay, dateHours, dateMinutes, dateSeconds));

        // get duration
        int durationTenthSeconds = (sdata(1, 15) >> 4);
        int durationSeconds = decodeBCD(sdata(1, 16));
        int durationMinutes = decodeBCD(sdata(1, 17));
        int durationHours = decodeBCD(sdata(1, 18));
        exercise.setDuration((durationHours * 60 * 60 * 10) + (durationMinutes * 60 * 10) + durationSeconds * 10 + durationTenthSeconds);

        // get heartrate data
        exercise.setHeartRateAVG((short) sdata(1, 19));
        exercise.setHeartRateMax((short) sdata(1, 20));

        // get number of laps
        int numberOfMeas = decodeBCD(sdata(1, 21));
        int numberOfLaps = decodeBCD(sdata(1, 22));

        // get unit format from bit 1 of byte 25
        // => 0 = metric, 1 = english
        boolean fMetricUnits = (sdata(1, 25) & 0x02) == 0x00;

        // decode recording mode (heartrate is always recorded)

        boolean fBike2 = (sdata(1, 26) & 0x20) == 0x20;
        boolean fBike1 = (sdata(1, 26) & 0x10) == 0x10;
        RecordingMode recMode = exercise.getRecordingMode();
        recMode.setHeartRate(true);
        recMode.setPower(false);
        recMode.setCadence(false);
        recMode.setAltitude(false);

        if (!fBike1 && !fBike2) {
            recMode.setSpeed(false);
            recMode.setBikeNumber(null);
        } else {
            recMode.setSpeed(true);
            if (fBike1) {
                recMode.setBikeNumber((byte) 1);
            } else {
                recMode.setBikeNumber((byte) 2);
            }
        }

        // if more measurements than laps are taken -> interval training
        recMode.setIntervalExercise(numberOfMeas != numberOfLaps);

        // get the heartrate limit data (Polar S510 has 3 limits)
        int indexHRLimitStart = 28;

        HeartRateLimit hrLimit1 = decodeHeartRateLimit(indexHRLimitStart + 0, indexHRLimitStart + 9);
        exercise.getHeartRateLimits().add(hrLimit1);
        HeartRateLimit hrLimit2 = decodeHeartRateLimit(indexHRLimitStart + 2, indexHRLimitStart + 18);
        exercise.getHeartRateLimits().add(hrLimit2);
        HeartRateLimit hrLimit3 = decodeHeartRateLimit(indexHRLimitStart + 4, indexHRLimitStart + 27);
        exercise.getHeartRateLimits().add(hrLimit3);

        for (HeartRateLimit hrLimit : exercise.getHeartRateLimits()) {
            hrLimit.setAbsoluteRange(fHeartRateRangeAbsolute);
        }

        // get energy (in kCal)
        int energyPart1 = decodeBCD(sdata(1, 69 + 0));
        int energyPart2 = decodeBCD(sdata(1, 69 + 1));
        int energyPart3 = decodeBCD(sdata(1, 69 + 2));
        exercise.setEnergy((energyPart1 + (energyPart2 * 100) + (energyPart3 * 10000)) / 10);

        // get total energy (in kCal)
        int energyTotalPart1 = decodeBCD(sdata(1, 72 + 0));
        int energyTotalPart2 = decodeBCD(sdata(1, 72 + 1));
        int energyTotalPart3 = decodeBCD(sdata(1, 72 + 2));
        exercise.setEnergyTotal(energyTotalPart1 + (energyTotalPart2 * 100) + (energyTotalPart3 * 10000));

        // get cumulative workout time
        int cumWorkoutPart1 = decodeBCD(sdata(1, 75 + 0));
        int cumWorkoutPart2 = decodeBCD(sdata(1, 75 + 1));
        int cumWorkoutPart3 = decodeBCD(sdata(1, 75 + 2));
        exercise.setSumExerciseTime(cumWorkoutPart3 + (cumWorkoutPart1 * 60) + (cumWorkoutPart2 * 60 * 100));

        // get cumulative ride time
        int cumRidePart1 = decodeBCD(sdata(1, 78 + 0));
        int cumRidePart2 = decodeBCD(sdata(1, 78 + 1));
        int cumRidePart3 = decodeBCD(sdata(1, 78 + 2));
        exercise.setSumRideTime(cumRidePart3 + (cumRidePart1 * 60) + (cumRidePart2 * 60 * 100));

        // get odometer
        int odometerPart1 = decodeBCD(sdata(1, 81 + 0));
        int odometerPart2 = decodeBCD(sdata(1, 81 + 1));
        int odometerPart3 = decodeBCD(sdata(1, 81 + 2));
        int odometer = odometerPart1 + (odometerPart2 * 100) + (odometerPart3 * 10000);
        if (fMetricUnits) {
            exercise.setOdometer(odometer);
        } else {
            exercise.setOdometer(ConvertUtils.convertMiles2Kilometer(odometer));
        }

        // get speed (bicycle) related data of exercise (if recorded)
        if (recMode.isSpeed()) {

            // get exercise distance (in 1/10th of km)
            int distance = (sdata(1, 84) + (sdata(1, 85) << 8)) * 100;
            if (!fMetricUnits) {
                distance = ConvertUtils.convertMiles2Kilometer(distance);
            }

            // get AVG speed
            int avgSpeedPart1 = sdata(1, 86);
            int avgSpeedPart2 = (sdata(1, 87) & 0x0f);
            float avgSpeed = ((avgSpeedPart2 << 8) | avgSpeedPart1) / 16f;
            if (!fMetricUnits) {
                avgSpeed = (float) ConvertUtils.convertMiles2Kilometer(avgSpeed);
            }

            // get max speed
            int maxSpeedPart1 = sdata(1, 87) >> 4;
            int maxSpeedPart2 = sdata(1, 88);
            float maxSpeed = ((maxSpeedPart2 << 4) | maxSpeedPart1) / 16f;
            if (!fMetricUnits) {
                maxSpeed = (float) ConvertUtils.convertMiles2Kilometer(maxSpeed);
            }

            exercise.setSpeed(new ExerciseSpeed(avgSpeed, maxSpeed, distance));
        }

        // get cadence (bicycle) data of exercise (if recorded)
        /*
        if (recMode.isCadence ()) {
            ExerciseCadence cadence = new ExerciseCadence ();
            exercise.setCadence (cadence);
            cadence.setCadenceAVG ((short) sdata(1, 89)); // TODO, does this work?
            cadence.setCadenceMax ((short) sdata(1, 90));
        } */

        ////////// decode lap data /////////////

        // calculate lap length (bytes) => depends on what is recorded
        int lapSize = 11;
        if (recMode.isIntervalExercise())
            lapSize += 5;
        if (recMode.isAltitude()) lapSize += 5;
        //if (recMode.isSpeed ()) other size?

        // determine in which section the lap data starts
        int lapsec = 4 + (numberOfSamples - 1) / 60;
        // add offset for speed data
        if (recMode.isSpeed())
            lapsec += 1 + (numberOfSamples - 1) / 60;
        // determine in how _many_ sections the lap data resides
        int lapseccnt = (int) (((double) (numberOfMeas * lapSize) / 60) + .9999);

        // check section size
        if (s[lapsec + lapseccnt - 1].length != (numberOfMeas * lapSize) % 60) {
            throw new EVException(String.format("Lap-data section (%d) has wrong size (%d instead of %d)",
                    lapsec + lapseccnt - 1, s[lapsec + lapseccnt - 1].length, (numberOfMeas * lapSize) % 60));
        }

        // process all laps
        int lapCount = recMode.isIntervalExercise() ? numberOfMeas : numberOfLaps;
        for (int l = 0; l < lapCount; l++) {
            int os = l * lapSize; // data offset 

            // get offset where the current lap starts
            Lap lap = new Lap();
            exercise.getLapList().add(lap);

            // get lap split time (in 1/10th seconds)
            int bLapEndHour = sdata(lapsec, os + 2);
            int bLapEndMinute = sdata(lapsec, os + 1) & 0x3f;
            int bLapEndSecond = sdata(lapsec, os + 0) & 0x3f;
            int bLapEndTenthSecond = 4 * (sdata(lapsec, os + 1) >> 6) + (sdata(lapsec, os + 0) >> 6);
            lap.setTimeSplit(bLapEndTenthSecond + (bLapEndSecond * 10) + (bLapEndMinute * 60 * 10) + (bLapEndHour * 60 * 60 * 10));

            // get heartrate data of lap
            lap.setHeartRateSplit((short) sdata(lapsec, os + 3));
            lap.setHeartRateAVG((short) sdata(lapsec, os + 4));
            lap.setHeartRateMax((short) sdata(lapsec, os + 5));

            // get speed (bicycle) related data of lap (if recorded)
            if (recMode.isSpeed()) {

                // TODO get lap distance (in 1/10th of km)
                int lapDistance = sdata(lapsec, os + 6);
                lapDistance += (sdata(lapsec, os + 7) << 8);
                //lapDistance += (sdata(lapsec, os + 8) << 16); not sure about this byte..
                lapDistance *= 100;
                if (!fMetricUnits) {
                    lapDistance = ConvertUtils.convertMiles2Kilometer(lapDistance);
                }

                // get speed at end of lap
                float lapEndSpeed = sdata(lapsec, os + 9);
                lapEndSpeed += sdata(lapsec, os + 10) << 8;
                lapEndSpeed *= 5.0f / 80;
                if (!fMetricUnits) {
                    lapEndSpeed = (float) ConvertUtils.convertMiles2Kilometer(lapEndSpeed);
                }

                lap.setSpeed(new LapSpeed(lapEndSpeed, 0f, lapDistance, null));
            } // end of if(isSpeed())

            // process exercise interval data | TODO, implement in polarviewer
            // flag stands for (TBC): 1 == warmup, 2 == interval, 3 == cooling down, 0 == basicuse
            //System.out.format("flag: %d\n", sdata(lapsec, os + 15) >> 5); 
            //System.out.format("recovery time: %d [sec]\n", sdata(lapsec, os + 13)*60,  sdata(lapsec, os + 12)); 
            //System.out.format("recovery heart rate: %d [bpm]\n", ( (sdata(lapsec, os+14) < 254 ) ? sdata(lapsec, os+14) : -1)); 
        }

        ////////// decode sample data /////////////

        int hrsec = 3;
        int spdsec = 4 + (numberOfSamples - 1) / 60;

        // create sample list => process all recorded samples
        for (int i = 0; i < numberOfSamples; i++) {
            ExerciseSample exeSample = new ExerciseSample();
            exeSample.setTimestamp(i * exercise.getRecordingInterval() * 1000L);
            exercise.getSampleList().add(exeSample);

            // get sample heartrate
            exeSample.setHeartRate((short) sdata(hrsec, i));

            // get bicycle related data (if recorded)
            if (recMode.isSpeed()) {

                // get sample speed
                float sampleSpeed = (float) (sdata(spdsec, i)) / 2f;
                if (fMetricUnits) {
                    exeSample.setSpeed(sampleSpeed);
                } else {
                    exeSample.setSpeed((float) ConvertUtils.convertMiles2Kilometer(sampleSpeed));
                }
            }
        }

        // when speed is recorded => calculate distance for each recorded sample
        // (distance is not recorded for each sample)
        if (recMode.isSpeed()) {
            // helper for compute sample distance (meters)
            double distanceAccum = 0f;

            // process all recorded samples
            for (ExerciseSample exeSample : exercise.getSampleList()) {
                // compute sample distance (it's not recorded)
                exeSample.setDistance((int) distanceAccum);
                distanceAccum += (exeSample.getSpeed() * exercise.getRecordingInterval()) / 3.6f;
            }
        }

        // repair distance values of samples
        exercise.repairSamples();

        // done :-)
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
    private HeartRateLimit decodeHeartRateLimit(int offsetLimits, int offsetTimes) throws EVException {
        short lowerHeartRate = (short) sdata(1, offsetLimits + 0);
        short upperHeartRate = (short) sdata(1, offsetLimits + 1);

        int hrLimitBelowSecs = decodeBCD(sdata(1, offsetTimes + 0));
        hrLimitBelowSecs += decodeBCD(sdata(1, offsetTimes + 1)) * 60;
        hrLimitBelowSecs += decodeBCD(sdata(1, offsetTimes + 2)) * 60 * 60;

        int hrLimitWithinSecs = decodeBCD(sdata(1, offsetTimes + 3));
        hrLimitWithinSecs += decodeBCD(sdata(1, offsetTimes + 4)) * 60;
        hrLimitWithinSecs += decodeBCD(sdata(1, offsetTimes + 5)) * 60 * 60;

        int hrLimitAboveSecs = decodeBCD(sdata(1, offsetTimes + 6));
        hrLimitAboveSecs += decodeBCD(sdata(1, offsetTimes + 7)) * 60;
        hrLimitAboveSecs += decodeBCD(sdata(1, offsetTimes + 8)) * 60 * 60;

        return new HeartRateLimit(lowerHeartRate, upperHeartRate,
                hrLimitBelowSecs, hrLimitWithinSecs, hrLimitAboveSecs, true);
    }
}
