package com.mistywillow.researchdb.database;


import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.mistywillow.researchdb.FileTypeConverter;
import com.mistywillow.researchdb.database.entities.*;

import java.io.File;
import java.nio.file.Paths;

@Database(entities = {AuthorBySource.class, Authors.class,  Comments.class, Files.class, FilesByNote.class,
        Notes.class, Questions.class, Quotes.class, Sources.class, Terms.class, Topics.class}, version = 1)
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

    public static final Migration MIGRATION_1_1 = new Migration(1, 1) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

        }
    };

    public static ResearchDatabase getInstance(Context context, String dbName){
        if(INSTANCE == null){
                synchronized (ResearchDatabase.class) {
                    INSTANCE = Room.databaseBuilder(context, ResearchDatabase.class, dbName)
                            .createFromAsset("databases/" + dbName).allowMainThreadQueries().build();
                }
        }
        return INSTANCE;
    }




}
