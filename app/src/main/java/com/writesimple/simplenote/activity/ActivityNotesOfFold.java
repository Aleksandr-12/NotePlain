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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.writesimple.simplenote.DI.DaggerViewModelComponent;
import com.writesimple.simplenote.DI.ViewModelComponent;
import com.writesimple.simplenote.DI.ViewModelModule;
import com.writesimple.simplenote.R;
import com.writesimple.simplenote.adapter.FolderNoteAdapter;
import com.writesimple.simplenote.FireBase.DataFirebase;
import com.writesimple.simplenote.model.Tables.FolderBase;
import com.writesimple.simplenote.model.Tables.UpdateFolderNoteUploadOnFirebase;
import com.writesimple.simplenote.model.ViewModel.FoldNoteViewModel;
import com.writesimple.simplenote.model.ViewModel.UserViewModel;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.graphics.Color.parseColor;

public class ActivityNotesOfFold extends BaseActivity implements SearchView.OnQueryTextListener {


    private final Context context = this;

    String[] data = {"умолчанию", "дате", "названию"};
    SharedPreferences sharedForSort;
    String valueSharedSort = "TEXT_SORT";

    SharedPreferences sharedForAuth;
    String valueSharedAuth = "TEXT_AUTH";

    SharedPreferences sharedForLayout;
    Boolean valueSharedLayout;

    private FoldNoteViewModel foldNoteViewModel;
    RecyclerView recyclerView;
    private FolderNoteAdapter adapter;
    private final Paint p = new Paint();

    private AlertDialog.Builder alertDialog;
    private View view;
    private ImageButton imageView;

