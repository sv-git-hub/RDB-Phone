package com.mistywillow.researchdb;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import com.mistywillow.researchdb.database.ResearchDatabase;
import com.mistywillow.researchdb.database.entities.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static androidx.core.content.FileProvider.getUriForFile;

public class AddNote extends AppCompatActivity {
    private Toolbar toolbar;
    private ResearchDatabase rdb;
    private int vNoteID;

    private List<String> viewNoteDetails;


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

    //AutoCompleteTextView sourceType;
    AutoCompleteTextView sourceType;
    AutoCompleteTextView sourceTitle;
    AutoCompleteTextView author;
    AutoCompleteTextView topic;
    AutoCompleteTextView question;
    AutoCompleteTextView summary;

    TableLayout tableLayoutFiles;
    TableLayout tableLayoutAuthors;
    Menu editMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getColor(R.color.colorWhite));
        setSupportActionBar(toolbar);

        // AUTOCOMPLETE TEXT VIEWS
        sourceType = findViewById(R.id.viewType);
        sourceTitle = findViewById(R.id.viewSource);
        author = findViewById(R.id.viewAuthors);
        topic = findViewById(R.id.viewTopic);
        question = findViewById(R.id.viewQuestion);
        summary = findViewById(R.id.viewSummary);

        // REGULAR TEXT VIEWS
        comment = findViewById(R.id.viewComment);
        hyperlink = findViewById(R.id.viewHyperlink);
        quote = findViewById(R.id.viewQuote);
        term = findViewById(R.id.viewTerm);

        tableLayoutFiles = findViewById(R.id.table_files);
        tableLayoutFiles.addView(BuildTableLayout.setupFilesTableRow(this,tableLayoutFiles,"FileID", "FileName",true));
        tableLayoutAuthors = findViewById(R.id.table_authors);
        tableLayoutAuthors.addView(BuildTableLayout.setupAuthorsTableRow(this,tableLayoutAuthors,"Organization/First", "Middle", "Last", "Suffix", true));

        // CAPTURE DB INFORMATION FOR AUTO-COMPLETE TEXT VIEWS
        loadAutoCompleteTextViews();
        setupOnClickActions();


        // BACK BUTTON <-
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    private void setupOnClickActions() {

        sourceTitle.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus){
                author.setText(DBQueryTools.captureAuthorNewOrOldSource(getApplicationContext(), sourceTitle.getText().toString()));
            }
            if(author.getText().toString().equals("Select an Author or Add new below")){
                Toast.makeText(this, "TODO: New Author Needed or Selected.", Toast.LENGTH_SHORT).show();

            }
        });
        sourceTitle.setOnKeyListener(new View.OnKeyListener() {
           @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
               if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER || keyCode == EditorInfo.IME_ACTION_DONE)
                   author.setText(DBQueryTools.captureAuthorNewOrOldSource(getApplicationContext(), sourceTitle.getText().toString()));
               InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
               imm.hideSoftInputFromWindow(sourceTitle.getWindowToken(), 0);
               return true;
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

            captureNoteDetails();
            DBQueryTools.addNewNote(this, viewNoteDetails, null);

            if (!requiredFields())
                return false;



            //startActivity(DBQueryTools.addNewNote(this, captureNoteDetails(), captureFiles()));
            Toast.makeText(this, "New Note Added", Toast.LENGTH_SHORT).show();
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

    private List<String> captureNoteDetails(){
        String[] parseDate = DBQueryTools.parseDate(date);
        viewNoteDetails = new ArrayList<>();
        viewNoteDetails.add(sourceType.getText().toString());    // 0: Type
        viewNoteDetails.add(summary.getText().toString());       // 1: Summary
        viewNoteDetails.add(sourceTitle.getText().toString());   // 2: Source
        viewNoteDetails.add(author.getText().toString());        // 3: Author(s)
        viewNoteDetails.add(question.getText().toString());      // 4: Question
        viewNoteDetails.add(quote.getText().toString());         // 5: Quote
        viewNoteDetails.add(term.getText().toString());          // 6: Term
        /*viewNoteDetails.add(viewNoteDetails.get(Globals.YEAR));                       // 7: Year
        viewNoteDetails.add(viewNoteDetails.get(Globals.MONTH));                       // 8: Month
        viewNoteDetails.add(viewNoteDetails.get(Globals.DAY));                       // 9: Day*/
        viewNoteDetails.add(parseDate[2]);                       // 7: Year
        viewNoteDetails.add(parseDate[0]);                       // 8: Month
        viewNoteDetails.add(parseDate[1]);                       // 9: Day
        viewNoteDetails.add(volume.getText().toString());        // 10: Volume
        viewNoteDetails.add(edition.getText().toString());       // 11: Edition
        viewNoteDetails.add(issue.getText().toString());         // 12: Issue
        viewNoteDetails.add(hyperlink.getText().toString());     // 13: Hyperlink
        viewNoteDetails.add(comment.getText().toString());       // 14: Comment
        viewNoteDetails.add(pgs_paras.getText().toString());     // 15: Page
        viewNoteDetails.add(timeStamp.getText().toString());     // 16: TimeStamp
        viewNoteDetails.add(topic.getText().toString());         // 17: Topic

        return viewNoteDetails;
    }

    private List<Files> captureFiles(){
        List<Files> files = rdb.getFilesDao().getFiles();
        return files;
    }

    private boolean requiredFields(){
        String msg = "";

        if (topic.getText().toString().isEmpty()){
            msg = "Please select or enter a topic.";
        }else {
            if (sourceTitle.getText().toString().isEmpty()) {
                msg = "Please select an existing source or enter a title for a new source.";

            } else if (author.getText().toString().isEmpty()) {
                msg = "Please select an existing author or organization, or add a new author or organization.";

            } else if (sourceType.getText().toString().isEmpty()) {
                msg = "Please select a source type.";

            } else if (sourceType.getText().toString().equals("Question") && (question.getText().toString().isEmpty())) {
                msg = "Please enter or select a question because 'Question' was selected as a source. A comment should expand on the meaning.";

            } else if (sourceType.getText().toString().equals("Quote") && (quote.getText().toString().isEmpty())) {
                msg = "Please enter a quote because 'Quote' was selected as a source. A comment should expand on the meaning.";

            } else if (sourceType.getText().toString().equals("Term") && (term.getText().toString().isEmpty())) {
                msg = "Please enter the term and definition. Expand within comments of other sources of interpretation.";

            } else if ((sourceType.getText().toString().equals("Video") || sourceType.getText().toString().equals("Audio")) && timeStamp.getText().toString().isEmpty()) {
                msg = "Please enter a TimeStamp value for an audio or video source.";

            } else if (comment.getText().toString().isEmpty()) {
                msg = "Please enter a comment. Comments are specific details related to the topic and summary.";

            } else if (summary.getText().toString().isEmpty()) {
                msg = "Please enter a summary point. This expands upon your Topic entry or selection.";

            } else {
                return true;
            }
        }
        PopupDialog.AlertMessage(AddNote.this, "Required Fields", msg);
        //PopupDialog.AlertInputBox(AddNote.this, "Required Input", "Please enter some input for testing.");
        return false;
    }


}
