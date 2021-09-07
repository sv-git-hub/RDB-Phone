package com.mistywillow.researchdb.database.entities;

import androidx.annotation.NonNull;
import androidx.room.*;

@Entity(tableName = "Notes", foreignKeys = {
        @ForeignKey(entity = Sources.class, parentColumns = "SourceID", childColumns = "SourceID"),
        @ForeignKey(entity = Comments.class, parentColumns = "CommentID", childColumns = "CommentID"),
        @ForeignKey(entity = Topics.class, parentColumns = "TopicID", childColumns = "TopicID")},
        indices = {@Index("SourceID"), @Index("CommentID"), @Index("QuestionID"), @Index("QuoteID"),
                @Index("TermID"), @Index("TopicID")})
public class Notes {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "NoteID")
    @NonNull
    private Integer noteID;
    @ColumnInfo(name = "SourceID")
    @NonNull
    private Integer sourceID;
    @ColumnInfo(name = "CommentID")
    @NonNull
    private Integer commentID;
    @ColumnInfo(name = "QuestionID")
    private Integer questionID;
    @ColumnInfo(name = "QuoteID")
    private Integer quoteID;
    @ColumnInfo(name = "TermID")
    private Integer termID;
    @ColumnInfo(name = "TopicID")
    @NonNull
    private Integer topicID;
    @ColumnInfo(name = "Deleted")
    private Integer deleted;

    public Notes(Integer noteID, Integer sourceID, Integer commentID, Integer questionID, Integer quoteID, Integer termID, Integer topicID, Integer deleted){
        this.noteID = noteID;
        this.sourceID = sourceID;
        this.commentID = commentID;
        this.questionID = questionID;
        this.quoteID = quoteID;
        this.termID = termID;
        this.topicID = topicID;
        this.deleted = deleted;
    }

    public int getNoteID() {
        return noteID;
    }

    public void setNoteID(int noteID) {
        this.noteID = noteID;
    }

    public int getSourceID() {
        return sourceID;
    }

    public void setSourceID(int sourceID) {
        this.sourceID = sourceID;
    }

    public int getCommentID() {
        return commentID;
    }

    public void setCommentID(int commentID) {
        this.commentID = commentID;
    }

    public int getQuestionID() {
        return questionID;
    }

    public void setQuestionID(int questionID) {
        this.questionID = questionID;
    }

    public int getQuoteID() {
        return quoteID;
    }

    public void setQuoteID(int quoteID) {
        this.quoteID = quoteID;
    }

    public int getTermID() {
        return termID;
    }

    public void setTermID(int termID) {
        this.termID = termID;
    }

    public int getTopicID() {
        return topicID;
    }

    public void setTopicID(int topicID) {
        this.topicID = topicID;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }
}
