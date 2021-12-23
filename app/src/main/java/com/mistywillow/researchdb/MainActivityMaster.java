package com.mistywillow.researchdb;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.mistywillow.researchdb.databases.MasterDatabase;
import com.mistywillow.researchdb.masterdb.entity.MasterDatabaseList;

import java.io.*;

import static android.Manifest.permission.*;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivityMaster extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor settings;

    ActivityResultLauncher<Intent> resultLauncher;

    MasterDatabase masterDB;
    EditText mDBToAdd;
    Button mAddDB,mUseSelectedDatabase, mImportDB;
    ListView mDatabaseList;
    MDBCursorAdapter mdbCA;
    Cursor mCsr;
    long mSelectedDatabaseId = 0;
    String mSelectedDatabaseName = "";

    private static final int PERMISSION_REQUEST_CODE = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);

        if(!checkPermission())
            requestPermission();

        sharedPreferences = getSharedPreferences(Globals.SHARED_PREF_FILE, MODE_PRIVATE);
        CopyAssets.copyAssets(this, "databases", "Apologetic.db");

        mDBToAdd = this.findViewById(R.id.database_name);
        mAddDB = this.findViewById(R.id.addDatabase);
        mUseSelectedDatabase = this.findViewById(R.id.useSelectedDatabase);
        mImportDB = this.findViewById(R.id.importDatabase);
        mDatabaseList = this.findViewById(R.id.database_list);
        masterDB = MasterDatabase.getInstance(this);


        setUpAddDBButton();
        setupImportDBButton();
        setUpUseSelectedDatabaseButton();
        setOrRefreshDatabaseList();

        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null){
                Uri uri = result.getData().getData();
                String sourcePath = RealPathUtil.getRealPath(this, uri);
                String strDBName = sourcePath.substring(sourcePath.lastIndexOf("/")+1);
                String destinationPath = this.getDatabasePath(strDBName).getPath();

                try {
                    InputStream source = new FileInputStream(sourcePath);
                    OutputStream destination = new FileOutputStream(destinationPath);
                    DatabaseManager.copyDatabase(source, destination);
                    addDatabaseToList(strDBName);
                }catch (FileNotFoundException f){
                    Log.e("Import DB Error", f.toString());
                }
            }
        });
    }

    private void setUpAddDBButton() {
        mAddDB.setOnClickListener(view -> {
            if (mDBToAdd.getText().toString().length() > 0) {
                addDatabaseToList(mDBToAdd.getText().toString());
            }
        });
    }

    private void addDatabaseToList(String dbName){
        if (masterDB.getMasterDao().insert(new MasterDatabaseList(dbName)) > 0) {
            mDBToAdd.setText("");
            setOrRefreshDatabaseList();
        }
    }

    private void setupImportDBButton(){
        mImportDB.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            resultLauncher.launch(intent);
        });
    }
    private void setUpUseSelectedDatabaseButton() {
        mUseSelectedDatabase.setOnClickListener(view -> {
            if (mSelectedDatabaseId > 0) {
                startChecks();

                Intent intent = new Intent(view.getContext(),MainActivity.class); // <- Updated from UseSelectDatabase
                /*intent.putExtra(MainActivity.INTENT_EXTRA_DATABASEID, mSelectedDatabaseId);
                intent.putExtra(MainActivity.INTENT_EXTRA_DATABASENAME,mSelectedDatabaseName);*/
                startActivity(intent);
            }
        });
    }

    private void setOrRefreshDatabaseList() {

        mCsr = masterDB.getMasterDao().getAllDatabasesAsCursor();

        /*if (mSCA == null) {
            mSCA = new SimpleCursorAdapter(
                    this.getApplicationContext(),
                    android.R.layout.simple_list_item_1,
                    mCsr,
                    new String[]{MasterDatabaseList.COL_DATABASE_NAME},
                    new int[]{android.R.id.text1},
                    0
            );
            mDatabaseList.setAdapter(mSCA);
            mDatabaseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                // Handle Clicking on an Item (i.e. prepare UseSelected Button)
                @SuppressLint("Range")
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    mSelectedDatabaseId = l;
                    if (l > 0) {
                        mSelectedDatabaseName = mCsr.getString(mCsr.getColumnIndex(MasterDatabaseList.COL_DATABASE_NAME));
                        mUseSelectedDatabase.setText(mSelectedDatabaseName);
                        mUseSelectedDatabase.setClickable(true);
                    } else {
                        mUseSelectedDatabase.setText(R.string.master_no_db_selected);
                        mUseSelectedDatabase.setClickable(false);
                    }
                }
            });
        } else {
            mSCA.swapCursor(mCsr);
        }*/

        if (mdbCA == null){
            mdbCA = new MDBCursorAdapter(this, mCsr,0);
            mDatabaseList.setAdapter(mdbCA);

            /* Handle Clicking on an Item (i.e. prepare UseSelected Button) */
            mDatabaseList.setOnItemClickListener((adapterView, view, i, l) -> {
                mSelectedDatabaseId = l;
                if (l > 0) {
                    mSelectedDatabaseName = mCsr.getString(mCsr.getColumnIndex(MasterDatabaseList.COL_DATABASE_NAME));
                    mUseSelectedDatabase.setText(mSelectedDatabaseName);
                    mUseSelectedDatabase.setClickable(true);
                } else {
                    mUseSelectedDatabase.setText(R.string.master_no_db_selected);
                    mUseSelectedDatabase.setClickable(false);
                }
            });
        } else {
            mdbCA.swapCursor(mCsr);
            //mSCA.swapCursor(mCsr);
        }
    }

    private void startChecks(){
        settings = sharedPreferences.edit();
        if(isFirstTime()) {
            settings.putString("database", mUseSelectedDatabase.getText().toString());
            settings.commit();
            settings.apply();
            GlobalFilePathVariables.DATABASE = sharedPreferences.getString("database","");
        }else{
            settings.putString("prevDB", sharedPreferences.getString("database", ""));
            settings.putString("database", mUseSelectedDatabase.getText().toString());
            settings.commit();
            settings.apply();
            GlobalFilePathVariables.DATABASE = sharedPreferences.getString("database","");
        }
    }

    private boolean isFirstTime(){
        if (sharedPreferences.getBoolean("firstTime", true)) {
            settings.putBoolean("firstTime", false);
            settings.commit();
            settings.apply();
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setOrRefreshDatabaseList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCsr.close();
    }

    /** The following permissions check functionality was sourced from:
     * https://www.journaldev.com/10409/android-runtime-permissions-example
     */
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), INTERNET);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{INTERNET, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean internetAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean readAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean writeAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (internetAccepted && readAccepted && writeAccepted)
                        Toast.makeText(this, "Permission Granted: Internet, Read and Write.", Toast.LENGTH_LONG).show();
                    else {

                        Toast.makeText(this, "Permission Denied: Internet, Read and Write.", Toast.LENGTH_LONG).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{INTERNET, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }


                break;
        }
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivityMaster.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}