package com.writesimple.simplenote.model.Tables;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity(tableName = "dataForFirebaseDateAdd")
@TypeConverters(NoteBase.DateStringConverter.class)
public
class dataForFirebaseDateAdd {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String dateAdd;

    public dataForFirebaseDateAdd(String dateAdd) {
        this.dateAdd = dateAdd;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDateAdd() {
        return dateAdd;
    }

    public void setDateAdd(String dateAdd) {
        this.dateAdd = dateAdd;
    }
}
