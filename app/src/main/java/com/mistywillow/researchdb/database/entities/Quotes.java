package com.mistywillow.researchdb.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Quotes")
public class Quotes {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "QuoteID")
    private int quoteID;
    @ColumnInfo(name = "Quote")
    private String quote;

    public Quotes(int quoteID, String quote){
        this.quoteID =  quoteID;
        this.quote = quote;
    }

    public int getQuoteID() {
        return quoteID;
    }

    public void setQuoteID(int quoteID) {
        this.quoteID = quoteID;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }
}
