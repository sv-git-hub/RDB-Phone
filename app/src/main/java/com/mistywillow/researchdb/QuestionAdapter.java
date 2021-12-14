package com.mistywillow.researchdb;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mistywillow.researchdb.researchdb.entities.Questions;

import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder>{

    private final LayoutInflater inflater;
    private final List<Questions> questions;

    public QuestionAdapter(Context context, List<Questions> questions){
        this.inflater = LayoutInflater.from(context);
        this.questions = questions;
    }

    @NonNull
    @Override
    public QuestionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.custom_list_question, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionAdapter.ViewHolder holder, int position) {
        String question = questions.get(position).getQuestion();
        holder.qItem.setText(question);
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView qItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            qItem = itemView.findViewById(R.id.rv_Question);

        }
    }
}
