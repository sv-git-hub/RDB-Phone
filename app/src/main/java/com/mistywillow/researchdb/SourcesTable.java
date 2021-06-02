package com.mistywillow.researchdb;

public class SourcesTable {
    private int NoteID;
    private int SourceID;
    private String SourceType;
    private String Title;
    private String Summary;

    public SourcesTable(int NoteID, int SourceID, String SourceType, String Title, String Summary){
        this.NoteID = NoteID;
        this.SourceID = SourceID;
        this.SourceType = SourceType;
        this.Title = Title;
        this.Summary = Summary;
    }

    public int getNoteID() {
        return NoteID;
    }

    public void setNoteID(int noteID) {
        this.NoteID = noteID;
    }

    public int getSourceID() {
        return SourceID;
    }

    public void setSourceID(int sourceID) {
        this.SourceID = sourceID;
    }

    public String getSourceType() {
        return SourceType;
    }

    public void setSourceType(String sourceType) {
        this.SourceType = sourceType;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        this.Title = title;
    }

    public String getSummary() {
        return Summary;
    }

    public void setSummary(String summary) {
        this.Summary = summary;
    }
}
