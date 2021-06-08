package com.mistywillow.researchdb.database;

import androidx.room.*;
import androidx.sqlite.db.SupportSQLiteQuery;
import com.mistywillow.researchdb.database.entities.Sources;

import java.util.List;

@Dao
public interface SourcesDao {
    @Insert
    void addSource(Sources source);
    @Update
    void updateSource(Sources source);
    @Delete
    void deleteSource(Sources source);

    // GETS ALL SOURCES
    @Query("SELECT * FROM Sources")
    List<Sources> getSources();

    // GETS INDIVIDUAL SOURCES
    @Query("SELECT * FROM Sources WHERE SourceID == :sourceID")
    Sources getSource(int sourceID);

    // GETS SOURCES BY TITLE
    @Query("SELECT * FROM Sources WHERE Title = :sourceTitle")
    List<Sources> getSourceByTitle(String sourceTitle);

    // GETS ID OF NEW SOURCE ENTRY
    @Query("SELECT last_insert_rowid()")
    int lastSourcePKID();

    @Query("SELECT COUNT(*) FROM Sources WHERE title = :sourceTitle")
    int countByTitle(String sourceTitle);

    // SEARCH BASED UPON A CUSTOM TEXTVIEW ENTRY
    @RawQuery
    List<Integer> customSearchSourcesTable(SupportSQLiteQuery query);
}
