package com.mistywillow.researchdb;

import android.content.Context;
import com.mistywillow.researchdb.databases.ResearchDatabase;
import com.mistywillow.researchdb.researchdb.entities.Authors;
import com.mistywillow.researchdb.researchdb.entities.Files;


import java.util.HashMap;
import java.util.List;

public class ExportNoteToXML {

    private static ResearchDatabase rdb = null;


    public static HashMap<String, Object[]> exportNote(Context context, Integer noteID,
                                                       List<String> noteDetails, List<Files> noteFiles){
        rdb = ResearchDatabase.getInstance(context, Globals.DATABASE);
        HashMap<String, Object[]> temp = new HashMap<>();
        int srcID = rdb.getSourcesDao().getSourceIDByTitle(noteDetails.get(Globals.SOURCE));
        temp.put("Author", xmlAuthorData(context, srcID));
        if(noteDetails.size() != 0){
            List<Files> nFiles = rdb.getFilesByNoteDao().getFilesByNoteForXML(noteID);
            temp.put("File", xmlFileData(context, nFiles));}
        temp.put("Comment", new Object[]{noteDetails.get(Globals.SUMMARY), noteDetails.get(Globals.COMMENT),
                exportText(noteDetails.get(Globals.PAGE)), exportText(noteDetails.get(Globals.TIMESTAMP)),
                exportText(noteDetails.get(Globals.HYPERLINK))});
        temp.put("Question", new Object[]{exportText(noteDetails.get(Globals.QUESTION))});
        temp.put("Quote", new Object[]{exportText(noteDetails.get(Globals.QUOTE))});
        temp.put("Source", new Object[]{noteDetails.get(Globals.TYPE), noteDetails.get(Globals.SOURCE),
                noteDetails.get(Globals.YEAR), noteDetails.get(Globals.MONTH), noteDetails.get(Globals.DAY),
                exportText(noteDetails.get(Globals.VOLUME)), exportText(noteDetails.get(Globals.EDITION)),
                exportText(noteDetails.get(Globals.ISSUE))});
        temp.put("Term", new Object[]{exportText(noteDetails.get(Globals.TERM))});
        temp.put("Topic", new Object[]{noteDetails.get(Globals.TOPIC)});

        return temp;
    }

    private static Object exportText(Object obj){
        return obj == null ? "" : obj;
    }

    private static Object[] xmlAuthorData(Context context, Integer srcID){
        rdb = ResearchDatabase.getInstance(context, Globals.DATABASE);
        List<Authors> authors = rdb.getAuthorBySourceDao().getAuthorsForSource(srcID);
        return new Object[]{authors};
    }

    private static Object[] xmlFileData(Context context, List<Files> noteFiles){
        HashMap<String, byte[]> fileData = new HashMap<>();
        noteFiles.forEach(f ->
                fileData.put(f.getFileName(), f.getFileData()));
        return new Object[]{fileData};
    }

}
