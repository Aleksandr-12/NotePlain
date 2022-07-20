package com.writesimple.simplenote.model.ViewModel;
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.writesimple.simplenote.DI.DaggerDataBaseComponent;
import com.writesimple.simplenote.DI.DataBaseComponent;
import com.writesimple.simplenote.DI.DataBaseModule;
import com.writesimple.simplenote.model.DataBase;
import com.writesimple.simplenote.FireBase.DataFirebase;
import com.writesimple.simplenote.FireBase.NoteFireBase;
import com.writesimple.simplenote.model.Tables.NoteBase;
import com.writesimple.simplenote.model.Tables.dataForFirebaseDateDelete;
import com.writesimple.simplenote.model.Tables.dataForFirebaseDateUpdate;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class NoteViewModel extends AndroidViewModel {

   // private final LiveData<List<NoteBase>> noteBase;
    private List<NoteBase> note;
    public final DataBase dataBase;
    Disposable disposable;
    @SuppressLint("StaticFieldLeak")
    private final Context context;

    private final MutableLiveData<List<NoteBase>> noteMutableLiveData =
            new MutableLiveData<>();
    @SuppressLint("CheckResult")
    @Inject
    public NoteViewModel(@NonNull Application application) {
        super(application);
        this.context = application.getApplicationContext();
        DataBaseComponent dataBaseComponent = DaggerDataBaseComponent.builder().dataBaseModule(new DataBaseModule(application)).build();
        dataBase = dataBaseComponent.provideDataBase();
         //noteBase = dataBase.noteDao().getAll();
        //dataBase = DataBase.getDatabase(this.getApplication());
        //noteBase =  dataBase.noteDao().getAll();
        initRx();
    }
    public void insert(final NoteBase note){
        Completable.fromAction( () -> dataBase.noteDao().insert(note))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {
                    }
                    @Override
                    public void onComplete() {
                        Toast.makeText(context,"Data inserted", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void update(Long id, String title, String content, String mFontFamily, int fontSizeTitle, int fontSizeContent, int bold, int isItalic, int underline){
        Completable.fromAction(() -> dataBase.noteDao().update(id,title,content,mFontFamily,fontSizeTitle,fontSizeContent, bold,  isItalic, underline))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {
                    }
                    @Override
                    public void onComplete() {
                        Toast.makeText(context,"Data updated", Toast.LENGTH_SHORT).show();
                    }
                    @SuppressLint("CheckResult")
                    @Override
                    public void onError(@NotNull Throwable e) {
                        Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @SuppressLint("CheckResult")
    public void initRx() {
        dataBase.noteDao().getAllRx()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<NoteBase>>() {
                    @Override
                    public void accept(List<NoteBase> noteBases) {
                        noteMutableLiveData.setValue(noteBases);
                    }
                });
    }
 /*  public LiveData<List<NoteBase>> getAllNotes(){
        return noteBase;
    }
*/
    public void addChangerDeleteNoteId(dataForFirebaseDateDelete deleteChangeId){
        new asyncDeleteWithFirebase(dataBase).execute(deleteChangeId);
    }
    public MutableLiveData<List<NoteBase>> getAllRx() {
        return noteMutableLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
//        disposable.dispose();
    }
    public List<NoteBase> getAll(){
        return dataBase.noteDao().get();
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

    public void syncWithDataFirebase(){
        new AsyncSyncWithDataFirebase(dataBase).execute();
    }

    private static class AsyncSyncWithDataFirebase extends AsyncTask<String, Void, Void> {
        private final DataBase dataBase;
        private DataFirebase dataFirebase;

        AsyncSyncWithDataFirebase(DataBase dataBase) {
            this.dataFirebase = new DataFirebase();
            this.dataBase = dataBase;
        }

        @Override
        protected Void doInBackground(String... dataForFirebaseDateUpdates) {
            List<dataForFirebaseDateUpdate> dataDateUpdate = dataBase.dataForFirebaseDateUpdateDao().getAllUpdate();
            List<NoteBase> notes = dataBase.noteDao().getAllWithoutlivedata();
            List<dataForFirebaseDateDelete> idKeyForFirebase = dataBase.dataForFirebaseDateDeleteDao().getAllDataDeleteWithouLiveData();
            List<NoteBase> notesWithoutIdKey = dataBase.noteDao().getAllWithoutIdKey();
            if(idKeyForFirebase!=null){
                for(dataForFirebaseDateDelete key : idKeyForFirebase){
                    dataFirebase.getMyRef().child(key.getDateDelete()).removeValue();
                }
                dataBase.dataForFirebaseDateDeleteDao().cleanTable();
            }
            if(notesWithoutIdKey!=null){
                NoteFireBase noteFireBase;
                for(NoteBase notesWId : notesWithoutIdKey){
                    dataFirebase = new DataFirebase();
                    noteFireBase = new NoteFireBase(dataFirebase.getKey(),notesWId.getDate(),notesWId.getTitle(),notesWId.getContent());
                    dataFirebase.setMyRef(noteFireBase);
                    dataBase.noteDao().updateIdKey(notesWId.getmId(),dataFirebase.getKey());
                }
            }
            if(dataDateUpdate!=null){
                NoteFireBase noteFireBase;
                for(dataForFirebaseDateUpdate key : dataDateUpdate){
                    for(NoteBase note : notes){
                        if(note.getIdNoteFirebase().equals(key.getDateUpdate())){
                            noteFireBase = new NoteFireBase(note.getIdNoteFirebase(),note.getDate(),note.getTitle(),note.getContent());
                            dataFirebase.getMyRef().child(note.getIdNoteFirebase()).setValue(noteFireBase);
                        }
                    }
                }
                dataBase.dataForFirebaseDateUpdateDao().cleanTable();
            }
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
    public void updateNote(Long id, String title, String content, String mFontFamily, int fontSizeTitle, int fontSizeContent, int bold, int isItalic, int underline){
        String mId = String.valueOf(id);
        String mFontSizeTitle = String.valueOf(fontSizeTitle);
        String mFontSizeContent = String.valueOf(fontSizeContent);
        String mBold = String.valueOf(bold);
        String mIsItalic = String.valueOf(isItalic);
        String mUnderline = String.valueOf(underline);
        new updateAsyncNote(dataBase).execute(mId,title,content,mFontFamily,mFontSizeTitle,mFontSizeContent,mBold,mIsItalic,mUnderline);
    }

    public void addNote(NoteBase noteBase){
        new addAsyncNote(dataBase).execute(noteBase);
    }
    private static class addAsyncNote extends AsyncTask<NoteBase, Void, Void> {

        private final DataBase db;

        addAsyncNote(DataBase dataBase) {
           this.db = dataBase;
        }

        @Override
        protected Void doInBackground(final NoteBase... NoteBases) {
            db.noteDao().insert(NoteBases[0]);
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
            db.noteDao().deleteById(id[0]);
            return null;
        }
    }
     /*  Observable.fromCallable(() -> noteViewModel.dataBase.noteDao().insertRx(new NoteBase(mTitle, mContent,mFontFamily,mFontSizeTitle,mFontSizeContent,mBold,mIsItalic,mUnderline, date.getTime(),idNoteFirebase)))
                                    .subscribeOn(Schedulers.io())
                                    .subscribe(
                                    );*/
                           /* Completable.fromRunnable(new Runnable(){

                                @Override
                                public void run() {
                                    noteViewModel.dataBase.noteDao().insert(new NoteBase(mTitle, mContent,mFontFamily,mFontSizeTitle,mFontSizeContent,mBold,mIsItalic,mUnderline, date.getTime(),idNoteFirebase));
                                    }
                                })
                                    .subscribeOn(Schedulers.io())
                                    .subscribe();*/
}
