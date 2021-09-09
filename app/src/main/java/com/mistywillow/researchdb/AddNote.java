package com.mistywillow.researchdb;

import android.app.Activity;
import android.content.Intent;
import android.view.*;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import com.mistywillow.researchdb.database.ResearchDatabase;
import com.mistywillow.researchdb.database.entities.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddNote extends AppCompatActivity {
    private ResearchDatabase rdb;
    ActivityResultLauncher<Intent> intentLauncher;

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

    private TableLayout tableLayoutFiles;
    private TableLayout tableLayoutAuthors;
    private Menu editMenu;

    private int selectedSourceID;

    private List<String> newNoteDetails;
    private List<Integer> newSourcesAuthorIDs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getColor(R.color.colorWhite));
        setSupportActionBar(toolbar);

        // AUTOCOMPLETE TEXT VIEWS
        sourceType = findViewById(R.id.viewType);
        sourceTitle = findViewById(R.id.viewSource);
        author = findViewById(R.id.viewAuthors);
        topic = findViewById(R.id.viewTopic);
        question = findViewById(R.id.viewQuestion);
        summary = findViewById(R.id.viewSummary);

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

        tableLayoutFiles = findViewById(R.id.table_files);
        tableLayoutFiles.addView(BuildTableLayout.setupFilesTableRow(this,tableLayoutFiles,"FileID", "FileName",true));
        tableLayoutAuthors = findViewById(R.id.table_authors);
        tableLayoutAuthors.addView(BuildTableLayout.setupAuthorsTableRow(this,tableLayoutAuthors,"Organization/First", "Middle", "Last", "Suffix", true));

        //ARRAYS, LISTS, ETC.
        selectedSourceID = 0;
        newSourcesAuthorIDs = new ArrayList<>();

        // GET DATABASE INSTANCE
        rdb = ResearchDatabase.getInstance(this, GlobalVariables.DATABASE);
        intentLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result ->{
                    if(result.getResultCode() == Activity.RESULT_OK) {
                        assert result.getData() != null;
                        Sources src = result.getData().getParcelableExtra("source");
                        if(src == null){
                            PopupDialog.AlertMessage(AddNote.this, "Error: Author Choice",
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


        // BACK BUTTON <-
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    private void setupOnClickActions() {
        sourceTitle.setOnItemClickListener((parent, view, position, id) ->
                populateSourceDetails(DBQueryTools.getSourcesByTitle(sourceTitle.getText().toString())));

        sourceTitle.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus)
                populateSourceDetails(DBQueryTools.getSourcesByTitle(sourceTitle.getText().toString()));
        });

        tableLayoutAuthors.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if(sourceTitle.getText().length() !=0 && !sourceTitle.hasFocus()){
                if (tableLayoutAuthors.getChildCount() > 1) {
                    author.setText(R.string.add_author_phrase);
                }else if(tableLayoutAuthors.getChildCount() == 1 && bottom != oldBottom){
                    populateSourceDetails(DBQueryTools.getSourcesByTitle(sourceTitle.getText().toString()));
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
        if(item.getItemId() == R.id.clear){
            //clearFields();
            Toast.makeText(this, String.valueOf(editMenu.size()), Toast.LENGTH_SHORT).show();

        }else if(item.getItemId() == R.id.update_note) {
            int srcID;
            if (!requiredFields()) {
                return false;
            }
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

            startActivity(DBQueryTools.addNewNote(this, newNoteIDs));

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

    private void loadSpinner(){
        ArrayAdapter<String> sourceTypeAdapter = DBQueryTools.captureSourceTypes(this, "custom");
        sourceType.setAdapter(sourceTypeAdapter);
    }

    private void loadAutoCompleteTextViews(){
        ArrayAdapter<String> sourceTitleAdapter = DBQueryTools.captureDBSources(this);
        sourceTitle.setThreshold(1);
        sourceTitle.setAdapter(sourceTitleAdapter);

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

    private void captureNoteDetails(){
        String[] parseDate= DateTimestampManager.parseDate(date);
        newNoteDetails = new ArrayList<>();
        newNoteDetails.add(sourceType.getSelectedItem().toString()); // 0: Type
        newNoteDetails.add(summary.getText().toString());       // 1: Summary
        newNoteDetails.add(sourceTitle.getText().toString());   // 2: Source
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
        /*if(sources.get(0).getTitle().equals("The Case for Christianity"))
            sources.add(new Sources(43, "Book", "The Case for Christianity", 2021, 0, 0, "", "", ""));*/

        if (sources.size() == 0) {
            author.setText(R.string.add_author_phrase);
            selectedSourceID = 0;
            setZeroSourceDetails();

        } else if (sources.size() > 1){
            // sourceID is returned by the intentLauncher.startActivityForResult
            getCorrectAuthors(sources);

        } else {
            author.setText(DBQueryTools.captureAuthorNewOrOldSource(sources.get(0)));
            setSourceDetails(sources.get(0));
            selectedSourceID = sources.get(0).getSourceID();
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
        if(src.getSourceID()!=0) {
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

    private List<Files> captureFiles(){
        List<Files> files = new ArrayList<>();
        return files;
    }

    private boolean requiredFields(){
        String msg;

        if (topic.getText().toString().equals("")){
            msg = "Please select or enter a topic.";
        }else {
            if (sourceTitle.getText().toString().equals("")) {
                msg = "Please select an existing source or enter a title for a new source.";

            } else if (author.getText().toString().equals("")) {
                msg = "Please select an existing author or organization, or add a new author or organization.";

            }else if(author.getText().toString().equals("For new sources add new/existing author(s) below") && tableLayoutAuthors.getChildCount()==1){
                msg = "Please enter a new or existing author in the Authors table below for new sources.";

            } else if (sourceType.getSelectedItem().toString().equals("")) {
                msg = "Please select a source type.";

            } else if (sourceType.getSelectedItem().toString().equals("Question") && (question.getText().toString().isEmpty())) {
                msg = "Please enter or select a question because 'Question' was selected as a source. A comment should expand on the meaning.";

            } else if (sourceType.getSelectedItem().toString().equals("Quote") && (quote.getText().toString().equals(""))) {
                msg = "Please enter a quote because 'Quote' was selected as a source. A comment should expand on the meaning.";

            } else if (sourceType.getSelectedItem().toString().equals("Term") && (term.getText().toString().equals(""))) {
                msg = "Please enter the term and definition. Expand within comments of other sources of interpretation.";

            } else if ((sourceType.getSelectedItem().toString().equals("Video") || sourceType.getSelectedItem().toString().equals("Audio")) && timeStamp.getText().toString().equals("")) {
                msg = "Please enter a TimeStamp value for an audio or video source.";

            } else if (comment.getText().toString().equals("")) {
                msg = "Please enter a comment. Comments are specific details related to the topic and summary.";

            } else if (summary.getText().toString().equals("")) {
                msg = "Please enter a summary point. This expands upon your Topic entry or selection.";

            }else if(date.getText().toString().equals("")){
                msg = "Please enter a date. Every source must have a date, if not, use the current year.";

            } else {
                return true;
            }
        }
        PopupDialog.AlertMessage(AddNote.this, "Required Field", msg);
        return false;
    }

}
