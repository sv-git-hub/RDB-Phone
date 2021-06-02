package com.mistywillow.researchdb;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import com.mistywillow.researchdb.database.ResearchDatabase;
import com.mistywillow.researchdb.database.entities.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static androidx.core.content.FileProvider.getUriForFile;

public class AddNote extends AppCompatActivity {
    private Toolbar toolbar;
    private ResearchDatabase rdb;
    private int vNoteID;

    List<String> viewNoteDetails;
    List<String> orgNoteDetails;
    List<Files> viewNoteFiles;
    List<Files> orgNoteFiles;
    List<String> addTypes;

    List<String> orgQuestions;
    List<String> orgTopics;
    List<String> orgSourceTypes;
    List<String> orgSourceTitles;
    List<String> orgAuthors;

    List<Sources> sources;
    List<Authors> authors;
    List<Topics> topics;
    List<Questions> questions;

    TextView quote;
    TextView term;
    TextView comment;
    TextView hyperlink;

    //AutoCompleteTextView sourceType;
    Spinner sourceType;
    AutoCompleteTextView sourceTitle;
    AutoCompleteTextView author;
    AutoCompleteTextView topic;
    AutoCompleteTextView question;
    AutoCompleteTextView summary;

    TableLayout tableLayout;
    Menu editMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getColor(R.color.colorWhite));
        setSupportActionBar(toolbar);

        // AUTOCOMPLETE TEXT VIEWS
        sourceType = findViewById(R.id.viewSourceType);
        sourceTitle = findViewById(R.id.viewSource);
        author = findViewById(R.id.viewAuthors);
        topic = findViewById(R.id.viewTopic);
        question = findViewById(R.id.viewQuestion);
        summary = findViewById(R.id.viewSummary);

        // REGULAR TEXT VIEWS
        comment = findViewById(R.id.viewComment);
        hyperlink = findViewById(R.id.viewHyperlink);
        quote = findViewById(R.id.tab_View_Quote);
        term = findViewById(R.id.tab_View_Term);

        tableLayout = findViewById(R.id.table_files);

        // CAPTURE DB INFORMATION FOR AUTO-COMPLETE TEXT VIEWS
        loadAutoCompleteTextViews();
        setupOnClickActions();

        // BACK BUTTON <-
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    private void setupOnClickActions() {
        sourceTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                List<Sources> newSource = rdb.getSourcesDao().getSourceByTitle(sourceTitle.getText().toString());
                if(newSource.size() == 1) {
                    List<Authors> newAuthors = rdb.getAuthorBySourceDao().getAuthorsForSource(newSource.get(0).getSourceID());
                    author.setText(DBQueryTools.concatenateAuthors(newAuthors));
                }
            }
        });
    }

    private void setupMenuOptions() {
        editMenu.findItem(R.id.clear).setEnabled(false);
        editMenu.findItem(R.id.edit_note).setEnabled(false);
    }

    // MENU METHODS
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_menu, menu);
        editMenu = menu;
        setupMenuOptions();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Bundle bundle;
        Intent menuIntent;

        if(item.getItemId() == R.id.clear){
            //clearFields();
            Toast.makeText(this, String.valueOf(editMenu.size()), Toast.LENGTH_SHORT).show();
        }else if(item.getItemId() == R.id.update_note) {
            Toast.makeText(this, "Update Note clicked!", Toast.LENGTH_SHORT).show();
        }else if(item.getItemId() == R.id.edit_note){
            Toast.makeText(this, "Edit Note for Delete clicked!", Toast.LENGTH_SHORT).show();
        }else if(item.getItemId() == R.id.mark_for_delete) {
            Toast.makeText(this, "Mark Note for Delete clicked!", Toast.LENGTH_SHORT).show();
        }else if(item.getItemId() == R.id.unMark_for_delete) {
            Toast.makeText(this, "Unmark Note for Delete clicked!", Toast.LENGTH_SHORT).show();
        }else if(item.getItemId() == R.id.review_for_delete) {
            Toast.makeText(this, "Review Notes for Delete clicked!", Toast.LENGTH_SHORT).show();
        }else if(item.getItemId() == R.id.permanently_delete) {
            Toast.makeText(this, "Permanently Delete Note clicked!", Toast.LENGTH_SHORT).show();
        }else if(item.getItemId() == R.id.delete_database) {
            Toast.makeText(this, "Delete Database clicked!", Toast.LENGTH_SHORT).show();
        }else if(item.getItemId() == R.id.main_close) {
            //closeApplication();
        }else if(item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }
    private void loadAutoCompleteTextViews(){
        ArrayAdapter<String> sourceTypeAdapter = DBQueryTools.captureSourceTypes(this);
        sourceType.setAdapter(sourceTypeAdapter);

        ArrayAdapter<String> sourceTitleAdapter = DBQueryTools.captureDBSources(this);
        sourceTitle.setThreshold(1);
        sourceTitle.setAdapter(sourceTitleAdapter);

        ArrayAdapter<String> authorsAdapter = DBQueryTools.captureDBAuthors(this);
        author.setThreshold(1);
        author.setAdapter(authorsAdapter);

        ArrayAdapter<String> summaryAdapter = DBQueryTools.captureSummaries(this);
        summary.setThreshold(1);
        summary.setAdapter(summaryAdapter);

        ArrayAdapter<String> topicsAdapter = DBQueryTools.captureDBTopics(this);
        topic.setThreshold(1);
        topic.setAdapter(topicsAdapter);

        ArrayAdapter<String> acQuestionAdapt = DBQueryTools.captureDBQuestions(this);
        question.setThreshold(1);
        question.setAdapter(acQuestionAdapt);
    }


    private void populateFields() {
        //sourceType.setText(viewNoteDetails.get(0));
        topic.setText(viewNoteDetails.get(17));
        question.setText(viewNoteDetails.get(4));
        quote.setText(viewNoteDetails.get(5));
        term.setText(viewNoteDetails.get(6));
        sourceTitle.setText(viewNoteDetails.get(2));
        author.setText(viewNoteDetails.get(3));
        summary.setText(viewNoteDetails.get(1));
        comment.setText(viewNoteDetails.get(14));
        if(viewNoteDetails.get(13).isEmpty()){
            hyperlink.setText(viewNoteDetails.get(13));
        }else {
            hyperlink.setText(Html.fromHtml(buildHyperlink(viewNoteDetails.get(13)), 0));
            hyperlink.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    private void populateFileData(List<Files> files){
        tableLayout.addView(setupTableRow("FileID", "FileName", true));
        if(files != null){
            if(files.size() > 0){
                for (Files f: files) {
                    tableLayout.addView(setupTableRow(String.valueOf(f.getFileID()), f.getFileName(), false));
                }
            }
        }
    }

    private TableRow setupTableRow(String fileID, String fileName, boolean bold) {

        TableRow row = new TableRow(this);
        row.addView(setupRowTextView(fileID, bold));
        row.addView(setupRowTextView(fileName, bold));
        row.setClickable(true);
        if (!bold) {
            row.setOnClickListener(v -> {

                TableRow tablerow = (TableRow) v;
                TextView sample = (TextView) tablerow.getChildAt(1);
                String result = sample.getText().toString();

                MimeTypeMap myMime = MimeTypeMap.getSingleton();
                String mimeType = myMime.getMimeTypeFromExtension(getFileExtension(result));

                checkFolderExists(this, "note_files");

                File filePaths = new File(getFilesDir().toString() + "/note_files/");
                File newFile = new File(filePaths, result);

                if(!newFile.exists()){
                    Uri contentUri = getUriForFile(getApplicationContext(), "com.mistywillow.fileprovider", newFile);
                    openFile(contentUri, mimeType);
                }


            });
        }
        return row;
    }

    private TextView setupRowTextView(String value, boolean bold){
        TextView tv = new TextView(this);
        TableRow.LayoutParams trLayoutParams = new TableRow.LayoutParams();
        trLayoutParams.setMargins(3,3,3,3);
        tv.setText(String.valueOf(value));
        if(bold) tv.setTypeface(null, Typeface.BOLD);
        tv.setLayoutParams(trLayoutParams);
        tv.setTextSize(12);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(8,8,8,8);
        tv.setBackgroundColor(getColor(R.color.colorWhite));
        return tv;
    }

    private void openFile(Uri uri, String mime){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri,mime);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, 2);
    }

    public static void checkFolderExists(Context context, String folder){
        File location = new File(context.getFilesDir() + "/" + folder);
        if(!location.exists())
            location.mkdir();
    }

    private String getFileExtension(String fileName){
        String[] ext = fileName.split("[.]");
        return ext[ext.length-1];
    }

    private String buildHyperlink(String link){
        StringBuilder sb = new StringBuilder();
        sb.append("<a href=\"").append(link).append("\">").append(link).append("</a> ");
        return sb.toString();
    }
}
