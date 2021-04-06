package de.saring.exerciseviewer.parser.impl.garminfit

import com.garmin.fit.*
import java.util.LinkedList

import de.saring.exerciseviewer.core.EVException
import de.saring.exerciseviewer.data.EVExercise
import de.saring.exerciseviewer.data.ExerciseAltitude
import de.saring.exerciseviewer.data.ExerciseCadence
import de.saring.exerciseviewer.data.ExerciseSample
import de.saring.exerciseviewer.data.ExerciseSpeed
import de.saring.exerciseviewer.data.ExerciseTemperature
import de.saring.exerciseviewer.data.Lap
import de.saring.exerciseviewer.data.LapAltitude
import de.saring.exerciseviewer.data.LapSpeed
import de.saring.exerciseviewer.data.LapTemperature
import de.saring.exerciseviewer.data.Position
import de.saring.util.Date310Utils
import de.saring.util.unitcalc.CalculationUtils
import de.saring.util.unitcalc.ConvertUtils

/**
 * Message listener implementation for creating the EVExercise object from the FIT messages send by the decoder/parser.
 *
 * @author Stefan Saring
 */
internal class FitMessageListener : MesgListener {

    /**
     * The parsed exercise.
     */
    private val exercise = EVExercise(EVExercise.ExerciseFileType.GARMIN_FIT)
    /**
     * List of created laps (collected in a LinkedList and not in EVExercise array, much faster).
     */
    private val lFitLaps = LinkedList<FitLap>()
    /**
     * List of created exercise samples (collected in a LinkedList and not in EVExercise array, much faster).
     */
    private val lSamples = LinkedList<ExerciseSample>()
    /**
     * Flag for availability of temperature data.
     */
    private var temperatureAvailable = false

    override fun onMesg(mesg: Mesg) {

        // delegate interesting messages to appropriate handler methods
        when (mesg.num) {
            MesgNum.SESSION -> readSessionMessage(SessionMesg(mesg))
            MesgNum.LAP -> readLapMessage(LapMesg(mesg))
            MesgNum.RECORD -> readRecordMessage(RecordMesg(mesg))
            MesgNum.LENGTH -> readLengthMessage(LengthMesg(mesg))
            MesgNum.DEVICE_INFO -> readDeviceInfoMessage(DeviceInfoMesg(mesg))
        }
    }

    /**
     * Reads exercise-level data from the specified Session message.
     *
     * @param mesg Session message
     */
    private fun readSessionMessage(mesg: SessionMesg) {

        // read time data
        exercise.dateTime = Date310Utils.dateToLocalDateTime(mesg.startTime.date)
        exercise.duration = Math.round(mesg.totalTimerTime * 10)

        // read optional heartrate data
        mesg.avgHeartRate?.let {
            exercise.recordingMode.isHeartRate = true
            exercise.heartRateAVG = it
        }
        mesg.maxHeartRate?.let {
            exercise.recordingMode.isHeartRate = true
            exercise.heartRateMax = it
        }
        mesg.totalCalories?.let {exercise.energy = it }

        // read optional speed data
        mesg.totalDistance?.let { totalDistance ->
            exercise.recordingMode.isSpeed = true

            val distance = Math.round(totalDistance)
            // AVG speed might be missing in Garmin Forerunner 910XT exercises, will be calculated afterwards
            val avgSpeed = mesg.avgSpeed?.let {ConvertUtils.convertMeterPerSecond2KilometerPerHour(it) } ?: 0f
            val maxSpeed = mesg.maxSpeed?.let {ConvertUtils.convertMeterPerSecond2KilometerPerHour(it) } ?: 0f
            exercise.speed = ExerciseSpeed(avgSpeed, maxSpeed, distance)
        }

        // check for location data
        mesg.startPositionLat?.let {exercise.recordingMode.isLocation = true }

        // read optional ascent and descent data
        mesg.totalAscent?.let {
            exercise.recordingMode.isAltitude = true
            val totalDescent = mesg.totalDescent ?: it
            exercise.altitude = ExerciseAltitude(0.toShort(), 0.toShort(), 0.toShort(), it, totalDescent)
        }

        // read optional cadence data
        mesg.avgCadence?.let { avgCadence ->
            exercise.recordingMode.isCadence = true
            val maxCadence = mesg.maxCadence ?: 0
            val totalCycles = mesg.totalCycles ?: null
            exercise.cadence = ExerciseCadence(avgCadence, maxCadence, totalCycles)
        }
    }

