package de.saring.util;

/**
 * This util class contains several String helper methods.
 *
 * @author Stefan Saring
 * @version 1.0
 */
public final class StringUtils {

    private StringUtils() {
    }

    /**
     * Returns the trimmed version of the specified text. If the passed text is null or if the trimmed text is
     * empty, then null will be returned.
     *
     * @param text text to trim
     * @return trimmed text or null
     */
    public static String getTrimmedTextOrNull(String text) {
        if (text == null) {
            return null;
        }

        final String trimmed = text.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    /**
     * Returns the passed text if it is not null. If it is null, then an empty string is returned.
     *
     * @param text the text to check
     * @return the passed text or an empty string
     */
    public static String getTextOrEmptyString(String text) {
        return text == null ? "" : text;
    }

    /**
     * Returns the trimmed first line of the specified text or the complete text when there is no line break.
     *
     * @param text the text to fit
     * @return the first line of text or null when text was null
     */
    public static String getFirstLineOfText(final String text) {
        if (text == null) {
            return null;
        } else {
            int indexNewLine = text.indexOf('\n');
            if (indexNewLine == -1) {
                return text.trim();
            } else {
                return text.substring(0, indexNewLine).trim();
            }
        }
    }
}
