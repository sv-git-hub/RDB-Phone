package com.mistywillow.researchdb;

public class NoteDetails {
    private String Question;
    private String Quote;
    private String Term;
    private String Year;
    private String Month;
    private String Day;
    private String Volume;
    private String Edition;
    private String Issue;
    private String Topic;
    private String Hyperlink;
    private String Comment;
    private String Page;
    private String TimeStamp;
    private String Summary;

    public NoteDetails(String Question, String Quote, String Term, String Year, String Month, String Day, String Volume,
                       String Edition, String Issue, String Topic, String Hyperlink, String Comment, String Page,
                       String TimeStamp, String Summary){
        this.Question = Question;
        this.Quote = Quote;
        this.Term = Term;
        this.Year = Year;
        this.Month = Month;
        this.Day = Day;
        this.Volume = Volume;
        this.Edition = Edition;
        this.Issue = Issue;
        this.Topic = Topic;
        this.Hyperlink = Hyperlink;
        this.Comment = Comment;
        this.Page = Page;
        this.TimeStamp = TimeStamp;
        this.Summary = Summary;
    }

    public String getQuestion() {
        return Question;
    }

    public void setQuestion(String question) {
        this.Question = question;
    }

    public String getQuote() {
        return Quote;
    }

    public void setQuote(String quote) {
        this.Quote = quote;
    }

    public String getTerm() {
        return Term;
    }

    public void setTerm(String term) {
        this.Term = term;
    }

    public String getYear() {
        return Year;
    }

    public void setYear(String year) {
        this.Year = year;
    }

    public String getMonth() {
        return Month;
    }

    public void setMonth(String month) {
        this.Month = month;
    }

    public String getDay() {
        return Day;
    }

    public void setDay(String day) {
        this.Day = day;
    }

    public String getVolume() {
        return Volume;
    }

    public void setVolume(String volume) {
        this.Volume = volume;
    }

    public String getEdition() {
        return Edition;
    }

    public void setEdition(String edition) {
        this.Edition = edition;
    }

    public String getIssue() {
        return Issue;
    }

    public void setIssue(String issue) {
        this.Issue = issue;
    }

    public String getTopic() {
        return Topic;
    }

    public void setTopic(String topic) {
        this.Topic = topic;
    }

    public String getHyperlink() { return Hyperlink; }

    public void setHyperlink(String hyperlink) { this.Hyperlink = hyperlink; }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        this.Comment = comment;
    }

    public String getPage() {
        return Page;
    }

    public void setPage(String page) {
        this.Page = page;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.TimeStamp = timeStamp;
    }

    public String getSummary() {
        return Summary;
    }

    public void setSummary(String summary) {
        this.Summary = summary;
    }
}
