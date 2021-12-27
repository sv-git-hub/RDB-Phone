package com.mistywillow.researchdb;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class FileExtensionTextWatcher implements TextWatcher {
    private final String extension;
    private final EditText text;

    FileExtensionTextWatcher(EditText t, String ext) {
        extension = ext;
        text = t;
    }

    // you may want to change around the logic in here to allow for
    // odd entries or other changes - but the overall approach should
    // be similar
    private String removeAutoExtension(String s) {
        String ext = "." + extension;
        if( s.contains(ext) ) {
            int ei = s.lastIndexOf(ext);
            if( ei == 0 ) return "";
            String trailingChar = "";
            if( ei < s.length() - ext.length() ) {
                trailingChar = s.substring(ei+ext.length());
            }
            return s.substring(0,ei) + trailingChar;
        }
        else {
            return s;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

    @Override
    public void afterTextChanged(Editable txt) {
        if( txt == null ) return;

        String currentText = txt.toString();
        String filename = removeAutoExtension(currentText);
        int initCursor = filename.length();
        String displayedText;
        if( filename.isEmpty() ) {
            displayedText = filename;
        }
        else {
            displayedText = filename + "." + extension;
        }

        text.removeTextChangedListener(this);
        txt.clear();
        txt.insert(0, displayedText);
        text.setSelection(initCursor);
        text.addTextChangedListener(this);
    }
}
