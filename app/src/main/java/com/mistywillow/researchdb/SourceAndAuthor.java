package com.mistywillow.researchdb;

public class SourceAndAuthor {
    private int srcID;
    private String title;
    private String authors;

    public SourceAndAuthor(int srcID, String title, String authors){
        this.srcID = srcID;
        this.title = title;
        this.authors = authors;
    }
    public int getSrcID(){return this.srcID;    }
    public String getTitle(){return this.title;}
    public String getAuthors(){return this.authors;}
}
