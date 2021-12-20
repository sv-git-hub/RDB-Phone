package com.mistywillow.researchdb;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.os.Environment;

public class GlobalFilePathVariables {

    public static String DATA_DIRECTORY = Environment.getDataDirectory().getPath();
    public static String DATABASE;

}
