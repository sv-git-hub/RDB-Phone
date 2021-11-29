package com.mistywillow.researchdb;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;
import com.mistywillow.researchdb.database.entities.Authors;
import com.mistywillow.researchdb.database.entities.AuthorBySource;
import com.mistywillow.researchdb.database.entities.Sources;

import java.util.List;

public class SourcesWithAuthors {
    @Embedded
    public Sources sources;
    @Relation(
            parentColumn = "SourceID",
            entityColumn = "AuthorID",
            associateBy = @Junction(AuthorBySource.class))

    public List<Authors> authors;
}
