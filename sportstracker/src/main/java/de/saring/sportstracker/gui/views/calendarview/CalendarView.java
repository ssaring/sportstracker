package de.saring.sportstracker.gui.views.calendarview;

import javax.inject.Inject;
import javax.inject.Singleton;
import de.saring.sportstracker.core.STException;
import de.saring.sportstracker.core.STExceptionID;
import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.data.Note;
import de.saring.sportstracker.data.Weight;
import de.saring.sportstracker.gui.STController;
import de.saring.sportstracker.gui.views.BaseView;
import de.saring.util.data.IdDateObject;
import de.saring.util.data.IdDateObjectList;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.Calendar;
import java.util.Date;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.standard.OrientationRequested;
import javax.swing.ActionMap;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.jdesktop.application.Action;

/**
 * This view class displays all (or a filtered list) exercises of the selected month in a calendar
 * view. It contains all the navigation widgets and functionality and the drawed calendar widget.
 * 
 * @author Stefan Saring
 * @version 1.0
 */
@Singleton
public class CalendarView extends BaseView {

    /** Constants for action and property names. */
    private static final String ACTION_PREVIOUS_MONTH = "st.calview.previousMonth";
    private static final String ACTION_NEXT_MONTH = "st.calview.nextMonth";
    private static final String ACTION_PREVIOUS_YEAR = "st.calview.previousYear";
    private static final String ACTION_NEXT_YEAR = "st.calview.nextYear";
    private static final String ACTION_TODAY = "st.calview.today";

    /** The custom calendar widget. */
    private CalendarWidget calendarWidget;

    /** The list of month names. */
    private String[] arMonthNames;

    /** The current displayed month. */
    private int currentMonth;

    /** The current displayed year. */
    private int currentYear;

    /** The popup (context) menu for the calendar. */
    private CalendarPopupMenu popupMenu;

    /**
     * Standard c'tor.
     * 
     * @param context the SportsTracker context
     * @param calendarWidget the CalendarWidget component
     */
    @Inject
    public CalendarView(final CalendarWidget calendarWidget) {
        this.calendarWidget = calendarWidget;
    }

    /** {@inheritDoc} */
    @Override
    public void initView () {
        initComponents ();
        
        // create list of month names
        arMonthNames = new String[12];
        for (int i = 0; i < 12; i++) {
            arMonthNames[i] = getContext ().getResReader ().getString ("st.calview.months." + (i+1));
        }
        
        // start with current date
        Calendar calToday = Calendar.getInstance();
        currentMonth = calToday.get (Calendar.MONTH) + 1;
        currentYear = calToday.get (Calendar.YEAR);
        
        // setup actions
        ActionMap actionMap = getContext ().getSAFContext ().getActionMap (getClass (), this);
        btPreviousMonth.setAction (actionMap.get (ACTION_PREVIOUS_MONTH));
        btNextMonth.setAction (actionMap.get (ACTION_NEXT_MONTH));
        btPreviousYear.setAction (actionMap.get (ACTION_PREVIOUS_YEAR));
        btNextYear.setAction (actionMap.get (ACTION_NEXT_YEAR));
        btToday.setAction (actionMap.get (ACTION_TODAY));

        // add calendar widget
        paCalendar.add (calendarWidget, BorderLayout.CENTER);
        
        // setup the calendar popup menu
        popupMenu = new CalendarPopupMenu ();
        popupMenu.add (getController().getActionMap ().get (STController.ACTION_EXERCISE_ADD));
        popupMenu.add (getController().getActionMap ().get (STController.ACTION_NOTE_ADD));
        popupMenu.add (getController().getActionMap ().get (STController.ACTION_WEIGHT_ADD));
        popupMenu.add (getController().getActionMap ().get (STController.ACTION_ENTRY_EDIT));
        popupMenu.add (getController().getActionMap ().get (STController.ACTION_ENTRY_COPY));
        popupMenu.add (getController().getActionMap ().get (STController.ACTION_ENTRY_DELETE));
        
        // register mouse listener for the calendar (check for pressed, not clicked
        // events, otherwise it doen't work when the mouse have been moved meanwhile)
        calendarWidget.addMouseListener (new MouseAdapter() {
            @Override public void mousePressed (final MouseEvent me) {
                onCalendarClicked (me);
            }
        });
        
        // register the mouse wheel scrolling on drawing area
        calendarWidget.addMouseWheelListener (new MouseWheelListener () {
            public void mouseWheelMoved (final MouseWheelEvent mwe) {
                if (mwe.getWheelRotation () < 0) {
                    displayPreviousMonth ();
                }
                if (mwe.getWheelRotation () > 0) {
                    displayNextMonth ();
                }
            }
        });
    }

