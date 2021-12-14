package com.mistywillow.researchdb.researchdb.entities;

// JOIN TABLE FOR AUTHORS AND SOURCES REQUIRING PRIMARY AND FOREIGN KEYS

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
 @Entity(tableName = "Author_By_Source", primaryKeys = {"AuthorID", "SourceID"}, foreignKeys = {
        @ForeignKey(entity = Authors.class, parentColumns = "AuthorID", childColumns = "AuthorID"),
        @ForeignKey(entity = Sources.class, parentColumns = "SourceID", childColumns = "SourceID")},
        indices = {@Index("AuthorID"), @Index("SourceID")})
public class AuthorBySource {
    @ColumnInfo(name = "AuthorID")
    private int authorID;
    @ColumnInfo(name = "SourceID")
    private int sourceID;

    public AuthorBySource(int authorID, int sourceID){
        this.authorID = authorID;
        this.sourceID = sourceID;
    }

    public int getAuthorID() {
        return authorID;
    }

    public void setAuthorID(int authorID) {
        this.authorID = authorID;
    }

    public int getSourceID() {
        return sourceID;
    }

    public void setSourceID(int sourceID) {
        this.sourceID = sourceID;
    }
}
