package com.writesimple.simplenote.FireBase;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.writesimple.simplenote.model.DataBase;
import com.writesimple.simplenote.model.Tables.FolderBase;
import com.writesimple.simplenote.model.Tables.NoteBase;
import com.writesimple.simplenote.model.Tables.UpdateFolderNoteUploadOnFirebase;
import com.writesimple.simplenote.model.Tables.User;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import androidx.annotation.NonNull;

import static android.content.ContentValues.TAG;

public class DataFirebase {

    private final FirebaseDatabase firebaseDatabase;
    private DataBase database;
    private DatabaseReference myRef;
    private DatabaseReference myRefSubs;
    private DatabaseReference myRefInApp;
    private DatabaseReference myRefUser;
    public DatabaseReference myRefFolder;
    public String id;
    public String idFolder;

    public DataFirebase() {
        FirebaseAuth firebaseAuth;
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        myRef = firebaseDatabase.getReference("note");
        myRefUser = firebaseDatabase.getReference("user");
        if(firebaseAuth.getCurrentUser()!=null){
            myRefSubs = myRefUser.child(firebaseAuth.getCurrentUser().getUid()).child("Subs");
            myRefInApp = myRefUser.child(firebaseAuth.getCurrentUser().getUid()).child("InApp");
            myRef.keepSynced(true);
            myRefInApp.keepSynced(true);
        }
        myRef.keepSynced(true);
        if(firebaseAuth.getCurrentUser()!=null){
            myRef = myRef.child(firebaseAuth.getCurrentUser().getUid());
        }
        myRefFolder = firebaseDatabase.getReference("folder");
        myRefFolder.keepSynced(true);
        id = firebaseDatabase.getReference().push().getKey();
        idFolder = myRefFolder.child("folder").push().getKey();
        if(firebaseAuth.getCurrentUser()!=null){
            myRefFolder = myRefFolder.child(firebaseAuth.getCurrentUser().getUid());
        }
    }

    public DatabaseReference getMyRefUserSubs() {
        return myRefSubs;
    }
    public DatabaseReference getMyRefUserInApp() {
        return myRefInApp;
    }

    public void setMyRerencesUserInApp(User user){
        myRefInApp.setValue(user);
    }
    public void setMyRerencesUserSubs(User user){
        myRefSubs.setValue(user);
    }
    public DatabaseReference getMyRef() {
        return myRef;
    }

    public DatabaseReference getMyRefFolder() {
        return myRefFolder;
    }

    public String getKey() {
        return id;
    }

    public String getKeyIdFolder() {
        return idFolder;
    }

    public void setMyRef(NoteFireBase noteFireBase) {
        myRef.child(id).setValue(noteFireBase);
    }


    public void setRefNoteOfFolder(NoteFireBase noteFireBase, String idKeyFolder) {
        myRefFolder.child(idKeyFolder).child(idFolder).setValue(noteFireBase);
    }

    public void setRefForFolder(FoldFireBase foldFireBase) {
        myRefFolder.child(idFolder).setValue(foldFireBase);
    }

