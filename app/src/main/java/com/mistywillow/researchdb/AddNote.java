package com.mistywillow.researchdb;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.*;
import android.view.inputmethod.EditorInfo;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

public class AddNote extends AppCompatActivity {
    private ResearchDatabase rdb;
    ActivityResultLauncher<Intent> intentLauncher;
    ActivityResultLauncher<Intent> resultLauncher;
    ActivityResultLauncher<Intent> xmlLauncher;

    private EditText quote;
    private EditText term;
    private EditText comment;
    private EditText hyperlink;
    private EditText date;
    private EditText volume;
    private EditText edition;
    private EditText issue;
    private EditText pgs_paras;
    private EditText timeStamp;

    private Spinner sourceType;
    private AutoCompleteTextView sourceTitle;
    private AutoCompleteTextView topic;
    private AutoCompleteTextView question;
    private AutoCompleteTextView summary;

    private TextView author;

    private TextView lblType;
    private TextView lblTopic;
    private TextView lblSummary;
    private TextView lblComment;
    private TextView lblSource;
    private TextView lblAuthor;
    private TextView lblDate;
    private TextView lblQuestion;
    private TextView lblQuote;
    private TextView lblTerm;
    private TextView lblTimestamp;

    private Button btnAddFile;

    private TableLayout tableLayoutFiles;
    private TableLayout tableLayoutAuthors;

    private int selectedSourceID;

