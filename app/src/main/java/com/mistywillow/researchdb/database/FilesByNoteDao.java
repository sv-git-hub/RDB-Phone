package com.mistywillow.researchdb.database;

import androidx.room.*;
import com.mistywillow.researchdb.FileTypeConverter;
import com.mistywillow.researchdb.FilesWithNotes;
import com.mistywillow.researchdb.NotesWithFiles;
import com.mistywillow.researchdb.database.entities.*;

import java.util.List;

@Dao
public interface FilesByNoteDao {
    @Insert
    void insert(FilesByNote filesByNote);

    @Delete
    void delete(FilesByNote filesByNote);

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT f.FileID, f.FileName FROM Files AS f \n" +
            "LEFT JOIN Files_By_Note AS fbn ON f.FileID=fbn.FileID \n" +
            "WHERE fbn.NoteID=:noteID")
    List<Files> getFilesByNote(final int noteID);

    @Query("SELECT Notes.NoteID, SourceID, CommentID, QuestionID, QuoteID, TermID, TopicID, Deleted FROM Notes \n" +
            "LEFT JOIN Files_By_Note ON Notes.NoteID=Files_By_Note.NoteID\n" +
            "WHERE Files_By_Note.FileID=:fileID")
    List<Notes> getNotesByFile(final int fileID);

    @Query("SELECT f.FileData FROM Files AS f \n" +
            "LEFT JOIN Files_By_Note AS fbn ON f.FileID=fbn.FileID \n" +
            "WHERE fbn.NoteID=:noteID")
    List<byte[]> getFileData(int noteID);

    @Transaction
    @Query("SELECT * FROM Notes")
    List<NotesWithFiles> getNotesWithFiles();

    @Transaction
    @Query("SELECT * FROM Files")
    List<FilesWithNotes> getFilesWithNotes();
}
