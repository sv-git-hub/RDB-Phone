package com.mistywillow.researchdb.database;

import androidx.room.*;
import androidx.sqlite.db.SupportSQLiteQuery;
import com.mistywillow.researchdb.database.entities.Quotes;

import java.util.List;

@Dao
public interface QuotesDao {
    @Insert
    void addQuote(Quotes quote);
    @Update
    void updateQuote(Quotes quote);
    @Delete
    void deleteQuote(Quotes quote);

    @Query("SELECT * FROM Quotes")
    List<Quotes> getQuotes();
    @Query("SELECT * FROM Quotes WHERE QuoteID = :quoteID")
    Quotes getQuote(int quoteID);
    @Query("SELECT last_insert_rowid()")
    int lastQuotesPKID();
    @RawQuery
    List<Integer> customSearchQuotesTable(SupportSQLiteQuery query);
}
