package com.writesimple.simplenote.model.Dao;

import com.writesimple.simplenote.model.Tables.NoteBase;
import com.writesimple.simplenote.model.Tables.User;

import java.util.Date;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.TypeConverters;

@Dao
@TypeConverters(NoteBase.DateStringConverter.class)
public interface UserDao {

    @Query("SELECT * FROM User WHERE isBuy IS NULl")
    List<User> getDate();

    @Query("SELECT isSeeSubs FROM User  WHERE isBuy IS NULl")
    String getIsSeeSubs();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);

    @Query("UPDATE User SET isNotify =:isNotify  WHERE isBuy IS NULl")
    void updateNotify(String isNotify);

    @Query("UPDATE User SET isSeeSubs =:isSeeSubs  WHERE isBuy IS NULl")
    void updateIsSeeSubs(String isSeeSubs);

    @Query("UPDATE User SET date =:date WHERE isBuy IS NULl")
    void updateDateUserForSubscribe(Date date);


    @Query("UPDATE User SET isSeeSubs =:isSeeSubs  WHERE isBuy IS NOT NULl")
    void updateIsSeeInApp(String isSeeSubs);

    @Query("UPDATE User SET isNotify =:isNotify  WHERE isBuy IS NOT NULl")
    void updateNotifyInApp(String isNotify);

    @Query("SELECT isSeeSubs FROM User  WHERE isBuy IS NOT NULl")
    String getIsSeeInApp();

    @Query("SELECT * FROM User WHERE isBuy IS NOT NULL")
    List<User> getIsBuy();

    @Query("DELETE FROM User")
    void delete();
}
