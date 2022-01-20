package com.mistywillow.researchdb;

import android.view.View;
import android.widget.AutoCompleteTextView;

public class AutoOnFocusChangeListener implements View.OnFocusChangeListener {

    private AutoCompleteTextView autoView;

    public AutoOnFocusChangeListener(AutoCompleteTextView auto){
        this.autoView = auto;
    }
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus && autoView.getText().toString().equals("")) {
            autoView.showDropDown();
        }
    }
}
