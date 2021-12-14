package com.mistywillow.researchdb.researchdb.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Comment")
public class Comments {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "CommentID")
    private int commentID;
    @ColumnInfo(name = "Summary")
    private String summary;
    @ColumnInfo(name = "Comment")
    private String comment;
    @ColumnInfo(name = "Page")
    private String page;
    @ColumnInfo(name = "TimeStamp", typeAffinity = ColumnInfo.UNSPECIFIED)
    private String timeStamp;
    @ColumnInfo(name = "Hyperlink")
    private String hyperlink;

    public Comments(int commentID, String summary, String comment, String page, String timeStamp, String hyperlink){
        this.commentID = commentID;
        this.summary = summary;
        this.comment = comment;
        this.page = page;
        this.timeStamp = timeStamp;
        this.hyperlink = hyperlink;
    }

    public int getCommentID() {
        return commentID;
    }

    public void setCommentID(int commentID) {
        this.commentID = commentID;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getTimeStamp() { return timeStamp; }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getHyperlink() {
        return hyperlink;
    }

    public void setHyperlink(String hyperlink) {
        this.hyperlink = hyperlink;
    }
}
