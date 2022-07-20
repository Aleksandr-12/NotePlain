package com.writesimple.simplenote.model.Dao;


import com.writesimple.simplenote.model.Tables.UpdateFolderNoteUploadOnFirebase;

import java.util.Date;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface UpdateFolderNoteUploadOnFirebaseDao {

    @Query("SELECT * FROM UpdateFolderNoteUploadOnFirebase")
    LiveData<List<UpdateFolderNoteUploadOnFirebase>> getAll();

    @Query("SELECT * FROM UpdateFolderNoteUploadOnFirebase")
    List<UpdateFolderNoteUploadOnFirebase> getAllWithoutlivedata();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UpdateFolderNoteUploadOnFirebase updateFolderNoteUploadOnFirebase);


    @Query("DELETE FROM UpdateFolderNoteUploadOnFirebase WHERE idKeyFolder =:idKeyFolder")
    void deleteByIdkewfolder(String idKeyFolder);

    @Query("DELETE FROM UpdateFolderNoteUploadOnFirebase WHERE idNoteFirebase =:idNoteFirebase")
    void deleteByIdNoteFirebase(String idNoteFirebase);

    @Query("SELECT id FROM UpdateFolderNoteUploadOnFirebase WHERE date =:date")
    Long getIdByDate(Long date);

    @Query("DELETE FROM UpdateFolderNoteUploadOnFirebase WHERE date =:date AND parent_id = :parent_id")
    void deleteByDate(Long date,Long parent_id);

    @Query("DELETE FROM UpdateFolderNoteUploadOnFirebase WHERE date =:date")
    void deleteByOnlyDate(Long date);

    @Query("DELETE FROM UpdateFolderNoteUploadOnFirebase WHERE parent_id =:id AND date =:date AND idKeyFolder = 'null' AND idNoteFirebase = 'null'")
    void deleteByDateAndId(Long id,Long date);

    @Query("DELETE FROM UpdateFolderNoteUploadOnFirebase WHERE parent_id =:id AND date =:date AND idKeyFolder =:idKeyFolder AND idNoteFirebase = 'null'")
    void deleteByIdNoteFirebaseAndId(Long id, String idKeyFolder,Long date);

    @Query("DELETE FROM UpdateFolderNoteUploadOnFirebase WHERE id = :id")
    void deleteById(Long id);

    @Query("DELETE FROM UpdateFolderNoteUploadOnFirebase")
    void deleteAll();
}
