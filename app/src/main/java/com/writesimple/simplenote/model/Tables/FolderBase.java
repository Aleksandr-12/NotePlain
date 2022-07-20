package com.writesimple.simplenote.model.Tables;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

@Entity(tableName = "FolderBase")
@TypeConverters(NoteBase.DateStringConverter.class)
public class FolderBase implements Serializable {


    @NonNull
    @PrimaryKey(autoGenerate = true)
    public Long mId;
    public Long parent_id;
    private String title;
    private String note;
    private String fontFamily;

    private String idKeyFolder;
    private int fontSizeTitle;
    private int fontSizeContent;
    private int isBold;
    private int isItalic;

    private String idNoteFirebase;

    public Long getKey() {
        return key;
    }

    public void setKey(Long key) {
        this.key = key;
    }

    private Long key;
    public String getIdKeyFolder() {
        return idKeyFolder;
    }

    public void setIdKeyFolder(String idKeyFolder) {
        this.idKeyFolder = idKeyFolder;
    }

    public Long getParent_id() {
        return parent_id;
    }

    public void setParent_id(Long parent_id) {
        this.parent_id = parent_id;
    }

    public String getIdNoteFirebase() {
        return idNoteFirebase;
    }

    public void setIdNoteFirebase(String idNoteFirebase) {
        this.idNoteFirebase = idNoteFirebase;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    public int getFontSizeTitle() {
        return fontSizeTitle;
    }

    public void setFontSizeTitle(int fontSizeTitle) {
        this.fontSizeTitle = fontSizeTitle;
    }

    public int getFontSizeContent() {
        return fontSizeContent;
    }


    public int getIsItalic() {
        return isItalic;
    }


    public int getIsBold() {
        return isBold;
    }

    public void setIsBold(int isBold) {
        this.isBold = isBold;
    }

    public int getIsUnderline() {
        return isUnderline;
    }

    public void setIsUnderline(int isUnderline) {
        this.isUnderline = isUnderline;
    }

    private int isUnderline;
    private Long date;

    public @NotNull Long getmId() {
        return mId;
    }
    @Ignore
    public FolderBase(String title,Long date,String idKeyFolder) {
        this.title = title;
        this.date = date;
        this.idKeyFolder = idKeyFolder;
    }
    @Ignore
    public FolderBase(){}
    public FolderBase(String title, String note, String fontFamily, int fontSizeTitle, int fontSizeContent, int isBold, int isItalic, int isUnderline, Long date, String idNoteFirebase,Long parent_id,String idKeyFolder) {
        this.title = title;
        this.note = note;
        this.fontFamily = fontFamily;
        this.fontSizeTitle = fontSizeTitle;
        this.fontSizeContent = fontSizeContent;
        this.isBold = isBold;
        this.isItalic = isItalic;
        this.isUnderline = isUnderline;
        this.date = date;
        this.idNoteFirebase = idNoteFirebase;
        this.parent_id = parent_id;
        this.idKeyFolder = idKeyFolder;
    }

    public void setmId(@NotNull Long mId) {
        this.mId = mId;
    }

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

    public static class DateStringConverter {
        @TypeConverter
        public Date FromString(Long value) {
            if (value != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(value);
                return cal.getTime();
            } else return null;

            //return new Date(value);
        }

        @TypeConverter
        public Long ToTimestamp(Date date) {
            if (date != null) {
                return date.getTime();
            }
            return null;
        }
    }
}