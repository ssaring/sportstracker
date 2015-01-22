package de.saring.sportstracker.gui.views;

import java.io.IOException;

import javafx.print.PageLayout;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.transform.Scale;

import de.saring.sportstracker.core.STException;
import de.saring.sportstracker.core.STExceptionID;
import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STController;
import de.saring.sportstracker.gui.STDocument;
import de.saring.util.gui.javafx.FxmlLoader;

/**
 * Abstract Controller (MVC) base class of for all SportsTracker content Views.
 *
 * @author Stefan Saring
 */
public abstract class AbstractEntryViewController implements EntryViewController {

    private static final int[] EMPTY_ID_ARRAY = new int[0];

    private final STContext context;
    private final STDocument document;
    private final STController controller;

    private Parent rootNode;

    /**
     * Standard c'tor for dependency injection.
     *
     * @param context the SportsTracker UI context
     * @param document the SportsTracker document / model
     * @param controller the SportsTracker UI controller
     */
    public AbstractEntryViewController(final STContext context, final STDocument document, final STController controller) {
        this.context = context;
        this.document = document;
        this.controller = controller;
    }

    @Override
    public void loadAndSetupViewContent() {
        final String fxmlFilename = getFxmlFilename();

        try {
            rootNode = FxmlLoader.load(this.getClass().getResource(fxmlFilename), //
                    context.getResources().getResourceBundle(), this);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load the FXML resource '" + fxmlFilename + "'!", e);
        }

        setupView();
    }

    @Override
    public Parent getRootNode() {
        return rootNode;
    }

    @Override
    public int getSelectedExerciseCount() {
        return 0;
    }

    @Override
    public int[] getSelectedExerciseIDs() {
        return EMPTY_ID_ARRAY;
    }

    @Override
    public int getSelectedNoteCount() {
        return 0;
    }

    @Override
    public int[] getSelectedNoteIDs() {
        return EMPTY_ID_ARRAY;
    }

    @Override
    public int getSelectedWeightCount() {
        return 0;
    }

    @Override
    public int[] getSelectedWeightIDs() {
        return EMPTY_ID_ARRAY;
    }

    @Override
    public void print() throws STException {
        // TODO execution on a separate thread?

        final PrinterJob printerJob = PrinterJob.createPrinterJob();
        if (printerJob == null) {
            // no printer available
            context.showMessageDialog(context.getPrimaryStage(), Alert.AlertType.ERROR, //
                    "common.error", "st.main.error.print_view.no_printer");
            return;
        }

        // display print dialog for confirmation and configuration by the user
        final boolean printConfirmed = printerJob.showPrintDialog(getContext().getPrimaryStage());
        // TODO remember the printer configuration (printer, page layout) for next printing? Store JobSettings?

        if (printConfirmed) {
            if (printView(printerJob, rootNode)) {
                if (!printerJob.endJob()) {
                    throw new STException(STExceptionID.GUI_PRINT_VIEW_FAILED, "Failed to end the print job!");
                }
            } else {
                throw new STException(STExceptionID.GUI_PRINT_VIEW_FAILED, "Failed to execute the view print!");
            }
        }
    }

    /**
     * Returns the name of the FXML file which contains the UI definition of the view.
     *
     * @return FXML filename
     */
    protected abstract String getFxmlFilename();

    /**
     * Sets up all the view controls. Will be called after the UI has been loaded from FXML.
     */
    protected abstract void setupView();

    /**
     * Returns the SportsTracker UI context.
     *
     * @return STContext
     */
    protected STContext getContext() {
        return context;
    }

    /**
     * Returns the SportsTracker model/document.
     *
     * @return STDocument
     */
    protected STDocument getDocument() {
        return document;
    }

    /**
     * Returns the SportsTracker UI controller.
     *
     * @return STController
     */
    protected STController getController() {
        return controller;
    }

    /**
     * Prints the specified view node completely to one single page.
     *
     * @param printerJob printer job which defines the printer, page layout, ...
     * @param view view node to print
     * @return true when the view was printed successfully
     */
    private boolean printView(final PrinterJob printerJob, final Node view) {

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
