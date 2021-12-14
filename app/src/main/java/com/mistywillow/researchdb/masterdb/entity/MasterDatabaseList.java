package com.mistywillow.researchdb.masterdb.entity;

import androidx.room.*;

@Entity(
        indices = { @Index(value = "databaseName", unique = true)
        }
)
public class MasterDatabaseList {
    public static final String TABLE_NAME = "masterdatabaselist";
    public static final String COl_ID = "id";
    public static final String COL_DATABASE_NAME = "databaseName";
    public static final String[] ALL_COLUMNS = new String[]{
            COl_ID, COL_DATABASE_NAME
    };
    @PrimaryKey
    @ColumnInfo(name = COl_ID)
    public Long id;
    @ColumnInfo(name = COL_DATABASE_NAME)
    public String databaseName;

    public MasterDatabaseList() {}

    @Ignore
    public MasterDatabaseList(String databaseName) {
        this.databaseName = databaseName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }
}
