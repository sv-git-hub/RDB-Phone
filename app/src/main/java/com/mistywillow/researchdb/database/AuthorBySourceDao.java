package com.mistywillow.researchdb.database;

import androidx.room.*;
import com.mistywillow.researchdb.AuthorsWithSources;
import com.mistywillow.researchdb.SourcesWithAuthors;
import com.mistywillow.researchdb.database.entities.AuthorBySource;
import com.mistywillow.researchdb.database.entities.Authors;
import com.mistywillow.researchdb.database.entities.Sources;

import java.util.List;

@Dao
public interface AuthorBySourceDao {
    @Insert
    void insert(AuthorBySource authorBySource);

    @Query("SELECT Authors.AuthorID, FirstName, MiddleName, LastName, Suffix FROM Authors\n" +
            "LEFT JOIN Author_By_Source ON Authors.AuthorID=Author_By_Source.AuthorID\n" +
            "WHERE Author_By_Source.SourceID = :sourceID")
    List<Authors> getAuthorsForSource(final int sourceID);
   // @Query("SELECT s.SourceID, s.SourceType, s.Title, s.Year, s.Month, s.Day, s.Volume FROM Sources as s " +
   @Query("SELECT s.SourceID, s.SourceType, s.Title, s.Year, s.Month, s.Day, s.Volume, s.Edition, s.Issue FROM Sources as s " +
            "INNER JOIN Author_By_Source ON s.SourceID=Author_By_Source.SourceID " +
            "WHERE Author_By_Source.AuthorID = :authorID")
    List<Sources> getSourcesByAuthor(final int authorID);

    @Transaction
    @Query("SELECT * FROM Authors")
    List<AuthorsWithSources> getAuthorsWithSources();

    @Transaction
    @Query("SELECT * FROM Sources")
    List<SourcesWithAuthors> getSourcesWithAuthors();
}
