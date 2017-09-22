package de.saring.exerciseviewer.parser.impl

import de.saring.exerciseviewer.core.EVException
import de.saring.exerciseviewer.data.EVExercise
import de.saring.exerciseviewer.data.ExerciseAltitude
import de.saring.exerciseviewer.data.ExerciseCadence
import de.saring.exerciseviewer.data.ExerciseSample
import de.saring.exerciseviewer.data.ExerciseSpeed
import de.saring.exerciseviewer.data.HeartRateLimit
import de.saring.exerciseviewer.data.Lap
import de.saring.exerciseviewer.data.LapAltitude
import de.saring.exerciseviewer.data.LapSpeed
import de.saring.exerciseviewer.data.LapTemperature
import de.saring.exerciseviewer.data.RecordingMode
import de.saring.exerciseviewer.parser.AbstractExerciseParser
import de.saring.exerciseviewer.parser.ExerciseParserInfo
import de.saring.util.unitcalc.ConvertUtils
import java.io.File
import java.time.LocalDateTime

/**
 * This implementation of an ExerciseParser is for reading the HRM files of Polar heartrate monitors. These files have
 * the extension ".hrm". They can be read from the Polar device using the software shipped with most Polar devices.
 * The format description is taken from the file "Polar_HRM2_file_format.pdf".
 *
 * @author Stefan Saring
 */
class PolarHRMParser : AbstractExerciseParser() {

    /** Information about this parser. */
    private val parserInfo = ExerciseParserInfo("Polar HRM", listOf("hrm", "HRM"))

    override
    val info:ExerciseParserInfo = parserInfo

    override
    fun parseExercise(filename: String): EVExercise {

        try {
            val fileContent = File(filename).readLines()
            return parseExerciseFromContent(fileContent)
        } catch (e: Exception) {
            throw EVException("Failed to read the HRM exercise file '$filename' ...", e)
        }
    }

    private fun parseExerciseFromContent(fileContent: List<String>): EVExercise
    {
        // parse basic exercise data
        val exercise = EVExercise(EVExercise.ExerciseFileType.HRM)
        exercise.deviceName = "Polar HRM"

        // parse exercise file blocks
        val fMetricUnits = parseBlockParams(fileContent, exercise)
        parseBlockIntTimes(fileContent, exercise, fMetricUnits)
        parseBlockSummaryTimes(fileContent, exercise)
        // ignore 'Summary-TH', 'HRZones' and 'SwapTimes' block
        parseBlockTrip(fileContent, exercise, fMetricUnits)
        parseBlockHrData(fileContent, exercise, fMetricUnits)

        // calculate average lap speed, the data was not recorded here
        calculateAverageLapSpeed(exercise)

        return exercise
    }

