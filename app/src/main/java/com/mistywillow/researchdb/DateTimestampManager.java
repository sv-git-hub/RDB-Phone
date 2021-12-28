package com.mistywillow.researchdb;

import android.content.Context;
import android.widget.TextView;

public class DateTimestampManager {

    public static void validateTopic(Context context, String checkTopic){
        String regEx = "^([\\w'-]*)$|^([\\w'-]* [\\w'-]*:?[\\w'-]*)$|^([\\w'-]* [\\w'-]* [\\w'-]*:?[\\w'-]*)$";
        if (!checkTopic.matches(regEx)) {
            PopupDialog.AlertMessageOK(context,"Error: Invalid Topic", "Keep your topic one to three words. No special characters.");
        }
    }

    public static void validateSearchTimeStamp(Context context, String checkTimeStamp){
        if (!checkTimeStamp.isEmpty() && !checkTimeStamp.matches("^((\\d\\d):([0-5]\\d):([0-5]\\d))$")){
            PopupDialog.AlertMessageOK(context,"Error: Invalid TimeStamp","Please enter the following format or leave blank.\n\n" +
                    "Example: 00:00:01 or 01:45:59 = hh:mm:ss");
        }
    }

    public static void validateDate(Context context, String date){
        if (!date.equals("") && !date.matches ("^([12][\\d][\\d][\\d])$|^(0[1-9]|1[012])/(0[1-9]|[12][0-9]|3[01])/([12][\\d][\\d][\\d])$|^((0[1-9]|1[012])/([12][\\d][\\d][\\d]))$|^([\\d]?[\\d]?[\\d]?[\\d])([A][.][D][.]|[B][.][C][.]|[B][.][C][.][E][.])$")){
            PopupDialog.AlertMessageOK(context, "Error: Invalid Date Format","Please enter an acceptable format or leave blank. Examples:\n\n" +
                    "mm/dd/yyyy\nmm/yyyy\nyyyy");
        }
    }

    public static String[] parseDate(TextView date){
        String[] tempDate = date.getText().toString().split("/");
        String[] returnDT = new String[3];
        if(tempDate.length==0){
            return new String[]{"0", "0", "0"};
        }else if(tempDate.length == 1){ // e.g., 2021 [0]
            returnDT[0] = tempDate[0]; // Year
            returnDT[1] = "0";
            returnDT[2] = "0";
        }else if(tempDate.length == 2){ // e.g., 12/2021 [0][1]
            returnDT[0] = tempDate[1]; // Year
            returnDT[1] = tempDate[0]; // Month
            returnDT[2] = "0";
        }else{                          // e.g., 01/02/2021 [0][1][2]
            returnDT[0] = tempDate[2]; // Year
            returnDT[1] = tempDate[0]; // Month
            returnDT[2] = tempDate[1]; // Day
        }
        return returnDT;
    }
}
