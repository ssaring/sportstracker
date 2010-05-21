package de.saring.exerciseviewer.parser.impl

import de.saring.exerciseviewer.core.PVException
import de.saring.exerciseviewer.data.*
import de.saring.exerciseviewer.parser.*
import de.saring.util.unitcalc.ConvertUtils

/**
 * This implementation of an ExerciseParser is for reading the common 
 * HRM files of Polar heartrate monitors.
 * These files have the extension ".hrm". They can be read from the
 * Polar device using the software shipped with most Polar devices.
 * The format description is taken from the file "Polar_HRM2_file_format.pdf".
 * 
 * @author  Stefan Saring
 * @version 1.0
 */
class PolarHRMParser extends AbstractExerciseParser {

    /** Informations about this parser. */
    private ExerciseParserInfo info = new ExerciseParserInfo ('Polar HRM', ["hrm", "HRM"] as String[])

    /** The list of lines (Strings) of the exercise file. */
     private def fileContent

    /**
     * Returns the informations about this parser implementation.
     * @return the parser informations
     */
    @Override
    ExerciseParserInfo getInfo () {
        info
    }
		
    /**
     * This method parses the specified exercise file and creates an
     * PVExercise object from it.
     *
     * @param filename name of exercise file to parse
     * @return the parsed PVExercise object
     * @throws PVException thrown on read/parse problems
     */
    @Override
    PVExercise parseExercise (String filename) throws PVException
    {
        try {
            fileContent = new File (filename).readLines ()
            return parseExerciseFromContent ()
        }
        catch (Exception e) {
            throw new PVException ("Failed to read the HRM exercise file '${filename}' ...", e)
        }
    }
    
