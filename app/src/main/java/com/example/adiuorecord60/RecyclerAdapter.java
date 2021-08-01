package com.example.adiuorecord60;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.Holder> {
    private File[] voices;

    private RecyclerViewOnItemClick listener;

    public RecyclerAdapter(File[] voices) {

        this.voices = voices;
    }

    interface RecyclerViewOnItemClick {
        void onItemClickListener(File file, int position);
    }


    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.name.setText(voices[position].getName());

        String date=new SimpleDateFormat("yyyy/MM/dd-hh:mm:ss", Locale.US).format(new Date(voices[position].lastModified()));

        holder.date.setText(date);



    }

    @Override
    public int getItemCount() {
        return voices.length;
    }

    class Holder extends RecyclerView.ViewHolder {

        TextView name, date;

        public Holder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.item_name);
            date = itemView.findViewById(R.id.item_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                        listener.onItemClickListener(voices[getAdapterPosition()], getAdapterPosition());
                    }
                }
            });

        }
    }


    public void setOnItemClickListener(RecyclerViewOnItemClick listener) {
        this.listener = listener;
    }
}
