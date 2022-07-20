package com.writesimple.simplenote.FireBase;

import java.util.Date;

public class NoteFireBase {
    private String title;
    private String note;
    private Long date;
    private Long id;
    private String idK;
    private Long parent_id;

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

    public void setDate(Long date) {
        this.date = date;
    }
    public NoteFireBase(){}

    public NoteFireBase(String idK,Long id,Long date,String title, String note){
        this.idK = idK;
        this.id = id;
        this.date = date;
        this.title = title;
        this.note = note;
    }

    public Long getParent_id() {
        return parent_id;
    }

    public void setParent_id(Long parent_id) {
        this.parent_id = parent_id;
    }

    public NoteFireBase(String idK, Long date, String title, String note){
        this.idK = idK;
        this.date = date;
        this.title = title;
        this.note = note;
    }

    public NoteFireBase(String idK,Long date,String title, String note, Long parent_id){
        this.idK = idK;
        this.date = date;
        this.title = title;
        this.note = note;
        this.parent_id = parent_id;
    }
}
