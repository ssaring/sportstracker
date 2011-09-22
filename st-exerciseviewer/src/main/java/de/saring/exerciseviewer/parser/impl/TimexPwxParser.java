package de.saring.exerciseviewer.parser.impl;

import java.text.SimpleDateFormat;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.saring.exerciseviewer.core.EVException;
import de.saring.exerciseviewer.data.EVExercise;
import de.saring.exerciseviewer.data.ExerciseAltitude;
import de.saring.exerciseviewer.data.ExerciseSample;
import de.saring.exerciseviewer.data.ExerciseSpeed;
import de.saring.exerciseviewer.data.HeartRateLimit;
import de.saring.exerciseviewer.data.Lap;
import de.saring.exerciseviewer.data.LapAltitude;
import de.saring.exerciseviewer.data.LapSpeed;
import de.saring.exerciseviewer.data.LapTemperature;
import de.saring.exerciseviewer.data.Position;
import de.saring.exerciseviewer.data.RecordingMode;
import de.saring.exerciseviewer.parser.AbstractExerciseParser;
import de.saring.exerciseviewer.parser.ExerciseParserInfo;

/**
 * This implementation of an ExerciseParser is for reading PWX files of the
 * Timex Race Trainer watch.  It will likely work with other PWX files
 * downloaded from the Training Peaks website.  It works well and has been
 * tested with Chrono PWX files but has not been tested with Interval PWX files.
 * <br/>
 * It is assumed that the exercise files have the extension ".pwx".
 * <br/>
 * This file has been completely rewritten from the initial version
 * that was based on PolarHsrRawParser.java by Remco den Breeje
 * which is based on PolarSRawParser.java by Stefan Saring
 * <br/>
 * TODO: This parser contains a lot of unused code (commented out),
 * remove it when not needed anymore.
 * 
 * 9/10/2010 Version 1.2
 *              Added support for Global Trainer Pwx Files
 *              Changed Lap Distance to Distance since beginning of exercise
 *
 * @author  Robert C. Schultz
 * @version 1.2
 */
public class TimexPwxParser extends AbstractExerciseParser {

    /** Informations about this parser. */
    private final ExerciseParserInfo info = new ExerciseParserInfo("Timex PWX", new String[]{"pwx", "PWX"});
    /** section data */
    int[][] s;

    private class MinMaxAvg {
        private float min=0;
        private float max=0;
        private float avg=0;
        
        public void setMin(float in){ min = in; }
        public float getMin(){ return min; }
        public void setMax(float in){ max = in; }
        public float getMax(){ return max; }
        public void setAvg(float in){ avg = in; }
        public float getAvg(){ return avg; }
    }

