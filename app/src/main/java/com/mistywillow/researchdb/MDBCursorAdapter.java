package com.mistywillow.researchdb;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.mistywillow.researchdb.masterdb.entity.MasterDatabaseList;

public class MDBCursorAdapter extends CursorAdapter {

    private LayoutInflater inflater;

    public MDBCursorAdapter(Context context, Cursor cursor, int flags){
        super(context, cursor, flags);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(R.layout.custom_database_list, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView dbItem = view.findViewById(R.id.database_items);
        String dbName = cursor.getString(cursor.getColumnIndex(MasterDatabaseList.COL_DATABASE_NAME));
        dbItem.setText(dbName);
    }
}
