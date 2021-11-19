package com.mistywillow.researchdb;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;


public class BuildTableLayout extends AppCompatActivity{

    // FILES ========================================================

    public static TableRow setupFilesTableRow(Context context, TableLayout table, String fileID, String fileName, boolean bold) {
        TableRow row = new TableRow(context);
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        row.setLayoutParams(ll);
        if(bold){
            row.addView(setupFilesAddRowButton(context, table));
            row.addView(addRowTextViewToTable(context, fileName, true));
        }
        if (!bold) {
            row.addView(setupDeleteRowButton(context, table, fileName));
            for(int r=1; r < 2; r++){
                row.setTag(fileName);
                row.addView(addRowTextViewToTable(context, fileName, false));
                row.setClickable(true);
            }
        }
        return row;
    }

    public static TextView setupFilesAddRowButton(Context context, TableLayout table){
        TextView btnAddRow = new TextView(context);
        TableRow.LayoutParams trLayoutParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT);
        trLayoutParams.setMargins(3,3,3,3);
        trLayoutParams.weight = 1;
        btnAddRow.setLayoutParams(trLayoutParams);
        btnAddRow.setBackgroundColor(Color.WHITE);
        btnAddRow.setText(R.string.lbl_column_file_id);
        btnAddRow.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        btnAddRow.setTextSize(12);
        btnAddRow.setGravity(Gravity.CENTER);
        btnAddRow.setPadding(5,5,5,5);
        //btnAddRow.setOnClickListener(v -> table.addView(setupFilesTableRow(context, table, "", "", false)));
        return btnAddRow;
    }

    // AUTHORS ======================================================

    public static TableRow setupAuthorsTableRow (Context context, TableLayout table, String first, String middle, String last, String suffix, boolean bold){
        TableRow row = new TableRow(context);
        if(bold) {
            row.addView(setupAuthorsAddRowButton(context, table));
            row.addView(addRowTextViewToTable(context, first, true));
            row.addView(addRowTextViewToTable(context, middle, true));
            row.addView(addRowTextViewToTable(context, last, true));
            row.addView(addRowTextViewToTable(context, suffix, true));
        }
        if(!bold) {
            String tag = "";
            row.addView(setupDeleteRowButton(context, table, tag));
            for(int r=1; r<5;r++) {
                row.addView(addEditTextToTable(context, ""));
                row.setClickable(true);
            }
        }
        return row;
    }

    public static Button setupAuthorsAddRowButton(Context context, TableLayout table){
        Button btnAddRow = new Button(context);
        TableRow.LayoutParams trLayoutParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT);
        trLayoutParams.setMargins(3,3,3,3);
        trLayoutParams.weight = 1;
        btnAddRow.setLayoutParams(trLayoutParams);
        btnAddRow.setBackgroundColor(Color.WHITE);
        btnAddRow.setText("+");
        btnAddRow.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        btnAddRow.setGravity(Gravity.CENTER);
        btnAddRow.setPadding(5,5,5,5);
        btnAddRow.setOnClickListener(v -> {
            table.addView(setupAuthorsTableRow(context, table, "", "", "", "", false));
        });
        return btnAddRow;
    }

    // ADDITIONAL METHODS

    public static Button setupDeleteRowButton(Context context, TableLayout table, String tag){
        Button btnDelete = new Button(context);
        TableRow.LayoutParams trLayoutParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT);
        trLayoutParams.setMargins(3,3,3,3);
        trLayoutParams.weight = 1;
        trLayoutParams.height =75;
        btnDelete.setLayoutParams(trLayoutParams);
        btnDelete.setBackgroundColor(Color.WHITE);
        btnDelete.setText("-");
        btnDelete.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        btnDelete.setTextSize(14);
        btnDelete.setGravity(Gravity.CENTER);
        btnDelete.setPadding(5,5,5,5);
        btnDelete.setTag(tag);
        btnDelete.setOnClickListener(v -> {
            String btnTag = btnDelete.getTag().toString();
            deleteTableRows(table, btnTag);
        });
        return btnDelete;
    }

    public static EditText addEditTextToTable(Context context, String value){
        EditText editText = new EditText(context);
        TableRow.LayoutParams trLayoutParams = new TableRow.LayoutParams();
        trLayoutParams.setMargins(3,3,3,3);
        trLayoutParams.weight = 1;
        editText.setLayoutParams(trLayoutParams);
        editText.setBackgroundColor(Color.WHITE);
        editText.setText(value);
        editText.setTextSize(14);
        editText.setGravity(Gravity.CENTER);
        editText.setSingleLine();
        editText.setPadding(5, 5, 5, 5);
        return editText;
    }

    public static TextView addRowTextViewToTable(Context context, String value, boolean bold){
        TextView tv;
        tv = new TextView(context);
        TableRow.LayoutParams trLayoutParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT);
        trLayoutParams.setMargins(3,3,3,3);
        trLayoutParams.weight = 5;
        tv.setLayoutParams(trLayoutParams);
        tv.setText(String.valueOf(value));
        if(bold) tv.setTypeface(null, Typeface.BOLD);
        tv.setTextSize(12);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(8,8,8,8);
        tv.setBackgroundColor(Color.WHITE);

        return tv;
    }

    public static void deleteTableRows(TableLayout table, String tag){
        for (int i=1; i < table.getChildCount(); i++){
            TableRow tblRow = (TableRow) table.getChildAt(i);
            if(tblRow.getTag().toString().equals(tag)) {
                table.removeView(tblRow);
                break;
            }
        }
    }

    /*public CheckBox addCheckBoxToTable(){
        CheckBox checkBox = new CheckBox(this);
        TableRow.LayoutParams trLayoutParams = new TableRow.LayoutParams();
        trLayoutParams.setMargins(3,3,3,3);
        trLayoutParams.width = 25;
        trLayoutParams.height = 55;
        checkBox.setLayoutParams(trLayoutParams);
        checkBox.setPadding(3,3,3,3);
        checkBox.setBackgroundColor(Color.WHITE);
        checkBox.setGravity(Gravity.CENTER);
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                deleteTableRows(tableLayoutAuthors);
            }
        });

        return checkBox;
    }*/

}
