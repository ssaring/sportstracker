-- SQLite schema update for the SportsTracker database.
-- Schema version 2 changes:
-- - Table NOTE: Added optional reference to SPORT_TYPE for sport type specific notes.
-- - Table NOTE: Added optional reference to EQUIPMENT for equipment specific notes.

DELETE FROM META WHERE 1=1;
INSERT INTO META (SCHEMA_VERSION) VALUES (2);

-- Disable foreign key support temporarily
PRAGMA foreign_keys = OFF;

-- In SQLite it's not possible to add new columns with foreign keys to existing tables.
-- Workaround: create new table with new columns and copy content from old table.
CREATE TABLE NOTE_NEW (
    ID INTEGER PRIMARY KEY NOT NULL,
    DATE_TIME TEXT NOT NULL,
    SPORT_TYPE_ID INTEGER,
    EQUIPMENT_ID INTEGER,
    COMMENT TEXT NOT NULL,
    FOREIGN KEY (SPORT_TYPE_ID) REFERENCES SPORT_TYPE (ID),
    FOREIGN KEY (EQUIPMENT_ID) REFERENCES EQUIPMENT (ID)
);

INSERT INTO NOTE_NEW (ID, DATE_TIME, COMMENT) SELECT ID, DATE_TIME, COMMENT FROM NOTE;
DROP TABLE NOTE;
ALTER TABLE NOTE_NEW RENAME TO NOTE;

PRAGMA foreign_keys = ON;
