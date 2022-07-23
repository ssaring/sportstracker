package de.saring.exerciseviewer.data

/**
 * Contains the FIT-protocol specific sport type and subtype info. The list of all sport types and subtypes defined in
 * the Garmin FIT protocol can be found in the enums [com.garmin.fit.Sport] and [com.garmin.fit.SubSport] in the Garmin
 * FIT SDK library.
 *
 * @property sportTypeId FIT ID of the sport type.
 * @property sportSubTypeId FIT ID of the sport subtype.
 *
 * @author Stefan Saring
 */
data class SportTypeFit(
    var sportTypeId: Short,
    var sportSubTypeId: Short? = null,
)
