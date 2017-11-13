/*
 * This class is derived from JFreeChart 1.0.9 StackedXYAreaRenderer2
 * and XYLineAndShapeRenderer which have the following copyright:
 */
/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2007, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation; either version 2.1 of the License, or 
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, 
 * USA.  
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * ---------------------------
 * StackedXYAreaRenderer2.java
 * ---------------------------
 * (C) Copyright 2004-2007, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited), based on 
 *                   the StackedXYAreaRenderer class by Richard Atkinson;
 * Contributor(s):   -;
 *
 * ---------------------------
 * XYLineAndShapeRenderer.java
 * ---------------------------
 * (C) Copyright 2004-2007, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 */

package de.saring.util.gui.jfreechart;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StackedXYAreaRenderer2;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.data.Range;
import org.jfree.data.xy.TableXYDataset;
import org.jfree.data.xy.XYDataset;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

/**
 * A stacked area renderer which supports transparency.
 *
 * @author Kai Pastor, based on the StackedXYAreaRenderer class
 *         by David Gilbert (for Object Refinery Limited),
 *         which is based on  the StackedXYAreaRenderer class
 *         by Richard Atkinson;
 */
public class StackedRenderer extends StackedXYAreaRenderer2 {

    private static final long serialVersionUID = 7251046217195966375L;

    /**
     * The alpha component to be used for the area (0.0 ... 1.0).
     */
    private float areaAlpha;

    /**
     * This flag controls whether or not the x-coordinates (in Java2D space)
     * are rounded to integers.  When set to true, this can avoid the vertical
     * striping that anti-aliasing can generate.  However, the rounding may not
     * be appropriate for output in high resolution formats (for example,
     * vector graphics formats such as SVG and PDF).
     *
     * @since 1.0.3
     */
    private boolean roundXCoordinates;

    /**
     * Creates a new renderer.
     */
    public StackedRenderer() {
        this(null, null);
    }

    /**
     * Constructs a new renderer.
     *
     * @param labelGenerator the tool tip generator to use.  <code>null</code>
     * is none.
     * @param urlGenerator the URL generator (<code>null</code> permitted).
     */
    public StackedRenderer(XYToolTipGenerator labelGenerator,
                           XYURLGenerator urlGenerator) {
        super(labelGenerator, urlGenerator);
        this.roundXCoordinates = super.getRoundXCoordinates();
        this.areaAlpha = 0.5f;
    }

    /**
     * Sets the alpha component to be applied for the area paint.
     *
     * @param areaAlpha the alpha component (0.0 ... 1.0)
     */
    public void setAreaAlpha(float areaAlpha) {
        this.areaAlpha = areaAlpha;
    }

    /**
     * Gets the alpha component to be applied for the area paint.
     *
     * @return the alpha component (0.0 ... 1.0)
     */
    public float getAreaAlpha() {
        return areaAlpha;
    }

    /**
     * Sets the flag that controls whether or not the x-coordinates (in
     * Java2D space) are rounded to integer values, and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param round the new flag value.
     * @see #getRoundXCoordinates()
     * @since 1.0.4
     */
    @Override
    public void setRoundXCoordinates(boolean round) {
        this.roundXCoordinates = round;
        super.setRoundXCoordinates(true);
    }

    /**
     * Returns the range of values the renderer requires to display all the
     * items from the specified dataset.
     *
     * @param dataset the dataset (<code>null</code> permitted).
     * @return The range (or <code>null</code> if the dataset is
     *         <code>null</code> or empty).
     */
    @Override
    public Range findRangeBounds(XYDataset dataset) {
        if (dataset == null) {
            return null;
        }
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        TableXYDataset d = (TableXYDataset) dataset;
        int itemCount = d.getItemCount();
        for (int i = 0; i < itemCount; i++) {
            double[] stackValues = getStackValues((TableXYDataset) dataset,
                    d.getSeriesCount(), i);
            min = Math.min(min, stackValues[0]);
            max = Math.max(max, stackValues[1]);
        }
        if (min == Double.POSITIVE_INFINITY) {
            return null;
        }
        return new Range(min, max);
    }