    /**
     * Reads lap-level data from the specified Lap message.
     *
     * @param mesg Lap message
     */
    private fun readLapMessage(mesg: LapMesg) {
        val lap = Lap()

        // read optional heartrate data
        mesg.avgHeartRate?.let { lap.heartRateAVG = it }
        mesg.maxHeartRate?.let { lap.heartRateMax = it }

        // read optional speed data
        mesg.totalDistance?.let { totalDistance ->
            val lapSpeedDistance = Math.round(totalDistance)
            // AVG speed might be missing in Garmin Forerunner 910XT exercises, will be calculated afterwards
            val lapSpeedAVG = mesg.avgSpeed?.let { ConvertUtils.convertMeterPerSecond2KilometerPerHour(it) } ?: 0f
            lap.speed = LapSpeed(0f, lapSpeedAVG, lapSpeedDistance)
        }

        // read optional ascent data
        mesg.totalAscent?.let {
            val totalDescent = mesg.totalDescent ?: it
            lap.altitude = LapAltitude(0.toShort(), it, totalDescent)
        }

        // read optional position data
        if (mesg.endPositionLat != null && mesg.endPositionLong != null) {
            lap.positionSplit = Position(
                    ConvertUtils.convertSemicircle2Degree(mesg.endPositionLat),
                    ConvertUtils.convertSemicircle2Degree(mesg.endPositionLong))
        }

        lFitLaps.add(FitLap(lap, Date310Utils.dateToLocalDateTime(mesg.timestamp.date)))
    }

    /**
     * Reads sample-level data from the specified Record message.
     *
     * @param mesg Record message
     */
    private fun readRecordMessage(mesg: RecordMesg) {

        val sample = ExerciseSample()
        lSamples.add(sample)

        // sample timestamp must be the offset from start time, will be corrected later
        // (in some cases the timestamp is missing and will be read from the next Length message)
        mesg.timestamp?.let { sample.timestamp = it.date.time }

        mesg.heartRate?.let {
            sample.heartRate = it
            exercise.recordingMode.isHeartRate = true
        }

        mesg.distance?.let { sample.distance = Math.round(it) }
        mesg.cadence?.let { sample.cadence = it }

        // on newer high-end Garmin devices (e.g. Fenix 6 or 5X) the speed and altitude is stored in the
        // enhancedSpeed or enhancedAltitude attributes => get it from there
        val mesgSpeed: Float? = if (mesg.enhancedSpeed != null) mesg.enhancedSpeed else mesg.speed
        mesgSpeed?.let {
            sample.speed = ConvertUtils.convertMeterPerSecond2KilometerPerHour(it)
        }

        val mesgAltitude: Float? = if (mesg.enhancedAltitude != null) mesg.enhancedAltitude else mesg.altitude
        mesgAltitude?.let {
            sample.altitude = Math.round(it).toShort()
        }

        mesg.temperature?.let {
            temperatureAvailable = true
            sample.temperature = it.toShort()
        }

        if (mesg.positionLat != null && mesg.positionLong != null) {
            sample.position = Position(
                    ConvertUtils.convertSemicircle2Degree(mesg.positionLat!!),
                    ConvertUtils.convertSemicircle2Degree(mesg.positionLong!!))

            // Suunto watches don't store the startPosition in the Session message
            // => so enable location recording mode when location data is stored in the Record messages (samples)
            exercise.recordingMode.isLocation = true
        }
    }

    /**
     * Special handling for Garmin Forerunner 910XT exercise files: Length messages are stored mostly in swimming
     * exercises, they contain information for a "length". For some reason the previous read Record message does
     * not contain any timing information, these are contained in the Length messages. So the end timestamp must
     * be read and assigned to the last read sample record here.
     *
     * @param mesg Length message
     */
    private fun readLengthMessage(mesg: LengthMesg) {
        // swimming exercises of Garmin Forerunner 645 don't contain any samples
        if (lSamples.isNotEmpty()) {

            val startTimestamp = mesg.startTime.date.time
            val totalElapsedTime = Math.round(mesg.totalElapsedTime.toDouble() * 1000.0)
            val endTimestamp = startTimestamp + totalElapsedTime

            val lastSample = lSamples[lSamples.size - 1]
            lastSample.timestamp = endTimestamp
        }
    }

