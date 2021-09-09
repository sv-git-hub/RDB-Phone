package com.mistywillow.researchdb;

import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class DateTimestampManager {

    public static boolean validateTopic(Context context, String checkTopic){
        String regEx = "^([\\w'-]*)$|^([\\w'-]*\\ [\\w'-]*:?[\\w'-]*)$|^([\\w'-]*\\ [\\w'-]*\\ [\\w'-]*:?[\\w'-]*)$";
        if (!checkTopic.matches(regEx)) {
            PopupDialog.AlertMessage(context,"Error: Invalid Topic", "Keep your topic one to three words. No special characters.");
            return false;
        }
        return true;
    }

    public static boolean validateSearchTimeStamp(Context context, String checkTimeStamp){
        if (!checkTimeStamp.isEmpty() && !checkTimeStamp.matches("^((\\d\\d):([0-5]\\d):([0-5]\\d))$")){
            PopupDialog.AlertMessage(context,"Error: Invalid TimeStamp","Please enter the following format or leave blank.\n\n" +
                    "Example: 00:00:01 or 01:45:59 = hh:mm:ss");
            return false;
        }
        return true;
    }

    public static boolean validateDate(Context context, String date){
        if (!date.equals("") && !date.matches ("^([12][\\d][\\d][\\d])$|^(0[1-9]|1[012])/(0[1-9]|[12][0-9]|3[01])/([12][\\d][\\d][\\d])$|^((0[1-9]|1[012])/([12][\\d][\\d][\\d]))$|^([\\d]?[\\d]?[\\d]?[\\d])([A][.][D][.]|[B][.][C][.]|[B][.][C][.][E][.])$")){
            PopupDialog.AlertMessage(context, "Error: Invalid Date Format","Please enter an acceptable format or leave blank. Examples:\n\n" +
                    "mm/dd/yyyy\nmm/yyyy\nyyyy");
            return false;
        }
        return true;
    }
/*
    private void validateMonth(){
        TextField x = this.dtMonth;
        if (x.getText() != null && !x.getText().isEmpty() && !x.getText().matches("^(0?[1-9]|1[0-2])$")){
            displayFailureMessage("Please enter a month value between 01 - 12.", "Error: Invalid Month");
        }
    }

    private void validateDay(){
        TextField x = this.dtDay;
        if (x.getText() != null && !x.getText().isEmpty() && !x.getText().matches("^(0?[1-9]|[1-2][0-9]|3[0-1])$")){
            displayFailureMessage("Please enter a value between 01 - 31", "Error: Invalid Day");
        }
    }

    // Called by OnAction or focusedProperty events
    private void validateYear(){
        TextField x = this.dtYear;
        if (x.getText() != null && !x.getText().isEmpty() && !x.getText().matches("^([1-2]\\d\\d\\d)$")){
            displayFailureMessage("Please enter a value between 1000 and 2999", "Error: Invalid Year");
        }
    }*/

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
