package com.writesimple.simplenote.FireBase;
import com.writesimple.simplenote.model.Tables.NoteBase;

import java.util.Date;
import androidx.room.TypeConverters;

@TypeConverters(NoteBase.DateStringConverter.class)
public class FoldFireBase {
    private String title;
    private String note;
    private Long parent_id;

    public Long getParent_id() {
        return parent_id;
    }

    public void setParent_id(Long parent_id) {
        this.parent_id = parent_id;
    }

    @TypeConverters(NoteBase.DateStringConverter.class)
    private Long date;
    private String idKeyFolder;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Long getDate() {
        return date;
    }

    public String getIdKeyFolder() {
        return idKeyFolder;
    }

    public FoldFireBase(){}

    public FoldFireBase(String idKeyFolder, Long date, String title){
        this.idKeyFolder = idKeyFolder;
        this.date = date;
        this.title = title;
    }
    public FoldFireBase(String title, String note, Long date, String idKeyFolder) {
        this.title = title;
        this.note = note;
        this.date = date;
        this.idKeyFolder = idKeyFolder;
    }

    public void setIdKeyFolder(String idKeyFolder) {
        this.idKeyFolder = idKeyFolder;
    }

    public void setDate(Long date) {
        this.date = date;
    }

}