    private List<String> newNoteDetails;
    private List<Integer> newSourcesAuthorIDs;
    private HashMap<String, Uri>fileURIs;
    private String filePath;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getColor(R.color.colorWhite));
        setSupportActionBar(toolbar);

        TextView page = findViewById(R.id.toolbar_page);
        page.setText(" ADD NOTE\r\n" + Globals.DATABASE);

        // AUTOCOMPLETE TEXT VIEWS
        sourceType = findViewById(R.id.viewType);
        sourceTitle = findViewById(R.id.viewSource);
        author = findViewById(R.id.viewAuthors);
        topic = findViewById(R.id.viewTopic);
        question = findViewById(R.id.viewQuestion);
        summary = findViewById(R.id.viewSummary);

        // TEXTVIEW LABELS
        lblType = findViewById(R.id.lbl_View_Type);
        lblTopic = findViewById(R.id.lbl_View_Topic);
        lblSummary = findViewById(R.id.lbl_View_Summary);
        lblComment = findViewById(R.id.lbl_View_Comment);
        lblSource = findViewById(R.id.lbl_View_Source);
        lblAuthor = findViewById(R.id.lbl_View_Authors);
        lblDate = findViewById(R.id.lbl_View_Date);
        lblQuestion = findViewById(R.id.lbl_View_Question);
        lblQuote = findViewById(R.id.lbl_View_Quote);
        lblTerm = findViewById(R.id.lbl_View_Term);
        lblTimestamp = findViewById(R.id.lbl_View_TimeStamp);


        // REGULAR EDITTEXT
        comment = findViewById(R.id.viewComment);
        hyperlink = findViewById(R.id.viewHyperlink);
        quote = findViewById(R.id.viewQuote);
        term = findViewById(R.id.viewTerm);
        date = findViewById(R.id.viewDate);
        timeStamp = findViewById(R.id.viewTimeStamp);
        pgs_paras = findViewById(R.id.viewPgsParas);
        volume = findViewById(R.id.viewVolume);
        edition = findViewById(R.id.viewEdition);
        issue = findViewById(R.id.viewIssue);

        btnAddFile = findViewById(R.id.addFile);
        fileURIs = new HashMap<>();

        tableLayoutFiles = findViewById(R.id.table_files);
        tableLayoutFiles.addView(BuildTableLayout.setupFilesTableRow(this,tableLayoutFiles, "FilePath/FileName",true));
        tableLayoutAuthors = findViewById(R.id.table_authors);
        tableLayoutAuthors.addView(BuildTableLayout.setupAuthorsTableRow(this,tableLayoutAuthors,"Organization/First", "Middle", "Last", "Suffix", true, false));

        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null){
                Uri uri = result.getData().getData();
                manageNewFileURIs(uri);
                tableLayoutFiles.addView(BuildTableLayout.setupFilesTableRow(AddNote.this,tableLayoutFiles, filePath,false));
            }
        });

        //ARRAYS, LISTS, ETC.
        selectedSourceID = 0;
        newSourcesAuthorIDs = new ArrayList<>();

        // GET DATABASE INSTANCE
        rdb = ResearchDatabase.getInstance(this, Globals.DATABASE);
        intentLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result ->{
                    if(result.getResultCode() == Activity.RESULT_OK) {
                        assert result.getData() != null;
                        Sources src = result.getData().getParcelableExtra("source");
                        if(src == null){
                            PopupDialog.AlertMessageOK(AddNote.this, "Error: Author Choice",
                                    "An issue occurred and an author choice was not returned.");
                            return;
                        }
                        setSourceDetails(src);
                        selectedSourceID = src.getSourceID();
                    }

                }
        );

        // CAPTURE DB INFORMATION FOR AUTO-COMPLETE TEXT VIEWS
        loadSpinner();
        loadAutoCompleteTextViews();
        setupOnClickActions();

        xmlLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null){
                Uri uri = result.getData().getData();
                String str = UriUtils.getPathFromUri(this, uri);


                assert str != null;
                if(str.toLowerCase().endsWith(".xml")) {
                    try {

                        ReadXMLFileDOMParser parser = new ReadXMLFileDOMParser(str);
                        if(parser.getError() == 0) {
                            invalidXMLMessage(0);
                        }else if(parser.getError() == 2) {
                            invalidXMLMessage(2);
                        }else if(parser.getError() == 3){
                            invalidXMLMessage(0);
                        }else{
                            loadImportedNoteDetails(parser.getImportNotes());
                        }
                    } catch (Exception e) {
                        invalidXMLMessage(0);
                        e.printStackTrace();

                    }
                }else{
                    invalidXMLMessage(3);
                }
            }

        });


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

        sourceTitle.setOnItemClickListener((parent, view, position, id) -> {
            setLabelColor(lblSource, position);
            populateSourceDetails(DBQueryTools.getSourcesByTitle(sourceTitle.getText().toString().trim()));
        });


        sourceTitle.setOnEditorActionListener((v, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                //do what you want on the press of 'done'
                populateSourceDetails(DBQueryTools.getSourcesByTitle(sourceTitle.getText().toString().trim()));
            }
            return false;
        });

        tableLayoutAuthors.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if(sourceTitle.getText().toString().trim().length() !=0 && !sourceTitle.hasFocus()){

                if (tableLayoutAuthors.getChildCount() > 1) {
                    author.setText(R.string.add_author_phrase);
                    // If selected Source was found but a new author added, the there is a duplicate but new Source
                    if (selectedSourceID > 0) {
                        selectedSourceID = 0;
                    }
                }else if(tableLayoutAuthors.getChildCount() == 1 && bottom != oldBottom){
                    populateSourceDetails(DBQueryTools.getSourcesByTitle(sourceTitle.getText().toString().trim()));
                }
            }
        });

        date.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus)
                DateTimestampManager.validateDate(AddNote.this, date.getText().toString());
        });

        timeStamp.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus)
                DateTimestampManager.validateSearchTimeStamp(AddNote.this, timeStamp.getText().toString());
        });

        topic.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus)
                DateTimestampManager.validateTopic(AddNote.this, topic.getText().toString());
        });
    }

    // MENU METHODS
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.clear){
            clearFields();

        }else if(item.getItemId() == R.id.import_note) {
            Intent xml = new Intent(Intent.ACTION_GET_CONTENT);
            xml.setType("*/*");
            xmlLauncher.launch(xml);

        }else if(item.getItemId() == R.id.add_note) {
            if (!requiredFields(true)) {
                return false;
            }
            addNewNote();
            onBackPressed();

        }else if(item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void invalidXMLMessage(int err){
        if(err == 0) {
            PopupDialog.AlertMessageOK(AddNote.this, "Invalid Import", "Failure: " +
                    "the chosen XML was not recognized as a valid " + getResources().getString(R.string.app_name) + " XML file and not importable.");
        }else if(err == 2){
            PopupDialog.AlertMessageOK(this, "Multiple Note Import",
                    "This file contains multiple notes. At present, only one note at a time can be imported.");
        }else if(err== 3){
            PopupDialog.AlertMessageOK(AddNote.this, "Invalid Import", "Failure: " +
                    "the chosen file was not recognized as a valid " + getResources().getString(R.string.app_name) + " XML file and may be corrupted.");
        }
    }

    private void manageNewFileURIs(Uri file){
        filePath = UriUtils.getPathFromUri(this, file);
        if(!fileURIs.containsKey(filePath)){
            fileURIs.put(filePath,file);
        }
    }

    private void addNewNote(){
        int srcID;
        captureNoteDetails();

        if (selectedSourceID > 0)
            srcID = selectedSourceID;
        else{
            srcID = DBQueryTools.addNewSource(new Sources(0, newNoteDetails.get(Globals.TYPE),
                    newNoteDetails.get(Globals.SOURCE), Integer.parseInt(newNoteDetails.get(Globals.YEAR)), Integer.parseInt(newNoteDetails.get(Globals.MONTH)),
                    Integer.parseInt(newNoteDetails.get(Globals.DAY)), newNoteDetails.get(Globals.VOLUME),
                    newNoteDetails.get(Globals.EDITION), newNoteDetails.get(Globals.ISSUE)));
        }

        if (author.getText().toString().equals("For new sources add new/existing author(s) below")) {

            List<Authors> ta = DBQueryTools.captureAuthorsTable(tableLayoutAuthors);
            for (Authors authors : ta) {
                int found = DBQueryTools.findAuthor(authors);
                if (found == 0) {
                    newSourcesAuthorIDs.add(DBQueryTools.addNewAuthor(authors));
                } else if (found > 0) {
                    newSourcesAuthorIDs.add(found);
                }
            }
        }

        List<Integer> newNoteIDs = new ArrayList<>();
        newNoteIDs.add(0);
        newNoteIDs.add(srcID);                                      // Source
        newNoteIDs.add(DBQueryTools.getCommentID(newNoteDetails));  // Comment
        newNoteIDs.add(DBQueryTools.getQuestionID(newNoteDetails)); // Question
        newNoteIDs.add(DBQueryTools.getQuoteID(newNoteDetails));    // Quote
        newNoteIDs.add(DBQueryTools.getTermID(newNoteDetails));     // Term
        newNoteIDs.add(DBQueryTools.getTopicID(newNoteDetails));    // Topic
        newNoteIDs.add(0);                                          // Delete

        if(!newSourcesAuthorIDs.isEmpty()){
            for(int aID : newSourcesAuthorIDs){
                rdb.getAuthorBySourceDao().insert(new AuthorBySource(aID,srcID));
            }
        }

        List<Files> addFiles = DBQueryTools.captureNoteFiles(this, null, tableLayoutFiles, fileURIs);
        startActivity(DBQueryTools.addNewNote(this, newNoteIDs, addFiles));
    }

    private void loadImportedNoteDetails(List<HashMap<String, List<String>>> data){
        if(data.size() > 1){
            PopupDialog.AlertMessageOK(this, "Multiple Note Import",
                    "At present, only one note at a time can be imported. " +
                            "This file contains: " + data.size());
            return;
        }
        data.forEach((n) ->{
            // SOURCE INFORMATION
            sourceType.setSelection(typePosition(Objects.requireNonNull(n.get("Source")).get(0)));
            sourceTitle.setText(Objects.requireNonNull(n.get("Source")).get(1));
            volume.setText(n.get("Source").get(5)); // Volume
            edition.setText(n.get("Source").get(6)); // Edition
            issue.setText(n.get("Source").get(7)); // Issue
            String temp = DBQueryTools.concatenateDate(
                    Objects.requireNonNull(n.get("Source")).get(3),
                    Objects.requireNonNull(n.get("Source")).get(4),
                    Objects.requireNonNull(n.get("Source")).get(2));
            date.setText(temp.trim());

            // TOPIC, QUESTION, TERM, QUOTE
            topic.setText(Objects.requireNonNull(n.get("Topic")).get(0));
            question.setText(n.get("Question").get(0));
            quote.setText(n.get("Quote").get(0));
            term.setText(n.get("Term").get(0));

            // COMMENT
            summary.setText(Objects.requireNonNull(n.get("Comment")).get(0)); // Summary
            comment.setText(Objects.requireNonNull(n.get("Comment")).get(1)); // Comment
            pgs_paras.setText(n.get("Comment").get(2)); // Page(s)/Para(s)
            timeStamp.setText(n.get("Comment").get(3)); // TimeStamp
            hyperlink.setText(n.get("Comment").get(4)); // Hyperlink

            // AUTHOR(s)
            if(rdb.getSourcesDao().countByTitle(n.get("Source").get(1)) == 0){

                Objects.requireNonNull(n.get("Author")).forEach(a -> {
                    String[] arr = a.split("\\*");
                    if (arr.length == 4)
                        tableLayoutAuthors.addView(BuildTableLayout.setupAuthorsTableRow(this, tableLayoutAuthors,
                                arr[0], arr[1], arr[2], arr[3], true, true));
                    if (arr.length == 3)
                        tableLayoutAuthors.addView(BuildTableLayout.setupAuthorsTableRow(this, tableLayoutAuthors,
                                arr[0], arr[1], arr[2], "", true, true));
                    if (arr.length == 2)
                        tableLayoutAuthors.addView(BuildTableLayout.setupAuthorsTableRow(this, tableLayoutAuthors,
                                arr[0], arr[1], "", "", true, true));
                    if (arr.length == 1)
                        tableLayoutAuthors.addView(BuildTableLayout.setupAuthorsTableRow(this, tableLayoutAuthors,
                                arr[0], "","","", true, true));
                });
            }

            // FILE(s)
            if(n.containsKey("File")) {
                n.get("File").forEach(f -> {
                    String[] ary = f.split("\\*", 2);
                    String cacheFilePath = getCacheDir().getPath() + "/" + ary[0];
                    byte[] bytes = ary[1].getBytes();
                    OutputStream os = null;
                    try {
                        os = new FileOutputStream(cacheFilePath);
                        os.write(Base64.getDecoder().decode(bytes));
                        os.flush();
                        os.close();

                        manageNewFileURIs(Uri.fromFile(new File(cacheFilePath)));
                        tableLayoutFiles.addView(BuildTableLayout.setupFilesTableRow(AddNote.this,tableLayoutFiles, cacheFilePath,false));

                    }catch(Exception io){
                        io.printStackTrace();
                    }finally{
                        try {
                            if(os != null){
                                os.flush();
                                os.close();
                            }
                        }catch(IOException | NullPointerException io){
                            io.printStackTrace();
                        }
                    }
                });
            }
            PopupDialog.AlertMessageOK(this, "Review and Add",
                    "Your note is ready to import permanently, select 'Add Note.' If you decide not to " +
                            "import this note either select clear or exit the page.");
        });
    }

    private int typePosition(String arrName){
        if(arrName == null){return 0;}
        HashMap<String, Integer> t = new HashMap<>();
        t.put("ARTICLE", 1);
        t.put("AUDIO", 2);
        t.put("BOOK", 3);
        t.put("JOURNAL", 4);
        t.put("PERIODICAL", 5);
        t.put("QUESTION", 6);
        t.put("QUOTE", 7);
        t.put("TERM", 8);
        t.put("VIDEO", 9);
        t.put("WEBSITE", 10);
        t.put("OTHER", 11);
        return t.get(arrName.toUpperCase());
    }

    private void loadSpinner(){
        ArrayAdapter<String> sourceTypeAdapter = DBQueryTools.captureSourceTypes(this, "custom");
        sourceType.setAdapter(sourceTypeAdapter);
    }

    private void loadAutoCompleteTextViews(){
        ArrayAdapter<String> sourceTitleAdapter = DBQueryTools.captureDBSources(this);
        sourceTitle.setThreshold(1);
        sourceTitle.setAdapter(sourceTitleAdapter);
        sourceTitle.setOnFocusChangeListener(new AutoOnFocusChangeListener(sourceTitle));
        sourceTitle.setOnKeyListener(new AutoOnKeyListener(sourceTitle));

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

    private void captureNoteDetails(){
        String[] parseDate= DateTimestampManager.parseDate(date);
        newNoteDetails = new ArrayList<>();
        newNoteDetails.add(sourceType.getSelectedItem().toString()); // 0: Type
        newNoteDetails.add(summary.getText().toString());       // 1: Summary
        newNoteDetails.add(sourceTitle.getText().toString().trim());   // 2: Source
        newNoteDetails.add(author.getText().toString());        // 3: Author(s)
        newNoteDetails.add(question.getText().toString());      // 4: Question
        newNoteDetails.add(quote.getText().toString());         // 5: Quote
        newNoteDetails.add(term.getText().toString());          // 6: Term
        newNoteDetails.add(parseDate[0]);                       // 7: Year
        newNoteDetails.add(parseDate[1]);                       // 8: Month
        newNoteDetails.add(parseDate[2]);                       // 9: Day
        newNoteDetails.add(volume.getText().toString());        // 10: Volume
        newNoteDetails.add(edition.getText().toString());       // 11: Edition
        newNoteDetails.add(issue.getText().toString());         // 12: Issue
        newNoteDetails.add(hyperlink.getText().toString());     // 13: Hyperlink
        newNoteDetails.add(comment.getText().toString());       // 14: Comment
        newNoteDetails.add(pgs_paras.getText().toString());     // 15: Page
        newNoteDetails.add(timeStamp.getText().toString());     // 16: TimeStamp
        newNoteDetails.add(topic.getText().toString());         // 17: Topic
    }

    private void populateSourceDetails(List<Sources> sources) {

        if (sources.size() == 0 && sourceTitle.length() != 0) {
            author.setText(R.string.add_author_phrase);
            selectedSourceID = 0;
            tableLayoutAuthors.addView(BuildTableLayout.setupAuthorsTableRow(this, tableLayoutAuthors,
                    "", "", "", "", false, true));
            tableLayoutAuthors.getChildAt(tableLayoutAuthors.getChildCount()-1).requestFocus();
            setZeroSourceDetails();

        } else if (sources.size() > 1){
            // sourceID is returned by the intentLauncher.startActivityForResult
            getCorrectAuthors(sources);

        } else if (sourceTitle.length() != 0) {
            author.setText(DBQueryTools.captureAuthorNewOrOldSource(sources.get(0)));
            setSourceDetails(sources.get(0));
            selectedSourceID = sources.get(0).getSourceID();
        }else if (sourceTitle.length() == 0){
            clearSourceDetails();
        }
    }

    private void getCorrectAuthors(List<Sources> sources) {
        sources.add(new Sources(0, "", "", 2021, 0, 0, "", "", ""));
        Intent a = new Intent(AddNote.this, AuthorPopup.class);
        a.putParcelableArrayListExtra("sources", new ArrayList<>(sources));
        intentLauncher.launch(a);
    }

    private void setCorrectSourceAuthor(Sources sourceAuthor){
        author.setText(DBQueryTools.concatenateAuthors(DBQueryTools.getAuthorsBySourceID(sourceAuthor.getSourceID())));
    }

    private void setSourceDetails(Sources src){
        if(src.getSourceID()!=0 && sourceTitle.length() !=0) {
            setCorrectSourceAuthor(src);
            date.setText(DBQueryTools.concatenateDate(String.valueOf(src.getMonth()), String.valueOf(src.getDay()), String.valueOf(src.getYear())));
            volume.setText(src.getVolume());
            edition.setText(src.getEdition());
            issue.setText(src.getIssue());
            sourceType.setSelection(getTypeSelection(src));
        }else{
            setZeroSourceDetails();
        }
    }

    private int getTypeSelection(Sources src){
        String[] res = getResources().getStringArray(R.array.types);
        int result = 0;
        for(int s = 0; s < res.length; s++){
            if(src.getSourceType().equals(res[s])) {
                result = s;
                break;
            }
        }
        return result;
    }

    private void setZeroSourceDetails(){
        author.setText(R.string.add_author_phrase);
        date.setText(null);
        volume.setText(null);
        edition.setText(null);
        issue.setText(null);
    }

    public boolean checkTableForMissingAuthors(TableLayout table){
        for (int i=1; i < table.getChildCount(); i++){
            TableRow tblRow = (TableRow) table.getChildAt(i);
            EditText fName = (EditText)tblRow.getChildAt(1);
            if(fName.getText().toString().equals("")) {
                return true;
            }
        }
        return false;
    }

    private void setLabelColor(TextView textView, int lenValue){
        if(lenValue==0){
            textView.setTextColor(ContextCompat.getColor(AddNote.this, R.color.colorRed));
        }else{
            textView.setTextColor(ContextCompat.getColor(AddNote.this, R.color.colorDkGray));
        }
    }

    private void clearRedLabels(){
        List<TextView> labels = new ArrayList<>(
                Arrays.asList(lblType, lblTopic, lblQuestion,lblSummary, lblComment, lblQuote, lblTerm, lblSource, lblAuthor, lblDate, lblTimestamp));
        for (TextView txtView: labels) {
            setLabelColor(txtView, 1);
        }
    }

    private boolean requiredFields(Boolean useMessageTF){
        String msg;
        clearRedLabels();

        if (topic.getText().toString().equals("")){
            setLabelColor(lblTopic, topic.length());
            msg = "Please select or enter a topic.";
        }else {
            if (sourceTitle.getText().toString().trim().equals("")) {
                setLabelColor(lblSource, sourceTitle.length());
                msg = "Please select an existing source or enter a title for a new source.";

            } else if (author.getText().toString().equals("")) {
                setLabelColor(lblAuthor, author.length());
                msg = "Please select an existing author or organization, or add a new author or organization.";

            }else if(author.getText().toString().equals("For new sources add new/existing author(s) below") && tableLayoutAuthors.getChildCount()==1){
                setLabelColor(lblAuthor, 0);
                msg = "Please enter a new or existing author in the Authors table below for new sources.";

            }else if(author.getText().toString().equals("For new sources add new/existing author(s) below") && tableLayoutAuthors.getChildCount()>1 &&
                    checkTableForMissingAuthors(tableLayoutAuthors)){
                    setLabelColor(lblAuthor, 0);
                    msg = "Please enter a new or existing author in the Authors table below for new sources.";

            } else if (sourceType.getSelectedItem().toString().equals("")) {
                setLabelColor(lblType, 0);
                msg = "Please select a source type.";

            } else if (sourceType.getSelectedItem().toString().equals("Question") && (question.getText().toString().isEmpty())) {
                setLabelColor(lblQuestion, 0);
                msg = "Please enter or select a question because 'Question' was selected as a source. A comment should expand on the meaning.";

            } else if (sourceType.getSelectedItem().toString().equals("Quote") && (quote.getText().toString().equals(""))) {
                setLabelColor(lblQuote, 0);
                msg = "Please enter a quote because 'Quote' was selected as a source. A comment should expand on the meaning.";

            } else if (sourceType.getSelectedItem().toString().equals("Term") && (term.getText().toString().equals(""))) {
                setLabelColor(lblTerm, 0);
                msg = "Please enter the term and definition. Expand within comments of other sources of interpretation.";

            } else if ((sourceType.getSelectedItem().toString().equals("Video") || sourceType.getSelectedItem().toString().equals("Audio")) && timeStamp.getText().toString().equals("")) {
                setLabelColor(lblTimestamp, 0);
                msg = "Please enter a TimeStamp value for an audio or video source.";

            } else if (comment.getText().toString().equals("")) {
                setLabelColor(lblComment, 0);
                msg = "Please enter a comment. Comments are specific details related to the topic and summary.";

            } else if (summary.getText().toString().equals("")) {
                setLabelColor(lblSummary, 0);
                msg = "Please enter a summary point. This expands upon your Topic entry or selection.";

            }else if(date.getText().toString().equals("")){
                setLabelColor(lblDate, 0);
                msg = "Please enter a date. Every source must have a date, if not, use the current year.";

            } else {
                return true;
            }
        }
        if(useMessageTF)
            PopupDialog.AlertMessageOK(AddNote.this, "Required Field", msg);
        return false;
    }

    private void clearFields() {
        sourceType.setSelection(0);
        topic.setText(null);
        question.setText(null);
        summary.setText(null);
        quote.setText(null);
        term.setText(null);
        sourceTitle.setText(null);
        comment.setText(null);
        clearSourceDetails();

    }

    private void clearSourceDetails(){
        author.setText(null);
        date.setText(null);
        volume.setText(null);
        edition.setText(null);
        issue.setText(null);
        pgs_paras.setText(null);
        timeStamp.setText(null);
        hyperlink.setText(null);
        if(tableLayoutFiles.getChildCount() > 1)
            deleteTableRows(tableLayoutFiles);
        if(tableLayoutAuthors.getChildCount() > 1)
            deleteTableRows(tableLayoutAuthors);
    }

    public static void deleteTableRows(TableLayout table){
        if(table.getChildCount() > 1){
            for (int i=table.getChildCount(); i > 0; i--){
                TableRow tblRow = (TableRow) table.getChildAt(i);
                table.removeView(tblRow);
            }
        }
    }
}
