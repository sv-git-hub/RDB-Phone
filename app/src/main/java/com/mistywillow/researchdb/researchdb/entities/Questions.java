package com.mistywillow.researchdb.researchdb.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Question")
public class Questions {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "QuestionID")
    private int questionID;
    @ColumnInfo(name = "Question")
    private String question;

    public Questions(String question){
        this.question = question;
    }

/*    @Ignore
    public Questions(int questionID, String question){
        this.questionID = questionID;
        this.question = question;
    }*/

    public int getQuestionID() {
        return questionID;
    }

    public void setQuestionID(int questionID) {
        this.questionID = questionID;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
