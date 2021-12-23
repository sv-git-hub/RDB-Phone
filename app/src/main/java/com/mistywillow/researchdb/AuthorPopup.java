package com.mistywillow.researchdb;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.mistywillow.researchdb.researchdb.entities.Sources;

import java.util.List;

public class AuthorPopup extends AppCompatActivity implements PopupAdapter.IPopupRecycler {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppThemeLauncher);
        setContentView(R.layout.popup_sources_dialog);

        Intent src = getIntent();
        List<Sources> sources = src.getParcelableArrayListExtra("sources");

        String text = sources.get(0).getTitle() + ": This title is published by more than one author(s). " +
                "Click on the author(s) that pertain to the source you want to reference. If this is a new " +
                "author(s), then click on any option, then when you return to the screen, add the new authors " +
                "in the Authors table, including whom may already be in the data base.";

        TextView description = findViewById(R.id.popupDescription);
        description.setText(text);

        RecyclerView listAuthors = findViewById(R.id.ListAuthors);
        listAuthors.setLayoutManager(new LinearLayoutManager(this));
        PopupAdapter popupAdapter = new PopupAdapter(this, sources, this);
        listAuthors.setAdapter(popupAdapter);
    }

    @Override
    public void correctSourceAuthor(Sources source) {
        Intent intent = new Intent(this, AddNote.class);
        intent.putExtra("source", source);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
