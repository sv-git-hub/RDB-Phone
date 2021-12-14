package com.mistywillow.researchdb.researchdb.entities;

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
    @NonNull
    @ColumnInfo(name = "NoteID")
    private Integer noteID;
    @NonNull
    @ColumnInfo(name = "SourceID")
    private Integer sourceID;
    @NonNull
    @ColumnInfo(name = "CommentID")
    private Integer commentID;
    @ColumnInfo(name = "QuestionID")
    private Integer questionID;
    @ColumnInfo(name = "QuoteID")
    private Integer quoteID;
    @ColumnInfo(name = "TermID")
    private Integer termID;
    @NonNull
    @ColumnInfo(name = "TopicID")
    private Integer topicID;
    @ColumnInfo(name = "Deleted")
    private Integer deleted;

    public Notes(@NonNull Integer noteID, @NonNull Integer sourceID, @NonNull Integer commentID, Integer questionID, Integer quoteID, Integer termID, @NonNull Integer topicID, Integer deleted){
        this.noteID = noteID;
        this.sourceID = sourceID;
        this.commentID = commentID;
        this.questionID = questionID;
        this.quoteID = quoteID;
        this.termID = termID;
        this.topicID = topicID;
        this.deleted = deleted;
    }
    @Ignore
    public Notes(@NonNull Integer sourceID, @NonNull Integer commentID, Integer questionID, Integer quoteID, Integer termID, @NonNull Integer topicID, Integer deleted){
        this.sourceID = sourceID;
        this.commentID = commentID;
        this.questionID = questionID;
        this.quoteID = quoteID;
        this.termID = termID;
        this.topicID = topicID;
        this.deleted = deleted;
    }

    @NonNull
    public Integer getNoteID() {
        return noteID;
    }

    public void setNoteID(int noteID) {
        this.noteID = noteID;
    }

    @NonNull
    public Integer getSourceID() {
        return sourceID;
    }

    public void setSourceID(int sourceID) {
        this.sourceID = sourceID;
    }

    @NonNull
    public Integer getCommentID() {
        return commentID;
    }

    public void setCommentID(int commentID) {
        this.commentID = commentID;
    }

    public Integer getQuestionID() {
        return questionID;
    }

    public void setQuestionID(int questionID) {
        this.questionID = questionID;
    }

    public Integer getQuoteID() {
        return quoteID;
    }

    public void setQuoteID(int quoteID) {
        this.quoteID = quoteID;
    }

    public Integer getTermID() {
        return termID;
    }

    public void setTermID(int termID) {
        this.termID = termID;
    }

    @NonNull
    public Integer getTopicID() {
        return topicID;
    }

    public void setTopicID(int topicID) {
        this.topicID = topicID;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }
}
