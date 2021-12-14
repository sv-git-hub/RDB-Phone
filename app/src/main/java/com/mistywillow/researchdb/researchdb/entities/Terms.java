package com.mistywillow.researchdb.researchdb.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Term")
public class Terms {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "TermID")
    private int termID;
    @ColumnInfo(name = "Term")
    private String term;

    public Terms(int termID, String term){
        this.termID = termID;
        this.term = term;
    }

    public int getTermID() {
        return termID;
    }

    public void setTermID(int termID) {
        this.termID = termID;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }
}
