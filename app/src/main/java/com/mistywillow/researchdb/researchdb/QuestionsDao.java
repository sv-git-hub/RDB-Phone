package com.mistywillow.researchdb.researchdb;

import androidx.room.*;
import androidx.sqlite.db.SupportSQLiteQuery;
import com.mistywillow.researchdb.researchdb.entities.Questions;

import java.util.List;

@Dao
public interface QuestionsDao {
    @Insert
    long addQuestion(Questions question);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    void updateQuestion(Questions question);

    @Delete
    void deleteQuestion(Questions question);

    @Query("SELECT * FROM Question ORDER BY Question")
    List<Questions> getQuestions();

    @Query("SELECT * FROM Question WHERE QuestionID = :questionID")
    Questions getQuestion(int questionID);

    @Query("SELECT QuestionID FROM Question WHERE Question = :question")
    int getQuestionByValue(String question);

    @Query("SELECT COUNT(*) FROM Question WHERE question = :question")
    int getCountByValue(String question);

    @Query("SELECT last_insert_rowid()")
    int lastQuestionPKID();

    @RawQuery
    List<Integer> customSearchQuestionsTable(SupportSQLiteQuery query);
}
