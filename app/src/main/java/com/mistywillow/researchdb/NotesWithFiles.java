package com.mistywillow.researchdb;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;
import com.mistywillow.researchdb.researchdb.entities.Files;
import com.mistywillow.researchdb.researchdb.entities.FilesByNote;
import com.mistywillow.researchdb.researchdb.entities.Notes;

import java.util.List;

public class NotesWithFiles {
    @Embedded public Notes notes;
    @Relation(
            parentColumn = "NoteID",
            entityColumn = "FileID",
            associateBy = @Junction(FilesByNote.class))

    public List<Files> files;
}
