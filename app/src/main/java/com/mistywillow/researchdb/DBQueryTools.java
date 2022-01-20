package com.mistywillow.researchdb;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.*;
import androidx.sqlite.db.SimpleSQLiteQuery;
import com.mistywillow.researchdb.databases.ResearchDatabase;
import com.mistywillow.researchdb.researchdb.entities.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.*;

public class DBQueryTools {



    private static ResearchDatabase rdb = null;

    // METHODS PERTAINING TO AUTHORS
    public static String concatenateAuthors(List<Authors> authors){
        StringBuilder str = new StringBuilder();
        for(int i = 0 ; i < authors.size(); i++){
            str.append(authors.get(i).getFirstName().trim());
            if(!authors.get(i).getMiddleName().isEmpty())
                str.append(" ").append(authors.get(i).getMiddleName().trim());
            if(!authors.get(i).getLastName().isEmpty())
                str.append(" ").append(authors.get(i).getLastName().trim());
            if(!authors.get(i).getSuffix().isEmpty())
                str.append(" ").append(authors.get(i).getSuffix().trim());
            if(authors.size()> 1 && (i+1) < authors.size())
                str.append("; ");
        }
        return str.toString().trim();
    }
    public static List<Authors> captureAuthorsTable(TableLayout authorsTable){
        List<Authors> authors = new ArrayList<>();
        for(int i = 1; i < authorsTable.getChildCount(); i++){
            View child = authorsTable.getChildAt(i);
            if(child instanceof TableRow){
                TableRow row = (TableRow) child;
                authors.add(new Authors(0, ((EditText) row.getChildAt(1)).getText().toString(),
                        ((EditText) row.getChildAt(2)).getText().toString(),
                        ((EditText) row.getChildAt(3)).getText().toString(),
                        ((EditText) row.getChildAt(4)).getText().toString()));
            }
        }
        return authors;
    }
    public static Integer findAuthor(Authors author) {
        List<Authors> all = rdb.getAuthorsDao().getAuthors();
        for (Authors authors1 : all) {
            if(author.getFirstName().toUpperCase().matches(authors1.getFirstName().toUpperCase()) &&
                    author.getMiddleName().toUpperCase().matches(authors1.getMiddleName().toUpperCase()) &&
                    author.getLastName().toUpperCase().matches(authors1.getLastName().toUpperCase()) &&
                    author.getSuffix().toUpperCase().matches(authors1.getSuffix().toUpperCase())){
                return authors1.getAuthorID();
            }
        }
        return 0;
    }
    public static Integer addNewAuthor(Authors newAuthor){
        return (int) rdb.getAuthorsDao().addAuthor(newAuthor);
    }
    public static List<Authors> getAuthorsBySource(Sources source){
        return getAuthorsBySourceID(source.getSourceID());
    }
    public static List<Authors> getAuthorsBySourceID(int srcID){
        return rdb.getAuthorBySourceDao().getAuthorsForSource(srcID);
    }
    public static String captureAuthorNewOrOldSource(Sources source){
        List<Authors> newAuthors = getAuthorsBySource(source);
        return DBQueryTools.concatenateAuthors(newAuthors);
    }

    // METHODS PERTAINING TO SOURCES
    public static Integer addNewSource(Sources src){
        return (int) rdb.getSourcesDao().addSource(src);
    }
    public static List<Sources> getSourcesByTitle(String sourceTitle){
        return rdb.getSourcesDao().getSourceByTitle(sourceTitle);
    }

