package com.mistywillow.researchdb;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class FileChooser extends AppCompatActivity {

    public FileChooser(Intent intent){
        if(intent==null) Toast.makeText(null, "Intent is Null", Toast.LENGTH_SHORT).show();
        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null){
                Uri uri = result.getData().getData();
                filePath = uri.getPath();
            }
        });
        if(resultLauncher==null)Toast.makeText(null, "Launcher is Null", Toast.LENGTH_SHORT).show();
        resultLauncher.launch(intent);
    }

    private String fileName;
    private String filePath;
    private final ActivityResultLauncher<Intent> resultLauncher;

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public ActivityResultLauncher<Intent> getResultLauncher() {
        return resultLauncher;
    }
}
