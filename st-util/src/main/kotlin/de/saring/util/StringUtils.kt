package de.saring.util

/**
 * This util class contains several String helper methods.
 *
 * @author Stefan Saring
 */
object StringUtils {

    /**
     * Returns the trimmed version of the specified text. If the passed text is null or if the trimmed text is empty,
     * then null will be returned.
     *
     * @param text text to trim
     * @return trimmed text or null
     */
    @JvmStatic
    fun getTrimmedTextOrNull(text: String?): String? {

        return text?.trim()?.let { trimmed ->
            if (trimmed.isBlank()) null else trimmed
        }
    }

    /**
     * Returns the passed text if it is not null. If it is null, then an empty string is returned.
     *
     * @param text the text to check
     * @return the passed text or an empty string
     */
    @JvmStatic
    fun getTextOrEmptyString(text: String?): String = text ?: ""

    /**
     * Returns the trimmed first line of the specified text or the complete text when there is no line break.
     *
     * @param text the text to fit
     * @return the first line of text or null when text was null
     */
    @JvmStatic
    fun getFirstLineOfText(text: String?): String? {

        return text?.let {
            val indexNewLine = it.indexOf('\n')
            if (indexNewLine < 0) text.trim() else text.substring(0, indexNewLine).trim()
        }
    }

    /**
     * Returns true when the specified text is null or when the trimmed text is empty.
     *
     * @param text text to check
     * @return when null or empty
     */
    @JvmStatic
    fun isNullOrEmpty(text: String?): Boolean = text == null || text.isBlank()
}
