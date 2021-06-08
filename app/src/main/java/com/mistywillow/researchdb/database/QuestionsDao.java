package com.mistywillow.researchdb.database;

import androidx.room.*;
import androidx.sqlite.db.SupportSQLiteQuery;
import com.mistywillow.researchdb.database.entities.Questions;

import java.util.List;

@Dao
public interface QuestionsDao {
    @Insert
    void addQuestion(Questions question);
    @Update(onConflict = OnConflictStrategy.IGNORE)
    void updateQuestion(Questions question);
    @Delete
    void deleteQuestion(Questions question);

    @Query("SELECT * FROM Questions ORDER BY Question")
    List<Questions> getQuestions();

    @Query("SELECT * FROM Questions WHERE QuestionID = :questionID")
    Questions getQuestion(int questionID);

    @Query("SELECT last_insert_rowid()")
    int lastQuestionPKID();
    @RawQuery
    List<Integer> customSearchQuestionsTable(SupportSQLiteQuery query);
}
