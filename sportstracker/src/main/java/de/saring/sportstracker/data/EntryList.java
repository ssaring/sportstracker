package de.saring.sportstracker.data;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import de.saring.util.StringUtils;
import de.saring.util.data.IdDateObjectList;

/**
 * This list extends IdDateObjectList and contains unique instances of Entry subclasses. It provides common
 * filtering for entries of all types.
 *
 * @param <T> the object type to store in this list, must be a subclass of Entry
 * @author Stefan Saring
 */
public class EntryList<T extends Entry> extends IdDateObjectList<T> {

    /**
     * This method searches through the whole entry list and returns an list of all notes which are fulfilling
     * all the specified filter criteria. The comment filter is optional. The filtering by a comment substring
     * is only case sensitive in regular expression mode.<br/>
     * The filter will be ignored when it is for another entry type than the entries stored in this list.
     *
     * @param filter the entry filter criteria
     * @return List of Entry objects which are valid for the specified filters
     * @throws PatternSyntaxException thrown on parsing problems of the regular expression for comment searching
     */
    public EntryList<T> getEntriesForFilter(EntryFilter filter) throws PatternSyntaxException {

        if (size() == 0) {
            return this;
        }

        // check the list type and filter type by using the first list entry
        T firstEntry = getAt(0);
        Class<? extends Entry> filterTypeClass = filter.getEntryType().getEntryClass();
        if (!filterTypeClass.isInstance(firstEntry)) {
            return this;
        }

        final EntryList<T> foundEntries = new EntryList<>();
        stream().filter(note -> filterEntry(note, filter))
                .forEach(foundEntries::set);
        return foundEntries;
    }

    /**
     * Checks whether the specified entry matches the comment of the filter. It filters the entry date
     * and the entry comment (if present).
     *
     * @param entry entry to check
     * @param filter entry filter
     * @return true if the filter matches
     */
    protected boolean filterEntry(T entry, EntryFilter filter) {

        // make sure that the entry is in the specified time period
        LocalDate entryDate = entry.getDateTime().toLocalDate();
        if (filter.getDateStart().isAfter(entryDate) || filter.getDateEnd().isBefore(entryDate)) {
            return false;
        }

        // do we need to search in comments ?
        if (!StringUtils.isNullOrEmpty(filter.getCommentSubString())) {
            if (!filterEntryByComment(entry, filter)) {
                return false;
            }
        }

        // all filter criteria are fulfilled
        return true;
    }

    private boolean filterEntryByComment(Entry entry, EntryFilter filter) {

        // ignore this entry when no comment present
        if (StringUtils.isNullOrEmpty(entry.getComment())) {
            return false;
        }

        String strCommentSubString = filter.getCommentSubString().trim();

        if (!filter.isRegularExpressionMode()) {
            // normal searching for substring (is not case sensitive !)
            strCommentSubString = strCommentSubString.toLowerCase();
            String strEntryComment = entry.getComment().toLowerCase();

            // normal search can contain multiple words separated by any whitespace character
            // => each of these words needs to be contained in the entry comment, the order does not matter (AND logic)
            String[] filterWords = strCommentSubString.split("\\s+");
            for (var filterWord : filterWords) {
                if (!strEntryComment.contains(filterWord)) {
                    return false;
                }
            }
        }
        else {
            // regular expression searching for substring (is case sensitive !)
            Pattern ptnCommentSubString = Pattern.compile(strCommentSubString);
            Matcher matcher = ptnCommentSubString.matcher(entry.getComment());
            if (!matcher.find()) {
                return false;
            }
        }

        return true;
    }
}
