package com.mistywillow.researchdb.researchdb;

import androidx.room.*;
import androidx.sqlite.db.SupportSQLiteQuery;
import com.mistywillow.researchdb.researchdb.entities.Authors;

import java.util.List;

@Dao
public interface AuthorsDao {
    @Insert
    long addAuthor(Authors author);
    @Update
    void updateAuthor(Authors author);
    @Delete
    void deleteAuthor(Authors author);

    // GETS ALL AUTHORS
    @Query("SELECT * FROM Author")
    List<Authors> getAuthors();

    // GETS INDIVIDUAL AUTHOR
    @Query("SELECT * FROM Author WHERE AuthorID = :authorID")
    Authors getAuthor(int authorID);

    // GETS ALL AUTHORS PERTAINING TO A SINGLE SOURCE
    @Query("SELECT a.AuthorID, a.FirstName, a.MiddleName, a.LastName, a.Suffix FROM Author as a " +
            "LEFT JOIN Author_By_Source as abs ON abs.AuthorID = a.AuthorID " +
            "WHERE abs.SourceID = :sourceID")
    List<Authors> getSourceAuthors(int sourceID);

    // CAPTURES THE ID OR NEW ENTRY IN AUTHORS
    @Query("SELECT last_insert_rowid()")
    int lastAuthorsPKID();

    @RawQuery
    List<Integer> customSearchAuthorsTable(SupportSQLiteQuery query);
}