    /**
     * Updates the view after data was modified.
     */
    @Override
    public void updateView() {
        updateCalendar();
    }

    /**
     * This methods returns the number of selected exercises.
     * 
     * @return number of selected exercises
     */
    @Override
    public int getSelectedExerciseCount() {
        return getSelectedExerciseIDs().length;
    }

    /**
     * This methods returns the list of the currently selected exercise ID's (maximum count is 1 in
     * the calendar view).
     * 
     * @return array of the selected exercise ID's (can be empty but not null)
     */
    @Override
    public int[] getSelectedExerciseIDs() {
        return getSelectedEntryIDsOfClass(Exercise.class);
    }

    /** {@inheritDoc} */
    @Override
    public int getSelectedNoteCount() {
        return getSelectedNoteIDs().length;
    }

    /** {@inheritDoc} */
    @Override
    public int[] getSelectedNoteIDs() {
        return getSelectedEntryIDsOfClass(Note.class);
    }

    /** {@inheritDoc} */
    @Override
    public int getSelectedWeightCount() {
        return getSelectedWeightIDs().length;
    }

    /** {@inheritDoc} */
    @Override
    public int[] getSelectedWeightIDs() {
        return getSelectedEntryIDsOfClass(Weight.class);
    }

    /**
     * Returns the list of the currently selected CalendarEntry ID's of the specified type (maximum
     * count in the calendar view is 1).
     * 
     * @return array of the selected CalendarEntry ID's (can be empty but not null)
     */
    private int[] getSelectedEntryIDsOfClass(final Class<? extends IdDateObject> clazz) {
        CalendarEntry selectedCalendarEntry = calendarWidget.getSelectedCalendarEntry();
        if ((selectedCalendarEntry == null) || (selectedCalendarEntry.getEntry().getClass() != clazz)) {
            return new int[0];
        } else {
            return new int[] {
                selectedCalendarEntry.getEntry().getId()
            };
        }
    }

    @Override
    public void selectEntry(IdDateObject entry) {
        // TODO: implement this feature
        updateView();
    }
    
    /**
     * Removes the current selection in the page.
     */
    @Override
    public void removeSelection() {
        calendarWidget.removeSelection();
        getView().updateEntryActions();
    }

    /**
     * This action displays the previous month.
     */
    @Action(name = ACTION_PREVIOUS_MONTH)
    public void displayPreviousMonth() {
        if (currentMonth > 1) {
            currentMonth--;
        } else {
            currentMonth = 12;
            currentYear--;
        }

        removeSelection();
        updateCalendar();
    }

    /**
     * This action displays the next month.
     */
    @Action(name = ACTION_NEXT_MONTH)
    public void displayNextMonth() {
        if (currentMonth < 12) {
            currentMonth++;
        } else {
            currentMonth = 1;
            currentYear++;
        }

        removeSelection();
        updateCalendar();
    }

    /**
     * This action displays the previous year.
     */
    @Action(name = ACTION_PREVIOUS_YEAR)
    public void displayPreviousYear() {
        currentYear--;
        removeSelection();
        updateCalendar();
    }

    /**
     * This action displays the next year.
     */
    @Action(name = ACTION_NEXT_YEAR)
    public void displayNextYear() {
        currentYear++;
        removeSelection();
        updateCalendar();
    }

    /**
     * This action selects the month with the current date.
     */
    @Action(name = ACTION_TODAY)
    public void selectToday() {
        Calendar calToday = Calendar.getInstance();
        currentMonth = calToday.get(Calendar.MONTH) + 1;
        currentYear = calToday.get(Calendar.YEAR);
        updateCalendar();
    }

