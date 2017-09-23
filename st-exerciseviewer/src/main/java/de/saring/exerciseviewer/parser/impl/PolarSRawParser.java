package de.saring.exerciseviewer.parser.impl;

import java.time.LocalDateTime;
import java.util.Arrays;

import de.saring.exerciseviewer.core.EVException;
import de.saring.exerciseviewer.data.EVExercise;
import de.saring.exerciseviewer.data.ExerciseAltitude;
import de.saring.exerciseviewer.data.ExerciseCadence;
import de.saring.exerciseviewer.data.ExerciseSample;
import de.saring.exerciseviewer.data.ExerciseSpeed;
import de.saring.exerciseviewer.data.ExerciseTemperature;
import de.saring.exerciseviewer.data.HeartRateLimit;
import de.saring.exerciseviewer.data.Lap;
import de.saring.exerciseviewer.data.LapAltitude;
import de.saring.exerciseviewer.data.LapSpeed;
import de.saring.exerciseviewer.data.LapTemperature;
import de.saring.exerciseviewer.data.RecordingMode;
import de.saring.exerciseviewer.parser.AbstractExerciseParser;
import de.saring.exerciseviewer.parser.ExerciseParserInfo;
import de.saring.util.unitcalc.ConvertUtils;

/**
 * This implementation of an ExerciseParser is for reading RAW files of the
 * Polar S-Series devices. The exercises of these devices are very similar,
 * better models are containing more informations, which can be found on
 * different indices, depending on the model.
 * <br/>
 * Currently the following devices are supported: S610(i), S710(i), S720i
 * and S725. Users of Polar S725 should read the usage informations in
 * README.txt.
 * <br/>
 * Usually the exercise files have the extension ".srd". They can be read
 * from the Polar device with the tool "s710" (available for free at
 * http://daveb.net/s710), which works with Linux and similar systems.
 * For the format description of the RAW files see the README.file_format
 * of the "s710" utility (or in documentation directory).
 *
 * @author Stefan Saring
 * @version 1.0
 */
public class PolarSRawParser extends AbstractExerciseParser {

    /**
     * Informations about this parser.
     */
    private final ExerciseParserInfo info = new ExerciseParserInfo("Polar SRD", Arrays.asList("srd", "SRD"));

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

        // is the SRD file coming from an Polar S610 ?
        // => then we need on many places special handling, because the data is located on other places
        boolean fS610 = (fileContent[34] == 0) && (fileContent[36] == 251);

        // create an PVExercise object from this data and set file type
        EVExercise exercise;
        if (!fS610) {
            exercise = new EVExercise(EVExercise.ExerciseFileType.S710RAW);
        } else {
            exercise = new EVExercise(EVExercise.ExerciseFileType.S610RAW);
        }
        exercise.setDeviceName("Polar S6xx/S7xx Series");

        // get bytes in file
        int bytesInFile = (fileContent[1] * 0x100) + fileContent[0];
        if (bytesInFile != fileContent.length) {
            throw new EVException("The exercise file is not valid, the file length is not correct ...");
        }

        // get exercise type (label)
        StringBuilder sbExerciseLabel = new StringBuilder();
        for (int i = 0; i < 7; i++) {
            sbExerciseLabel.append(decodeChar(fileContent[i + 3]));
        }
        exercise.setType(sbExerciseLabel.toString());

        // get exercise date
        int dateSeconds = decodeBCD(fileContent[10]);
        int dateMinutes = decodeBCD(fileContent[11]);
        int dateHours = decodeBCD(fileContent[12] & 0x7f);
        int dateDay = decodeBCD(fileContent[13] & 0x7f);
        int dateYear = 2000 + decodeBCD(fileContent[14]);
        int dateMonth = fileContent[15] & 0x0f;

        // when bit 7 of byte 12 is set, then the hour is in PM mode
        if (dateHours < 12 && (fileContent[12] & 0x80) == 0x80) {
            dateHours += 12;
        }

        exercise.setDateTime(LocalDateTime.of(dateYear, dateMonth, dateDay, dateHours, dateMinutes, dateSeconds));

        // get duration
        int durationTenthSeconds = (fileContent[15] >> 4);
        int durationSeconds = decodeBCD(fileContent[16]);
        int durationMinutes = decodeBCD(fileContent[17]);
        int durationHours = decodeBCD(fileContent[18]);
        exercise.setDuration((durationHours * 60 * 60 * 10) + (durationMinutes * 60 * 10) + durationSeconds * 10 + durationTenthSeconds);

        // get heartrate data
        exercise.setHeartRateAVG((short) fileContent[19]);
        exercise.setHeartRateMax((short) fileContent[20]);

