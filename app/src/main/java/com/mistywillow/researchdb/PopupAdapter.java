package com.mistywillow.researchdb;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mistywillow.researchdb.researchdb.entities.Sources;

import java.util.List;

public class PopupAdapter extends RecyclerView.Adapter<PopupAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private final List<Sources> sources;
    private final IPopupRecycler mListener;

    public PopupAdapter(Context context, List<Sources> sources, IPopupRecycler mListener){
        this.inflater = LayoutInflater.from(context);
        this.sources = sources;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public PopupAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = inflater.inflate(R.layout.custom_select_authors_list, viewGroup, false);
        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PopupAdapter.ViewHolder viewHolder, int i) {
        String authors = DBQueryTools.concatenateAuthors(DBQueryTools.getAuthorsBySourceID(sources.get(i).getSourceID()));
        viewHolder.author.setText(authors);
        if(sources.get(i).getSourceID()==0)
            viewHolder.author.setText(R.string.new_source);
    }

    @Override
    public int getItemCount() {
        return sources.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView author;
        IPopupRecycler mListener;
        public ViewHolder(@NonNull View itemView, IPopupRecycler mListener){
            super(itemView);
            this.mListener = mListener;
            author = itemView.findViewById(R.id.popupAuthor);
            itemView.setOnClickListener(v ->{
                //String temp = DBQueryTools.concatenateAuthors(DBQueryTools.getAuthorsBySourceID(sources.get(getBindingAdapterPosition()).getSourceID()));
                mListener.correctSourceAuthor(sources.get(getBindingAdapterPosition()));

            });
        }
    }
    interface IPopupRecycler{
        void correctSourceAuthor(Sources source);
    }
}