    public void getDataNote(Context context) {
        database = DataBase.getDatabase(context);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    addNote(database, dataSnapshot);
                }
            }
            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public void addNote(DataBase dataBase, DataSnapshot dataSnapshot) {
        new addAsyncNote(dataBase, dataSnapshot).execute(dataSnapshot);
    }

    public void isIdKeyFolderInFirebase(String idKeyFolder, FolderBase folderItem) {
        myRefFolder.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                dataSnapshot.getChildren();
               /* if (dataSnapshot.getValue() != null) {
                   if (!dataSnapshot.hasChild(idKeyFolder)) {
                        FolderFireBase folderFireBase = new FolderFireBase(idKeyFolder, folderItem.getDate(), folderItem.getTitle());
                        myRefFolder.child(idKeyFolder).setValue(folderFireBase);
                    }else{
                    }
                }*/
            }
            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public void preWriteDataInFirebase(Context context) {
        new preWriteDataInFirebase(context).execute();
    }
    private static class preWriteDataInFirebase extends AsyncTask<List<FolderBase>, Void, Void> {

        private final DataBase dataBase;
        DataFirebase dataFirebase;

        preWriteDataInFirebase(Context context) {
            this.dataBase = DataBase.getDatabase(context);
            this.dataFirebase = new DataFirebase();
        }

        @Override
        protected Void doInBackground(List<FolderBase>... lists) {
            List<FolderBase> notes;
            List<FolderBase> allFolder;
            notes = dataBase.FolderDao().getAllIsNotNullParentId();
            allFolder = dataBase.FolderDao().getAllFolders();
            dataFirebase.myRefFolder.removeValue();
            for (FolderBase allfold : allFolder) {
                if (allfold.getIdKeyFolder() != null) {
                    FoldFireBase foldFireBase = new FoldFireBase(allfold.getIdKeyFolder(), allfold.getDate(), allfold.getTitle());
                    dataFirebase.myRefFolder.child(allfold.getIdKeyFolder()).setValue(foldFireBase);
                    //dataBase.FolderDao().updateIdNoteFirebase(allfold.getmId(), idFolder);

                }else if ("null".equals(allfold.getIdKeyFolder())) {
                    DataFirebase dataFBase = new DataFirebase();
                    String newIdFolder = dataFBase.getKeyIdFolder();
                    FoldFireBase foldFireBase = new FoldFireBase(newIdFolder, allfold.getDate(),allfold.getTitle());
                    dataFirebase.myRefFolder.child(newIdFolder).setValue(foldFireBase);
                    dataBase.FolderDao().updateById(allfold.getmId(), newIdFolder);
                    List<FolderBase> allNoteParentId =  dataBase.FolderDao().getAllByParentId(allfold.getmId());
                    for(FolderBase allNote: allNoteParentId){
                        dataBase.FolderDao().updateByParentId(allNote.getmId(), newIdFolder);
                    }
                }
            }
            for(FolderBase note: notes){
                DataFirebase dataFBase = new DataFirebase();
                String idKeyNote = dataFBase.getKey();
                String idFolder = dataBase.FolderDao().getFolderById(note.getParent_id());
                if("null".equals(note.getIdNoteFirebase())){
                    NoteFireBase noteFireBase = new NoteFireBase(idKeyNote, note.getDate(), note.getTitle(), note.getNote());
                    dataBase.FolderDao().updateIdNoteFirebase(note.getmId(), idKeyNote);
                    dataFirebase.myRefFolder.child(idFolder).child(idKeyNote).setValue(noteFireBase);
                }else{
                    NoteFireBase noteFireBase = new NoteFireBase(note.getIdNoteFirebase(), note.getDate(), note.getTitle(), note.getNote());
                    dataFirebase.myRefFolder.child(idFolder).child(note.getIdNoteFirebase()).setValue(noteFireBase);
                }

            }
            return null;
        }
    }


    private static class addAsyncNote extends AsyncTask<DataSnapshot, Void, Void> {

        private final DataBase dataBase;
        private final DataSnapshot dataSnapshot;

        addAsyncNote(DataBase dataBase, DataSnapshot dataSnapshot) {
            this.dataBase = dataBase;
            this.dataSnapshot = dataSnapshot;
        }

        @Override
        protected Void doInBackground(DataSnapshot... dataSnapshots) {
            List<NoteBase> noteBase;
            noteBase = dataBase.noteDao().getAllWithoutlivedata();
            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                NoteFireBase value = childSnapshot.getValue(NoteFireBase.class);
                int trueIterator = 0;
                for (NoteBase note : noteBase) {
                    if (note.getIdNoteFirebase().equals(childSnapshot.getKey())) {
                        trueIterator++;
                    }
                }
                if (trueIterator == 0) {
                    dataBase.noteDao().insertFromFirebase(value.getTitle(), value.getNote(), 22, 20, 0, 0,
                            0, value.getDate(), childSnapshot.getKey());
                }
            } //addFolderNote(database, dataSnapshot);
            return null;
        }
    }

    public void getAndSetDataOfFolder(Context context) {
        database = DataBase.getDatabase(context);
        myRefFolder.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    addFolderNote(database, task);
                }
            }
        });
    }

    public void addFolderNote(DataBase dataBase, @NotNull Task<DataSnapshot> dataSnapshot) {
        new addAsyncFolder(dataBase, dataSnapshot).execute();
    }


    private static class addAsyncFolder extends AsyncTask<DataSnapshot, Void, Void> {

        private final DataBase dataBase;
        private final @NotNull Task<DataSnapshot> dataSnapshot;

        addAsyncFolder(DataBase dataBase, @NotNull Task<DataSnapshot> dataSnapshot) {
            this.dataBase = dataBase;
            this.dataSnapshot = dataSnapshot;
        }

        @Override
        protected Void doInBackground(DataSnapshot... dataSnapshots) {
            List<FolderBase> folderBase;
            folderBase = dataBase.FolderDao().getAllFolders();
            for (DataSnapshot childSnapshot : dataSnapshot.getResult().getChildren()) {
                FoldFireBase value = childSnapshot.getValue(FoldFireBase.class);
                int trueFolder = 0;
                for (FolderBase folder : folderBase) {
                    if (folder.getIdKeyFolder() != null) {
                        if (folder.getIdKeyFolder().equals(childSnapshot.getKey())) {
                            trueFolder++;
                        }
                    }
                }
                if (trueFolder == 0) {
                    dataBase.FolderDao().insert(new FolderBase(value.getTitle(), value.getDate(), childSnapshot.getKey()));
                   // Long id = dataBase.FolderDao().getIdByIdKeyFolder(childSnapshot.getKey());
                    //dataBase.FolderDao().updateByIdKeyFolder(id,childSnapshot.getKey());
                }
                    if (childSnapshot.hasChildren()) {
                        for (DataSnapshot ch : childSnapshot.getChildren()) {
                          /*if (!("date".equals(ch.getKey()))) {
                                if (!("idKeyFolder".equals(ch.getKey()))) {
                                    if (!("title".equals(ch.getKey()))) {*/
                         if (!("a".equals(ch.getKey()))) {
                                if (!("b".equals(ch.getKey()))) {
                                if (!("c".equals(ch.getKey()))) {
                                    NoteFireBase vl = ch.getValue(NoteFireBase.class);
                                        folderBase = dataBase.FolderDao().getAllWithoutlivedata();
                                        int trueNote = 0;
                                        for (FolderBase fold : folderBase) {
                                            if (ch.getKey().equals(fold.getIdNoteFirebase())) {
                                                trueNote++;
                                            }
                                        }
                                        if(ch.getKey().length()>0){
                                            if (trueNote == 0) {
                                                Long id = dataBase.FolderDao().getIdByIdKeyFolder(childSnapshot.getKey());
                                               //dataBase.FolderDao().insert(new FolderBase(vl.getTitle(), vl.getNote(), "open_sans", 22, 22, 0, 0, 0, vl.getDate(), ch.getKey(), id, childSnapshot.getKey()));
                                               dataBase.FolderDao().insert(new FolderBase((String) ch.child("a").getValue(), (String) ch.child("b").getValue(), "open_sans", 22, 22, 0, 0, 0, (Long) ch.child("c").getValue(), ch.getKey(), id, childSnapshot.getKey()));
                                              //  dataBase.FolderDao().updateIdByNoteFirebase(id,ch.getKey());
                                            }
                                        }
                                    }
                                }
                            }
                       }
                    }
            }
            return null;
        }
    }
    public void addDataInFirebase(Context context,DataFirebase dataFirebase) {
        DataBase database = DataBase.getDatabase(context);
        List<FolderBase> folderBase = (List<FolderBase>) database.FolderDao().getAllWithoutlivedata();
        new addAsyncFoldeOfNote(database, dataFirebase).execute(folderBase);
    }

    private static class addAsyncFoldeOfNote extends AsyncTask<List<FolderBase>, Void, Void> {

        private final DataBase dataBase;
        DataFirebase dataFirebase;

        addAsyncFoldeOfNote(DataBase dataBase, DataFirebase dataFirebase) {
            this.dataBase = dataBase;
            this.dataFirebase = dataFirebase;
        }

        @Override
        protected Void doInBackground(List<FolderBase>... lists) {
            List<UpdateFolderNoteUploadOnFirebase> updateFolderNoteUploadOnFirebase;
            updateFolderNoteUploadOnFirebase = dataBase.updateFolderNoteUploadOnFirebaseDao().getAllWithoutlivedata();
            List<FolderBase> folder;
            folder = dataBase.FolderDao().getAllWithoutlivedata();
            for (FolderBase fold : folder) {
                for (UpdateFolderNoteUploadOnFirebase updateAddDelete : updateFolderNoteUploadOnFirebase) {
                    if (updateAddDelete.getUpdateFolderNote() == 1) {
                        if (((updateAddDelete.getIdKeyFolder()).equals(fold.getIdKeyFolder())) && ((updateAddDelete.getIdNoteFirebase().equals(fold.getIdNoteFirebase())))) {
                            if (fold.getIdNoteFirebase() != null && fold.getIdKeyFolder() != null) {
                                NoteFireBase noteFireBase = new NoteFireBase(fold.getIdNoteFirebase(), fold.getDate(), fold.getTitle(), fold.getNote());
                                dataFirebase.myRefFolder.child(fold.getIdKeyFolder()).child(fold.getIdNoteFirebase()).setValue(noteFireBase);
                                dataBase.updateFolderNoteUploadOnFirebaseDao().deleteByIdNoteFirebase(updateAddDelete.getIdNoteFirebase());
                            }
                        }
                    }
                    if (updateAddDelete.getDeleteFolderNote() == 1) {
                        if ((updateAddDelete.getIdKeyFolder()).equals(fold.getIdKeyFolder())) {
                            if (updateAddDelete.getIdNoteFirebase() != null) {
                                dataFirebase.myRefFolder.child(fold.getIdKeyFolder()).child(updateAddDelete.getIdNoteFirebase()).removeValue();
                                dataBase.updateFolderNoteUploadOnFirebaseDao().deleteByIdNoteFirebase(updateAddDelete.getIdNoteFirebase());
                            }
                        }
                        if(updateAddDelete.getIdNoteFirebase() == null || updateAddDelete.getIdNoteFirebase().equals("null")){
                            dataFirebase.myRefFolder.child(updateAddDelete.getIdKeyFolder()).removeValue();
                            dataBase.updateFolderNoteUploadOnFirebaseDao().deleteByIdkewfolder(updateAddDelete.getIdKeyFolder());
                        }
                    }
                    if (updateAddDelete.getAddFolderNote() == 1) {
                        if ((updateAddDelete.getIdKeyFolder()).equals(fold.getIdKeyFolder())) {
                            if (!("null".equals(updateAddDelete.getIdKeyFolder()))) {
                                if ("null".equals(updateAddDelete.getIdNoteFirebase())) {
                                    if (fold.getParent_id() != null && updateAddDelete.getParent_id() != null) {
                                        if (fold.getParent_id().equals(updateAddDelete.getParent_id()) && fold.getDate().equals(updateAddDelete.getDate())) {
                                            DataFirebase dataFBase = new DataFirebase();
                                            String idFolder = dataFBase.getKeyIdFolder();
                                            NoteFireBase noteFireBase = new NoteFireBase(fold.getIdNoteFirebase(), fold.getDate(), fold.getTitle(), fold.getNote());
                                            dataFirebase.myRefFolder.child(fold.getIdKeyFolder()).child(idFolder).setValue(noteFireBase);
                                            dataBase.FolderDao().updateIdNoteFirebase(fold.getmId(), idFolder);
                                            dataBase.updateFolderNoteUploadOnFirebaseDao().deleteByDate(updateAddDelete.getDate(), updateAddDelete.getParent_id());
                                        }
                                    }
                                }
                            }
                        }
                        if ("null".equals(updateAddDelete.getIdKeyFolder()) && "null".equals(updateAddDelete.getIdNoteFirebase())) {

                            if (fold.getDate().equals(updateAddDelete.getDate()) && updateAddDelete.getParent_id() ==null) {
                                DataFirebase dataFBase = new DataFirebase();
                                String idFolder = dataFBase.getKeyIdFolder();
                                FoldFireBase foldFireBase = new FoldFireBase(idFolder, updateAddDelete.getDate(), fold.getTitle());
                                dataFirebase.myRefFolder.child(idFolder).setValue(foldFireBase);
                                List<FolderBase> noteFold = dataBase.FolderDao().getAllByParentId(fold.getmId());
                                if(noteFold!=null){
                                    for(FolderBase note : noteFold){
                                        dataBase.FolderDao().updateByParentId(note.getParent_id(),idFolder);
                                    }
                                }
                                dataBase.FolderDao().updateByDate(idFolder,updateAddDelete.getDate());
                                dataBase.updateFolderNoteUploadOnFirebaseDao().deleteByOnlyDate(updateAddDelete.getDate());
                            }
                            if (fold.getParent_id() != null && updateAddDelete.getParent_id() != null) {
                                if ((fold.getParent_id()).equals(updateAddDelete.getParent_id()) && fold.getDate().equals(updateAddDelete.getDate())) {
                                    DataFirebase dataFBase = new DataFirebase();
                                    String IdFolder = dataBase.FolderDao().getFolderById(fold.getParent_id());
                                   /* if (parent_id == null || parent_id != updateAddDelete.getParent_id()) {
                                        parent_id = fold.getParent_id();
                                        String mTitle = getTitleById(folder, updateAddDelete.getParent_id());
                                        Date mDate = getDateById(folder, updateAddDelete.getParent_id());
                                        FolderFireBase folderFireBase = new FolderFireBase(IdFolder, mDate, mTitle);
                                        dataFirebase.myRefFolder.child(IdFolder).setValue(folderFireBase);
                                        List<FolderBase> noteFold = dataBase.FolderDao().getAllByParentId(updateAddDelete.getParent_id());
                                        if(noteFold!=null){
                                            for(FolderBase note : noteFold){
                                                dataBase.FolderDao().updateByParentId(note.getParent_id(),IdFolder);
                                            }
                                        }
                                        dataBase.FolderDao().updateIdKeyFolder(fold.getParent_id(), IdFolder,mDate);
                                        dataBase.updateFolderNoteUploadOnFirebaseDao().deleteByDate(updateAddDelete.getDate(),updateAddDelete.getParent_id());
                                    }*/

                                    String newKey = dataFBase.getKey();
                                    NoteFireBase noteFireBase = new NoteFireBase(newKey, fold.getDate(), fold.getTitle(), fold.getNote());
                                    dataFirebase.myRefFolder.child(IdFolder).child(newKey).setValue(noteFireBase);
                                    dataBase.FolderDao().updateIdKeyFolderAndIdNFirebase(fold.getmId(), IdFolder, newKey);
                                    dataBase.updateFolderNoteUploadOnFirebaseDao().deleteByDate(updateAddDelete.getDate(),updateAddDelete.getParent_id());
                                }
                            }
                        }
                    }
                }
            }
            return null;
        }

        private String getTitleById(List<FolderBase> folderBase, Long parent_id) {
            String title = null;
            for (FolderBase folder : folderBase) {
                if (folder.getmId() == parent_id) {
                    title = folder.getTitle();
                }
            }
            return title;
        }

        private Long getDateById(List<FolderBase> folderBase, Long parent_id) {
            Long date = null;
            for (FolderBase folder : folderBase) {
                if (folder.getmId() == parent_id) {
                    date = folder.getDate();
                }
            }
            return date;
        }
    }

    public void setNoteOfFolderForFirebase(String idKeyFolder, NoteFireBase noteFireBase, FoldFireBase foldFireBase, String idNoteFirebase){
        myRefFolder.get().addOnCompleteListener(task -> {
            int i=0;
            if (task.isSuccessful()) {
                for(DataSnapshot t:task.getResult().getChildren()){
                    if(t.getKey().equals(idKeyFolder)){
                        i++;
                    }
                }
                if(i>0){
                    getMyRefFolder().child(idKeyFolder).child(idNoteFirebase).setValue(noteFireBase);
                }else{
                    getMyRefFolder().child(idKeyFolder).setValue(foldFireBase);
                    getMyRefFolder().child(idKeyFolder).child(idNoteFirebase).setValue(noteFireBase);
                }
               // FolderBase tsk = task.getResult().getValue(FolderBase.class);
            }
            else {
                Log.e("firebase", "Error getting data", task.getException());
            }
        });
    }

    public void setNoteOrNoteFolder(NoteFireBase noteFireBase, String idKeyFolder, Context context) {
        database = DataBase.getDatabase(context);
        //DatabaseReference data = FirebaseDatabase.getInstance().getReference("folder");
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = firebaseDatabase.getReference("folder");
       /* for(DataSnapshot res:databaseReference.getKey().){
            //FolderBase folderBase = res.getValue(FolderBase.class);

        }*/
       /* myRefFolder.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    addNoteOrFolderNote(database, dataSnapshot, noteFireBase, idKeyFolder);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });*/
        //myRefFolder.child(idKeyFolder).child(getKey()).setValue(noteFireBase);
    }

    public void addNoteOrFolderNote(DataBase dataBase, DataSnapshot dataSnapshot, NoteFireBase noteFireBase, String idKeyFolder) {
        new addAsyncNoteOrFolderNote(dataBase, dataSnapshot, noteFireBase, idKeyFolder).execute(dataSnapshot);
    }

    private static class addAsyncNoteOrFolderNote extends AsyncTask<DataSnapshot, Void, Void> {

        private final DataBase dataBase;
        private final DataFirebase dataFirebase;
        private final DataSnapshot dataSnapshot;

        NoteFireBase noteFireBase;
        String idKeyFolder;

        addAsyncNoteOrFolderNote(DataBase dataBase, DataSnapshot dataSnapshot, NoteFireBase noteFireBase, String idKeyFolder) {
            this.dataBase = dataBase;
            this.dataFirebase = new DataFirebase();
            this.dataSnapshot = dataSnapshot;
            this.noteFireBase = noteFireBase;
            this.idKeyFolder = idKeyFolder;
        }

        @Override
        protected Void doInBackground(DataSnapshot... dataSnapshots) {
            List<FolderBase> folderBase;
            folderBase = dataBase.FolderDao().getAllWithoutlivedata();
            int trueFolder = 0;
            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
               // FolderFireBase value = childSnapshot.getValue(FolderFireBase.class);

                if (idKeyFolder.equals(childSnapshot.getKey())) {
                    trueFolder++;
                }
            }
            if (trueFolder == 0) {
                for(FolderBase folder : folderBase){
                    if(folder.getIdKeyFolder().equals(idKeyFolder)){
                        FoldFireBase foldFireBase = new FoldFireBase(idKeyFolder, folder.getDate(), folder.getTitle());
                        dataFirebase.myRefFolder.child(idKeyFolder).setValue(foldFireBase);
                    }
                }
            }
            return null;
        }
    }
}
