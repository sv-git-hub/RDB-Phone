package com.mistywillow.researchdb;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class FileChooser extends AppCompatActivity {
    private String fileName;
    private String filePath;
    private final ActivityResultLauncher<Intent> resultLauncher;

    public FileChooser(){
        this.resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null){
                Uri uri = result.getData().getData();
                this.filePath = uri.getPath();
            }
        });
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        resultLauncher.launch(intent);
    }

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
