package com.mistywillow.researchdb.databases;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.mistywillow.researchdb.masterdb.dao.MasterDao;
import com.mistywillow.researchdb.masterdb.entity.MasterDatabaseList;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {MasterDatabaseList.class},
        version = 1
)
public abstract class MasterDatabase extends RoomDatabase {
    public abstract MasterDao getMasterDao();

    private static volatile MasterDatabase instance = null;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static MasterDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context,MasterDatabase.class,"master.db")
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }
}
