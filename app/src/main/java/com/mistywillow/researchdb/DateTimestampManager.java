package com.mistywillow.researchdb;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class DateTimestampManager {




    // Called by OnAction or focusedProperty events
    private void validateDate(String checkDate){
        if (!checkDate.isEmpty() && !checkDate.matches ("^([12][\\d][\\d][\\d])$|^(0[1-9]|1[012])/(0[1-9]|[12][0-9]|3[01])/([12][\\d][\\d][\\d])$|^((0[1-9]|1[012])/([12][\\d][\\d][\\d]))$|^([\\d]?[\\d]?[\\d]?[\\d])([A][.][D][.]|[B][.][C][.]|[B][.][C][.][E][.])$")){
            PopupDialog.AlertMessage(null, "Error: Invalid Date Format","Please enter an acceptable format or leave blank. Examples:\n\n" +
                    "mm/dd/yyyy\nmm/yyyy\nyyyy");
        }
    }
/*    // Called by OnAction or focusedProperty events
    private void validateMonth(){
        TextField x = this.dtMonth;
        if (x.getText() != null && !x.getText().isEmpty() && !x.getText().matches("^(0?[1-9]|1[0-2])$")){
            displayFailureMessage("Please enter a month value between 01 - 12.", "Error: Invalid Month");
        }
    }
    // Called by OnAction or focusedProperty events
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
    // Called by OnAction
    private void validateTopic(String checkTopic){
        String noSpc1 = "[^ \\[\\]!@#$%&?*()+-/]";
        String range = "[a-zA-Z0-9'-]";
        String regEx = "^(" + range + "*)\\.?$|" +
                "^(" + noSpc1 + range + "*\\.?([: ]|[ ])" + range + "*)\\.?$|" +
                "^(" + noSpc1 + range + "*\\.?([: ]|[ ])" + range + "*\\.?([: ]|[ ])" + range + "*)\\.?$";
        if (!checkTopic.matches(regEx)) {
            PopupDialog.AlertMessage(null,"Error: Invalid Topic", "Keep your topic one to three words. No special characters.");
        }

    }
    // Called by OnAction or focusedProperty events
    private void validateSearchTimeStamp(String checkTimeStamp){
        if (!checkTimeStamp.isEmpty() && !checkTimeStamp.matches("^((\\d\\d):([0-5]\\d):([0-5]\\d))$")){

            PopupDialog.AlertMessage(null,"Error: Invalid TimeStamp","Please enter the following format or leave blank.\n\n" +
                    "Example: 00:00:01 or 01:45:59 = hh:mm:ss");
        }
    }

    public static String[] parseDate(TextView date){
        String[] tempDate = date.getText().toString().split("/");
        String[] returnDT = new String[3];
        if(tempDate.length==0){
            return new String[]{"0", "0", "0"};
        }else if(tempDate.length == 1){
            returnDT[0] = tempDate[0]; // Year
            returnDT[1] = "0";
            returnDT[2] = "0";
        }else if(tempDate.length == 2){
            returnDT[0] = tempDate[0]; // Year
            returnDT[1] = tempDate[1]; // Month
            tempDate[2] = "0";
        }else{
            returnDT[0] = tempDate[2]; // Year
            returnDT[1] = tempDate[0]; // Month
            returnDT[2] = tempDate[1]; // Day
        }
        return returnDT;
    }
}
