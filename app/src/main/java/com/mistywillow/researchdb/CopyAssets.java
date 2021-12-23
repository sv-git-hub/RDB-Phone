package com.mistywillow.researchdb;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CopyAssets {

    private static void checkFolderExists(Context context, String folder){
        File location = new File(context.getFilesDir() + "/" + folder);
        if(!location.exists())
            location.mkdir();
    }

    public static void copyAssets(Context context, String folder, String filename) {
        AssetManager assetManager = context.getAssets();
        String checkForFile = "";

                checkFolderExists(context, folder);

                InputStream in = null;
                OutputStream out = null;
                checkForFile = context.getFilesDir().getAbsolutePath() + "/" + folder + "/" + filename;

                if(!Files.exists(Paths.get(checkForFile))) {
                    try {

                        in = assetManager.open(folder + "/" + filename);
                        out = new FileOutputStream(checkForFile);
                        copyFile(in, out);

                    } catch (IOException e) {
                        Log.e("tag", "Failed to copy asset file: " + filename, e);
                    } finally {
                        if (in != null) {
                            try {
                                in.close();
                                in = null;
                            } catch (IOException e) {
                                Log.e("CopyAssets", "Failed to close.");
                            }
                        }
                        if (out != null) {
                            try {
                                out.flush();
                                out.close();
                                out = null;
                            } catch (IOException e) {
                                Log.e("CopyAssets", "Failed to flush and close.");
                            }
                        }
                    }
                }
            //}
        //}
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }
}