    /**
     * Parses the 'Params' block of the exercise file.
     *
     * @param fileContent all exercise file lines
     * @param exercise the created exercise
     * @return flag whether the exercise file uses metric (true) or english (false) units
     */
    private fun parseBlockParams(fileContent: List<String>, exercise: EVExercise): Boolean {

        // get lines of 'Params' block
        val lParamsBlock = getBlockLines(fileContent, "Params", true)

        // check HRM file version
        val strVersion = getValueFromBlock(lParamsBlock, "Version")
        if (!strVersion.equals("106") && !strVersion.equals("107")) {
            throw EVException("Failed to read HRM file, the version needs to be '106' or '107'!")
        }

        // parse recording mode informations
        // (since Polar S720 the length can be 9 instead of 8, althought the HRM version is still 1.06)
        val strSMode = getValueFromBlock(lParamsBlock, "SMode")
        exercise.recordingMode = RecordingMode()
        exercise.recordingMode.isSpeed = strSMode[0] == '1'
        exercise.recordingMode.isCadence = strSMode[1] == '1'
        exercise.recordingMode.isAltitude = strSMode[2] == '1'
        exercise.recordingMode.isPower = strSMode[3] == '1'

        if (exercise.recordingMode.isSpeed) {
            exercise.speed = ExerciseSpeed(0f, 0f, 0)
        }

        // does the HRM file uses metric or english units ?
        val fMetricUnits = strSMode[7] == '0'

        // parse exercise dateTime (yyyymmdd)
        val strDate = getValueFromBlock(lParamsBlock, "Date")
        val exeYear = strDate.substring(0, 4).toInt()
        val exeMonth = strDate.substring(4, 6).toInt()
        val exeDay = strDate.substring(6, 8).toInt()

        // parse exercise start time (can be either h:mm:ss.d or hh:mm:ss.d !)
        val strStartTime = getValueFromBlock(lParamsBlock, "StartTime")
        val startTimeSplitted = strStartTime.split(":", ".")
        if (startTimeSplitted.size != 4) {
            throw EVException("Failed to read HRM file, can't parse exercise start time (wrong format)!");
        }

        // parse start time (the 1/10th second part will be ignored)
        val exeHour = startTimeSplitted[0].toInt()
        val exeMinute = startTimeSplitted[1].toInt()
        val exeSecond = startTimeSplitted[2].toInt()
        exercise.dateTime = LocalDateTime.of(exeYear, exeMonth, exeDay, exeHour, exeMinute, exeSecond)

        // parse exercise duration (can be either h:mm:ss.d or hh:mm:ss.d !)
        val strDuration = getValueFromBlock(lParamsBlock, "Length")
        val durationSplitted = strDuration.split(":", ".")
        if (durationSplitted.size != 4) {
            throw EVException("Failed to read HRM file, can't parse exercise duration (wrong format)!")
        }

        // parse start time (the 1/10th second part will be ignored)
        val durHour = durationSplitted[0].toInt()
        val durMinute = durationSplitted[1].toInt()
        val durSecond = durationSplitted[2].toInt()
        val durTenthOfSecond = durationSplitted[3].toInt()
        exercise.duration = (durHour * 60 * 60 * 10) + (durMinute * 60 * 10) + durSecond * 10 + durTenthOfSecond

        // parse interval
        val strInterval = getValueFromBlock(lParamsBlock, "Interval")
        exercise.recordingInterval = strInterval.toShort()

        // ignore Upper1, Lower1, ... Lower3, they're again in block Summary-123
        // ignore Timer1,Timer2,Timer3, ActiveLimit, MaxHR, RestHR, StartDelay, VO2max, Weight
        return fMetricUnits
    }

