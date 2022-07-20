package com.writesimple.simplenote.model.Dao;
import com.writesimple.simplenote.model.Tables.NoteBase;

import java.util.Date;
import java.util.List;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.TypeConverters;
import io.reactivex.Completable;
import io.reactivex.Flowable;

@Dao
@TypeConverters(NoteBase.DateStringConverter.class)
public interface NoteDao {

    @Query("SELECT * FROM noteBase")
    LiveData<List<NoteBase>> getAll();

    @Query("SELECT * FROM NoteBase")
    Flowable<List<NoteBase>> getAllRx();

    @Query("SELECT * FROM noteBase")
    List<NoteBase> getAllWithoutlivedata();

    @Query("SELECT * FROM noteBase WHERE idNoteFirebase IS NULL OR  idNoteFirebase = '' OR idNoteFirebase = 'null'")
    List<NoteBase> getAllWithoutIdKey();

// SELECT * FROM `NoteBase` WHERE NOT EXISTS (SELECT * FROM `NoteBase` WHERE `idNoteFirebase`= :mIdNoteFirebase)where on duplicate key update idNoteFirebase =:mIdNoteFirebase
    @Query("INSERT INTO `NoteBase` (title,content,fontSizeTitle,fontSizeContent,isBold, isItalic, isUnderline,date,idNoteFirebase) VALUES (:mTitle,  :mContent, :fontSizeTitle,:fontSizeContent, :mBold, :mIsItalic, :mUnderline, :mDate," +
            " :mIdNoteFirebase) ")
    void insertFromFirebase(String mTitle, String mContent, int fontSizeTitle, int fontSizeContent,int mBold, int mIsItalic, int mUnderline, Long mDate, String mIdNoteFirebase);

    @Query("SELECT * FROM noteBase WHERE mId = :mId")
    NoteBase getContent(int mId);

    /*@Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertList(List<NoteBase> list);*/

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void  insert(NoteBase note);

    @Insert
    Completable insertRx(NoteBase note);

    @Query("UPDATE notebase SET title = :title, content=:content, fontFamily=:mFontFamily,fontSizeTitle=:mFontSizeTitle,fontSizeContent=:mFontSizeContent,isBold=:mBold, isItalic=:mIsItalic,isUnderline=:mUnderline WHERE mId =:mId")
    void update(Long mId, String title, String content, String mFontFamily, int mFontSizeTitle, int mFontSizeContent, int mBold, int mIsItalic, int mUnderline);

    @Query("UPDATE notebase SET idNoteFirebase =:idNoteFirebase WHERE mId =:mId")
    void updateIdKey(Long mId,String idNoteFirebase);

    @Query("DELETE FROM notebase WHERE mId =:mId")
    void deleteById(Long mId);

    @Query("SELECT * FROM noteBase")
    List<NoteBase> get();
}
