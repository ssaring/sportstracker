package de.saring.exerciseviewer.parser.impl

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue

import java.io.File
import java.time.LocalDateTime
import java.util.Locale
import java.util.TimeZone

import de.saring.exerciseviewer.core.EVException
import org.junit.jupiter.api.Test

import de.saring.exerciseviewer.data.EVExercise
import de.saring.exerciseviewer.parser.ExerciseParser
import de.saring.exerciseviewer.parser.impl.garminfit.GarminFitParser
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance

/**
 * This class contains all unit tests for the GarminFitParser class.
 *
 * @author Stefan Saring
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GarminFitParserTest {

    /** Instance to be tested. */
    private val parser: ExerciseParser = GarminFitParser()

    private val defaultLocale = Locale.getDefault()
    private val defaultTimeZone = TimeZone.getDefault()

    /**
     * Change locale/timezone to Germany (files recorded there), otherwise the datetime comparison fails.
     */
    @BeforeAll
    fun setUp() {
        Locale.setDefault(Locale.GERMANY)
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Berlin"))
    }

    /**
     * Reset default locale/timezone afterwards.
     */
    @AfterAll
    fun tearDown() {
        Locale.setDefault(defaultLocale)
        TimeZone.setDefault(defaultTimeZone)
    }

    /**
     * This method must fail on parsing an exercise file which doesn't exists.
     */
    @Test
    fun testParseExerciseMissingFile() {
        assertThrows(EVException::class.java) {
            parser.parseExercise("missing-file.fit")
        }
    }

    /**
     * This method tests the parser with an FIT file which contains only settings data,
     * no exercise data. An exception needs to be thrown.
     */
    @Test
    fun testParseExerciseSettingsFile() {
        val filename = "misc/testdata/garmin-fit/Settings.fit"
        assertTrue(File(filename).exists())

        assertThrows(EVException::class.java) {
            parser.parseExercise(filename)
        }
    }

    /**
     * This method tests the parser with an exercise file with cycling data
     * (contains speed, heartrate, altitude and cadence data).
     */
    @Test
    @Throws(EVException::class)
    fun testParseExercise() {
        // parse exercise file
        val exercise = parser.parseExercise("misc/testdata/garmin-fit/2010-07-04-06-07-36.fit")

        // check exercise data
        assertEquals(EVExercise.ExerciseFileType.GARMIN_FIT, exercise.fileType)
        assertEquals("GARMIN EDGE500 (SW 2.3)", exercise.deviceName)
        assertTrue(exercise.recordingMode.isHeartRate)
        assertTrue(exercise.recordingMode.isSpeed)
        assertTrue(exercise.recordingMode.isLocation)
        assertTrue(exercise.recordingMode.isAltitude)
        assertTrue(exercise.recordingMode.isCadence)
        assertTrue(exercise.recordingMode.isTemperature)

        assertEquals(LocalDateTime.of(2010, 7, 4, 6, 7, 36), exercise.dateTime)
        assertEquals(146499, exercise.duration?.toInt())

        assertEquals(121, exercise.heartRateAVG?.toInt())
        assertEquals(180, exercise.heartRateMax?.toInt())
        assertEquals(1567, exercise.energy?.toInt())

        assertEquals(101710, exercise.speed?.distance)
        assertEquals(24.9948, exercise.speed?.speedAvg!!.toDouble(), 0.001)
        assertEquals(68.4648, exercise.speed?.speedMax!!.toDouble(), 0.001)

        assertEquals(1115, exercise.altitude?.ascent)
        assertEquals(1110, exercise.altitude?.descent)
        assertEquals(127, exercise.altitude?.altitudeMin?.toInt())
        assertEquals(290, exercise.altitude?.altitudeAvg?.toInt())
        assertEquals(419, exercise.altitude?.altitudeMax?.toInt())

        assertEquals(84, exercise.cadence?.cadenceAvg?.toInt())
        assertEquals(119, exercise.cadence?.cadenceMax?.toInt())
        assertNull(exercise.cadence?.cyclesTotal)

        assertEquals(19, exercise.temperature?.temperatureMin?.toInt())
        assertEquals(24, exercise.temperature?.temperatureAvg?.toInt())
        assertEquals(32, exercise.temperature?.temperatureMax?.toInt())

        // check lap data
        assertEquals(5, exercise.lapList.size)
        assertEquals((0 * 3600 + 29 * 60 + 15) * 10, exercise.lapList[0].timeSplit)
        assertEquals(126, exercise.lapList[0].heartRateAVG?.toInt())
        assertEquals(146, exercise.lapList[0].heartRateMax?.toInt())
        assertEquals(122, exercise.lapList[0].heartRateSplit?.toInt())
        assertEquals(11084, exercise.lapList[0].speed?.distance)
        assertEquals(22.7334, exercise.lapList[0].speed?.speedAVG!!.toDouble(), 0.001)
        assertEquals(22.1364, exercise.lapList[0].speed?.speedEnd!!.toDouble(), 0.001)
        assertEquals(151, exercise.lapList[0].altitude?.ascent)
        assertEquals(81, exercise.lapList[0].altitude?.descent)
        assertEquals(302, exercise.lapList[0].altitude?.altitude?.toInt())
        assertEquals(20, exercise.lapList[0].temperature?.temperature?.toInt())
        assertEquals(51.05553, exercise.lapList[0].positionSplit?.latitude!!, 0.001)
        assertEquals(13.93589, exercise.lapList[0].positionSplit?.longitude!!, 0.001)

        assertEquals((2 * 3600 + 11 * 60 + 46) * 10, exercise.lapList[2].timeSplit)
        assertEquals(124, exercise.lapList[2].heartRateAVG!!.toInt())
        assertEquals(145, exercise.lapList[2].heartRateMax!!.toInt())
        assertEquals(98, exercise.lapList[2].heartRateSplit!!.toInt())
        assertEquals(48391, exercise.lapList[2].speed!!.distance)
        assertEquals(21.7080, exercise.lapList[2].speed!!.speedAVG.toDouble(), 0.001)
        assertEquals(1.0440, exercise.lapList[2].speed!!.speedEnd.toDouble(), 0.001)
        assertEquals(342, exercise.lapList[2].altitude!!.ascent)
        assertEquals(183, exercise.lapList[2].altitude!!.descent)
        assertEquals(417, exercise.lapList[2].altitude!!.altitude.toInt())
        assertEquals(24, exercise.lapList[2].temperature!!.temperature.toInt())
        assertEquals(51.00746, exercise.lapList[2].positionSplit!!.latitude, 0.001)
        assertEquals(14.20151, exercise.lapList[2].positionSplit!!.longitude, 0.001)

        assertEquals((4 * 3600 + 28 * 60 + 16) * 10, exercise.lapList[4].timeSplit)
        assertEquals(120, exercise.lapList[4].heartRateAVG!!.toInt())
        assertEquals(144, exercise.lapList[4].heartRateMax!!.toInt())
        assertEquals(94, exercise.lapList[4].heartRateSplit!!.toInt())
        assertEquals(101711, exercise.lapList[4].speed!!.distance)
        assertEquals(26.0136, exercise.lapList[4].speed!!.speedAVG.toDouble(), 0.001)
        assertEquals(0.0, exercise.lapList[4].speed!!.speedEnd.toDouble(), 0.001)
        assertEquals(206, exercise.lapList[4].altitude!!.ascent)
        assertEquals(220, exercise.lapList[4].altitude!!.descent)
        assertEquals(237, exercise.lapList[4].altitude!!.altitude.toInt())
        assertEquals(30, exercise.lapList[4].temperature!!.temperature.toInt())
        assertEquals(51.05450, exercise.lapList[4].positionSplit!!.latitude, 0.001)
        assertEquals(13.83227, exercise.lapList[4].positionSplit!!.longitude, 0.001)

        // check sample data
        assertEquals(8235, exercise.sampleList.size)
        assertEquals(1000, exercise.sampleList[0].timestamp!!.toInt())
        assertEquals(97, exercise.sampleList[0].heartRate!!.toInt())
        assertEquals(0, exercise.sampleList[0].distance!!.toInt())
        assertEquals(13.8744, exercise.sampleList[0].speed!!.toDouble(), 0.001)
        assertEquals(232, exercise.sampleList[0].altitude!!.toInt())
        assertEquals(67, exercise.sampleList[0].cadence!!.toInt())
        assertEquals(51.05350, exercise.sampleList[0].position!!.latitude, 0.001)
        assertEquals(13.83309, exercise.sampleList[0].position!!.longitude, 0.001)
        assertEquals(20, exercise.sampleList[0].temperature!!.toInt())

        assertEquals(10 * 1000, exercise.sampleList[5].timestamp!!.toInt())
        assertEquals(110, exercise.sampleList[5].heartRate!!.toInt())
        assertEquals(34, exercise.sampleList[5].distance!!.toInt())
        assertEquals(12.2364, exercise.sampleList[5].speed!!.toDouble(), 0.001)
        assertEquals(233, exercise.sampleList[5].altitude!!.toInt())
        assertEquals(69, exercise.sampleList[5].cadence!!.toInt())
        assertEquals(51.05323, exercise.sampleList[5].position!!.latitude, 0.001)
        assertEquals(13.83324, exercise.sampleList[5].position!!.longitude, 0.001)
        assertEquals(20, exercise.sampleList[5].temperature!!.toInt())

        assertEquals((4 * 3600 + 28 * 60 + 15) * 1000, exercise.sampleList[8234].timestamp!!.toInt())
        assertEquals(94, exercise.sampleList[8234].heartRate!!.toInt())
        assertEquals(101710, exercise.sampleList[8234].distance!!.toInt())
        assertEquals(0.0, exercise.sampleList[8234].speed!!.toDouble(), 0.001)
        assertEquals(237, exercise.sampleList[8234].altitude!!.toInt())
        assertEquals(0, exercise.sampleList[8234].cadence!!.toInt())
        assertEquals(51.05450, exercise.sampleList[8234].position!!.latitude, 0.001)
        assertEquals(13.83227, exercise.sampleList[8234].position!!.longitude, 0.001)
        assertEquals(30, exercise.sampleList[8234].temperature!!.toInt())
    }

    /**
     * This method tests the parser with an exercise file with running data recorded by
     * a Garmin Forerunner 910XT. Mostly the differences of this device are tested here.
     */
    @Test
    @Throws(EVException::class)
    fun testParseExerciseForerunner910XT() {
        // parse exercise file
        val exercise = parser.parseExercise("misc/testdata/garmin-fit/Garmin_Forerunner_910XT-Running.fit")

        // check exercise data
        assertEquals(EVExercise.ExerciseFileType.GARMIN_FIT, exercise.fileType)
        assertEquals("GARMIN FR910XT (SW 2.5)", exercise.deviceName)
        assertTrue(exercise.recordingMode.isHeartRate)
        assertTrue(exercise.recordingMode.isSpeed)
        assertTrue(exercise.recordingMode.isLocation)
        assertTrue(exercise.recordingMode.isAltitude)
        assertFalse(exercise.recordingMode.isCadence)
        assertFalse(exercise.recordingMode.isTemperature)

        assertEquals(LocalDateTime.of(2012, 9, 29, 17, 2, 19), exercise.dateTime)
        assertEquals(30067, exercise.duration!!.toInt())

        assertEquals(155, exercise.heartRateAVG!!.toInt())
        assertEquals(168, exercise.heartRateMax!!.toInt())
        assertEquals(681, exercise.energy!!.toInt())

        assertEquals(9843, exercise.speed!!.distance)
        assertEquals(11.7841, exercise.speed!!.speedAvg.toDouble(), 0.001)
        assertEquals(15.2424, exercise.speed!!.speedMax.toDouble(), 0.001)

        assertEquals(69, exercise.altitude!!.ascent)
        assertEquals(68, exercise.altitude!!.descent)
        assertEquals(97, exercise.altitude!!.altitudeMin.toInt())
        assertEquals(108, exercise.altitude!!.altitudeAvg.toInt())
        assertEquals(116, exercise.altitude!!.altitudeMax.toInt())

        assertNull(exercise.cadence)
        assertNull(exercise.temperature)

        // check some lap data
        assertEquals(10, exercise.lapList.size)

        assertEquals((0 * 3600 + 25 * 60 + 53) * 10, exercise.lapList[4].timeSplit)
        assertEquals(155, exercise.lapList[4].heartRateAVG!!.toInt())
        assertEquals(159, exercise.lapList[4].heartRateMax!!.toInt())
        assertEquals(159, exercise.lapList[4].heartRateSplit!!.toInt())
        assertEquals(5000, exercise.lapList[4].speed!!.distance)
        assertEquals(11.5905, exercise.lapList[4].speed!!.speedAVG.toDouble(), 0.001)
        assertEquals(11.5848, exercise.lapList[4].speed!!.speedEnd.toDouble(), 0.001)
        assertEquals(5, exercise.lapList[4].altitude!!.ascent)
        assertEquals(2, exercise.lapList[4].altitude!!.descent)
        assertEquals(104, exercise.lapList[4].altitude!!.altitude.toInt())
        assertNull(exercise.lapList[4].temperature)
        assertEquals(49.42301, exercise.lapList[4].positionSplit!!.latitude, 0.001)
        assertEquals(8.620427, exercise.lapList[4].positionSplit!!.longitude, 0.001)

        // check some sample data
        assertEquals(743, exercise.sampleList.size)
        assertEquals(0, exercise.sampleList[0].timestamp!!.toInt())
        assertEquals(79, exercise.sampleList[0].heartRate!!.toInt())
        assertEquals(1, exercise.sampleList[0].distance!!.toInt())
        assertEquals(2.5452, exercise.sampleList[0].speed!!.toDouble(), 0.001)
        assertEquals(109, exercise.sampleList[0].altitude!!.toInt())
        assertNull(exercise.sampleList[0].cadence)
        assertEquals(49.41165, exercise.sampleList[0].position!!.latitude, 0.001)
        assertEquals(8.65186, exercise.sampleList[0].position!!.longitude, 0.001)
        assertNull(exercise.sampleList[0].temperature)

        assertEquals(2048000, exercise.sampleList[500].timestamp!!.toInt())
        assertEquals(157, exercise.sampleList[500].heartRate!!.toInt())
        assertEquals(6670, exercise.sampleList[500].distance!!.toInt())
        assertEquals(12.2508, exercise.sampleList[500].speed!!.toDouble(), 0.001)
        assertEquals(102, exercise.sampleList[500].altitude!!.toInt())
        assertNull(exercise.sampleList[500].cadence)
        assertEquals(49.42749, exercise.sampleList[500].position!!.latitude, 0.001)
        assertEquals(8.63364, exercise.sampleList[500].position!!.longitude, 0.001)
        assertNull(exercise.sampleList[500].temperature)
    }

    /**
     * This method tests the parser with an exercise file with running data
     * recorded by a Garmin Fenix 2. Mostly the differences of this device are
     * tested here.
     */
    @Test
    @Throws(EVException::class)
    fun testParseExerciseFenix2() {
        val exercise = parser.parseExercise("misc/testdata/garmin-fit/Garmin_Fenix2_running_with_hrm.fit")

        // check exercise data
        assertEquals(EVExercise.ExerciseFileType.GARMIN_FIT, exercise.fileType)
        assertEquals("GARMIN FENIX2 (SW 4.4)", exercise.deviceName)
        assertTrue(exercise.recordingMode.isHeartRate)
        assertTrue(exercise.recordingMode.isSpeed)
        assertTrue(exercise.recordingMode.isLocation)
        assertTrue(exercise.recordingMode.isAltitude)
        assertFalse(exercise.recordingMode.isCadence)
        assertTrue(exercise.recordingMode.isTemperature)

        assertEquals(LocalDateTime.of(2015, 7, 21, 19, 8, 50), exercise.dateTime)
        assertEquals(23960, exercise.duration!!.toInt())

        assertEquals(169, exercise.heartRateAVG!!.toInt())
        assertEquals(192, exercise.heartRateMax!!.toInt())
        assertEquals(553, exercise.energy!!.toInt())

        assertEquals(6235, exercise.speed!!.distance)
        assertEquals(9.36, exercise.speed!!.speedAvg.toDouble(), 0.01)
        assertEquals(15.58, exercise.speed!!.speedMax.toDouble(), 0.01)

        assertEquals(4, exercise.altitude!!.ascent)
        assertEquals(4, exercise.altitude!!.descent)
        assertEquals(304, exercise.altitude!!.altitudeMin.toInt())
        assertEquals(305, exercise.altitude!!.altitudeAvg.toInt())
        assertEquals(307, exercise.altitude!!.altitudeMax.toInt())

        assertNull(exercise.cadence)

        assertEquals(30, exercise.temperature!!.temperatureMin.toInt())
        assertEquals(31, exercise.temperature!!.temperatureAvg.toInt())
        assertEquals(34, exercise.temperature!!.temperatureMax.toInt())

        // check some lap data
        assertEquals(7, exercise.lapList.size)

        val lap5 = exercise.lapList[4]
        assertEquals(19520, lap5.timeSplit)

        // average values of heart rate are still missing
        // assertEquals(169, lap5.getHeartRateAVG());
        // assertEquals(173, lap5.getHeartRateMax());
        assertEquals(170, lap5.heartRateSplit!!.toInt())
        assertEquals(5000, lap5.speed!!.distance)
        assertEquals(9.22, lap5.speed!!.speedAVG.toDouble(), 0.01)
        assertEquals(8.35, lap5.speed!!.speedEnd.toDouble(), 0.01)
        assertEquals(0, lap5.altitude!!.ascent)
        assertEquals(0, lap5.altitude!!.descent)
        assertEquals(305, lap5.altitude!!.altitude.toInt())
        assertEquals(31, lap5.temperature!!.temperature.toInt())
        assertEquals(49.426330681890, lap5.positionSplit!!.latitude, 0.000001)
        assertEquals(11.115129310637, lap5.positionSplit!!.longitude, 0.000001)

        // check some sample data
        assertEquals(2392, exercise.sampleList.size)
        assertEquals(0, exercise.sampleList[0].timestamp!!.toInt())
        assertEquals(140, exercise.sampleList[0].heartRate!!.toInt())
        assertEquals(0, exercise.sampleList[0].distance!!.toInt())
        assertEquals(3.56, exercise.sampleList[0].speed!!.toDouble(), 0.01)
        assertEquals(304, exercise.sampleList[0].altitude!!.toInt())
        assertEquals(73, exercise.sampleList[0].cadence!!.toInt())
        assertEquals(49.430309236049, exercise.sampleList[0].position!!.latitude, 0.000001)
        assertEquals(11.1262008827179, exercise.sampleList[0].position!!.longitude, 0.000001)
        assertEquals(32, exercise.sampleList[0].temperature!!.toInt())

        assertEquals(500000, exercise.sampleList[500].timestamp!!.toInt())
        assertEquals(166, exercise.sampleList[500].heartRate!!.toInt())
        assertEquals(1395, exercise.sampleList[500].distance!!.toInt())
        assertEquals(9.0, exercise.sampleList[500].speed!!.toDouble(), 0.01)
        assertEquals(305, exercise.sampleList[500].altitude!!.toInt())
        assertEquals(80, exercise.sampleList[500].cadence!!.toInt())
        assertEquals(49.433980593457, exercise.sampleList[500].position!!.latitude, 0.000001)
        assertEquals(11.1192220263183, exercise.sampleList[500].position!!.longitude, 0.000001)
        assertEquals(32, exercise.sampleList[500].temperature!!.toInt())
    }

    /**
     * This method tests the parser with an exercise file with cycling data recorded by a Garmin Edge 820.
     * It makes sure that the parser supports the new protocol format 2.0.
     */
    @Test
    @Throws(EVException::class)
    fun testParseExerciseEdge820Cycling() {
        val exercise = parser.parseExercise("misc/testdata/garmin-fit/Garmin_Edge_820-Cycling.fit")

        // check exercise data
        assertEquals(EVExercise.ExerciseFileType.GARMIN_FIT, exercise.fileType)
        assertEquals("GARMIN EDGE_820 (SW 7.0)", exercise.deviceName)
        assertTrue(exercise.recordingMode.isHeartRate)
        assertTrue(exercise.recordingMode.isSpeed)
        assertTrue(exercise.recordingMode.isLocation)
        assertTrue(exercise.recordingMode.isAltitude)
        assertTrue(exercise.recordingMode.isCadence)
        assertTrue(exercise.recordingMode.isTemperature)

        assertEquals(LocalDateTime.of(2017, 5, 13, 8, 18, 55), exercise.dateTime)
        assertEquals(70963, exercise.duration!!.toInt())

        assertEquals(134, exercise.heartRateAVG!!.toInt())
        assertEquals(173, exercise.heartRateMax!!.toInt())
        assertEquals(910, exercise.energy!!.toInt())

        assertEquals(51110, exercise.speed!!.distance)
        assertEquals(25.93, exercise.speed!!.speedAvg.toDouble(), 0.01)
        assertEquals(56.37, exercise.speed!!.speedMax.toDouble(), 0.01)

        assertEquals(653, exercise.altitude!!.ascent)
        assertEquals(663, exercise.altitude!!.descent)
        assertEquals(35, exercise.altitude!!.altitudeMin.toInt())
        assertEquals(130, exercise.altitude!!.altitudeAvg.toInt())
        assertEquals(320, exercise.altitude!!.altitudeMax.toInt())

        assertEquals(78, exercise.cadence!!.cadenceAvg.toInt())
        assertEquals(98, exercise.cadence!!.cadenceMax.toInt())
        assertEquals(8069L, exercise.cadence!!.cyclesTotal)


        assertEquals(16, exercise.temperature!!.temperatureMin.toInt())
        assertEquals(18, exercise.temperature!!.temperatureAvg.toInt())
        assertEquals(20, exercise.temperature!!.temperatureMax.toInt())

        // check some lap data
        assertEquals(1, exercise.lapList.size)

        val lap1 = exercise.lapList[0]
        assertEquals(76570, lap1.timeSplit)
        assertEquals(107, lap1.heartRateSplit!!.toInt())
        assertEquals(51110, lap1.speed!!.distance)
        assertEquals(25.93, lap1.speed!!.speedAVG.toDouble(), 0.01)
        assertEquals(0.0, lap1.speed!!.speedEnd.toDouble(), 0.01)
        assertEquals(653, lap1.altitude!!.ascent)
        assertEquals(663, lap1.altitude!!.descent)
        assertEquals(126, lap1.altitude!!.altitude.toInt())
        assertEquals(19, lap1.temperature!!.temperature.toInt())
        assertEquals(41.467856112867, lap1.positionSplit!!.latitude, 0.000001)
        assertEquals(2.0811714045703, lap1.positionSplit!!.longitude, 0.000001)

        // check some sample data
        assertEquals(1932, exercise.sampleList.size)
        assertEquals(0, exercise.sampleList[0].timestamp!!.toInt())
        assertEquals(86, exercise.sampleList[0].heartRate!!.toInt())
        assertEquals(2, exercise.sampleList[0].distance!!.toInt())
        assertEquals(7.56, exercise.sampleList[0].speed!!.toDouble(), 0.01)
        assertEquals(138, exercise.sampleList[0].altitude!!.toInt())
        assertNull(exercise.sampleList[0].cadence)
        assertEquals(41.467817220836, exercise.sampleList[0].position!!.latitude, 0.000001)
        assertEquals(2.0810086280107, exercise.sampleList[0].position!!.longitude, 0.000001)
        assertEquals(20, exercise.sampleList[0].temperature!!.toInt())

        assertEquals(216000, exercise.sampleList[50].timestamp!!.toInt())
        assertEquals(91, exercise.sampleList[50].heartRate!!.toInt())
        assertEquals(1202, exercise.sampleList[50].distance!!.toInt())
        assertEquals(26.81, exercise.sampleList[50].speed!!.toDouble(), 0.01)
        assertEquals(127, exercise.sampleList[50].altitude!!.toInt())
        assertEquals(73, exercise.sampleList[50].cadence!!.toInt())
        assertEquals(41.471779597923, exercise.sampleList[50].position!!.latitude, 0.000001)
        assertEquals(2.0914101507514, exercise.sampleList[50].position!!.longitude, 0.000001)
        assertEquals(18, exercise.sampleList[50].temperature!!.toInt())

        // check heartrate zone data
        // (heartrate zone boundaries are not available in Edge 820 FIT files, just the time in these zones)
        assertEquals(5, exercise.heartRateLimits.size)
        assertEquals(0, exercise.heartRateLimits[0].lowerHeartRate)
        assertEquals(0, exercise.heartRateLimits[0].upperHeartRate)
        assertFalse(exercise.heartRateLimits[0].isAbsoluteRange)
        assertEquals(1, exercise.heartRateLimits[0].timeBelow)
        assertEquals(634, exercise.heartRateLimits[0].timeWithin)
        assertNull(exercise.heartRateLimits[0].timeAbove)

        assertEquals(0, exercise.heartRateLimits[2].lowerHeartRate)
        assertEquals(0, exercise.heartRateLimits[2].upperHeartRate)
        assertFalse(exercise.heartRateLimits[2].isAbsoluteRange)
        assertNull(exercise.heartRateLimits[2].timeBelow)
        assertEquals(1565, exercise.heartRateLimits[2].timeWithin)
        assertNull(exercise.heartRateLimits[2].timeAbove)

        assertEquals(0, exercise.heartRateLimits[4].lowerHeartRate)
        assertEquals(0, exercise.heartRateLimits[4].upperHeartRate)
        assertFalse(exercise.heartRateLimits[4].isAbsoluteRange)
        assertNull(exercise.heartRateLimits[4].timeBelow)
        assertEquals(0, exercise.heartRateLimits[4].timeWithin)
        assertEquals(0, exercise.heartRateLimits[4].timeAbove)
    }

    /**
     * This method tests the parser with an exercise file with running data (incl. heartrate, altitude, cadence)
     * recorded by a Garmin Fenix 6 watch.
     * The speed and altitude values in the sample data are stored differently since newer Fenix devices.
     */
    @Test
    @Throws(EVException::class)
    fun testParseExerciseFenix6Running() {
        val exercise = parser.parseExercise("misc/testdata/garmin-fit/Garmin_Fenix_6-Running.fit")

        // check exercise data
        assertEquals(EVExercise.ExerciseFileType.GARMIN_FIT, exercise.fileType)
        assertEquals("GARMIN FENIX6 (SW 5.0)", exercise.deviceName)
        assertTrue(exercise.recordingMode.isHeartRate)
        assertTrue(exercise.recordingMode.isSpeed)
        assertTrue(exercise.recordingMode.isLocation)
        assertTrue(exercise.recordingMode.isAltitude)
        assertTrue(exercise.recordingMode.isCadence)
        assertTrue(exercise.recordingMode.isTemperature)

        assertEquals(LocalDateTime.of(2019, 12, 12, 20, 0, 1, 0), exercise.dateTime)
        assertEquals(1238, exercise.duration!!.toInt())

        assertEquals(120, exercise.heartRateAVG!!.toInt())
        assertEquals(23, exercise.energy!!.toInt())

        assertEquals(193, exercise.speed!!.distance)
        assertEquals(5.6, exercise.speed!!.speedAvg.toDouble(), 0.01)
        assertEquals(6.35, exercise.speed!!.speedMax.toDouble(), 0.01)

        assertEquals(1, exercise.altitude!!.ascent)
        assertEquals(3, exercise.altitude!!.altitudeAvg.toInt())

        // check some lap data
        assertEquals(1, exercise.lapList.size)
        assertEquals(4.7, exercise.lapList[0].speed!!.speedEnd.toDouble(), 0.01)
        assertEquals(1, exercise.lapList[0].altitude!!.ascent)
        assertEquals(2, exercise.lapList[0].altitude!!.altitude.toInt())

        // check some sample data
        assertEquals(29, exercise.sampleList.size)
        assertEquals(0, exercise.sampleList[0].timestamp!!.toInt())
        assertEquals(5.81, exercise.sampleList[0].speed!!.toDouble(), 0.01)
        assertEquals(5, exercise.sampleList[0].altitude!!.toInt())

        assertEquals(30 * 1000, exercise.sampleList[10].timestamp!!.toInt())
        assertEquals(5.34, exercise.sampleList[10].speed!!.toDouble(), 0.01)
        assertEquals(3, exercise.sampleList[10].altitude!!.toInt())

        assertEquals(87 * 1000, exercise.sampleList[20].timestamp!!.toInt())
        assertEquals(6.15, exercise.sampleList[20].speed!!.toDouble(), 0.01)
        assertEquals(2, exercise.sampleList[20].altitude!!.toInt())
    }

    /**
     * This method tests the parser with an exercise file with running data (incl. heartrate, altitude, cadence)
     * recorded by a Garmin Fenix 6S Pro watch.
     * This test focuses on the heartrate zone data, as this is stored in a different way on newer Garmin devices
     * (e.g. Garmin Fenix 5 or later, Garmin Edge 530 or later)
     */
    @Test
    @Throws(EVException::class)
    fun testParseExerciseFenix6SProRunningHeartrateZones() {
        val exercise = parser.parseExercise("misc/testdata/garmin-fit/Garmin_Fenix_6S_Pro-Running.fit")

        // check exercise data
        assertEquals(EVExercise.ExerciseFileType.GARMIN_FIT, exercise.fileType)
        assertEquals("GARMIN FENIX6S (SW 13.1)", exercise.deviceName)
        assertTrue(exercise.recordingMode.isHeartRate)

        // check some basic data
        assertEquals(LocalDateTime.of(2021, 2, 6, 12, 3, 55, 0), exercise.dateTime)
        assertEquals(3140, exercise.duration!!.toInt())

        // check heartrate zone data
        assertEquals(5, exercise.heartRateLimits.size)
        assertEquals(87, exercise.heartRateLimits[0].lowerHeartRate)
        assertEquals(105, exercise.heartRateLimits[0].upperHeartRate)
        assertTrue(exercise.heartRateLimits[0].isAbsoluteRange)
        assertEquals(0, exercise.heartRateLimits[0].timeBelow)
        assertEquals(21, exercise.heartRateLimits[0].timeWithin)
        assertNull(exercise.heartRateLimits[0].timeAbove)

        assertEquals(121, exercise.heartRateLimits[2].lowerHeartRate)
        assertEquals(140, exercise.heartRateLimits[2].upperHeartRate)
        assertNull(exercise.heartRateLimits[2].timeBelow)
        assertEquals(240, exercise.heartRateLimits[2].timeWithin)
        assertNull(exercise.heartRateLimits[2].timeAbove)

        assertEquals(157, exercise.heartRateLimits[4].lowerHeartRate)
        assertEquals(174, exercise.heartRateLimits[4].upperHeartRate)
        assertNull(exercise.heartRateLimits[4].timeBelow)
        assertEquals(0, exercise.heartRateLimits[4].timeWithin)
        assertEquals(0, exercise.heartRateLimits[4].timeAbove)
    }

    /**
     * This method tests the parser with an exercise file with running data (incl. heartrate, altitude, cadence)
     * recorded by a Suunto Spartan Sport Wrist HR Baro watch. For Suunto watches is speial handling of the location
     * data needed, they don't store the start position of a exercise (just the sample positions).
     */
    @Test
    @Throws(EVException::class)
    fun testParseExerciseSuuntoSpartanSportWristHrBaro() {
        val exercise = parser.parseExercise("misc/testdata/garmin-fit/Suunto_Spartan_Sport_Wrist_HR_Baro.fit")

        // check exercise data
        assertEquals(EVExercise.ExerciseFileType.GARMIN_FIT, exercise.fileType)
        assertNull(exercise.deviceName) // Suunto models are missing in the GarminProduct class (FIT SDK)
        assertTrue(exercise.recordingMode.isHeartRate)
        assertTrue(exercise.recordingMode.isSpeed)
        assertTrue(exercise.recordingMode.isLocation)
        assertTrue(exercise.recordingMode.isAltitude)
        assertFalse(exercise.recordingMode.isCadence)
        assertTrue(exercise.recordingMode.isTemperature)

        assertEquals(LocalDateTime.of(2020, 10, 23, 14, 34, 58, 0), exercise.dateTime)
        assertEquals(22404, exercise.duration!!.toInt())

        assertEquals(112, exercise.heartRateAVG!!.toInt())
        assertEquals(282, exercise.energy!!.toInt())

        assertEquals(5411, exercise.speed!!.distance)
        assertEquals(8.69, exercise.speed!!.speedAvg.toDouble(), 0.01)
        assertEquals(35.28, exercise.speed!!.speedMax.toDouble(), 0.01)

        assertEquals(99, exercise.altitude!!.ascent)
        assertEquals(73, exercise.altitude!!.altitudeAvg.toInt())

        // no lap data contained

        // check some sample data (on Suunto watches some samples may not contain all data)
        assertEquals(2235, exercise.sampleList.size)
        assertEquals(0, exercise.sampleList[0].timestamp!!.toInt())
        assertNull(exercise.sampleList[0].speed)
        assertNull(exercise.sampleList[0].altitude)
        assertNull(exercise.sampleList[0].position)

        assertEquals(256 * 1000, exercise.sampleList[250].timestamp!!.toInt())
        assertEquals(12.10, exercise.sampleList[250].speed!!.toDouble(), 0.01)
        assertEquals(85, exercise.sampleList[250].altitude!!.toInt())
        assertEquals(52.029409417882, exercise.sampleList[250].position!!.latitude, 0.000001)
        assertEquals(6.0244788229465, exercise.sampleList[250].position!!.longitude, 0.000001)

        assertEquals(506 * 1000, exercise.sampleList[500].timestamp!!.toInt())
        assertEquals(15.98, exercise.sampleList[500].speed!!.toDouble(), 0.01)
        assertEquals(69, exercise.sampleList[500].altitude!!.toInt())
        assertEquals(52.028528228402, exercise.sampleList[500].position!!.latitude, 0.000001)
        assertEquals(6.0391104407608, exercise.sampleList[500].position!!.longitude, 0.000001)

        // check heartrate zone data
        // (heartrate zone boundaries are not available in Suunto Spartan FIT files, just the time in these zones)
        assertEquals(3, exercise.heartRateLimits.size)
        assertEquals(0, exercise.heartRateLimits[0].lowerHeartRate)
        assertEquals(0, exercise.heartRateLimits[0].upperHeartRate)
        assertFalse(exercise.heartRateLimits[0].isAbsoluteRange)
        assertEquals(1320, exercise.heartRateLimits[0].timeBelow)
        assertEquals(191, exercise.heartRateLimits[0].timeWithin)
        assertNull(exercise.heartRateLimits[0].timeAbove)

        assertEquals(0, exercise.heartRateLimits[1].lowerHeartRate)
        assertEquals(0, exercise.heartRateLimits[1].upperHeartRate)
        assertFalse(exercise.heartRateLimits[1].isAbsoluteRange)
        assertNull(exercise.heartRateLimits[1].timeBelow)
        assertEquals(312, exercise.heartRateLimits[1].timeWithin)
        assertNull(exercise.heartRateLimits[1].timeAbove)

        assertEquals(0, exercise.heartRateLimits[2].lowerHeartRate)
        assertEquals(0, exercise.heartRateLimits[2].upperHeartRate)
        assertFalse(exercise.heartRateLimits[2].isAbsoluteRange)
        assertNull(exercise.heartRateLimits[2].timeBelow)
        assertEquals(254, exercise.heartRateLimits[2].timeWithin)
        assertEquals(162, exercise.heartRateLimits[2].timeAbove)
    }

    /**
     * Tests the parser with a FIT exercise file with indoor cycling data recorded in Zwift, which contains data from
     * heartrate, powermeter and cadence sensors. The Zwift recording does not provide normalized power data.
     */
    @Test
    @Throws(EVException::class)
    fun testParseExerciseIndoorCyclingPowermeterZwift() {
        val exercise = parser.parseExercise("misc/testdata/garmin-fit/Zwift-Cycling_Indoor-Assioma_Uno.fit")

        // check exercise data
        assertEquals(EVExercise.ExerciseFileType.GARMIN_FIT, exercise.fileType)
        assertEquals("ZWIFT (SW 5.62)", exercise.deviceName)
        assertTrue(exercise.recordingMode.isHeartRate)
        assertTrue(exercise.recordingMode.isSpeed)
        assertTrue(exercise.recordingMode.isCadence)
        assertTrue(exercise.recordingMode.isPower)
        assertTrue(exercise.recordingMode.isLocation)

        // check some basic data
        assertEquals(LocalDateTime.of(2021, 12, 12, 10, 15, 49, 0), exercise.dateTime)
        assertEquals(3700, exercise.duration!!.toInt())

        // check power and cadence data on exercise level
        assertEquals(118, exercise.power!!.powerAvg!!.toInt())
        assertEquals(248, exercise.power!!.powerMax!!.toInt())
        assertNull(exercise.power!!.powerNormalized)

        assertEquals(85, exercise.cadence!!.cadenceAvg!!.toInt())
        assertEquals(100, exercise.cadence!!.cadenceMax!!.toInt())
        assertEquals(0, exercise.cadence!!.cyclesTotal)

        // check power and cadence data on lap level (just one)
        assertEquals(1, exercise.lapList.size)
        assertEquals(118, exercise.lapList[0].power!!.powerAvg!!.toInt())
        assertEquals(248, exercise.lapList[0].power!!.powerMax!!.toInt())
        assertNull(exercise.lapList[0].power!!.powerNormalized)
        assertEquals(0, exercise.lapList[0].speed!!.cadence!!.toInt())

        // check power and cadence data on sample level
        assertEquals(370, exercise.sampleList.size)
        assertEquals(66, exercise.sampleList[0].power!!.toInt())
        assertEquals(68, exercise.sampleList[0].cadence!!!!.toInt())
        assertEquals(124, exercise.sampleList[200].power!!.toInt())
        assertEquals(87, exercise.sampleList[200].cadence!!!!.toInt())
        assertEquals(0, exercise.sampleList[369].power!!.toInt())
        assertEquals(0, exercise.sampleList[369].cadence!!!!.toInt())
    }

    /**
     * Tests the parser with a FIT exercise file with indoor cycling data recorded on a Garmin Fenix 6S Pro, which
     * contains data from heartrate, powermeter and cadence sensors.
     */
    @Test
    @Throws(EVException::class)
    fun testParseExerciseIndoorCyclingPowermeterFenix6() {
        val exercise = parser.parseExercise("misc/testdata/garmin-fit/Garmin_Fenix_6S_Pro-Cycling-Indoor-Assioma_Uno.fit")

        // check exercise data
        assertEquals(EVExercise.ExerciseFileType.GARMIN_FIT, exercise.fileType)
        assertEquals("GARMIN FENIX6S (SW 19.2)", exercise.deviceName)
        assertTrue(exercise.recordingMode.isHeartRate)
        assertTrue(exercise.recordingMode.isSpeed)
        assertTrue(exercise.recordingMode.isCadence)
        assertTrue(exercise.recordingMode.isPower)
        assertFalse(exercise.recordingMode.isLocation)

        // check some basic data
        assertEquals(LocalDateTime.of(2021, 12, 12, 10, 15, 53, 0), exercise.dateTime)
        assertEquals(3548, exercise.duration!!.toInt())

        // check power and cadence data on exercise level
        assertEquals(122, exercise.power!!.powerAvg!!.toInt())
        assertEquals(248, exercise.power!!.powerMax!!.toInt())
        assertEquals(127, exercise.power!!.powerNormalized!!.toInt())

        assertEquals(87, exercise.cadence!!.cadenceAvg!!.toInt())
        assertEquals(101, exercise.cadence!!.cadenceMax!!.toInt())
        assertEquals(517, exercise.cadence!!.cyclesTotal!!.toInt())

        // check power data on lap level
        assertEquals(3, exercise.lapList.size)
        assertEquals(113, exercise.lapList[0].power!!.powerAvg!!.toInt())
        assertEquals(134, exercise.lapList[0].power!!.powerMax!!.toInt())
        assertEquals(114, exercise.lapList[0].power!!.powerNormalized!!.toInt())
        assertEquals(133, exercise.lapList[1].power!!.powerAvg!!.toInt())
        assertEquals(248, exercise.lapList[1].power!!.powerMax!!.toInt())
        assertEquals(136, exercise.lapList[1].power!!.powerNormalized!!.toInt())

        // check power and cadence data on sample level
        assertEquals(356, exercise.sampleList.size)
        assertEquals(45, exercise.sampleList[0].power!!.toInt())
        assertEquals(69, exercise.sampleList[0].cadence!!!!.toInt())
        assertEquals(123, exercise.sampleList[100].power!!.toInt())
        assertEquals(91, exercise.sampleList[100].cadence!!!!.toInt())
        assertEquals(60, exercise.sampleList[355].power!!.toInt())
        assertEquals(79, exercise.sampleList[355].cadence!!!!.toInt())
    }
}