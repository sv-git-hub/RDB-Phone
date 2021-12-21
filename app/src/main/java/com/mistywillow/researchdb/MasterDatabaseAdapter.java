package com.mistywillow.researchdb;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mistywillow.researchdb.masterdb.entity.MasterDatabaseList;

import java.util.List;

public class MasterDatabaseAdapter extends RecyclerView.Adapter<MasterDatabaseAdapter.ViewHolder>{

    private final LayoutInflater inflater;
    private final List<MasterDatabaseList> databases;

    public MasterDatabaseAdapter(Context context, List<MasterDatabaseList> databases){
        this.inflater = LayoutInflater.from(context);
        this.databases = databases;
    }


    @NonNull
    @Override
    public MasterDatabaseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = inflater.inflate(R.layout.custom_database_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MasterDatabaseAdapter.ViewHolder holder, int position) {

        String dbName = databases.get(position).databaseName;
    }

    @Override
    public int getItemCount() {
        return databases.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView mdlDatabaseName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mdlDatabaseName = itemView.findViewById(R.id.database_items);

            itemView.setOnClickListener(v -> {

            });

        }
    }
}