    /**
     * Parses the exercise data from the file content.
     */
    private PVExercise parseExerciseFromContent ()
    {
        // parse basic exercise data
        PVExercise exercise = new PVExercise ()
        exercise.fileType = PVExercise.ExerciseFileType.HRM
        
        //////////////////////////////////////////////////////////////////////
        // parse 'Params' block
        
        // get lines of 'Params' block
        def lParamsBlock = getBlockLines ('Params', true)
                    
        // check HRM file version
        def strVersion = getValueFromBlock (lParamsBlock, "Version")
        if (strVersion != '106' && strVersion != '107') {
            throw new PVException ("Failed to read HRM file, the version needs to be '106' or '107' ...")
        }
            
        // parse recording mode informations
        // (since Polar S720 the length can be 9 instead of 8, althought the HRM version is still 1.06)
        def strSMode = getValueFromBlock (lParamsBlock, 'SMode')
        exercise.recordingMode = new RecordingMode ()
        exercise.recordingMode.speed = strSMode[0] == '1'
        exercise.recordingMode.cadence = strSMode[1] == '1'
        exercise.recordingMode.altitude = strSMode[2] == '1'
        exercise.recordingMode.power = strSMode[3] == '1'
        // TODO: the bikenumber is not decoded yet
        
        // does the HRM file uses metric or english units ?
        def fMetricUnits = strSMode[7] == '0'
        
        // parse exercise date (yyyymmdd)
        def strDate = getValueFromBlock (lParamsBlock, 'Date')
        def exeYear = strDate.substring (0, 4).toInteger ()
        def exeMonth = strDate.substring (4, 6).toInteger () - 1
        def exeDay = strDate.substring (6, 8).toInteger ()

        // parse exercise start time (can be either h:mm:ss.d or hh:mm:ss.d !)
        def strStartTime = getValueFromBlock (lParamsBlock, 'StartTime')
        def startTimeSplitted = strStartTime.tokenize (':.')
        if (startTimeSplitted.size () != 4) {
            throw new PVException ("Failed to read HRM file, can't parse exercise start time (wrong format) ...");
        }
        
        // parse start time (the 1/10th second part will be ignored)
        def exeHour = startTimeSplitted[0].toInteger ()
        def exeMinute = startTimeSplitted[1].toInteger ()
        def exeSecond = startTimeSplitted[2].toInteger ()        
        def calDate = Calendar.getInstance ()
        calDate.set (exeYear, exeMonth, exeDay, exeHour, exeMinute, exeSecond)
        exercise.date = calDate.time        

        // parse exercise duration (can be either h:mm:ss.d or hh:mm:ss.d !)
        def strDuration = getValueFromBlock (lParamsBlock, 'Length')
        def durationSplitted = strDuration.tokenize (':.')
        if (durationSplitted.size () != 4)  {
            throw new PVException ("Failed to read HRM file, can't parse exercise duration (wrong format) ...")
        }
        
        // parse start time (the 1/10th second part will be ignored)
        def durHour = durationSplitted[0].toInteger ()
        def durMinute = durationSplitted[1].toInteger ()
        def durSecond = durationSplitted[2].toInteger ()
        def durTenthOfSecond = durationSplitted[3].toInteger ()
        exercise.duration = (durHour * 60 * 60 * 10) + (durMinute * 60 * 10) + durSecond * 10 + durTenthOfSecond
        
        // parse interval
        def strInterval = getValueFromBlock (lParamsBlock, 'Interval')
        exercise.recordingInterval = strInterval.toInteger ()

        // ignore Upper1, Lower1, ... Lower3, they're again in block Summary-123        
        // ignore Timer1,Timer2,Timer3, ActiveLimit, MaxHR, RestHR, StartDelay, VO2max, Weight

        //////////////////////////////////////////////////////////////////////
        // parse 'IntTimes' block (Lap times)
        
        // get lines of 'IntTimes' block (can be empty when 0 laps, e.g. for Polar S510)
        def lIntTimesBlock = getBlockLines ('IntTimes', false)
        if (lIntTimesBlock.size () % 5 != 0) {
            throw new PVException ("Failed to read HRM file, invalid number of lines in block 'IntTimes' ...")          
        }
        
        // parse all laps of exercise (each lap consists of 5 lines)
        def numberOfLaps = lIntTimesBlock.size () / 5
        exercise.lapList = new Lap[numberOfLaps]
        def lapDistanceAccumulated = 0
        
        for (i in 0..<numberOfLaps) {
            exercise.lapList[i] = new Lap ()
            
            // 1. lap line needs to be of 5 parts
            def currLapLineSplitted = lIntTimesBlock[i * 5].tokenize ('\t')
            if (currLapLineSplitted.size () != 5) {
                throw new PVException ("Failed to read HRM file, can't parse 1. line of current lap in block 'IntTimes' ...")           
            }
            
            // parse lap split time (1. part) (can be either h:mm:ss.d or hh:mm:ss.d !)
            def lapSplitTimeSplitted = currLapLineSplitted[0].tokenize (':.')
            if (lapSplitTimeSplitted.size () != 4)  {
                throw new PVException ("Failed to read HRM file, can't parse lap split time of current lap (wrong format) ...")
            }
            
            def lapSplitTimeHour = lapSplitTimeSplitted[0].toInteger ()
            def lapSplitTimeMinute = lapSplitTimeSplitted[1].toInteger ()
            def lapSplitTimeSecond = lapSplitTimeSplitted[2].toInteger ()
            def lapSplitTimeTenthSecond = lapSplitTimeSplitted[3].toInteger ()                      
            exercise.lapList[i].timeSplit = (lapSplitTimeHour * 60 * 60 * 10) + (lapSplitTimeMinute * 60 * 10) + (lapSplitTimeSecond * 10) + lapSplitTimeTenthSecond
            
            // get lap heartrate values (the other parts of the first line)
            exercise.lapList[i].heartRateSplit = currLapLineSplitted[1].toInteger ()
            // minimum lap heartrate will be ignored
            exercise.lapList[i].heartRateAVG = currLapLineSplitted[3].toInteger ()
            exercise.lapList[i].heartRateMax = currLapLineSplitted[4].toInteger ()
            
            // parse 2. lap line (needs to be of 6 parts)
            currLapLineSplitted = lIntTimesBlock[(i * 5) + 1].tokenize ('\t')
            if (currLapLineSplitted.size () != 6) {
                throw new PVException ("Failed to read HRM file, can't parse 2. line of current lap in block 'IntTimes' ...")           
            }
                            
            // parse speed and cadence at lap split
            if (exercise.recordingMode.speed) {
                def lapSpeed = currLapLineSplitted[3].toInteger () / 10f
                if (!fMetricUnits) {
                    lapSpeed = ConvertUtils.convertMiles2Kilometer (lapSpeed)
                }
                
                exercise.lapList[i].speed = new LapSpeed ()                
                exercise.lapList[i].speed.speedEnd = lapSpeed
                exercise.lapList[i].speed.cadence = currLapLineSplitted[4].toInteger ()
            }
            
            // parse altitude at lap split
            if (exercise.recordingMode.altitude) {
                def lapAltitude = currLapLineSplitted[5].toInteger ()
                if (!fMetricUnits) {
                    lapAltitude = ConvertUtils.convertFeet2Meter (lapAltitude)
                }
                
                exercise.lapList[i].altitude = new LapAltitude ()                
                exercise.lapList[i].altitude.altitude = lapAltitude
                // lap ascent can't be read from HRM files
            }
            
            // the 3. lap line can be ignored completely
            
            // parse 4. lap line (needs to be of 6 parts)
            currLapLineSplitted = lIntTimesBlock[(i * 5) + 3].tokenize ('\t')
            if (currLapLineSplitted.size () != 6) {
                throw new PVException ("Failed to read HRM file, can't parse 4. line of current lap in block 'IntTimes' ...")           
            }
            
            // get lap distance
            if (exercise.recordingMode.speed) {
                def lapDistance = currLapLineSplitted[1].toInteger ()
                if (!fMetricUnits) {
                    // TODO: is not consistent for english units - 
                    // documentation says, it's in yards => so meters = yards * 0.9144),
                    // but then accumulation does not work
                }
                
                // distance needs to be accumulated
                lapDistanceAccumulated += lapDistance
                exercise.lapList[i].speed.distance = lapDistanceAccumulated
            }
            
            // get lap temperature
            if (exercise.recordingMode.altitude) {
                exercise.lapList[i].temperature = new LapTemperature ()                    
                
                if (fMetricUnits) {
                    // temperature is C / 10
                    def lapTemperature = currLapLineSplitted[3].toInteger ()
                    exercise.lapList[i].temperature.temperature = lapTemperature / 10
                }
                else {
                    // temperature is Fahreinheit / 10
                    short lapTemperature = currLapLineSplitted[3].toInteger () / 10
                    exercise.lapList[i].temperature.temperature = ConvertUtils.convertFahrenheit2Celsius (lapTemperature)
                }
            }

            // the 5. lap line can be ignored completely
        }
            
        //////////////////////////////////////////////////////////////////////
        // parse 'Summary-123' block (Lap times)
        
        // get lines of 'Summary-123' block
        // (mostly 7 lines, 8 lines for Polar CS600, data of last line is unknown)
        def lSummary123Block = getBlockLines ("Summary-123", true)
        if (lSummary123Block.size () < 7) {
            throw new PVException ("Failed to read HRM file, can't find block 'Summary-123' or block is not valid ...")         
        }
        
        // parse data for 3 heartrate limit ranges
        // TODO: second and third HR ranges does not have sensefull content
        exercise.heartRateLimits = new HeartRateLimit[3]        
        for (i in 0..<3) {
            
            // 1. heratrate limits info line needs to be of 6 parts
            def firstHRLLineSplitted = lSummary123Block[i * 2].tokenize ('\t')
            if (firstHRLLineSplitted.size () != 6) {
                throw new PVException ("Failed to read HRM file, can't parse 1. line of current heartrate limits in block 'Summary-123' ...")           
            }
            
            // get seconds below, within and above current heartrate range
            exercise.heartRateLimits[i] = new HeartRateLimit ();
            exercise.heartRateLimits[i].timeAbove = firstHRLLineSplitted[2].toInteger ()
            exercise.heartRateLimits[i].timeWithin = firstHRLLineSplitted[3].toInteger ()
            exercise.heartRateLimits[i].timeBelow = firstHRLLineSplitted[4].toInteger ()
            
            // 2. heratrate limits info line needs to be of 4 parts
            def secondHRLLineSplitted = lSummary123Block[(i * 2) + 1].tokenize ('\t')
            if (secondHRLLineSplitted.size () != 4) {
                throw new PVException ("Failed to read HRM file, can't parse 2. line of current heartrate limits in block 'Summary-123' ...")           
            }
            
            exercise.heartRateLimits[i].upperHeartRate = secondHRLLineSplitted[1].toInteger ()                
            exercise.heartRateLimits[i].lowerHeartRate = secondHRLLineSplitted[2].toInteger ()
            
            // TODO: When the monitor displays heartrate and ranges in percent instead in bpm
            // the heartrate limit ranges in the HRM files are also stored in percent. But it's
            // not possible yet to determine whether it's bpm (default) or percent. That's 
            // why the parses always assumes bpm values.
            //
            // if ('are values stored in percent instead of bpm') {
            //     // => calculate the BPM with help of max. heartrate => this should work ...  
            //     def maxHR = secondHRLLineSplitted[0].toInteger ()                
            //     exercise.heartRateLimits[i].lowerHeartRate = (maxHR * exercise.heartRateLimits[i].lowerHeartRate) / 100f
            //     exercise.heartRateLimits[i].upperHeartRate = (maxHR * exercise.heartRateLimits[i].upperHeartRate) / 100f
            // } 
        }
        
        // ignore 'Summary-TH', 'HRZones' and 'SwapTimes' block
        
        //////////////////////////////////////////////////////////////////////////
        // parse 'Trip' block (Cycling data) (it's not in all files, e.g. on S410 or S610)

        // get lines of 'Trip' block
        def lTripBlock = getBlockLines ('Trip', false)
        if (lTripBlock.size () == 8) 
        {
            // parse speed informations
            if (exercise.recordingMode.speed) 
            {
                exercise.speed = new ExerciseSpeed ()
                exercise.speed.distance = lTripBlock[0].toInteger () * 100
                exercise.speed.speedAVG = lTripBlock[5].toInteger () / 128f
                // ignore maximum speed data, it is often wrong for many Polar models (will be calculated later) 
                
                if (!fMetricUnits) {
                    exercise.speed.distance = ConvertUtils.convertMiles2Kilometer (exercise.speed.distance)
                    exercise.speed.speedAVG = ConvertUtils.convertMiles2Kilometer (exercise.speed.speedAVG)
                }
                
                // now we have the exercise distance, that's why the lap distances needs to be corrected
                // (in HRM file format is an error, the distances of the last laps are often greater then
                // the complete exercise distance, so the greater values needs to be set to exercise
                // distance)
                if (exercise.lapList != null) {
                    for (i in 0..<exercise.lapList.size ()) {
                        exercise.lapList[i].speed.distance = Math.min (
                            exercise.lapList[i].speed.distance, exercise.speed.distance)
                    }
                }
            }
            
            // parse altitude informations
            if (exercise.recordingMode.altitude)
            {
                exercise.altitude = new ExerciseAltitude ()
                exercise.altitude.ascent = lTripBlock[1].toInteger ()
                // minimum exercise altitude is not available in HRM files
                exercise.altitude.altitudeAVG = lTripBlock[3].toInteger ()
                exercise.altitude.altitudeMax = lTripBlock[4].toInteger ()
                
                if (!fMetricUnits) {
                    exercise.altitude.ascent = ConvertUtils.convertFeet2Meter (exercise.altitude.ascent)
                    exercise.altitude.altitudeAVG = ConvertUtils.convertFeet2Meter (exercise.altitude.altitudeAVG)
                    exercise.altitude.altitudeMax = ConvertUtils.convertFeet2Meter (exercise.altitude.altitudeMax)
                }
            }
            
            // parse odometer value
            exercise.odometer = lTripBlock[7].toInteger ()
            if (!fMetricUnits) {
                exercise.odometer = ConvertUtils.convertMiles2Kilometer (exercise.odometer)
            }
        }

        //////////////////////////////////////////////////////////////////////
        // parse 'HRData' block (Sample data)
        
        // get lines of 'HRData' block
        def lHRDataBlock = getBlockLines ('HRData', true)
        
        // create array of exercise sample
        exercise.sampleList = new ExerciseSample[lHRDataBlock.size ()]
        
        // parse each exercise sample line
        for (i in 0..<lHRDataBlock.size ()) 
        {
            def tokenIndex = 0
            exercise.sampleList[i] = new ExerciseSample ()
            
            // split sample line into parts
            def currSampleSplitted = lHRDataBlock[i].tokenize ('\t')
            
            // 1. part is heartrate
            exercise.sampleList[i].heartRate = currSampleSplitted[tokenIndex].toInteger ()
            tokenIndex++
            
            // next part can be speed, when recorded
            if ((currSampleSplitted.size () > tokenIndex) &&
                (exercise.recordingMode.speed))
            {
                // speed is km/h or m/h * 10
                def speedX10 = currSampleSplitted[tokenIndex].toInteger ()
                if (!fMetricUnits) {
                    speedX10 = ConvertUtils.convertMiles2Kilometer (speedX10)
                }
                
                exercise.sampleList[i].speed = speedX10 / 10f
                tokenIndex++
            }
            
            // next part can be cadence, when recorded
            if ((currSampleSplitted.size () > tokenIndex) &&
                (exercise.recordingMode.cadence)) 
            {
                exercise.sampleList[i].cadence = currSampleSplitted[tokenIndex].toInteger ()
                tokenIndex++
            }
            
            // next part can be altitude, when recorded
            if ((currSampleSplitted.size () > tokenIndex) &&
                (exercise.recordingMode.altitude))
            {
                def altitude = currSampleSplitted[tokenIndex].toInteger ()
                if (!fMetricUnits) {
                    altitude = ConvertUtils.convertFeet2Meter (altitude)
                }
                
                exercise.sampleList[i].altitude = altitude
                tokenIndex++
            }
        }

        // when speed is recorded:
        // - calculate distance for each recorded sample (distance is not recorded for each sample)
        // - find the maximum speed from samples (max speed is stored in HRM files, but often a wrong value)
        if (exercise.recordingMode.speed) {            
            def distanceAccum = 0f
            exercise.speed.speedMax = 0f
                            
            for (i in 0..<exercise.sampleList.size ())  {
                exercise.sampleList[i].distance = distanceAccum
                distanceAccum += (exercise.sampleList[i].speed * exercise.recordingInterval) / 3.6f
                exercise.speed.speedMax = Math.max (exercise.sampleList[i].speed, exercise.speed.speedMax)
            }
        }
        
        // compute average/maximum heartrate of exercise (not in HRM file)
        def avgHeartrateSum = 0
        exercise.heartRateMax = 0
        
        for (i in 0..<exercise.sampleList.size ())  {
            avgHeartrateSum += exercise.sampleList[i].heartRate
            exercise.heartRateMax = Math.max (exercise.sampleList[i].heartRate, exercise.heartRateMax)
        }
        
        // calculate AVG heartrate
        exercise.heartRateAVG = Math.round (avgHeartrateSum / (float) exercise.sampleList.size ())
        
        // when altitude is recorded => search minimum altitude of exercise (is not in HRM file)
        if (exercise.recordingMode.altitude) {
            exercise.altitude.altitudeMin = Short.MAX_VALUE
            
            for (i in 0..<exercise.sampleList.size ()) {
                exercise.altitude.altitudeMin = Math.min (
                    exercise.altitude.altitudeMin, exercise.sampleList[i].altitude)
            }
        }
        
        // compute min and max cadence when recorded (not in HRM file)
        if (exercise.recordingMode.cadence) {
            exercise.cadence = new ExerciseCadence ()
                         
            // compute average cadence from all samples, where cadence > 0
            def avgCadenceSum = 0
            def avgCadenceSamples = 0
        
            for (i in 0..<exercise.sampleList.size ())
            {
                if (exercise.sampleList[i].cadence > 0) {
                    avgCadenceSum += exercise.sampleList[i].cadence
                    avgCadenceSamples++
                }
                
                exercise.cadence.cadenceMax = Math.max (
                    exercise.sampleList[i].cadence, exercise.cadence.cadenceMax)
            }
            
            if (avgCadenceSum > 0 && avgCadenceSamples > 0) {
                exercise.cadence.cadenceAVG = avgCadenceSum / avgCadenceSamples
            } 
        }
        
        // repair distance values of samples
        exercise.repairSamples ()
        
        // calculate average lap speed, the data was not recorded here
        calculateAverageLapSpeed (exercise)

        // done :-)
        return exercise
    }    

