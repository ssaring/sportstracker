package de.saring.util.gui.jfreechart;

import java.awt.Color;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;

/**
 * Utility class for JFreeChart usage.
 *
 * @author Stefan Saring
 */
public final class ChartUtils {

    private ChartUtils() {
    }

    /**
     * Customizes the chart for SportsTracker requirements. The chart background is
     * transparent, so it uses the parent component background. The plot background
     * is white and the gridlines are grey. The diagram will use the default font for
     * JavaFX labels, the default chart fonts are much bigger.
     *
     * @param chart the chart component
     */
    public static void customizeChart(JFreeChart chart) {

        // set transparent background paint, so the background color of the parent is used
        chart.setBackgroundPaint(new Color(1.0f, 1.0f, 1.0f, 0f));

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.GRAY);
        plot.setRangeGridlinePaint(Color.GRAY);

        // get default JavaFX label font and convert it to a AWT font
        javafx.scene.text.Font fxLabelFont = new javafx.scene.control.Label().getFont();
        java.awt.Font chartFont = new java.awt.Font(fxLabelFont.getName(), java.awt.Font.PLAIN,
                (int) fxLabelFont.getSize());
        plot.getDomainAxis().setTickLabelFont(chartFont);
        plot.getRangeAxis().setTickLabelFont(chartFont);
        plot.getDomainAxis().setLabelFont(chartFont);
        plot.getRangeAxis().setLabelFont(chartFont);

        if (chart.getLegend() != null) {
            chart.getLegend().setItemFont(chartFont);
        }
    }
}