    /**
     * Returns the number of passes required by the renderer.
     *
     * @return 1.
     */
    @Override
    public int getPassCount() {
        return 2;
    }

    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2 the graphics device.
     * @param state the renderer state.
     * @param dataArea the area within which the data is being drawn.
     * @param info collects information about the drawing.
     * @param plot the plot (can be used to obtain standard color information
     * etc).
     * @param domainAxis the domain axis.
     * @param rangeAxis the range axis.
     * @param dataset the dataset.
     * @param series the series index (zero-based).
     * @param item the item index (zero-based).
     * @param crosshairState information about crosshairs on a plot.
     * @param pass the pass index.
     */
    @Override
    public void drawItem(Graphics2D g2,
                         XYItemRendererState state,
                         Rectangle2D dataArea,
                         PlotRenderingInfo info,
                         XYPlot plot,
                         ValueAxis domainAxis,
                         ValueAxis rangeAxis,
                         XYDataset dataset,
                         int series,
                         int item,
                         CrosshairState crosshairState,
                         int pass) {

        // setup for collecting optional entity info...
        Shape entityArea = null;
        EntityCollection entities = null;
        if (info != null) {
            entities = info.getOwner().getEntityCollection();
        }

        TableXYDataset tdataset = (TableXYDataset) dataset;

        // get the data point...
        double x1 = dataset.getXValue(series, item);
        double y1 = dataset.getYValue(series, item);
        if (Double.isNaN(y1)) {
            y1 = 0.0;
        }
        double[] stack1 = getStackValues(tdataset, series, item);

        // get the previous point and the next point so we can calculate a 
        // "hot spot" for the area (used by the chart entity)...
        double x0 = dataset.getXValue(series, Math.max(item - 1, 0));
        double y0 = dataset.getYValue(series, Math.max(item - 1, 0));
        if (Double.isNaN(y0)) {
            y0 = 0.0;
        }
        double[] stack0 = getStackValues(tdataset, series, Math.max(item - 1,
                0));

        int itemCount = dataset.getItemCount(series);
        double x2 = dataset.getXValue(series, Math.min(item + 1,
                itemCount - 1));
        double y2 = dataset.getYValue(series, Math.min(item + 1,
                itemCount - 1));
        if (Double.isNaN(y2)) {
            y2 = 0.0;
        }
        double[] stack2 = getStackValues(tdataset, series, Math.min(item + 1,
                itemCount - 1));

        double xleft = (x0 + x1) / 2.0;
        double xright = (x1 + x2) / 2.0;
        double[] stackLeft = averageStackValues(stack0, stack1);
        double[] stackRight = averageStackValues(stack1, stack2);
        double[] adjStackLeft = adjustedStackValues(stack0, stack1);
        double[] adjStackRight = adjustedStackValues(stack1, stack2);

        RectangleEdge edge0 = plot.getDomainAxisEdge();

        float transX1 = (float) domainAxis.valueToJava2D(x1, dataArea, edge0);
        float transXLeft = (float) domainAxis.valueToJava2D(xleft, dataArea,
                edge0);
        float transXRight = (float) domainAxis.valueToJava2D(xright, dataArea,
                edge0);

        if (this.roundXCoordinates) {
            transX1 = Math.round(transX1);
            transXLeft = Math.round(transXLeft);
            transXRight = Math.round(transXRight);
        }
        float transY1;

        RectangleEdge edge1 = plot.getRangeAxisEdge();

        GeneralPath left = new GeneralPath();
        GeneralPath right = new GeneralPath();
        if (y1 >= 0.0) {  // handle positive value
            transY1 = (float) rangeAxis.valueToJava2D(y1 + stack1[1], dataArea,
                    edge1);
            float transStack1 = (float) rangeAxis.valueToJava2D(stack1[1],
                    dataArea, edge1);
            float transStackLeft = (float) rangeAxis.valueToJava2D(
                    stackLeft[1], dataArea, edge1); // other than StackedXYAreaRenderer2!

            // LEFT POLYGON
            if (y0 >= 0.0) {
                double yleft = (y0 + y1) / 2.0 + stackLeft[1];
                float transYLeft
                        = (float) rangeAxis.valueToJava2D(yleft, dataArea, edge1);
                left.moveTo(transX1, transY1);
                left.lineTo(transX1, transStack1);
                left.lineTo(transXLeft, transStackLeft);
                left.lineTo(transXLeft, transYLeft);
                left.closePath();
            } else {
                left.moveTo(transX1, transStack1);
                left.lineTo(transX1, transY1);
                left.lineTo(transXLeft, transStackLeft);
                left.closePath();
            }

            float transStackRight = (float) rangeAxis.valueToJava2D(
                    stackRight[1], dataArea, edge1); // other than StackedXYAreaRenderer2!
            // RIGHT POLYGON
            if (y2 >= 0.0) {
                double yright = (y1 + y2) / 2.0 + stackRight[1];
                float transYRight
                        = (float) rangeAxis.valueToJava2D(yright, dataArea, edge1);
                right.moveTo(transX1, transStack1);
                right.lineTo(transX1, transY1);
                right.lineTo(transXRight, transYRight);
                right.lineTo(transXRight, transStackRight);
                right.closePath();
            } else {
                right.moveTo(transX1, transStack1);
                right.lineTo(transX1, transY1);
                right.lineTo(transXRight, transStackRight);
                right.closePath();
            }
        } else {  // handle negative value
            transY1 = (float) rangeAxis.valueToJava2D(y1 + stack1[0], dataArea,
                    edge1);
            float transStack1 = (float) rangeAxis.valueToJava2D(stack1[0],
                    dataArea, edge1);
            float transStackLeft = (float) rangeAxis.valueToJava2D(
                    adjStackLeft[0], dataArea, edge1);

            // LEFT POLYGON
            if (y0 >= 0.0) {
                left.moveTo(transX1, transStack1);
                left.lineTo(transX1, transY1);
                left.lineTo(transXLeft, transStackLeft);
                left.clone();
            } else {
                double yleft = (y0 + y1) / 2.0 + stackLeft[0];
                float transYLeft = (float) rangeAxis.valueToJava2D(yleft,
                        dataArea, edge1);
                left.moveTo(transX1, transY1);
                left.lineTo(transX1, transStack1);
                left.lineTo(transXLeft, transStackLeft);
                left.lineTo(transXLeft, transYLeft);
                left.closePath();
            }
            float transStackRight = (float) rangeAxis.valueToJava2D(
                    adjStackRight[0], dataArea, edge1);

            // RIGHT POLYGON
            if (y2 >= 0.0) {
                right.moveTo(transX1, transStack1);
                right.lineTo(transX1, transY1);
                right.lineTo(transXRight, transStackRight);
                right.closePath();
            } else {
                double yright = (y1 + y2) / 2.0 + stackRight[0];
                float transYRight = (float) rangeAxis.valueToJava2D(yright,
                        dataArea, edge1);
                right.moveTo(transX1, transStack1);
                right.lineTo(transX1, transY1);
                right.lineTo(transXRight, transYRight);
                right.lineTo(transXRight, transStackRight);
                right.closePath();
            }
        }

        //  Get series Paint and Stroke
        Paint itemPaint = getItemPaint(series, item);
        if (pass == 0) {
            g2.setPaint(itemPaint);
            g2.fill(left);
            g2.fill(right);
        } else if (pass == 1) {
            if (item == 0 || (y1 == 0.0 && y0 == 0.0)) {
                return;
            }

            // get the data point...
            if (Double.isNaN(y1) || Double.isNaN(x1)) {
                return;
            }

            if (Double.isNaN(y0) || Double.isNaN(x0)) {
                return;
            }

            RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
            RectangleEdge yAxisLocation = plot.getRangeAxisEdge();

            double transX0 = domainAxis.valueToJava2D(x0, dataArea, xAxisLocation);
            double transY0 = rangeAxis.valueToJava2D(y0 + stack0[1], dataArea, yAxisLocation);

            // only draw if we have good values
            if (Double.isNaN(transX0) || Double.isNaN(transY0)
                    || Double.isNaN(transX1) || Double.isNaN(transY1)) {
                return;
            }

            state.workingLine.setLine(transX0, transY0, transX1, transY1);

            if (state.workingLine.intersects(dataArea)) {
                g2.setStroke(getItemStroke(series, item));
                g2.setPaint(super.lookupSeriesPaint(series));
                g2.draw(state.workingLine);
            }

            return;
        }

        // add an entity for the item...
        if (entities != null) {
            GeneralPath gp = new GeneralPath(left);
            gp.append(right, false);
            entityArea = gp;
            addEntity(entities, entityArea, dataset, series, item,
                    transX1, transY1);
        }

    }

