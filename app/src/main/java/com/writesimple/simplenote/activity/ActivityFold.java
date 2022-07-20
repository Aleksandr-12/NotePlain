package com.writesimple.simplenote.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.writesimple.simplenote.DI.DaggerViewModelComponent;
import com.writesimple.simplenote.DI.ViewModelComponent;
import com.writesimple.simplenote.DI.ViewModelModule;
import com.writesimple.simplenote.R;
import com.writesimple.simplenote.adapter.FolderAdapter;
import com.writesimple.simplenote.FireBase.DataFirebase;
import com.writesimple.simplenote.FireBase.FoldFireBase;
import com.writesimple.simplenote.model.Tables.FolderBase;
import com.writesimple.simplenote.model.Tables.UpdateFolderNoteUploadOnFirebase;
import com.writesimple.simplenote.model.ViewModel.FoldNoteViewModel;
import com.writesimple.simplenote.model.ViewModel.FoldViewModel;
import com.writesimple.simplenote.model.ViewModel.UpdateFolderNoteUOnFirebaseViewModel;
import com.writesimple.simplenote.model.ViewModel.UserViewModel;

import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import static android.graphics.Color.parseColor;

public class ActivityFold extends BaseActivity  implements SearchView.OnQueryTextListener {


    private final Context context = this;

    String[] data = {"умолчанию", "дате", "названию"};
    SharedPreferences sharedForSort;
    String valueSharedSort = "TEXT_SORT";

    SharedPreferences sharedForAuth;
    String valueSharedAuth = "TEXT_AUTH";

    SharedPreferences sharedForLayout;
    Boolean valueSharedLayout;

    private FoldNoteViewModel foldNoteViewModel;
    private FoldViewModel foldViewModel;
    RecyclerView recyclerView;
    private FolderAdapter adapter;
    private final Paint p = new Paint();

    private AlertDialog.Builder alertDialog;
    private View view;
    private ImageButton imageView;

