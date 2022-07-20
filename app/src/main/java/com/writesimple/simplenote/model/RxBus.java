package com.writesimple.simplenote.model;

import com.writesimple.simplenote.model.Tables.FolderBase;
import com.writesimple.simplenote.model.Tables.NoteBase;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class RxBus {
    private static RxBus mInstance;
    public static RxBus getInstance() {
        if (mInstance == null) {
            mInstance = new RxBus();
        }
        return mInstance;
    }
    private RxBus() {
    }
    private final PublishSubject<NoteBase> publisherNote = PublishSubject.create();
    private final PublishSubject<FolderBase> publisherFold = PublishSubject.create();
    public void publishNote(NoteBase event) {
        publisherNote.onNext(event);
    }
    public void publishFoldNote(FolderBase event) {
        publisherFold.onNext(event);
    }
    public Observable<NoteBase> listenNote() {
        return publisherNote;
    }
    public Observable<FolderBase> listenFoldNote() {
        return publisherFold;
    }
}
