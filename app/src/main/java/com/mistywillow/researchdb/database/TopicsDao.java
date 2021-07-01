package com.mistywillow.researchdb.database;

import androidx.room.*;
import androidx.sqlite.db.SupportSQLiteQuery;
import com.mistywillow.researchdb.database.entities.Topics;

import java.util.List;

@Dao
public interface TopicsDao {
    @Insert
    void addTopic(Topics topic);
    @Update
    void updateTopic(Topics topic);
    @Delete
    void deleteTopic(Topics topic);

    @Query("SELECT * FROM Topics ORDER BY Topic")
    List<Topics> getTopics();
    @Query("SELECT * FROM Topics WHERE TopicID = :topicID")
    Topics getTopic(int topicID);
    @Query("Select topicID FROM Topics WHERE Topic = :topic")
    int getTopicID(String topic);
    @Query("SELECT last_insert_rowid()")
    int lastTopicsPKID();
    @RawQuery
    List<Integer> customSearchTopicsTable(SupportSQLiteQuery query);
}
