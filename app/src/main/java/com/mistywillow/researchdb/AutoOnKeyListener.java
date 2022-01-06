package com.mistywillow.researchdb;

import android.view.KeyEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;

public class AutoOnKeyListener implements View.OnKeyListener {

    private AutoCompleteTextView autoView;

    public AutoOnKeyListener(AutoCompleteTextView auto){
        this.autoView = auto;
    }
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if(autoView.getText().length() == 0 || autoView.getText().toString().trim().equals("")){
            autoView.showDropDown();
        }
            return false;
    }
}
