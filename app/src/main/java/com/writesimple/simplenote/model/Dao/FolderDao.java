package com.writesimple.simplenote.model.Dao;
import com.writesimple.simplenote.model.Tables.FolderBase;
import com.writesimple.simplenote.model.Tables.NoteBase;

import java.util.Date;
import java.util.List;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.TypeConverters;
import io.reactivex.Flowable;
import io.reactivex.Observable;

@Dao
@TypeConverters(NoteBase.DateStringConverter.class)
public interface FolderDao {

    @Query("SELECT * FROM FolderBase WHERE parent_id IS NULL OR parent_id = '' OR parent_id = null")
    LiveData<List<FolderBase>> getAll();

    @Query("SELECT * FROM FolderBase WHERE parent_id IS NULL OR parent_id = '' OR parent_id = null")
    Flowable<List<FolderBase>> getAllRx();

    @Query("SELECT * FROM FolderBase")
    List<FolderBase> getAllAbsolute();

    @Query("SELECT title FROM FolderBase WHERE mId =:parent_id")
    String getFoldName(Long parent_id);

    @Query("SELECT * FROM FolderBase WHERE  parent_id = :parent_id")
    List<FolderBase> getAllByParentId(Long parent_id);

    @Query("SELECT * FROM FolderBase WHERE parent_id IS NULL OR parent_id = '' OR parent_id = null")
    List<FolderBase> getAllFolders();

    @Query("SELECT idKeyFolder FROM FolderBase WHERE mId = :id")
    String getFolderById(Long id);

    @Query("SELECT * FROM FolderBase WHERE parent_id = :id")
    LiveData<List<FolderBase>> getAllNotesOfFolder(Long id);

    @Query("SELECT * FROM FolderBase WHERE parent_id = :id")
    Flowable<List<FolderBase>> getAllNotesOfFoldRx(Long id);

    @Query("SELECT * FROM FolderBase WHERE parent_id = :id")
    List<FolderBase> getAllNotesOfFolderWithoutLiveData(Long id);

    @Query("SELECT * FROM FolderBase ORDER BY parent_id")
    List<FolderBase> getAllWithoutlivedata();

    @Query("SELECT * FROM FolderBase WHERE parent_id IS NOT NULL ORDER BY parent_id")
    List<FolderBase> getAllIsNotNullParentId();

    @Query("SELECT * FROM FolderBase WHERE parent_id IS NOT NULL ORDER BY parent_id")
    LiveData<List<FolderBase>> getAllIsNotNullParentIdLiveData();


    @Query("SELECT * FROM FolderBase WHERE parent_id IS NOT NULL ORDER BY parent_id")
    Flowable<List<FolderBase>> getAllIsNotNullParentIdRx();

    @Query("SELECT mId FROM FolderBase WHERE idKeyFolder = :idKeyFolder")
    Long getIdByIdKeyFolder(String idKeyFolder);

    @Query("SELECT * FROM FolderBase WHERE idKeyFolder = :idKeyFolder")
    FolderBase getFolderIdKeyFolder(String idKeyFolder);

    @Query("SELECT count(*) FROM FolderBase WHERE parent_id = :mId")
    Long getCountNoteOfFoldById(Long mId);

    @Query("UPDATE FolderBase SET parent_id = :parent_id WHERE parent_id = 123456789 AND idKeyFolder = :idKeyFolder AND idNoteFirebase != '' AND idNoteFirebase != 'null' AND idNoteFirebase IS NOT NULL")
    void updateByIdKeyFolder(Long parent_id,String idKeyFolder);

    @Query("UPDATE FolderBase SET parent_id = :parent_id WHERE parent_id = 123456789 AND idNoteFirebase = :idNoteFirebase AND idNoteFirebase != '' AND idNoteFirebase != 'null' AND idNoteFirebase IS NOT NULL")
    void updateIdByNoteFirebase(Long parent_id,String idNoteFirebase);

    @Query("SELECT * FROM FolderBase WHERE idNoteFirebase IS NULL OR  idNoteFirebase = '' OR idNoteFirebase = 'null'")
    List<FolderBase> getAllWithoutIdKey();
    // SELECT * FROM `NoteBase` WHERE NOT EXISTS (SELECT * FROM `NoteBase` WHERE `idNoteFirebase`= :mIdNoteFirebase)where on duplicate key update idNoteFirebase =:mIdNoteFirebase
    @Query("INSERT INTO `FolderBase` (title,note,fontSizeTitle,fontSizeContent,isBold, isItalic, isUnderline, date,idNoteFirebase) VALUES (:mTitle,  :mContent, :fontSizeTitle,:fontSizeContent, :mBold, :mIsItalic, :mUnderline, :mDate," +
            "  :mIdNoteFirebase)")
    void insertFromFirebase(String mTitle, String mContent, int fontSizeTitle, int fontSizeContent, int mBold, int mIsItalic, int mUnderline, Long mDate, String mIdNoteFirebase);

    @Query("SELECT * FROM FolderBase WHERE mId = :mId")
    FolderBase getContent(int mId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FolderBase folderBase);

    @Query("UPDATE FolderBase SET title = :title, note=:content, fontFamily=:mFontFamily,fontSizeTitle=:mFontSizeTitle,fontSizeContent=:mFontSizeContent,isBold=:mBold, isItalic=:mIsItalic,isUnderline=:mUnderline WHERE mId =:mId")
    void update(Long mId, String title, String content, String mFontFamily, int mFontSizeTitle, int mFontSizeContent, int mBold, int mIsItalic, int mUnderline);

    @Query("UPDATE FolderBase SET idNoteFirebase = :idNoteFirebase, idKeyFolder =:idKeyFolder WHERE mId =:mId")
    void updateIdKeyFolderAndIdNFirebase(Long mId,String idKeyFolder,String idNoteFirebase);

    @Query("UPDATE FolderBase SET idKeyFolder =:idKeyFolder WHERE date =:date AND idKeyFolder='null' AND mId =:mId")
    void updateIdKeyFolder(Long mId,String idKeyFolder,Long date);

    @Query("UPDATE FolderBase SET idKeyFolder =:idKeyFolder WHERE date =:date AND idKeyFolder='null'")
    void updateByDate(String idKeyFolder,Long date);

    @Query("UPDATE FolderBase SET idKeyFolder =:idKeyFolder WHERE idKeyFolder='null' AND mId =:mId")
    void updateById(Long mId,String idKeyFolder);

    @Query("UPDATE FolderBase SET idNoteFirebase =:idNoteFirebase WHERE mId =:mId")
    void updateIdNoteFirebase(Long mId,String idNoteFirebase);

    @Query("DELETE FROM FolderBase WHERE mId =:mId")
    void deleteById(Long mId);

    @Query("UPDATE FolderBase SET idKeyFolder =:idKeyFolder WHERE parent_id =:mId")
    void updateByParentId(Long mId, String idKeyFolder);

    @Query("DELETE FROM FolderBase")
    void deleteAll();
}