    /**
     * Parses the 'IntTimes' block of the exercise file, which contains the lap information.
     *
     * @param fileContent all exercise file lines
     * @param exercise the created exercise
     * @param fMetricUnits flag whether the exercise file uses metric (true) or english (false) units
     */
    private fun  parseBlockIntTimes(fileContent: List<String>, exercise: EVExercise, fMetricUnits: Boolean) {
        //////////////////////////////////////////////////////////////////////
        // parse 'IntTimes' block (Lap times)

        // get lines of 'IntTimes' block (can be empty when 0 laps, e.g. for Polar S510)
        val lIntTimesBlock = getBlockLines(fileContent, "IntTimes", false)
        if (lIntTimesBlock.size % 5 != 0) {
            throw EVException("Failed to read HRM file, invalid number of lines in block 'IntTimes'!")
        }

        // parse all laps of exercise (each lap consists of 5 lines)
        val numberOfLaps = lIntTimesBlock.size / 5
        var lapDistanceAccumulated = 0

        for (i in 0..(numberOfLaps - 1)) {
            val lap = Lap()
            exercise.lapList.add(lap)

            // 1. lap line needs to be of 5 parts
            var currLapLineSplitted = lIntTimesBlock[i * 5].split("\t")
            if (currLapLineSplitted.size != 5) {
                throw EVException("Failed to read HRM file, can't parse 1. line of current lap in block 'IntTimes'!")
            }

            // parse lap split time (1. part) (can be either h:mm:ss.d or hh:mm:ss.d !)
            val lapSplitTimeSplitted = currLapLineSplitted[0].split(":", ".")
            if (lapSplitTimeSplitted.size != 4) {
                throw EVException("Failed to read HRM file, can't parse lap split time of current lap (wrong format)!")
            }

            val lapSplitTimeHour = lapSplitTimeSplitted[0].toInt()
            val lapSplitTimeMinute = lapSplitTimeSplitted[1].toInt()
            val lapSplitTimeSecond = lapSplitTimeSplitted[2].toInt()
            val lapSplitTimeTenthSecond = lapSplitTimeSplitted[3].toInt()
            lap.timeSplit = (lapSplitTimeHour * 60 * 60 * 10) + (lapSplitTimeMinute * 60 * 10) + (lapSplitTimeSecond * 10) + lapSplitTimeTenthSecond

            // get lap heartrate values (the other parts of the first line)
            lap.heartRateSplit = currLapLineSplitted[1].toShort()
            // minimum lap heartrate will be ignored
            lap.heartRateAVG = currLapLineSplitted[3].toShort()
            lap.heartRateMax = currLapLineSplitted[4].toShort()

            // parse 2. lap line (needs to be of 6 parts)
            currLapLineSplitted = lIntTimesBlock[(i * 5) + 1].split("\t")
            if (currLapLineSplitted.size != 6) {
                throw EVException("Failed to read HRM file, can't parse 2. line of current lap in block 'IntTimes'!")
            }

            // parse speed and cadence at lap split
            if (exercise.recordingMode.isSpeed) {
                var lapSpeed = currLapLineSplitted[3].toInt() / 10.0
                if (!fMetricUnits) {
                    lapSpeed = ConvertUtils.convertMiles2Kilometer(lapSpeed)
                }

                val lapSpeedCadence = if (exercise.recordingMode.isCadence)
                    currLapLineSplitted[4].toShort() else null
                lap.speed = LapSpeed(lapSpeed.toFloat(), 0f, 0, lapSpeedCadence)
            }

            // parse altitude at lap split
            if (exercise.recordingMode.isAltitude) {
                var lapAltitude = currLapLineSplitted[5].toInt()
                if (!fMetricUnits) {
                    lapAltitude = ConvertUtils.convertFeet2Meter(lapAltitude)
                }

                // lap ascent can't be read from HRM files
                lap.altitude = LapAltitude(lapAltitude.toShort(), 0)
            }

            // the 3. lap line can be ignored completely

            // parse 4. lap line (needs to be of 6 parts)
            currLapLineSplitted = lIntTimesBlock[(i * 5) + 3].split("\t")
            if (currLapLineSplitted.size != 6) {
                throw EVException("Failed to read HRM file, can't parse 4. line of current lap in block 'IntTimes'!")
            }

            // get lap distance
            if (exercise.recordingMode.isSpeed) {
                val lapDistance = currLapLineSplitted[1].toInt()
                if (!fMetricUnits) {
                    // TODO: is not consistent for english units -
                    // documentation says, it's in yards => so meters = yards * 0.9144),
                    // but then accumulation does not work
                }

                // distance needs to be accumulated
                lapDistanceAccumulated += lapDistance
                lap.speed!!.distance = lapDistanceAccumulated
            }

            // get lap temperature
            if (exercise.recordingMode.isAltitude) {

                val lapTemperature = if (fMetricUnits) {
                    // temperature is C / 10
                    val lapTemperature = currLapLineSplitted[3].toInt()
                    (lapTemperature / 10).toShort()
                } else {
                    // temperature is Fahreinheit / 10
                    val lapTemperature = currLapLineSplitted[3].toInt() / 10
                    ConvertUtils.convertFahrenheit2Celsius(lapTemperature.toShort())
                }

                lap.temperature = LapTemperature(lapTemperature)
            }

            // the 5. lap line can be ignored completely
        }
    }

