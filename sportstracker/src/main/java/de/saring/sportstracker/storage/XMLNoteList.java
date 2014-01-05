package de.saring.sportstracker.storage;

import de.saring.sportstracker.core.STException;
import de.saring.sportstracker.core.STExceptionID;
import de.saring.sportstracker.data.Note;
import de.saring.sportstracker.data.NoteList;
import org.jdom2.Document;
import org.jdom2.Element;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * This class is for reading or writing a NoteList object from or to a XML file.
 *
 * @author Stefan Saring
 * @version 1.0
 */
public class XMLNoteList {
    /**
     * The XSD filename with the structure of the note list.
     */
    private static final String XSD_NOTES = "notes.xsd";

    /**
     * The date and time parser instance.
     */
    private static final SimpleDateFormat SD_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * Reads the note list from the specified XML file.
     * Returns an empty list when the file doesn't exists yet.
     *
     * @param source name of the XML file to read from
     * @return the created NoteList
     * @throws STException thrown on read problems
     */
    public NoteList readNoteList(String source) throws STException {

        try {
            // return an empty list if the file doesn't exists yet
            NoteList noteList = new NoteList();
            File fSource = new File(source);
            if (!fSource.exists()) {
                return noteList;
            }

            // create JDOM Document from XML with XSD validation
            Document document = XMLUtils.getJDOMDocument(fSource, XSD_NOTES);

            // get root element and read all the contained notes
            Element eNoteList = document.getRootElement();
            eNoteList.getChildren("note").forEach(eNote ->
                    noteList.set(readNote(eNote)));

            return noteList;
        } catch (Exception e) {
            throw new STException(STExceptionID.XMLSTORAGE_READ_NOTE_LIST,
                    "Failed to read note list from XML file '" + source + "' ...", e);
        }
    }

    /**
     * Reads the data from the specified note element and returns the created
     * Note object.
     *
     * @param eNote note JDOM element
     * @return the created Note object
     */
    private Note readNote(Element eNote) {
        Note note = new Note(Integer.parseInt(eNote.getChildText("id")));
        note.setText(eNote.getChildText("text"));

        // get and convert date (format allready checked by XSD schema)
        String strDate = eNote.getChildText("date");
        try {
            note.setDate(SD_FORMAT.parse(strDate));
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse note with ID '" + note.getId() +
                    "', the date format '" + strDate + "' is not valid!");
        }
        return note;
    }

    /**
     * Writes the NoteList to the specified XML file.
     *
     * @param noteList the NoteList to store
     * @param destination name of xml file to write to
     * @throws STException thrown on store problems
     */
    public void storeNoteList(NoteList noteList, String destination) throws STException {

        // create JDOM element with all notes
        Element eNoteList = createNoteListElement(noteList);

        // write the element to XML file
        try {
            XMLUtils.writeXMLFile(eNoteList, destination);
        } catch (IOException e) {
            throw new STException(STExceptionID.XMLSTORAGE_STORE_NOTE_LIST,
                    "Failed to write note list to XML file '" + destination + "' ...", e);
        }
    }

    /**
     * Creates the "note-list" element with all notes for the specified
     * note list.
     *
     * @param noteList the NoteList to store
     * @return the created Element
     */
    private Element createNoteListElement(NoteList noteList) {
        Element eNoteList = new Element("note-list");

        noteList.forEach(note -> {
            Element eNote = new Element("note");
            eNoteList.addContent(eNote);

            XMLUtils.addElement(eNote, "id", String.valueOf(note.getId()));
            XMLUtils.addElement(eNote, "date", SD_FORMAT.format(note.getDate()));
            XMLUtils.addElement(eNote, "text", note.getText());
        });
        return eNoteList;
    }
}