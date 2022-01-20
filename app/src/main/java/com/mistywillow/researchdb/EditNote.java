package com.mistywillow.researchdb;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.*;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import com.mistywillow.researchdb.databases.ResearchDatabase;
import com.mistywillow.researchdb.researchdb.entities.*;

import java.util.*;

public class EditNote extends AppCompatActivity {

    ActivityResultLauncher<Intent> resultLauncher;

    private Notes orgNoteTableIDs;

    private List<String> viewNoteDetails;
    private List<String> updatedNoteDetails;

    private List<Files> viewNoteFiles;
    private HashMap<String, Uri>fileURIs;
    private String filePath;

    private int nid;

    // TEXTVIEWS
    private TextView sourceType;
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

    // LABELS
    private TextView lblTopic;
    private TextView lblSummary;
    private TextView lblComment;
    private TextView lblQuestion;
    private TextView lblQuote;
    private TextView lblTerm;
    private TextView lblTimestamp;


    private AutoCompleteTextView topic;
    private AutoCompleteTextView question;
    private AutoCompleteTextView summary;

    private Button btnAddFile;
    private TableLayout tableLayoutFiles;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getColor(R.color.colorWhite));
        setSupportActionBar(toolbar);

        TextView page = findViewById(R.id.toolbar_page);
        page.setText(" EDIT NOTE\r\n" + Globals.DATABASE);

        ResearchDatabase rdb = ResearchDatabase.getInstance(this, Globals.DATABASE);

        // AUTOCOMPLETE TEXT VIEWS
        sourceTitle = findViewById(R.id.viewSource);
        author = findViewById(R.id.viewAuthors);
        topic = findViewById(R.id.viewTopic);
        question = findViewById(R.id.viewQuestion);
        summary = findViewById(R.id.viewSummary);

        // LABELS
        lblTopic = findViewById(R.id.lbl_View_Topic);
        lblSummary = findViewById(R.id.lbl_View_Summary);
        lblComment = findViewById(R.id.lbl_View_Comment);
        lblQuestion = findViewById(R.id.lbl_View_Question);
        lblQuote = findViewById(R.id.lbl_View_Quote);
        lblTerm = findViewById(R.id.lbl_View_Term);
        lblTimestamp = findViewById(R.id.lbl_View_TimeStamp);

        // REGULAR TEXT VIEWS
        sourceType = findViewById(R.id.viewType);
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

        btnAddFile = findViewById(R.id.addFile);
        fileURIs = new HashMap<>();

        // TABLES
        tableLayoutFiles = findViewById(R.id.table_files);
        tableLayoutFiles.setTag(R.id.table_files, "tableFiles");

        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null){
                Uri uri = result.getData().getData();
                manageNewFileURIs(uri);
                tableLayoutFiles.addView(BuildTableLayout.setupFilesTableRow(EditNote.this,tableLayoutFiles, filePath,false));
            }
        });

        // BEGIN SETUP and LOADING ACTIVITY and DATA ==================================================================
        Intent viewDetails = getIntent();
        Bundle viewBundle = viewDetails.getExtras();

        int vNoteID = viewBundle.getInt("NoteID");
        nid = vNoteID;
        orgNoteTableIDs = rdb.getNotesDao().getNote(vNoteID);
        viewNoteDetails = viewBundle.getStringArrayList("NoteDetails");
        viewNoteFiles = viewBundle.getParcelableArrayList("NoteFiles");

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
        btnAddFile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            resultLauncher.launch(intent);
        });
    }

    // MENU METHODS
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.update_note) {
            if (requiredFields()){
                update();}

        }else if(item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private void manageNewFileURIs(Uri file){
        filePath = UriUtils.getPathFromUri(this, file);
        if(!fileURIs.containsKey(filePath)){
            fileURIs.put(filePath,file);
        }
    }

    public void update() {
        captureFieldsUponUpdate();
        List<Files> newNoteFiles = DBQueryTools.captureNoteFiles(EditNote.this, viewNoteFiles, tableLayoutFiles, fileURIs);
        startActivity(DBQueryTools.updateNote(this, orgNoteTableIDs, viewNoteDetails, updatedNoteDetails,
                viewNoteFiles, newNoteFiles, nid));
    }

    // CUSTOM METHODS
    private void loadAutoCompleteTextViews(){
        ArrayAdapter<String> summaryAdapter = DBQueryTools.captureSummaries(this);
        summary.setThreshold(1);
        summary.setAdapter(summaryAdapter);
        summary.setOnFocusChangeListener(new AutoOnFocusChangeListener(summary));
        summary.setOnKeyListener(new AutoOnKeyListener(summary));

        ArrayAdapter<String> topicsAdapter = DBQueryTools.captureDBTopics(this);
        topic.setThreshold(1);
        topic.setAdapter(topicsAdapter);
        topic.setOnFocusChangeListener(new AutoOnFocusChangeListener(topic));
        topic.setOnKeyListener(new AutoOnKeyListener(topic));

        ArrayAdapter<String> acQuestionAdapt = DBQueryTools.captureDBQuestions(this);
        question.setThreshold(1);
        question.setAdapter(acQuestionAdapt);
        question.setOnFocusChangeListener(new AutoOnFocusChangeListener(question));
        question.setOnKeyListener(new AutoOnKeyListener(question));
    }

    private void captureFieldsUponUpdate(){
        String[] parseDate = DateTimestampManager.parseDate(date);
        updatedNoteDetails = new ArrayList<>();
        updatedNoteDetails.add(sourceType.getText().toString());    // 0: Type
        updatedNoteDetails.add(summary.getText().toString());       // 1: Summary
        updatedNoteDetails.add(sourceTitle.getText().toString());   // 2: Source
        updatedNoteDetails.add(author.getText().toString());        // 3: Author(s)
        updatedNoteDetails.add(question.getText().toString());      // 4: Question
        updatedNoteDetails.add(quote.getText().toString());         // 5: Quote
        updatedNoteDetails.add(term.getText().toString());          // 6: Term
        updatedNoteDetails.add(parseDate[0]);                       // 7: Year
        updatedNoteDetails.add(parseDate[1]);                       // 8: Month
        updatedNoteDetails.add(parseDate[2]);                       // 9: Day
        updatedNoteDetails.add(viewNoteDetails.get(Globals.VOLUME));// 10: Volume
        updatedNoteDetails.add(viewNoteDetails.get(Globals.EDITION));// 11: Edition
        updatedNoteDetails.add(viewNoteDetails.get(Globals.ISSUE)); // 12: Issue
        updatedNoteDetails.add(hyperlink.getText().toString());     // 13: Hyperlink
        updatedNoteDetails.add(comment.getText().toString());       // 14: Comment
        updatedNoteDetails.add(pgs_paras.getText().toString());     // 15: Page
        updatedNoteDetails.add(timeStamp.getText().toString());     // 16: TimeStamp
        updatedNoteDetails.add(topic.getText().toString());         // 17: Topic
    }

    private void populateFields() {
        sourceType.setText(viewNoteDetails.get(Globals.TYPE));
        topic.setText(viewNoteDetails.get(Globals.TOPIC));
        summary.setText(viewNoteDetails.get(Globals.SUMMARY));
        question.setText(viewNoteDetails.get(Globals.QUESTION));
        quote.setText(viewNoteDetails.get(Globals.QUOTE));
        term.setText(viewNoteDetails.get(Globals.TERM));
        sourceTitle.setText(viewNoteDetails.get(Globals.SOURCE));
        author.setText(viewNoteDetails.get(Globals.AUTHORS));
        comment.setText(viewNoteDetails.get(Globals.COMMENT));
        date.setText(DBQueryTools.concatenateDate(viewNoteDetails.get(Globals.MONTH), viewNoteDetails.get(Globals.DAY), viewNoteDetails.get(Globals.YEAR)));
        volume.setText(viewNoteDetails.get(Globals.VOLUME));
        edition.setText(viewNoteDetails.get(Globals.EDITION));
        issue.setText(viewNoteDetails.get(Globals.ISSUE));
        pgs_paras.setText(viewNoteDetails.get(Globals.PAGE));
        timeStamp.setText(viewNoteDetails.get(Globals.TIMESTAMP));
        if(viewNoteDetails.get(13).isEmpty()){
            hyperlink.setText(viewNoteDetails.get(Globals.HYPERLINK));
        }else {
            hyperlink.setText(Html.fromHtml(buildHyperlink(viewNoteDetails.get(Globals.HYPERLINK)), 0));
            hyperlink.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    private void populateFileData(List<Files> files){
        tableLayoutFiles.addView(BuildTableLayout.setupFilesTableRow(this,tableLayoutFiles, "FileName",true));
        if(files != null){
            if(files.size() > 0){
                for (Files f: files) {
                    String fName = f.getFileName();
                    tableLayoutFiles.addView(BuildTableLayout.setupFilesTableRow(this, tableLayoutFiles, fName,false));
                }
            }
        }
    }

    private String buildHyperlink(String link){
        return "<a href=\"" + link + "\">" + link + "</a> ";
    }

    private void setLabelColor(TextView textView, int lenValue){
        if(lenValue==0){
            textView.setTextColor(ContextCompat.getColor(EditNote.this, R.color.colorRed));
        }else{
            textView.setTextColor(ContextCompat.getColor(EditNote.this, R.color.colorDkGray));
        }
    }

    private void clearRedLabels(){
        List<TextView> labels = new ArrayList<>(
                Arrays.asList(lblTopic, lblQuestion,lblSummary, lblComment, lblQuote, lblTerm, lblTimestamp));
        for (TextView txtView: labels) {
            setLabelColor(txtView, 1);
        }
    }

    private boolean requiredFields(){
        String msg;
        clearRedLabels();

        if (topic.getText().toString().equals("")){
            setLabelColor(lblTopic, 0);
            msg = "Please select or enter a topic.";
        } else {

            if (comment.getText().toString().equals("")) {
                setLabelColor(lblComment, 0);
                msg = "Please enter a comment. Comments are specific details related to the topic and summary.";

            } else if (summary.getText().toString().equals("")) {
                setLabelColor(lblSummary, 0);
                msg = "Please enter a summary point. This expands upon your Topic entry or selection.";

            } else if (sourceType.getText().toString().equals("Question") && (question.getText().toString().isEmpty())) {
                setLabelColor(lblQuestion, 0);
                msg = "Please enter or select a question because 'Question' was selected as a source. A comment should expand on the meaning.";

            } else if (sourceType.getText().toString().equals("Quote") && (quote.getText().toString().equals(""))) {
                setLabelColor(lblQuote, 0);
                msg = "Please enter a quote because 'Quote' was selected as a source. A comment should expand on the meaning.";

            } else if (sourceType.getText().toString().equals("Term") && (term.getText().toString().equals(""))) {
                setLabelColor(lblTerm, 0);
                msg = "Please enter the term and definition. Expand within comments of other sources of interpretation.";

            } else if ((sourceType.getText().toString().equals("Video") || sourceType.getText().toString().equals("Audio")) && timeStamp.getText().toString().equals("")) {
                setLabelColor(lblTimestamp, 0);
                msg = "Please enter a TimeStamp value for an audio or video source.";

            } else {
                return true;
            }
        }
        PopupDialog.AlertMessageOK(EditNote.this, "Required Field", msg);
        return false;
    }
}
