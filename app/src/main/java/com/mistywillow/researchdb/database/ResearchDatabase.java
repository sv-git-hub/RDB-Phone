package com.mistywillow.researchdb.database;


import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.*;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.mistywillow.researchdb.FileTypeConverter;
import com.mistywillow.researchdb.database.entities.*;

import java.io.File;
import java.nio.file.Paths;

@Database(entities = {AuthorBySource.class, Authors.class,  Comments.class, Files.class, FilesByNote.class,
        Notes.class, Questions.class, Quotes.class, Sources.class, Terms.class, Topics.class}, version = 3)
public abstract class ResearchDatabase extends RoomDatabase {
    private static ResearchDatabase INSTANCE;
    public abstract AuthorBySourceDao getAuthorBySourceDao();
    public abstract AuthorsDao getAuthorsDao();
    public abstract CommentsDao getCommentsDao();

    //@TypeConverters(FileTypeConverter.class)
    public abstract FilesDao getFilesDao();
    //@TypeConverters(FileTypeConverter.class)
    public abstract FilesByNoteDao getFilesByNoteDao();

    public abstract NotesDao getNotesDao();
    public abstract QuestionsDao getQuestionsDao();
    public abstract QuotesDao getQuotesDao();
    public abstract SourcesDao getSourcesDao();
    public abstract TermsDao getTermsDao();
    public abstract TopicsDao getTopicsDao();

    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Authors RENAME TO Author");
            database.execSQL("ALTER TABLE Comments RENAME TO Comment");
            database.execSQL("ALTER TABLE Files RENAME TO File");
            database.execSQL("ALTER TABLE Files_By_Note RENAME TO File_By_Note");
            database.execSQL("ALTER TABLE Questions RENAME TO Question");
            database.execSQL("ALTER TABLE Quotes RENAME TO Quote");
            database.execSQL("ALTER TABLE Sources RENAME TO Source");
            database.execSQL("ALTER TABLE Terms RENAME TO Term");
            database.execSQL("ALTER TABLE Topics RENAME TO Topic");

        }
    };

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
                database.execSQL(
                        "CREATE TABLE IF NOT EXISTS NotesTmp (NoteID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, SourceID INTEGER NOT NULL, CommentID INTEGER NOT NULL, QuestionID INTEGER, QuoteID INTEGER, TermID INTEGER, TopicID INTEGER NOT NULL, Deleted INTEGER, FOREIGN KEY (SourceID) REFERENCES Sources (SourceID), FOREIGN KEY (CommentID) REFERENCES Comments (CommentID), FOREIGN KEY (TopicID) REFERENCES Topics (TopicID))");

                // Copy the data
                database.execSQL(
                        "INSERT INTO NotesTmp (NoteID, SourceID, CommentID, QuestionID, QuoteID, TermID, TopicID, Deleted) SELECT NoteID, SourceID, CommentID, QuestionID, QuoteID, TermID, TopicID, Deleted FROM Notes");

                // Remove the old table
                database.execSQL("DROP TABLE Notes");

                // Change the table name to the correct one
                database.execSQL("ALTER TABLE NotesTmp RENAME TO Notes");

            database.execSQL("CREATE INDEX index_Notes_SourceID ON Notes (SourceID)");
            database.execSQL("CREATE INDEX index_Notes_CommentID ON Notes (CommentID)");
            database.execSQL("CREATE INDEX index_Notes_QuestionD ON Notes (QuestionID)");
            database.execSQL("CREATE INDEX index_Notes_QuoteID ON Notes (QuoteID)");
            database.execSQL("CREATE INDEX index_Notes_TermID ON Notes (TermID)");
            database.execSQL("CREATE INDEX index_Notes_TopicID ON Notes (TopicID)");
        }
    };



    public static ResearchDatabase getInstance(Context context, String dbName){
        if(INSTANCE == null){
                synchronized (ResearchDatabase.class) {
                    INSTANCE = Room.databaseBuilder(context, ResearchDatabase.class, dbName)
                            .createFromAsset("databases/" + dbName)
                            .allowMainThreadQueries()
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                            .build();
                }
        }
        return INSTANCE;
    }




}