        // get number of laps
        int numberOfLaps = decodeBCD(fileContent[21]);

        // get unit format from bit 1 of byte 25
        // => 0 = metric, 1 = english
        boolean fMetricUnits = (fileContent[25] & 0x02) == 0x00;

        // decode recording mode (heartrate is always recorded)
        // (not available on S610 files)
        RecordingMode recMode = new RecordingMode();
        exercise.setRecordingMode(recMode);

        if (!fS610) {
            boolean fBike2 = (fileContent[26] & 0x20) == 0x20;
            boolean fBike1 = (fileContent[26] & 0x10) == 0x10;
            recMode.setPower((fileContent[26] & 0x08) == 0x08);
            recMode.setCadence((fileContent[26] & 0x04) == 0x04);
            recMode.setAltitude((fileContent[26] & 0x02) == 0x02);

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
        }

        // get recording interval
        short recInterval = 0;
        int indexRecInt = getProperIndex(27, 26, fS610);
        switch (fileContent[indexRecInt] & 15) {
            case 0:
                recInterval = 5;
                break;
            case 1:
                recInterval = 15;
                break;
            case 2:
                recInterval = 60;
                break;
            default:
                throw new EVException("Recording interval '" + fileContent[indexRecInt] + "' not valid ...");
        }
        exercise.setRecordingInterval(recInterval);

        // does the heartrate ranges are specified by absolute or percentual values?
        // the bit 4 of byte 27 (or 26 for S610) is 0 for absolute and 1 for percentual ranges
        boolean fHeartRateRangeAbsolute = (fileContent[indexRecInt] & 0x10) == 0;

        // get the number of samples recorded.  floor of (seconds / hertz).
        // Watch seems to always record a last entry, even partial seconds: +1;
        int numberOfSamples = ((exercise.getDuration() / 10) / recInterval) + 1;

        // get the heartrate limit data (Polar S710 has 3 limits)
        int indexHRLimitStart = getProperIndex(29, 28, fS610);

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
        int indexEnergyStart = getProperIndex(70, 69, fS610);
        int energyPart1 = decodeBCD(fileContent[indexEnergyStart + 0]);
        int energyPart2 = decodeBCD(fileContent[indexEnergyStart + 1]);
        int energyPart3 = decodeBCD(fileContent[indexEnergyStart + 2]);
        exercise.setEnergy((energyPart1 + (energyPart2 * 100) + (energyPart3 * 10000)) / 10);

        // get total energy (in kCal)
        int indexTotalEnergyStart = getProperIndex(73, 72, fS610);
        int energyTotalPart1 = decodeBCD(fileContent[indexTotalEnergyStart + 0]);
        int energyTotalPart2 = decodeBCD(fileContent[indexTotalEnergyStart + 1]);
        int energyTotalPart3 = decodeBCD(fileContent[indexTotalEnergyStart + 2]);
        exercise.setEnergyTotal(energyTotalPart1 + (energyTotalPart2 * 100) + (energyTotalPart3 * 10000));

        // get cumulative workout time
        int indexCumWorkoutStart = getProperIndex(76, 75, fS610);
        int cumWorkoutPart1 = decodeBCD(fileContent[indexCumWorkoutStart + 0]);
        int cumWorkoutPart2 = decodeBCD(fileContent[indexCumWorkoutStart + 1]);
        int cumWorkoutPart3 = decodeBCD(fileContent[indexCumWorkoutStart + 2]);
        exercise.setSumExerciseTime(cumWorkoutPart3 + (cumWorkoutPart1 * 60) + (cumWorkoutPart2 * 60 * 100));

        if (!fS610) {
            // get cumulative ride time
            int cumRidePart1 = decodeBCD(fileContent[79]);
            int cumRidePart2 = decodeBCD(fileContent[80]);
            int cumRidePart3 = decodeBCD(fileContent[81]);
            exercise.setSumRideTime(cumRidePart3 + (cumRidePart1 * 60) + (cumRidePart2 * 60 * 100));

            // get odometer
            int odometerPart1 = decodeBCD(fileContent[82]);
            int odometerPart2 = decodeBCD(fileContent[83]);
            int odometerPart3 = decodeBCD(fileContent[84]);
            int odometer = odometerPart1 + (odometerPart2 * 100) + (odometerPart3 * 10000);
            if (fMetricUnits) {
                exercise.setOdometer(odometer);
            } else {
                exercise.setOdometer(ConvertUtils.convertMiles2Kilometer(odometer));
            }
        }

