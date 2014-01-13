package de.saring.sportstracker.gui.views.calendarview;

import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.data.Note;
import de.saring.sportstracker.data.Weight;
import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STController;
import de.saring.sportstracker.gui.STDocument;
import de.saring.util.Date310Utils;
import de.saring.util.data.IdDateObject;
import de.saring.util.unitcalc.FormatUtils;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class is a custom component for painting the monthly calendar.
 *
 * @author Stefan Saring
 */
public class CalendarWidget extends JComponent {

    private final STContext context;
    @Inject
    private STDocument document;

    /**
     * Array of all the days displayed in the calendar cells.
     */
    private CalendarDay[] arCalendarDays;

    /**
     * The number of the currently displayed month (January = 1).
     */
    private int displayedMonth;

    /**
     * The currently displayed year.
     */
    private int displayedYear;

    /**
     * This is the current selected calendar entry (null when there's no selection).
     */
    private CalendarEntry selectedCalendarEntry;

    /**
     * This is the height of the top line displaying all weekday names.
     */
    private int weekdayLineHeight;

    /**
     * This is the width of one day cell.
     */
    private double dCellWidth;

    /**
     * This is the height of one day cell.
     */
    private double dCellHeight;

    /**
     * The LocalDate instance of the current day (kept as a member,
     * otherwise it will be created too often).
     */
    private LocalDate today;

    // the calendar colors
    private final Color COLOR_BACKGROUND1;
    private final Color COLOR_BACKGROUND2;
    private final Color COLOR_BACKGROUND_SUM1;
    private final Color COLOR_BACKGROUND_SUM2;
    private final Color COLOR_BACKGROUND_WEEKDAY1;
    private final Color COLOR_BACKGROUND_WEEKDAY2;
    private final Color COLOR_BACKGROUND_WEEKDAY3;
    private final Color COLOR_BACKGROUND_WEEKDAY4;
    private final Color COLOR_BACKGROUND_SELECTED1;
    private final Color COLOR_BACKGROUND_SELECTED2;
    private final Color COLOR_BACKGROUND_SELECTED_BORDER;
    private final Color COLOR_FOREGROUND;
    private final Color COLOR_FOREGROUND_TODAY;
    private final Color COLOR_FOREGROUND_OUTSIDE;
    private final Color COLOR_FOREGROUND_SUNDAY;
    private final Color COLOR_FOREGROUND_SUNDAY_TODAY;
    private final Color COLOR_FOREGROUND_SUNDAY_OUTSIDE;
    private final Color COLOR_FOREGROUND_NOTE;
    private final Color COLOR_FOREGROUND_WEIGHT;

    /**
     * The vertical space above and below the column (weekday) names.
     */
    private static final int COLUMN_NAME_VERTICAL_SPACE = 2;
    /**
     * The space between cell borders and text.
     */
    private static final int CELL_BORDER_SPACE = 4;


    /**
     * Standard c'tor.
     *
     * @param context the SportsTracker context
     * @param controller the SportsTracker controller
     */
    @Inject
    public CalendarWidget(STContext context, STController controller) {
        this.context = context;

        // read calendar colors from properties
        COLOR_BACKGROUND1 = context.getResReader().getColor("st.calendar.background1");
        COLOR_BACKGROUND2 = context.getResReader().getColor("st.calendar.background2");
        COLOR_BACKGROUND_SUM1 = context.getResReader().getColor("st.calendar.background_sum1");
        COLOR_BACKGROUND_SUM2 = context.getResReader().getColor("st.calendar.background_sum2");
        COLOR_BACKGROUND_WEEKDAY1 = context.getResReader().getColor("st.calendar.background_weekday1");
        COLOR_BACKGROUND_WEEKDAY2 = context.getResReader().getColor("st.calendar.background_weekday2");
        COLOR_BACKGROUND_WEEKDAY3 = context.getResReader().getColor("st.calendar.background_weekday3");
        COLOR_BACKGROUND_WEEKDAY4 = context.getResReader().getColor("st.calendar.background_weekday4");
        COLOR_BACKGROUND_SELECTED1 = context.getResReader().getColor("st.calendar.background_selected1");
        COLOR_BACKGROUND_SELECTED2 = context.getResReader().getColor("st.calendar.background_selected2");
        COLOR_BACKGROUND_SELECTED_BORDER = context.getResReader().getColor("st.calendar.background_selected_border");
        COLOR_FOREGROUND = context.getResReader().getColor("st.calendar.foreground");
        COLOR_FOREGROUND_TODAY = context.getResReader().getColor("st.calendar.foreground_today");
        COLOR_FOREGROUND_OUTSIDE = context.getResReader().getColor("st.calendar.foreground_outside");
        COLOR_FOREGROUND_SUNDAY = context.getResReader().getColor("st.calendar.foreground_sunday");
        COLOR_FOREGROUND_SUNDAY_TODAY = context.getResReader().getColor("st.calendar.foreground_sunday_today");
        COLOR_FOREGROUND_SUNDAY_OUTSIDE = context.getResReader().getColor("st.calendar.foreground_sunday_outside");
        COLOR_FOREGROUND_NOTE = context.getResReader().getColor("st.calendar.foreground.note");
        COLOR_FOREGROUND_WEIGHT = context.getResReader().getColor("st.calendar.foreground.weight");

        // enable exercise tooltips (will be location dependent)
        setToolTipText("");

        // setup Drag&Drop functionality
        setTransferHandler(new HrmDndTransferHandler(context, controller, this));
    }