    private MinMaxAvg Node2MinMaxAvg(Node inNode) {
        MinMaxAvg result = new MinMaxAvg();
        NamedNodeMap attributes = inNode.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            if (attributes.item(i).getNodeName().equals("max")) {
                result.setMax(Float.valueOf(attributes.item(i).getTextContent()));
            } else if (attributes.item(i).getNodeName().equals("min")) {
                result.setMin(Float.valueOf(attributes.item(i).getTextContent()));
            } else if (attributes.item(i).getNodeName().equals("avg")) {
                result.setAvg(Float.valueOf(attributes.item(i).getTextContent()));
            }
        }
        return result;
    }

    private class SummaryData {
        private double beginning = 0;
        private double duration = 0;
        private int work = 0;
        private MinMaxAvg hr;
//        private double durationStopped = 0;
//        private float tss = 0;
//        private int normalizedPower = 0;
        private MinMaxAvg speed;
//        private MinMaxAvg power;
//        private MinMaxAvg torque;
//        private MinMaxAvg cadence;
        private float distance = 0;
        private MinMaxAvg altitude;
//        private MinMaxAvg temperature;
//        private int variabilityIndex = 0;
//        private float climbingElevation = 0;
        
        public void setBeginning(double in){ beginning = in; }
        public double getBeginning(){ return beginning; }
        public void setDuration(double in){ duration = in; }
        public double getDuration(){ return duration; }
        public void setWork(int in){ work = in; }
        public int getWork(){ return work; }
        public void setHr(MinMaxAvg in){ hr = in; }
        public MinMaxAvg getHr(){ return hr; }
//        public void setDurationStopped(double in){ durationStopped = in; }
//        public double getDurationStopped(){ return durationStopped; }
//        public void setTss(float in){ tss = in; }
//        public float getTss(){ return tss; }
//        public void setNormalizedPower(int in){ normalizedPower = in; }
//        public int getNormalizedPower(){ return normalizedPower; }
        public void setSpeed(MinMaxAvg in){ speed = in; }
        public MinMaxAvg getSpeed(){ return speed; }
//        public void setPower(MinMaxAvg in){ power = in; }
//        public MinMaxAvg getPower(){ return power ; }
//        public void setTorque(MinMaxAvg in){ torque = in; }
//        public MinMaxAvg getTorque(){ return torque; }
//        public void setCadence(MinMaxAvg in){ cadence = in; }
//        public MinMaxAvg getCadence(){ return cadence; }
        public void setDistance(float in){ distance = in; }
        public float getDistance(){ return distance; }
        public void setAltitude(MinMaxAvg in){ altitude = in; }
        public MinMaxAvg getAltitude(){ return altitude; }
//        public void setTemperature(MinMaxAvg in){ temperature  = in; }
//        public MinMaxAvg getTemperature(){ return temperature; }
//        public void setVariabilityIndex(int in){ variabilityIndex = in; }
//        public int getVariabilityIndex(){ return variabilityIndex; }
//        public void setClimbingElevation(float in){ climbingElevation = in; }
//        public float getClimbingElevation(){ return climbingElevation; }
    }
    
    @Override
    public ExerciseParserInfo getInfo() {
        return info;
    }

    public int countNodeItems(Node node, String string2count) {
        // Given a Node and a Child Node Name, count the number of children with that node name
        NodeList children = node.getChildNodes();

        int numChildren = children.getLength();
        String currentNodeName = null;
        int numMatches = 0;


        for (int i = 0; i < numChildren; i++) {
            currentNodeName=children.item(i).getNodeName();
            if (currentNodeName.equals(string2count)) {
                numMatches++;
            }
        }
        return numMatches;
    }

    private EVExercise parseWorkoutNode(EVExercise exercise, Node workoutNode){
        NodeList children = workoutNode.getChildNodes();
        String childName;
        for (int i = 0; i < children.getLength(); i++) {
            childName = children.item(i).getNodeName();
            if (childName.equals("athlete")) {
                // Nothing to do with this yet...or is there?
            } else if (childName.equals("goal")) {
                // Not in files downloaded directly from the Timex 843/844
                // Probably is in the files downloaded from the online software
            } else if (childName.equals("sportType")) {
                // obtain sportType
                exercise.setType((byte)0);
                exercise.setTypeLabel(children.item(i).getTextContent());
            } else if (childName.equals("cmt")) {
                // Not implemented
            } else if (childName.equals("code")) {
                // Not implemented
            } else if (childName.equals("device")) {
                // parse device
                exercise = parseWorkoutDeviceNode(exercise, children.item(i));
                // The passing an object and then assigning the result of the method to the same object is akward to me.
                // It seems like that could result in a lot of time moving data.  My understanding is that is not the case
                // in Java though.
            } else if (childName.equals("time")) {
                // obtain start time
                try {
                    exercise.setDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(children.item(i).getTextContent()));
                } catch (Exception e) {
                    exercise.setDate(null);
                }
            } else if (childName.equals("summarydata")) {
                // parse workout summary data
                SummaryData workoutSummary = parseSummaryData(children.item(i));
                exercise.setDuration((int) workoutSummary.getDuration() * 10);
                exercise.setSumExerciseTime((int) workoutSummary.getDuration() / 60); // Not sure why these are different.
                exercise.setSumRideTime((int) workoutSummary.getDuration() / 60);  // Assume some watches keep track of bike specific time..This one doesn't
                exercise.setEnergy((int) (workoutSummary.getWork() * (0.238845896627495939619))); // Convert to Calories first
                //exercise.setEnergyTotal((int) (workoutSummary.getWork() * (0.238845896627495939619))); // Using the value in device/extensions
                exercise.setHeartRateMax((short) workoutSummary.getHr().getMax());
                // exercise.setHeartRateMin((short) workoutSummary.getHr().getMin()); // Not implemented in EVExercise
                exercise.setHeartRateAVG((short) workoutSummary.getHr().getAvg());
                exercise.setOdometer((int)workoutSummary.getDistance()/1000);
                if (workoutSummary.getSpeed() != null) {
                    ExerciseSpeed workoutSpeed = new ExerciseSpeed();
                    workoutSpeed.setDistance((int) workoutSummary.getDistance());
                    workoutSpeed.setSpeedAVG(workoutSummary.getSpeed().getAvg() * (float) 3.6);
                    workoutSpeed.setSpeedMax(workoutSummary.getSpeed().getMax() * (float) 3.6);
                    exercise.setSpeed(workoutSpeed);
                }
                if (workoutSummary.getAltitude() != null) {
                    ExerciseAltitude workoutAltitude = new ExerciseAltitude();
                    workoutAltitude.setAltitudeAVG((short)workoutSummary.getAltitude().getAvg() );
                    workoutAltitude.setAltitudeMax((short)workoutSummary.getAltitude().getMax() );
                    workoutAltitude.setAltitudeMin((short)workoutSummary.getAltitude().getMin() );
                    exercise.setAltitude(workoutAltitude);
                }

            } else if (childName.equals("segment")) {
                // This is handled after parsing everything else
            } else if (childName.equals("sample")) {
                // This is handled after parsing everything else
            } else if (childName.equals("extension")) {
                // Used for Timex Global Trainer and possibly others.
                exercise = parseWorkoutExtensionNode(exercise, children.item(i));
            }
        }
        // parse lap segments
        exercise = parseWorkoutSegments(exercise, workoutNode);
        // parse samples
        exercise = parseWorkoutSamples(exercise, workoutNode);
        return exercise;
    }

    private EVExercise parseWorkoutExtensionNode(EVExercise exercise, Node workoutExtensionNode){
        // Used for Global Trainer
        NodeList children = workoutExtensionNode.getChildNodes();
        String childName;
        for (int i = 0; i < children.getLength(); i++) {
            childName = children.item(i).getNodeName();//
            if (childName.equals("laps")) {
                exercise.setLapList(new Lap[Integer.valueOf(children.item(i).getTextContent())]);
            } else if (childName.equals("ascent")) {
                exercise.getAltitude().setAscent(Integer.valueOf(children.item(i).getTextContent()));
            } else if (childName.equals("descent")) {
                // obtain descent - not used in EVExercise
            } else if (childName.equals("points")) {
                // points - not used in EVExercise
            }
        }
        return exercise;
    }
    private EVExercise parseWorkoutDeviceNode(EVExercise exercise, Node deviceNode) {
        NodeList children = deviceNode.getChildNodes();
        String childName;
        for (int i = 0; i < children.getLength(); i++) {
            childName = children.item(i).getNodeName();
            if (childName.equals("extension")) {
                // parse extension
                exercise = parseDeviceExtensionNode(exercise, children.item(i));
            } else if (childName.equals("make")) {
                // obtain make        
            } else if (childName.equals("model")) {
                // obtain model        
                if (children.item(i).getTextContent().equals("Global Trainer")){
                    exercise = setGlobalTrainerRecordingMode(exercise);
                    exercise = setGlobalTrainerZones(exercise);
                }

            } else if (childName.equals("stopdetectionsetting")) {
                // obtain stopdetectionsetting        
            } else if (childName.equals("elevationchangesetting")) {
                // obtain elevationchangesetting        
            }
        }
        return exercise;
    }

    private EVExercise setGlobalTrainerRecordingMode(EVExercise exercise){
        RecordingMode recMode = new RecordingMode();

        recMode.setPower(true);
        recMode.setLocation(true);
        recMode.setCadence(false);
        recMode.setAltitude(true);
        recMode.setSpeed(true);
        recMode.setBikeNumber((byte) 0);
        recMode.setIntervalExercise(false); //

        exercise.setRecordingMode(recMode);
        return exercise;
    }
    private EVExercise setGlobalTrainerZones(EVExercise exercise){
        HeartRateLimit Zones[] = new HeartRateLimit[6];
        for (int i = 0; i < 6; i++) {
            Zones[i] = new HeartRateLimit();
            Zones[i].setUpperHeartRate((short) (50+(i+1) * 25) );
            Zones[i].setLowerHeartRate((short) (50+i*25) );
            Zones[i].setAbsoluteRange(true);
            Zones[i].setTimeAbove(0);
            Zones[i].setTimeBelow(0);
            Zones[i].setTimeWithin(0);
        }

        exercise.setHeartRateLimits(new HeartRateLimit[6]);
        System.arraycopy(Zones, 0, exercise.getHeartRateLimits(), 0, 6);
        return exercise;
    }
    private EVExercise parseDeviceExtensionNode(EVExercise exercise, Node deviceExtensionNode) {
        NodeList children = deviceExtensionNode.getChildNodes();
        String childName;
        for (int i = 0; i < children.getLength(); i++) {
            childName = children.item(i).getNodeName();//
            if (childName.equals("settings")) {
                exercise = parseDeviceExtensionSettingsNode(exercise, children.item(i));
            } else if (childName.equals("stoppage")) {
                // obtain stoppage - not used in EVExercise
            }
        }
        return exercise;
    }

    private EVExercise parseDeviceExtensionSettingsNode(EVExercise exercise, Node deviceExtensionSettingsNode) {
        // None of this data is explicitly specified in the pwx.xsd.
        // It is in the pwx files from the Timex watch though.
        //------------------------------------------------------------
        NodeList children = deviceExtensionSettingsNode.getChildNodes();
        String childName;
        // Create and Initialize Heart Rate Limits
        HeartRateLimit Zones[] = new HeartRateLimit[6];
        for (int i = 0; i < 6; i++) {
            Zones[i] = new HeartRateLimit();
            Zones[i].setUpperHeartRate((short) 0);
            Zones[i].setLowerHeartRate((short) 0);
            Zones[i].setAbsoluteRange(true);
            Zones[i].setTimeAbove(0);
            Zones[i].setTimeBelow(0);
            Zones[i].setTimeWithin(0);
        }

        for (int i = 0; i < children.getLength(); i++) {
            childName = children.item(i).getNodeName();// 
            if (childName.equals("CHRMaxHR") || childName.equals("HRMMaxHR")) {
                // Obtain Max HR - This is basis for Timex Zones
                short HRMMaxHR = Short.valueOf(children.item(i).getTextContent());
                double HRZonesPercentages[] = {1, .9, .8, .7, .6, .5};
                for (int k = 0; k < 5; k++) {
                    Zones[k] = new HeartRateLimit();
                    Zones[k].setUpperHeartRate((short) (HRZonesPercentages[k] * HRMMaxHR));
                    Zones[k].setLowerHeartRate((short) (1 + HRZonesPercentages[k + 1] * HRMMaxHR));
                    Zones[k].setAbsoluteRange(true);
                    Zones[k].setTimeAbove(0);
                    Zones[k].setTimeBelow(0);
                    Zones[k].setTimeWithin(0);
                }
            } else if (childName.equals("CHRManualZoneHigherLimit") || childName.equals("HRMBpmManHi")) {
                // obtain Manual Zone Higher Limit
                Zones[5].setUpperHeartRate(Short.valueOf(children.item(i).getTextContent()));
            } else if (childName.equals("CHRManualZoneLowerLimit") || childName.equals("HRMBpmManLo")) {
                // obtain Manual Zone Lower Limit          
                Zones[5].setLowerHeartRate(Short.valueOf(children.item(i).getTextContent()));
            } else if (childName.equals("HasHRMData")) {
                // does file have hrm data   
            } else if (childName.equals("TotalNumberOfLaps")) {
                // obtain total number of laps and generate LapList in the exercise
                exercise.setLapList(new Lap[Integer.valueOf(children.item(i).getTextContent())]);
            } else if (childName.equals("KCalPerDevice")) {
                // obtain kCalPerDevice        
                exercise.setEnergyTotal(Integer.valueOf(children.item(i).getTextContent()));
            } else if (childName.equals("WorkoutType")) {
                // obtain workout type - If not Chrono, then stop parsing since intervals aren't yet implemented
                if (!children.item(i).getTextContent().equals("Chrono")) {
                    // Not sure how to handle this... I want it to stop parsing and report an unsupported file.
                    
                }
            } else {
                // obtain Alarm flags 1-3 (Enabled/Disabled)
                // obtain Alarm Hours 1-3
                // obtain Alarm Minutes 1-3
                // obtain Alarm Type 1-3
                // obtain Application that generated the file
                // obtain AVG Lap time
                // obtain Best Lap time
                // obtain best lap number
                // obtain average HRs for each lap (CHRDatabaseTable##)
                // Don't Care about HRM display format
                // obtain Time In Target Zone
                // obtain recovery end bpm
                // obtain recovery start bpm
                // obtain CHRSplitDuration
                // obtain CHRStatus
                // obtain Target HR Zone
                // obtain ManZone Percentage Hi (This is different then the Manual HR Zone in bpm)
                // obtain ManZone Percentage Low (This is different then the Manual HR Zone in bpm)
                // obtain weight
                // obtain weight units
                // does file have Recovery BPM
                // obtain interval data --- Not implementing this yet ---
                // obtain watch ID - This is a String
                // obtain watch manufacturer
                // obtain watch model
                // obtain version number
                // obtain workout number - Not sure what this number is
            }
        }
        // don't care about Button Beep, Hourly Chime, Night Mode, Night Mode Duration, Display Format
        // don't care about HRMAlertApp
        // don't care about Display Units (need to check to see if changing to Percentage changes the way data is stored.)
        // don't care about out of zone alert
        // don't care about RCVYPresetIndex
        // don't care about the Timer data
        // don't care about the Time of Day format /Time Zone (Might care about the time zone if it was actual time zone but its not)
        // don't care if - is file Locked
        // do laps overflow - might care about this but not sure when
        // don't care about some ucaddr# values
        RecordingMode recMode = new RecordingMode();

        recMode.setPower(false);
        recMode.setCadence(false);
        recMode.setAltitude(false);
        recMode.setSpeed(false);
        recMode.setBikeNumber((byte) 0);
        recMode.setIntervalExercise(false); //

        exercise.setRecordingMode(recMode);
        exercise.setHeartRateLimits(new HeartRateLimit[6]);
        System.arraycopy(Zones, 0, exercise.getHeartRateLimits(), 0, 6);

        return exercise;
    }

    private SummaryData parseSummaryData(Node summaryDataNode) {
        SummaryData nodeSummaryData = new SummaryData();
        NodeList children = summaryDataNode.getChildNodes();

        String childName;
        for (int i = 0; i < children.getLength(); i++) {
            childName = children.item(i).getNodeName();
            if (childName.equals("beginning")) {
                // obtain beginning time
                nodeSummaryData.setBeginning(Double.valueOf(children.item(i).getTextContent()));
            } else if (childName.equals("duration")) {
                // obtain duration
                nodeSummaryData.setDuration(Double.valueOf(children.item(i).getTextContent()));
            } else if (childName.equals("hr")) {
                // obtain hr (MinMaxAvg)  (bpm)
                nodeSummaryData.setHr(Node2MinMaxAvg(children.item(i)));
            } else if (childName.equals("work")) {
                // obtain work (Apparently Not used in Laps) (kJ)
                nodeSummaryData.setWork(Integer.valueOf(children.item(i).getTextContent()));
            } else if (childName.equals("spd")) {
                // obtain spd (MinMaxAvg) (meters/second)
                nodeSummaryData.setSpeed(Node2MinMaxAvg(children.item(i)));
            } else if (childName.equals("alt")) {
                // obtain altitude (MinMaxAvg) (meters)
                nodeSummaryData.setAltitude(Node2MinMaxAvg(children.item(i)));
            } else if (childName.equals("dist")) {
                // obtain distance (meters)
                nodeSummaryData.setDistance(Float.valueOf(children.item(i).getTextContent()));
            }
            // 1st time its for the entire workout
            // remaining times is for the Laps
            // obtain duration stopped
            // obtain tss
            // obtain normalizedPower (watts)
            // obtain pwr (MinMaxAvg) (watts)
            // obtain torq (MinMaxAvg) (nM)
            // obtain cadence (MinMaxAvg) (rpm)
            // obtain temp (MinMaxAvg) (C)
            // obtain variabilityIndex - Not sure what this is
            // obtain climbingelevation
        }
        return nodeSummaryData; // Probably don't want to pass and return the Exercise itself.
    }

    private EVExercise parseWorkoutSegments(EVExercise exercise, Node workoutNode){
        // obtain segment name  ( Either laps or Workout Summary )
        // parse segment summary data
        int totalLaps = exercise.getLapList().length ;
        int currentLap = 0;
        // Create and initialize a holding Lap

        // Finished Holding Lap
        NodeList children = workoutNode.getChildNodes();
        NodeList segmentChildren = null;
        String childName;
        float runningDistance=0;
        for (int i = 0; i < children.getLength(); i++) {
            childName = children.item(i).getNodeName();
            if (childName.equals("segment")) {
                segmentChildren = children.item(i).getChildNodes();
                Lap lap = new Lap();
                LapAltitude lapAlt = new LapAltitude();
                LapSpeed lapSpd = new LapSpeed();
                LapTemperature lapTmp = new LapTemperature();
                lapAlt.setAscent(0);
                lapAlt.setAltitude((short) 0);
                lap.setAltitude(lapAlt);
                lapSpd.setCadence((short) 0);
                lapSpd.setDistance(402); // I typically mark each lap at the 1/4 mile.  A popup might be nice to fill in the rest.
                lapSpd.setSpeedAVG((float) 0.0);
                lapSpd.setSpeedEnd((float) 0.0);
                lap.setSpeed(lapSpd);
                lapTmp.setTemperature((short) 25);
                lap.setTemperature(lapTmp);
                lap.setHeartRateSplit((short) 0);
                lap.setHeartRateMax((short) 0);
                for (int j = 0; j < segmentChildren.getLength(); j++) {
                    childName = segmentChildren.item(j).getNodeName();
                    if (childName.equals("summarydata")) {
                         SummaryData segmentSummary = parseSummaryData(segmentChildren.item(j));
                         lap.setTimeSplit((int) ((segmentSummary.getDuration() + segmentSummary.getBeginning()) * 10));
                         if ( segmentSummary.getDistance()!= 0 ){
                            runningDistance+=segmentSummary.getDistance();
                            lapSpd.setDistance((int)runningDistance);
                            lapSpd.setSpeedAVG((float) ( 3.600*segmentSummary.getDistance() / segmentSummary.getDuration())); // Assumes 1/4 Mile Lap
                            lapSpd.setSpeedEnd((float) 0.0);
                         } else                         {
                            runningDistance+=402.336;
                            lapSpd.setDistance((int)runningDistance);
                         lapSpd.setSpeedAVG((float) (3.6 * 402.336 / segmentSummary.getDuration())); // Assumes 1/4 Mile Lap
                         lapSpd.setSpeedEnd((float) 0.0);
                         }
                         lap.setSpeed(lapSpd);
                         lap.setHeartRateAVG((short)segmentSummary.getHr().getAvg());
                         lap.setHeartRateMax((short)segmentSummary.getHr().getMax());
                         if ( segmentSummary.getAltitude() != null) {
                            lapAlt.setAltitude((short) segmentSummary.getAltitude().getMax());
                            lapAlt.setAscent((int) (segmentSummary.getAltitude().getMax() - segmentSummary.getAltitude().getMin()));
                            lap.setAltitude(lapAlt);
                    }
                }
                }
                if (currentLap < totalLaps) {
                    exercise.getLapList()[currentLap++] = lap;
                }
            }
        }
        return exercise;
    }
    public static float getDistanceFromPositions(Position startPosition, Position stopPosition){ //float lat1, float lng1, float lat2, float lng2) {
        double earthRadius = 6369.6; //3958.75;
        double dLat = Math.toRadians(stopPosition.getLatitude()-startPosition.getLatitude());
        double dLng = Math.toRadians(stopPosition.getLongitude()-startPosition.getLongitude());
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(startPosition.getLatitude())) * Math.cos(Math.toRadians(stopPosition.getLatitude()))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;

        int meterConversion = 1000; // 1609;

        if (dist<0){
            dist=0-dist;
        }
        return (float)(dist * meterConversion);
    }

    private EVExercise parseWorkoutSamples(EVExercise exercise, Node workoutNode ){
        // obtain all the sample data.
        int totalSamples = countNodeItems(workoutNode,"sample");
        int currentSampleNumber = 0;
        float lastDistance = 0;
        boolean distanceinsample = false;
        boolean firstsample=true;
        exercise.setSampleList(new ExerciseSample[totalSamples]);
        double lastOffset = 0;
        double currentOffset = 0;
        Position lastPosition = new Position(0,0);
        NodeList children = workoutNode.getChildNodes();
        NodeList sampleChildren = null;
        String childName;
        ExerciseSample lastSample = new ExerciseSample(); // Stop the jitters... assumes no
        Double latitude=0.0, longitude=0.0;
        double belowZone[] = {0, 0, 0, 0, 0, 0};
        double inZone[] = {0, 0, 0, 0, 0, 0};
        double aboveZone[] = {0, 0, 0, 0, 0, 0};
        int istop = children.getLength(); // getLength() is a slow function so keep it out of the loop.
        for (int i = 0; i < istop; i++) {
            childName = children.item(i).getNodeName();
            if (childName.equals("sample")){
                ExerciseSample sample = new ExerciseSample();
                sampleChildren = children.item(i).getChildNodes();
                int jstop = sampleChildren.getLength();
                for( int j = 0; j< jstop;j++){
                    childName = sampleChildren.item(j).getNodeName();
                    if (childName.equals("timeoffset")){
                        if (currentOffset != 0 )
                            lastOffset = currentOffset;
                        currentOffset=Double.valueOf(sampleChildren.item(j).getTextContent()).doubleValue();
                        sample.setTimestamp((long) (1000*currentOffset));
                    }else if (childName.equals("hr")){
                        sample.setHeartRate((short) Short.valueOf(sampleChildren.item(j).getTextContent()));
                    }else if (childName.equals("spd")){
                        sample.setSpeed((float)3.6*Float.valueOf(sampleChildren.item(j).getTextContent()).floatValue());
                    }else if (childName.equals("pwr")){
                        // Not implemented in ExerciseSample class
                    }else if (childName.equals("torq")){
                        // Not implemented in ExerciseSample class
                    }else if (childName.equals("cad")){
                        sample.setCadence((short) Short.valueOf(sampleChildren.item(j).getTextContent()));
                        exercise.getRecordingMode().setCadence(true);
                    }else if (childName.equals("dist")){
                        sample.setDistance(Integer.valueOf(sampleChildren.item(j).getTextContent()));
                        distanceinsample=true;
                    }else if (childName.equals("lat")){
                        latitude = Double.valueOf(sampleChildren.item(j).getTextContent()).doubleValue();
                    }else if (childName.equals("lon")){
                        longitude = Double.valueOf(sampleChildren.item(j).getTextContent()).doubleValue();
                    }else if (childName.equals("alt")){
                        sample.setAltitude(Float.valueOf(sampleChildren.item(j).getTextContent()).shortValue());
                    }else if (childName.equals("temp")){
                        sample.setTemperature(Float.valueOf(sampleChildren.item(j).getTextContent()).shortValue());
                    }else if (childName.equals("time")){
                        // Not implemented in ExerciseSample
                    }
                }
                sample.setPosition(new Position(latitude, longitude));
                if (firstsample){
                    lastPosition=sample.getPosition();
                    firstsample=false;
                }
                if(!distanceinsample){
                    lastDistance+=getDistanceFromPositions(lastPosition,sample.getPosition());
                    sample.setDistance((int)lastDistance);
                    lastPosition=sample.getPosition();
                }
                // Eliminates the jitters of 0bpm samples... assumes that heart rate won't change instantiously by much and
                // that there will only be the occasional missed heart beat.  Also fixes the laps not adding up.
                if (sample.getHeartRate()==0)
                    sample.setHeartRate(lastSample.getHeartRate());
                else
                    lastSample.setHeartRate(sample.getHeartRate());
                exercise.getSampleList()[currentSampleNumber++] = sample;

                // update Zone information
                for (int j = 0; j < 6; j++) {
                    if ( sample.getHeartRate() > exercise.getHeartRateLimits()[j].getUpperHeartRate()) {
                        aboveZone[j] +=(currentOffset-lastOffset);
                    } else if (sample.getHeartRate() < exercise.getHeartRateLimits()[j].getLowerHeartRate()) {
                        belowZone[j] += (currentOffset-lastOffset);
                    } else {
                        inZone[j] += (currentOffset-lastOffset);
                    }
                }
            }
            
        }
        // Store Zone Information in the exercise file
        for (int i = 0; i < 6; i++) {
            exercise.getHeartRateLimits()[i].setTimeAbove((short)aboveZone[i]);
            exercise.getHeartRateLimits()[i].setTimeBelow((short)belowZone[i]);
            exercise.getHeartRateLimits()[i].setTimeWithin((short)inZone[i]);
        }
        exercise.setRecordingInterval((short)2);
        return exercise;        
    }

    private Node findFirstPwx(Document doc) {
        // Find the first node of the document that is a pwx and then return it otherwise, return null
        // Normally only expect one node at this level but who knows.
        NodeList rootNodeList = doc.getChildNodes();
        for (int i = 0; i < rootNodeList.getLength(); i++) {
            if (rootNodeList.item(i).getNodeName().equals("pwx")) {
                return rootNodeList.item(i);
            }
        }
        return null;
    }

    @Override
    public EVExercise parseExercise(String filename) throws EVException {

        // create an EVExercise object from this data and set file type
   
        EVExercise exercise = new EVExercise();
        exercise.setFileType(EVExercise.ExerciseFileType.TIMEX_PWX);
        // Open Document and Get root

        DocumentBuilderFactory dbf = null;
        DocumentBuilder db = null;
        Document doc = null;

        Node root = null;

        NodeList children = null;
        // Open the pwx file
        try {
            dbf = DocumentBuilderFactory.newInstance(); // DocumentBuilderFactory
            db = dbf.newDocumentBuilder(); // DocumentBuilder
            doc = db.parse(filename); // Document
            root = findFirstPwx(doc); // Node
        } catch (Exception e) {
            throw new EVException ("Failed to open pwx exercise file '" + filename + "' ...", e);
        }
        if ( root != null )
              exercise.setFileType(EVExercise.ExerciseFileType.TIMEX_PWX);
        else
            throw new EVException ("Failed to find a pwx node in file '" + filename + "'");

        children = root.getChildNodes();
        for(int i=0;i<children.getLength(); i++)
        {
            if( children.item(i).getNodeName().equals("workout")){
                exercise = parseWorkoutNode(exercise, children.item(i));
            }
        }

        // done :-) ?

        return exercise;
    }
}

