package com.mistywillow.researchdb.researchdb.entities;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Source")
public class Sources implements Parcelable{

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "SourceID")
    private int sourceID;
    @ColumnInfo(name = "SourceType")
    private String sourceType;
    @ColumnInfo(name = "Title")
    private String title;
    @Nullable
    @ColumnInfo(name = "Year")
    private int year;
    @ColumnInfo(name = "Month")
    @Nullable
    private int month;
    @ColumnInfo(name = "Day")
    @Nullable
    private int day;
    @ColumnInfo(name = "Volume")
    private String volume;
    @ColumnInfo(name = "Edition")
    private String edition;
    @ColumnInfo(name = "Issue")
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

    protected Sources(Parcel in) {
        sourceID = in.readInt();
        sourceType = in.readString();
        title = in.readString();
        year = in.readInt();
        month = in.readInt();
        day = in.readInt();
        volume = in.readString();
        edition = in.readString();
        issue = in.readString();
    }

    public static final Creator<Sources> CREATOR = new Creator<Sources>() {
        @Override
        public Sources createFromParcel(Parcel in) {
            return new Sources(in);
        }

        @Override
        public Sources[] newArray(int size) {
            return new Sources[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(sourceID);
        dest.writeString(sourceType);
        dest.writeString(title);
        dest.writeInt(year);
        dest.writeInt(month);
        dest.writeInt(day);
        dest.writeString(volume);
        dest.writeString(edition);
        dest.writeString(issue);

    }
}
