package com.writesimple.simplenote.model.Tables;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity(tableName = "dataForFirebaseDateDelete")
@TypeConverters(NoteBase.DateStringConverter.class)
public class dataForFirebaseDateDelete {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String dateDelete;

    public dataForFirebaseDateDelete(String dateDelete) {

        this.dateDelete = dateDelete;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDateDelete() {
        return dateDelete;
    }

}
