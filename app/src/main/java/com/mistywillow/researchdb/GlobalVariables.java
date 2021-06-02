package com.mistywillow.researchdb;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Environment;

public class GlobalVariables {

    public static final String ROOT_DIRECTORY = Environment.getRootDirectory().getPath() ;      // /system
    public static final String DATA_DIRECTORY = Environment.getDataDirectory().getPath();       // /data
    public static final String EXTERNAL_DIRECTORY = Environment.getExternalStorageState();      // mounted

}