    FirebaseUser currentUser;
    DatabaseReference ref;
    List<FolderBase> isFolders;
    DataFirebase dataFirebase;
    UserViewModel userViewModel;
    private ViewModelComponent component;

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getResources().getString(R.string.FolderTitle));
        component = DaggerViewModelComponent.builder().viewModelModule(new ViewModelModule(ActivityFold.this)).build();
        ref = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            dataFirebase = new DataFirebase();
        }
        userViewModel = component.provideUserViewModel();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter(this, R.layout.spinner_layout_view, data);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_layout_items);

        Spinner spinner = findViewById(R.id.spinner);
        spinner.setAdapter(arrayAdapter);

        spinner.setPrompt("Title");
        spinner.setSelection(loadSharedValueSort());

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                saveSharedValueSort(position);
                adapter.updateSortAndAdapter(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        imageView = findViewById(R.id.imageView);

        recyclerView = findViewById(R.id.recyclerV);
        foldNoteViewModel = component.provideFoldNoteViewModel();
        foldViewModel = component.provideFoldViewModel();
        adapter = new FolderAdapter(context, new ArrayList<FolderBase>(), foldNoteViewModel);
        setLayoutManagerAndAdapter();
        imageView.setOnClickListener(view -> {
            if (loadSharedLayout()) {
                saveSharedValueLayout(false);
            } else {
                saveSharedValueLayout(true);
            }
            setLayoutManagerAndAdapter();
        });

        /*foldViewModel.getAllFolders().observe(this, folders -> {
            adapter.addFolder(folders, loadSharedValueSort());
            setTextSortVisible(folders);
        });*/
        foldViewModel.getAllRx().observe(this,folders -> {
            adapter.addFolder(folders, loadSharedValueSort());
            setTextSortVisible(folders);
        });

        UpdateFolderNoteUOnFirebaseViewModel updateFolderNoteUOnFirebaseViewModel = component.provideUpdateFolderNoteUOnFirebaseViewModel();
        if(currentUser!=null && isOnline() && userViewModel.getBooleanDateSubs()){
                updateFolderNoteUOnFirebaseViewModel.getAllUpdateFUploadOnFB().observe(this,folds -> {
                    if(!folds.isEmpty()){
                        DataFirebase dataFirebase = new DataFirebase();
                        dataFirebase.addDataInFirebase(context,dataFirebase);
                        alertDialog = new AlertDialog.Builder(ActivityFold.this);
                        alertDialog.setPositiveButton(getResources().getString(R.string.clear), (dialog, which) -> dialog.dismiss());
                        alertDialog.setMessage(getResources().getString(R.string.sendDataCloud));
                        alertDialog.setTitle(getResources().getString(R.string.sendData));
                        alertDialog.show();
                    }
                });
        }
        initSwipe();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle(getResources().getString(R.string.enterNameFold));
            final EditText input = new EditText(context);
            alert.setView(input);
            alert.setPositiveButton(getResources().getString(R.string.make), (dialog, whichButton) -> {
                Date date = new Date();
                String idKeyFolder;
                String title = input.getText().toString();
                if(currentUser!=null && isOnline() && userViewModel.getBooleanDateSubs()){
                    DataFirebase dataFirebase = new DataFirebase();
                    idKeyFolder = dataFirebase.getKeyIdFolder();
                    FoldFireBase foldFireBase = new FoldFireBase(idKeyFolder, date.getTime(), title);
                    dataFirebase.setRefForFolder(foldFireBase);
                    foldViewModel.addFolder(new FolderBase(title,date.getTime(),idKeyFolder));
                }else{
                    idKeyFolder = "null";
                    foldViewModel.addFolder(new FolderBase(title,date.getTime(),idKeyFolder));
                    foldNoteViewModel.addUpdateFolderNoteUploadOnFirebase( new UpdateFolderNoteUploadOnFirebase(idKeyFolder,"null",0,1,0,date.getTime()));
                }
            });
            alert.setNegativeButton(getResources().getString(R.string.console), (dialog, whichButton) -> {
            });
            alert.show();
        });
    }
    private void callFunForCloudSyncUpload() {
            if(currentUser!=null && isOnline() && userViewModel.getBooleanDateSubs()){
                    alertDialog = new AlertDialog.Builder(ActivityFold.this);
                    alertDialog.setPositiveButton(getResources().getString(R.string.yes), (dialog, which) -> {
                        DataFirebase dataFirebase = new DataFirebase();
                        dataFirebase.preWriteDataInFirebase(context);
                        dialog.dismiss();
                    });
                    alertDialog.setNegativeButton(getResources().getString(R.string.no), (dialog, which) -> dialog.dismiss());
                    alertDialog.setTitle(getResources().getString(R.string.forseUploadCload));
                   alertDialog.setMessage(getResources().getString(R.string.loadIsDeleteOnCloud));
                    alertDialog.show();

            }else {
                alertDialog = new AlertDialog.Builder(ActivityFold.this);
                alertDialog.setPositiveButton(getResources().getString(R.string.ok), (dialog, which) -> dialog.dismiss());
                alertDialog.setTitle(getResources().getString(R.string.dontAccessCloud));
                alertDialog.show();
            }
    }
    private void callFunCloudSyncDownload() {
        if (currentUser != null && isOnline() && userViewModel.getBooleanDateSubs()) {
            if(isOnline()){
                alertDialog = new AlertDialog.Builder(ActivityFold.this);
                alertDialog.setPositiveButton(getResources().getString(R.string.yes), (dialog, which) -> {
                    DataFirebase dataFirebase = new DataFirebase();
                    dataFirebase.getAndSetDataOfFolder(context);
                    dialog.dismiss();
                });
                alertDialog.setNegativeButton(getResources().getString(R.string.no), (dialog, which) -> dialog.dismiss());
                alertDialog.setTitle(getResources().getString(R.string.loadDataCloud));
                alertDialog.show();
            }
        } else {
            alertDialog = new AlertDialog.Builder(ActivityFold.this);
            alertDialog.setPositiveButton(getResources().getString(R.string.ok), (dialog, which) -> dialog.dismiss());
            alertDialog.setTitle(getResources().getString(R.string.dontAccessCloud));
            alertDialog.show();
        }
    }

    private void setLayoutManagerAndAdapter() {
        if (loadSharedLayout()) {
            setImageView();
            recyclerView.setAdapter(null);
            recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
            recyclerView.setAdapter(adapter);
        } else {
            setImageView();
            recyclerView.setAdapter(null);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(adapter);
        }
    }

    private void setImageView() {
        if (loadSharedLayout()) {
            imageView.setImageResource(R.drawable.list);
        } else {
            imageView.setImageResource(R.drawable.grig);
        }
    }

    private Boolean loadSharedLayout() {
        sharedForLayout = getPreferences(MODE_PRIVATE);
        return sharedForLayout.getBoolean(String.valueOf(valueSharedLayout), false);
    }


    private void saveSharedValueLayout(Boolean value) {
        sharedForLayout = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editShareSort = sharedForLayout.edit();
        editShareSort.putBoolean(String.valueOf(valueSharedLayout), value);
        editShareSort.apply();
    }

    private int loadSharedValueSort() {
        sharedForSort = getPreferences(MODE_PRIVATE);
        String valueSort = sharedForSort.getString(valueSharedSort, "");
        int value;
        switch (valueSort) {
            case "date":
                value = 1;
                break;
            case "title":
                value = 2;
                break;
            default:
                value = 0;
        }
       // setIconForSpinner();
        return value;
    }

    private void saveSharedValueSort(int position) {
       // setIconForSpinner();
        String value = null;
        switch (position) {
            case 1:
                value = "date";
                break;
            case 2:
                value = "title";
                break;
        }
        sharedForSort = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editShareSort = sharedForSort.edit();
        editShareSort.putString(valueSharedSort, value);
        editShareSort.apply();
    }

    private void saveSharedValueAuth() {
        sharedForAuth = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editShareAuth = sharedForAuth.edit();
        editShareAuth.putString(valueSharedAuth, "exit_auth");
        editShareAuth.apply();
    }

    private void setTextSortVisible(@NotNull List<FolderBase> notes) {
        TextView text_sort = findViewById(R.id.text_sort);
        AppCompatSpinner spinner = findViewById(R.id.spinner);
        TextView idIsEmpty = findViewById(R.id.idIsEmpty);
        imageView = findViewById(R.id.imageView);
        if (!notes.isEmpty()) {
            text_sort.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);
            idIsEmpty.setVisibility(View.INVISIBLE);
        } else {
            text_sort.setVisibility(View.INVISIBLE);
            spinner.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.INVISIBLE);
            idIsEmpty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_folder, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(
                new ComponentName(this, MainActivity.class)));
        searchView.setIconifiedByDefault(false);
        LinearLayout ll = (LinearLayout) searchView.getChildAt(0);
        LinearLayout ll2 = (LinearLayout) ll.getChildAt(2);
        LinearLayout ll3 = (LinearLayout) ll2.getChildAt(1);
        SearchView.SearchAutoComplete autoComplete = (SearchView.SearchAutoComplete) ll3.getChildAt(0);
        ImageView searchIcon = searchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
        searchIcon.setColorFilter(getResources().getColor(R.color.colorTheme),
                android.graphics.PorterDuff.Mode.SRC_IN);
        autoComplete.setHintTextColor(getResources().getColor(R.color.colorTheme));
        autoComplete.setTextColor(getResources().getColor(R.color.colorTheme));
        View searchPlate = searchView.findViewById(R.id.search);
        searchPlate.setBackgroundResource(R.drawable.background_search);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        adapter.searchFilter(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.searchFilter(newText);
        return true;
    }

    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NotNull RecyclerView recyclerView, RecyclerView.@NotNull ViewHolder viewHolder, RecyclerView.@NotNull ViewHolder target) {
                return false;
            }
            @SuppressLint("InflateParams")
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                isFolders = foldNoteViewModel.getAllNotesOfFolderWithoutLiveData(Objects.requireNonNull(foldViewModel.getAllFolders().getValue()).get(position).mId);
                if(isFolders.size() >0){
                    alertDialog = new AlertDialog.Builder(ActivityFold.this);
                    alertDialog.setPositiveButton(getResources().getString(R.string.clear), (dialog, which) -> {
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    });
                    alertDialog.setMessage(getResources().getString(R.string.cantDeleteInFoldOfNote));
                    alertDialog.setTitle(getResources().getString(R.string.forbidden));
                    alertDialog.show();
                }else{
                    alertDialog = new AlertDialog.Builder(ActivityFold.this);
                    view = getLayoutInflater().inflate(R.layout.alert_dialog_delete_note, null);
                    alertDialog.setView(view);
                    alertDialog.setPositiveButton(getResources().getString(R.string.delete), (dialog, which) -> {
                        foldViewModel.deleteByIdNote(foldViewModel.getAllFolders().getValue().get(position).mId);
                        if(currentUser!=null && isOnline() && userViewModel.getBooleanDateSubs()){
                            dataFirebase.getMyRefFolder().child(foldViewModel.getAllFolders().getValue().get(position).getIdKeyFolder()).removeValue();
                            }else{
                                if(foldViewModel.getAllFolders().getValue().get(position).getIdKeyFolder() !=null){
                                    if(!(loadSharedSettings(sharedForDelete,valueDeleteSettings)).equals("noDelete")){
                                        foldNoteViewModel.addUpdateFolderNoteUploadOnFirebase( new UpdateFolderNoteUploadOnFirebase(foldViewModel.getAllFolders().getValue().get(position).getIdKeyFolder(),"null",0,0,1, foldViewModel.getAllFolders().getValue().get(position).getmId(), foldViewModel.getAllFolders().getValue().get(position).getDate()));
                                    }
                                }
                                if(foldViewModel.getAllFolders().getValue().get(position).getIdKeyFolder() .equals("null")){
                                    foldNoteViewModel.deleleteFolderNoteUploadOnFirebase(foldViewModel.getAllFolders().getValue().get(position).getmId(), foldViewModel.getAllFolders().getValue().get(position).getDate());
                                }
                        }
                        dialog.dismiss();
                    });
                    alertDialog.setNegativeButton(getResources().getString(R.string.no), (dialog, which) -> {
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    });
                    alertDialog.setTitle(getResources().getString(R.string.isDeleteFod));
                    alertDialog.show();
                }
            }

            @Override
            public void onChildDraw(@NotNull Canvas c, @NotNull RecyclerView recyclerView, RecyclerView.@NotNull ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;
                    if (dX > 0) {
                        p.setColor(parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    } else {
                        p.setColor(parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete);

                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null){
            return;
        }
        String delete = data.getStringExtra("deleteFolder");
        String update = data.getStringExtra("updateFolder");

        saveSharedValueSettings(sharedForDelete,valueDeleteSettings,delete);
        saveSharedValueSettings(sharedForUpdate,valueUpdateSettings,update);
    }

   /* private String loadSharedSettings(SharedPreferences shared,String name) {
        shared = getPreferences(MODE_PRIVATE);
        return shared.getString(name, "");
    }

    private void saveSharedValueSettings(SharedPreferences shared,String name,String value) {
        shared = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editShareSort = shared.edit();
        editShareSort.putString(name, value);
        editShareSort.apply();
    }*/

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(ActivityFold.this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.download:
                callFunCloudSyncDownload();
                return true;
            case R.id.upload:
                callFunForCloudSyncUpload();
                return true;
            case R.id.action_settings:
                Intent intent2 = new Intent(ActivityFold.this, ActivitySettings.class);
                intent2.putExtra("deleteFolder", loadSharedSettings(sharedForDelete,valueDeleteSettings));
                intent2.putExtra("updateFolder", loadSharedSettings(sharedForUpdate,valueUpdateSettings));
                startActivityForResult(intent2, 2);
                return true;
            case R.id.shoAllNoteOfFolders:
                Intent intent3 = new Intent(ActivityFold.this, ActivityNotesOfFold.class);
                intent3.putExtra("showAllNotesOfFolders", "showAllNotesOfFolders");
                startActivity(intent3);
                finish();
                return true;
            case R.id.exit:
                saveSharedValueAuth();
                FirebaseAuth.getInstance().signOut();
                Intent ActivityExit = new Intent(getApplicationContext(), ActivityAuthenticate.class);
                startActivity(ActivityExit);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
