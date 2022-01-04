package com.mistywillow.researchdb;

import android.os.Environment;

public class Globals {
    // SHARED PREFERENCES
    public static final String SHARED_PREF_FILE = "AppSettings";
    public static final String DOWNLOADS_FOLDER = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
    // DATABASE
    public static long DB_ID;
    public static String DATABASE;
    // NOTE ARRAY POSITION VARIABLES
    public static final int TYPE = 0;
    public static final int SUMMARY = 1;
    public static final int SOURCE = 2;
    public static final int AUTHORS = 3;
    public static final int QUESTION = 4;
    public static final int QUOTE = 5;
    public static final int TERM = 6;
    public static final int YEAR = 7;
    public static final int MONTH = 8;
    public static final int DAY = 9;
    public static final int VOLUME = 10;
    public static final int EDITION = 11;
    public static final int ISSUE = 12;
    public static final int HYPERLINK = 13;
    public static final int COMMENT = 14;
    public static final int PAGE = 15;
    public static final int TIMESTAMP = 16;
    public static final int TOPIC = 17;
}
