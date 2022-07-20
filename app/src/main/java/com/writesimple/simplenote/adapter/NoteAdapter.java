package com.writesimple.simplenote.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.writesimple.simplenote.R;
import com.writesimple.simplenote.activity.ActivityEditNote;
import com.writesimple.simplenote.model.RxBus;
import com.writesimple.simplenote.model.Tables.NoteBase;
import com.writesimple.simplenote.model.WorkFontManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NoteAdapter  extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder>{

        private final Context context;
        private List<NoteBase> notes;
        private List<NoteBase> copyNotes;

    public NoteAdapter(Context context, ArrayList<NoteBase> notes) {
            this.context = context;
            this.notes = notes;
        }

    public List<NoteBase> getNotes(){
             return  notes;
        }

        @NonNull
        @Override
        public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new NoteViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_note, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {

            NoteBase noteItem = notes.get(position);
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat formatForDateNow = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            String d = formatForDateNow.format(noteItem.getDate());
            holder.title.setText(noteItem.getTitle());
            holder.content.setText(noteItem.getContent());
            holder.itemDate.setText(d);
            WorkFontManager workFontManager;
            workFontManager = new WorkFontManager(context);
            workFontManager.setmBold(noteItem.getIsBold());
            workFontManager.setmIsItalic(noteItem.getIsItalic());
            workFontManager.setmUnderline(noteItem.getIsUnderline());
            workFontManager.exangeStyleFontForTextView(noteItem.getContent(), holder.content);

            workFontManager.setTitleIdTextView(holder.title);
            workFontManager.setContentIdTextView(holder.content);
            workFontManager.setFontFamily(noteItem.getFontFamily());
            workFontManager.setNoteFontFamily();
            workFontManager.setTextSizeFont(noteItem.getFontSizeTitle(),noteItem.getFontSizeContent());

            holder.list_id.setOnClickListener(view -> {
                Intent intent = new Intent(context, ActivityEditNote.class);
                intent.putExtra("detail_note", noteItem);
                context.startActivity(intent);

            });
        }

        @Override
        public int getItemCount() {
            return notes == null? 0: notes.size();
        }

    public void addNote(List<NoteBase> notes,int position){
        this.notes = notes;
        updateSortAndAdapter(position);
        this.copyNotes = this.notes;
    }

    public void searchFilter(String newText) {
        List<NoteBase> newNotes =  this.copyNotes;
        newText = newText.trim().toLowerCase();
        ArrayList<NoteBase> mNewNotes = new ArrayList<>();
        if (newText.length() == 0) {
            mNewNotes.addAll(newNotes);
        } else {
            for (NoteBase item : newNotes) {
                if (item.getTitle().trim().toLowerCase(Locale.getDefault()).contains(newText)) {
                    mNewNotes.add(item);
                }
            }
        }
        notes = mNewNotes;
        notifyDataSetChanged();
    }

    public void sortOfdate(){
        Collections.sort(notes, (lhs, rhs) -> {
            // return lhs.getmId().compareTo(rhs.getmId());
            if(lhs.getmId() > rhs.getmId()) {
                return -1;
            } else {
                return 1;
            }
        });
        this.copyNotes = this.notes;
        notifyDataSetChanged();
    }

    public void sortOfTitle(){
        Collections.sort(notes, (lhs, rhs) -> lhs.getTitle().compareTo(rhs.getTitle()));
        this.copyNotes = this.notes;
        notifyDataSetChanged();
    }

    public void sort(){
        Collections.sort(notes, (lhs, rhs) -> {
            // return lhs.getmId().compareTo(rhs.getmId());
            if(lhs.getmId() < rhs.getmId()) {
                return -1;
            } else {
                return 1;
            }
        });
        this.copyNotes = this.notes;
        notifyDataSetChanged();
    }

    public void updateSortAndAdapter(int position) {
        switch (position){
            case 1:sortOfdate();
                 break;
            case 2:sortOfTitle();
                break;
            default:sort();
        }
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
            View list_id;
            TextView title;
            TextView content;
            TextView itemDate;
        public NoteViewHolder(View itemView) {
                super(itemView);
                list_id = itemView.findViewById(R.id.list_id);
                title = itemView.findViewById(R.id.title);
                content = itemView.findViewById(R.id.content);
                itemDate = (TextView) itemView.findViewById(R.id.itemDate);
            }
        }
    }

