package com.writesimple.simplenote.model.Dao;
import com.writesimple.simplenote.model.Tables.dataForFirebaseDateDelete;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface dataForFirebaseDateDeleteDao {

    @Query("SELECT * FROM dataForFirebaseDateDelete")
    LiveData<List<dataForFirebaseDateDelete>> getAllDataDelete();

    @Query("SELECT * FROM dataForFirebaseDateDelete")
    List<dataForFirebaseDateDelete> getAllDataDeleteWithouLiveData();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(dataForFirebaseDateDelete dataForFirebase);

    @Query("DELETE FROM dataForFirebaseDateDelete")
    public void cleanTable();

    @Query("DELETE FROM dataForFirebaseDateDelete WHERE id =:mId")
    void deleteById(Long mId);


}