    /**
     * Parses the 'Summary-123' block of the exercise file, which contains the heart rate range information.
     *
     * @param fileContent all exercise file lines
     * @param exercise the created exercise
     */
    private fun parseBlockSummaryTimes(fileContent: List<String>, exercise: EVExercise) {

        // get lines of 'Summary-123' block
        // (mostly 7 lines, 8 lines for Polar CS600, data of last line is unknown)
        // (HRM export of Polar RCX3 does contain 6 lines only)
        val lSummary123Block = getBlockLines(fileContent, "Summary-123", true)
        if (lSummary123Block.size < 6) {
            throw EVException("Failed to read HRM file, can't find block 'Summary-123' or block is not valid!")
        }

        // parse data for 3 heartrate limit ranges
        // TODO: second and third HR ranges does not have sensefull content
        for (i in 0..(3-1)) {

            // 1. heratrate limits info line needs to be of 6 parts
            val firstHRLLineSplitted = lSummary123Block[i * 2].split("\t")
            if (firstHRLLineSplitted.size != 6) {
                throw EVException("Failed to read HRM file, can't parse 1. line of current heartrate limits in block 'Summary-123'!")
            }

            // get seconds below, within and above current heartrate range
            val timeAbove = firstHRLLineSplitted[2].toInt()
            val timeWithin = firstHRLLineSplitted[3].toInt()
            val timeBelow = firstHRLLineSplitted[4].toInt()

            // 2. heratrate limits info line needs to be of 4 parts
            val secondHRLLineSplitted = lSummary123Block[(i * 2) + 1].split("\t")
            if (secondHRLLineSplitted.size != 4) {
                throw EVException("Failed to read HRM file, can't parse 2. line of current heartrate limits in block 'Summary-123'!")
            }

            val upperHeartRate = secondHRLLineSplitted[1].toShort()
            val lowerHeartRate = secondHRLLineSplitted[2].toShort()

            exercise.heartRateLimits.add(HeartRateLimit(lowerHeartRate, upperHeartRate, timeBelow, timeWithin, timeAbove))

            // TODO: When the monitor displays heartrate and ranges in percent instead in bpm
            // the heartrate limit ranges in the HRM files are also stored in percent. But it's
            // not possible yet to determine whether it's bpm (default) or percent. That's
            // why the parses always assumes bpm values.
            //
            // if ('are values stored in percent instead of bpm') {
            //     // => calculate the BPM with help of max. heartrate => this should work ...
            //     val maxHR = secondHRLLineSplitted[0].toInt()
            //     exercise.heartRateLimits[i].lowerHeartRate = (maxHR * exercise.heartRateLimits[i].lowerHeartRate) / 100f
            //     exercise.heartRateLimits[i].upperHeartRate = (maxHR * exercise.heartRateLimits[i].upperHeartRate) / 100f
            // }
        }
    }

    /**
     * Parses the 'Trip' block of the exercise file, which contains the speed and altitude information. This block is
     * not contained in all files (e.g. missing on S410 or S610).
     *
     * @param fileContent all exercise file lines
     * @param exercise the created exercise
     * @param fMetricUnits flag whether the exercise file uses metric (true) or english (false) units
     */
    private fun parseBlockTrip(fileContent: List<String>, exercise: EVExercise, fMetricUnits: Boolean) {

        // get lines of 'Trip' block
        val lTripBlock = getBlockLines(fileContent, "Trip", false)
        if (lTripBlock.size == 8) {
            // parse speed informations
            if (exercise.recordingMode.isSpeed) {
                exercise.speed!!.distance = lTripBlock[0].toInt() * 100
                exercise.speed!!.speedAvg = lTripBlock[5].toInt() / 128f
                // ignore maximum speed data, it is often wrong for many Polar models (will be calculated later)

                if (!fMetricUnits) {
                    exercise.speed!!.distance = ConvertUtils.convertMiles2Kilometer(exercise.speed!!.distance)
                    exercise.speed!!.speedAvg = ConvertUtils.convertMiles2Kilometer(exercise.speed!!.speedAvg.toDouble()).toFloat()
                }

                // now we have the exercise distance, that's why the lap distances needs to be corrected
                // (in HRM file format is an error, the distances of the last laps are often greater then
                // the complete exercise distance, so the greater values needs to be set to exercise distance)
                for (lap in exercise.lapList) {
                    lap.speed!!.distance = Math.min(lap.speed!!.distance, exercise.speed!!.distance)
                }
            }

            // parse altitude informations
            if (exercise.recordingMode.isAltitude) {
                var ascent = lTripBlock[1].toInt()
                // minimum exercise altitude is not available in HRM files
                var altitudeAvg = lTripBlock[3].toInt()
                var altitudeMax = lTripBlock[4].toInt()

                if (!fMetricUnits) {
                    ascent = ConvertUtils.convertFeet2Meter(ascent)
                    altitudeAvg = ConvertUtils.convertFeet2Meter(altitudeAvg)
                    altitudeMax = ConvertUtils.convertFeet2Meter(altitudeMax)
                }

                exercise.altitude = ExerciseAltitude(0, altitudeAvg.toShort(), altitudeMax.toShort(), ascent)
            }

            // parse odometer value
            exercise.odometer = lTripBlock[7].toInt()
            if (!fMetricUnits) {
                exercise.odometer = ConvertUtils.convertMiles2Kilometer(exercise.odometer!!)
            }
        }
    }

