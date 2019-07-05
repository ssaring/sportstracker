package de.saring.util.gui.jfreechart

import org.jfree.chart.axis.NumberAxis
import org.jfree.data.Range

/**
 * Extended JFreeChart NumberAxis which displays a fixed range. This can e.g. be used for a altitude chart to avoid
 * that the axis starts with 0, it can display only the range where altitude data is present. It also makes sure that
 * this fixed range is displayed again, when the user zooms into the chart and out again.
 * Source: https://stackoverflow.com/questions/8551604/restoring-manual-domain-axis-range-after-zooming-out-in-jfreechart
 *
 * @property label of the axis (optional)
 * @property fixedRange range to be displayed
 * @property rangeWithMargins flag whether the range should be extended with a margin
 *
 * @author Stefan Saring
 */
class FixedRangeNumberAxis(
        label: String?,
        private val fixedRange: Range,
        private val rangeWithMargins: Boolean) : NumberAxis(label) {

    override fun autoAdjustRange() {
        if (rangeWithMargins) {
            setRangeWithMargins(fixedRange, false, false)
        } else {
            setRange(fixedRange, false, false)
        }
    }
}