    /**
     * Returns the displayed calendar data .
     *
     * @return array of calendar data
     */
    public CalendarDay[] getCalendarDays() {
        return this.arCalendarDays;
    }

    /**
     * Sets the calendar data to be displayed.
     *
     * @param arCalendarDays array of calendar data for each displayed day
     * @param month the month to be displayed (January = 1)
     * @param year the year to be displayed
     */
    public void setCalendarDays(CalendarDay[] arCalendarDays, int month, int year) {
        // get the new current selected CalendarEntry instance with the same type and ID
        this.selectedCalendarEntry = lookupSelectedCalendarEntry(arCalendarDays);
        this.arCalendarDays = arCalendarDays;
        this.displayedMonth = month;
        this.displayedYear = year;
    }

    /**
     * This method checks the specified array of CalendarDay's and all their
     * entries for an entry of the same type and with the same ID as the currently
     * selected CalendarEntry and returns it.
     *
     * @param calendarDays array of CalendarDays to search in
     * @return the CalendarEntry with the same ID or null when nothing found
     */
    private CalendarEntry lookupSelectedCalendarEntry(CalendarDay[] calendarDays) {
        if (selectedCalendarEntry != null) {
            for (CalendarDay calDay : calendarDays) {
                for (CalendarEntry calEntry : calDay.getCalendarEntries()) {
                    if (calEntry.getEntry().equals(selectedCalendarEntry.getEntry())) {
                        return calEntry;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Returns the selected calendar entry or null when there's no selection.
     *
     * @return the selected calendar entry
     */
    public CalendarEntry getSelectedCalendarEntry() {
        return this.selectedCalendarEntry;
    }

    /**
     * Selects the specified entry if it's available in the current displayed month.
     *
     * @param entry the calendar entry object to select
     */
    public void selectEntry(IdDateObject entry) {
        for (CalendarDay calDay : arCalendarDays) {
            for (CalendarEntry calEntry : calDay.getCalendarEntries()) {
                if (calEntry.getEntry().equals(entry)) {
                    this.selectedCalendarEntry = calEntry;
                    repaint();
                }
            }
        }
    }

    /**
     * Removes the current calendar entry selection.
     */
    public void removeSelection() {
        this.selectedCalendarEntry = null;
        repaint();
    }

    /**
     * Selects the calendar entry in the day cell at the specified point (
     * or remove the selection when there's no entry at this point.
     *
     * @param point the point for selection
     */
    public void selectCalendarEntryAtPoint(Point point) {
        selectedCalendarEntry = getCalendarEntryForPoint(point).orElse(null);
        repaint();
    }

    /**
     * Returns the calendar day cell for the specified point inside the calendar.
     *
     * @param point the point inside the calendar
     * @return the appropriate CalendarDay of this cell or null when not assignable
     */
    CalendarDay getCalendarDayForPoint(Point point) {

        // not possible when on the weekday name line or on the week summary (last column)
        int column = (int) (point.getX() / this.dCellWidth);
        if ((point.getY() <= this.weekdayLineHeight) || (column > 7 - 1)) {
            return null;
        }

        int row = (int) ((point.getY() - this.weekdayLineHeight) / this.dCellHeight);
        column = Math.min(column, 7 - 1);
        row = Math.min(row, 6 - 1);
        int cellIndex = row * 7 + column;

        return cellIndex >= 0 && cellIndex < arCalendarDays.length ?
                arCalendarDays[cellIndex] : null;
    }

    /**
     * Returns the CalendarEntry for the specified point inside the calendar.
     *
     * @param point the point inside the calendar
     * @return the appropriate CalendarEntry when there is one
     */
    Optional<CalendarEntry> getCalendarEntryForPoint(Point point) {

        // get CalendarDay at this point and check all it's entries
        CalendarDay calDay = getCalendarDayForPoint(point);
        if (calDay != null) {
            return calDay.getCalendarEntries().stream()
                    .filter(entry -> entry.getLocationRect() != null && entry.getLocationRect().contains(point))
                    .findFirst();
        }
        return Optional.empty();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawCalendar((Graphics2D) g);
    }

    /**
     * This method draws the whole calendar (6 weeks) for the specified days.
     * Each row contains 7 cells for week days (monday to sunday) and one cell
     * for week summary.
     *
     * @param g the graphics context
     */
    private void drawCalendar(Graphics2D g) {
        today = LocalDate.now();

        // calculate the weekday line height
        weekdayLineHeight = g.getFontMetrics().getHeight() + (2 * COLUMN_NAME_VERTICAL_SPACE);

        // calculate cell dimenstions
        dCellWidth = (getWidth() - 1) / (7d + 1d);
        dCellHeight = (getHeight() - weekdayLineHeight - 1) / 6d;

        drawCalendarBackground(g);
        drawSummaryBackground(g);
        drawColumnNames(g);
        drawCalendarLines(g);

        // do nothing when there's no cell data yet
        if ((arCalendarDays == null) || (arCalendarDays.length != 7 * 6)) {
            return;
        }

        // draw the content of the calendar cells
        // => process all rows and all columns => all cells
        for (int row = 0; row < 6; row++) {
            List<Exercise> exercisesOfWeek = new ArrayList<>();

            for (int column = 0; column < (7 + 1); column++) {
                // calculate index and cell position
                int cellIndex = (row * 7) + column;
                boolean fWeekDay = column < 7;
                int xPos = (int) (column * dCellWidth);
                int nextXPos = (int) ((column + 1) * dCellWidth);
                int yPos = (int) (row * dCellHeight) + weekdayLineHeight;

                // compute clipping rectangles to make sure for not drawing outside the cell
                Rectangle cellClipFull = new Rectangle(xPos, yPos,
                        nextXPos - xPos, (int) dCellHeight);
                Rectangle cellClipWithBorderSpace = new Rectangle(xPos, yPos,
                        (int) dCellWidth - CELL_BORDER_SPACE, (int) dCellHeight - CELL_BORDER_SPACE);
                g.setClip(cellClipWithBorderSpace);

                // draw content of a week day or week summary (last column)
                if (fWeekDay) {
                    drawCalendarDayCell(g, arCalendarDays[cellIndex], exercisesOfWeek,
                            xPos, yPos, cellClipFull, cellClipWithBorderSpace);
                } else {
                    // get week number for a date in the middle of the week (otherwise problems with JSR 310
                    // DateTime API, it sometimes returns week ranges 53, 0, 1 or 53, 2, 3)
                    LocalDate weekMiddleDate = arCalendarDays[row * 7 + 3].getDate();
                    boolean isWeekStartSunday = document.getOptions().isWeekStartSunday();
                    int weekNr = Date310Utils.getWeekNumber(weekMiddleDate, isWeekStartSunday);

                    drawWeekSummaryCell(g, String.valueOf(weekNr), exercisesOfWeek, xPos, yPos);
                }

                // remove clipping rectangle
                g.setClip(null);
            }
        }
    }

    /**
     * Fills complete area with 2-color gradient background and draws the  main
     * calendar rectangle.
     *
     * @param g the graphics context
     */
    private void drawCalendarBackground(Graphics2D g) {
        g.setPaint(new GradientPaint(0, 0, COLOR_BACKGROUND1,
                getWidth() - (float) dCellWidth, 0, COLOR_BACKGROUND2));
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(COLOR_FOREGROUND);
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
    }

    /**
     * Draws a filled rectangle with 2-color gradient for the last column (weekly summary).
     *
     * @param g the graphics context
     */
    private void drawSummaryBackground(Graphics2D g) {
        int xPosWeekSummary = (int) (dCellWidth * 7) + 1;
        g.setPaint(new GradientPaint(xPosWeekSummary, 0, COLOR_BACKGROUND_SUM1,
                xPosWeekSummary + (float) dCellWidth, 0, COLOR_BACKGROUND_SUM2));
        g.fillRect(xPosWeekSummary, 1, getWidth() - xPosWeekSummary - 1, getHeight() - 2);
    }

    /**
     * Draws the column names in the top of the calendar (weekday names and summary).
     *
     * @param g the graphics context
     */
    private void drawColumnNames(Graphics2D g) {
        FontMetrics fontMetrics = g.getFontMetrics();

        String[] arColumnNames = getCalendarColumnNames();
        int indexSunday = getColumnIndexForSunday();

        // draw 2 filled rectangles with 2 gradients (for pseudo-3d effect) by
        // using special colors for the weekday line (the upper gets darker to 
        // the half, the lower start more dark and becomes brighter)
        int weekdayLineHeightHalf = weekdayLineHeight / 2;
        g.setPaint(new GradientPaint(0, 1, COLOR_BACKGROUND_WEEKDAY1,
                0, weekdayLineHeightHalf, COLOR_BACKGROUND_WEEKDAY2));
        g.fillRect(1, 1, getWidth() - 2, weekdayLineHeightHalf);
        g.setPaint(new GradientPaint(0, weekdayLineHeightHalf, COLOR_BACKGROUND_WEEKDAY3,
                0, weekdayLineHeight, COLOR_BACKGROUND_WEEKDAY4));
        g.fillRect(1, weekdayLineHeightHalf + 1, getWidth() - 2, weekdayLineHeightHalf);

        // draw horizontal line under the weekday names  
        g.setColor(COLOR_FOREGROUND);
        g.drawLine(0, weekdayLineHeight, getWidth() - 1, weekdayLineHeight);

        // draw all weekday names (sunday in red color)
        for (int i = 0; i < (7 + 1); i++) {
            if (i == indexSunday) {
                g.setColor(COLOR_FOREGROUND_SUNDAY);
            }

            double textWidth = fontMetrics.getStringBounds(arColumnNames[i], g).getWidth();
            int xPos = (int) ((i * dCellWidth) + (dCellWidth / 2) - (textWidth / 2));
            int yPos = COLUMN_NAME_VERTICAL_SPACE + fontMetrics.getAscent();
            g.drawString(arColumnNames[i], xPos, yPos);
            g.setColor(COLOR_FOREGROUND);
        }
    }

    /**
     * Draws the horizontal and vertical calendar weekday lines.
     *
     * @param g the graphics context
     */
    private void drawCalendarLines(Graphics2D g) {
        // draw vertical lines
        for (int i = 1; i < (7 + 1); i++) {
            int horizontalPos = (int) (i * dCellWidth);
            g.drawLine(horizontalPos, 0, horizontalPos, getHeight() - 1);
        }

        // draw horizontal lines
        for (int i = 1; i < 6; i++) {
            int verticalPos = (int) (i * dCellHeight) + weekdayLineHeight;
            g.drawLine(0, verticalPos, getWidth() - 1, verticalPos);
        }
    }

    /**
     * Draws the day cell with all entries for the specified CalendarDay.
     *
     * @param g the graphics context
     * @param calendarDay the number of the week of the year
     * @param exercisesOfWeek list of Exercise objects in this week (will be filled)
     * @param xPos x position of the upper left cell corner
     * @param yPos y position of the upper left cell corner
     * @param cellClipFull the clipping rectangle for the complete area of the calendar cell
     * @param cellClipWithBorderSpace the clipping rectangle for the calendar cell area including border space (this is currently set)
     */
    private void drawCalendarDayCell(Graphics2D g, CalendarDay calendarDay,
                                     List<Exercise> exercisesOfWeek, int xPos, int yPos,
                                     Rectangle cellClipFull, Rectangle cellClipWithBorderSpace) {

        // draw the day numbers of the cell (right aligned)
        // => use bold font for today
        Font fontDefault = getFont();
        FontMetrics fontMetrics = g.getFontMetrics();
        boolean isSameDay = calendarDay.getDate().equals(today);
        if (isSameDay) {
            g.setFont(fontDefault.deriveFont(fontDefault.getStyle() | Font.BOLD));
            fontMetrics = g.getFontMetrics();
        }
        g.setColor(getWeekDayNumberColor(calendarDay, isSameDay));

        String strDayNr = String.valueOf(calendarDay.getDate().getDayOfMonth());
        double textWidth = fontMetrics.getStringBounds(strDayNr, g).getWidth();
        int dayNrXPos = xPos + (int) (dCellWidth - textWidth) - CELL_BORDER_SPACE;
        int dayNrYPos = yPos + CELL_BORDER_SPACE + fontMetrics.getAscent();
        g.drawString(strDayNr, dayNrXPos, dayNrYPos);
        g.setFont(fontDefault);
        fontMetrics = g.getFontMetrics();

        // calculate vertical position for first exercise text
        yPos += fontMetrics.getHeight() + 6;

        // process and draw all entries of the calendar day cell
        for (CalendarEntry tempEntry : calendarDay.getCalendarEntries()) {
            IdDateObject tempDateObject = tempEntry.getEntry();

            if (tempEntry.isExercise()) {
                exercisesOfWeek.add((Exercise) tempDateObject);
            }

            Rectangle rectLocation = new Rectangle(xPos, yPos - 1,
                    (int) Math.round(dCellWidth), fontMetrics.getHeight() + 2);

            // is this entry the current selected entry?
            // => then draw the selection background
            if (tempEntry == selectedCalendarEntry) {
                g.setClip(cellClipFull);
                drawSelectedEntryBackground(g, rectLocation);
                g.setClip(cellClipWithBorderSpace);
            }

            // draw entry text
            g.setColor(getTextColorForEntry(tempEntry));
            g.drawString(createEntryText(tempEntry),
                    xPos + CELL_BORDER_SPACE, yPos + fontMetrics.getAscent());

            // create and store tooltip text and location for this entry
            tempEntry.setToolTipText(createEntryToolTipText(tempEntry));
            tempEntry.setLocationRect(rectLocation);

            // calculate vertical position for next exercise text
            yPos += fontMetrics.getHeight() + 3;
        }
    }

    /**
     * Draws the week summary cell for the specified exercises of the week.
     *
     * @param g the graphics context
     * @param weekNumber the number of the week of the year
     * @param exercisesOfWeek list of Exercise objects in this week
     * @param xPos x position of the upper left cell corner
     * @param yPos y position of the upper left cell corner
     */
    private void drawWeekSummaryCell(Graphics2D g, String weekNumber,
                                     List<Exercise> exercisesOfWeek, int xPos, int yPos) {

        // draw the week number in the current cell (right aligned)
        g.setColor(COLOR_FOREGROUND);
        FontMetrics fontMetrics = g.getFontMetrics();
        double textWidth = fontMetrics.getStringBounds(weekNumber, g).getWidth();
        int weekNrXPos = xPos + (int) (dCellWidth - textWidth) - CELL_BORDER_SPACE;
        int weekNrYPos = yPos + CELL_BORDER_SPACE + fontMetrics.getAscent();
        g.drawString(weekNumber, weekNrXPos, weekNrYPos);
        yPos += fontMetrics.getHeight() + 6;

        // draw content of week summary (last column) when there are exercises
        if (exercisesOfWeek.size() > 0) {
            // calculate summary distance and duration for all exercises of week
            float weekDistance = 0;
            int weekDuration = 0;

            for (Exercise exercise : exercisesOfWeek) {
                weekDistance += exercise.getDistance();
                weekDuration += exercise.getDuration();
            }

            // create distance and duration strings
            FormatUtils formatUtils = context.getFormatUtils();
            String strDistance = formatUtils.distanceToString(weekDistance, 2);
            String strDuration = formatUtils.seconds2TimeString(weekDuration);

            // draw distance and duration strings
            g.drawString(strDistance,
                    xPos + CELL_BORDER_SPACE, yPos + fontMetrics.getAscent());
            yPos += fontMetrics.getHeight() + 3;
            g.drawString(strDuration,
                    xPos + CELL_BORDER_SPACE, yPos + fontMetrics.getAscent());
        }
    }

    /**
     * Returns the foreground color for the week day number.
     *
     * @param calendarDay the CalendarDay cell to be drawn
     * @param isToday flag is true when the specified calendar day is today
     * @return the day number color
     */
    private Color getWeekDayNumberColor(CalendarDay calendarDay, boolean isToday) {

        boolean isSunday = calendarDay.getDate().getDayOfWeek() == DayOfWeek.SUNDAY;
        boolean isInsideCurrentMonth =
                calendarDay.getDate().getMonthValue() == displayedMonth &&
                        calendarDay.getDate().getYear() == displayedYear;

        // use black color, but grey for days outside of month and red for sundays,
        // but highlight today with different colors
        if (!isSunday) {
            if (isToday) {
                return COLOR_FOREGROUND_TODAY;
            } else if (isInsideCurrentMonth) {
                return COLOR_FOREGROUND;
            } else {
                return COLOR_FOREGROUND_OUTSIDE;
            }
        } else {
            if (isToday) {
                return COLOR_FOREGROUND_SUNDAY_TODAY;
            } else if (isInsideCurrentMonth) {
                return COLOR_FOREGROUND_SUNDAY;
            } else {
                return COLOR_FOREGROUND_SUNDAY_OUTSIDE;
            }
        }
    }

    /**
     * Draws the background for the selected entry.
     *
     * @param g the graphics context
     * @param rectLocation the location of the selected entry
     */
    private void drawSelectedEntryBackground(Graphics2D g, Rectangle rectLocation) {
        // fill rectangle with 2-color gradient 
        g.setPaint(new GradientPaint(
                rectLocation.x, rectLocation.y, COLOR_BACKGROUND_SELECTED1,
                rectLocation.x + rectLocation.width, rectLocation.y, COLOR_BACKGROUND_SELECTED2));
        g.fillRect(rectLocation.x + 1, rectLocation.y,
                rectLocation.width, rectLocation.height);

        // draw a border rectangle (one color) around the filled one
        g.setPaint(COLOR_BACKGROUND_SELECTED_BORDER);
        g.drawRect(rectLocation.x + 1, rectLocation.y - 1,
                rectLocation.width, rectLocation.height + 1);
    }

    /**
     * Creates an array of the columns names in the calendar. The first seven columns
     * are weeknames, the last is the week summary.
     *
     * @return array of column names (size is 8)
     */
    private String[] getCalendarColumnNames() {
        String[] arColumnNames = new String[8];

        if (document.getOptions().isWeekStartSunday()) {
            arColumnNames[0] = context.getResReader().getString("st.valview.weekdays.sunday");
            arColumnNames[1] = context.getResReader().getString("st.valview.weekdays.monday");
            arColumnNames[2] = context.getResReader().getString("st.valview.weekdays.tuesday");
            arColumnNames[3] = context.getResReader().getString("st.valview.weekdays.wednesday");
            arColumnNames[4] = context.getResReader().getString("st.valview.weekdays.thursday");
            arColumnNames[5] = context.getResReader().getString("st.valview.weekdays.friday");
            arColumnNames[6] = context.getResReader().getString("st.valview.weekdays.saturday");
        } else {
            arColumnNames[0] = context.getResReader().getString("st.valview.weekdays.monday");
            arColumnNames[1] = context.getResReader().getString("st.valview.weekdays.tuesday");
            arColumnNames[2] = context.getResReader().getString("st.valview.weekdays.wednesday");
            arColumnNames[3] = context.getResReader().getString("st.valview.weekdays.thursday");
            arColumnNames[4] = context.getResReader().getString("st.valview.weekdays.friday");
            arColumnNames[5] = context.getResReader().getString("st.valview.weekdays.saturday");
            arColumnNames[6] = context.getResReader().getString("st.valview.weekdays.sunday");
        }
        arColumnNames[7] = context.getResReader().getString("st.valview.week_sum");
        return arColumnNames;
    }

    private int getColumnIndexForSunday() {
        return document.getOptions().isWeekStartSunday() ? 0 : 6;
    }

    /**
     * Creates the entry text to be shown in the calendar cell. For Notes it's
     * the text, for Weight's it's the weight value and for Exercises it's
     * the first sport type char + distance (if recorded) + duration.
     *
     * @param calendarEntry the displayed exercise
     * @return the created text
     */
    private String createEntryText(CalendarEntry calendarEntry) {
        FormatUtils formatUtils = context.getFormatUtils();
        StringBuilder sb = new StringBuilder();

        if (calendarEntry.isNote()) {
            Note note = (Note) calendarEntry.getEntry();
            sb.append(context.getResReader().getString("st.calview.note_short"));
            sb.append(" ");
            sb.append(note.getText());
        } else if (calendarEntry.isWeight()) {
            Weight weight = (Weight) calendarEntry.getEntry();
            sb.append(context.getResReader().getString("st.calview.weight_short"));
            sb.append(" ");
            sb.append(formatUtils.weightToString(weight.getValue(), 2));
        } else if (calendarEntry.isExercise()) {
            Exercise exercise = (Exercise) calendarEntry.getEntry();
            sb.append(exercise.getSportType().getName().charAt(0));
            sb.append(": ");
            if (exercise.getSportType().isRecordDistance()) {
                sb.append(formatUtils.distanceToString(exercise.getDistance(), 2));
                sb.append(", ");
            }
            sb.append(formatUtils.seconds2TimeString(exercise.getDuration()));
        }
        return sb.toString();
    }

    /**
     * Creates the tooltip text to be shown for the specified calendar cell
     * entry (uses HTML format).
     *
     * @param calendarEntry calendar entry
     * @return the created tool tip text
     */
    private String createEntryToolTipText(CalendarEntry calendarEntry) {
        FormatUtils formatUtils = context.getFormatUtils();
        StringBuilder sb = new StringBuilder("<html>");

        if (calendarEntry.isNote()) {
            Note note = (Note) calendarEntry.getEntry();
            sb.append(formatUtils.firstLineOfText(note.getText()));
        } else if (calendarEntry.isWeight()) {
            Weight weight = (Weight) calendarEntry.getEntry();
            sb.append(context.getResReader().getString("st.calview.weight_tooltip.weight"));
            sb.append(" ");
            sb.append(formatUtils.weightToString(weight.getValue(), 2));
        } else if (calendarEntry.isExercise()) {

            Exercise exercise = (Exercise) calendarEntry.getEntry();
            sb.append(context.getResReader().getString("st.calview.exe_tooltip.sport_type"));
            sb.append(" ");
            sb.append(exercise.getSportType().getName());
            sb.append(" (");
            sb.append(exercise.getSportSubType().getName());
            sb.append(")<br/>");

            if (exercise.getSportType().isRecordDistance()) {
                sb.append(context.getResReader().getString("st.calview.exe_tooltip.distance"));
                sb.append(" ");
                sb.append(formatUtils.distanceToString(exercise.getDistance(), 2));
                sb.append("<br/>");
                sb.append(context.getResReader().getString("st.calview.exe_tooltip.avg_speed"));
                sb.append(" ");
                sb.append(formatUtils.speedToString(exercise.getAvgSpeed(), 2));
                sb.append("<br/>");
            }

            sb.append(context.getResReader().getString("st.calview.exe_tooltip.duration"));
            sb.append(" ");
            sb.append(formatUtils.seconds2TimeString(exercise.getDuration()));
        }
        sb.append("</html>");
        return sb.toString();
    }

    /**
     * Returns the text color to be used for the specified CalendarEntry.
     *
     * @param calendarEntry the CalendarEntry
     * @return the text color
     */
    private Color getTextColorForEntry(CalendarEntry calendarEntry) {
        if (calendarEntry.isExercise()) {
            return ((Exercise) calendarEntry.getEntry()).getSportType().getColor();
        } else if (calendarEntry.isNote()) {
            return COLOR_FOREGROUND_NOTE;
        } else if (calendarEntry.isWeight()) {
            return COLOR_FOREGROUND_WEIGHT;
        } else {
            throw new IllegalArgumentException("Invalid CalendarEntry type!");
        }
    }

    @Override
    public String getToolTipText(MouseEvent event) {
        // return the tooltip of the CalendarEntry at the mouse position (if available)
        Optional<CalendarEntry> oCalEntry = getCalendarEntryForPoint(event.getPoint());
        return oCalEntry.isPresent() ? oCalEntry.get().getToolTipText() : null;
    }
}