    /**
     * Parses the 'HRData' block of the exercise file, which contains the exercise sample information.
     *
     * @param fileContent all exercise file lines
     * @param exercise the created exercise
     * @param fMetricUnits flag whether the exercise file uses metric (true) or english (false) units
     */
    private fun parseBlockHrData(fileContent: List<String>, exercise: EVExercise, fMetricUnits: Boolean) {

        // get lines of 'HRData' block
        val lHRDataBlock = getBlockLines(fileContent, "HRData", true)
        val sampleCount = lHRDataBlock.size

        // parse each exercise sample line
        for (i in 0..(sampleCount - 1)) {
            var tokenIndex = 0
            val sample = ExerciseSample()
            exercise.sampleList.add(sample)
            sample.timestamp = i * exercise.recordingInterval!! * 1000L

            // split sample line into parts
            val currSampleSplitted = lHRDataBlock[i].split("\t")

            // 1. part is heartrate
            sample.heartRate = currSampleSplitted[tokenIndex].toShort()
            tokenIndex++

            // next part can be speed, when recorded
            if (currSampleSplitted.size > tokenIndex && exercise.recordingMode.isSpeed) {
                // speed is km/h or m/h * 10
                var speedX10 = currSampleSplitted[tokenIndex].toInt()
                if (!fMetricUnits) {
                    speedX10 = ConvertUtils.convertMiles2Kilometer(speedX10)
                }

                sample.speed = speedX10 / 10f
                tokenIndex++
            }

            // next part can be cadence, when recorded
            if (currSampleSplitted.size > tokenIndex && exercise.recordingMode.isCadence) {
                sample.cadence = currSampleSplitted[tokenIndex].toShort()
                tokenIndex++
            }

            // next part can be altitude, when recorded
            if (currSampleSplitted.size > tokenIndex && exercise.recordingMode.isAltitude) {
                var altitude = currSampleSplitted[tokenIndex].toInt()
                if (!fMetricUnits) {
                    altitude = ConvertUtils.convertFeet2Meter(altitude)
                }

                sample.altitude = altitude.toShort()
            }
        }

        // when speed is recorded:
        // - calculate distance for each recorded sample (distance is not recorded for each sample)
        // - find the maximum speed from samples (max speed is stored in HRM files, but often a wrong value)
        if (exercise.recordingMode.isSpeed) {
            var distanceAccum = 0.0
            exercise.speed!!.speedMax = 0f

            for (sample in exercise.sampleList) {
                val sampleSpeed = sample.speed ?: 0f
                sample.distance = distanceAccum.toInt()
                distanceAccum += (sampleSpeed * exercise.recordingInterval!!) / 3.6
                exercise.speed!!.speedMax = Math.max(sampleSpeed, exercise.speed!!.speedMax)
            }
        }

        // compute average/maximum heartrate of exercise (not in HRM file)
        var avgHeartrateSum = 0
        exercise.heartRateMax = 0

        for (sample in exercise.sampleList) {
            val sampleHeartRate = sample.heartRate ?: 0
            avgHeartrateSum += sampleHeartRate
            exercise.heartRateMax = maxShort(sampleHeartRate, exercise.heartRateMax ?: 0)
        }

        // calculate AVG heartrate
        exercise.heartRateAVG = Math.round(avgHeartrateSum / exercise.sampleList.size.toDouble()).toShort()

        // when altitude is recorded => search minimum altitude of exercise (is not in HRM file)
        if (exercise.recordingMode.isAltitude) {
            exercise.altitude!!.altitudeMin = Short.MAX_VALUE

            for (sample in exercise.sampleList) {
                exercise.altitude!!.altitudeMin = minShort(exercise.altitude!!.altitudeMin, sample.altitude ?: Short.MAX_VALUE)
            }
        }

        // compute min and max cadence when recorded (not in HRM file)
        if (exercise.recordingMode.isCadence) {

            // compute average cadence from all samples, where cadence > 0
            var avgCadenceSum = 0
            var avgCadenceSamples = 0
            var cadenceAvg: Short = 0
            var cadenceMax: Short = 0

            for (sample in exercise.sampleList) {
                sample.cadence?.let { sampleCadence ->
                    avgCadenceSum += sampleCadence
                    avgCadenceSamples++
                }

                cadenceMax = maxShort(sample.cadence ?: 0, cadenceMax)
            }

            if (avgCadenceSum > 0 && avgCadenceSamples > 0) {
                cadenceAvg = Math.round(avgCadenceSum / avgCadenceSamples.toDouble()).toShort()
            }

            exercise.cadence = ExerciseCadence(cadenceAvg, cadenceMax)
        }

        // repair distance values of samples
        exercise.repairSamples()
    }

