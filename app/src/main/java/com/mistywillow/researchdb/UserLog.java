package com.mistywillow.researchdb;

import android.content.Context;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

public class UserLog {

    private static String data;

    public static String getUserInfo(Context context, String parameter){
        try {
            Scanner f = new Scanner(Paths.get(context.getFilesDir() + "/user_log/userInfo.txt"));
            while (f.hasNextLine()){
                String l = f.nextLine();
                if(l.contains(parameter +":=")) {
                    data = l.split(":=")[1];
                    break;
                }
            }
            f.close();
            return data;

        }catch ( Exception ep){
            ep.printStackTrace();
            return null;
        }
    }
    public static void setUserInfo(Context context, String parameter, String data) {
        try {
            Scanner f = new Scanner(Paths.get(context.getFilesDir() + "/user_log/userInfo.txt"));
            String currentUserInfo = parameter + ":=" + getUserInfo(context, parameter);
            String line;
            String newLine = "";
            while (f.hasNextLine()) {
                line = f.nextLine();
                if (line.contains(currentUserInfo)) {
                    newLine = newLine + line.replace(currentUserInfo, parameter + ":=" + data) + "\r\n";
                } else {
                    newLine = newLine + line + "\r\n";
                }
            }
            f.close();
            FileWriter writer = new FileWriter(GlobalFilePathVariables.DATA_DIRECTORY + "/user_log/userInfo.txt");
            writer.write(newLine);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
