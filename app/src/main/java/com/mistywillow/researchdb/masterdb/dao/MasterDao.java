package com.mistywillow.researchdb.masterdb.dao;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.BaseColumns;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.mistywillow.researchdb.masterdb.entity.MasterDatabaseList;

import java.util.List;

@Dao
public abstract class MasterDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long insert(MasterDatabaseList masterDatabaseList);
    @Query("SELECT * FROM masterdatabaselist")
    public abstract List<MasterDatabaseList> getAllDatabases();

    public Cursor getAllDatabasesAsCursor() {
        MatrixCursor matrixCursor = new MatrixCursor(
                new String[]{
                        BaseColumns._ID, /* Cursor Adapter must use _id column for id) */
                        MasterDatabaseList.COL_DATABASE_NAME
                },
                0
        );
        for(MasterDatabaseList m: getAllDatabases()) {
            matrixCursor.addRow(new Object[]{m.id,m.databaseName});
        }
        return matrixCursor;
    }
}