    /**
     * Updates the Calendar and the navigation widgets.
     */
    private void updateCalendar() {

        // display the current selected month and year
        laMonth.setText(arMonthNames[currentMonth - 1]);
        laYear.setText(String.valueOf(currentYear));

        // calculate the first displayed day in calendar (this is mostly a day of the previous
        // month)
        Calendar cMonthStart = Calendar.getInstance();
        cMonthStart.clear();
        boolean isWeekStartSunday = getDocument().getOptions().isWeekStartSunday();
        cMonthStart.setFirstDayOfWeek(isWeekStartSunday ? Calendar.SUNDAY : Calendar.MONDAY);
        cMonthStart.set(currentYear, currentMonth - 1, 1, 0, 0, 0);
        Calendar cCalendarStart = (Calendar)cMonthStart.clone();

        if (isWeekStartSunday) {
            switch (cMonthStart.get(Calendar.DAY_OF_WEEK)) {
                case Calendar.MONDAY:
                    cCalendarStart.add(Calendar.DATE, -1);
                    break;
                case Calendar.TUESDAY:
                    cCalendarStart.add(Calendar.DATE, -2);
                    break;
                case Calendar.WEDNESDAY:
                    cCalendarStart.add(Calendar.DATE, -3);
                    break;
                case Calendar.THURSDAY:
                    cCalendarStart.add(Calendar.DATE, -4);
                    break;
                case Calendar.FRIDAY:
                    cCalendarStart.add(Calendar.DATE, -5);
                    break;
                case Calendar.SATURDAY:
                    cCalendarStart.add(Calendar.DATE, -6);
                    break;
            }
        } else {
            switch (cMonthStart.get(Calendar.DAY_OF_WEEK)) {
                case Calendar.TUESDAY:
                    cCalendarStart.add(Calendar.DATE, -1);
                    break;
                case Calendar.WEDNESDAY:
                    cCalendarStart.add(Calendar.DATE, -2);
                    break;
                case Calendar.THURSDAY:
                    cCalendarStart.add(Calendar.DATE, -3);
                    break;
                case Calendar.FRIDAY:
                    cCalendarStart.add(Calendar.DATE, -4);
                    break;
                case Calendar.SATURDAY:
                    cCalendarStart.add(Calendar.DATE, -5);
                    break;
                case Calendar.SUNDAY:
                    cCalendarStart.add(Calendar.DATE, -6);
                    break;
            }
        }

        // create the datetimes for all calendar cells (6 weeks)
        CalendarDay[] arCalendarDays = new CalendarDay[7 * 6];
        for (int i = 0; i < arCalendarDays.length; i++) {
            Calendar cTemp = (Calendar)cCalendarStart.clone();
            cTemp.add(Calendar.DATE, i);
            arCalendarDays[i] = new CalendarDay(cTemp);
        }

        // fill the CalendarDay array with all notes, weights and exercises in this timespan
        setEntriesInCalendarDays(arCalendarDays, getDocument().getNoteList());
        setEntriesInCalendarDays(arCalendarDays, getDocument().getWeightList());
        setEntriesInCalendarDays(arCalendarDays, getView().getDisplayedExercises());

        // redraw calendar
        calendarWidget.setCalendarDays (arCalendarDays, currentMonth, currentYear);
        calendarWidget.repaint ();
    }

    /**
     * This method fills the specified CalendarDay array with all entries in the specified
     * IdDateObjectList. When the date of an entry matches an CalendarDay date then the entry will
     * be stored in this CalendarDay.
     * 
     * @param arCalendarDays the array of CalendarDays
     * @param dateObjectList the list of entries to store
     */
    private void setEntriesInCalendarDays(final CalendarDay[] arCalendarDays,
        final IdDateObjectList<? extends IdDateObject> dateObjectList) {
        Date calendarStart = arCalendarDays[0].getDate().getTime();

        for (IdDateObject dateObject : dateObjectList) {
            // calculate the timespan between exercise day and first calendar day
            long diffMillis = dateObject.getDate().getTime() - calendarStart.getTime();
            if (diffMillis >= 0) {
                int diffDays = (int)(diffMillis / (24 * 60 * 60 * 1000));
                if (diffDays < arCalendarDays.length) {
                    CalendarEntry tempEntry = new CalendarEntry(dateObject);
                    arCalendarDays[diffDays].getCalendarEntries().add(tempEntry);
                }
            }
        }
    }

