package com.mistywillow.researchdb;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CopyAssets {

    public static void copyAssets(Context context, String filename) {
        AssetManager assetManager = context.getAssets();

                InputStream in = null;
                OutputStream out = null;
                String checkForFile = context.getCacheDir().getAbsolutePath() + "/" + filename;

                if(!Files.exists(Paths.get(checkForFile))) {
                    try {

                        in = assetManager.open(filename);
                        out = new FileOutputStream(checkForFile);
                        copyFile(in, out);

                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (in != null) {
                            try {
                                in.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (out != null) {
                            try {
                                out.flush();
                                out.close();
                            } catch (IOException e) {
                                e.printStackTrace();
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
        in.close();
        out.close();
    }
}