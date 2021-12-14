package com.mistywillow.researchdb;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;
import com.mistywillow.researchdb.researchdb.entities.Authors;
import com.mistywillow.researchdb.researchdb.entities.AuthorBySource;
import com.mistywillow.researchdb.researchdb.entities.Sources;

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
