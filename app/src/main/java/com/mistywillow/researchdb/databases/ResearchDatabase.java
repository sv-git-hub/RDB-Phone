package com.mistywillow.researchdb.databases;


import android.content.Context;
import android.content.SharedPreferences;
import androidx.room.*;
import com.mistywillow.researchdb.researchdb.*;
import com.mistywillow.researchdb.researchdb.entities.*;

@Database(entities = {AuthorBySource.class, Authors.class,  Comments.class, Files.class, FilesByNote.class,
        Notes.class, Questions.class, Quotes.class, Sources.class, Terms.class, Topics.class}, version = 1)
public abstract class ResearchDatabase extends RoomDatabase {
    private static ResearchDatabase INSTANCE;
    public abstract AuthorBySourceDao getAuthorBySourceDao();
    public abstract AuthorsDao getAuthorsDao();
    public abstract CommentsDao getCommentsDao();
    public abstract FilesDao getFilesDao();
    public abstract FilesByNoteDao getFilesByNoteDao();
    public abstract NotesDao getNotesDao();
    public abstract QuestionsDao getQuestionsDao();
    public abstract QuotesDao getQuotesDao();
    public abstract SourcesDao getSourcesDao();
    public abstract TermsDao getTermsDao();
    public abstract TopicsDao getTopicsDao();

    /*public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

        }
    };*/

    public static ResearchDatabase getInstance(Context context, String dbName){
        INSTANCE = null; // Necessary to allow for multiple database connections. May cause and need to resolve possible leak issue
        synchronized (ResearchDatabase.class) {
            INSTANCE = Room.databaseBuilder(context, ResearchDatabase.class, dbName)
                    .allowMainThreadQueries()
                    .build();
            //.addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            //.createFromAsset("databases/" + dbName)
        }
        return INSTANCE;
    }




}
