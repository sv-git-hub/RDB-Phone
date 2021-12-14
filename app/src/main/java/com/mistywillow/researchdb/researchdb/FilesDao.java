package com.mistywillow.researchdb.researchdb;

import androidx.room.*;
import androidx.sqlite.db.SupportSQLiteQuery;
import com.mistywillow.researchdb.researchdb.entities.Files;

import java.util.List;

@Dao
public interface FilesDao {
    @Insert
    long addFile(Files file);
    @Update
    void updateFile(Files file);
    @Delete
    void deleteFile(Files file);

    @Query("SELECT * FROM File")
    List<Files> getFiles();
    @Query("SELECT * FROM File WHERE FileID = :fileID")
    Files getFile(int fileID);
    @Query("SELECT :field FROM File WHERE :values")
    List<Integer> searchCommentsTable(String field, String values);
    @Query("SELECT last_insert_rowid()")
    int lastFilesPKID();

    @RawQuery
    List<Integer> customSearchFilesTable(SupportSQLiteQuery query);
}