    /**
     * Reads HRM device information from the specified DeviceInfoMesg message.
     *
     * @param mesg device info message
     */
    private fun readDeviceInfoMessage(mesg: DeviceInfoMesg) {

        // get the device name from the first DeviceInfo message, it's for the device itself
        // (the next messages are for sensors etc.)
        if (exercise.deviceName == null) {

            if (mesg.manufacturer != null && mesg.product != null) {
                var deviceName = Manufacturer.getStringFromValue(mesg.manufacturer)

                // the product name might be missing in the FIT SDK for newer device models
                val productName = GarminProduct.getStringFromValue(mesg.product)
                if (productName?.isNotEmpty() == true) {
                    deviceName += " $productName"
                }

                // append software version, if present
                if (mesg.softwareVersion != null) {
                    deviceName += " (SW ${mesg.softwareVersion})"
                }

                exercise.deviceName = deviceName
            }
        }
    }

    /**
     * Returns the EVExercise created from the received message. It sets up all lap and sample data and calculates the
     * missing data before.
     *
     * @return exercise
     */
    fun getExercise(): EVExercise {

        // has activity data been found in the FIT file? (FIT files may contain other data too)
        if (exercise.dateTime == null) {
            throw EVException("The FIT file does not contain any exercise (activity) data...")
        }

        storeSamples()
        storeLaps()

        calculateMissingAverageSpeed()
        if (!exercise.sampleList.isEmpty()) {
            calculateAltitudeSummary()
            calculateTemperatureSummary()
            calculateMissingMaxSpeed()
            calculateMissingHeartRateAVG()
            calculateMissingHeartRateMax()
        }

        return exercise
    }

    /**
     * Stores the sample data in the exercise. It also fixes the timestamps in all ExerciseSamples, it must be the
     * offset from the start time.
     */
    private fun storeSamples() {
        val startTime = Date310Utils.getMilliseconds(exercise.dateTime!!)
        for (sample in lSamples) {
            sample.timestamp?.let { sample.timestamp = it - startTime }
        }
        exercise.sampleList.addAll(lSamples)
    }

    /**
     * Stores the lap data in the exercise and calculate the missing values.
     */
    private fun storeLaps() {
        var lapDistanceSum = 0

        // convert FitLap to Lap objects
        val lLaps = LinkedList<Lap>()
        val startTime = Date310Utils.getMilliseconds(exercise.dateTime!!)

        for (fitLap in lFitLaps) {
            val lap = fitLap.lap
            lLaps.add(lap)

            // fix the split time in all Laps, it must be the offset from the start time
            val lapSplitDateTimeMillis = Date310Utils.getMilliseconds(fitLap.splitDatTime)
            lap.timeSplit = ((lapSplitDateTimeMillis - startTime) / 100).toInt()

            // get all the missing lap data from the sample at lap end time
            val sampleAtLapEnd = getExerciseSampleForLapEnd(lap)
            if (sampleAtLapEnd != null) {
                lap.heartRateSplit = sampleAtLapEnd.heartRate

                lap.speed?.let { lapSpeed ->
                    // fix lap distance, it must be the distance from exercise start (FIT stores from Lap start)
                    lapDistanceSum += lapSpeed.distance
                    lapSpeed.distance = lapDistanceSum

                    lapSpeed.speedEnd = sampleAtLapEnd.speed ?: 0f
                    lapSpeed.cadence = sampleAtLapEnd.cadence
                }

                lap.altitude?.let {
                    if (sampleAtLapEnd.altitude != null) {
                        lap.altitude = it.copy(altitude = sampleAtLapEnd.altitude as Short, ascent = lap.altitude!!.ascent)
                    }
                }

                if (temperatureAvailable) {
                    sampleAtLapEnd.temperature?.let { lap.temperature = LapTemperature(it) }
                }
            }
        }

        exercise.lapList.addAll(lLaps)
    }

    /**
     * Returns the closest ExerciseSample for the lap end time.
     *
     * @param lap the lap for search
     * @return the closest ExerciseSample
     */
    private fun getExerciseSampleForLapEnd(lap: Lap): ExerciseSample? {
        val lapSplitTimestamp = lap.timeSplit * 100L
        var closestSample: ExerciseSample? = null
        var closestTimeDistance = java.lang.Long.MAX_VALUE

        for (sample in exercise.sampleList) {
            sample.timestamp?.let {
                val timeDistance = Math.abs(it - lapSplitTimestamp)
                if (timeDistance < closestTimeDistance) {
                    closestTimeDistance = timeDistance
                    closestSample = sample
                }
            }
        }
        return closestSample
    }

