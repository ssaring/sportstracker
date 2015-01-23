package de.saring.sportstracker.gui.views;

import javafx.print.PageLayout;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.transform.Scale;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.saring.sportstracker.core.STException;
import de.saring.sportstracker.core.STExceptionID;
import de.saring.sportstracker.gui.STContext;

/**
 * Class for printing SportsTracker views. It provides the printer action and remembers
 * the printer selection and page layout configuration.
 *
 * @author Stefan Saring
 */
@Singleton
public class ViewPrinter {

    private final STContext context;

    private Printer previousPrinter;
    private PageLayout previousPageLayout;

    /**
     * Standard c'tor for dependency injection.
     *
     * @param context the SportsTracker UI context
     */
    @Inject
    public ViewPrinter(final STContext context) {
        this.context = context;
    }

    /**
     * Prints the specified view.
     * 
     * @param rootNode root node of the view to print
     * @throws STException on printing problems
     */
    public void printView(final Node rootNode) throws STException {
        // TODO execution on a separate thread?

        final PrinterJob printerJob = PrinterJob.createPrinterJob();
        if (printerJob == null) {
            // no printer available
            context.showMessageDialog(context.getPrimaryStage(), Alert.AlertType.ERROR, //
                    "common.error", "st.main.error.print_view.no_printer");
            return;
        }

        // select printer and page layout from previous printing, if available
        if (previousPrinter != null) {
            printerJob.setPrinter(previousPrinter);
            if (previousPageLayout != null) {
                printerJob.getJobSettings().setPageLayout(previousPageLayout);
            }
        }

        // display print dialog for confirmation and configuration by the user
        final boolean printConfirmed = printerJob.showPrintDialog(context.getPrimaryStage());

        previousPageLayout = printerJob.getJobSettings().getPageLayout();
        previousPrinter = printerJob.getPrinter();

        if (printConfirmed) {
            if (printViewPage(printerJob, rootNode)) {
                if (!printerJob.endJob()) {
                    throw new STException(STExceptionID.GUI_PRINT_VIEW_FAILED, "Failed to end the print job!");
                }
            } else {
                throw new STException(STExceptionID.GUI_PRINT_VIEW_FAILED, "Failed to execute the view print!");
            }
        }
    }

    /**
     * Prints the specified view node completely to one single page.
     *
     * @param printerJob printer job which defines the printer, page layout, ...
     * @param view view node to print
     * @return true when the view was printed successfully
     */
    private boolean printViewPage(final PrinterJob printerJob, final Node view) {

        // the view needs to be scaled to fit the selected page layout of the PrinterJob
        // => the passed view node can't be scaled, this would scale the displayed UI
        // => solution: create a snapshot image for printing and scale this image
        final WritableImage snapshot = view.snapshot(null, null);
        final ImageView ivSnapshot = new ImageView(snapshot);

        // compute the needed scaling (aspect ratio must be kept)
        final PageLayout pageLayout = printerJob.getJobSettings().getPageLayout();
        final double scaleX = pageLayout.getPrintableWidth() / ivSnapshot.getImage().getWidth();
        final double scaleY = pageLayout.getPrintableHeight() / ivSnapshot.getImage().getHeight();
        final double scale = Math.min(scaleX, scaleY);

        // scale the calendar image only when it's too big for the selected page
        if (scale < 1.0) {
            ivSnapshot.getTransforms().add(new Scale(scale, scale));
        }

        return printerJob.printPage(ivSnapshot);
    }
}
