package de.saring.util

/**
 * Data class for storing a comparable semantic version. A version always consists of a major, a minor and a patch
 * version number (numbers only, no characters). See https://semver.org for further details.
 * Examples: 1.0.0, 0.5.1 or 4.3.1
 *
 * @property major major version number
 * @property minor minor version number
 * @property patch patch version number
 *
 * @author Stefan Saring
 */
data class SemanticVersion(
        val major: Int,
        val minor: Int,
        val patch: Int) : Comparable<SemanticVersion> {

    override fun compareTo(other: SemanticVersion): Int {

        val majorCompare = this.major.compareTo(other.major)
        if (majorCompare != 0) {
            return majorCompare
        }

        val minorCompare = this.minor.compareTo(other.minor)
        if (minorCompare != 0) {
            return minorCompare
        }

        return this.patch.compareTo(other.patch)
    }

    companion object {

        /**
         * Creates a SemanticVersion from the specified string version. An [IllegalArgumentException] will be thrown
         * when the version is not valid.
         *
         * @param version version number as a string
         * @return SemanticVersion
         */
        fun parse(version: String): SemanticVersion {

            var versionParts = version.split('.')
            if (versionParts.size != 3) {
                throw IllegalArgumentException("Invalid version number '$version', needs to consist of 3 parts!")
            }

            return try {
                SemanticVersion(
                        versionParts[0].toInt(),
                        versionParts[1].toInt(),
                        versionParts[2].toInt())
            }
            catch (nfe: NumberFormatException) {
                throw IllegalArgumentException("Invalid version number '$version', the parts are not valid numbers!")
            }
        }
    }
}
