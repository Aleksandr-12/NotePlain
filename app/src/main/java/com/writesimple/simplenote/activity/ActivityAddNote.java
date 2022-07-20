package com.writesimple.simplenote.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
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
import com.writesimple.simplenote.model.Tables.FolderBase;
import com.writesimple.simplenote.model.Tables.NoteBase;
import com.writesimple.simplenote.model.Tables.UpdateFolderNoteUploadOnFirebase;
import com.writesimple.simplenote.model.ViewModel.FoldNoteViewModel;
import com.writesimple.simplenote.model.ViewModel.FoldViewModel;
import com.writesimple.simplenote.model.ViewModel.NoteViewModel;
import com.writesimple.simplenote.model.ViewModel.UserViewModel;
import com.writesimple.simplenote.model.WorkFontManager;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ActivityAddNote extends BaseActivity {

    private EditText title;
    private EditText content;
    private String mFontFamily;
    private int mFontSizeTitle;
    private int mFontSizeContent;
    private WorkFontManager workFontManager;
    private SharedPreferences sharedForSwitch;
    public Boolean valueSharedSwitch;
    FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    NoteFireBase noteFireBase;
    String idNoteFirebase;
    String idKeyFolder;
    DataFirebase ref;
    UserViewModel userViewModel;
    ViewModelComponent component;
    NoteViewModel noteViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        ActionBar actionBar =getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getResources().getString(R.string.oneTitle));
        title = findViewById(R.id.title);
        content = findViewById(R.id.content);
        ImageButton bold;
        ImageButton italic;
        ImageButton underline;
        bold = findViewById(R.id.adBold);
        italic = findViewById(R.id.adItalic);
        underline = findViewById(R.id.adUnderline);

        callEventFormateFont(bold,"bold");
        callEventFormateFont(italic,"italic");
        callEventFormateFont(underline,"underline");

        workFontManager = new WorkFontManager(this,new ImageButton[]{bold, italic, underline},content);
        int ColorBakcgroundForButtonActive;
        ColorBakcgroundForButtonActive = ResourcesCompat.getColor(getResources(), R.color.colorBunttonFont,null);
        workFontManager.setColorBakcgroundForButtonActive(ColorBakcgroundForButtonActive);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            ref = new DataFirebase();
        }

        if(mFontFamily==null){
            mFontFamily = "open_sans";
        }
        if(mFontSizeTitle==0){
            mFontSizeTitle = 22;
        }
        if(mFontSizeContent==0){
            mFontSizeContent = 20;
        }

        workFontManager.setNoteFontFamily();
        workFontManager.setTextSizeFont(mFontSizeTitle,mFontSizeContent);

        component = DaggerViewModelComponent.builder().viewModelModule(new ViewModelModule(ActivityAddNote.this)).build();
        userViewModel = component.provideUserViewModel();

        BottomNavigationView bottomNavigationView = findViewById(R.id.BottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch aSwitch = (Switch) findViewById(R.id.switch1);
        View format = findViewById(R.id.format);
        if (loadSharedSwitch()) {
            format.setVisibility(View.VISIBLE);
            aSwitch.setChecked(true);
        } else {
            format.setVisibility(View.INVISIBLE);
            aSwitch.setChecked(false);
        }

        aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            @SuppressLint("CutPasteId") View format1 = findViewById(R.id.format);
            if (isChecked) {
                format1.setVisibility(View.VISIBLE);
                saveSharedValueSwitch(true);
            } else {
                format1.setVisibility(View.INVISIBLE);
                saveSharedValueSwitch(false);
            }
        });
        content.addTextChangedListener(workFontManager.onTextChangedListener(content));
    }

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

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {


        @SuppressLint({"NonConstantResourceId", "CheckResult"})
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_add:
                    if(currentUser!=null && isOnline()){
                        ref = new DataFirebase();
                    }
                    Date date = new Date();
                    String mTitle = title.getText().toString().trim();
                    String mContent = content.getText().toString().trim();
                    int mIsItalic;
                    int mBold;
                    int mUnderline;
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
                        Toast.makeText(ActivityAddNote.this, R.string.inputError, Toast.LENGTH_SHORT).show();
                    }else {
                        noteViewModel = new ViewModelProvider(ActivityAddNote.this).get(NoteViewModel.class);
                        //noteViewModel = component.provideNoteViewModel();
                        mAuth = FirebaseAuth.getInstance();
                        currentUser = mAuth.getCurrentUser();

                        if(getIntent().getSerializableExtra("folderItem") != null){
                           // FoldNoteViewModel foldNoteViewModel = new ViewModelProvider(ActivityAddNote.this).get(FoldNoteViewModel.class);
                            FoldNoteViewModel foldNoteViewModel = component.provideFoldNoteViewModel();
                            Intent intent;
                            intent = new Intent(ActivityAddNote.this, ActivityNotesOfFold.class);
                            FolderBase folderItem = (FolderBase) getIntent().getSerializableExtra("folderItem");
                            intent.putExtra("folderItem",folderItem);
                            if(folderItem.getIdKeyFolder()!=null){
                                idKeyFolder = folderItem.getIdKeyFolder();

                            }else{
                                idKeyFolder = "null";
                            }

                            long id = 0;
                            if(folderItem.getmId()!=null){
                                id = folderItem.getmId();
                            }
                            if(currentUser!=null && isOnline() && userViewModel.getBooleanDateSubs()){
                                FoldViewModel foldViewModel = component.provideFoldViewModel();
                                idNoteFirebase = ref.getKey();
                                noteFireBase = new NoteFireBase(idNoteFirebase,date.getTime(),mTitle,mContent);
                                FoldFireBase foldFireBase = new FoldFireBase(idKeyFolder, folderItem.getDate(), folderItem.getTitle());
                                if(idKeyFolder.equals("null")){
                                    idKeyFolder = ref.getKeyIdFolder();
                                    ref.getMyRefFolder().child(idKeyFolder).setValue(foldFireBase);
                                    foldViewModel.updateFolder(id,idKeyFolder,folderItem.getDate());
                                    ref.getMyRefFolder().child(idKeyFolder).child(idNoteFirebase).setValue(noteFireBase);
                                }else{
                                    idKeyFolder = foldViewModel.getFolderById(folderItem.getmId());
                                    ref.setNoteOfFolderForFirebase(idKeyFolder,noteFireBase, foldFireBase,idNoteFirebase);
                                }
                            }else{
                                idNoteFirebase = "null";
                                foldNoteViewModel.addUpdateFolderNoteUploadOnFirebase( new UpdateFolderNoteUploadOnFirebase(idKeyFolder,"null",0,1,0,id,date.getTime()));
                            }
                            FolderBase folderBase = new FolderBase(mTitle, mContent,mFontFamily,mFontSizeTitle,mFontSizeContent,mBold,mIsItalic,mUnderline, date.getTime(),idNoteFirebase,id,idKeyFolder);
                            foldNoteViewModel.addFolder(folderBase);
                            startActivity(intent);
                            finish();
                        }else{
                            Intent intent;
                            intent = new Intent(ActivityAddNote.this, MainActivity.class);
                            noteFireBase = new NoteFireBase(idNoteFirebase,date.getTime(),mTitle,mContent);
                            if(currentUser!=null && isOnline() && userViewModel.getBooleanDateSubs()){
                                 idNoteFirebase = ref.getKey();
                                 noteFireBase = new NoteFireBase(idNoteFirebase,date.getTime(),mTitle,mContent);
                                ref.setMyRef(noteFireBase);
                            }else{
                                idNoteFirebase = "null";
                            }
                             //noteViewModel.addNote(new NoteBase(mTitle, mContent,mFontFamily,mFontSizeTitle,mFontSizeContent,mBold,mIsItalic,mUnderline, date.getTime(),idNoteFirebase));
                            noteViewModel.insert(new NoteBase(mTitle, mContent,mFontFamily,mFontSizeTitle,mFontSizeContent,mBold,mIsItalic,mUnderline, date.getTime(),idNoteFirebase));
                            startActivity(intent);
                            finish();
                        }

                    }
                    return true;
                case R.id.cansel:
                    finish();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null){
            return;
        }
        mFontFamily = data.getStringExtra("mFontFamily");
        mFontSizeTitle = data.getIntExtra("mFontSizeTitle",16);
        mFontSizeContent = data.getIntExtra("mFontSizeContent",16);
        workFontManager.setIdTitle(title);
        workFontManager.setIdContent(content);
        workFontManager.setFontFamily(mFontFamily);
        workFontManager.setNoteFontFamily();
        workFontManager.setTextSizeFont(mFontSizeTitle,mFontSizeContent);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_fonts:
                Intent intent = new Intent(ActivityAddNote.this, ActivityFontSettings.class);
                intent.putExtra("mFontFamily", mFontFamily);
                intent.putExtra("mFontSizeTitle", mFontSizeTitle);
                intent.putExtra("mFontSizeContent", mFontSizeContent);
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