    /**
     * Calculates the stacked values (one positive and one negative) of all
     * series up to, but not including, <code>series</code> for the specified
     * item. It returns [0.0, 0.0] if <code>series</code> is the first series.
     *
     * @param dataset the dataset (<code>null</code> not permitted).
     * @param series the series index.
     * @param index the item index.
     * @return An array containing the cumulative negative and positive values
     *         for all series values up to but excluding <code>series</code>
     *         for <code>index</code>.
     */
    private double[] getStackValues(TableXYDataset dataset,
                                    int series, int index) {
        double[] result = new double[2];
        for (int i = 0; i < series; i++) {
            double v = dataset.getYValue(i, index);
            if (!Double.isNaN(v)) {
                if (v >= 0.0) {
                    result[1] += v;
                } else {
                    result[0] += v;
                }
            }
        }
        return result;
    }

    /**
     * Returns a pair of "stack" values calculated as the mean of the two
     * specified stack value pairs.
     *
     * @param stack1 the first stack pair.
     * @param stack2 the second stack pair.
     * @return A pair of average stack values.
     */
    private double[] averageStackValues(double[] stack1, double[] stack2) {
        double[] result = new double[2];
        result[0] = (stack1[0] + stack2[0]) / 2.0;
        result[1] = (stack1[1] + stack2[1]) / 2.0;
        return result;
    }

