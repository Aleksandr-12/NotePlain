package com.writesimple.simplenote.model.Tables;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity(tableName = "dataForFirebaseDateUpdate")
@TypeConverters(NoteBase.DateStringConverter.class)
public
class dataForFirebaseDateUpdate {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String dateUpdate;

    public String getDateUpdate() {
        return dateUpdate;
    }

    public dataForFirebaseDateUpdate(String dateUpdate) {
        this.dateUpdate = dateUpdate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
