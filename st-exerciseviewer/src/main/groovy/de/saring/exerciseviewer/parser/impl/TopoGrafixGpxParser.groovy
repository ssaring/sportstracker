package de.saring.exerciseviewer.parser.impl

import de.saring.exerciseviewer.core.EVException
import de.saring.exerciseviewer.data.*
import de.saring.exerciseviewer.parser.AbstractExerciseParser;
import de.saring.exerciseviewer.parser.ExerciseParserInfo;

import java.text.SimpleDateFormat

/**
 * ExerciseParser implementation for reading TopoGrafix GPX v1.1 exercise files (XML-based).
 * Documentation about the format can be found at the TopoGrafix website
 * ( http://www.topografix.com/gpx.asp ).
 *
 * @author  Stefan Saring
 * @version 1.0
 */
class TopoGrafixGpxParser extends AbstractExerciseParser {

	/** Informations about this parser. */
	private def info = new ExerciseParserInfo ('TopoGrafix GPX', ["gpx", "GPX"] as String[])
	
	/** The date and time parser instance for XML date standard. */
	private def sdFormat = new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ss");
		
	/** {@inheritDoc} */
	@Override
	ExerciseParserInfo getInfo () {
		info
	}
		
	/** {@inheritDoc} */
	@Override
	EVExercise parseExercise (String filename) throws EVException {
		
		try {
			// get GPathResult object by using the XmlSlurper parser
			def gpx = new XmlSlurper().parse(new File(filename))
			return parseExercisePath(gpx)
		}
		catch (Exception e) {
			throw new EVException ("Failed to read the TopoGrafix GPX exercise file '${filename}' ...", e)
		}
	}
	
	/**
	 * Parses the exercise data from the specified gpx (root) element.
	 */
	private EVExercise parseExercisePath(gpx) {
		
		EVExercise exercise = createExercise(gpx)
		exercise.sampleList = parseSampleTrackpoints(gpx, exercise)

		if (exercise.recordingMode.altitude) {
			computeAltitudeSummary(exercise)
		}
		
		exercise
	}

	/**
	 * Creates the EVExercise with basic exercise data.	
	 */
	private def createExercise(gpx) {		
		
		EVExercise exercise = new EVExercise()		
		exercise.fileType = EVExercise.ExerciseFileType.GPX
		exercise.recordingInterval = EVExercise.DYNAMIC_RECORDING_INTERVAL
		exercise.recordingMode = new RecordingMode ()
		exercise.recordingMode.location = true
	
		exercise.heartRateLimits = new HeartRateLimit[0]
		exercise.lapList = new Lap[0]
		
		// get date and time (optional)
		if (!gpx.metadata.time.isEmpty()) {
			exercise.date = sdFormat.parse(gpx.metadata.time.text())
		}
		
		exercise
	}
	
	/**
	 * Parses all trackpoints in all tracks and track segments under the "gpx" element.
	 * 
	 * @return Array of ExerciseSample objects for each trackpoint
	 */
	private def parseSampleTrackpoints(gpx, exercise) {
		def eSamples = []
		
		gpx.trk.each { trk ->
			trk.trkseg.each { trkseg ->
				trkseg.trkpt.each { trkpt ->
					
					def sample = new ExerciseSample()
					eSamples << sample

					// get position					
					sample.position = new Position(trkpt.@lat.text().toDouble(), trkpt.@lon.text().toDouble())
					
					// get altitude (optional)
					if (!trkpt.ele.isEmpty()) {
						exercise.recordingMode.altitude = true
						sample.altitude = Math.round(trkpt.ele.text().toDouble())
					}
					
					// TODO: get time offset (optional)
				}			
			}			
		}			

		eSamples as ExerciseSample[]
	}	

	/**
	 * Computes the min, avg and max altitude and the ascent of the exercise.
	 */
	def computeAltitudeSummary(exercise) {		
		def altitude = new ExerciseAltitude()
		
		exercise.altitude = altitude		
		altitude.altitudeMin = Short.MAX_VALUE 
		altitude.altitudeMax = Short.MIN_VALUE
		
		long altitudeSum = 0
		short previousAltitude = exercise.sampleList[0].altitude
		
		exercise.sampleList.each { sample ->
			
			altitude.altitudeMin = Math.min(sample.altitude, altitude.altitudeMin) 
			altitude.altitudeMax = Math.max(sample.altitude, altitude.altitudeMax)
			altitudeSum += sample.altitude
			
			if (previousAltitude < sample.altitude) {
				altitude.ascent += sample.altitude - previousAltitude
			}
			previousAltitude = sample.altitude
		}
		
		altitude.altitudeAVG = altitudeSum / exercise.sampleList.size()
	}
}
