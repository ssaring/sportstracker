package de.saring.exerciseviewer.parser.impl

import static org.junit.Assert.assertEquals;
import de.saring.exerciseviewer.core.EVException
import de.saring.exerciseviewer.data.EVExercise
import de.saring.exerciseviewer.parser.ExerciseParser

/**
 * This class contains all unit tests for the PolarHRMParser class.
 *
 * @author Stefan Saring
 */
class PolarHRMParserTest extends GroovyTestCase {
    
    /** Instance to be tested. */
    private ExerciseParser parser
    
    /**
     * This method initializes the environment for testing.
     */
    void setUp () {
        parser = new PolarHRMParser ()
    }
    
    /**
     * This method must fail on parsing an exerise file which doesn't exists.
     */
    void testParseExerciseMissingFile () {
        shouldFail (EVException) {
            parser.parseExercise ('misc/testdata/sample-123.hrm')
        }
    }
    
    /**
     * This method tests the parser by with an cycling exercise file
     * recorded in metric units.
     */
    void testParseExerciseWithMetricUnits () {
        
        // parse exercise file
        def exercise = parser.parseExercise ('misc/testdata/s710/cycling-metric.hrm')
        
        // check exercise data
        assertEquals (EVExercise.ExerciseFileType.HRM, exercise.fileType)
        assertEquals (0, exercise.userID)
        assertTrue (exercise.recordingMode.altitude)
        assertTrue (exercise.recordingMode.speed)
        assertFalse (exercise.recordingMode.cadence)
        assertFalse (exercise.recordingMode.power)        
        // assertEquals (exercise.type, 1) // (not in HRM file)
        // assertEquals (exercise.typeLabel, "ExeSet1") // (not in HRM file)
        // assertEquals (exercise.recordingMode.bikeNumber, 2) // (not in HRM file)
        
        def calDate = Calendar.getInstance ()
        calDate.set (2002, 11-1, 20, 14, 07, 44)
        assertEquals ((int) (calDate.time.time / 1000), (int) (exercise.date.time / 1000))
        assertEquals ((1*60*60*10) + (13*60*10) + 15*10, exercise.duration)
        assertEquals (15, exercise.recordingInterval)

        assertEquals (135, exercise.heartRateAVG)
        assertEquals (218, exercise.heartRateMax)
        assertEquals (251, Math.round (exercise.speed.speedAVG * 10))
        assertEquals (969, Math.round (exercise.speed.speedMax * 10))
        assertEquals (29900, exercise.speed.distance)

        assertNull (exercise.cadence)
        assertEquals (215, exercise.altitude.altitudeMin)
        assertEquals (253, exercise.altitude.altitudeAVG)
        assertEquals (300, exercise.altitude.altitudeMax)
        // assertEquals (exercise.temperature.temperatureMin, 3) // (not in HRM file)
        // assertEquals (exercise.temperature.temperatureAVG, 3) // (not in HRM file)
        // assertEquals (exercise.temperature.temperatureMax, 5) // (not in HRM file)   
        // assertEquals (exercise.energy, 591) // (not in HRM file)
        // assertEquals (exercise.energyTotal, 24099) // (not in HRM file)
        // assertEquals (exercise.sumExerciseTime, (56*60) + 34) // (not in HRM file)
        // assertEquals (exercise.sumRideTime, (42*60) + 56) // (not in HRM file)
        assertEquals (1024, exercise.odometer)
        
        // check heart rate limits
        assertEquals (3, exercise.heartRateLimits.size ())
        assertEquals (120, exercise.heartRateLimits[0].lowerHeartRate)
        assertEquals (155, exercise.heartRateLimits[0].upperHeartRate)
        assertEquals ((0*60*60) + (5*60) + 0, exercise.heartRateLimits[0].timeBelow)
        assertEquals ((1*60*60) + (3*60) + 45, exercise.heartRateLimits[0].timeWithin)
        assertEquals ((0*60*60) + (4*60) + 15, exercise.heartRateLimits[0].timeAbove)

        assertEquals (80, exercise.heartRateLimits[1].lowerHeartRate)
        assertEquals (160, exercise.heartRateLimits[1].upperHeartRate)
        assertEquals ((0*60*60) + (5*60) + 0, exercise.heartRateLimits[1].timeBelow)   // same as for first range, but they should be different ???
        assertEquals ((1*60*60) + (3*60) + 45, exercise.heartRateLimits[1].timeWithin) // same as for first range, but they should be different ???
        assertEquals ((0*60*60) + (4*60) + 15, exercise.heartRateLimits[1].timeAbove)  // same as for first range, but they should be different ???

        assertEquals (80, exercise.heartRateLimits[2].lowerHeartRate)
        assertEquals (160, exercise.heartRateLimits[2].upperHeartRate)
        assertEquals ((0*60*60) + (0*60) + 0, exercise.heartRateLimits[2].timeBelow)  // 0 for some reason, although should be same as for second range ???
        assertEquals ((0*60*60) + (0*60) + 0, exercise.heartRateLimits[2].timeWithin) // 0 for some reason, although should be same as for second range ???
        assertEquals ((0*60*60) + (0*60) + 0, exercise.heartRateLimits[2].timeAbove)  // 0 for some reason, although should be same as for second range ???
        
        // check lap data (first, one from middle and last lap only)
        assertEquals (5, exercise.lapList.size ())
        assertEquals ((0*60*60*10) + (06*60*10) + (59*10) + 2, exercise.lapList[0].timeSplit)
        assertEquals (136, exercise.lapList[0].heartRateSplit)
        assertEquals (128, exercise.lapList[0].heartRateAVG, 128)
        assertEquals (151, exercise.lapList[0].heartRateMax, 151)
        assertEquals (141, Math.round (exercise.lapList[0].speed.speedEnd * 10))
        assertEquals (258, Math.round (exercise.lapList[0].speed.speedAVG * 10))
        assertEquals (2998, exercise.lapList[0].speed.distance)
        assertEquals (0, exercise.lapList[0].speed.cadence)
        assertEquals (231, exercise.lapList[0].altitude.altitude)
        // assertEquals (25, exercise.lapList[0].altitude.ascent) // (not in HRM file)
        assertEquals (4, exercise.lapList[0].temperature.temperature)
        
        assertEquals ((0*60*60*10) + (40*60*10) + (18*10) + 8, exercise.lapList[2].timeSplit)
        assertEquals (136, exercise.lapList[2].heartRateSplit)
        assertEquals (133, exercise.lapList[2].heartRateAVG)
        assertEquals (168, exercise.lapList[2].heartRateMax)
        assertEquals (193, Math.round (exercise.lapList[2].speed.speedEnd * 10))
        assertEquals (247, Math.round (exercise.lapList[2].speed.speedAVG * 10))
        assertEquals (15874, exercise.lapList[2].speed.distance)
        assertEquals (0, exercise.lapList[2].speed.cadence)
        assertEquals (247, exercise.lapList[2].altitude.altitude)
        // assertEquals (135, exercise.lapList[2].altitude.ascent) // (not in HRM file)
        assertEquals (4, exercise.lapList[2].temperature.temperature)

        assertEquals ((1*60*60*10) + (13*60*10) + (34*10) + 3, exercise.lapList[4].timeSplit)
        assertEquals (123, exercise.lapList[4].heartRateSplit)
        assertEquals (60, exercise.lapList[4].heartRateAVG) // this value is impossible ???
        assertEquals (121, exercise.lapList[4].heartRateMax) // hoe can this be, the split HR is higher ???
        assertEquals (0, Math.round (exercise.lapList[4].speed.speedEnd * 10))
        assertEquals (0, Math.round (exercise.lapList[4].speed.speedAVG * 10))
        assertEquals (29900, exercise.lapList[4].speed.distance)
        assertEquals (0, exercise.lapList[4].speed.cadence)
        assertEquals (229, exercise.lapList[4].altitude.altitude)
        // assertEquals (240, exercise.lapList[4].altitude.ascent) // (not in HRM file)
        assertEquals (4, exercise.lapList[4].temperature.temperature)

        // check sample data (first, two from middle and last only)
        assertEquals (294, exercise.sampleList.size ())
        assertEquals (0, exercise.sampleList[0].timestamp)        
        assertEquals (101, exercise.sampleList[0].heartRate)
        assertEquals (240, exercise.sampleList[0].altitude)
        assertEquals (42, Math.round (exercise.sampleList[0].speed * 10))
        assertEquals (0, exercise.sampleList[0].cadence)
        assertEquals (0, exercise.sampleList[0].distance)

        assertEquals (100*15*1000, exercise.sampleList[100].timestamp)        
        assertEquals (147, exercise.sampleList[100].heartRate)
        assertEquals (278, exercise.sampleList[100].altitude)
        assertEquals (171, Math.round (exercise.sampleList[100].speed * 10))
        assertEquals (0, exercise.sampleList[100].cadence)
        assertEquals (9499, exercise.sampleList[100].distance)

        assertEquals (200*15*1000, exercise.sampleList[200].timestamp)        
        assertEquals (166, exercise.sampleList[200].heartRate)
        assertEquals (275, exercise.sampleList[200].altitude)
        assertEquals (141, Math.round (exercise.sampleList[200].speed * 10))
        assertEquals (0, exercise.sampleList[200].cadence)
        assertEquals (19296, exercise.sampleList[200].distance)

        assertEquals (293*15*1000, exercise.sampleList[293].timestamp)        
        assertEquals (121, exercise.sampleList[293].heartRate)
        assertEquals (228, exercise.sampleList[293].altitude)
        assertEquals (153, Math.round (exercise.sampleList[293].speed * 10))
        assertEquals (0, exercise.sampleList[293].cadence)
        assertEquals (29900, exercise.sampleList[293].distance)
    }

