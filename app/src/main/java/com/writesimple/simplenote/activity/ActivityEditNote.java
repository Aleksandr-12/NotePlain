package com.writesimple.simplenote.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.writesimple.simplenote.DI.DaggerViewModelComponent;
import com.writesimple.simplenote.DI.ViewModelComponent;
import com.writesimple.simplenote.DI.ViewModelModule;
import com.writesimple.simplenote.R;
import com.writesimple.simplenote.FireBase.DataFirebase;
import com.writesimple.simplenote.FireBase.FoldFireBase;
import com.writesimple.simplenote.FireBase.NoteFireBase;
import com.writesimple.simplenote.model.RxBus;
import com.writesimple.simplenote.model.Tables.FolderBase;
import com.writesimple.simplenote.model.Tables.NoteBase;
import com.writesimple.simplenote.model.Tables.UpdateFolderNoteUploadOnFirebase;
import com.writesimple.simplenote.model.Tables.dataForFirebaseDateDelete;
import com.writesimple.simplenote.model.Tables.dataForFirebaseDateUpdate;
import com.writesimple.simplenote.model.ViewModel.FoldNoteViewModel;
import com.writesimple.simplenote.model.ViewModel.NoteViewModel;
import com.writesimple.simplenote.model.ViewModel.UserViewModel;
import com.writesimple.simplenote.model.WorkFontManager;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ActivityEditNote extends BaseActivity {

    public WorkFontManager workFontManager;
    EditText title, content;
    private NoteViewModel noteViewModel;
    private FoldNoteViewModel foldNoteViewModel;
    private Long id;
    private String idNoteFirebase;
    private String idKeyFolder;
    private String mFontFamily;
    private int mFontSizeTitle;
    private int mFontSizeContent;
    private int mIsItalic;
    private int mBold;
    private int mUnderline;
    private int colorBackground;
    FirebaseUser currentUser;
    NoteFireBase noteFireBase;
    DataFirebase ref;
    UserViewModel userViewModel;

    private SharedPreferences sharedForSwitch;
    private Boolean valueSharedSwitch;
    private  Long date;
    private ViewModelComponent component;
    int ColorBakcgroundForButtonActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getResources().getString(R.string.oneTitle));
        //Подключение dagger2
        component = DaggerViewModelComponent.builder().viewModelModule(new ViewModelModule(ActivityEditNote.this)).build();
        userViewModel = component.provideUserViewModel();
        BottomNavigationView bottomNavigationView = findViewById(R.id.BottomNavigationEdit);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        title = findViewById(R.id.titleEdit);
        content = findViewById(R.id.contentEdit);
        ImageButton bold;
        ImageButton italic;
        ImageButton underline;
        bold = findViewById(R.id.bold);
        italic = findViewById(R.id.italic);
        underline = findViewById(R.id.underline);
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            ref = new DataFirebase();
        }

        callEventFormateFont(bold, "bold");
        callEventFormateFont(italic, "italic");
        callEventFormateFont(underline, "underline");
        ColorBakcgroundForButtonActive = ResourcesCompat.getColor(getResources(), R.color.colorBunttonFont, null);

        workFontManager = new WorkFontManager(this, new ImageButton[]{bold, italic, underline}, content);
        RxBus.getInstance().listenNote().subscribe(getInputObserverNote());
        if (getIntent().getSerializableExtra("detail_note") != null) {
            NoteBase detailNote = (NoteBase) getIntent().getSerializableExtra("detail_note");
            id = detailNote.getmId();
            idNoteFirebase = detailNote.getIdNoteFirebase();
            date = detailNote.getDate();
            String detailTitle = detailNote.getTitle();
            String detailContent = detailNote.getContent();

            title.setText(detailTitle);
            content.setText(detailContent);

            mFontSizeTitle = detailNote.getFontSizeTitle();
            mFontSizeContent = detailNote.getFontSizeContent();

            mBold = detailNote.getIsBold();
            mIsItalic = detailNote.getIsItalic();
            mUnderline = detailNote.getIsUnderline();
            workFontManager.setmBold(mBold);
            workFontManager.setmIsItalic(mIsItalic);
            workFontManager.setmUnderline(mUnderline);

            workFontManager.setColorBakcgroundForButtonActive(ColorBakcgroundForButtonActive);

            workFontManager.exangeStyleFont(content.getText().toString());
            mFontFamily = detailNote.getFontFamily();
            workFontManager.setIdTitle(title);
            workFontManager.setIdContent(content);
            workFontManager.setFontFamily(mFontFamily);
            workFontManager.setNoteFontFamily();
            workFontManager.setTextSizeFont(mFontSizeTitle, mFontSizeContent);
            workFontManager.setContentText(content.getText().toString());
        }
        if (getIntent().getSerializableExtra("folder_note") != null) {

            FolderBase detailNote = (FolderBase) getIntent().getSerializableExtra("folder_note");
            id = detailNote.getmId();
            idNoteFirebase = detailNote.getIdNoteFirebase();
            idKeyFolder = detailNote.getIdKeyFolder();
            String detailTitle;
            String detailContent;
            if(detailNote.getTitle()!=null){
                detailTitle = detailNote.getTitle();
            }else{
                detailTitle = "no Title";
            }

            if(detailNote.getNote()!=null){
                detailContent = detailNote.getNote();
            }else{
                detailContent = "no Note";
            }

            if(detailNote.getDate()!=null){
                date = detailNote.getDate();
            }else{
                date = new Date().getTime();
            }

            title.setText(detailTitle);
            content.setText(detailContent);

            mFontSizeTitle = detailNote.getFontSizeTitle();
            mFontSizeContent = detailNote.getFontSizeContent();

            mBold = detailNote.getIsBold();
            mIsItalic = detailNote.getIsItalic();
            mUnderline = detailNote.getIsUnderline();
            workFontManager.setmBold(mBold);
            workFontManager.setmIsItalic(mIsItalic);
            workFontManager.setmUnderline(mUnderline);

            workFontManager.setColorBakcgroundForButtonActive(ColorBakcgroundForButtonActive);

            workFontManager.exangeStyleFont(content.getText().toString());
            mFontFamily = detailNote.getFontFamily();
            workFontManager.setIdTitle(title);
            workFontManager.setIdContent(content);
            workFontManager.setFontFamily(mFontFamily);
            workFontManager.setNoteFontFamily();
            workFontManager.setTextSizeFont(mFontSizeTitle, mFontSizeContent);
            workFontManager.setContentText(content.getText().toString());
        }
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch aSwitch =  findViewById(R.id.switch1);
        View format = findViewById(R.id.format);
        if (loadSharedSwitch()) {
            format.setVisibility(View.VISIBLE);
            aSwitch.setChecked(true);
        } else {
            format.setVisibility(View.INVISIBLE);
            aSwitch.setChecked(false);
        }

        aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                format.setVisibility(View.VISIBLE);
                saveSharedValueSwitch(true);
            } else {
                format.setVisibility(View.INVISIBLE);
                saveSharedValueSwitch(false);
            }
        });
        content.addTextChangedListener(workFontManager.onTextChangedListener(content));
    }
    private Observer<NoteBase> getInputObserverNote() {
        return new Observer<NoteBase>() {
            @Override
            public void onSubscribe(@NotNull Disposable d) {
            }
            @Override
            public void onNext(@NotNull NoteBase detailNote) {
                Log.d("xxxxxxxxxxxxxxxxxxxxxxx",detailNote+" //");

            }
            @Override
            public void onError(@NotNull Throwable e) {
            }
            @Override
            public void onComplete() {
            }
        };
    }
    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @SuppressLint("NonConstantResourceId")
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            noteViewModel = component.provideNoteViewModel();
            foldNoteViewModel = component.provideFoldNoteViewModel();

            switch (item.getItemId()) {
                case R.id.nav_add_edit:
                    title = findViewById(R.id.titleEdit);
                    content = findViewById(R.id.contentEdit);
                    String mTitle = title.getText().toString().trim();
                    String mContent = content.getText().toString().trim();
                    mBold =  workFontManager.getmBold();
                    mIsItalic = workFontManager.getmIsItalic();
                    mUnderline = workFontManager.getmUnderline();
                    if(mFontFamily==null){
                        mFontFamily = "open_sans";
                    }
                    if(mFontSizeTitle==0){
                        mFontSizeTitle = 22;
                    }
                    if(mFontSizeContent==0){
                        mFontSizeContent = 20;
                    }

                    if(mTitle.isEmpty() || mContent.isEmpty()){
                        Toast.makeText(ActivityEditNote.this, R.string.inputError, Toast.LENGTH_SHORT).show();
                    }else {
                        if (getIntent().getSerializableExtra("folder_note") != null) {
                            foldNoteViewModel.updateNote(id,mTitle,mContent,mFontFamily,mFontSizeTitle,mFontSizeContent, mBold,  mIsItalic, mUnderline);
                            if(currentUser!=null && isOnline() && userViewModel.getBooleanDateSubs()){
                                noteFireBase = new NoteFireBase(idNoteFirebase,date,mTitle,mContent);
                                FolderBase folderBase = foldNoteViewModel.getFolderIdKeyFolder(idKeyFolder);
                                FoldFireBase foldFireBase = new FoldFireBase(idKeyFolder, folderBase.getDate(), folderBase.getTitle());
                                if(idKeyFolder.equals("null")){
                                    idKeyFolder = ref.getKeyIdFolder();
                                    ref.getMyRefFolder().child(idKeyFolder).setValue(foldFireBase);
                                    foldNoteViewModel.updateFolder(id,idKeyFolder,folderBase.getDate());
                                    ref.getMyRefFolder().child(idKeyFolder).child(idNoteFirebase).setValue(noteFireBase);
                                }else{
                                    ref.setNoteOfFolderForFirebase(idKeyFolder,noteFireBase, foldFireBase,idNoteFirebase);
                                }
                            }else{
                                if(!(loadSharedSettings(sharedForUpdate,valueUpdateSettings)).equals("noUpdate")) {
                                    foldNoteViewModel.addUpdateFolderNoteUploadOnFirebase(new UpdateFolderNoteUploadOnFirebase(idKeyFolder, idNoteFirebase, 1, 0, 0, id, date));
                                }
                            }
                        }else{
                            // noteViewModel.updateNote(id,mTitle,mContent,mFontFamily,mFontSizeTitle,mFontSizeContent, mBold,  mIsItalic, mUnderline);
                            Completable.fromAction(() -> noteViewModel.dataBase.noteDao().update(id,mTitle,mContent,mFontFamily,mFontSizeTitle,mFontSizeContent, mBold,  mIsItalic, mUnderline))
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new CompletableObserver() {
                                        @Override
                                        public void onSubscribe(@NotNull Disposable d) {
                                        }
                                        @Override
                                        public void onComplete() {
                                            Toast.makeText(ActivityEditNote.this,"Data updated", Toast.LENGTH_SHORT).show();
                                        }
                                        @SuppressLint("CheckResult")
                                        @Override
                                        public void onError(@NotNull Throwable e) {
                                            Toast.makeText(ActivityEditNote.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            if(currentUser!=null && isOnline() && userViewModel.getBooleanDateSubs()){
                                noteFireBase = new NoteFireBase(idNoteFirebase,date,mTitle,mContent);
                                ref.getMyRef().child(idNoteFirebase).setValue(noteFireBase);
                            }else{
                                if(!(loadSharedSettings(sharedForUpdate,valueMainUpdateSettings)).equals("noUpdate")){
                                    noteViewModel.addChangerUpdateNoteId(new dataForFirebaseDateUpdate(idNoteFirebase));
                                }
                            }
                        }
                        finish();
                    }
                    return true;
                case R.id.cansole_edit:
                    AlertDialog.Builder alertDialog;
                    alertDialog = new AlertDialog.Builder(ActivityEditNote.this);
                    View view;
                    view = getLayoutInflater().inflate(R.layout.alert_dialog_delete_note,null);
                    alertDialog.setView(view);
                    alertDialog.setPositiveButton("Да", (dialog, which) -> {
                        if(getIntent().getSerializableExtra("folder_note") != null){
                            foldNoteViewModel.deleteByIdNote(id);
                            if(currentUser!=null && isOnline()  && userViewModel.getBooleanDateSubs()){
                                ref.getMyRefFolder().child(idKeyFolder).child(idNoteFirebase).removeValue();
                            }else{
                                if(idKeyFolder!=null){
                                    if(idNoteFirebase!=null){
                                        if(!(loadSharedSettings(sharedForDelete,valueDeleteSettings)).equals("noDelete")) {
                                            foldNoteViewModel.addUpdateFolderNoteUploadOnFirebase( new UpdateFolderNoteUploadOnFirebase(idKeyFolder,idNoteFirebase,0,0,1,id,date));
                                        }
                                    }else{
                                        foldNoteViewModel.deleleteByIdNoteFirebase(id, idKeyFolder, date);
                                    }
                                }
                                assert idKeyFolder != null;
                                if(idKeyFolder.equals("null")){
                                    if(idNoteFirebase.equals("null")){
                                        foldNoteViewModel.deleleteFolderNoteUploadOnFirebase(id,date);
                                    }
                                }
                            }
                        }
                        if(getIntent().getSerializableExtra("detail_note") != null){
                            noteViewModel.deleteByIdNote(id);
                            if(currentUser!=null && isOnline() && userViewModel.getBooleanDateSubs()){
                                ref.getMyRef().child(idNoteFirebase).removeValue();
                            }else if(!idNoteFirebase.equals("null")){
                                if(!(loadSharedSettings(sharedForDelete,valueMainDeleteSettings)).equals("noDelete")){
                                    noteViewModel.addChangerDeleteNoteId(new dataForFirebaseDateDelete(idNoteFirebase));
                                }
                            }
                        }
                        dialog.dismiss();
                        finish();
                    });
                    alertDialog.setNegativeButton(getResources().getString(R.string.no), (dialog, which) -> dialog.dismiss());
                    alertDialog.setTitle(getResources().getString(R.string.isDeleteNote));
                    alertDialog.show();
                    return true;
            }
            return false;
        }
    };
    private Boolean loadSharedSwitch() {
        sharedForSwitch = getPreferences(MODE_PRIVATE);
        return sharedForSwitch.getBoolean(String.valueOf(valueSharedSwitch),false);
    }

    private void saveSharedValueSwitch(Boolean value) {
        sharedForSwitch = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editShareSort = sharedForSwitch.edit();
        editShareSort.putBoolean(String.valueOf(valueSharedSwitch), value);
        editShareSort.apply();
    }


    private void callEventFormateFont(ImageButton event, String value) {
        event.setOnClickListener(view -> workFontManager.initVarFormateFont(value,content.getText().toString()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null){
            return;
        }
        mFontFamily = data.getStringExtra("mFontFamily");
        mFontSizeTitle = data.getIntExtra("mFontSizeTitle",16);
        mFontSizeContent = data.getIntExtra("mFontSizeContent",16);
        colorBackground = data.getIntExtra("colorBackground",1);
        workFontManager.setFontFamily(mFontFamily);
        workFontManager.setNoteFontFamily();
        workFontManager.setTextSizeFont(mFontSizeTitle,mFontSizeContent);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_fonts:
                Intent intent = new Intent(ActivityEditNote.this, ActivityFontSettings.class);
                intent.putExtra("mFontFamily", mFontFamily);
                intent.putExtra("mFontSizeTitle", mFontSizeTitle);
                intent.putExtra("mFontSizeContent", mFontSizeContent);
                intent.putExtra("colorBackground", colorBackground);
                startActivityForResult(intent, 1);
                return true;
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_add, menu);
        return true;
    }
}
