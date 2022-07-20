package com.writesimple.simplenote.model.Tables;


import java.util.Date;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity(tableName = "User")
@TypeConverters(NoteBase.DateStringConverter.class)
public class User {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    public Long mId;

    public String isNotify;

    public String getIsSeeSubs() {
        return isSeeSubs;
    }

    public void setIsSeeSubs(String isSeeSubs) {
        this.isSeeSubs = isSeeSubs;
    }

    public String isSeeSubs;
    public String isBuy;

    public String getIsNotify() {
        return isNotify;
    }

    public String getIsBuy() {
        return isBuy;
    }

    public void setIsBuy(String isBuy) {
        this.isBuy = isBuy;
    }

    public void setIsNotify(String isNotify) {
        this.isNotify = isNotify;
    }

    public Long getmId() {
        return mId;
    }

    public void setmId(Long mId) {
        this.mId = mId;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    @TypeConverters(NoteBase.DateStringConverter.class)
    private Long date;

    @Ignore
    public User(){}
    @Ignore
    public User(Long date,String isNotify,String isSeeSubs,String isBuy) {
        this.date = date;
        this.isNotify = isNotify;
        this.isSeeSubs = isSeeSubs;
        this.isBuy = isBuy;
    }
    public User(Long date,String isNotify,String isSeeSubs) {
        this.date = date;
        this.isNotify = isNotify;
        this.isSeeSubs = isSeeSubs;
    }
}
