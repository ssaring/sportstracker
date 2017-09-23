package de.saring.exerciseviewer.parser

/**
 * This class contains the name and the supported files of an ExerciseParser.
 *
 * @property name The name of the parser.
 * @property suffixes List of exercise file suffixes which can be read by this parser.
 *
 * @author Stefan Saring
 */
class ExerciseParserInfo(

        val name: String,
        val suffixes: List<String>)