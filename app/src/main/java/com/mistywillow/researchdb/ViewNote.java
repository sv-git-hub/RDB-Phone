package com.mistywillow.researchdb;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.*;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import com.mistywillow.researchdb.database.*;
import android.widget.*;
import com.mistywillow.researchdb.database.entities.Files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static androidx.core.content.FileProvider.getUriForFile;

public class ViewNote extends AppCompatActivity {
    ResearchDatabase researchDatabase;
    int nNoteID;

    List<String> noteDetails = new ArrayList<>();
    List<Files> noteFiles;

    TextView sourceType;
    TextView topic;
    TextView question;
    TextView quote;
    TextView term;
    TextView source;
    TextView authors;
    TextView summary;
    TextView comment;
    TextView hyperlink;

    TableLayout tableLayout;
    Menu viewMenu;

    List<byte[]> fileBytes;

    String nType = null;
    String nSummary = null;
    String nSource = null;
    String nAuthors = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getColor(R.color.colorWhite));
        setSupportActionBar(toolbar);

        sourceType = findViewById(R.id.viewType);
        topic = findViewById(R.id.viewTopic);
        question = findViewById(R.id.viewQuestion);
        source = findViewById(R.id.viewSource);
        authors = findViewById(R.id.viewAuthors);
        summary = findViewById(R.id.viewSummary);
        comment = findViewById(R.id.viewComment);
        hyperlink = findViewById(R.id.viewHyperlink);
        quote = findViewById(R.id.viewQuote);
        term = findViewById(R.id.viewTerm);

        tableLayout = findViewById(R.id.table_files);

        // PREVENTS KEYBOARD POPPING UP ON ACTIVITY LOAD
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // RECEIVE INTENT FROM NOTE ADAPTER PASSING SELECTED NOTE ID
        Intent n = getIntent();
        nNoteID = n.getIntExtra("ID", 0);
        nType = n.getStringExtra("Type");
        nSummary = n.getStringExtra("Summary");
        nSource = n.getStringExtra("Source");
        nAuthors = n.getStringExtra("Authors");

        researchDatabase = ResearchDatabase.getInstance(this, "Apologetic.db");

        NoteDetails details = researchDatabase.getNotesDao().getNoteDetails(nNoteID);
        noteDetails.clear();
        noteDetails = loadAllNoteDetails(nType, nSummary, nSource, nAuthors, details);
        populateFields();

        noteFiles = researchDatabase.getFilesByNoteDao().getFilesByNote(nNoteID);
        populateFileData(noteFiles);

        fileBytes = researchDatabase.getFilesByNoteDao().getFileData(nNoteID);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    // MENU METHODS
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.view_menu, menu);
        viewMenu = menu;
        setupMenuOptionsNotAvailable();
        return true;
    }

    private void setupMenuOptionsNotAvailable() {
        viewMenu.findItem(R.id.clear).setEnabled(false);
        viewMenu.findItem(R.id.add_note).setEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent menuIntent;
        if(item.getItemId() == R.id.clear) {
            //clearFields();
            Toast.makeText(this, "Clear Fields clicked!", Toast.LENGTH_SHORT).show();
        }else if(item.getItemId() == R.id.add_note) {
            Toast.makeText(this, "Add Note clicked!", Toast.LENGTH_SHORT).show();

        }else if(item.getItemId() == R.id.edit_note){
            menuIntent = new Intent(this, EditNote.class);
            menuIntent.putExtra("NoteID", nNoteID);
            menuIntent.putExtra("NoteDetails", new ArrayList<>(noteDetails));
            if(noteFiles.size() > 0)
                menuIntent.putParcelableArrayListExtra("NoteFiles", new ArrayList<>(noteFiles));
            startActivity(menuIntent);
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
        }

        return super.onOptionsItemSelected(item);
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

                //File filePaths = new File(getFilesDir().toString());
                File filePaths = new File(getFilesDir().toString() + "/note_files/");
                Log.d("File: filePaths", filePaths.getAbsolutePath());

                File newFile = new File(filePaths, result);
                Log.d("Files: newFile", newFile.getAbsolutePath());

                if(!newFile.exists()){
                    buildFiles(filePaths);
                }

                Uri contentUri = getUriForFile(getApplicationContext(), "com.mistywillow.fileprovider", newFile);
                Log.d("FIle: contentUri", Objects.requireNonNull(contentUri.getPath()));
                openFile(contentUri, mimeType);
            });
        }
        return row;
    }

    public static void checkFolderExists(Context context, String folder){
        File location = new File(context.getFilesDir() + "/" + folder);
        if(!location.exists()) {
            if(location.mkdir())
                Toast.makeText(context, "Directory " + folder + " was created!", Toast.LENGTH_SHORT).show();
        }
    }

    private void openFile(Uri uri, String mime){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri,mime);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    private void buildFiles(File location){
        int i = 0;
        for (Files f: noteFiles) {
            createFile(location.getAbsolutePath() + "/" + f.getFileName(), fileBytes.get(i));
            i++;
        }
    }

    private void createFile(String fileName, byte[] fileBytes){
        FileOutputStream fos = null;
        try {
            //fos = openFileOutput(fileName, MODE_PRIVATE);
            fos = new FileOutputStream(fileName);
            fos.write(fileBytes);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void populateFields() {
        sourceType.setText(noteDetails.get(0));
        topic.setText(noteDetails.get(17));
        question.setText(noteDetails.get(4));
        quote.setText(noteDetails.get(5));
        term.setText(noteDetails.get(6));
        source.setText(noteDetails.get(2));
        authors.setText(noteDetails.get(3));
        summary.setText(noteDetails.get(1));
        comment.setText(noteDetails.get(14));
        if(noteDetails.get(13).isEmpty()){
            hyperlink.setText(noteDetails.get(13));
        }else {
            hyperlink.setText(Html.fromHtml(buildHyperlink(noteDetails.get(13)), 0));
            hyperlink.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    private String buildHyperlink(String link) {
        return "<a href=\"" + link + "\">" + link + "</a> ";
    }

  private List<String> loadAllNoteDetails(String type, String summary, String source, String authors, NoteDetails noteDetails){
        List<String> nDetails = new ArrayList<>();
        nDetails.add(type);                         // 0: Type
        nDetails.add(summary);                      // 1: Summary
        nDetails.add(source);                       // 2: Source
        nDetails.add(authors);                      // 3: Author(s)

        nDetails.add(noteDetails.getQuestion());    // 4: Question
        nDetails.add(noteDetails.getQuote());       // 5: Quote
        nDetails.add(noteDetails.getTerm());        // 6: Term
        nDetails.add(noteDetails.getYear());        // 7: Year
        nDetails.add(noteDetails.getMonth());       // 8: Month
        nDetails.add(noteDetails.getDay());         // 9: Day
        nDetails.add(noteDetails.getVolume());      // 10: Volume
        nDetails.add(noteDetails.getEdition());     // 11: Edition
        nDetails.add(noteDetails.getIssue());       // 12: Issue
        nDetails.add(noteDetails.getHyperlink());   // 13: Hyperlink
        nDetails.add(noteDetails.getComment());     // 14: Comment
        nDetails.add(noteDetails.getPage());        // 15: Page
        nDetails.add(noteDetails.getTimeStamp());   // 16: TimeStamp
        nDetails.add(noteDetails.getTopic());       // 17: Topic
        return nDetails;
    }

    private void populateFileData(List<Files> files){
        tableLayout.addView(setupTableRow("FileID", "FileName", true));
        if(files.size() > 0){
            for (Files f: files) {
                tableLayout.addView(setupTableRow(String.valueOf(f.getFileID()), f.getFileName(), false));
            }
        }
    }

    private String getFileExtension(String fileName){
        String[] ext = fileName.split("[.]");
        return ext[ext.length-1];
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

    @Override
    public void onBackPressed() {
          super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        int REQUEST_CODE = 1;
        if(requestCode == REQUEST_CODE){
            if(resultCode == RESULT_OK) {
                nNoteID = data.getIntExtra("ID", 0);
                nType = data.getStringExtra("Type");
                nSummary = data.getStringExtra("Summary");
                nSource = data.getStringExtra("Source");
                nAuthors = data.getStringExtra("Authors");
            }
        }
    }
}