    /**
     * This method tests the parser by with an cycling exercise file
     * recorded in english units.
     */
    void testParseCyclingExerciseWithEnglishUnits ()
    {
        // parse exercise file
        def exercise = parser.parseExercise ('misc/testdata/s710/cycling-english.hrm')

        // check exercise data
        assertEquals (exercise.fileType, EVExercise.ExerciseFileType.HRM)
        assertEquals (exercise.userID, 0)
        // assertEquals (exercise.Type, 1) // (not in HRM file)
        // assertEquals (exercise.TypeLabel, "ExeSet1") // (not in HRM file)
        assertTrue (exercise.recordingMode.altitude)
        assertTrue (exercise.recordingMode.speed)
        assertFalse (exercise.recordingMode.cadence)
        assertFalse (exercise.recordingMode.power)
        // assertEquals (exercise.recordingMode.BikeNumber, 2) // (not in HRM file)

        def calDate = Calendar.getInstance ()
        calDate.set (2002, 11-1, 20, 13, 10, 42)
        assertEquals ((int) (calDate.time.time / 1000), (int) (exercise.date.time / 1000))
        assertEquals ((0*60*60*10) + (51*60*10) + 0*10, exercise.duration)
        assertEquals (15, exercise.recordingInterval)

        assertEquals (exercise.heartRateAVG, 136)
        assertEquals (exercise.heartRateMax, 232)
        assertEquals (Math.round (exercise.speed.speedAVG * 10), 247)
        assertEquals (Math.round (exercise.speed.speedMax * 10), 1077)
        assertEquals (exercise.speed.distance, 20921)
        assertNull (exercise.cadence)
        assertEquals (exercise.altitude.altitudeMin, 221)
        assertEquals (exercise.altitude.altitudeAVG, 245)
        assertEquals (exercise.altitude.altitudeMax, 277)
        // assertEquals (exercise.temperature.temperatureMin, 3) // (not in HRM file)
        // assertEquals (exercise.temperature.temperatureAVG, 4) // (not in HRM file)
        // assertEquals (exercise.temperature.temperatureMax, 15) // (not in HRM file)
        // assertEquals (exercise.energy, 418) // (not in HRM file)
        // assertEquals (exercise.energyTotal, 23508) // (not in HRM file)
        // assertEquals (exercise.sumExerciseTime, (55*60) + 21) // (not in HRM file)
        // assertEquals (exercise.sumRideTime, (41*60) + 45)     // (not in HRM file)
        assertEquals (exercise.odometer, 993)

        // check heart rate limits
        assertEquals (exercise.heartRateLimits.size (), 3)
        assertEquals (exercise.heartRateLimits[0].lowerHeartRate, 120)
        assertEquals (exercise.heartRateLimits[0].upperHeartRate, 155)
        assertEquals (exercise.heartRateLimits[0].timeBelow, (0*60*60) + (1*60) + 45)
        assertEquals (exercise.heartRateLimits[0].timeWithin, (0*60*60) + (45*60) + 15)
        assertEquals (exercise.heartRateLimits[0].timeAbove, (0*60*60) + (2*60) + 0)

        assertEquals (exercise.heartRateLimits[1].lowerHeartRate, 80)
        assertEquals (exercise.heartRateLimits[1].upperHeartRate, 160)
        assertEquals (exercise.heartRateLimits[1].timeBelow, (0*60*60) + (1*60) + 45) // same as for first range, but they should be different ???
        assertEquals (exercise.heartRateLimits[1].timeWithin, (0*60*60) + (45*60) + 15) // same as for first range, but they should be different ???
        assertEquals (exercise.heartRateLimits[1].timeAbove, (0*60*60) + (2*60) + 0) // same as for first range, but they should be different ???

        assertEquals (exercise.heartRateLimits[2].lowerHeartRate, 80)
        assertEquals (exercise.heartRateLimits[2].upperHeartRate, 160)
        assertEquals (exercise.heartRateLimits[2].timeBelow, (0*60*60) + (0*60) + 0) // 0 for some reason, although should be same as for second range ???
        assertEquals (exercise.heartRateLimits[2].timeWithin, (0*60*60) + (0*60) + 0) // 0 for some reason, although should be same as for second range ???
        assertEquals (exercise.heartRateLimits[2].timeAbove, (0*60*60) + (0*60) + 0) // 0 for some reason, although should be same as for second range ???

        // check lap data (first, one from middle and last lap only)
        assertEquals (exercise.lapList.size (), 4)
        assertEquals (exercise.lapList[0].timeSplit, (0*60*60*10) + (20*60*10) + (34*10) + 6)
        assertEquals (exercise.lapList[0].heartRateSplit, 143)
        assertEquals (exercise.lapList[0].heartRateAVG, 140)
        assertEquals (exercise.lapList[0].heartRateMax, 232)
        assertEquals (Math.round (exercise.lapList[0].speed.speedEnd * 10), 206)
        assertEquals (Math.round (exercise.lapList[0].speed.speedAVG * 10), 243)
        assertEquals (exercise.lapList[0].speed.distance, 8332)
        assertEquals (exercise.lapList[0].speed.cadence, 0)
        assertEquals (exercise.lapList[0].altitude.altitude, 273)
        // assertEquals (exercise.lapList[0].altitude.ascent, 73) // (not in HRM file)
        assertEquals (exercise.lapList[0].temperature.temperature, 3)

        assertEquals (exercise.lapList[1].timeSplit, (0*60*60*10) + (46*60*10) + (51*10) + 2)
        assertEquals (exercise.lapList[1].heartRateSplit, 129)
        assertEquals (exercise.lapList[1].heartRateAVG, 133)
        assertEquals (exercise.lapList[1].heartRateMax, 159)
        assertEquals (Math.round (exercise.lapList[1].speed.speedEnd * 10), 352)
        assertEquals (Math.round (exercise.lapList[1].speed.speedAVG * 10), 254)
        assertEquals (exercise.lapList[1].speed.distance, 19437)
        assertEquals (exercise.lapList[1].speed.cadence, 0)
        assertEquals (exercise.lapList[1].altitude.altitude, 248)
        // assertEquals (exercise.lapList[1].altitude.ascent, 146) // (not in HRM file)
        assertEquals (exercise.lapList[1].temperature.temperature, 3)

        assertEquals (exercise.lapList[3].timeSplit, (0*60*60*10) + (51*60*10) + (22*10) + 6)
        assertEquals (exercise.lapList[3].heartRateSplit, 116)
        assertEquals (exercise.lapList[3].heartRateAVG, 60) // this value is impossible ???
        assertEquals (exercise.lapList[3].heartRateMax, 121)
        assertEquals (Math.round (exercise.lapList[3].speed.speedEnd * 10), 0*10)
        assertEquals (Math.round (exercise.lapList[3].speed.speedAVG * 10), 0*10)
        assertEquals (exercise.lapList[3].speed.distance, 20921)
        assertEquals (exercise.lapList[3].speed.cadence, 0)
        assertEquals (exercise.lapList[3].altitude.altitude, 239)
        // assertEquals (exercise.lapList[3].altitude.ascent, 152) // (not in HRM file)
        assertEquals (exercise.lapList[3].temperature.temperature, 4)

        // check sample data (first, two from middle and last only)
        assertEquals (exercise.sampleList.size (), 205)
        assertEquals (exercise.sampleList[0].heartRate, 83)
        assertEquals (exercise.sampleList[0].altitude, 221)
        assertEquals (Math.round (exercise.sampleList[0].speed * 10), 0*10)
        assertEquals (exercise.sampleList[0].cadence, 0)
        assertEquals (exercise.sampleList[0].distance, 0)

        assertEquals (100*15*1000, exercise.sampleList[100].timestamp)        
        assertEquals (exercise.sampleList[100].heartRate, 124)
        assertEquals (exercise.sampleList[100].altitude, 270)
        assertEquals (Math.round (exercise.sampleList[100].speed * 10), 351)
        assertEquals (exercise.sampleList[100].cadence, 0)
        assertEquals (exercise.sampleList[100].distance, 9970)

        assertEquals (200*15*1000, exercise.sampleList[200].timestamp)        
        assertEquals (exercise.sampleList[200].heartRate, 138)
        assertEquals (exercise.sampleList[200].altitude, 242)
        assertEquals (Math.round (exercise.sampleList[200].speed * 10), 291)
        assertEquals (exercise.sampleList[200].cadence, 0)
        assertEquals (exercise.sampleList[200].distance, 20452)

        assertEquals (204*15*1000, exercise.sampleList[204].timestamp)        
        assertEquals (exercise.sampleList[204].heartRate, 121)
        assertEquals (exercise.sampleList[204].altitude, 241)
        assertEquals (Math.round (exercise.sampleList[204].speed * 10), 0)
        assertEquals (exercise.sampleList[204].cadence, 0)
        assertEquals (exercise.sampleList[204].distance, 20921)
    }
    
