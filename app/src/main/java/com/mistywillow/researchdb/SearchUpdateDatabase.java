package com.mistywillow.researchdb;

import android.content.Context;
import android.widget.Toast;
import androidx.room.Dao;
import androidx.room.RawQuery;
import androidx.sqlite.db.SimpleSQLiteQuery;
import com.mistywillow.researchdb.database.ResearchDatabase;

@Dao
public class SearchUpdateDatabase {

   /* public void updateField(Integer noteID, String table, String tableID, String field, String fieldData){

        int currValuePKID = findPKIDonNotes(noteID, tableID);
        int newValuePKID = findDataPKID(table, field, fieldData);
        int intCurrValue = count("Notes", tableID, currValuePKID);

        if(currValuePKID == 0) {
            addNewDataToDatabase(noteID, table, tableID, field, fieldData);
        }else if (currValuePKID > 0){
            if (!table.equals("Topic") && !table.equals("Question")) {
                replaceData(noteID, table, tableID, field, fieldData);
            } // Beyond this point handles "Topic" and "Question" updates

            else if (newValuePKID == 0 && count("Notes", tableID, currValuePKID) == 1) {
                replaceData(noteID, table, tableID, field, fieldData);

            } else if (newValuePKID == 0 && count("Notes", tableID, currValuePKID) > 1) {
                addNewDataToDatabase(noteID, table, tableID, field, fieldData);

            }else if (newValuePKID > 0) {
                updateNotesTable(noteID, tableID, newValuePKID);
                if(intCurrValue == 1) {
                    deleteTableRecord(table, tableID, currValuePKID);
                }
            }
        }else{
            Toast.makeText(context, "The " + field + " field in table " + table + " failed to update.", Toast.LENGTH_LONG);
        }
    }

    private void deleteTableRecord(String table, String tableID, int currValuePKID) {
    }

    private void updateNotesTable(Integer noteID, String tableID, int newValuePKID) {
    }

    private void replaceData(Integer noteID, String table, String tableID, String field, String fieldData) {
    }

    private void addNewDataToDatabase(Integer noteID, String table, String tableID, String field, String fieldData) {
        String statement = "INSERT INTO " + table + "(" + field + ") VALUES(?)";
        new SimpleSQLiteQuery(statement);


    }

    private int count(String notes, String tableID, int currValuePKID) {
    }

    private int findDataPKID(String table, String field, String fieldData) {
    }
    @RawQuery
    private int findPKIDonNotes(Integer noteID, String tableID) {

    }*/
}