    // CAPTURE METHODS FOR LOADING SPINNERS IN EditNote and AddNote classes
    public static ArrayAdapter<String>captureSourceTypes(Context context, String layout){
        List<String> orgSourceTypes = new ArrayList<>(Arrays.asList("","Article", "Audio", "Book", "Journal", "Periodical", "Question", "Quote", "Term", "Video", "Website", "Other"));
        if(layout.equals("simple"))
            return new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, orgSourceTypes);
        else
            return new ArrayAdapter<>(context, R.layout.custom_dropdown_list, orgSourceTypes);
    }
    public static ArrayAdapter<String> captureDBSources(Context context){
        rdb = ResearchDatabase.getInstance(context, Globals.DATABASE);
        List<Sources> sources = rdb.getSourcesDao().getSources();
        List<String> orgSourceTitles = new ArrayList<>();
        for(Sources s : sources){
            if(!orgSourceTitles.contains(s.getTitle()))
                orgSourceTitles.add(s.getTitle());
        }
        return new ArrayAdapter<>(context, R.layout.custom_dropdown_list, orgSourceTitles);
    }
    public static ArrayAdapter<String> captureDBTopics(Context context){
        rdb = ResearchDatabase.getInstance(context, Globals.DATABASE);
        List<Topics> topics = rdb.getTopicsDao().getTopics();
        List<String> orgTopics = new ArrayList<>();
        for(Topics t : topics){
            orgTopics.add(t.getTopic());
        }
        return new ArrayAdapter<>(context, R.layout.custom_dropdown_list, orgTopics);
        //topic.setAdapter(topicsAdapter);
    }
    public static ArrayAdapter<String> captureDBQuestions(Context context){
        rdb = ResearchDatabase.getInstance(context, Globals.DATABASE);
        List<Questions> questions = rdb.getQuestionsDao().getQuestions();
        List<String> orgQuestions = new ArrayList<>();
        for(Questions q : questions){
            orgQuestions.add(q.getQuestion());
        }
        return new ArrayAdapter<>(context, R.layout.custom_drowdown_list_wrap, orgQuestions);
    }
    public static ArrayAdapter<String> captureSummaries(Context context){
        rdb = ResearchDatabase.getInstance(context, Globals.DATABASE);
        List<Comments> summaries = rdb.getCommentsDao().getComments();
        List<String> orgSummaries = new ArrayList<>();
        for(Comments c : summaries){
            if(!c.getSummary().isEmpty() && !orgSummaries.contains(c.getSummary().trim()))
                orgSummaries.add(c.getSummary().trim());
        }
        return new ArrayAdapter<>(context, R.layout.custom_dropdown_list, orgSummaries);
    }

    public static List<Files> captureNoteFiles(Context context, List<Files> curFiles, TableLayout table, HashMap<String, Uri>fileURIs){
        List<Files> noteFiles = new ArrayList<>();
        int i = table.getChildCount();
        if(i>1){
            for (int itr = 1; itr<i; itr++) { // iterating through indexes
                TableRow tr = (TableRow) table.getChildAt(itr);
                TextView tv = (TextView) tr.getChildAt(1); // 1 is the file path position
                // Check if file exists by ID, if so, this is an update, add it to the new file list
                if(curFiles != null && !tv.getText().toString().contains("/")){
                    for (Files cur : curFiles){
                        if(cur.getFileName().equals(tv.getText().toString())) {
                            noteFiles.add(cur);
                            //noteFiles.add(rdb.getFilesDao().getFile(cur.getFileID())); // add current files to keep
                        }
                    }
                }else{
                    File f = new File(tv.getText().toString());
                    String n = f.getName();
                    try {
                        InputStream fis = context.getContentResolver().openInputStream(fileURIs.get(f.getPath()));
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        byte[] buf = new byte[1024];
                        for (int read; (read = fis.read(buf)) != -1; ) {
                            bos.write(buf, 0, read);
                        }
                        fis.close();

                        noteFiles.add(new Files(0, n, bos.toByteArray()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }
        return noteFiles;
    }

        // METHODS TO CAPTURE NEW NOTE IDS
    public static Integer getCommentID(List<String> data){
        Comments comment = new Comments(0, data.get(Globals.SUMMARY), data.get(Globals.COMMENT),
                data.get(Globals.PAGE), data.get(Globals.TIMESTAMP), data.get(Globals.HYPERLINK));
        return (int) rdb.getCommentsDao().addComment(comment);
    }
    public static Integer getQuestionID(List<String> data){
        int queID = rdb.getQuestionsDao().getQuestionByValue(data.get(Globals.QUESTION));
        if((!data.get(Globals.QUESTION).isEmpty() || !data.get(Globals.QUESTION).matches("")) && queID == 0) {
            Questions questions = new Questions(data.get(Globals.QUESTION));
            queID = (int) rdb.getQuestionsDao().addQuestion(questions);
        }
        return queID;}
    public static Integer getQuoteID(List<String> data){
        int quoID = rdb.getQuotesDao().getQuoteByValue(data.get(Globals.QUOTE));
        if((!data.get(Globals.QUOTE).isEmpty() || !data.get(Globals.QUOTE).matches("")) && quoID == 0) {
            Quotes quote = new Quotes(0, data.get(Globals.QUOTE));
            quoID = (int) rdb.getQuotesDao().addQuote(quote);
        }
        return quoID;
    }
    public static Integer getTermID(List<String> data){
        int terID = rdb.getTermsDao().getTermByValue(data.get(Globals.TERM));
        if((!data.get(Globals.TERM).isEmpty() || !data.get(Globals.TERM).matches("")) && terID == 0) {
            Terms term = new Terms(terID, data.get(Globals.TERM));
            terID = (int) rdb.getTermsDao().addTerm(term);
        }
        return terID;}
    public static Integer getTopicID(List<String> data){
        int topID = rdb.getTopicsDao().getTopicID(data.get(Globals.TOPIC));
        if((!data.get(Globals.TOPIC).isEmpty() || !data.get(Globals.TOPIC).matches("")) && topID == 0) {
            Topics topic = new Topics(topID, data.get(Globals.TOPIC));
            topID = (int) rdb.getTopicsDao().addTopic(topic);
        }
        return topID;}

        // METHODS TO MANAGE TABLE DATA ENTRY AND UPDATES EFFICIENTLY
    private static void updateCurrentNoteIDs(Notes note){
        rdb.getNotesDao().updateNote(note);
    }
    private static void updateQuestion(Notes thisNote, List<String> update){
        Questions questions = null;
        Questions deleteThis;
        int orgQuestionID = thisNote.getQuestionID();
        int orgQuestionIDCount = 0;
        int newQuestionCount = rdb.getQuestionsDao().getCountByValue(update.get(Globals.QUESTION));

        if(thisNote.getQuestionID() != 0) {
            orgQuestionIDCount = rdb.getNotesDao().countQuestionByID(thisNote.getQuestionID());
            questions = rdb.getQuestionsDao().getQuestion(thisNote.getQuestionID());
            questions.setQuestion(update.get(Globals.QUESTION));
        }

        // nothing exist in this form of quote
        if(newQuestionCount == 0 && orgQuestionIDCount == 0){
            thisNote.setQuestionID((int)rdb.getQuestionsDao().addQuestion(new Questions(update.get(Globals.QUESTION))));
            updateCurrentNoteIDs(thisNote);
        }

        // since the original question is a single instance only used by this note, replace the note and keep the ID
        else if(newQuestionCount == 0 && orgQuestionIDCount == 1) {
            rdb.getQuestionsDao().updateQuestion(questions);}

        // since the original question is used my other notes, and the new doesn't exist, add it and capture the new QuestionID
        else if(newQuestionCount == 0 && orgQuestionIDCount > 1){
            int q = (int)rdb.getQuestionsDao().addQuestion(new Questions(update.get(Globals.QUESTION)));
            thisNote.setQuestionID(q);
            updateCurrentNoteIDs(thisNote);
        }

        // Multiples of the new question exist (the new question isn't really new but different), update the notes table for the note only
        else if(newQuestionCount > 0){
            thisNote.setQuestionID(rdb.getQuestionsDao().getQuestionByValue(update.get(Globals.QUESTION)));
            updateCurrentNoteIDs(thisNote);
            // If the current value is the only existing, delete it.
            if(orgQuestionIDCount == 1) {
                deleteThis = rdb.getQuestionsDao().getQuestion(orgQuestionID);
                rdb.getQuestionsDao().deleteQuestion(deleteThis);
            }
        }
    }
    private static void updateQuote(Notes thisNote, List<String> update){
        Quotes quotes = null;
        Quotes deleteThis;
        int orgQuoteID = thisNote.getQuoteID();
        int orgQuoteIDCount = 0;
        int newQuoteCount = rdb.getQuotesDao().getCountByValue(update.get(Globals.QUOTE));

        if(thisNote.getQuoteID() != 0){
            orgQuoteIDCount = rdb.getNotesDao().countQuoteByID(thisNote.getQuoteID());
            quotes = rdb.getQuotesDao().getQuote(thisNote.getQuoteID());
            quotes.setQuote(update.get(Globals.QUOTE));
        }

        // nothing exist in this form of quote
        if(newQuoteCount == 0 && orgQuoteIDCount == 0){
            thisNote.setQuoteID((int)rdb.getQuotesDao().addQuote(new Quotes(0, update.get(Globals.QUOTE))));
            updateCurrentNoteIDs(thisNote);
        }

        // since the original quote is a single  only used by this note, replace the note and keep the ID
        else if(newQuoteCount == 0 && orgQuoteIDCount == 1) {
            rdb.getQuotesDao().updateQuote(quotes);}

        // since the original quote is used my other notes, and the new doesn't exist, add it and capture the new QuoteID
        else if(newQuoteCount == 0 && orgQuoteIDCount > 1){
            thisNote.setQuoteID((int)rdb.getQuotesDao().addQuote(new Quotes(0, update.get(Globals.QUOTE))));
            updateCurrentNoteIDs(thisNote);
        }

        // Multiples of the new quote exist (the new quote isn't really new but different), update the notes table for the note only
        else if(newQuoteCount > 0){
            thisNote.setQuoteID(rdb.getQuotesDao().getQuoteByValue(update.get(Globals.QUOTE)));
            updateCurrentNoteIDs(thisNote);

            // If the current value is the only existing, delete it.
            if(orgQuoteIDCount == 1) {
                deleteThis = rdb.getQuotesDao().getQuote(orgQuoteID);
                rdb.getQuotesDao().deleteQuote(deleteThis);
            }
        }
    }
    private static void updateTerm(Notes thisNote, List<String> update){
        Terms terms = null;
        Terms deleteThis;
        int orgTerm = thisNote.getTermID();
        int orgTermIDCount = 0;
        int newTermCount = rdb.getTermsDao().getCountByValue(update.get(Globals.TERM));

        if(thisNote.getTermID() != 0) {
            orgTermIDCount = rdb.getNotesDao().countTermByID(thisNote.getTermID());
            terms = rdb.getTermsDao().getTerm(thisNote.getTermID());
            terms.setTerm(update.get(Globals.TERM));
        }

        // nothing exist in this form of quote
        if(newTermCount == 0 && orgTermIDCount == 0){
            thisNote.setTermID((int)rdb.getTermsDao().addTerm(new Terms(0, update.get(Globals.TERM))));
            updateCurrentNoteIDs(thisNote);
        }

        // since the original term is a single instance only used by this note, replace the note and keep the ID
        else if(newTermCount == 0 && orgTermIDCount == 1) {
            rdb.getTermsDao().updateTerm(terms);}

        // since the original term is used my other notes, and the new doesn't exist, add it and capture the new TermID
        else if(newTermCount == 0 && orgTermIDCount > 1){
            thisNote.setTermID((int)rdb.getTermsDao().addTerm(terms));
            updateCurrentNoteIDs(thisNote);
        }

        // Multiples of the new term exist (the new term isn't really new but different), update the notes table for the note only
        else if(newTermCount > 0){
            thisNote.setTermID(rdb.getTermsDao().getTermByValue(update.get(Globals.TERM)));
            updateCurrentNoteIDs(thisNote);

            // If the current value is the only existing, delete it.
            if(orgTermIDCount == 1) {
                deleteThis = rdb.getTermsDao().getTerm(orgTerm);
                rdb.getTermsDao().deleteTerm(deleteThis);
            }
        }
    }
    private static void updateTopic(Notes thisNote, List<String> update){
        Topics topics = rdb.getTopicsDao().getTopic(thisNote.getTopicID());
        Topics deleteThis;
        int orgTopic = thisNote.getTopicID();
        int orgTopicIDCount = rdb.getNotesDao().countTopicByID(thisNote.getTopicID());
        int newTopicCount = rdb.getTopicsDao().getCountByValue(update.get(Globals.TOPIC));

        // since the original topic is a single instance only used by this note, replace the note and keep the ID
        if(newTopicCount == 0 && orgTopicIDCount == 1) {
            topics.setTopic(update.get(Globals.TOPIC));
            rdb.getTopicsDao().updateTopic(topics);}

        // since the original topic is used by other notes, and the new doesn't exist, add it and capture the new TopicID
        else if(newTopicCount == 0 && orgTopicIDCount > 1){
            thisNote.setTopicID((int)rdb.getTopicsDao().addTopic(new Topics(0, update.get(Globals.TOPIC))));
            updateCurrentNoteIDs(thisNote);
        }
        // Multiples of the new question exist (the new question isn't really new but different), update the notes table for the note only
        else if(newTopicCount > 0){
            thisNote.setTopicID(rdb.getTopicsDao().getTopicByValue(update.get(Globals.TOPIC)));
            updateCurrentNoteIDs(thisNote);
            // If the current value is the only existing, delete it.
            if(orgTopicIDCount == 1) {
                deleteThis = rdb.getTopicsDao().getTopic(orgTopic);
                rdb.getTopicsDao().deleteTopic(deleteThis);
            }
        }
    }

        // METHODS TO ADD OR UPDATE NOTES
    public static Intent addNewNote(Context context, List<Integer> data, List<Files> files){

            rdb = ResearchDatabase.getInstance(context, Globals.DATABASE);
            int noteID;
            int srcID = data.get(1);
            int cmtID = data.get(2);
            int queID = data.get(3);
            int quoID = data.get(4);
            int terID = data.get(5);
            int topID = data.get(6);
            int del =0;

            // NOTES
            Notes note = new Notes(srcID, cmtID, queID, quoID,terID, topID, del);
            noteID = (int) rdb.getNotesDao().addNote(note);
            Sources tmpSource = rdb.getSourcesDao().getSource(srcID);
            Comments tmpComment = rdb.getCommentsDao().getComment(cmtID);
            if(files!=null)
                addNewNoteFiles(noteID, files);

            Intent a = new Intent(context, ViewNote.class);
            a.putExtra("ID", noteID);
            a.putExtra("Type", tmpSource.getSourceType());
            a.putExtra("Summary", tmpComment.getSummary());
            a.putExtra("Source", tmpSource.getTitle());
            a.putExtra("Authors", DBQueryTools.concatenateAuthors(DBQueryTools.getAuthorsBySourceID(srcID)));

            return a;
    }

    // Counts Topics: If the topic count is the last new topic and the count = 1 then returning to the
    // MainActivity should be a refresh.
    public static long countTopic(int iTopic){
        long lastPKID = getSpecificQuery("SELECT TopicID FROM Topic WHERE TopicID ORDER BY TopicID DESC LIMIT 1");
        long numOfTheTopic = getSpecificQuery("SELECT Count(TopicID) FROM Notes Where TopicID = " + iTopic);
        //if last topic id = the new note topic id and is the first and only one, then refresh MainActivity
        if(lastPKID == (long)iTopic && numOfTheTopic == 1)
            return 1;
        return 0;
    }

    public static long getSpecificQuery(String query){
        return rdb.getTopicsDao().getTableSequenceValue(new SimpleSQLiteQuery(query));
    }

    public static void addNewNoteFiles(int noteID, List<Files> nf){
        if(nf.size()>0) {
            for (Files f : nf) {
                long id = rdb.getFilesDao().addFile(f);
                rdb.getFilesByNoteDao().insert(new FilesByNote(noteID, (int) id));
            }
        }
    }

    public static Intent updateNote(Context context, Notes orgNoteTableIDs, List<String> original, List<String> update,
                                    List<Files> orgFiles, List<Files> newFiles, int vNoteID){
        // NO SOURCE OR AUTHORS UPDATES. A NEW NOTE SHOULD BE REQUIRED.

        // SUMMARY; COMMENT; PAGE; TIMESTAMP; HYPERLINK
        if(!valuesAreDifferent(original.get(Globals.SUMMARY), update.get(Globals.SUMMARY)) || !valuesAreDifferent(original.get(Globals.COMMENT), update.get(Globals.COMMENT)) ||
                !valuesAreDifferent(original.get(Globals.PAGE), update.get(Globals.PAGE)) || !valuesAreDifferent(original.get(Globals.TIMESTAMP), update.get(Globals.TIMESTAMP)) ||
                !valuesAreDifferent(original.get(Globals.HYPERLINK), update.get(Globals.HYPERLINK))){

            Comments cmts = rdb.getCommentsDao().getComment(orgNoteTableIDs.getCommentID());
            cmts.setSummary(update.get(Globals.SUMMARY));     // Summary
            cmts.setComment(update.get(Globals.COMMENT));    // Comment
            cmts.setPage(update.get(Globals.PAGE));       // Page
            cmts.setTimeStamp(update.get(Globals.TIMESTAMP));  // TimeStamp
            cmts.setHyperlink(update.get(Globals.HYPERLINK));  // Hyperlink
            rdb.getCommentsDao().updateComment(cmts);
        }

        // QUESTION
        if(!valuesAreDifferent(original.get(Globals.QUESTION), update.get(Globals.QUESTION))){
            updateQuestion(orgNoteTableIDs, update);
        }
        // QUOTE
        if(!valuesAreDifferent(original.get(Globals.QUOTE), update.get(Globals.QUOTE))){
            updateQuote(orgNoteTableIDs, update);
        }
        // TERM
        if(!valuesAreDifferent(original.get(Globals.TERM), update.get(Globals.TERM))){
            updateTerm(orgNoteTableIDs, update);
        }
        // TOPIC
        if(!valuesAreDifferent(original.get(Globals.TOPIC), update.get(Globals.TOPIC))){
            updateTopic(orgNoteTableIDs, update);
        }
        // NOTEFILES
        updateFiles(vNoteID, orgFiles, newFiles);

        Intent u = new Intent(context, ViewNote.class);
        u.putExtra("ID", vNoteID);
        u.putExtra("Type", update.get(0));
        u.putExtra("Summary", update.get(1));
        u.putExtra("Source", update.get(2));
        u.putExtra("Authors",update.get(3));
        return u;
    }

    public static void updateFiles(int id, List<Files> orgFiles, List<Files> newFiles){
        try {
            if (newFiles != null && orgFiles != null) {
                // Add new files
                for (Files nFile : newFiles) {
                    boolean found = false;
                    for (Files oFile : orgFiles) {
                        if (nFile.getFileID() == oFile.getFileID() && nFile.getFileName().equals(oFile.getFileName())) {
                            found = true; // Do nothing
                            break;
                        }
                    }
                    if (!found) {
                        long add = rdb.getFilesDao().addFile(nFile);
                            rdb.getFilesByNoteDao().insert(new FilesByNote(id,(int)add));
                    }
                }

                // Delete Files
                for (Files oFile : orgFiles) {
                    boolean found = false;
                    for (Files nFile : newFiles) {
                        if (nFile.getFileID() != 0 && oFile.getFileName().equals(nFile.getFileName())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        rdb.getFilesByNoteDao().delete(new FilesByNote(id, oFile.getFileID()));
                        rdb.getFilesDao().deleteFile(oFile);
                    }
                }
            } else if (newFiles.size() > 0) {
                for (Files f:newFiles) {
                    long add = rdb.getFilesDao().addFile(f);
                    rdb.getFilesByNoteDao().insert(new FilesByNote(id,(int)add));
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void deleteNote(Context context, int noteID){
        rdb = ResearchDatabase.getInstance(context, Globals.DATABASE);
        List<String> arrTables  = Arrays.asList("Files", "Notes", "Source", "Topic", "Comment", "Question", "Quote", "Term");

        Notes noteIDs = rdb.getNotesDao().getNote(noteID);
        int srcID = noteIDs.getSourceID();
        int notesSrcCount = rdb.getNotesDao().countSourcesByID(srcID); // How many times source is used in Notes
        int absSourceAuthorCount = rdb.getAuthorBySourceDao().countAuthorsBySourceID(srcID); // How many authors of the source
        int notesTopicCount = rdb.getNotesDao().countTopicByID(noteIDs.getTopicID());
        int notesQuestionCount = rdb.getNotesDao().countQuestionByID(noteIDs.getQuestionID());
        List<Authors> authors = rdb.getAuthorBySourceDao().getAuthorsForSource(srcID); // List of Source Authors
        List<Files> files = rdb.getFilesByNoteDao().getFilesByNote(noteID); // Number of files linked to the source

        try {
            //long save = rdb.getNotesDao().savePoint(new SimpleSQLiteQuery("SAVEPOINT 'theKraken'"));
            for(String tbl : arrTables) {
                int authorID;
                Sources source = rdb.getSourcesDao().getSource(srcID);

                // File need deleted first due to a NoteID foreign key dependency
                if (tbl.equals("Files") && files.size() >= 1) {
                    for (Files file : files) {
                        rdb.getFilesByNoteDao().delete(new FilesByNote(noteID, file.getFileID()));
                        rdb.getFilesDao().deleteFile(file);
                    }

                    // Notes contain all foreign keys and need deleted first after files have been checked
                } else if (tbl.equals("Notes")) {
                    rdb.getNotesDao().deleteNote(noteIDs);
                }else if (tbl.equals("Source")) {
                    // Only 1 occurrence of Source and 1 author for the source
                    if (notesSrcCount == 1 && absSourceAuthorCount == 1) {
                        // We only have one author
                        authorID = authors.get(0).getAuthorID();

                        // The author is used 1 time
                        if (rdb.getAuthorBySourceDao().countAuthorIsUsed(authorID) == 1) {
                            rdb.getAuthorBySourceDao().deleteABS(new AuthorBySource(authorID, srcID));
                            Authors author = rdb.getAuthorsDao().getAuthor(authorID);
                            rdb.getAuthorsDao().deleteAuthor(author);
                            rdb.getSourcesDao().deleteSource(source);

                        } else { // Keep the author but delete the source and source/author reference in ABS
                            rdb.getAuthorBySourceDao().deleteABS(new AuthorBySource(authorID, srcID));
                            rdb.getSourcesDao().deleteSource(source);
                        }

                        // Source is used once with multiple authors
                    } else if (notesSrcCount == 1 && absSourceAuthorCount > 1) {
                        for (Authors author : authors) {
                            authorID = author.getAuthorID();

                            // Author is only used once delete the author and source reference
                            int absAuthorUsed = rdb.getAuthorBySourceDao().countAuthorIsUsed(author.getAuthorID());
                            if(absAuthorUsed == 1) {
                                rdb.getAuthorBySourceDao().deleteABS(new AuthorBySource(authorID, srcID));
                                rdb.getAuthorsDao().deleteAuthor(author);

                                // Author is used elsewhere, delete just the source reference
                            } else {
                                rdb.getAuthorBySourceDao().deleteABS(new AuthorBySource(authorID, srcID));
                            }
                        }
                        rdb.getSourcesDao().deleteSource(source);
                    }

                } else if (tbl.equals("Topic") && notesTopicCount == 1) {
                    Topics topic = rdb.getTopicsDao().getTopic(noteIDs.getTopicID());
                    rdb.getTopicsDao().deleteTopic(topic);

                } else if(tbl.equals("Comment") && noteIDs.getCommentID() != 0){
                    Comments comment = rdb.getCommentsDao().getComment(noteIDs.getCommentID());
                    rdb.getCommentsDao().deleteComment(comment);

                }else if (tbl.equals("Question") && noteIDs.getQuestionID() != 0 && notesQuestionCount == 1) {
                    Questions question = rdb.getQuestionsDao().getQuestion(noteIDs.getQuestionID());
                    rdb.getQuestionsDao().deleteQuestion(question);

                } else if (tbl.equals("Quote") && noteIDs.getQuoteID() != 0) {
                    Quotes quote = rdb.getQuotesDao().getQuote(noteIDs.getQuoteID());
                    rdb.getQuotesDao().deleteQuote(quote);

                } else if (tbl.equals("Term") && noteIDs.getTermID() != 0) {
                    Terms term = rdb.getTermsDao().getTerm(noteIDs.getTermID());
                    rdb.getTermsDao().deleteTerm(term);
                }

            }
            //long release = rdb.getNotesDao().releaseSavePoint(new SimpleSQLiteQuery("RELEASE SAVEPOINT 'theKraken'"));
            Toast.makeText(context, "Note Deleted Successfully!!!", Toast.LENGTH_LONG).show();
        }catch (Exception e){
            e.printStackTrace();
        }




    }

        // METHODS - OTHER
    public static String concatenateDate(String month, String day, String year){
        StringBuilder sb = new StringBuilder();
        if(!month.isEmpty() && !month.equals("0")){
            sb.append(month);
        }
        if(!day.isEmpty() && !day.equals("0")){
            if (!month.isEmpty() && !month.equals("0")){
                sb.append("/").append(day);
            }else{
                sb.append(day);
            }
        }
        if(!year.isEmpty()){
            if ((month.isEmpty() || month.equals("0")) && (day.isEmpty() || day.equals("0"))){
                sb.append(year);
            }else{
                sb.append("/").append(year);
            }
        }
        return sb.toString();
    }

        // EVALUATES NOTES AS OBJECTS
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean valuesAreDifferent(String obj1, String obj2){
        if(obj1 == null || obj1.isEmpty() || obj1.trim().isEmpty()){obj1 = "";}
        if(obj2 == null || obj2.isEmpty() || obj2.trim().isEmpty()){obj2 = "";}
        return obj1.equals(obj2);
    }




}
