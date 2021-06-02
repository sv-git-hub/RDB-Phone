package com.mistywillow.researchdb.database;

import androidx.room.*;
import androidx.sqlite.db.SupportSQLiteQuery;
import com.mistywillow.researchdb.database.entities.Authors;

import java.util.List;

@Dao
public interface AuthorsDao {
    @Insert
    void addAuthor(Authors author);
    @Update
    void updateAuthor(Authors author);
    @Delete
    void deleteAuthor(Authors author);

    @Query("SELECT * FROM Authors")
    List<Authors> getAuthors();
    @Query("SELECT * FROM Authors WHERE AuthorID = :authorID")
    Authors getAuthor(int authorID);
    @Query("SELECT a.AuthorID, a.FirstName, a.MiddleName, a.LastName, a.Suffix FROM Authors as a " +
            "LEFT JOIN Author_By_Source as abs ON abs.AuthorID = a.AuthorID " +
            "WHERE abs.SourceID = :sourceID")
    List<Authors> getSourceAuthors(int sourceID);
    @Query("SELECT last_insert_rowid()")
    int lastAuthorsPKID();

    @RawQuery
    List<Integer> customSearchAuthorsTable(SupportSQLiteQuery query);
}
