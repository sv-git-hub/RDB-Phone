package com.mistywillow.researchdb.database;

import androidx.room.*;
import androidx.sqlite.db.SimpleSQLiteQuery;
import com.mistywillow.researchdb.NoteDetails;
import com.mistywillow.researchdb.database.entities.Files;
import com.mistywillow.researchdb.database.entities.Notes;
import com.mistywillow.researchdb.SourcesTable;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface NotesDao {

    @Insert
    void addNote(Notes note);

    @Update
    void updateNote(Notes note);

    @Query("SELECT * FROM NOTES WHERE NoteID = :noteID")
    Notes getNote(int noteID);

    @Query("SELECT * FROM Notes")
    List<Notes> getAllNotes();

    @Query("SELECT n.NoteID, s.SourceID, s.SourceType, s.Title, c.Summary FROM Comments as c " +
            "LEFT JOIN Notes as n ON n.CommentID = c.CommentID " +
            "LEFT JOIN Sources as s ON n.SourceID = s.SourceID " +
            "LEFT JOIN Topics as t ON n.TopicID = t.TopicID WHERE t.Topic = :topic AND n.Deleted = 0")
    List<SourcesTable> getNotesOnTopic(String topic);

    // Search on Question
    @Query("SELECT n.NoteID, s.SourceID, s.SourceType, s.Title, c.Summary FROM Comments as c " +
            "LEFT JOIN Notes as n ON n.CommentID = c.CommentID " +
            "LEFT JOIN Sources as s ON n.SourceID = s.SourceID " +
            "LEFT JOIN Questions as q ON n.QuestionID = q.QuestionID WHERE q.Question = :question AND n.Deleted = 0")
    List<SourcesTable> getNotesOnQuestion(String question);

    // Custom Search
    @Query("SELECT n.NoteID, s.SourceID, s.SourceType, s.Title, c.Summary FROM Comments as c " +
            "LEFT JOIN Notes as n ON n.CommentID = c.CommentID " +
            "LEFT JOIN Sources as s ON n.SourceID = s.SourceID " +
            "LEFT JOIN Questions as q ON n.QuestionID = q.QuestionID WHERE n.NoteID IN(:noteIDs) AND n.Deleted = 0")
    List<SourcesTable> getAllNotesOnNoteIDs(List<Integer> noteIDs);

    @RawQuery
    List<Integer> getNotesOnCustomSearch(SimpleSQLiteQuery query);

    @Query("SELECT qn.Question, qt.Quote, te.Term, s.Year, s.Month, s.Day, s.Volume, s.Edition, s.Issue, tp.Topic, c.Hyperlink, c.Comment, c.Page, c.TimeStamp, c.Summary FROM Comments as c " +
            "LEFT JOIN Notes as n ON n.CommentID = c.CommentID " +
            "LEFT JOIN Sources as s ON n.SourceID = s.SourceID " +
            "LEFT JOIN Questions as qn ON n.QuestionID = qn.QuestionID " +
            "LEFT JOIN Quotes as qt ON n.QuoteID = qt.QuoteID " +
            "LEFT JOIN Terms as te ON n.TermID = te.TermID " +
            "LEFT JOIN Topics as tp ON n.TopicID = tp.TopicID " +
            "WHERE n.NoteID = :noteID")
    NoteDetails getNoteDetails(int noteID);

    @Query("SELECT COUNT(*) FROM Notes WHERE QuestionID = :id")
    int countQuestionByID(int id);

    @Query("SELECT COUNT(*) FROM Notes WHERE QuestionID = :id")
    int countTopicByID(int id);
}
