package com.mistywillow.researchdb.database;

import androidx.room.*;
import androidx.sqlite.db.SupportSQLiteQuery;
import com.mistywillow.researchdb.database.entities.Comments;

import java.util.List;

@Dao
public interface CommentsDao {
    @Insert
    void addComment(Comments comment);
    @Update
    void updateComment(Comments comment);
    @Delete
    void deleteComment(Comments comment);

//    @Query("UPDATE Comments SET Comment = :data " +
//            "WHERE EXISTS (SELECT NoteID FROM Notes WHERE Comments.CommentID = Notes.CommentID and Notes.NoteID = :nid)")
//    void update(long nid, String data);

    @Query("SELECT * FROM Comments")
    List<Comments> getComments();
    @Query("SELECT * FROM Comments WHERE CommentID = :commentID")
    Comments getComment(int commentID);
    @Query("SELECT last_insert_rowid()")
    int lastCommentPKID();

    @RawQuery
    List<Integer> customSearchCommentsTable(SupportSQLiteQuery query);
}
