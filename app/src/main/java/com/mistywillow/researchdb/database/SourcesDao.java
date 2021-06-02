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

    @Query("SELECT * FROM Sources")
    List<Sources> getSources();
    @Query("SELECT * FROM Sources WHERE SourceID == :sourceID")
    Sources getSource(int sourceID);
    @Query("SELECT * FROM Sources WHERE Title = :sourceTitle")
    List<Sources> getSourceByTitle(String sourceTitle);
    @Query("SELECT last_insert_rowid()")
    int lastSourcePKID();
    @RawQuery
    List<Integer> customSearchSourcesTable(SupportSQLiteQuery query);
}
