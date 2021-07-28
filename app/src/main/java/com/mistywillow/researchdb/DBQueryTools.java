package com.mistywillow.researchdb;

import android.content.Context;
import android.content.Intent;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.mistywillow.researchdb.database.ResearchDatabase;
import com.mistywillow.researchdb.database.entities.*;

import java.util.*;

public class DBQueryTools extends AppCompatActivity {

    private static ResearchDatabase rdb = null;

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
        rdb = ResearchDatabase.getInstance(context, "Apologetic.db");
        List<Sources> sources = rdb.getSourcesDao().getSources();
        List<String> orgSourceTitles = new ArrayList<>();
        for(Sources s : sources){
            orgSourceTitles.add(s.getTitle());
        }
        return new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, orgSourceTitles);
        //sourceTitle.setAdapter(sourceTitleAdapter);
    }

    public static ArrayAdapter<String> captureDBSourcesWithAuthors(Context context){
        rdb = ResearchDatabase.getInstance(context, "Apologetic.db");
        List<Sources> sources = rdb.getSourcesDao().getSources();
        List<String> orgSourceTitles = new ArrayList<>();
        for(Sources src : sources){
            List<Authors> authors = rdb.getAuthorsDao().getSourceAuthors(src.getSourceID());
            orgSourceTitles.add(src.getTitle() + " by " + concatenateAuthors(authors));
        }


//        for(Sources s : sources){
//            orgSourceTitles.add(s.getTitle());
//        }
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

            ;
        };
        //sourceTitle.setAdapter(sourceTitleAdapter);
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
            if(!c.getSummary().isEmpty() || !orgSummaries.contains(c.getSummary()))
                orgSummaries.add(c.getSummary());
        }
        return new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, orgSummaries);
    }

    public static String captureAuthorNewOrOldSource(Context context, String sourceTitle){
        List<Authors> newAuthors;
        List<Sources> newSource = rdb.getSourcesDao().getSourceByTitle(sourceTitle);
        boolean authorsMatch;

        if(newSource.size()>1)
            Toast.makeText( context, "Which author?", Toast.LENGTH_SHORT).show();

        if(newSource.size() == 1) {
            newAuthors = rdb.getAuthorBySourceDao().getAuthorsForSource(newSource.get(0).getSourceID());
        }else{
            Toast.makeText( context, "New Source", Toast.LENGTH_SHORT).show();
            return "Select an author and or add new authors in the table below";
        }
        return DBQueryTools.concatenateAuthors(newAuthors);
    }

    private static boolean checkAuthorsMatchSource(List<Authors> newAuthors) {
        return false;
    }

    public static boolean checkForDuplicate(String data, String table, String column){
        return false;
    }

    private long countByString(){return 0;}

    public static void addNewNote(Context context, List<String> data, List<Files> files){

        rdb = ResearchDatabase.getInstance(context, "Apologetic.db");
        int noteID =0, srcID =0, cmtID =0, queID =0, quo =0, terID =0, topID =0;

        // ENTER REQUIRED FIELDS


        // CHECK FOR EXISTING IDs
        topID = rdb.getTopicsDao().getTopicID(data.get(Globals.TOPIC));
        if(topID == 0) PopupDialog.AlertMessage (context, "Error: No Topic Available", String.valueOf(topID));

        // TYPE; TITLE; YEAR; MONTH; DAY; VOLUME; EDITION; ISSUE
        // Source and Author
        /*Sources src = new Sources(data.get(Globals.TYPE),data.get(Globals.SOURCE), Integer.parseInt(data.get(Globals.YEAR)),
                Integer.parseInt(data.get(Globals.MONTH)), Integer.parseInt(data.get(Globals.DAY)), data.get(Globals.VOLUME), data.get(Globals.EDITION),
                data.get(Globals.ISSUE));
        rdb.getSourcesDao().addSource(src);
        srcID = rdb.getSourcesDao().lastSourcePKID();*/

        // SUMMARY; COMMENT; PAGE; TIMESTAMP; HYPERLINK

        // QUESTION

        // QUOTE

        // TERM

        // TOPIC

        // INTENT

        // Table Files


    }

    public static Intent updateNote(Context context, Notes orgNoteTableIDs, List<String> original, List<String> update, int vNoteID){
        // NO SOURCE OR AUTHORS UPDATES. A NEW NOTE SHOULD BE REQUIRED.

        // PARSE AND PREPARE DATE VALUES


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

    // EVALUATES NOTES AS OBJECTS
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean valuesAreDifferent(String obj1, String obj2){
        if(obj1 == null || obj1.isEmpty() || obj1.trim().isEmpty()){obj1 = "";}
        if(obj2 == null || obj2.isEmpty() || obj2.trim().isEmpty()){obj2 = "";}
        return obj1.equals(obj2);
    }


}
