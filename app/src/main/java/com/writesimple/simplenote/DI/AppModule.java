package com.writesimple.simplenote.DI;

import android.content.Context;

import com.writesimple.simplenote.activity.ActivityAddNote;
import com.writesimple.simplenote.model.DataBase;
import com.writesimple.simplenote.model.ViewModel.NoteViewModel;

import javax.inject.Singleton;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.room.Room;
import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    /*@Provides
    @Singleton
    DataBase provideDataBase(Context context){
        return Room.databaseBuilder(context,DataBase.class,"NoteBase").fallbackToDestructiveMigration().build();
    }*/

}
