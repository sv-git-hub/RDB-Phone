package com.mistywillow.researchdb.researchdb.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(tableName = "File_By_Note", primaryKeys = {"NoteID", "FileID"},foreignKeys = {
        @ForeignKey(entity = Notes.class, parentColumns = "NoteID", childColumns = "NoteID"),
        @ForeignKey(entity = Files.class, parentColumns = "FileID", childColumns = "FileID")},
        indices = {@Index("NoteID"), @Index("FileID")})
public class FilesByNote {

    @ColumnInfo(name = "NoteID")
    private int noteID;
    @ColumnInfo(name = "FileID")
    private int fileID;

    public FilesByNote(int noteID, int fileID){
        this.noteID = noteID;
        this.fileID = fileID;
    }

    public int getNoteID() {
        return noteID;
    }

    public void setNoteID(int noteID) {
        this.noteID = noteID;
    }

    public int getFileID() {
        return fileID;
    }

    public void setFileID(int fileID) {
        this.fileID = fileID;
    }
}
