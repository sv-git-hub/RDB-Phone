package com.mistywillow.researchdb.database.entities;

import androidx.room.*;

@Entity(tableName = "Notes", foreignKeys = {
        @ForeignKey(entity = Sources.class, parentColumns = "SourceID", childColumns = "SourceID"),
        @ForeignKey(entity = Comments.class, parentColumns = "CommentID", childColumns = "CommentID"),
        @ForeignKey(entity = Questions.class, parentColumns = "QuestionID", childColumns = "QuestionID"),
        @ForeignKey(entity = Quotes.class, parentColumns = "QuoteID", childColumns = "QuoteID"),
        @ForeignKey(entity = Terms.class, parentColumns = "TermID", childColumns = "TermID"),
        @ForeignKey(entity = Topics.class, parentColumns = "TopicID", childColumns = "TopicID")},
        indices = {@Index("SourceID"), @Index("CommentID"), @Index("QuestionID"), @Index("QuoteID"),
                @Index("TermID"), @Index("TopicID")})
public class Notes {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "NoteID")
    private int noteID;
    @ColumnInfo(name = "SourceID")
    private int sourceID;
    @ColumnInfo(name = "CommentID")
    private int commentID;
    @ColumnInfo(name = "QuestionID", defaultValue = "0")
    private int questionID;
    @ColumnInfo(name = "QuoteID", defaultValue = "0")
    private int quoteID;
    @ColumnInfo(name = "TermID", defaultValue = "0")
    private int termID;
    @ColumnInfo(name = "TopicID")
    private int topicID;
    @ColumnInfo(name = "Deleted", defaultValue = "0")
    private int deleted;

    public Notes(int noteID, int sourceID, int commentID, int questionID, int quoteID, int termID, int topicID, int deleted){
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
