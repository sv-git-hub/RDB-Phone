package com.mistywillow.researchdb;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.*;
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
import java.util.List;
import java.util.Objects;

import static androidx.core.content.FileProvider.getUriForFile;

public class EditNote extends AppCompatActivity {
    private ResearchDatabase rdb;

    private Notes updatedNote;
    private Notes orgNoteTableIDs;

    private List<String> viewNoteDetails;
    private List<String> updatedNoteDetails;

    private int nid;

    private TextView quote;
    private TextView term;
    private TextView comment;
    private TextView hyperlink;

    private AutoCompleteTextView sourceType;
    private AutoCompleteTextView sourceTitle;
    private AutoCompleteTextView author;
    private AutoCompleteTextView topic;
    private AutoCompleteTextView question;
    private AutoCompleteTextView summary;

    private TableLayout tableLayoutFiles;
    private TableLayout tableLayoutAuthors;
    private Menu editMenu;

    private Button btnAddAuthor;
    private Button btnDelAuthor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getColor(R.color.colorWhite));
        setSupportActionBar(toolbar);

        rdb = ResearchDatabase.getInstance(this, "Apologetic.db");

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

        // TABLES
        tableLayoutFiles = findViewById(R.id.table_files);
        tableLayoutAuthors = findViewById(R.id.table_authors);
        tableLayoutAuthors.addView(setupAuthorsTableRow("+", "Organization/First", "Middle Name", "Last Name","Suffix", true));

        // BUTTONS and SPECIAL ONCLICK ACTIONS
        btnAddAuthor = findViewById(R.id.btn_add_author);
        btnDelAuthor = findViewById(R.id.btn_del_author);

        // BEGIN SETUP and LOADING ACTIVITY and DATA ==================================================================

        Intent viewDetails = getIntent();
        Bundle viewBundle = viewDetails.getExtras();

        assert viewBundle != null;
        int vNoteID = viewBundle.getInt("NoteID");
        nid = vNoteID;
        updatedNote = rdb.getNotesDao().getNote(vNoteID);
        viewNoteDetails = viewBundle.getStringArrayList("NoteDetails");
        List<Files> viewNoteFiles = viewBundle.getParcelableArrayList("NoteFiles");

        // CAPTURE DB INFORMATION FOR AUTO-COMPLETE TEXT VIEWS
        captureNoteAndTableIDs(vNoteID);
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
        sourceTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    author.setText(DBQueryTools.captureAuthorNewOrOldSource(getApplicationContext(), sourceTitle.getText().toString()));
                }
                if(author.getText().toString().equals("need new author")){

                }
            }
        });
        btnAddAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tableLayoutAuthors.addView(setupAuthorsTableRow("-","", "", "", "", false));
            }
        });

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
            //clearFields();
            Toast.makeText(this, String.valueOf(editMenu.size()), Toast.LENGTH_SHORT).show();

        }else if(item.getItemId() == R.id.update_note) {
            captureFieldsUponUpdate();
            checkForUpdates(viewNoteDetails, updatedNoteDetails, nid);
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

    private void captureNoteAndTableIDs(int noteID){
        orgNoteTableIDs = rdb.getNotesDao().getNote(noteID);
    }

    private void loadAutoCompleteTextViews(){
        ArrayAdapter<String> sourceTypeAdapter = DBQueryTools.captureSourceTypes(this);
        sourceType.setThreshold(1);
        sourceType.setInputType(0);
        sourceType.setAdapter(sourceTypeAdapter);

        ArrayAdapter<String> sourceTitleAdapter = DBQueryTools.captureDBSources(this);
        sourceTitle.setThreshold(1);
        sourceTitle.setAdapter(sourceTitleAdapter);

        ArrayAdapter<String> authorsAdapter = DBQueryTools.captureDBAuthors(this);
        author.setThreshold(1);
        author.setAdapter(authorsAdapter);
        author.setFocusable(false);

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
        updatedNoteDetails = new ArrayList<>();
        updatedNoteDetails.add(sourceType.getText().toString());    // 0: Type
        updatedNoteDetails.add(summary.getText().toString());       // 1: Summary
        updatedNoteDetails.add(sourceTitle.getText().toString());   // 2: Source
        updatedNoteDetails.add(author.getText().toString());        // 3: Author(s)
        updatedNoteDetails.add(question.getText().toString());      // 4: Question
        updatedNoteDetails.add(quote.getText().toString());         // 5: Quote
        updatedNoteDetails.add(term.getText().toString());          // 6: Term
        updatedNoteDetails.add(viewNoteDetails.get(7));             // 7: Year
        updatedNoteDetails.add(viewNoteDetails.get(8));             // 8: Month
        updatedNoteDetails.add(viewNoteDetails.get(9));             // 9: Day
        updatedNoteDetails.add(viewNoteDetails.get(10));            // 10: Volume
        updatedNoteDetails.add(viewNoteDetails.get(11));            // 11: Edition
        updatedNoteDetails.add(viewNoteDetails.get(12));            // 12: Issue
        updatedNoteDetails.add(hyperlink.getText().toString());     // 13: Hyperlink
        updatedNoteDetails.add(comment.getText().toString());       // 14: Comment
        updatedNoteDetails.add(viewNoteDetails.get(15));            // 15: Page
        updatedNoteDetails.add(viewNoteDetails.get(16));            // 16: TimeStamp
        updatedNoteDetails.add(topic.getText().toString());         // 17: Topic
    }

    private void checkForUpdates(List<String> original, List<String> update, int vNoteID){
        // TYPE; TITLE; YEAR; MONTH; DAY; VOLUME; EDITION; ISSUE
        if(!valuesAreDifferent(original.get(0), update.get(0)) || !valuesAreDifferent(original.get(2), update.get(2)) ||
                !valuesAreDifferent(original.get(7), update.get(7)) || !valuesAreDifferent(original.get(8), update.get(8)) ||
                !valuesAreDifferent(original.get(9), update.get(9)) || !valuesAreDifferent(original.get(10), update.get(10)) ||
                !valuesAreDifferent(original.get(11), update.get(11)) || !valuesAreDifferent(original.get(12), update.get(12))){
            Sources src = new Sources(orgNoteTableIDs.getSourceID(), update.get(0),update.get(2), Integer.parseInt(update.get(7)),
                    Integer.parseInt(update.get(8)), Integer.parseInt(update.get(9)), update.get(10), update.get(11), update.get(12));
            rdb.getSourcesDao().updateSource(src);
        }
        // SUMMARY; COMMENT; PAGE; TIMESTAMP; HYPERLINK
        if(!valuesAreDifferent(original.get(1), update.get(1)) || !valuesAreDifferent(original.get(14), update.get(14)) ||
                !valuesAreDifferent(original.get(15), update.get(15)) || !valuesAreDifferent(original.get(16), update.get(16)) ||
                !valuesAreDifferent(original.get(13), update.get(13))){

            Comments cmts = rdb.getCommentsDao().getComment(orgNoteTableIDs.getCommentID());
            cmts.setSummary(update.get(1));     // Summary
            cmts.setComment(update.get(14));    // Comment
            cmts.setPage(update.get(15));       // Page
            cmts.setTimeStamp(update.get(16));  // TimeStamp
            cmts.setHyperlink(update.get(13));  // Hyperlink
            rdb.getCommentsDao().updateComment(cmts);
        }
        // AUTHOR(S)
        if(!valuesAreDifferent(original.get(3), update.get(3))){
            Toast.makeText(this, "TODO: Authors with Edit Note!",Toast.LENGTH_SHORT).show();
        }
        // QUESTION
        if(!valuesAreDifferent(original.get(4), update.get(4))){
            Questions questions = rdb.getQuestionsDao().getQuestion(updatedNote.getQuestionID());
            questions.setQuestion(update.get(4));
            rdb.getQuestionsDao().updateQuestion(questions);
        }
        // QUOTE
        if(!valuesAreDifferent(original.get(5), update.get(5))){
            Quotes quotes = new Quotes(orgNoteTableIDs.getQuoteID(), update.get(5));
            rdb.getQuotesDao().updateQuote(quotes);
        }
        // TERM
        if(!valuesAreDifferent(original.get(6), update.get(6))){
            Terms terms = new Terms(orgNoteTableIDs.getTermID(), update.get(6));
            rdb.getTermsDao().updateTerm(terms);
        }
        // TOPIC
        if(!valuesAreDifferent(original.get(17), update.get(17))){
            Topics topics = new Topics(orgNoteTableIDs.getTopicID(), update.get(17));
            rdb.getTopicsDao().updateTopic(topics);
        }
            Intent u = new Intent(this, ViewNote.class);
            u.putExtra("ID", vNoteID);
            u.putExtra("Type", update.get(0));
            u.putExtra("Summary", update.get(1));
            u.putExtra("Source", update.get(2));
            u.putExtra("Authors",update.get(3));
            startActivity(u);
    }

    // EVALUATES NOTES AS OBJECTS
    private boolean valuesAreDifferent(String obj1, String obj2){
        if(obj1 == null || obj1.isEmpty() || obj1.trim().isEmpty()){obj1 = "";}
        if(obj2 == null || obj2.isEmpty() || obj2.trim().isEmpty()){obj2 = "";}
        return obj1.equals(obj2);
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
        if(viewNoteDetails.get(13).isEmpty()){
            hyperlink.setText(viewNoteDetails.get(13));
        }else {
            hyperlink.setText(Html.fromHtml(buildHyperlink(viewNoteDetails.get(13)), 0));
            hyperlink.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    private void populateFileData(List<Files> files){
        tableLayoutFiles.addView(setupFilesTableRow("FileID", "FileName", true));
        if(files != null){
            if(files.size() > 0){
                for (Files f: files) {
                    tableLayoutFiles.addView(setupFilesTableRow(String.valueOf(f.getFileID()), f.getFileName(), false));
                }
            }
        }
    }

    private TableRow setupFilesTableRow(String fileID, String fileName, boolean bold) {

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

    private TableRow setupAuthorsTableRow (String add_del, String first, String middle, String last, String suffix, boolean bold){
        TableRow row = new TableRow(this);
        if(bold) {
            row.addView(setupRowTextView(add_del, true));
            row.addView(setupRowTextView(first, true));
            row.addView(setupRowTextView(middle, true));
            row.addView(setupRowTextView(last, true));
            row.addView(setupRowTextView(suffix, true));
        }
        if(!bold) {
            //row.addView(setupRowTextView(add_del, true));
            row.addView(addCheckBoxToTable());
            for(int r=1; r<5;r++) {
                row.addView(addEditTextToTable());
                row.setClickable(true);
            }
        }
        return row;
    }

    private Button setupAddRow(){
        btnAddAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tableLayoutAuthors.addView(setupAuthorsTableRow("-","", "", "", "", false));
            }
        });
        return btnAddAuthor;
    }

    private CheckBox addCheckBoxToTable(){
        CheckBox checkBox = new CheckBox(this);
        TableRow.LayoutParams trLayoutParams = new TableRow.LayoutParams();
        trLayoutParams.setMargins(3,3,3,3);
        trLayoutParams.width = 25;
        trLayoutParams.height = 55;
        checkBox.setLayoutParams(trLayoutParams);
        checkBox.setPadding(3,3,3,3);
        checkBox.setBackgroundColor(Color.WHITE);
        checkBox.setGravity(Gravity.CENTER);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    deleteTableRows(tableLayoutAuthors);
                }
            }
        });

        return checkBox;
    }

    private EditText addEditTextToTable(){
        EditText editText = new EditText(this);
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

    private void deleteTableRows(TableLayout table){
        for (int i=1; i < table.getChildCount(); i++){
            TableRow tblRow = (TableRow) table.getChildAt(i);
            CheckBox cbx = (CheckBox) tblRow.getChildAt(0);
            if(cbx.isChecked()){
                tableLayoutAuthors.removeView(tblRow);
            }

        }
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
            if(location.mkdir())
                Toast.makeText(context, "Director " + folder + " was created!", Toast.LENGTH_SHORT).show();
    }

    private String getFileExtension(String fileName){
        String[] ext = fileName.split("[.]");
        return ext[ext.length-1];
    }

    private String buildHyperlink(String link){
        return "<a href=\"" + link + "\">" + link + "</a> ";
    }
}
