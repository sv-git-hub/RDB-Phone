package com.mistywillow.researchdb;

import android.content.Context;
import android.content.Intent;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.mistywillow.researchdb.database.ResearchDatabase;
import com.mistywillow.researchdb.database.entities.*;

import java.util.*;

public class DBQueryTools {

    private static ResearchDatabase rdb = null;

    // METHODS PERTAINING TO AUTHORS
    public static String concatenateAuthor(Authors author){
        StringBuilder str = new StringBuilder();
        str.append(author.getFirstName().trim());
        if(!author.getMiddleName().isEmpty())
            str.append(" ").append(author.getMiddleName().trim());
        if(author.getMiddleName().length() == 1)
            str.append(".");
        if(!author.getLastName().isEmpty())
            str.append(" ").append(author.getLastName().trim());
        if(!author.getSuffix().isEmpty())
            str.append(" ").append(author.getSuffix().trim());
        return str.toString().trim();
    }
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
    public static ArrayAdapter<String> captureDBAuthors(Context context){
        rdb = ResearchDatabase.getInstance(context, "Apologetic.db");
        List<Authors> authors = rdb.getAuthorsDao().getAuthors();
        List<String> orgAuthors = new ArrayList<>();
        for(Authors a: authors){
            orgAuthors.add(DBQueryTools.concatenateAuthor(a));
        }
        return new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, orgAuthors);
        //author.setAdapter(authorsAdapter);
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
    public static ArrayAdapter<String> captureDBSourcesWithAuthors(Context context){
        rdb = ResearchDatabase.getInstance(context, "Apologetic.db");
        List<Sources> sources = rdb.getSourcesDao().getSources();
        List<String> orgSourceTitles = new ArrayList<>();
        for(Sources src : sources){
            List<Authors> authors = rdb.getAuthorsDao().getSourceAuthors(src.getSourceID());
            orgSourceTitles.add(src.getTitle() + " by " + concatenateAuthors(authors));
        }
        return new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, orgSourceTitles) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                /// Get the Item from ListView
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view.findViewById(android.R.id.text1);
                // Set the text size 25 dip for ListView each item
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);

                // Return the view
                return view;
            }
        };
    }

        // CAPTURE METHODS FOR MAIN ACTIVITY ONLY - INCLUDES ""  (BLANK)
    public static List<String>spinnerTopicsList(Context context){
        rdb = ResearchDatabase.getInstance(context, "Apologetic.db");
        // TOPIC LIST
        List<Topics> arrTopics = rdb.getTopicsDao().getTopics();
        final List<String> nTopics = new ArrayList<>();
        nTopics.add("");
        for (Topics t: arrTopics) {
            nTopics.add(t.getTopic());
        }
        return nTopics;
    }
    public static List<String>spinnerQuestionList(Context context){
        rdb = ResearchDatabase.getInstance(context, "Apologetic.db");
        // QUESTIONS LIST
        List<Questions> arrQuestions = rdb.getQuestionsDao().getQuestions();
        List<String> nQuestions = new ArrayList<>();
        nQuestions.add("");
        for (Questions q: arrQuestions) {
            nQuestions.add(q.getQuestion());
        }
        return nQuestions;
    }

        // CAPTURE METHODS FOR LOADING SPINNERS IN EditNote and AddNote classes
    public static ArrayAdapter<String>captureSourceTypes(Context context, String layout){
        List<String> orgSourceTypes = new ArrayList<>(Arrays.asList("","Article", "Audio", "Book", "Journal", "Periodical", "Question", "Quote", "Term", "Video", "Website", "Other"));
        if(layout=="simple")
            return new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, orgSourceTypes);
        else
            return new ArrayAdapter<>(context, R.layout.custom_type_spinner, orgSourceTypes);
    }
    public static ArrayAdapter<String> captureDBSources(Context context){
        rdb = ResearchDatabase.getInstance(context, GlobalVariables.DATABASE);
        List<Sources> sources = rdb.getSourcesDao().getSources();
        List<String> orgSourceTitles = new ArrayList<>();
        for(Sources s : sources){
            /*String authors = concatenateAuthors(getAuthorsBySourceID(s.getSourceID()));
            orgSourceTitles.add(s.getSourceID() + " ~ " + s.getTitle());*/
            orgSourceTitles.add(s.getTitle());
        }
        return new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, orgSourceTitles);
    }
    public static ArrayAdapter<String> captureDBTopics(Context context){
        rdb = ResearchDatabase.getInstance(context, "Apologetic.db");
        List<Topics> topics = rdb.getTopicsDao().getTopics();
        List<String> orgTopics = new ArrayList<>();
        for(Topics t : topics){
            orgTopics.add(t.getTopic());
        }
        return new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, orgTopics);
        //topic.setAdapter(topicsAdapter);
    }
    public static ArrayAdapter<String> captureDBQuestions(Context context){
        rdb = ResearchDatabase.getInstance(context, "Apologetic.db");
        List<Questions> questions = rdb.getQuestionsDao().getQuestions();
        List<String> orgQuestions = new ArrayList<>();
        for(Questions q : questions){
            orgQuestions.add(q.getQuestion());
        }
        return new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, orgQuestions);
        //question.setAdapter(acQuestionAdapt);
    }
    public static ArrayAdapter<String> captureSummaries(Context context){
        rdb = ResearchDatabase.getInstance(context, "Apologetic.db");
        List<Comments> summaries = rdb.getCommentsDao().getComments();
        List<String> orgSummaries = new ArrayList<>();
        for(Comments c : summaries){
            if(!c.getSummary().isEmpty() || !orgSummaries.contains(c.getSummary().trim()))
                orgSummaries.add(c.getSummary());
        }
        return new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, orgSummaries);
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
    public static boolean checkForDuplicate(String data, String table, String column){
        return false;
    }
    private long countByString(){return 0;}

        // EVALUATES NOTES AS OBJECTS
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean valuesAreDifferent(String obj1, String obj2){
        if(obj1 == null || obj1.isEmpty() || obj1.trim().isEmpty()){obj1 = "";}
        if(obj2 == null || obj2.isEmpty() || obj2.trim().isEmpty()){obj2 = "";}
        return obj1.equals(obj2);
    }

    public static Intent addNewNote(Context context, List<Integer> data){

        rdb = ResearchDatabase.getInstance(context, GlobalVariables.DATABASE);
        int noteID = 0;
        int srcID = data.get(1);
        int cmtID = data.get(2);
        int queID = data.get(3);
        int quoID = data.get(4);
        int terID = data.get(5);
        int topID = data.get(6);
        int del =0;

        // NOTES
        Notes note = new Notes(noteID, srcID, cmtID, queID, quoID,terID, topID, del);
        noteID = (int) rdb.getNotesDao().addNote(note);
        Sources tmpSource = rdb.getSourcesDao().getSource(srcID);
        Comments tmpComment = rdb.getCommentsDao().getComment(cmtID);
        Intent a = new Intent(context, ViewNote.class);
        a.putExtra("ID", noteID);
        a.putExtra("Type", tmpSource.getSourceType());
        a.putExtra("Summary", tmpComment.getSummary());
        a.putExtra("Source", tmpSource.getTitle());
        a.putExtra("Authors", DBQueryTools.concatenateAuthors(DBQueryTools.getAuthorsBySourceID(srcID)));
        return a;

    }

    public static Intent updateNote(Context context, Notes orgNoteTableIDs, List<String> original, List<String> update, int vNoteID){
        // NO SOURCE OR AUTHORS UPDATES. A NEW NOTE SHOULD BE REQUIRED.

        // TYPE; TITLE; YEAR; MONTH; DAY; VOLUME; EDITION; ISSUE
        if(!valuesAreDifferent(original.get(Globals.TYPE), update.get(Globals.TYPE)) ||
                !valuesAreDifferent(original.get(Globals.YEAR), update.get(Globals.YEAR)) || !valuesAreDifferent(original.get(Globals.MONTH), update.get(Globals.MONTH)) ||
                !valuesAreDifferent(original.get(Globals.DAY), update.get(Globals.DAY)) || !valuesAreDifferent(original.get(Globals.VOLUME), update.get(Globals.VOLUME)) ||
                !valuesAreDifferent(original.get(Globals.EDITION), update.get(Globals.EDITION)) || !valuesAreDifferent(original.get(Globals.ISSUE), update.get(Globals.ISSUE))){
            Sources src = new Sources(orgNoteTableIDs.getSourceID(), update.get(Globals.TYPE),update.get(Globals.SOURCE), Integer.parseInt(update.get(Globals.YEAR)),
                    Integer.parseInt(update.get(Globals.MONTH)), Integer.parseInt(update.get(Globals.DAY)), update.get(Globals.VOLUME), update.get(Globals.EDITION),
                    update.get(Globals.ISSUE));
            rdb.getSourcesDao().updateSource(src);
        }

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
            Questions questions = rdb.getQuestionsDao().getQuestion(orgNoteTableIDs.getQuestionID());
            Questions deleteThis = null;
            int orgQuestionID = orgNoteTableIDs.getQuestionID();
            int orgQuestionIDCount = rdb.getNotesDao().countQuestionByID(orgNoteTableIDs.getQuestionID());
            int newQuestionCount = rdb.getQuestionsDao().getCountByValue(update.get(Globals.QUESTION));

            questions.setQuestion(update.get(Globals.QUESTION));

            // since the original question is a single instance only used by this note, replace the note and keep the ID
            if(newQuestionCount == 0 && orgQuestionIDCount == 1) {
                rdb.getQuestionsDao().updateQuestion(questions);}
            // since the original question is used my other notes, and the new doesn't exist, add it and capture the new QuestionID
            else if(newQuestionCount == 0 && orgQuestionIDCount > 1){
                rdb.getQuestionsDao().addQuestion(questions);
                Notes thisNote = rdb.getNotesDao().getNote(vNoteID);
                thisNote.setQuestionID(rdb.getQuestionsDao().lastQuestionPKID());
                rdb.getNotesDao().updateNote(thisNote);
            }
            // Multiples of the new question exist, update the notes table for the note only
            else if(newQuestionCount > 0){
                orgNoteTableIDs.setQuestionID(rdb.getQuestionsDao().lastQuestionPKID());
                // If the current value is the only existing, delete it.
                if(orgQuestionIDCount == 1)
                    deleteThis = rdb.getQuestionsDao().getQuestion(orgQuestionID);
                    rdb.getQuestionsDao().deleteQuestion(deleteThis);
            }

        }
        // QUOTE
        if(!valuesAreDifferent(original.get(Globals.QUOTE), update.get(Globals.QUOTE))){
            Quotes quotes = new Quotes(orgNoteTableIDs.getQuoteID(), update.get(5));
            rdb.getQuotesDao().updateQuote(quotes);
        }
        // TERM
        if(!valuesAreDifferent(original.get(Globals.TERM), update.get(Globals.TERM))){
            Terms terms = new Terms(orgNoteTableIDs.getTermID(), update.get(6));
            rdb.getTermsDao().updateTerm(terms);
        }
        // TOPIC
        if(!valuesAreDifferent(original.get(Globals.TOPIC), update.get(Globals.TOPIC))){
            Topics topics = new Topics(orgNoteTableIDs.getTopicID(), update.get(17));
            rdb.getTopicsDao().updateTopic(topics);
        }

        Intent u = new Intent(context, ViewNote.class);
        u.putExtra("ID", vNoteID);
        u.putExtra("Type", update.get(0));
        u.putExtra("Summary", update.get(1));
        u.putExtra("Source", update.get(2));
        u.putExtra("Authors",update.get(3));
        return u;
    }








}