    /**
     * Event handler for mouse clicks on the calendar widget.
     * 
     * @param me the mouse event
     */
    private void onCalendarClicked(final MouseEvent me) {
        // get CelendarDay on this position => cancel when outside of day cells
        CalendarDay calDay = calendarWidget.getCalendarDayForPoint (me.getPoint ());
        if (calDay == null) {
            return;
        }

        // set the date to be used when the user creates a new entry 
        getController ().setDateForNewEntries (calDay.getDate ().getTime ());
        
        // left mouse button ?
        if (me.getButton() == MouseEvent.BUTTON1) {

            // was it a single click ? => select the calendar entry if there is one
            if (me.getClickCount() == 1) {
                selectCelendarWidgetEntryAtPoint (me.getPoint ());
            }
            // was it a double click ?
            else if (me.getClickCount() == 2) {
                // there was a single click before, so cell selection is not necessary
    
                if (getSelectedExerciseCount() > 0 || getSelectedNoteCount() > 0 || getSelectedWeightCount() > 0) {
                    // there's a selected entry => start edit action for it
                    getController().editEntry();
                } else {
                    // the double click was on no entry
                    // => add a new exercise for the clicked calendar cell date
                    getController().addExercise();
                }
            }

            // remove the date to be used for new entries
            getController ().setDateForNewEntries (null);                        
        }
        // right mouse button ?
        else if (me.getButton() == MouseEvent.BUTTON3) {
            
            // first select the entry at the mouse position (if there is one)
            // and display the popup menu
            selectCelendarWidgetEntryAtPoint (me.getPoint ());            
            popupMenu.show (calendarWidget, me.getX (), me.getY ());
        }
    }

    private void selectCelendarWidgetEntryAtPoint (Point point) {
        calendarWidget.selectCalendarEntryAtPoint(point);
        getView().updateEntryActions();
    }

    /** {@inheritDoc} */
    @Override
    public void print() throws STException {

        // create a new PrinterJob and set the Printable content
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(new Printable() {
            public int print(final Graphics graphics, final PageFormat pageFormat,
                final int pageIndex) {
                if (pageIndex > 0) {
                    return NO_SUCH_PAGE;
                } else {
                    printPageContent((Graphics2D)graphics, pageFormat);
                    return PAGE_EXISTS;
                }
            }
        });

        // show the print dialog (set default oriantation to landscape)
        HashPrintRequestAttributeSet praSet = new HashPrintRequestAttributeSet();
        praSet.add(OrientationRequested.LANDSCAPE);

        if (job.printDialog(praSet)) {
            try {
                job.print(praSet);
            }
            catch (PrinterException e) {
                throw new STException(STExceptionID.GUI_PRINT_VIEW_FAILED,
                    "Failed to print the calendar view ...", e);
            }
        }
    }

    /**
     * Print the page content to the specified graphics device.
     * 
     * @param g2 the Java2D graphics device to print too
     * @param pageFormat the page format
     */
    private void printPageContent(final Graphics2D g2, final PageFormat pageFormat) {
        final int FONT_SIZE = 15;
        final int TITLE_HEIGHT = FONT_SIZE * 3;

        // move and scale the content to fill the page
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY() + TITLE_HEIGHT);
        double scale = calculateScaleFactor(pageFormat, TITLE_HEIGHT);
        g2.scale(scale, scale);

        // draw the calendar first, otherwise it would clean the title
        calendarWidget.paint(g2);

