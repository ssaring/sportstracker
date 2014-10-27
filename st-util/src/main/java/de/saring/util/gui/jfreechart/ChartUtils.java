package de.saring.util.gui.jfreechart;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;

import javax.swing.*;
import java.awt.*;

/**
 * Utility class for JFreeChart usage.
 *
 * @author Stefan Saring
 */
public final class ChartUtils {

    private ChartUtils() {
    }

    /**
     * Customizes the chart for SportsTracker requirements. The chart background
     * will be the same as the parent component background. The plot background
     * is white and the gridlines are grey. The diagram will use the same font
     * as the parent component (otherwise it has BIG fonts on Win32 systems).
     *
     * @param chart the chart component
     * @param parent the parent component of the chart
     * @param useParentBackground flag whether use the background color of the parent or not
     */
    public static void customizeChart(JFreeChart chart, JComponent parent, boolean useParentBackground) {
        if (useParentBackground) {
            chart.setBackgroundPaint(parent.getBackground());
        }

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.GRAY);
        plot.setRangeGridlinePaint(Color.GRAY);

        Font diaFont = parent.getFont();
        plot.getDomainAxis().setTickLabelFont(diaFont);
        plot.getRangeAxis().setTickLabelFont(diaFont);
        plot.getDomainAxis().setLabelFont(diaFont);
        plot.getRangeAxis().setLabelFont(diaFont);

        if (chart.getLegend() != null) {
            chart.getLegend().setItemFont(diaFont);
        }
    }
}
