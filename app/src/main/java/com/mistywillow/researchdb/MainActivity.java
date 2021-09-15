package com.mistywillow.researchdb;

import android.content.Intent;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.sqlite.db.SimpleSQLiteQuery;
import com.mistywillow.researchdb.database.ResearchDatabase;
import java.io.File;
import java.util.*;

public class MainActivity extends AppCompatActivity {
    private AutoCompleteTextView topic;
    private AutoCompleteTextView question;
    private RecyclerView rListNotes;
    private EditText customSearch;
    private ResearchDatabase researchDatabase;
    private Menu mainMenu;

    private List<Integer> noteIDsFromCustomSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppThemeLauncher);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getColor(R.color.colorWhite));
        setSupportActionBar(toolbar);

        topic = findViewById(R.id.listTopic);
        question = findViewById(R.id.listQuestion);
        customSearch = findViewById(R.id.txtCustom);
        rListNotes = findViewById(R.id.listNotes);

        // PREVENTS KEYBOARD POPPING UP ON ACTIVITY LOAD
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            // GET ASSETS
            //CopyAssets.copyAssets(this, "user_log", "userInfo.txt");
            //CopyAssets.copyAssets(this, "databases", "Apologetic.db");

            // MY DATABASE:
            researchDatabase = ResearchDatabase.getInstance(this, GlobalFilePathVariables.DATABASE);

            // TOPIC LIST
            ArrayAdapter<String> topicsAdapter = DBQueryTools.captureDBTopics(this);
            topic.setAdapter(topicsAdapter);
            topic.setOnItemClickListener((parent, view, position, id) -> {
                if(!topic.getText().toString().equals("")) {
                    question.setText("");
                    rListNotes.setAdapter(null);
                    customSearch.setText(null);
                    loadNotes(captureNotes(researchDatabase.getNotesDao().getNotesOnTopic(topic.getText().toString())));
                }
            });

            // QUESTION LIST
            ArrayAdapter<String> acQuestionAdapt = DBQueryTools.captureDBQuestions(this);
            //question.setThreshold(1);
            question.setAdapter(acQuestionAdapt);
            question.setOnItemClickListener((parent, view, position, id) -> {
                if(!question.getText().toString().equals("")) {
                    topic.setText("");
                    rListNotes.setAdapter(null);
                    customSearch.setText(null);
                    loadNotes(captureNotes(researchDatabase.getNotesDao().getNotesOnQuestion(question.getText().toString())));
                }
            });

            // CUSTOM SEARCH
            customSearch.setOnEditorActionListener((v, actionId, event) -> {
                if(actionId == EditorInfo.IME_ACTION_DONE)
                    if(!customSearch.getText().toString().trim().equals("")){
                        noteIDsFromCustomSearch = new ArrayList<>();
                        rListNotes.setAdapter(null);
                        completeSearch(false, customSearch.getText().toString());
                        loadNotes(captureNotes(researchDatabase.getNotesDao().getAllNotesOnNoteIDs(noteIDsFromCustomSearch)));
                        Toast.makeText(getApplicationContext(), String.valueOf(noteIDsFromCustomSearch.size()), Toast.LENGTH_SHORT).show();
                    }
                return false;
            });
            customSearch.setOnKeyListener((v, keyCode, event) -> {
                topic.setText(null);
                question.setText(null);
                rListNotes.setAdapter(null);
                return false;
            });
    }

    @Override
    public void onResume(){
        super.onResume();
        if(!topic.getText().toString().equals("")) {
            rListNotes.setAdapter(null);
            customSearch.setText(null);
            loadNotes(captureNotes(researchDatabase.getNotesDao().getNotesOnTopic(topic.getText().toString())));
        }
        if(!question.getText().toString().equals("")) {
            rListNotes.setAdapter(null);
            customSearch.setText(null);
            loadNotes(captureNotes(researchDatabase.getNotesDao().getNotesOnQuestion(question.getText().toString())));
        }
        if(!customSearch.getText().toString().trim().equals("")){
            noteIDsFromCustomSearch = new ArrayList<>();
            rListNotes.setAdapter(null);
            completeSearch(false, customSearch.getText().toString());
            loadNotes(captureNotes(researchDatabase.getNotesDao().getAllNotesOnNoteIDs(noteIDsFromCustomSearch)));
        }
    }

    // MENU METHODS
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        mainMenu = menu;
        setupMenuOptionsNotAvailable();
        return true;
    }

    private void setupMenuOptionsNotAvailable() {
        mainMenu.findItem(R.id.clear).setEnabled(false);
        mainMenu.findItem(R.id.main_print).setEnabled(false);
        mainMenu.findItem(R.id.edit_note).setEnabled(false);
        mainMenu.findItem(R.id.mark_for_delete).setEnabled(false);
        mainMenu.findItem(R.id.unMark_for_delete).setEnabled(false);
        mainMenu.findItem(R.id.permanently_delete).setEnabled(false);
        mainMenu.findItem(R.id.note_export).setEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.clear) {
            clearFields();
            mainMenu.findItem(R.id.clear).setEnabled(false);
            mainMenu.findItem(R.id.note_export).setEnabled(false);


        }else if(item.getItemId() == R.id.add_note) {
            Intent launchAdd = new Intent(this, AddNote.class);
            startActivity(launchAdd);

        }else if(item.getItemId() == R.id.edit_note){
            Toast.makeText(this, "Edit Note for Delete clicked!", Toast.LENGTH_SHORT).show();
        }else if(item.getItemId() == R.id.mark_for_delete) {
            Toast.makeText(this, "Mark Note for Delete clicked!", Toast.LENGTH_SHORT).show();
        }else if(item.getItemId() == R.id.unMark_for_delete) {
            Toast.makeText(this, "Un-mark Note for Delete clicked!", Toast.LENGTH_SHORT).show();
        }else if(item.getItemId() == R.id.review_for_delete) {
            Toast.makeText(this, "Review Notes for Delete clicked!", Toast.LENGTH_SHORT).show();
        }else if(item.getItemId() == R.id.permanently_delete) {
            Toast.makeText(this, "Permanently Delete Note clicked!", Toast.LENGTH_SHORT).show();
        }else if(item.getItemId() == R.id.delete_database) {
            Toast.makeText(this, "Delete Database clicked!", Toast.LENGTH_SHORT).show();
        }else if(item.getItemId() == R.id.main_close) {
            finishAndRemoveTask();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        deleteNoteFiles();
        super.onDestroy();
    }

    private void deleteNoteFiles() {
        boolean tf;
        File file = new File(getFilesDir() + "/note_files");
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        tf = f.delete();
                        if(tf)
                            Toast.makeText(this, f.getName() + " was deleted!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    // CUSTOM METHODS

    public void clearFields(){
        topic.setText(null);
        question.setText(null);
        customSearch.setText(null);
        rListNotes.setAdapter(null);
    }

    private List<SourcesTable> captureNotes(List<SourcesTable> list){
        return list;
    }

    private void loadNotes(List<SourcesTable> sourcesTable){
        rListNotes.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        rListNotes.setAdapter(new NoteAdapter(MainActivity.this, sourcesTable));
        mainMenu.findItem(R.id.clear).setEnabled(true);
        mainMenu.findItem(R.id.note_export).setEnabled(true);
    }

    private void completeSearch(Boolean all, String criteria){
        captureCustomSearchNoteIDs("Sources", "SourceID", researchDatabase.getSourcesDao().customSearchSourcesTable(searchTable(all, criteria, "Sources", "SourceID", Collections.singletonList("Title"))));
        captureCustomSearchNoteIDs("Comments", "CommentID", researchDatabase.getCommentsDao().customSearchCommentsTable(searchTable(all, criteria,"Comments", "CommentID", Arrays.asList("Summary", "Comment", "Page", "TimeStamp","Hyperlink"))));
        captureCustomSearchNoteIDs("Questions", "QuestionID", researchDatabase.getQuestionsDao().customSearchQuestionsTable(searchTable(all, criteria, "Questions", "QuestionID", Collections.singletonList("Question"))));
        captureCustomSearchNoteIDs("Quotes", "QuoteID", researchDatabase.getQuotesDao().customSearchQuotesTable(searchTable(all, criteria, "Quotes", "QuoteID", Collections.singletonList("Quote"))));
        captureCustomSearchNoteIDs("Terms", "TermID", researchDatabase.getTermsDao().customSearchTermsTable(searchTable(all, criteria, "Terms", "TermID", Collections.singletonList("Term"))));
        captureCustomSearchNoteIDs("Topics", "TopicID", researchDatabase.getTopicsDao().customSearchTopicsTable(searchTable(all, criteria, "Topics", "TopicID", Collections.singletonList("Topic"))));
        captureCustomSearchNoteIDs("Files", "FileID", researchDatabase.getFilesDao().customSearchFilesTable(searchTable(all, criteria, "Files", "FileID", Collections.singletonList("FileName"))));
        captureCustomSearchNoteIDs("Authors", "AuthorID", researchDatabase.getTopicsDao().customSearchTopicsTable(searchTable(all, criteria, "Authors", "AuthorID", Arrays.asList("FirstName", "MiddleName", "LastName", "Suffix"))));
    }

    private SimpleSQLiteQuery searchTable(boolean all, String criteria, String table, String tableID, List<String> columnNames){
        String statement = "SELECT " + tableID + " FROM " + table + " WHERE" + searchColumns(all, criteria, columnNames);
        return new SimpleSQLiteQuery(statement);
    }

    private void captureCustomSearchNoteIDs(String table, String tableID, List<Integer> tableList){
        List<Integer> temp = new ArrayList<>();
        String query;
        for(Integer id : tableList) {
            if (table.equals("Files"))
                query = "SELECT NoteID FROM " + table + "_By_Note WHERE " + tableID + " = " + id;
            else if (table.equals("Authors"))
                query = "SELECT NoteID FROM Notes AS n " +
                        "LEFT JOIN Author_By_Source AS s " +
                        "WHERE " + id + " = s.AuthorID AND s.SourceID = n.SourceID";
            else
                query = "SELECT NoteID FROM Notes WHERE " + tableID + " = " + id;
            temp.addAll(researchDatabase.getNotesDao().getNotesOnCustomSearch(new SimpleSQLiteQuery(query)));
        }
        for (Integer i : temp) {
            if(!noteIDsFromCustomSearch.contains(i))
                noteIDsFromCustomSearch.add(i);
        }
    }

    // CONCATENATION METHODS
    private String searchColumns(boolean containsAll, String criteria, List<String> columns){

        StringBuilder sb = new StringBuilder();

        for(String str : columns){
            if(columns.indexOf(str) == 0)
                sb.append(searchWords(containsAll, str, criteria));
            else
                sb.append(" OR ").append(searchWords(containsAll, str, criteria));
        }
        return sb.toString();
    }

    private String searchWords(boolean containsAll, String column, String words){
        StringBuilder sb = new StringBuilder();
        List<String> word = Arrays.asList(words.split(" "));
        String operator = " OR ";
        if(containsAll)
            operator = " AND ";

        for(String str : word){
            if(word.indexOf(str) == 0)
                sb.append(" ").append(column).append(" LIKE '%").append(str.replace("'", "''")).append("%'");
            else
                sb.append(operator).append(column).append(" LIKE '%").append(str.replace("'", "''")).append("%'");
        }
        return sb.toString();
    }
}