    /**
     * This method returns the list of all content lines of the specified block in the exercise file (e.g. when
     * blockName="Params" it returns all lines after the line "[Params]" and before next block start.
     * An empty list will be returned when the block can't be found or is empty. When the fRequired flag is true and
     * nothing was found then a EVException will be thrown.
     *
     * @param fileContent list of lines from the exercise files
     * @param blockName name of the block
     * @param required flag whether the specified block is required
     * return lines of the found block
     * @throws EVException when the block was required and was not found
     */
    private fun getBlockLines(fileContent: List<String>, blockName: String, required: Boolean): List<String> {

        val strBlockLine = "[$blockName]"
        var foundLines = mutableListOf<String>()
        var found = false

        // collect all lines from the block start to end
        for (line in fileContent) {

            if (line.startsWith(strBlockLine)) {
                found = true
                continue
            }

            if (found) {
                if (line.isBlank() || line.startsWith('[')) {
                    break
                }
                foundLines.add(line)
            }
        }

        return if (required && foundLines.isEmpty())
            throw EVException("Failed to read HRM file, can't find block '$blockName'!")
            else foundLines
    }

    /**
     * Searches for the specified value in the passed list of block lines (Strings).
     * Example: if name is "Version" this method return "106" if the line "Version=106" is in blockLines.
     *
     * @param blockLines list of lines of a block
     * @param name name of the value
     * @return value
     * @throws EVException when the value can't be found
     */
    private fun getValueFromBlock(blockLines: List<String>, name: String): String {
        return blockLines.find { it.startsWith("$name=") }
                ?.substring(name.length + 1)
                ?: throw EVException("Failed to read HRM file, can't find value for '$name'!")
    }

    private fun minShort(value1: Short, value2: Short): Short =
            Math.min(value1.toInt(), value2.toInt()).toShort()

    private fun maxShort(value1: Short, value2: Short): Short =
            Math.max(value1.toInt(), value2.toInt()).toShort()
}
