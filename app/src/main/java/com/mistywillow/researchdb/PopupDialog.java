package com.mistywillow.researchdb;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;

public class PopupDialog{

    private static String result = "";

    public static void AlertMessage(Context context, String title, String message){
        // Create a AlertDialog Builder.
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        // Set title, icon, can not cancel properties.
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setIcon(R.drawable.img_rdb_icon);
        alertDialogBuilder.setCancelable(false);

        // Set the inflated layout view object to the AlertDialog builder.
        View popup = MessageBox(context, message);
        alertDialogBuilder.setView(popup);

        // Create AlertDialog and show.
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        Button ok = popup.findViewById(R.id.btn_OK);
        ok.setOnClickListener(v -> alertDialog.cancel());
    }

    /* Initialize popup dialog view and ui controls in the popup dialog. */
    private static View MessageBox(Context context, String message)
    {
        // Get layout inflater object.
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        // Inflate the popup dialog from a layout xml file.
        View popupInputDialogView = layoutInflater.inflate(R.layout.popup_message, null);
        TextView messageBody = popupInputDialogView.findViewById(R.id.popupMessage);
        messageBody.setText(message);
        return popupInputDialogView;
    }

    public static String AlertInputBox(Context context, String title, String message){
        // Create a AlertDialog Builder.
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        // Set title, icon, can not cancel properties.
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setIcon(R.drawable.img_rdb_icon);
        alertDialogBuilder.setCancelable(false);

        // Set the inflated layout view object to the AlertDialog builder.
        View popup = SingleInputBox(context, message);
        alertDialogBuilder.setView(popup);

        // Create AlertDialog and show.
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        EditText input = popup.findViewById(R.id.popupInput);
        Button ok = popup.findViewById(R.id.btn_OK);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = input.getText().toString();
                alertDialog.cancel();
                Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
            }
        });
        Button cancel = popup.findViewById(R.id.btn_Cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
                Toast.makeText(context, "CANCELLED!", Toast.LENGTH_SHORT).show();

            }
        });
        return result;
    }

    private static View SingleInputBox(Context context, String message)    {
        // Get layout inflater object.
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        // Inflate the popup dialog from a layout xml file.
        View popupInputDialogView = layoutInflater.inflate(R.layout.popup_input_dialog_single, null);
        TextView inputMessage = popupInputDialogView.findViewById(R.id.popupInputMessage);
        inputMessage.setText(message);
        return popupInputDialogView;
    }
}
