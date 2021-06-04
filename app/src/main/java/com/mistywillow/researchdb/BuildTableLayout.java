package com.mistywillow.researchdb;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.*;

public class BuildTableLayout {

    public static TableRow setupFilesTableRow(Context context, TableLayout table, String fileID, String fileName, boolean bold) {

        TableRow row = new TableRow(context);
        if(bold) {
            //row.addView(setupRowTextView(fileID, true));
            row.addView(addFilesAddRowButton(context, table));
            row.addView(addRowTextViewToTable(context, fileName, true));

        }
        if (!bold) {
            row.addView(addDeleteRowButton(context, table));
            for(int r=1; r < 2; r++){
                row.addView(addEditTextToTable(context));
                row.setClickable(true);
            }
        }
        return row;
    }

     public static TableRow setupAuthorsTableRow (Context context, TableLayout table, String first, String middle, String last, String suffix, boolean bold){
        TableRow row = new TableRow(context);
        if(bold) {
            row.addView(addAuthorsAddRowButton(context, table));
            row.addView(addRowTextViewToTable(context, first, true));
            row.addView(addRowTextViewToTable(context, middle, true));
            row.addView(addRowTextViewToTable(context, last, true));
            row.addView(addRowTextViewToTable(context, suffix, true));
        }
        if(!bold) {
            row.addView(addDeleteRowButton(context, table));
            for(int r=1; r<5;r++) {
                row.addView(addEditTextToTable(context));
                row.setClickable(true);
            }
        }
        return row;
    }

    public static Button addFilesAddRowButton(Context context, TableLayout table){
        Button btnAddRow = new Button(context);
        TableRow.LayoutParams trLayoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
        trLayoutParams.setMargins(3,3,3,3);
        btnAddRow.setBackgroundColor(Color.WHITE);
        btnAddRow.setLayoutParams(trLayoutParams);
        btnAddRow.setText("+");
        btnAddRow.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        btnAddRow.setGravity(Gravity.CENTER);
        btnAddRow.setPadding(5,5,5,5);
        btnAddRow.setOnClickListener(v -> table.addView(setupFilesTableRow(context, table, "", "", false)));
        return btnAddRow;
    }

    public static Button addAuthorsAddRowButton(Context context, TableLayout table){
        Button btnAddRow = new Button(context);
        TableRow.LayoutParams trLayoutParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT);
        trLayoutParams.setMargins(3,3,3,3);
        btnAddRow.setLayoutParams(trLayoutParams);
        btnAddRow.setBackgroundColor(Color.WHITE);
        btnAddRow.setText("+");
        btnAddRow.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        btnAddRow.setGravity(Gravity.CENTER);
        btnAddRow.setPadding(5,5,5,5);
        if(table.getTag().equals("tableLayoutAuthors"))
            btnAddRow.setOnClickListener(v -> table.addView(setupAuthorsTableRow(context, table, "", "", "", "", false)));
        else if(table.getTag().equals("tableLayoutFiles"))
            btnAddRow.setOnClickListener(v -> table.addView(setupFilesTableRow(context, table, "", "",  false)));
        return btnAddRow;
    }


    public static Button addDeleteRowButton(Context context, TableLayout table){
        Button btnDeleteAuthor = new Button(context);
        TableRow.LayoutParams trLayoutParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT);
        trLayoutParams.setMargins(3,3,3,3);
        btnDeleteAuthor.setBackgroundColor(Color.WHITE);
        btnDeleteAuthor.setLayoutParams(trLayoutParams);
        btnDeleteAuthor.setText("-");
        btnDeleteAuthor.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        btnDeleteAuthor.setGravity(Gravity.CENTER);
        btnDeleteAuthor.setPadding(5,5,5,5);
        btnDeleteAuthor.setOnClickListener(v -> deleteTableRows(table));
        return btnDeleteAuthor;
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

    public static EditText addEditTextToTable(Context context){
        EditText editText = new EditText(context);
        TableRow.LayoutParams trLayoutParams = new TableRow.LayoutParams();
        trLayoutParams.setMargins(3,3,3,3);
        editText.setLayoutParams(trLayoutParams);
        editText.setBackgroundColor(Color.WHITE);
        editText.setTextSize(14);
        editText.setGravity(Gravity.CENTER);
        editText.setSingleLine();
        editText.setPadding(5, 5, 5, 5);
        return editText;
    }

    public static TextView addRowTextViewToTable(Context context, String value, boolean bold){
        TextView tv = new TextView(context);
        TableRow.LayoutParams trLayoutParams = new TableRow.LayoutParams();
        trLayoutParams.setMargins(3,3,3,3);
        tv.setText(String.valueOf(value));
        if(bold) tv.setTypeface(null, Typeface.BOLD);
        tv.setLayoutParams(trLayoutParams);
        tv.setTextSize(12);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(8,8,8,8);
        tv.setBackgroundColor(Color.WHITE);
        return tv;
    }

    public static void deleteTableRows(TableLayout table){
        for (int i=1; i < table.getChildCount(); i++){
            TableRow tblRow = (TableRow) table.getChildAt(i);
            table.removeView(tblRow);
        }
    }


}
