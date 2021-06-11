package com.mistywillow.researchdb;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class FileChooser extends AppCompatActivity {

    public FileChooser(Intent intent){
        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null){
                Uri uri = result.getData().getData();
                filePath = uri.getPath();
            }
        });
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