        // get speed (bicycle) related data of exercise (if recorded)
        if (recMode.isSpeed()) {

            // get exercise distance (in 1/10th of km)
            int distance = (fileContent[85] + (fileContent[86] << 8)) * 100;
            if (!fMetricUnits) {
                distance = ConvertUtils.convertMiles2Kilometer(distance);
            }

            // get AVG speed
            int avgSpeedPart1 = fileContent[87];
            int avgSpeedPart2 = (fileContent[88] & 0x0f);
            float avgSpeed = ((avgSpeedPart2 << 8) | avgSpeedPart1) / 16f;
            if (!fMetricUnits) {
                avgSpeed = (float) ConvertUtils.convertMiles2Kilometer(avgSpeed);
            }

            // get max speed
            int maxSpeedPart1 = fileContent[88] >> 4;
            int maxSpeedPart2 = fileContent[89];
            float maxSpeed = ((maxSpeedPart2 << 4) | maxSpeedPart1) / 16f;
            if (!fMetricUnits) {
                maxSpeed = (float) ConvertUtils.convertMiles2Kilometer(maxSpeed);
            }

            exercise.setSpeed(new ExerciseSpeed(avgSpeed, maxSpeed, distance));
        }

        // get cadence (bicycle) data of exercise (if recorded)
        if (recMode.isCadence()) {
            short cadenceAvg = (short) fileContent[90];
            short cadenceMax = (short) fileContent[91];
            exercise.setCadence(new ExerciseCadence(cadenceAvg, cadenceMax));
        }

        // get altitude data of exercise (if recorded)
        if (recMode.isAltitude()) {
            short altitudeMin = decodeAltitude(fileContent[92], fileContent[93]);
            short altitudeAvg = decodeAltitude(fileContent[94], fileContent[95]);
            short altitudeMax = decodeAltitude(fileContent[96], fileContent[97]);
            int ascent = fileContent[101] + (fileContent[102] << 8);

            if (!fMetricUnits) {
                altitudeMin = (short) ConvertUtils.convertFeet2Meter(altitudeMin);
                altitudeAvg = (short) ConvertUtils.convertFeet2Meter(altitudeAvg);
                altitudeMax = (short) ConvertUtils.convertFeet2Meter(altitudeMax);
                ascent = (short) ConvertUtils.convertFeet2Meter(ascent);
            }

            exercise.setAltitude(new ExerciseAltitude(altitudeMin, altitudeAvg, altitudeMax, ascent));

            // get temperature data of exercise (only available, when altitude recorded)
            short temperatureMin = decodeTemperature(fileContent[98], fMetricUnits);
            short temperatureAvg = decodeTemperature(fileContent[99], fMetricUnits);
            short temperatureMax = decodeTemperature(fileContent[100], fMetricUnits);
            exercise.setTemperature(new ExerciseTemperature(temperatureMin, temperatureAvg, temperatureMax));
        }


        ////////// decode lap data /////////////

        // calculate lap length (bytes) => depends on what is recorded
        int lapSize = 6;
        if (recMode.isAltitude()) lapSize += 5;
        if (recMode.isSpeed()) {
            if (recMode.isCadence()) lapSize += 1;
            if (recMode.isPower()) lapSize += 4;
            lapSize += 4;
        }

        // calculate sample length (bytes) => depends on what is recorded
        // We do this before we calculate indexLapsStart, because
        // we need it.
        int sampleSize = 1;
        if (recMode.isAltitude()) sampleSize += 2;
        if (recMode.isSpeed()) {
            if (recMode.isAltitude()) sampleSize -= 1;
            if (recMode.isCadence()) sampleSize += 1;
            if (recMode.isPower()) sampleSize += 4;
            sampleSize += 2;
        }

        // We need to calculate where the lap data starts.
        // That way, we avoid guessing different Polar models.
        int totalLapSize = numberOfLaps * lapSize;
        int totalSampleSize = numberOfSamples * sampleSize;
        int indexLapsStart = bytesInFile - totalLapSize - totalSampleSize;

        // process all laps

