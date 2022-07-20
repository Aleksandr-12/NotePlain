package com.writesimple.simplenote.Repository;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

import com.writesimple.simplenote.model.DataBase;
import com.writesimple.simplenote.model.Tables.NoteBase;

import org.jetbrains.annotations.NotNull;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class Repository {
    private final DataBase dataBase;
    private final Context context;

    public Repository(Context context) {
        this.context = context;
       // DataBaseComponent dataBaseComponent = DaggerDataBaseComponent.builder().dataBaseModule(new DataBaseModule(application)).build();
       // dataBase = dataBaseComponent.provideDataBase();
        dataBase = DataBase.getDatabase(context);

    }
    public void insert(final NoteBase note){
        Completable.fromAction( () -> dataBase.noteDao().insertRx(note))
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

}
