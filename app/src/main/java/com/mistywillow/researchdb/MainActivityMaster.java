package com.mistywillow.researchdb;

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
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.mistywillow.researchdb.databases.MasterDatabase;
import com.mistywillow.researchdb.databases.ResearchDatabase;
import com.mistywillow.researchdb.masterdb.entity.MasterDatabaseList;

import java.io.*;

import static android.Manifest.permission.*;
import static androidx.core.content.FileProvider.getUriForFile;

public class MainActivityMaster extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor settings;

    ActivityResultLauncher<Intent> resultLauncher;

    MasterDatabase masterDB;
    EditText mDBToAdd;
    Button mAddDB, mImportDB, mGuide;
    ListView mDatabaseList;
    MDBCursorAdapter mdbCA;
    Cursor mCsr;
    long mSelectedDatabaseId = 0;
    String mSelectedDatabaseName = "";

    private static final int PERMISSION_REQUEST_CODE = 200;
    private String strDBName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_master);

        if(!checkPermission())
            requestPermission();

        sharedPreferences = getSharedPreferences(Globals.SHARED_PREF_FILE, MODE_PRIVATE);
        CopyAssets.copyAssets(this, "ResearchDB.pdf");

        mGuide = this.findViewById(R.id.guide_btn);
        mDBToAdd = this.findViewById(R.id.database_name);
        mAddDB = this.findViewById(R.id.addDatabase);
        mImportDB = this.findViewById(R.id.importDatabase);
        mDatabaseList = this.findViewById(R.id.database_list);
        masterDB = MasterDatabase.getInstance(this);
        mDBToAdd.addTextChangedListener(new FileExtensionTextWatcher(mDBToAdd, "db"));

        setupGuideButton();
        setUpAddDBButton();
        setupImportDBButton();
        setOrRefreshDatabaseList();

        // resultLauncher Imports the database and add it to the list to be selected
        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null){
                Uri uri = result.getData().getData();
                /*String sourcePath = RealPathUtil.getRealPath(this, uri);
                String strDBName = sourcePath.substring(sourcePath.lastIndexOf("/")+1);*/
                databaseNameAndSize(uri);
                String destinationPath = this.getDatabasePath(strDBName).getPath();

                if(strDBName.endsWith(".db")) {
                    try {
                        InputStream source = this.getContentResolver().openInputStream(uri);
                        OutputStream destination = this.getContentResolver().openOutputStream(Uri.fromFile(new File(destinationPath)));
                        DatabaseManager.copyDatabase(source, destination);
                        if(validateDatabase(strDBName)){
                            addDatabaseToList(strDBName);
                        }
                        source.close();
                        destination.close();
                    } catch (IOException f) {
                        PopupDialog.AlertMessageOK(this, "Wrong Database Type", "Only ResearchDB databases can be imported and must end in '.db'");
                    }
                }else{
                    PopupDialog.AlertMessageOK(this, "Wrong Database Type", "Only ResearchDB databases can be imported and must end in '.db'");
                }
            }
        });
    }

    private void databaseNameAndSize(Uri dbURI){

        Cursor returnCursor = this.getContentResolver().query(dbURI, new String[]{
                OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE
        }, null, null, null);

        /*
         * Get the column indexes of the data in the Cursor,
         *     * move to the first row in the Cursor, get the data,
         *     * and display it.
         * */
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        strDBName = (returnCursor.getString(nameIndex));
        returnCursor.close();
    }

    private boolean validateDatabase(String dbName){
        ResearchDatabase rdb = ResearchDatabase.getInstance(this, dbName);
        try {
            SupportSQLiteDatabase supportDB = rdb.getOpenHelper().getWritableDatabase();
            return true;
        }catch (Exception e){
            deleteDatabase(dbName);
            PopupDialog.AlertMessageOK(this, "Database Failure", dbName +
                    " failed to load for the following reason:\n\n" + e);
        }
        return false;
    }

    /**https://stackoverflow.com/questions/1109022/how-do-you-close-hide-the-android-soft-keyboard-programmatically*/
    private static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(MainActivityMaster.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void setupGuideButton(){
        mGuide.setOnClickListener(v -> getHelp());
    }

    private void setUpAddDBButton() {
        mAddDB.setOnClickListener(view -> {
            if (mDBToAdd.getText().toString().length() > 0) {
                hideKeyboardFrom(MainActivityMaster.this, view);
                addDatabaseToList(mDBToAdd.getText().toString());
            }
        });
    }

    private void setupImportDBButton(){
        mImportDB.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            resultLauncher.launch(intent);
        });
    }

    private void addDatabaseToList(String dbName){
        if (masterDB.getMasterDao().insert(new MasterDatabaseList(dbName)) > 0) {
            mDBToAdd.setText("");
            setOrRefreshDatabaseList();
        }
    }

    private void setOrRefreshDatabaseList() {

        mCsr = masterDB.getMasterDao().getAllDatabasesAsCursor();

        if (mdbCA == null){
            mdbCA = new MDBCursorAdapter(this, mCsr,0);
            mDatabaseList.setAdapter(mdbCA);

            /* Handle Clicking on an Item (i.e. prepare UseSelected Button) */
            mDatabaseList.setOnItemClickListener((adapterView, view, i, l) -> {
                mSelectedDatabaseId = l;
                if (l > 0) {
                    mSelectedDatabaseName = mCsr.getString(mCsr.getColumnIndex(MasterDatabaseList.COL_DATABASE_NAME));
                    startChecks();
                    Intent intent = new Intent(view.getContext(),MainActivity.class); // <- Updated from UseSelectDatabase
                    startActivity(intent);
                }
            });
        } else {
            mdbCA.swapCursor(mCsr);
        }
    }

    private void startChecks(){
        settings = sharedPreferences.edit();
        if(isFirstTime()) {
            settings.putString("database", mSelectedDatabaseName);
            settings.commit();
            settings.apply();
            Globals.DATABASE = sharedPreferences.getString("database","");
        }else{
            settings.putString("prevDB", sharedPreferences.getString("database", ""));
            settings.putString("database", mSelectedDatabaseName);
            settings.commit();
            settings.apply();
            Globals.DATABASE = sharedPreferences.getString("database","");
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

    private void getHelp(){
        MimeTypeMap myMime = MimeTypeMap.getSingleton();
        File helpFile = new File(getCacheDir()+ "/ResearchDB.pdf");
        String mimeType = myMime.getMimeTypeFromExtension(getFileExtension(helpFile.getName()));
        Uri contentUri = getUriForFile(MainActivityMaster.this, "com.mistywillow.fileprovider", helpFile);
        openFile(contentUri, mimeType);
    }

    private String getFileExtension(String fileName){
        String[] ext = fileName.split("[.]");
        return ext[ext.length-1];
    }


    private void openFile(Uri uri, String mime){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri,mime);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
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
     * https://www.journaldev.com/10409/android-runtime-permissions-example*/
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), INTERNET);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{INTERNET, READ_EXTERNAL_STORAGE,
                WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
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
                                showMessageOKCancel("You need to allow access permissions for " +
                                                getResources().getString(R.string.app_name) + " to be used.",
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