        // finally draw the page title text in the upper left corner
        // (unscale the font and height, the title text must not be scaled)
        g2.setColor(Color.BLACK);
        g2.setFont(new Font(Font.DIALOG, Font.BOLD, (int)Math.round(FONT_SIZE / scale)));
        g2.drawString(getPrintPageTitle(), 0, -(float)(TITLE_HEIGHT / 2 / scale));
    }

    /**
     * Calculates the scale factor, the printed title text and the calendar must fill the page.
     * There is a gap of 10 pixels, just to make sure that everything's on the page.
     * 
     * @param pageFormat format of the page
     * @param titleHeight the height of the title text, the calendar must be below
     * @return the scale factor
     */
    private double calculateScaleFactor(final PageFormat pageFormat, final int titleHeight) {
        double calWidth = calendarWidget.getSize().width + 10;
        double calHeight = calendarWidget.getSize().height + titleHeight + 10;
        double pageWidth = pageFormat.getImageableWidth();
        double pageHeight = pageFormat.getImageableHeight();
        double scaleX = pageWidth / calWidth;
        double scaleY = pageHeight / calHeight;
        return Math.min(scaleX, scaleY);
    }

    private String getPrintPageTitle() {
        StringBuilder sb = new StringBuilder();
        sb.append(getContext().getResReader().getString("st.calview.print.title"));
        sb.append(" - ");
        sb.append(laMonth.getText());
        sb.append(" ");
        sb.append(laYear.getText());
        return sb.toString();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT
     * modify this code. The content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btPreviousMonth = new de.saring.util.gui.RolloverButton();
        laMonth = new javax.swing.JLabel();
        btNextMonth = new de.saring.util.gui.RolloverButton();
        btToday = new de.saring.util.gui.RolloverButton();
        btPreviousYear = new de.saring.util.gui.RolloverButton();
        laYear = new javax.swing.JLabel();
        btNextYear = new de.saring.util.gui.RolloverButton();
        paCalendar = new javax.swing.JPanel();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));

        btPreviousMonth.setText("_PM");

        laMonth.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        laMonth.setText("_Month");
        laMonth.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        btNextMonth.setText("_NM");

        btToday.setText("_TD");

        btPreviousYear.setText("_PY");

        laYear.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        laYear.setText("_Year");

        btNextYear.setText("_NY");

        paCalendar.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(
            javax.swing.GroupLayout.Alignment.LEADING).addGroup(
            layout.createSequentialGroup().addComponent(btPreviousMonth,
                javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(laMonth, javax.swing.GroupLayout.PREFERRED_SIZE, 100,
                    javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(btNextMonth,
                    javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                    javax.swing.GroupLayout.PREFERRED_SIZE).addGap(50, 50, 50).addComponent(
                    btPreviousYear, javax.swing.GroupLayout.PREFERRED_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(laYear, javax.swing.GroupLayout.PREFERRED_SIZE, 50,
                    javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(btNextYear,
                    javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                    javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED, 101, Short.MAX_VALUE)
                .addComponent(btToday, javax.swing.GroupLayout.PREFERRED_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(paCalendar, javax.swing.GroupLayout.DEFAULT_SIZE, 533, Short.MAX_VALUE));
        layout.setVerticalGroup(layout.createParallelGroup(
            javax.swing.GroupLayout.Alignment.LEADING).addGroup(
            layout.createSequentialGroup().addGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER).addComponent(
                    btNextMonth, javax.swing.GroupLayout.PREFERRED_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(laMonth).addComponent(btPreviousMonth,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(btPreviousYear,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(laYear).addComponent(
                        btNextYear, javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(btToday,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
                javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(paCalendar,
                javax.swing.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)));

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {
            laMonth, laYear
        });

    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.saring.util.gui.RolloverButton btNextMonth;
    private de.saring.util.gui.RolloverButton btNextYear;
    private de.saring.util.gui.RolloverButton btPreviousMonth;
    private de.saring.util.gui.RolloverButton btPreviousYear;
    private de.saring.util.gui.RolloverButton btToday;
    private javax.swing.JLabel laMonth;
    private javax.swing.JLabel laYear;
    private javax.swing.JPanel paCalendar;
    // End of variables declaration//GEN-END:variables

    
    /**
     * Extension for the JPopupMenu class which adds calendar-specific behaviour.
     */
    class CalendarPopupMenu extends JPopupMenu {

        /** {@inheritDoc} */
        @Override
        public void setVisible(boolean visible) {
            super.setVisible (visible);
            
            // the date to be used for new added entries must be deleted after
            // the popup menu has been closed and the actions have been processed
            if (!visible) {
                SwingUtilities.invokeLater(new Runnable () {                
                    @Override
                    public void run() {
                        getController ().setDateForNewEntries (null);                                        
                    }
                });
            }
        }
    }    
}
