package com.mistywillow.researchdb.researchdb.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "Author")
public class Authors {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "AuthorID")    private int authorID;
    @ColumnInfo(name = "FirstName") private String firstName;
    @ColumnInfo(name = "MiddleName") private String middleName;
    @ColumnInfo(name = "LastName") private String lastName;
    @ColumnInfo(name = "Suffix") private String suffix;

    public Authors(int authorID, String firstName, String middleName, String lastName, String suffix){
        this.authorID = authorID;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.suffix = suffix;
    }
    @Ignore
    public Authors(String firstName, String middleName, String lastName, String suffix){
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.suffix = suffix;
    }

    public int getAuthorID() {
        return authorID;
    }

    public void setAuthorID(int authorID) {
        this.authorID = authorID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

}