    /**
     * This method returns the list of all content lines of the specified
     * block in the exercise file (e.g. when blockName="Params" it returns
     * all lines after the line "[Params]" and before next block start.
     * An empty list will be returned when the block can't be found or is empty.
     * When the fRequired flag is true and nothing was found then a PVException 
     * will be thrown.
     */
    private def getBlockLines (blockName, fRequired) throws PVException
    {
        def strBlockLine = "[$blockName]"
        def lFoundLines = []
        def fFound = false
     
        // process all lines of exercise file
        for (line in fileContent) {
            
            // is this line the start of the block ?
            if (line.startsWith (strBlockLine)) {
                fFound = true
                continue
            }
            
            // add lines and stop when this is the line the end of the block
            if (fFound) {
                if (line.length () == 0 || line.startsWith ('[')) {
                    break
                }
                lFoundLines << line
            }
        }
        
        if (fRequired && lFoundLines.size () == 0) {
            throw new PVException ("Failed to read HRM file, can't find block '$blockName' ...")
        }
        return lFoundLines
    }

    /**
     * Searches for the specified value in the passed list of block lines 
     * (Strings). Example: if name is "Version" this method return "106" 
     * if the line "Version=106" is in blockLines.
     * A PVException will be thrown when the value can't be found.
     */
    private def getValueFromBlock (def blockLines, def name) throws PVException {
        for (line in blockLines) {
            if (line.startsWith ("$name=")) {
                return line.substring (name.length () + 1)
            }
        }
        throw new PVException ("Failed to read HRM file, can't find value for '$name' ...")
    }        
}
