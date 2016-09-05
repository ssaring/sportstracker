package de.saring.sportstracker.gui.views.calendarview;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.data.Note;
import de.saring.sportstracker.data.Weight;
import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STDocument;
import de.saring.util.AppResources;
import de.saring.util.StringUtils;
import de.saring.util.gui.javafx.control.calendar.CalendarEntry;
import de.saring.util.gui.javafx.control.calendar.CalendarDataProvider;
import de.saring.util.unitcalc.FormatUtils;

/**
 * Provides the note, weight and exercise entries to be shown in the calendar.
 *
 * @author Stefan Saring
 */
public class CalendarDataProviderImpl implements CalendarDataProvider {

    private final STContext context;
    private final STDocument document;

    /**
     * Standard c'tor for dependency injection.
     *
     * @param context the SportsTracker UI context
     * @param document the SportsTracker document / model
     */
    public CalendarDataProviderImpl(final STContext context, final STDocument document) {
        this.context = context;
        this.document = document;
    }

    /**
     * Provides the note, weight and exercise entries to be shown in the calendar for the specified date.
     *
     * @param date date
     * @return list of calendar entries
     */
    @Override
    public List<CalendarEntry> getCalendarEntriesForDate(final LocalDate date) {

        final List<Note> notes = document.getNoteList().getEntriesInDateRange(date, date);
        final List<CalendarEntry> calendarEntries = notes.stream() //
                .map(note -> createCalendarEntryForNote(note)) //
                .collect(Collectors.toList());

        final List<Weight> weights = document.getWeightList().getEntriesInDateRange(date, date);
        calendarEntries.addAll(weights.stream() //
                .map(weight -> createCalendarEntryForWeight(weight)) //
                .collect(Collectors.toList()));

        final List<Exercise> exercises = document.getFilterableExerciseList().getEntriesInDateRange(date, date);
        calendarEntries.addAll(exercises.stream() //
                .map(exercise -> createCalendarEntryForExercise(exercise)) //
                .collect(Collectors.toList()));

        return calendarEntries;
    }

    @Override
    public List<String> getSummaryForDateRange(final LocalDate dateStart, final LocalDate dateEnd) {
        final List<String> summaryLines = new ArrayList<>();

        // calculate summary distance and duration for all exercises in range
        final List<Exercise> exersisesInRange = document.getFilterableExerciseList().getEntriesInDateRange( //
                dateStart, dateEnd);

        if (exersisesInRange.size() > 0) {
            float summaryDistance = 0;
            int summaryDuration = 0;

            for (Exercise exercise : exersisesInRange) {
                summaryDistance += exercise.getDistance();
                summaryDuration += exercise.getDuration();
            }

            // add formatted distance and duration strings
            final FormatUtils formatUtils = context.getFormatUtils();
            summaryLines.add(formatUtils.distanceToString(summaryDistance, 2));
            summaryLines.add(formatUtils.seconds2TimeString(summaryDuration));
        }

        return summaryLines;
    }

    private CalendarEntry createCalendarEntryForNote(final Note note) {
        final StringBuilder sbText = new StringBuilder();
        sbText.append(context.getResources().getString("st.calview.note_short")) //
                .append(" ") //
                .append(StringUtils.getFirstLineOfText(note.getComment()));

        return new CalendarEntry(note, sbText.toString(), note.getComment(), null);
    }

    private CalendarEntry createCalendarEntryForWeight(final Weight weight) {
        final String strWeightValue = context.getFormatUtils().weightToString(weight.getValue(), 2);

        final StringBuilder sbText = new StringBuilder();
        sbText.append(context.getResources().getString("st.calview.weight_short")) //
                .append(" ") //
                .append(strWeightValue);

        final StringBuilder sbToolTip = new StringBuilder();
        sbToolTip.append(context.getResources().getString("st.calview.weight_tooltip.weight")) //
                .append(" ") //
                .append(strWeightValue);

        return new CalendarEntry(weight, sbText.toString(), sbToolTip.toString(), null);
    }

    private CalendarEntry createCalendarEntryForExercise(final Exercise exercise) {
        final AppResources resources = context.getResources();
        final FormatUtils formatUtils = context.getFormatUtils();

        final StringBuilder sbText = new StringBuilder();
        sbText.append(exercise.getSportType().getName().charAt(0)).append(": ");
        if (exercise.getSportType().isRecordDistance()) {
            sbText.append(formatUtils.distanceToString(exercise.getDistance(), 2)).append(", ");
        }
        sbText.append(formatUtils.minutes2TimeString(exercise.getDuration() / 60));

        final StringBuilder sbToolTip = new StringBuilder();
        sbToolTip.append(resources.getString("st.calview.exe_tooltip.sport_type")) //
                .append(" ").append(exercise.getSportType().getName()) //
                .append(" (").append(exercise.getSportSubType().getName()).append(")\n");

        if (exercise.getSportType().isRecordDistance()) {
            sbToolTip.append(resources.getString("st.calview.exe_tooltip.distance")) //
                    .append(" ").append(formatUtils.distanceToString(exercise.getDistance(), 2)).append("\n");

            sbToolTip.append(resources.getString("st.calview.exe_tooltip.avg_speed")) //
                    .append(" ").append(formatUtils.speedToString(exercise.getAvgSpeed(), 2)).append("\n");
        }

        sbToolTip.append(resources.getString("st.calview.exe_tooltip.duration")) //
                .append(" ").append(formatUtils.seconds2TimeString(exercise.getDuration()));

        return new CalendarEntry(exercise, sbText.toString(), sbToolTip.toString(), exercise.getSportType().getColor());
    }
}
