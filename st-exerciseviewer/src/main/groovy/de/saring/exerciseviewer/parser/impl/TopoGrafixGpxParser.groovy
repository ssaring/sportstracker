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
		
		EVExercise exercise = new EVExercise()
		exercise.heartRateLimits = new HeartRateLimit[0]
		exercise.lapList = new Lap[0]
		def eSamples = []
		
		// TODO: split into multiple methods!
		
		// set basic exercise data
		exercise.fileType = EVExercise.ExerciseFileType.GPX
		exercise.recordingInterval = EVExercise.DYNAMIC_RECORDING_INTERVAL
		exercise.recordingMode = new RecordingMode ()
		exercise.recordingMode.location = true
	
		// get date and time (optional)
		if (!gpx.metadata.time.isEmpty()) {
			exercise.date = sdFormat.parse(gpx.metadata.time.text())
		}

		// parse all trackpoints in all tracks and track segments
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

		exercise.sampleList = eSamples as ExerciseSample[]
		// TODO: compute min, avg, max altitude and ascent (if available)
		exercise
	}	
}
