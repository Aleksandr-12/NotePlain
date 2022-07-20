package com.writesimple.simplenote.model.Dao;
import com.writesimple.simplenote.model.Tables.dataForFirebaseDateUpdate;
import java.util.List;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface dataForFirebaseDateUpdateDao {

    @Query("SELECT * FROM dataForFirebaseDateUpdate")
    List<dataForFirebaseDateUpdate> getAllUpdate();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(dataForFirebaseDateUpdate idNote);

    @Query("DELETE FROM dataForFirebaseDateUpdate")
    void cleanTable();
}