        for (int i = 0; i < numberOfLaps; i++) {
            // get offset where the current lap starts
            int lapOffset = indexLapsStart + i * lapSize;
            Lap lap = new Lap();
            exercise.getLapList().add(lap);

            // get lap split time (in 1/10th seconds)
            int bLapEndHour = fileContent[lapOffset + 2];
            int bLapEndMinute = fileContent[lapOffset + 1] & 0x3f;
            int bLapEndSecond = fileContent[lapOffset] & 0x3f;
            int bLapEndTenthSecond = ((fileContent[lapOffset + 1] & 0xc0) >> 4) | ((fileContent[lapOffset] & 0xc0) >> 6);
            lap.setTimeSplit(bLapEndTenthSecond + (bLapEndSecond * 10) + (bLapEndMinute * 60 * 10) + (bLapEndHour * 60 * 60 * 10));

            // get heartrate data of lap
            lap.setHeartRateSplit((short) (fileContent[lapOffset + 3]));
            lap.setHeartRateAVG((short) (fileContent[lapOffset + 4]));
            lap.setHeartRateMax((short) (fileContent[lapOffset + 5]));
            lapOffset += 6;

            // get altitude related data of lap (if recorded)
            if (recMode.isAltitude()) {

                // get altitude at end of the lap (has on offset of 512)
                short lapEndAltitude = (short) (fileContent[lapOffset] + (fileContent[lapOffset + 1] << 8) - 512);
                if (!fMetricUnits) {
                    // english units: multiples of 5 feets
                    lapEndAltitude = (short) ConvertUtils.convertFeet2Meter(lapEndAltitude * 5);
                }

                // get ascent of the lap
                int lapAscent = (fileContent[lapOffset + 2] + (fileContent[lapOffset + 3] << 8));
                if (!fMetricUnits) {
                    lapAscent = ConvertUtils.convertFeet2Meter(lapAscent);
                }

                lap.setAltitude(new LapAltitude(lapEndAltitude, lapAscent));


                // get temperature at end of the lap
                short lapTemperature;
                if (fMetricUnits) {
                    // metric units: offset from -10 C
                    lapTemperature = (short) (fileContent[lapOffset + 4] - 10);
                } else {
                    // english units: offset from 14 F
                    lapTemperature = ConvertUtils.convertFahrenheit2Celsius((short) (fileContent[lapOffset + 4] + 14));
                }
                lap.setTemperature(new LapTemperature(lapTemperature));

                lapOffset += 5;
            }

            // get speed (bicycle) related data of lap (if recorded)
            if (recMode.isSpeed()) {

                // get cadence at end of the lap (if recorded)
                Short lapCadence = null;
                if (recMode.isCadence()) {
                    lapCadence = (short) fileContent[lapOffset];
                    lapOffset += 1;
                }

                // ignore power data, but increase offset if recorded
                if (recMode.isPower()) {
                    lapOffset += 4;
                }

                // get lap distance (in 1/10th of km)
                int lapDistance = (fileContent[lapOffset] + (fileContent[lapOffset + 1] << 8)) * 100;
                if (!fMetricUnits) {
                    lapDistance = ConvertUtils.convertMiles2Kilometer(lapDistance);
                }

                // get lap speed
                float lapEndSpeed = ((float) (fileContent[lapOffset + 2] + ((fileContent[lapOffset + 3] & 0xf0) << 4)) / 16);
                if (!fMetricUnits) {
                    lapEndSpeed = (float) ConvertUtils.convertMiles2Kilometer(lapEndSpeed);
                }

                lap.setSpeed(new LapSpeed(lapEndSpeed, 0f, lapDistance, lapCadence));
                lapOffset += 4;
            }
        }


        ////////// decode sample data /////////////

        // get offset of first sample (the first sample is most recent - reverse order)
        int sampleOffset = indexLapsStart + (numberOfLaps * lapSize);

        // create sample list

