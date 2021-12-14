package com.mistywillow.researchdb;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;
import com.mistywillow.researchdb.researchdb.entities.Files;
import com.mistywillow.researchdb.researchdb.entities.FilesByNote;
import com.mistywillow.researchdb.researchdb.entities.Notes;

import java.util.List;

public class FilesWithNotes {
    @Embedded
    public Files files;
    @Relation(
            parentColumn = "FileID",
            entityColumn = "NoteID",
            associateBy = @Junction(FilesByNote.class))

    public List<Notes> notes;
}