    /**
     * Calculates adjusted stack values from the supplied values.  The value is
     * the mean of the supplied values, unless either of the supplied values
     * is zero, in which case the adjusted value is zero also.
     *
     * @param stack1 the first stack pair.
     * @param stack2 the second stack pair.
     * @return A pair of average stack values.
     */
    private double[] adjustedStackValues(double[] stack1, double[] stack2) {
        double[] result = new double[2];
        if (stack1[0] == 0.0 || stack2[0] == 0.0) {
            result[0] = 0.0;
        } else {
            result[0] = (stack1[0] + stack2[0]) / 2.0;
        }
        if (stack1[1] == 0.0 || stack2[1] == 0.0) {
            result[1] = 0.0;
        } else {
            result[1] = (stack1[1] + stack2[1]) / 2.0;
        }
        return result;
    }

    /**
     * Returns the paint used to fill an item drawn by the renderer.
     * Overridden for proper display of legend items
     *
     * @param series the series index (zero-based).
     * @return The paint (never <code>null</code>).
     */
    @Override
    public Paint lookupSeriesPaint(int series) {
        Paint paint = super.lookupSeriesPaint(series);
        if (paint instanceof Color) {
            Color color = (Color) paint;
            paint = new Color(color.getColorSpace(), color.getColorComponents(null), areaAlpha);
        }
        return paint;
    }

    /**
     * Tests this renderer for equality with an arbitrary object.
     *
     * @param obj the object (<code>null</code> permitted).
     * @return A boolean.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StackedRenderer)) {
            return false;
        }
        StackedRenderer that = (StackedRenderer) obj;
        if (this.roundXCoordinates != that.roundXCoordinates) {
            return false;
        }
        if (Math.abs(this.areaAlpha - that.areaAlpha) > 0.0000001) {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return 42;
    }

    /**
     * Returns a clone of the renderer.
     *
     * @return A clone.
     * @throws CloneNotSupportedException if the renderer cannot be cloned.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
