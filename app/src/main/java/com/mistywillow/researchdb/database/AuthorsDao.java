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

    // GETS ALL AUTHORS
    @Query("SELECT * FROM Authors")
    List<Authors> getAuthors();

    // GETS INDIVIDUAL AUTHOR
    @Query("SELECT * FROM Authors WHERE AuthorID = :authorID")
    Authors getAuthor(int authorID);

    // GETS ALL AUTHORS PERTAINING TO A SINGLE SOURCE
    @Query("SELECT a.AuthorID, a.FirstName, a.MiddleName, a.LastName, a.Suffix FROM Authors as a " +
            "LEFT JOIN Author_By_Source as abs ON abs.AuthorID = a.AuthorID " +
            "WHERE abs.SourceID = :sourceID")
    List<Authors> getSourceAuthors(int sourceID);

    // CAPTURES THE ID OR NEW ENTRY IN AUTHORS
    @Query("SELECT last_insert_rowid()")
    int lastAuthorsPKID();

    @RawQuery
    List<Integer> customSearchAuthorsTable(SupportSQLiteQuery query);
}
