package com.mistywillow.researchdb;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.mistywillow.researchdb.database.ResearchDatabase;
import com.mistywillow.researchdb.database.entities.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DBQueryTools {

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
    public static boolean checkForDuplicate(String data, String table){
            return false;
        }
    public static ArrayAdapter<String>captureSourceTypes(Context context){
        List<String> orgSourceTypes = new ArrayList<>(Arrays.asList("Article", "Audio", "Book", "Journal", "Periodical", "Question", "Quote", "Term", "Video", "Website", "Other"));
        return new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, orgSourceTypes);
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

        if(newSource.size() == 1) {
            newAuthors = rdb.getAuthorBySourceDao().getAuthorsForSource(newSource.get(0).getSourceID());
        }else{
            Toast.makeText( context, "New Source", Toast.LENGTH_SHORT).show();
            return "need new author";
        }


        return DBQueryTools.concatenateAuthors(newAuthors);
    }

    private static boolean checkAuthorsMatchSource(List<Authors> newAuthors) {
        return false;
    }
}
