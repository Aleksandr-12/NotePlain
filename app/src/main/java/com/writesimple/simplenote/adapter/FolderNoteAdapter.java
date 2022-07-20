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
import com.writesimple.simplenote.model.Tables.FolderBase;
import com.writesimple.simplenote.model.ViewModel.FoldNoteViewModel;
import com.writesimple.simplenote.model.WorkFontManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FolderNoteAdapter  extends RecyclerView.Adapter<FolderNoteAdapter.FolderViewHolder>{

    private final Context context;
    private List<FolderBase> notes;
    private List<FolderBase> copyNotes;
    private static boolean value;
    private final int itemList;
    FoldNoteViewModel foldNoteViewModel;

    public FolderNoteAdapter(Context context, ArrayList<FolderBase> notes, boolean value, FoldNoteViewModel foldNoteViewModel) {
        this.context = context;
        this.notes = notes;
        FolderNoteAdapter.value = value;
        this.foldNoteViewModel = foldNoteViewModel;
        if(value){
            itemList =  R.layout.item_list_note_folder;
        }else{
            itemList =  R.layout.item_list_note;
        }
    }

    public List<FolderBase> getNotes(){
        return  notes;
    }

    @NonNull
    @Override
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FolderViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(itemList, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull FolderViewHolder holder, int position) {

        FolderBase noteItem = notes.get(position);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatForDateNow = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        String d = formatForDateNow.format(noteItem.getDate());
        if(value){
            String foldName = foldNoteViewModel.getFoldName(noteItem.getParent_id());
            if(foldName !=null){
                holder.foldName.setText(foldName);
            }else{
                holder.foldName.setText("no foldName");
            }
        }

        if(noteItem.getTitle()!=null){
            holder.title.setText(noteItem.getTitle());
        }else{
            holder.title.setText("no Title");
        }

        if(noteItem.getNote()!=null){
            holder.content.setText(noteItem.getNote());
        }else{
            holder.content.setText("no Note");
        }

        if(noteItem.getDate()!=null){
            holder.itemDate.setText(d);
        }else{
            holder.itemDate.setText("no Date");
        }
        WorkFontManager workFontManager;
        workFontManager = new WorkFontManager(context);
        workFontManager.setmBold(noteItem.getIsBold());
        workFontManager.setmIsItalic(noteItem.getIsItalic());
        workFontManager.setmUnderline(noteItem.getIsUnderline());
        if(noteItem.getNote()!=null){
            workFontManager.exangeStyleFontForTextView(noteItem.getNote(), holder.content);
        }else{
            workFontManager.exangeStyleFontForTextView("no Note", holder.content);
        }

        workFontManager.setTitleIdTextView(holder.title);
        workFontManager.setContentIdTextView(holder.content);
        if(noteItem.getFontFamily()!=null){
            workFontManager.setFontFamily(noteItem.getFontFamily());
        }else {
            workFontManager.setFontFamily("open_sans");
        }

        workFontManager.setNoteFontFamily();
        workFontManager.setTextSizeFont(noteItem.getFontSizeTitle(),noteItem.getFontSizeContent());

        holder.list_id.setOnClickListener(view -> {
            Intent intent = new Intent(context, ActivityEditNote.class);
            intent.putExtra("folder_note", noteItem);
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return notes == null? 0: notes.size();
    }

    public void addNote(List<FolderBase> notes,int position){
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
        if(value){
            Collections.sort(notes, (lhs, rhs) -> {
                if(lhs.getParent_id() < rhs.getParent_id()) {
                    return -1;
                } else {
                    return 1;
                }
            });
        }else {
            Collections.sort(notes, (lhs, rhs) -> {
                if(lhs.getmId() < rhs.getmId()) {
                    return -1;
                } else {
                    return 1;
                }
            });
        }

        this.copyNotes = this.notes;
        notifyDataSetChanged();
    }

    public void updateSortAndAdapter(int position) {
        switch (position){
            case 0:sort();
                break;
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
        TextView content;
        TextView itemDate;
        TextView foldName;
        public FolderViewHolder(View itemView) {
            super(itemView);
            if(FolderNoteAdapter.value){
                foldName = itemView.findViewById(R.id.fold);
            }
            list_id = itemView.findViewById(R.id.list_id);
            title = itemView.findViewById(R.id.title);
            content = itemView.findViewById(R.id.content);
            itemDate = (TextView) itemView.findViewById(R.id.itemDate);
        }
    }
}


