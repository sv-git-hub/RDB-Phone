package com.mistywillow.researchdb;

import android.content.Intent;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import com.mistywillow.researchdb.database.ResearchDatabase;
import com.mistywillow.researchdb.database.entities.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EditNote extends AppCompatActivity {

    private Notes orgNoteTableIDs;

    private List<String> viewNoteDetails;
    private List<String> updatedNoteDetails;

    private int nid;

    private TextView sourceTitle;
    private TextView author;
    private TextView quote;
    private TextView term;
    private TextView comment;
    private TextView hyperlink;
    private TextView date;
    private TextView volume;
    private TextView edition;
    private TextView issue;
    private TextView pgs_paras;
    private TextView timeStamp;

    private AutoCompleteTextView sourceType;
    private AutoCompleteTextView topic;
    private AutoCompleteTextView question;
    private AutoCompleteTextView summary;

    private TableLayout tableLayoutFiles;
    private Menu editMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getColor(R.color.colorWhite));
        setSupportActionBar(toolbar);

        ResearchDatabase rdb = ResearchDatabase.getInstance(this, "Apologetic.db");

        // AUTOCOMPLETE TEXT VIEWS
        sourceType = findViewById(R.id.viewType);
        sourceTitle = findViewById(R.id.viewSource);
        author = findViewById(R.id.viewAuthors);
        topic = findViewById(R.id.viewTopic);
        question = findViewById(R.id.viewQuestion);
        summary = findViewById(R.id.viewSummary);

        // REGULAR TEXT VIEWS
        comment = findViewById(R.id.viewComment);
        quote = findViewById(R.id.viewQuote);
        term = findViewById(R.id.viewTerm);
        hyperlink = findViewById(R.id.viewHyperlink);
        date = findViewById(R.id.viewDate);
        volume = findViewById(R.id.viewVolume);
        edition = findViewById(R.id.viewEdition);
        issue = findViewById(R.id.viewIssue);
        pgs_paras = findViewById(R.id.viewPgsParas);
        timeStamp = findViewById(R.id.viewTimeStamp);

        // TABLES
        tableLayoutFiles = findViewById(R.id.table_files);
        tableLayoutFiles.setTag(R.id.table_files, "tableFiles");

        // BEGIN SETUP and LOADING ACTIVITY and DATA ==================================================================
        Intent viewDetails = getIntent();
        Bundle viewBundle = viewDetails.getExtras();

        assert viewBundle != null;
        int vNoteID = viewBundle.getInt("NoteID");
        nid = vNoteID;
        orgNoteTableIDs = rdb.getNotesDao().getNote(vNoteID);
        viewNoteDetails = viewBundle.getStringArrayList("NoteDetails");
        List<Files> viewNoteFiles = viewBundle.getParcelableArrayList("NoteFiles");

        // CAPTURE DB INFORMATION FOR AUTO-COMPLETE TEXT VIEWS
        loadAutoCompleteTextViews(); // INCLUDES SOURCE TYPES and SOURCE TITLE
        setupOnClickActions();

        // ALL FIELDS EXCEPT FILE
        populateFields();

        // POPULATE FILE DATA IF PRESENT
        populateFileData(viewNoteFiles);
        // BACK BUTTON <-
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    private void setupOnClickActions() {

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
        if(item.getItemId() == R.id.clear){
            Toast.makeText(this, String.valueOf(editMenu.size()), Toast.LENGTH_SHORT).show();

        }else if(item.getItemId() == R.id.update_note) {
            captureFieldsUponUpdate();
            startActivity(DBQueryTools.updateNote(this, orgNoteTableIDs, viewNoteDetails, updatedNoteDetails, nid));
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
            finishAndRemoveTask();
        }else if(item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private void setupMenuOptions() {
        editMenu.findItem(R.id.clear).setEnabled(false);
        editMenu.findItem(R.id.edit_note).setEnabled(false);
    }

    // CUSTOM METHODS

    private void loadAutoCompleteTextViews(){
        ArrayAdapter<String> sourceTypeAdapter = DBQueryTools.captureSourceTypes(this);
        sourceType.setThreshold(1);
        sourceType.setInputType(0);
        sourceType.setAdapter(sourceTypeAdapter);

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

    private void captureFieldsUponUpdate(){
        String[] parseDate = DBQueryTools.parseDate(date);
        updatedNoteDetails = new ArrayList<>();
        updatedNoteDetails.add(sourceType.getText().toString());    // 0: Type
        updatedNoteDetails.add(summary.getText().toString());       // 1: Summary
        updatedNoteDetails.add(sourceTitle.getText().toString());   // 2: Source
        updatedNoteDetails.add(author.getText().toString());        // 3: Author(s)
        updatedNoteDetails.add(question.getText().toString());      // 4: Question
        updatedNoteDetails.add(quote.getText().toString());         // 5: Quote
        updatedNoteDetails.add(term.getText().toString());          // 6: Term
        updatedNoteDetails.add(viewNoteDetails.get(7));                       // 7: Year
        updatedNoteDetails.add(viewNoteDetails.get(8));                       // 8: Month
        updatedNoteDetails.add(viewNoteDetails.get(9));                       // 9: Day
/*        updatedNoteDetails.add(parseDate[2]);                       // 7: Year
        updatedNoteDetails.add(parseDate[0]);                       // 8: Month
        updatedNoteDetails.add(parseDate[1]);                       // 9: Day*/
        updatedNoteDetails.add(viewNoteDetails.get(10));            // 10: Volume
        updatedNoteDetails.add(viewNoteDetails.get(11));            // 11: Edition
        updatedNoteDetails.add(viewNoteDetails.get(12));            // 12: Issue
        updatedNoteDetails.add(hyperlink.getText().toString());     // 13: Hyperlink
        updatedNoteDetails.add(comment.getText().toString());       // 14: Comment
        updatedNoteDetails.add(viewNoteDetails.get(15));            // 15: Page
        updatedNoteDetails.add(viewNoteDetails.get(16));            // 16: TimeStamp
        updatedNoteDetails.add(topic.getText().toString());         // 17: Topic
    }

    private void populateFields() {
        sourceType.setText(viewNoteDetails.get(0));
        summary.setText(viewNoteDetails.get(1));
        sourceTitle.setText(viewNoteDetails.get(2));
        author.setText(viewNoteDetails.get(3));
        question.setText(viewNoteDetails.get(4));
        quote.setText(viewNoteDetails.get(5));
        term.setText(viewNoteDetails.get(6));
        comment.setText(viewNoteDetails.get(14));
        topic.setText(viewNoteDetails.get(17));
        date.setText(DBQueryTools.concatenateDate(viewNoteDetails.get(8), viewNoteDetails.get(9), viewNoteDetails.get(7)));
        volume.setText(viewNoteDetails.get(10));
        edition.setText(viewNoteDetails.get(11));
        issue.setText(viewNoteDetails.get(12));
        pgs_paras.setText(viewNoteDetails.get(15));
        timeStamp.setText(viewNoteDetails.get(16));
        if(viewNoteDetails.get(13).isEmpty()){
            hyperlink.setText(viewNoteDetails.get(13));
        }else {
            hyperlink.setText(Html.fromHtml(buildHyperlink(viewNoteDetails.get(13)), 0));
            hyperlink.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    private void populateFileData(List<Files> files){
        tableLayoutFiles.addView(BuildTableLayout.setupFilesTableRow(this,tableLayoutFiles,"FileID", "FileName",true));
        if(files != null){
            if(files.size() > 0){
                for (Files f: files) {
                    String fName = f.getFileName();
                    tableLayoutFiles.addView(BuildTableLayout.setupFilesTableRow(this, tableLayoutFiles,"", fName,false));
                }
            }
        }
    }

    private String buildHyperlink(String link){
        return "<a href=\"" + link + "\">" + link + "</a> ";
    }
}