    FirebaseUser currentUser;
    DataFirebase ref;
    Long id = null;
    String idKeyFolder;
    List<FolderBase> folderNotes;

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_of_note);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        ViewModelComponent component = DaggerViewModelComponent.builder().viewModelModule(new ViewModelModule(ActivityNotesOfFold.this)).build();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            ref = new DataFirebase();
        }
        FloatingActionButton fab = findViewById(R.id.fab);
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
            public void onNothingSelected(AdapterView<?> arg0) {}
        });
        FolderBase folderItem = null;
        String title = null;
        imageView = findViewById(R.id.imageView);
        recyclerView = findViewById(R.id.recyclerV);
        foldNoteViewModel = component.provideFoldNoteViewModel();

        if(getIntent().getSerializableExtra("folderItem")!=null){
            folderItem = (FolderBase) getIntent().getSerializableExtra("folderItem");
            if(folderItem.getmId()!=null) {
                id = folderItem.getmId();
            }
            if(folderItem.getIdKeyFolder()!=null) {
                idKeyFolder = folderItem.getIdKeyFolder();
            }
            if(folderItem.getTitle()!=null) {
                title = folderItem.getTitle();
                actionBar.setTitle(title);
            }
        }
        /*if(getIntent().getStringExtra("idKeyFolder")!=null){
              idKeyFolder = getIntent().getStringExtra("idKeyFolder");
        }*/
        initSwipe();
        if(getIntent().getSerializableExtra("showAllNotesOfFolders")!=null){
            fab.setVisibility(View.GONE);
            adapter = new FolderNoteAdapter(context, new ArrayList<>(),true, foldNoteViewModel);
          /* foldNoteViewModel.getAllIsNotNullParentIdLiveData().observe(this, folders->{
               folderNotes = folders;
               adapter.addNote(folders, loadSharedValueSort());
               setTextSortVisible(folders);
           });*/
            foldNoteViewModel.getAllRx().observe(this,folders -> {
                folderNotes = folders;
                adapter.addNote(folders, loadSharedValueSort());
                setTextSortVisible(folders);
          });
        }else{
            adapter = new FolderNoteAdapter(context, new ArrayList<>(),false,null);
            foldNoteViewModel.getAllNotesOfFoldRx(id).observe(this, folders -> {
                folderNotes = folders;
                adapter.addNote(folders, loadSharedValueSort());
                setTextSortVisible(folders);
            });
        }


        setLayoutManagerAndAdapter();
        imageView.setOnClickListener(view -> {
            if (loadSharedLayout()) {
                saveSharedValueLayout(false);
            } else {
                saveSharedValueLayout(true);
            }
            setLayoutManagerAndAdapter();
        });
        if(getIntent().getSerializableExtra("showAllNotesOfFolders") ==null){
            FolderBase finalFolderItem = folderItem;
            fab.setOnClickListener(view -> {
                Intent intent;
                intent = new Intent(ActivityNotesOfFold.this, ActivityAddNote.class);
                if(finalFolderItem!=null){
                    intent.putExtra("folderItem", finalFolderItem);
                }
                startActivity(intent);
                finish();
            });
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


  /*  private void setIconForSpinner() {
        Context themedContext = new ContextThemeWrapper(ActivityNotesOfFolder.this, R.style.spinner_style_title);
        LayoutInflater.from(themedContext)
                .inflate(R.layout.spinner_layout_items, null);
        new View(themedContext);
    }*/

    private int loadSharedValueSort() {
        sharedForSort = getPreferences(MODE_PRIVATE);
        String valueSort = sharedForSort.getString(valueSharedSort, "");
        int value;
        switch (valueSort) {
            case "name":
                value = 0;
                break;
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
        //setIconForSpinner();
        String value = null;
        switch (position) {
            case 0:
                value = "name";
                break;
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
        getMenuInflater().inflate(R.menu.menu_note, menu);
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
                alertDialog = new AlertDialog.Builder(ActivityNotesOfFold.this);
                view = getLayoutInflater().inflate(R.layout.alert_dialog_delete_note, null);
                alertDialog.setView(view);
                alertDialog.setPositiveButton(getResources().getString(R.string.delete), (dialog, which) -> {
                    foldNoteViewModel.deleteByIdNote(folderNotes.get(position).getmId());
                    adapter.notifyDataSetChanged();
                    UserViewModel userViewModel = new ViewModelProvider(ActivityNotesOfFold.this).get(UserViewModel.class);;
                    if(currentUser!=null && isOnline() && userViewModel.getBooleanDateSubs()){
                        ref.getMyRefFolder().child(folderNotes.get(position).getIdKeyFolder()).child(folderNotes.get(position).getIdNoteFirebase()).removeValue();
                    }else{
                        if(folderNotes.get(position).getIdKeyFolder() !=null){
                            if(folderNotes.get(position).getIdNoteFirebase()!=null){
                                if(!(loadSharedSettings(sharedForDelete,valueDeleteSettings)).equals("noDelete")){
                                    foldNoteViewModel.addUpdateFolderNoteUploadOnFirebase( new UpdateFolderNoteUploadOnFirebase(folderNotes.get(position).getIdKeyFolder(),folderNotes.get(position).getIdNoteFirebase(),0,0,1,id,folderNotes.get(position).getDate()));
                                }
                            }else{
                                foldNoteViewModel.deleleteByIdNoteFirebase(id,folderNotes.get(position).getIdKeyFolder(),folderNotes.get(position).getDate());
                            }
                        }
                        if(idKeyFolder.equals("null")){
                            if(folderNotes.get(position).getIdNoteFirebase().equals("null")){
                                foldNoteViewModel.deleleteFolderNoteUploadOnFirebase(id,folderNotes.get(position).getDate());
                            }
                        }
                    }
                    dialog.dismiss();
                });
                alertDialog.setNegativeButton(getResources().getString(R.string.no), (dialog, which) -> {
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                });
                alertDialog.setTitle(getResources().getString(R.string.isDeleteNote));
                alertDialog.show();
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

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(ActivityNotesOfFold.this, ActivityFold.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.go_main:
                Intent intent1 = new Intent(ActivityNotesOfFold.this, MainActivity.class);
                startActivity(intent1);
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
