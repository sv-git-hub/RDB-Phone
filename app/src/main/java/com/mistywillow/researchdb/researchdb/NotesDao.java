package com.mistywillow.researchdb.researchdb;

import androidx.room.*;
import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteQuery;
import com.mistywillow.researchdb.NoteDetails;
import com.mistywillow.researchdb.researchdb.entities.Notes;
import com.mistywillow.researchdb.SourcesTable;

import javax.xml.transform.Source;
import java.util.List;

@Dao
public interface NotesDao {

    @Insert
    long addNote(Notes note);

    @Update
    void updateNote(Notes note);

    @Delete
    void deleteNote(Notes notes);

    @Query("SELECT * FROM NOTES WHERE NoteID = :noteID")
    Notes getNote(int noteID);

    @Query("SELECT * FROM Notes")
    List<Notes> getAllNotes();

    @Query("SELECT n.NoteID, s.SourceID, s.SourceType, s.Title, c.Summary FROM Comment as c " +
            "LEFT JOIN Notes as n ON n.CommentID = c.CommentID " +
            "LEFT JOIN Source as s ON n.SourceID = s.SourceID " +
            "LEFT JOIN Topic as t ON n.TopicID = t.TopicID WHERE t.Topic = :topic AND n.Deleted = 0")
    List<SourcesTable> getNotesOnTopic(String topic);

    // Search on Question
    @Query("SELECT n.NoteID, s.SourceID, s.SourceType, s.Title, c.Summary FROM Comment as c " +
            "LEFT JOIN Notes as n ON n.CommentID = c.CommentID " +
            "LEFT JOIN Source as s ON n.SourceID = s.SourceID " +
            "LEFT JOIN Question as q ON n.QuestionID = q.QuestionID WHERE q.Question = :question AND n.Deleted = 0")
    List<SourcesTable> getNotesOnQuestion(String question);

    // Custom Search
    @Query("SELECT n.NoteID, s.SourceID, s.SourceType, s.Title, c.Summary FROM Comment as c " +
            "LEFT JOIN Notes as n ON n.CommentID = c.CommentID " +
            "LEFT JOIN Source as s ON n.SourceID = s.SourceID " +
            "LEFT JOIN Question as q ON n.QuestionID = q.QuestionID WHERE n.NoteID IN(:noteIDs) AND n.Deleted = 0")
    List<SourcesTable> getAllNotesOnNoteIDs(List<Integer> noteIDs);

    // Mark Note for Deletion
    @Query("UPDATE Notes SET Deleted = 1 WHERE Notes.NoteID = :noteID")
    void markNoteToDelete(int noteID);

    // UnMark note for Deletion
    @Query("UPDATE Notes SET Deleted = 0 WHERE Notes.NoteID = :noteID")
    void unMarkNoteToDelete(int noteID);

    // Retrieve Note Marked for Deletion
    @Query("SELECT n.NoteID, s.SourceID, s.SourceType, s.Title, c.Summary FROM Comment as c " +
            "LEFT JOIN Notes as n ON n.CommentID = c.CommentID " +
            "LEFT JOIN Source as s ON n.SourceID = s.SourceID " +
            "WHERE n.Deleted = 1")
    List<SourcesTable>getAllNotesMarkedToDelete();

    @RawQuery
    List<Integer> getNotesOnCustomSearch(SimpleSQLiteQuery query);

    @Query("SELECT qn.Question, qt.Quote, te.Term, s.Year, s.Month, s.Day, s.Volume, s.Edition, s.Issue, tp.Topic, c.Hyperlink, c.Comment, c.Page, c.TimeStamp, c.Summary FROM Comment as c " +
            "LEFT JOIN Notes as n ON n.CommentID = c.CommentID " +
            "LEFT JOIN Source as s ON n.SourceID = s.SourceID " +
            "LEFT JOIN Question as qn ON n.QuestionID = qn.QuestionID " +
            "LEFT JOIN Quote as qt ON n.QuoteID = qt.QuoteID " +
            "LEFT JOIN Term as te ON n.TermID = te.TermID " +
            "LEFT JOIN Topic as tp ON n.TopicID = tp.TopicID " +
            "WHERE n.NoteID = :noteID")
    NoteDetails getNoteDetails(int noteID);

    @Query("SELECT last_insert_rowid()")
    int lastNotePKID();

    @Query("SELECT COUNT(*) FROM Notes WHERE SourceID = :id")
    int countSourcesByID(int id);

    @Query("SELECT COUNT(*) FROM Notes WHERE QuestionID = :id")
    int countQuestionByID(int id);

    @Query("SELECT COUNT(*) FROM Notes WHERE QuoteID = :id")
    int countQuoteByID(int id);

    @Query("SELECT COUNT(*) FROM Notes WHERE TermID = :id")
    int countTermByID(int id);

    @Query("SELECT COUNT(*) FROM Notes WHERE TopicID = :id")
    int countTopicByID(int id);

    @RawQuery
    long savePoint(SupportSQLiteQuery query);

    @RawQuery
    long releaseSavePoint(SupportSQLiteQuery query);
}
