package de.saring.exerciseviewer.parser.testutil;

import de.saring.exerciseviewer.data.RecordingMode;

public class RecordingModeBuilder {

    private RecordingMode recordingMode = new RecordingMode();

    public RecordingMode get() {
        return recordingMode;
    }

    public RecordingModeBuilder isAltitude(boolean isAltitude) {
        recordingMode.setAltitude(isAltitude);
        return this;
    }

}
