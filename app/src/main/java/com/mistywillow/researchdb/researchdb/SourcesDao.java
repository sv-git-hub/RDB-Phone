package com.mistywillow.researchdb.researchdb;

import androidx.room.*;
import androidx.sqlite.db.SupportSQLiteQuery;
import com.mistywillow.researchdb.researchdb.entities.Sources;

import java.util.List;

@Dao
public interface SourcesDao {
    @Insert
    long addSource(Sources source);
    @Update
    void updateSource(Sources source);
    @Delete
    void deleteSource(Sources source);

    // GETS ALL SOURCES
    @Query("SELECT * FROM Source ORDER BY Title")
    List<Sources> getSources();

    // GETS INDIVIDUAL SOURCES
    @Query("SELECT * FROM Source WHERE SourceID == :sourceID")
    Sources getSource(int sourceID);

    // GETS SOURCES BY TITLE
    @Query("SELECT * FROM Source WHERE Title = :sourceTitle")
    List<Sources> getSourceByTitle(String sourceTitle);

    @Query("SELECT SourceID FROM Source WHERE Title = :sourceTitle")
    int getSourceIDByTitle(String sourceTitle);

    // GETS ID OF NEW SOURCE ENTRY
    @Query("SELECT last_insert_rowid()")
    int lastSourcePKID();

    @Query("SELECT COUNT(*) FROM Source WHERE title = :sourceTitle")
    int countByTitle(String sourceTitle);

    // SEARCH BASED UPON A CUSTOM TEXTVIEW ENTRY
    @RawQuery
    List<Integer> customSearchSourcesTable(SupportSQLiteQuery query);
}
