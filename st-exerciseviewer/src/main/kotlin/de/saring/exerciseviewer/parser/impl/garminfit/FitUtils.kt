package de.saring.exerciseviewer.parser.impl.garminfit

/**
 * Garmin FIT-related helper methods.
 */
object FitUtils {

    /**
     * Converts the name of the Garmin Sport and SubSport enums into a readable variant (words are split with spaces
     * instead of underscores, each word in lowercase with a capital first letter).
     * Example: 'GRAVEL_CYCLING' will be 'Gravel Cycling'.
     * @param enumName name to convert
     * @return readable name
     */
    @JvmStatic
    fun enumToReadableName(enumName: String): String {
        return enumName
            .lowercase()
            .split('_')
            .joinToString(" ") { word -> word.replaceFirstChar { it.uppercase() } };
    }
}