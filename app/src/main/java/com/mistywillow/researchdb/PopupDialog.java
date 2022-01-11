package com.mistywillow.researchdb;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import com.mistywillow.researchdb.databases.MasterDatabase;
import com.mistywillow.researchdb.masterdb.entity.MasterDatabaseList;

public class PopupDialog{

    public static void AlertMessageOK(Context context, String title, String message){
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

    public static void DeleteDatabaseYN(Context context, String title, String message){
        // Create a AlertDialog Builder.
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        // Set title, icon, can not cancel properties.
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setIcon(R.drawable.img_rdb_icon);
        alertDialogBuilder.setCancelable(false);

        // Set the inflated layout view object to the AlertDialog builder.
        View popup = MessageBoxYN(context, message);
        alertDialogBuilder.setView(popup);

        // Create AlertDialog and show.
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        Button cancel = popup.findViewById(R.id.btn_Cancel);
        Button ok = popup.findViewById(R.id.btn_OK);
        cancel.setOnClickListener(v -> alertDialog.cancel());
        ok.setOnClickListener(v -> {
            try {
                MasterDatabase mdb = MasterDatabase.getInstance(context);
                MasterDatabaseList curMDB = mdb.getMasterDao().getDatabaseNamed(Globals.DATABASE);
                mdb.getMasterDao().delete(curMDB);
                context.deleteDatabase(Globals.DATABASE);

                Intent home = new Intent(context, MainActivityMaster.class);
                context.startActivity(home);
                alertDialog.cancel();
            }catch (Exception del){
                del.printStackTrace();
            }
        });
    }

    /* Initialize popup dialog view and ui controls in the popup dialog. */
    @SuppressLint("InflateParams")
    private static View MessageBox(Context context, String message)    {
        View popupInputDialogView;
        // Get layout inflater object.
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        // Inflate the popup dialog from a layout xml file.
        popupInputDialogView = layoutInflater.inflate(R.layout.popup_message, null);
        TextView messageBody = popupInputDialogView.findViewById(R.id.popupMessage);
        messageBody.setText(message);
        return popupInputDialogView;
    }

    @SuppressLint("InflateParams")
    private static View MessageBoxYN(Context context, String message)    {
        View popupInputDialogView;
        // Get layout inflater object.
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        // Inflate the popup dialog from a layout xml file.
        popupInputDialogView = layoutInflater.inflate(R.layout.popup_message_yn, null);
        TextView messageBody = popupInputDialogView.findViewById(R.id.popupMessage);
        messageBody.setText(message);
        return popupInputDialogView;
    }


}
