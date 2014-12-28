package de.saring.util.gui.jfreechart;

import java.awt.Color;

import javafx.scene.layout.Pane;

import javax.swing.JComponent;

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

    // TODO remove
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
    @Deprecated
    public static void customizeChart(JFreeChart chart, JComponent parent, boolean useParentBackground) {
        if (useParentBackground) {
            chart.setBackgroundPaint(parent.getBackground());
        }

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.GRAY);
        plot.setRangeGridlinePaint(Color.GRAY);

        java.awt.Font diaFont = parent.getFont();
        System.out.println(diaFont);
        plot.getDomainAxis().setTickLabelFont(diaFont);
        plot.getRangeAxis().setTickLabelFont(diaFont);
        plot.getDomainAxis().setLabelFont(diaFont);
        plot.getRangeAxis().setLabelFont(diaFont);

        if (chart.getLegend() != null) {
            chart.getLegend().setItemFont(diaFont);
        }
    }

    /**
     * Customizes the chart for SportsTracker requirements. The chart background
     * will be the same as the parent component background. The plot background
     * is white and the gridlines are grey. The diagram will use the default font for
     * JavaFX labels, the default chart fonts are much bigger.
     *
     * @param chart the chart component
     * @param parent the parent component of the chart
     */
    public static void customizeChart(JFreeChart chart, Pane parent) {

        // TODO currently there is no API to get the parent background color defined in CSS
        // => use static color of JavaFX Modena skin meanwhile
        chart.setBackgroundPaint(new Color(244, 244, 244));

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
