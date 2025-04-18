package de.saring.sportstracker.data;

import java.util.regex.PatternSyntaxException;

/**
 * This class contains the list of all note entries of the the user and provides
 * access methods to them. It doesn't contain any special functionality yet, but
 * maybe in the future.
 *
 * @author Stefan Saring
 */
public final class NoteList extends EntryList<Note> {

    /**
     * This method checks whether the specified note entry matches the specified entry filter criteria.
     * It extends the default filter (date time and comment) by sport type and equipment criteria.
     *
     * @param note note to check
     * @param filter the entry filter criteria
     * @return true if the note matches the filter criteria
     * @throws PatternSyntaxException thrown on parsing problems of the regular expression for comment searching
     */
    @Override
    protected boolean filterEntry(Note note, EntryFilter filter) {

        // entry datetime and comment are filtered by the base class
        if (!super.filterEntry(note, filter)) {
            return false;
        }

        // if a sport type filter is specified => make sure that note has the same sport type (is optional)
        if (filter.getSportType() != null && !filter.getSportType().equals(note.getSportType())) {
            return false;
        }

        // if an equipment filter is specified => make sure that note has the same equipment (is optional)
        if (filter.getEquipment() != null && !filter.getEquipment().equals(note.getEquipment())) {
            return false;
        }

        // all filter criteria are fulfilled
        return true;
    }

}
