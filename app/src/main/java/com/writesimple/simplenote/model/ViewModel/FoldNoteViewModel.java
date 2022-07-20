package com.writesimple.simplenote.model.ViewModel;
import android.annotation.SuppressLint;
import android.app.Application;
import android.os.AsyncTask;

import com.writesimple.simplenote.DI.DaggerDataBaseComponent;
import com.writesimple.simplenote.DI.DataBaseComponent;
import com.writesimple.simplenote.DI.DataBaseModule;
import com.writesimple.simplenote.model.DataBase;
import com.writesimple.simplenote.model.Tables.FolderBase;
import com.writesimple.simplenote.model.Tables.UpdateFolderNoteUploadOnFirebase;
import com.writesimple.simplenote.model.Tables.dataForFirebaseDateDelete;

import java.util.List;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class FoldNoteViewModel extends AndroidViewModel {

    private final LiveData<List<FolderBase>> folderBase;

    private final DataBase dataBase;
    private final MutableLiveData<List<FolderBase>> foldMutableLiveData =
            new MutableLiveData<>();
    private final MutableLiveData<List<FolderBase>> allNotesFoldMutableLiveData =
            new MutableLiveData<>();
    public FoldNoteViewModel(@NonNull Application application) {
        super(application);
        DataBaseComponent dataBaseComponent = DaggerDataBaseComponent.builder().dataBaseModule(new DataBaseModule(application)).build();
        dataBase = dataBaseComponent.provideDataBase();
        folderBase =  dataBase.FolderDao().getAll();
        initRx();
    }
    @SuppressLint("CheckResult")
    public void initRx() {
        dataBase.FolderDao().getAllIsNotNullParentIdRx()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<FolderBase>>() {
                    @Override
                    public void accept(List<FolderBase> folderBases) {
                        foldMutableLiveData.setValue(folderBases);
                    }
                });
    }

    @SuppressLint("CheckResult")
    public MutableLiveData<List<FolderBase>> getAllNotesOfFoldRx(long id){
        dataBase.FolderDao().getAllNotesOfFoldRx(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<FolderBase>>() {
                    @Override
                    public void accept(List<FolderBase> folderBases) {
                        allNotesFoldMutableLiveData.setValue(folderBases);
                    }
                });
        return allNotesFoldMutableLiveData;
    }
    public LiveData<List<FolderBase>> getAllNotesOfFolder(Long id){
        return dataBase.FolderDao().getAllNotesOfFolder(id);
    }

    public MutableLiveData<List<FolderBase>> getAllRx() {
        return foldMutableLiveData;
    }
    public List<FolderBase> getAllIsNotNullParentId(){
        return dataBase.FolderDao().getAllIsNotNullParentId();
    }
    public Long getCountNoteOfFoldById(Long id){
        return dataBase.FolderDao().getCountNoteOfFoldById(id);
    }

    public LiveData<List<FolderBase>> getAllIsNotNullParentIdLiveData(){
        return dataBase.FolderDao().getAllIsNotNullParentIdLiveData();
    }

    public String getFoldName(Long parent_id){
        return dataBase.FolderDao().getFoldName(parent_id);
    }

    public void updateFolder(Long id,String idKeyFolder, Long date){
        String mId = String.valueOf(id);
        new FoldNoteViewModel.updateFolderAsync(dataBase,date).execute(mId,idKeyFolder);
    }

    private static class updateFolderAsync extends AsyncTask<String, Void, Void> {

        private final DataBase db;
        private final Long date;

        updateFolderAsync(DataBase dataBase,Long date) {
            this.db = dataBase;
            this.date = date;
        }

        @Override
        protected Void doInBackground(final String... Notes) {
            Long id = Long.parseLong(Notes[0]);
            db.FolderDao().updateIdKeyFolder(id,Notes[1],date);
            return null;
        }
    }

    public FolderBase getFolderIdKeyFolder(String idKeyFolder){
        return dataBase.FolderDao().getFolderIdKeyFolder(idKeyFolder);
    }
    public List<FolderBase> getAllAbsolute(){
        return dataBase.FolderDao().getAllAbsolute();
    }

    public Long getIdByDate(Long date){
        return dataBase.updateFolderNoteUploadOnFirebaseDao().getIdByDate(date);
    }


    public List<FolderBase> getAllNotesOfFolderWithoutLiveData(Long mId) {
        return dataBase.FolderDao().getAllNotesOfFolderWithoutLiveData(mId);
    }

    private static class asyncDeleteWithFirebase extends AsyncTask<dataForFirebaseDateDelete, Void, Void> {

        private final DataBase db;

        asyncDeleteWithFirebase(DataBase dataBase) {
            this.db = dataBase;
        }

        @Override
        protected Void doInBackground(dataForFirebaseDateDelete... dataForFirebaseDateDeletes) {
            db.dataForFirebaseDateDeleteDao().insert(dataForFirebaseDateDeletes[0]);
            return null;
        }
    }

    public void updateNote(Long id, String title, String content, String mFontFamily, int fontSizeTitle, int fontSizeContent, int bold, int isItalic, int underline){
        String mId = String.valueOf(id);
        String mFontSizeTitle = String.valueOf(fontSizeTitle);
        String mFontSizeContent = String.valueOf(fontSizeContent);
        String mBold = String.valueOf(bold);
        String mIsItalic = String.valueOf(isItalic);
        String mUnderline = String.valueOf(underline);
        new updateAsyncNote(dataBase).execute(mId,title,content,mFontFamily,mFontSizeTitle,mFontSizeContent,mBold,mIsItalic,mUnderline);
    }
    public void deleteByIdNote(Long id){
        new deleteByIdAsyncNote(dataBase).execute(id);
    }

    public void addFolder(FolderBase folderBase){
        new addAsyncFolder(dataBase).execute(folderBase);
    }
    private static class addAsyncFolder extends AsyncTask<FolderBase, Void, Void> {

        private final DataBase db;

        addAsyncFolder(DataBase dataBase) {
            this.db = dataBase;
        }

        @Override
        protected Void doInBackground(final FolderBase... FolderBases) {
            db.FolderDao().insert(FolderBases[0]);
            return null;
        }
    }

    public void addUpdateFolderNoteUploadOnFirebase(UpdateFolderNoteUploadOnFirebase updateFolderNoteUploadOnFirebase){
        new addAsyncUpdateFolderNoteUploadOnFirebase(dataBase).execute(updateFolderNoteUploadOnFirebase);
    }
    private static class addAsyncUpdateFolderNoteUploadOnFirebase extends AsyncTask<UpdateFolderNoteUploadOnFirebase, Void, Void> {

        private final DataBase db;

        addAsyncUpdateFolderNoteUploadOnFirebase(DataBase dataBase) {
            this.db = dataBase;
        }

        @Override
        protected Void doInBackground(final UpdateFolderNoteUploadOnFirebase... UpdateFolderNoteUploadOnFirebases) {
            db.updateFolderNoteUploadOnFirebaseDao().insert(UpdateFolderNoteUploadOnFirebases[0]);
            return null;
        }
    }

    private static class updateAsyncNote extends AsyncTask<String, Void, Void> {

        private final DataBase db;

        updateAsyncNote(DataBase dataBase) {
            this.db = dataBase;
        }

        @Override
        protected Void doInBackground(final String... Notes) {
            Long id = Long.parseLong(Notes[0]);
            int mFontSizeTitle = Integer.parseInt(Notes[4]);
            int mFontSizeContent= Integer.parseInt(Notes[5]);
            int mBold = Integer.parseInt(Notes[6]);
            int mIsItalic = Integer.parseInt(Notes[7]);
            int mUnderline = Integer.parseInt(Notes[8]);
            db.FolderDao().update(id,Notes[1],Notes[2],Notes[3],mFontSizeTitle,mFontSizeContent,mBold,mIsItalic,mUnderline);
            return null;
        }
    }

    private static class deleteByIdAsyncNote extends AsyncTask<Long, Void, Void> {

        private final DataBase db;

        deleteByIdAsyncNote(DataBase dataBase) {
            this.db = dataBase;
        }

        @Override
        protected Void doInBackground(Long... id) {

            db.FolderDao().deleteById(id[0]);
            return null;
        }
    }

    public void deleleteFolderNoteUploadOnFirebase(Long id, Long date) {
        new deleteUploadOnFirebase(dataBase,date).execute(id);
    }

    private static class deleteUploadOnFirebase extends AsyncTask<Long, Void, Void> {
        private final DataBase db;
        private final Long date;
        deleteUploadOnFirebase(DataBase dataBase,Long date) {
            this.db = dataBase;
            this.date = date;
        }
        @Override
        protected Void doInBackground(Long... id) {
            db.updateFolderNoteUploadOnFirebaseDao().deleteByDateAndId(id[0],date);
            return null;
        }
    }

    public void deleleteByIdNoteFirebase(Long id, String idNoteFirebase,Long date) {
        new deleleteByIdNoteFirebase(dataBase,idNoteFirebase,date).execute(id);
    }

    private static class deleleteByIdNoteFirebase extends AsyncTask<Long, Void, Void> {
        private final String idNoteFirebase;
        private final Long date;
        private final DataBase db;
        deleleteByIdNoteFirebase(DataBase dataBase,String idNoteFirebase,Long date) {
            this.date = date;
            this.db = dataBase;
            this.idNoteFirebase = idNoteFirebase;
        }
        @Override
        protected Void doInBackground(Long... id) {
            db.updateFolderNoteUploadOnFirebaseDao().deleteByIdNoteFirebaseAndId(id[0],idNoteFirebase,date);
            return null;
        }
    }
}
