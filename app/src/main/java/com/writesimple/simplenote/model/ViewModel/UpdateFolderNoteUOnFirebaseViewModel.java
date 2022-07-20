package com.writesimple.simplenote.model.ViewModel;

import android.app.Application;
import android.os.AsyncTask;

import com.writesimple.simplenote.DI.DaggerDataBaseComponent;
import com.writesimple.simplenote.DI.DataBaseComponent;
import com.writesimple.simplenote.DI.DataBaseModule;
import com.writesimple.simplenote.model.DataBase;
import com.writesimple.simplenote.model.Tables.UpdateFolderNoteUploadOnFirebase;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class UpdateFolderNoteUOnFirebaseViewModel extends AndroidViewModel {
    private final LiveData<List<UpdateFolderNoteUploadOnFirebase>> updateFolderNoteUploadOnFirebase;
    private final DataBase dataBase;

    public UpdateFolderNoteUOnFirebaseViewModel(@NonNull Application application) {
        super(application);
        DataBaseComponent dataBaseComponent = DaggerDataBaseComponent.builder().dataBaseModule(new DataBaseModule(application)).build();
        dataBase = dataBaseComponent.provideDataBase();
        //dataBase = DataBase.getDatabase(this.getApplication());
        updateFolderNoteUploadOnFirebase =  dataBase.updateFolderNoteUploadOnFirebaseDao().getAll();
    }

    public LiveData<List<UpdateFolderNoteUploadOnFirebase>> getAllUpdateFUploadOnFB(){
        return updateFolderNoteUploadOnFirebase;
    }
    public void deleteAll() {
        new UpdateFolderNoteUOnFirebaseViewModel.deleleteAll(dataBase).execute();
    }

    private static class deleleteAll extends AsyncTask<Void, Void, Void> {
        private final DataBase db;
        deleleteAll(DataBase dataBase) {
            this.db = dataBase;

        }

        @Override
        protected Void doInBackground(Void... voids) {
            db.updateFolderNoteUploadOnFirebaseDao().deleteAll();
            return null;
        }
    }
}
