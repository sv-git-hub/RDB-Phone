package com.mistywillow.researchdb;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private final List<SourcesTable> notes;

    public NoteAdapter(Context context, List<SourcesTable> notes){
        this.inflater = LayoutInflater.from(context);
        this.notes = notes;
    }

    @NonNull
    @Override
    public NoteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = inflater.inflate(R.layout.custom_notes_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteAdapter.ViewHolder viewHolder, int i) {

        int noteID = notes.get(i).getNoteID();
        String noteType = notes.get(i).getSourceType();
        String noteSummary = notes.get(i).getSummary();
        String noteSource = notes.get(i).getTitle();
        String noteAuthors = DBQueryTools.concatenateAuthors(DBQueryTools.getAuthorsBySourceID(notes.get(i).getSourceID()));
        viewHolder.nNoteID.setText(String.valueOf(noteID));
        viewHolder.nNoteType.setText(noteType);
        viewHolder.nSummary.setText(noteSummary);
        viewHolder.nSource.setText(noteSource);
        viewHolder.nAuthors.setText(noteAuthors);

    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView nNoteID;
        TextView nNoteType;
        TextView nSummary;
        TextView nSource;
        TextView nAuthors;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nNoteID = itemView.findViewById(R.id.nNoteID);
            nNoteType = itemView.findViewById(R.id.nNoteSourceType);
            nSummary = itemView.findViewById(R.id.nNoteSummary);
            nSource = itemView.findViewById(R.id.nNoteSource);
            nAuthors = itemView.findViewById(R.id.nNoteAuthors);

            itemView.setOnClickListener(v -> {
                Intent i = new Intent(v.getContext(), ViewNote.class);
                i.putExtra("FROM","ADAPTER");
                i.putExtra("ID", notes.get(getBindingAdapterPosition()).getNoteID());
                i.putExtra("Type", notes.get(getBindingAdapterPosition()).getSourceType());
                i.putExtra("Summary", notes.get(getBindingAdapterPosition()).getSummary());
                i.putExtra("Source", notes.get(getBindingAdapterPosition()).getTitle());
                i.putExtra("Authors", DBQueryTools.concatenateAuthors(DBQueryTools.getAuthorsBySourceID(notes.get(getBindingAdapterPosition()).getSourceID())));
                v.getContext().startActivity(i);
            });

        }
    }
}
