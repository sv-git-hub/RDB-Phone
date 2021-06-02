package com.mistywillow.researchdb.database;

import androidx.room.*;
import androidx.sqlite.db.SupportSQLiteQuery;
import com.mistywillow.researchdb.database.entities.Terms;

import java.util.List;

@Dao
public interface TermsDao {
    @Insert
    void addTerm(Terms term);
    @Update
    void updateTerm(Terms term);
    @Delete
    void deleteTerm(Terms term);

    @Query("SELECT * FROM Terms")
    List<Terms> getTerms();
    @Query("SELECT * FROM Terms WHERE TermID = :termID")
    Terms getTerm(int termID);
    @Query("SELECT last_insert_rowid()")
    int lastTermsPKID();

    @RawQuery
    List<Integer> customSearchTermsTable(SupportSQLiteQuery query);
}
