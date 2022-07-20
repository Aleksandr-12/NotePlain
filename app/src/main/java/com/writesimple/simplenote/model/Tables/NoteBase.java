package com.writesimple.simplenote.model.Tables;
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

@Entity(tableName = "NoteBase")
@TypeConverters(NoteBase.DateStringConverter.class)
public class NoteBase implements Serializable {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    public Long mId;
    private String title;
    private String content;
    private String fontFamily;
    private  int fontSizeTitle;
    private int fontSizeContent;
    private int isBold;
    private int isItalic;

    public String getIdNoteFirebase() {
        return idNoteFirebase;
    }

    public void setIdNoteFirebase(String idNoteFirebase) {
        this.idNoteFirebase = idNoteFirebase;
    }

    private String idNoteFirebase;


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

    public void setFontSizeContent(int fontSizeContent) {
        this.fontSizeContent = fontSizeContent;
    }

    public int getIsItalic() {
        return isItalic;
    }

    public void setIsItalic(int isItalic) {
        this.isItalic = isItalic;
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

    private  int isUnderline;

    private Long date;

    public Long getmId() {
        return mId;
    }
    @Ignore
    public NoteBase(String title, String content,Long date, String idNoteFirebase){
        this.title = title;
        this.content = content;
        this.date = date;
        this.idNoteFirebase = idNoteFirebase;
    }
    @Ignore
    public NoteBase(){}


    public NoteBase(String title, String content, String fontFamily, int fontSizeTitle, int fontSizeContent, int isBold, int isItalic,  int isUnderline, Long date,String idNoteFirebase) {
        this.mId = mId;
        this.title = title;
        this.content = content;
        this.fontFamily = fontFamily;
        this.fontSizeTitle = fontSizeTitle;
        this.fontSizeContent = fontSizeContent;
        this.isBold = isBold;
        this.isItalic = isItalic;
        this.isUnderline = isUnderline;
        this.date = date;
        this.idNoteFirebase = idNoteFirebase;
    }

    public void setmId(Long mId) {
        this.mId = mId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
           if(value!=null){
               Calendar cal = Calendar.getInstance();
               cal.setTimeInMillis(value);
               return cal.getTime();
           }else return  null;

          //return new Date(value);
        }

        @TypeConverter
        public Long ToTimestamp(Date date){
           if(date!=null){
               return date.getTime();
           }
            return  null;
        }
    }
}
