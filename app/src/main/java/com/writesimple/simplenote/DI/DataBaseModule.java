package com.writesimple.simplenote.DI;

import android.content.Context;

import com.writesimple.simplenote.model.DataBase;

import javax.inject.Singleton;

import androidx.annotation.NonNull;
import androidx.room.Room;
import dagger.Module;
import dagger.Provides;

@Module
public class DataBaseModule {

    private final Context context;
    public DataBaseModule(@NonNull Context context) {
        this.context = context;
    }

    @Provides
    DataBase provideDataBase(){
        return Room.databaseBuilder(context, DataBase.class,"NoteBase").allowMainThreadQueries().fallbackToDestructiveMigration().build();
    }
}