    /**
     * This method tests the parser by with an running exercise file
     * recorded in metric units.
     */
    void testParseRunningExerciseWithMetricUnits ()
    {
        def exercise = parser.parseExercise ('misc/testdata/s710/running-metric.hrm')

        // check exercise data
        assertEquals (exercise.fileType, EVExercise.ExerciseFileType.HRM)
        assertEquals (exercise.userID, 0)
        // assertEquals (exercise.type, 2) // (not in HRM file)
        // assertEquals (exercise.typeLabel, "ExeSet2") // (not in HRM file)
        assertTrue (exercise.recordingMode.altitude)
        assertFalse (exercise.recordingMode.speed)
        assertFalse (exercise.recordingMode.cadence)
        assertFalse (exercise.recordingMode.power)
        // assertEquals (exercise.recordingMode.bikeNumber, 0) // (not in HRM file)
        
        def calDate = Calendar.getInstance ()
        calDate.set (2002, 12-1, 25, 10, 21, 04)
        assertEquals ((int) (calDate.time.time / 1000), (int) (exercise.date.time / 1000))
        assertEquals ((0*60*60*10) + (42*60*10) + 24*10 + 7, exercise.duration)
        assertEquals (15, exercise.recordingInterval)
        
        assertEquals (exercise.heartRateAVG, 147)
        assertEquals (exercise.heartRateMax, 158)
        assertNull (exercise.speed)
        assertNull (exercise.cadence)
        assertEquals (exercise.altitude.altitudeMin, 86)
        assertEquals (exercise.altitude.altitudeAVG, 93)
        assertEquals (exercise.altitude.altitudeMax, 101)
        // assertEquals (exercise.temperature.temperatureMin, 12) // (not in HRM file)
        // assertEquals (exercise.temperature.temperatureAVG, 15) // (not in HRM file)
        // assertEquals (exercise.temperature.temperatureMax, 21) // (not in HRM file)
        // assertEquals (exercise.energy, 399) // (not in HRM file)
        // assertEquals (exercise.energyTotal, 30058) // (not in HRM file)
        // assertEquals (exercise.sumExerciseTime, (72*60) + 7) // (not in HRM file)
        // assertEquals (exercise.sumRideTime, (51*60) + 54) // (not in HRM file)
        assertEquals (exercise.odometer, 1200)

        // check heart rate limits
        assertEquals (exercise.heartRateLimits.size (), 3)
        assertEquals (exercise.heartRateLimits[0].lowerHeartRate, 130)
        assertEquals (exercise.heartRateLimits[0].upperHeartRate, 150)
        assertEquals (exercise.heartRateLimits[0].timeBelow, (0*60*60) + (0*60) + 45)
        assertEquals (exercise.heartRateLimits[0].timeWithin, (0*60*60) + (31*60) + 0)
        assertEquals (exercise.heartRateLimits[0].timeAbove, (0*60*60) + (10*60) + 15)

        assertEquals (exercise.heartRateLimits[1].lowerHeartRate, 80)
        assertEquals (exercise.heartRateLimits[1].upperHeartRate, 160)
        assertEquals (exercise.heartRateLimits[1].timeBelow, (0*60*60) + (0*60) + 45) // same as for first range, but they should be different ???
        assertEquals (exercise.heartRateLimits[1].timeWithin, (0*60*60) + (31*60) + 0) // same as for first range, but they should be different ???
        assertEquals (exercise.heartRateLimits[1].timeAbove, (0*60*60) + (10*60) + 15) // same as for first range, but they should be different ???

        assertEquals (exercise.heartRateLimits[2].lowerHeartRate, 80)
        assertEquals (exercise.heartRateLimits[2].upperHeartRate, 160)
        assertEquals (exercise.heartRateLimits[2].timeBelow, (0*60*60) + (0*60) + 0) // 0 for some reason, although should be same as for second range ???
        assertEquals (exercise.heartRateLimits[2].timeWithin, (0*60*60) + (0*60) + 0) // 0 for some reason, although should be same as for second range ???
        assertEquals (exercise.heartRateLimits[2].timeAbove, (0*60*60) + (0*60) + 0) // 0 for some reason, although should be same as for second range ???

        // check lap data (one lap only)
        assertEquals (exercise.lapList.size (), 1)
        assertEquals (exercise.lapList[0].timeSplit, (0*60*60*10) + (42*60*10) + (24*10) + 7)
        assertEquals (exercise.lapList[0].heartRateSplit, 146)
        assertEquals (exercise.lapList[0].heartRateAVG, 146)
        assertEquals (exercise.lapList[0].heartRateMax, 158)
        assertEquals (exercise.lapList[0].speed, null)
        assertEquals (exercise.lapList[0].altitude.altitude, 88)
        // assertEquals (exercise.lapList[0].altitude.ascent, 20) // (not in HRM file)
        assertEquals (exercise.lapList[0].temperature.temperature, 19)

        // check sample data (first, two from middle and last only)
        assertEquals (exercise.sampleList.size (), 170)
        assertEquals (0, exercise.sampleList[0].timestamp)        
        assertEquals (exercise.sampleList[0].heartRate, 0)
        assertEquals (exercise.sampleList[0].altitude, 91)
        assertEquals (exercise.sampleList[0].speed, 0f, 0f)
        assertEquals (exercise.sampleList[0].cadence, 0)
        assertEquals (exercise.sampleList[0].distance, 0)

        assertEquals (100*15*1000, exercise.sampleList[100].timestamp)        
        assertEquals (exercise.sampleList[100].heartRate, 149)
        assertEquals (exercise.sampleList[100].altitude, 98)
        assertEquals (exercise.sampleList[100].speed, 0f, 0.0001f)
        assertEquals (exercise.sampleList[100].cadence, 0)
        assertEquals (exercise.sampleList[100].distance, 0)

        assertEquals (150*15*1000, exercise.sampleList[150].timestamp)        
        assertEquals (exercise.sampleList[150].heartRate, 142)
        assertEquals (exercise.sampleList[150].altitude, 89)
        assertEquals (exercise.sampleList[150].speed, 0f, 0.0001f)
        assertEquals (exercise.sampleList[150].cadence, 0)
        assertEquals (exercise.sampleList[150].distance, 0)

        assertEquals (169*15*1000, exercise.sampleList[169].timestamp)        
        assertEquals (exercise.sampleList[169].heartRate, 147)
        assertEquals (exercise.sampleList[169].altitude, 88)
        assertEquals (exercise.sampleList[169].speed, 0f, 0.0001f)
        assertEquals (exercise.sampleList[169].cadence, 0)
        assertEquals (exercise.sampleList[169].distance, 0) 
    }
}