        // process all recorded samples
        for (int i = 0; i < numberOfSamples; i++) {
            // store sample in list in reverse order (insert new samples at the list start)
            int sampleIndex = numberOfSamples - i - 1;
            ExerciseSample exeSample = new ExerciseSample();
            exeSample.setTimestamp(sampleIndex * exercise.getRecordingInterval() * 1000L);
            exercise.getSampleList().add(0, exeSample);

            // get sample heartrate
            exeSample.setHeartRate((short) fileContent[sampleOffset]);
            sampleOffset++;

            // get sample altitude (if recorded) - (has on offset of 512)
            if (recMode.isAltitude()) {
                short sampleAltitude = (short) (fileContent[sampleOffset] + ((fileContent[sampleOffset + 1] & 0x1f) << 8) - 512);
                if (fMetricUnits) {
                    // metric units: meters without modification
                    exeSample.setAltitude(sampleAltitude);
                } else {
                    // english units: multiples of 5 feets
                    exeSample.setAltitude((short) ConvertUtils.convertFeet2Meter(sampleAltitude * 5));
                }
                sampleOffset += 2;
            }

            // get bicycle related data (if recorded)
            if (recMode.isSpeed()) {
                // when altitude is always recorded, use last byte again
                if (recMode.isAltitude()) {
                    sampleOffset -= 1;
                }

                // get sample speed
                float sampleSpeed = (float) (((fileContent[sampleOffset] & 0xe0) << 3) + fileContent[sampleOffset + 1]) / 16f;
                if (fMetricUnits) {
                    exeSample.setSpeed(sampleSpeed);
                } else {
                    exeSample.setSpeed((float) ConvertUtils.convertMiles2Kilometer(sampleSpeed));
                }
                sampleOffset += 2;

                // ignore sample power, but add offset (if recorded)
                if (recMode.isPower()) {
                    sampleOffset += 4;
                }

                // get sample cadence (if recorded)
                if (recMode.isCadence()) {
                    exeSample.setCadence((short) fileContent[sampleOffset]);
                    sampleOffset++;
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
                distanceAccum += (exeSample.getSpeed() * recInterval) / 3.6f;
            }
        }

        // repair distance values of samples
        exercise.repairSamples();

        // calculate average lap speed, the data was not recorded here
        calculateAverageLapSpeed(exercise);

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
        char cDecoded = '?';

        switch (value) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
                cDecoded = (char) ('0' + value);
                break;
            case 10:
                cDecoded = ' ';
                break;
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
                cDecoded = (char) ('A' + value - 11);
                break;
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
            case 46:
            case 47:
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
                cDecoded = (char) ('a' + value - 37);
                break;
            case 63:
                cDecoded = '-';
                break;
            case 64:
                cDecoded = '%';
                break;
            case 65:
                cDecoded = '/';
                break;
            case 66:
                cDecoded = '(';
                break;
            case 67:
                cDecoded = ')';
                break;
            case 68:
                cDecoded = '*';
                break;
            case 69:
                cDecoded = '+';
                break;
            case 70:
                cDecoded = '.';
                break;
            case 71:
                cDecoded = ':';
                break;
            case 72:
                cDecoded = '?';
                break;
            default:
                break;
        }

        return cDecoded;
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
     * This helper method is only for easy switching between the S710 and S610
     * exercise indexes while parsing.
     *
     * @param indexS710 the index in S710 exercise files
     * @param indexS610 the index in S610 exercise files
     * @param fS610 true, when the exercise is from S610, false when from S710
     * @return the index depending on the device
     */
    private int getProperIndex(int indexS710, int indexS610, boolean fS610) {
        if (fS610) {
            return indexS610;
        } else {
            return indexS710;
        }
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

        int hrLimitBelowSecs = decodeBCD(fileContent[offsetTimes + 0]);
        hrLimitBelowSecs += decodeBCD(fileContent[offsetTimes + 1]) * 60;
        hrLimitBelowSecs += decodeBCD(fileContent[offsetTimes + 2]) * 60 * 60;

        int hrLimitWithinSecs = decodeBCD(fileContent[offsetTimes + 3]);
        hrLimitWithinSecs += decodeBCD(fileContent[offsetTimes + 4]) * 60;
        hrLimitWithinSecs += decodeBCD(fileContent[offsetTimes + 5]) * 60 * 60;

        int hrLimitAboveSecs = decodeBCD(fileContent[offsetTimes + 6]);
        hrLimitAboveSecs += decodeBCD(fileContent[offsetTimes + 7]) * 60;
        hrLimitAboveSecs += decodeBCD(fileContent[offsetTimes + 8]) * 60 * 60;

        return new HeartRateLimit(lowerHeartRate, upperHeartRate,
                hrLimitBelowSecs, hrLimitWithinSecs, hrLimitAboveSecs, true);
    }

    /**
     * This method decodes an altitude information from the specified high and
     * low "byte" values.
     *
     * @param lsb lower "byte" value
     * @param msb higher "byte" value
     * @return the decoded altitude value
     */
    private short decodeAltitude(int lsb, int msb) {
        short altitude = (short) (lsb + ((msb & 0x7f) << 8));
        if ((msb & 0x80) != 0x80) {
            altitude *= -1;
        }
        return altitude;
    }

    /**
     * This method decodes a temperature information from the specified "byte".
     * The result is in celsius or fahreinheit depending on the specified unit flag.
     *
     * @param temperature the encoded temperature "byte"
     * @param fMetricUnits true, when exercise was recorded in metric untis (false = english units)
     * @return the decoded temperature value
     */
    private short decodeTemperature(int temperature, boolean fMetricUnits) {
        if (fMetricUnits) {
            // metric units: -79..+79, 8th bit 1/0 = +/-
            short tempValue = (byte) (temperature & 0x7f);
            if ((temperature & 0x80) != 0x80) {
                tempValue *= -1;
            }
            return tempValue;
        } else {
            // english units: temperature in degrees F, binary
            return ConvertUtils.convertFahrenheit2Celsius((short) temperature);
        }
    }
}
