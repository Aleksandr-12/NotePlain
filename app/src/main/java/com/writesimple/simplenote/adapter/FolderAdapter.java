package com.writesimple.simplenote.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.writesimple.simplenote.R;
import com.writesimple.simplenote.activity.ActivityNotesOfFold;
import com.writesimple.simplenote.model.Tables.FolderBase;
import com.writesimple.simplenote.model.ViewModel.FoldNoteViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FolderAdapter  extends RecyclerView.Adapter<FolderAdapter.FolderViewHolder>{

    private final Context context;
    private List<FolderBase> notes;
    private List<FolderBase> copyNotes;
    FoldNoteViewModel foldNoteViewModel;

    public FolderAdapter(Context context, ArrayList<FolderBase> notes, FoldNoteViewModel foldNoteViewModel) {
        this.context = context;
        this.notes = notes;
        this.foldNoteViewModel = foldNoteViewModel;
    }
    public List<FolderBase> getNotes(){
        return  notes;
    }

    @NonNull
    @Override
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FolderViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_folder, parent, false));    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull FolderViewHolder holder, int position) {

        FolderBase folderItem = notes.get(position);

        Long count = foldNoteViewModel.getCountNoteOfFoldById(folderItem.getmId());
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        String d = formatForDateNow.format(folderItem.getDate());
        holder.title.setText(folderItem.getTitle());
        if(count>0){
            holder.countNote.setText("кол-во "+count.toString());
        }else{
            holder.countNote.setText("кол-во 0");
        }

        holder.itemDate.setText(d);

        holder.list_id.setOnClickListener(view -> {
            Intent intent = new Intent(context, ActivityNotesOfFold.class);
            intent.putExtra("folderItem", folderItem);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return notes == null? 0: notes.size();
    }

    public void addFolder(List<FolderBase> notes,int position){
        this.notes = notes;
        updateSortAndAdapter(position);
        this.copyNotes = this.notes;

    }

    public void searchFilter(String newText) {
        List<FolderBase> newNotes =  this.copyNotes;
        newText = newText.trim().toLowerCase();
        ArrayList<FolderBase> mNewNotes = new ArrayList<>();
        if (newText.length() == 0) {
            mNewNotes.addAll(newNotes);
        } else {
            for (FolderBase item : newNotes) {
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

    public static class FolderViewHolder extends RecyclerView.ViewHolder {
        View list_id;
        TextView title;
        TextView itemDate;
        TextView countNote;
        public FolderViewHolder(View itemView) {
            super(itemView);
            list_id = itemView.findViewById(R.id.list_item);
            title = itemView.findViewById(R.id.title);
            itemDate = (TextView) itemView.findViewById(R.id.itemDate);
            countNote = (TextView) itemView.findViewById(R.id.countNote);
        }
    }
}


