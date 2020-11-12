package com.example.cora;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    //list to store list of all classes on that day
    private ArrayList<Subject> subjectsList;
    private Context mContext;
    private OnNoteListener onNoteListener;

    //constructor to the class
    public MyAdapter(ArrayList<Subject> subjectsList, Context mContext, OnNoteListener onNoteListener){
        this.subjectsList = subjectsList;
        this.mContext = mContext;
        this.onNoteListener = onNoteListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_item_design, parent, false);
        return new MyViewHolder(view,onNoteListener);

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        Subject s = subjectsList.get(position);
        holder.setLecture(s.symbol);
        holder.setTiming(s.time);
        if(s.bunk) holder.setBunk("Bunk");
        else holder.setBunk("");

        if(s.cancelled) holder.setCancel("Cancelled");
        else holder.setCancel("");


    }

    @Override
    public int getItemCount() {
        return subjectsList.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView lecture;
        private TextView timing;
        private TextView bunk;
        private TextView cancel;
        private OnNoteListener onNoteListener;

        public MyViewHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);

            lecture = itemView.findViewById(R.id.lecture_name_text_view);
            timing = itemView.findViewById(R.id.timing_text_view);
            bunk = itemView.findViewById(R.id.bunk_text_view);
            cancel = itemView.findViewById(R.id.cancel_text_view);
            this.onNoteListener = onNoteListener;

            itemView.setOnClickListener(this);

        }

        public void setLecture(String lecture) {
            this.lecture.setText(lecture);;
        }

        public void setTiming(String timing) {
            this.timing.setText(timing);
        }

        public void setBunk(String bunk) {
            this.bunk.setText(bunk);
        }

        public void setCancel(String cancel) {this.cancel.setText(cancel);}


        @Override
        public void onClick(View view) {

            onNoteListener.OnNoteClick(getAdapterPosition());

        }
    }

    public interface OnNoteListener {

        void OnNoteClick(int position);

    }

}
