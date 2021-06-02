package com.mistywillow.researchdb.database.entities;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Sources")
public class Sources {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "SourceID")
    private int sourceID;
    @ColumnInfo(name = "SourceType")
    private String sourceType;
    @ColumnInfo(name = "Title")
    private String title;
    @ColumnInfo(name = "Year", defaultValue = "")
    @Nullable
    private int year;
    @ColumnInfo(name = "Month", defaultValue = "")
    @Nullable
    private int month;
    @ColumnInfo(name = "Day", defaultValue = "")
    @Nullable
    private int day;
    @ColumnInfo(name = "Volume", defaultValue = "")
    private String volume;
    @ColumnInfo(name = "Edition", defaultValue = "")
    private String edition;
    @ColumnInfo(name = "Issue", defaultValue = "")
    private String issue;

    public Sources(int sourceID, String sourceType, String title, int year, int month, int day, String volume, String edition, String issue){
        this.sourceID = sourceID;
        this.sourceType = sourceType;
        this.title = title;
        this.year = year;
        this.month = month;
        this.day = day;
        this.volume = volume;
        this.edition = edition;
        this.issue = issue;
    }

    public int getSourceID() {
        return sourceID;
    }

    public void setSourceID(int sourceID) {
        this.sourceID = sourceID;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

}
