package com.writesimple.simplenote.model.Tables;
import java.util.Date;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity(tableName = "UpdateFolderNoteUploadOnFirebase")
@TypeConverters(NoteBase.DateStringConverter.class)
public class UpdateFolderNoteUploadOnFirebase {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String idKeyFolder;
    private String idNoteFirebase;

    private int updateFolderNote;
    private int addFolderNote;
    private int deleteFolderNote;

    private Long parent_id;
    private Long date;

    public UpdateFolderNoteUploadOnFirebase(String idKeyFolder, String idNoteFirebase, int updateFolderNote, int addFolderNote, int deleteFolderNote, Long parent_id, Long date) {
        this.idKeyFolder = idKeyFolder;
        this.idNoteFirebase = idNoteFirebase;
        this.updateFolderNote = updateFolderNote;
        this.addFolderNote = addFolderNote;
        this.deleteFolderNote = deleteFolderNote;
        this.parent_id = parent_id;
        this.date = date;
    }
    @Ignore
    public UpdateFolderNoteUploadOnFirebase(String idKeyFolder, String idNoteFirebase, int updateFolderNote, int addFolderNote, int deleteFolderNote, Long date) {
        this.idKeyFolder = idKeyFolder;
        this.idNoteFirebase = idNoteFirebase;
        this.updateFolderNote = updateFolderNote;
        this.addFolderNote = addFolderNote;
        this.deleteFolderNote = deleteFolderNote;
        this.date = date;
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIdKeyFolder() {
        return idKeyFolder;
    }

    public void setIdKeyFolder(String idKeyFolder) {
        this.idKeyFolder = idKeyFolder;
    }

    public String getIdNoteFirebase() {
        return idNoteFirebase;
    }

    public void setIdNoteFirebase(String idNoteFirebase) {
        this.idNoteFirebase = idNoteFirebase;
    }

    public int getUpdateFolderNote() {
        return updateFolderNote;
    }

    public void setAddFolderNote(int addFolderNote) {
        this.addFolderNote = addFolderNote;
    }

    public void setDeleteFolderNote(int deleteFolderNote) {
        this.deleteFolderNote = deleteFolderNote;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Date Long) {
        this.date = date;
    }

    public void setUpdateFolderNote(int updateFolderNote) {
        this.updateFolderNote = updateFolderNote;
    }

    public int getAddFolderNote() {
        return addFolderNote;
    }


    public int getDeleteFolderNote() {
        return deleteFolderNote;
    }

    public Long getParent_id() {
        return parent_id;
    }

    public void setParent_id(Long parent_id) {
        this.parent_id = parent_id;
    }

}

