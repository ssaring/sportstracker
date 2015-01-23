
package de.saring.sportstracker.gui.views;

import java.util.logging.Logger;

import javafx.concurrent.Task;
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

import de.saring.sportstracker.gui.STContext;

/**
 * Class for printing SportsTracker views. It provides the printer action and remembers
 * the printer selection and page layout configuration.
 *
 * @author Stefan Saring
 */
@Singleton
public class ViewPrinter {

    private static final Logger LOGGER = Logger.getLogger(ViewPrinter.class.getName());

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
     * Prints the specified view. The print action is executed asynchronously, so it does not block
     * the application UI thread. However, the application window will be blocked during printing
     * to avoid user actions (e.g. restart the print action).
     * 
     * @param rootNode root node of the view to print
     */
    public void printView(final Node rootNode) {
        LOGGER.info("Printing current view...");

        context.blockMainWindow(true);
        new Thread(new CreatePrinterJobTask(rootNode)).start();
    }

    /**
     * Executes the print action on the JavaFX UI thread after the PrinterJob has been created.
     *
     * @param rootNode root node of view to print
     * @param printerJob created printer job, can be null
     */
    private void printViewInJob(final Node rootNode, final PrinterJob printerJob) {
        boolean success = false;

        try {
            // PrinterJob is null when no printer is available (most systems provide default printers)
            if (printerJob == null) {
                context.showMessageDialog(context.getPrimaryStage(), Alert.AlertType.ERROR, //
                        "common.error", "st.main.error.print_view.no_printer");
                return;
            }

            // use printer and page layout from previous printing, if available
            restorePrinterConfiguration(printerJob);

            // display print dialog for confirmation and configuration by the user
            final boolean printConfirmed = printerJob.showPrintDialog(context.getPrimaryStage());
            storePrinterConfiguration(printerJob);

            if (printConfirmed) {
                // the printing itself is quite fast, no need to execute it asynchronously
                if (printViewPage(printerJob, rootNode)) {
                    if (printerJob.endJob()) {
                        success = true;
                    } else {
                        LOGGER.severe("Failed to end the print job!");
                    }
                } else {
                    LOGGER.severe("Failed to execute the view print!");
                }
            } else {
                success = true;
            }
        } finally {
            context.blockMainWindow(false);
            if (!success) {
                context.showMessageDialog(context.getPrimaryStage(), Alert.AlertType.ERROR, //
                        "common.error", "st.main.error.print_view");
            }
        }
    }

    private void restorePrinterConfiguration(final PrinterJob printerJob) {
        if (previousPrinter != null) {
            printerJob.setPrinter(previousPrinter);
            if (previousPageLayout != null) {
                printerJob.getJobSettings().setPageLayout(previousPageLayout);
            }
        }
    }

    private void storePrinterConfiguration(final PrinterJob printerJob) {
        previousPageLayout = printerJob.getJobSettings().getPageLayout();
        previousPrinter = printerJob.getPrinter();
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

    /**
     * This class creates a PrinterJob as a asynchronous Task to avoid blocking of the application UI.
     * The lookup of available printers can take some time, depending on the OS, network and printers
     * (mostly on the first run).
     */
    private class CreatePrinterJobTask extends Task<PrinterJob> {
        private final Node view;

        public CreatePrinterJobTask(final Node view) {
            this.view = view;
        }

        @Override
        protected PrinterJob call() throws Exception {
            LOGGER.info("Creating printer job...");
            return PrinterJob.createPrinterJob();
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            printViewInJob(view, getValue());
        }

        @Override
        protected void failed() {
            super.failed();
            context.blockMainWindow(false);
        }
    }
}