    /**
     * Calculates the min, max and average altitude (if available) from the sample data.
     */
    private fun calculateAltitudeSummary() {

        exercise.altitude?.let { exerciseAltitude ->
            var altMin = Short.MAX_VALUE
            var altMax = Short.MIN_VALUE
            var altitudeSum = 0

            for (sample in exercise.sampleList) {
                val sampleAltitude = sample.altitude ?: 0
                altMin = Math.min(sampleAltitude.toInt(), altMin.toInt()).toShort()
                altMax = Math.max(sampleAltitude.toInt(), altMax.toInt()).toShort()
                altitudeSum += sampleAltitude.toInt()
            }

            exerciseAltitude.altitudeMin = altMin
            exerciseAltitude.altitudeMax = altMax
            exerciseAltitude.altitudeAvg = Math.round(altitudeSum / exercise.sampleList.size.toDouble()).toShort()
        }
    }

    /**
     * Calculates the min, max and average temperature (if available) from the sample data.
     */
    private fun calculateTemperatureSummary() {
        if (temperatureAvailable) {
            exercise.recordingMode.isTemperature = true

            var tempMin = Short.MAX_VALUE
            var tempMax = Short.MIN_VALUE
            var temperatureSum = 0
            var temperatureCount = 0

            for (sample in exercise.sampleList) {
                // it's possible that just a few samples contain no temperature (e.g. for Edge 800)
                sample.temperature?.let {sampleTemperature ->
                    tempMin = Math.min(sampleTemperature.toInt(), tempMin.toInt()).toShort()
                    tempMax = Math.max(sampleTemperature.toInt(), tempMax.toInt()).toShort()
                    temperatureSum += sampleTemperature.toInt()
                    temperatureCount++
                }
            }

            val tempAvg = Math.round(temperatureSum / temperatureCount.toDouble()).toShort()
            exercise.temperature = ExerciseTemperature(tempMin, tempAvg, tempMax)
        }
    }

    /**
     * Workaround for Garmin Forerunner 910XT exercise files: the AVG speed is often not available for the parsed laps
     * and for the exercise (for unknown reasons). So the AVG speed needs to be calculated after parsing.
     * TODO This workaround works only for exercise files without transitions between sport types (then there are
     * negative timestamps for some strange reasons).
     */
    private fun calculateMissingAverageSpeed() {

        val exerciseSpeed = exercise.speed
        val exerciseDuration = exercise.duration
        if (exerciseSpeed != null && exerciseDuration != null && exerciseSpeed.speedAvg == 0f) {
            exerciseSpeed.speedAvg = CalculationUtils.calculateAvgSpeed(
                    exerciseSpeed.distance / 1000f, Math.round(exerciseDuration / 10f))
        }

        for (lap in exercise.lapList) {
            lap.speed?.let { lapSpeed ->
                if (lapSpeed.speedAVG == 0f) {
                    lapSpeed.speedAVG = CalculationUtils.calculateAvgSpeed(
                            lapSpeed.distance / 1000f, Math.round(lap.timeSplit / 10f))
                }
            }
        }
    }

    /**
     * Calculates the max speed of the exercise, if missing (e.g. in Fenix exercise files).
     */
    private fun calculateMissingMaxSpeed() {
        exercise.speed?.let { exerciseSpeed ->
            if (exerciseSpeed.speedMax < 0.01) {

                exercise.sampleList
                        .map { it.speed ?: 0f }
                        .maxOrNull()
                        ?.let { exerciseSpeed.speedMax = it }
            }
        }
    }

    /**
     * Calculates the average heartrate of the exercise, if missing (e.g. in Fenix exercise files).
     */
    private fun calculateMissingHeartRateAVG() {
        if (exercise.heartRateAVG == null) {

            exercise.sampleList
                    .filter { it.heartRate != null }
                    .map { it.heartRate ?: 0 }
                    .average()
                    .let { exercise.heartRateAVG = Math.round(it).toShort() }
        }
    }

    /**
     * Calculates the maximum heartrate of the exercise, if missing (e.g. in Fenix exercise files).
     */
    private fun calculateMissingHeartRateMax() {
        if (exercise.heartRateMax == null) {

            exercise.sampleList
                    .map { it.heartRate ?: 0 }
                    .maxOrNull()
                    ?.let { exercise.heartRateMax = it }
        }
    }
}