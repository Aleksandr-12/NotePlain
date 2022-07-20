package com.writesimple.simplenote.model.ViewModel;
import android.annotation.SuppressLint;
import android.app.Application;
import android.os.AsyncTask;

import com.google.firebase.database.DatabaseReference;
import com.writesimple.simplenote.DI.DaggerDataBaseComponent;
import com.writesimple.simplenote.DI.DataBaseComponent;
import com.writesimple.simplenote.DI.DataBaseModule;
import com.writesimple.simplenote.model.DataBase;
import com.writesimple.simplenote.FireBase.DataFirebase;
import com.writesimple.simplenote.FireBase.NoteFireBase;
import com.writesimple.simplenote.model.Tables.FolderBase;
import com.writesimple.simplenote.model.Tables.NoteBase;
import com.writesimple.simplenote.model.Tables.dataForFirebaseDateDelete;
import com.writesimple.simplenote.model.Tables.dataForFirebaseDateUpdate;

import java.util.Date;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class FoldViewModel extends AndroidViewModel {

    private static String idKeyFolder;
    private final LiveData<List<FolderBase>> folderBase;
    private final DataBase dataBase;
    private final MutableLiveData<List<FolderBase>> foldMutableLiveData =
            new MutableLiveData<>();

    public FoldViewModel(@NonNull Application application) {
        super(application);
        DataBaseComponent dataBaseComponent = DaggerDataBaseComponent.builder().dataBaseModule(new DataBaseModule(application)).build();
        dataBase = dataBaseComponent.provideDataBase();
        folderBase =  dataBase.FolderDao().getAll();
        initRx();
    }
    @SuppressLint("CheckResult")
    public void initRx() {
        dataBase.FolderDao().getAllRx()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<FolderBase>>() {
                    @Override
                    public void accept(List<FolderBase> folderBases) {
                        foldMutableLiveData.setValue(folderBases);
                    }
                });
    }
    public MutableLiveData<List<FolderBase>> getAllRx() {
        return foldMutableLiveData;
    }
    public Long getIdByIdKeyFolder(String idKeyFolder){
        return  dataBase.FolderDao().getIdByIdKeyFolder(idKeyFolder);
    }
    public String getFolderById(Long id){
        idKeyFolder =dataBase.FolderDao().getFolderById(id);
        return idKeyFolder;
    }
    private static class AsyncGetFolderById extends AsyncTask<Long, Void, Void> {

        private final DataBase db;

        AsyncGetFolderById(DataBase dataBase) {
            this.db = dataBase;
        }

        @Override
        protected Void doInBackground(Long... longs) {
            FoldViewModel.idKeyFolder = db.FolderDao().getFolderById(longs[0]);
            return null;
        }
    }

    public LiveData<List<FolderBase>> getAllFolders(){
        return folderBase;
    }

    public void addChangerDeleteNoteId(dataForFirebaseDateDelete deleteChangeId){
        new asyncDeleteWithFirebase(dataBase).execute(deleteChangeId);
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

    public void syncWithDataFirebase(DatabaseReference reference){
        new AsyncSyncWithDataFirebase(reference, dataBase).execute();
    }

    private static class AsyncSyncWithDataFirebase extends AsyncTask<String, Void, Void> {
        private final DatabaseReference reference;
        private final DataBase dataBase;

        AsyncSyncWithDataFirebase(DatabaseReference reference, DataBase dataBase) {
            this.reference = reference;
            this.dataBase = dataBase;
        }

        @Override
        protected Void doInBackground(String... dataForFirebaseDateUpdates) {
            List<dataForFirebaseDateUpdate> dataDateUpdate = dataBase.dataForFirebaseDateUpdateDao().getAllUpdate();
            List<NoteBase> note = dataBase.noteDao().getAllWithoutlivedata();
            List<dataForFirebaseDateDelete> idKeyForFirebase = dataBase.dataForFirebaseDateDeleteDao().getAllDataDeleteWithouLiveData();
            List<NoteBase> notesWithoutIdKey = dataBase.noteDao().getAllWithoutIdKey();

            if(!idKeyForFirebase.isEmpty()){
                for(dataForFirebaseDateDelete key : idKeyForFirebase){
                    reference.child("note").child(key.getDateDelete()).removeValue();
                }
                dataBase.dataForFirebaseDateDeleteDao().cleanTable();
            }
            if(!notesWithoutIdKey.isEmpty()){
                NoteFireBase noteFireBase;
                for(NoteBase notesWId : notesWithoutIdKey){
                    DataFirebase dataFirebase;
                    dataFirebase = new DataFirebase();
                    noteFireBase = new NoteFireBase(notesWId.getIdNoteFirebase(),notesWId.getDate(),notesWId.getTitle(),notesWId.getContent());
                    dataFirebase.setMyRef(noteFireBase);
                    dataBase.noteDao().updateIdKey(notesWId.getmId(),dataFirebase.getKey());
                }
            }
            if(!dataDateUpdate.isEmpty()){
                NoteFireBase noteFireBase;
                for(dataForFirebaseDateUpdate key : dataDateUpdate){
                    for(NoteBase notes : note){
                        if(notes.getIdNoteFirebase().equals(key.getDateUpdate())){
                            noteFireBase = new NoteFireBase(notes.getIdNoteFirebase(),notes.getDate(),notes.getTitle(),notes.getContent());
                            reference.child("note").child(key.getDateUpdate()).setValue(noteFireBase);
                            dataBase.dataForFirebaseDateUpdateDao().cleanTable();
                        }
                    }
                }
            }

            //dataFirebase.child("note").child(String.valueOf(dataForFirebaseDateUpdates[0])).removeValue();
            //DatabaseReference listOfObjects = dataFirebase.orderByChild("n").endAt(myLimit).getRef();
            return null;
        }
    }

    public void addChangerUpdateNoteId(dataForFirebaseDateUpdate updateChangeId){
        new addAsyncChangerUpdateNoteId(dataBase).execute(updateChangeId);
    }

    private static class addAsyncChangerUpdateNoteId extends AsyncTask<dataForFirebaseDateUpdate, Void, Void> {

        private final DataBase db;

        addAsyncChangerUpdateNoteId(DataBase dataBase) {
            this.db = dataBase;
        }

        @Override
        protected Void doInBackground(final dataForFirebaseDateUpdate... dataForFirebaseDateUpdates) {
            db.dataForFirebaseDateUpdateDao().insert(dataForFirebaseDateUpdates[0]);
            return null;
        }
    }
    public void updateNote(Long id, String title, String content, String mFontFamily, int fontSizeTitle, int fontSizeContent, String bold, String isItalic, String underline, int colorBackground, Date reminder, int idNotification){
        String mId = String.valueOf(id);
        String mFontSizeTitle = String.valueOf(fontSizeTitle);
        String mFontSizeContent = String.valueOf(fontSizeContent);
        String mBold = String.valueOf(bold);
        String mIsItalic = String.valueOf(isItalic);
        String mUnderline = String.valueOf(underline);
        new updateAsyncNote(dataBase).execute(mId,title,content,mFontFamily,mFontSizeTitle,mFontSizeContent,mBold,mIsItalic,mUnderline);
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
            db.noteDao().update(id,Notes[1],Notes[2],Notes[3],mFontSizeTitle,mFontSizeContent,mBold,mIsItalic,mUnderline);
            return null;
        }
    }


    public void deleteByIdNote(Long id){
        new deleteByIdAsyncNote(dataBase).execute(id);
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

    public void updateFolder(Long id,String idKeyFolder, Long date){
        String mId = String.valueOf(id);
        new updateFolderAsync(dataBase,date).execute(mId,idKeyFolder);
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

}

