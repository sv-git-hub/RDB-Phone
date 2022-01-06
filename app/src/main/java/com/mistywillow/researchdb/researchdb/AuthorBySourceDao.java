package com.mistywillow.researchdb.researchdb;

import androidx.room.*;
import com.mistywillow.researchdb.AuthorsWithSources;
import com.mistywillow.researchdb.SourcesWithAuthors;
import com.mistywillow.researchdb.researchdb.entities.Authors;
import com.mistywillow.researchdb.researchdb.entities.AuthorBySource;
import com.mistywillow.researchdb.researchdb.entities.Sources;

import java.util.List;

@Dao
public interface AuthorBySourceDao {
    @Insert
    long insert(AuthorBySource authorBySource);

    @Delete
    void deleteABS(AuthorBySource authorBySource);


    @Query("SELECT Author.AuthorID, FirstName, MiddleName, LastName, Suffix FROM Author\n" +
            "LEFT JOIN Author_By_Source ON Author.AuthorID=Author_By_Source.AuthorID\n" +
            "WHERE Author_By_Source.SourceID = :sourceID")
    List<Authors> getAuthorsForSource(final int sourceID);

   @Query("SELECT s.SourceID, s.SourceType, s.Title, s.Year, s.Month, s.Day, s.Volume, s.Edition, s.Issue FROM Source as s " +
            "INNER JOIN Author_By_Source ON s.SourceID=Author_By_Source.SourceID " +
            "WHERE Author_By_Source.AuthorID = :authorID")
    List<Sources> getSourcesByAuthor(final int authorID);

   @Query("SELECT COUNT(*) FROM Author_By_Source WHERE SourceID = :id")
   int countAuthorsBySourceID(int id);

    @Query("SELECT COUNT(*) FROM Author_By_Source WHERE AuthorID = :id")
    int countAuthorIsUsed(int id);

    @Transaction
    @Query("SELECT * FROM Author")
    List<AuthorsWithSources> getAuthorsWithSources();

    @Transaction
    @Query("SELECT * FROM Source")
    List<SourcesWithAuthors> getSourcesWithAuthors();
}
