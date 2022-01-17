package com.mistywillow.researchdb;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.*;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import com.mistywillow.researchdb.databases.ResearchDatabase;
import android.widget.*;
import com.mistywillow.researchdb.researchdb.entities.Files;
import com.mistywillow.researchdb.researchdb.entities.Notes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import static androidx.core.content.FileProvider.getUriForFile;

public class ViewNote extends AppCompatActivity {
    ResearchDatabase rdb;
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
    TextView date;
    TextView volume;
    TextView edition;
    TextView issue;
    TextView pgs_paras;
    TextView timeStamp;

    TableLayout tableLayout;
    Menu viewMenu;

    List<byte[]> fileBytes;

    String nType = null;
    String nSummary = null;
    String nSource = null;
    String nAuthors = null;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getColor(R.color.colorWhite));
        setSupportActionBar(toolbar);

        TextView page = findViewById(R.id.toolbar_page);
        page.setText(" VIEW NOTE\r\n" + Globals.DATABASE);

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
        date = findViewById(R.id.viewDate);
        volume = findViewById(R.id.viewVolume);
        edition = findViewById(R.id.viewEdition);
        issue = findViewById(R.id.viewIssue);
        pgs_paras = findViewById(R.id.viewPgsParas);
        timeStamp = findViewById(R.id.viewTimeStamp);


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

        rdb = ResearchDatabase.getInstance(this, Globals.DATABASE);

        NoteDetails details = rdb.getNotesDao().getNoteDetails(nNoteID);
        noteDetails.clear();
        noteDetails = loadAllNoteDetails(nType, nSummary, nSource, nAuthors, details);
        populateFields();

        noteFiles = rdb.getFilesByNoteDao().getFilesByNote(nNoteID);
        populateFileData(noteFiles);

        fileBytes = rdb.getFilesByNoteDao().getFileData(nNoteID);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    // MENU METHODS

    @Override
    public boolean onMenuOpened(int featureId, Menu menu){
        super.onMenuOpened(R.menu.view_menu, viewMenu);
        Notes notes = rdb.getNotesDao().getNote(nNoteID);
        if(notes.getDeleted() == 0) {
            viewMenu.findItem(R.id.mark_for_delete).setEnabled(true);
            viewMenu.findItem(R.id.unMark_for_delete).setEnabled(false);
            viewMenu.findItem(R.id.permanently_delete).setEnabled(false);
        }else{
            viewMenu.findItem(R.id.mark_for_delete).setEnabled(false);
            viewMenu.findItem(R.id.unMark_for_delete).setEnabled(true);
            viewMenu.findItem(R.id.permanently_delete).setEnabled(true);
        }
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.view_menu, menu);
        viewMenu = menu;
        setupMenuOptionsNotAvailable();
        return true;
    }

    private void setupMenuOptionsNotAvailable() {
        Notes notes = rdb.getNotesDao().getNote(nNoteID);
        if(notes.getDeleted() == 0) {
            viewMenu.findItem(R.id.mark_for_delete).setEnabled(true);
            viewMenu.findItem(R.id.unMark_for_delete).setEnabled(false);
        }else{
            viewMenu.findItem(R.id.mark_for_delete).setEnabled(false);
            viewMenu.findItem(R.id.unMark_for_delete).setEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent menuIntent;
        if(item.getItemId() == R.id.edit_note){
            menuIntent = new Intent(this, EditNote.class);
            menuIntent.putExtra("NoteID", nNoteID);
            menuIntent.putExtra("NoteDetails", new ArrayList<>(noteDetails));

            if(noteFiles.size() > 0)
                menuIntent.putParcelableArrayListExtra("NoteFiles", new ArrayList<>(noteFiles));
            startActivity(menuIntent);

        }else if(item.getItemId() == R.id.export_note) {
            exportToXML(ExportNoteToXML.exportNote(this, nNoteID, noteDetails, noteFiles));


        }else if(item.getItemId() == R.id.mark_for_delete) {
            rdb.getNotesDao().markNoteToDelete(nNoteID);
            PopupDialog.AlertMessageOK(this, "Note Marked to Delete",
                    "You have marked this note for deletion only and has not been permanently deleted to prevent " +
                            "accidental deletion. You can select 'Permanently Delete Note' now, review it for deletion " +
                            "or restore it by un-marking it. It will not be listed in search results unless restored. The " +
                            "topic associated with this note may return blank unless other notes related notes exist.");

        }else if(item.getItemId() == R.id.unMark_for_delete) {
            rdb.getNotesDao().unMarkNoteToDelete(nNoteID);
            PopupDialog.AlertMessageOK(this, "Note Unmarked to Delete",
                    "You have unmarked this note for deletion.");

        }else if(item.getItemId() == R.id.permanently_delete) {
            DBQueryTools.deleteNote(ViewNote.this, nNoteID);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void exportToXML(HashMap<String, Object[]> allNotes){
        HashMap<Integer, HashMap<String, Object[]>> notes = new HashMap<>();
        notes.put(nNoteID, new HashMap<>(allNotes));
        String timeStamp = DateTimestampManager.currentTimestamp();
        new CreateXMLFileDOMParser(Globals.DATABASE + "_notes_" + timeStamp + ".xml", notes);
        PopupDialog.AlertMessageOK(this, "Export Successful", "The note was exported successfully!");
    }

    /** Cache references used creating to and reading from cache
     * https://c1ctech.com/android-read-and-write-internal-storage-example/
     * https://stackoverflow.com/questions/23830157/open-file-in-cache-directory-with-action-view*/
    private TableRow setupTableRow(String fileID, String fileName, boolean bold) {

        TableRow row = new TableRow(this);
        row.addView(setupRowTextView(fileID, bold));
        row.addView(setupRowTextView(fileName, bold));
        row.setClickable(true);
        if (!bold) {
            row.setOnClickListener(v -> {

                TableRow tablerow = (TableRow) v;
                TextView sample = (TextView) tablerow.getChildAt(1);
                TextView num = (TextView) tablerow.getChildAt(0);
                int fID = Integer.parseInt(num.getText().toString());
                String result = sample.getText().toString();

                Files nFile = rdb.getFilesDao().getFile(fID);

                MimeTypeMap myMime = MimeTypeMap.getSingleton();
                String mimeType = myMime.getMimeTypeFromExtension(getFileExtension(nFile.getFileName()));

                try {

                    checkFolderExists(this, "note_files");
                    File filePaths = new File(getCacheDir().toString() + "/note_files/");

                    File newFile = new File(filePaths, result);
                    if(!newFile.exists()){
                        buildFile(filePaths, nFile);
                    }

                    Uri contentUri = getUriForFile(ViewNote.this, "com.mistywillow.fileprovider", newFile);
                    openFile(contentUri, mimeType);

                }catch(Exception e){
                    e.printStackTrace();
                    PopupDialog.AlertMessageOK(this, "Open File Failed", "Attachment failed to open " +
                            "or the temporary cache directory couldn't be created.");
                }

            });
        }
        return row;
    }

    public static void checkFolderExists(Context context, String folder){
        File location = new File(context.getCacheDir() + "/" + folder);
        if(!location.exists()) location.mkdir();
    }

    private void openFile(Uri uri, String mime){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri,mime);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    private void buildFile(File fPath, Files file){
        createFile(fPath.getAbsolutePath() + "/" + file.getFileName(), file.getFileData());
    }

    private void createFile(String fileName, byte[] fileBytes){
        FileOutputStream fos = null;
        try {
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

    private String getFileExtension(String fileName){
        String[] ext = fileName.split("[.]");
        return ext[ext.length-1];
    }

    private void populateFields() {
        sourceType.setText(noteDetails.get(Globals.TYPE));
        topic.setText(noteDetails.get(Globals.TOPIC));
        question.setText(noteDetails.get(Globals.QUESTION));
        summary.setText(noteDetails.get(Globals.SUMMARY));
        quote.setText(noteDetails.get(Globals.QUOTE));
        term.setText(noteDetails.get(Globals.TERM));
        source.setText(noteDetails.get(Globals.SOURCE));
        authors.setText(noteDetails.get(Globals.AUTHORS));
        comment.setText(noteDetails.get(Globals.COMMENT));
        date.setText(DBQueryTools.concatenateDate(noteDetails.get(Globals.MONTH), noteDetails.get(Globals.DAY), noteDetails.get(Globals.YEAR)));
        volume.setText(noteDetails.get(Globals.VOLUME));
        edition.setText(noteDetails.get(Globals.EDITION));
        issue.setText(noteDetails.get(Globals.ISSUE));
        pgs_paras.setText(noteDetails.get(Globals.PAGE));
        timeStamp.setText(noteDetails.get(Globals.TIMESTAMP));
        if(noteDetails.get(13).isEmpty()){
            hyperlink.setText(noteDetails.get(Globals.HYPERLINK));
        }else {
            hyperlink.setText(Html.fromHtml(buildHyperlink(noteDetails.get(Globals.HYPERLINK)), 0));
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
        if(files.size() > 0){
            if(tableLayout.getChildCount() == 1) tableLayout.removeView(tableLayout.getChildAt(0));
            tableLayout.addView(setupTableRow("FileID", "FileName", true));
            for (Files f: files) {
                tableLayout.addView(setupTableRow(String.valueOf(f.getFileID()), f.getFileName(), false));
            }
        }else{
            tableLayout.addView(setupTableRow("", "No Attachments", true));
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if(DBQueryTools.countTopic(rdb.getTopicsDao().getTopicID(topic.getText().toString())) == 1){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }else {
            super.onBackPressed();
        }
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
