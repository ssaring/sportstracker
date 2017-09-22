package de.saring.exerciseviewer.parser

import de.saring.exerciseviewer.core.EVException
import java.util.ServiceLoader

/**
 * This factory will always returns the proper ExerciseParser implementation for the specified filename.
 *
 * New parsers can be added just by implementing the ExerciseParser interfaces and registering them in the
 * META-INF/service directory. New parsers must not be part of the SportsTracker Jar file(s),the parser Jar
 * file just needs to be added to the application classpath.
 *
 * @author Stefan Saring
 */
object ExerciseParserFactory {

    /** The ServiceLoader instance for all ExerciseParser implementations. */
    private val exerciseParserLoader: ServiceLoader<ExerciseParser> = ServiceLoader.load(ExerciseParser::class.java)

    /**
     * Returns the instance of the appropriate exercise parser for the specified exercise filename.
     * The proper parser will be assigned by using the filename suffix.
     *
     * @param filename name of the exercise file to parse
     * @return instance of the appropriate exercise parser
     * @throws EVException when no proper parser has been found
     */
    @Throws(EVException::class)
    fun getParser(filename: String): ExerciseParser =
            exerciseParserLoader.find { isFileSupportedByParser(it, filename) } ?:
                    throw EVException("No parser has been found for filename '$filename'!")

    /**
     * Returns the list of all ExerciseParserInfo objects for all available parser implementations
     * (useful e.g. for File Open dialogs for list of suffixes).
     *
     * @return list of ExerciseParserInfo objects for all parser implementations
     */
    val exerciseParserInfos: List<ExerciseParserInfo> = exerciseParserLoader
            .map { parser -> parser.info }
            .toList()

    private fun isFileSupportedByParser(parser: ExerciseParser, filename: String): Boolean =
            parser.info.suffixes.any { filename.endsWith(".$it") }
}
