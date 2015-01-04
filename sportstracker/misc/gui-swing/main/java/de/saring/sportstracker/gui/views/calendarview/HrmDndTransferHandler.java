package de.saring.sportstracker.gui.views.calendarview;

import de.saring.exerciseviewer.core.EVException;
import de.saring.exerciseviewer.parser.ExerciseParserFactory;
import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STController;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class handles the Drag&Drop-transfer of HRM exercise files from
 * outside the application (e.g. from Nautilus or Explorer) to the calendar
 * widget.
 *
 * @author Stefan Saring
 */
public class HrmDndTransferHandler extends TransferHandler {
    private static final Logger LOGGER = Logger.getLogger(HrmDndTransferHandler.class.getName());

    private final STContext context;
    private final STController controller;
    private final CalendarWidget calendarWidget;

    /**
     * Standard c'tor.
     *
     * @param context the SportsTracker context
     * @param controller the SportsTracker controller
     * @param calendarWidget the calendar widget
     */
    public HrmDndTransferHandler(STContext context, STController controller, CalendarWidget calendarWidget) {
        this.context = context;
        this.controller = controller;
        this.calendarWidget = calendarWidget;
    }

    @Override
    public boolean canImport(TransferSupport support) {

        // only drops are supported (not clipboard paste)
        if (!support.isDrop()) {
            return false;
        }

        // It's only possible to check the flavor here, not the transfered content
        // (will throw "java.awt.dnd.InvalidDnDOperationException: No drop current")
        if (!isWindowsFileDragFlavor(support) && !isUnixFileDragFlavor(support)) {
            return false;
        }

        // accept only drops to a calendar day cell
        CalendarDay calDayTarget = calendarWidget.getCalendarDayForPoint(
                support.getDropLocation().getDropPoint());
        return calDayTarget != null;
    }

    @Override
    public boolean importData(TransferSupport support) {

        // must be checked again (e.g. for copy&paste actions) 
        if (!canImport(support)) {
            return false;
        }

        // try to get name of dropped file
        String dropFilename = getFileToDrop(support);
        if (dropFilename == null) {
            context.showMessageDialog(calendarWidget, JOptionPane.ERROR_MESSAGE,
                    "common.error", "st.calview.draganddrop.invalid_hrm_file");
            return false;
        }

        // get the drop destination entry, this must be an exercise (if there is one)
        Optional<CalendarEntry> oCalEntryToDrop = calendarWidget.getCalendarEntryForPoint(
                support.getDropLocation().getDropPoint());

        Exercise exerciseToDrop = null;
        if (oCalEntryToDrop.isPresent() && oCalEntryToDrop.get().isExercise()) {
            exerciseToDrop = (Exercise) oCalEntryToDrop.get().getEntry();
        }

        // assign the HRM file to the destination exercise or add a new one
        return controller.dropHrmFile(dropFilename, exerciseToDrop);
    }

    /**
     * Does the specified TransferSupport contain Windows-specific dragged
     * files? The data flavor of dragged files is a List of File objects.
     *
     * @param support the object containing the details of the transfer, not null
     * @return true when dragging files on Windows
     */
    private boolean isWindowsFileDragFlavor(TransferSupport support) {
        return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
    }

    /**
     * Does the specified TransferSupport contain Unix/Linux-specific dragged
     * files? The data flavor of dragged files is plain text of the filenames
     * (InputStream with Unicode encoding).
     *
     * @param support the object containing the details of the transfer, not null
     * @return true when dragging files on Unix/Linux
     */
    private boolean isUnixFileDragFlavor(TransferSupport support) {
        return support.isDataFlavorSupported(DataFlavor.getTextPlainUnicodeFlavor());
    }

    /**
     * Checks if the specified TransferSupport contains one single HRM file which
     * is supported by the HRM importer and returns it. Otherwise it returns null.
     * Windows and Unix systems have different handling for drag and drop of files.
     *
     * @param support the object containing the details of the transfer, not null
     * @return the absolute path of the HRM file to drop or null
     */
    private String getFileToDrop(TransferSupport support) {
        List<File> fileList = null;

        Transferable transferable = support.getTransferable();
        if (isWindowsFileDragFlavor(support)) {
            fileList = getFileListForWindows(transferable);
        } else if (isUnixFileDragFlavor(support)) {
            fileList = getFileListForUnix(transferable);
        }

        if (fileList == null ||
                fileList.size() != 1 ||
                fileList.get(0).isDirectory()) {
            return null;
        }

        try {
            String dropFilename = fileList.get(0).getAbsolutePath();
            ExerciseParserFactory.getParser(dropFilename);
            return dropFilename;
        } catch (EVException pe) {
            return null;
        }
    }

    /**
     * Creates a list of files from the specified drag&drop Transferable.
     * It only works on Windows systems, the data flavor of dragged files is
     * a List of File objects.
     *
     * @param transferable the data of the drag&drop operation
     * @return list of files or null on errors
     */
    @SuppressWarnings("unchecked")
    private List<File> getFileListForWindows(Transferable transferable) {
        try {
            return (List<File>) transferable.getTransferData(
                    DataFlavor.javaFileListFlavor);
        } catch (UnsupportedFlavorException | IOException e) {
            LOGGER.log(Level.WARNING, "Failed to get the list of dragged files!", e);
            return null;
        }
    }

    /**
     * Creates a list of files from the specified drag&drop Transferable.
     * It only works on Unix/Linux systems, the data flavor of dragged files is
     * plain unicode text of the URI's (must be accessed by using a reader).
     *
     * @param transferable the data of the drag&drop operation
     * @return list of files or null on errors
     */
    private List<File> getFileListForUnix(Transferable transferable) {
        List<File> lFiles = new ArrayList<>();
        DataFlavor textPlainFlavor = DataFlavor.getTextPlainUnicodeFlavor();

        try (Reader reader = textPlainFlavor.getReaderForText(transferable);
             BufferedReader bReader = new BufferedReader(reader)) {

            String line = null;
            while ((line = bReader.readLine()) != null) {
                // KDE seems to append a 0 char to the reader's end => ignore
                if ("0".equals(line)) {
                    continue;
                }
                lFiles.add(new File(new URI(line)));
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to get the list of dragged files!", e);
        }
        return lFiles;
    }
}
