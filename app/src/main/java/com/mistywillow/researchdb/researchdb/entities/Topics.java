package com.mistywillow.researchdb.researchdb.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Topic")
public class Topics {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "TopicID")
    private int topicID;
    @ColumnInfo(name = "Topic")
    private String topic;

    public Topics(int topicID, String topic){
        this.topicID = topicID;
        this.topic = topic;
    }

    public int getTopicID() {
        return topicID;
    }

    public void setTopicID(int topicID) {
        this.topicID = topicID;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
