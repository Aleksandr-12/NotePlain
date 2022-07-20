package com.writesimple.simplenote.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
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

import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.writesimple.simplenote.BillingModels.BillingClientSetup;
import com.writesimple.simplenote.DI.DaggerViewModelComponent;
import com.writesimple.simplenote.DI.ViewModelComponent;
import com.writesimple.simplenote.DI.ViewModelModule;
import com.writesimple.simplenote.R;
import com.writesimple.simplenote.adapter.NoteAdapter;
import com.writesimple.simplenote.FireBase.DataFirebase;
import com.writesimple.simplenote.model.Tables.NoteBase;
import com.writesimple.simplenote.model.Tables.User;
import com.writesimple.simplenote.model.Tables.dataForFirebaseDateDelete;
import com.writesimple.simplenote.model.ViewModel.NoteViewModel;
import com.writesimple.simplenote.model.ViewModel.UserViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static android.graphics.Color.parseColor;

public class MainActivity extends BaseActivity  implements SearchView.OnQueryTextListener, PurchasesUpdatedListener {

    private static AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener;
    private static BillingClient billingClient;
    private UserViewModel userViewModel;
    private FirebaseAuth mAuth;

    String[] data = {"умолчанию", "дате", "названию"};
    SharedPreferences sharedForSort;
    String valueSharedSort = "TEXT_SORT";

    SharedPreferences sharedForAuth;
    String valueSharedAuth = "TEXT_AUTH";

    SharedPreferences sharedForLayout;
    Boolean valueSharedLayout;

    private NoteViewModel noteViewModel;
    RecyclerView recyclerView;
    NoteAdapter adapter;
    Context context = this;
    private final Paint p = new Paint();

    private AlertDialog.Builder alertDialog;
    private View view;
    private ImageButton imageView;

    private DrawerLayout drawer;
    View header;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;
    private Toolbar toolbar;
    FirebaseUser currentUser;
    DataFirebase ref;
    @SuppressLint("StaticFieldLeak")
    static ImageView  cloud_sync;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"LongLogTag", "SetTextI18n", "CheckResult"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.MainTitle));
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        drawer = findViewById(R.id.drawer_layout);
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.color_icon);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        //authenticate
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if(currentUser!=null && isOnline()){
            ref = new DataFirebase();
        }
        navigationView = findViewById(R.id.nav_view);
        header = navigationView.getHeaderView(0);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent;
            intent = new Intent(MainActivity.this, ActivityAddNote.class);
            startActivity(intent);
        });

        setupDrawerLayout();

        drawerToggle = setupDrawerToggle();
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
        cloud_sync = findViewById(R.id.cloud_sync);
        recyclerView = findViewById(R.id.recyclerV);
        if(currentUser!=null && isOnline()){
            cloud_sync.setImageResource(R.mipmap.cloud_sync_foreground);
        }else {
            cloud_sync.setImageResource(R.mipmap.not_sync_foreground);
        }

        callFunForSyncFirebase(cloud_sync);

        adapter = new NoteAdapter(context, new ArrayList<>());
        setLayoutManagerAndAdapter();
        imageView.setOnClickListener(view -> {
            if (loadSharedLayout()) {
                saveSharedValueLayout(false);
            } else {
                saveSharedValueLayout(true);
            }
            setLayoutManagerAndAdapter();
        });
        ViewModelComponent component = DaggerViewModelComponent.builder().viewModelModule(new ViewModelModule(MainActivity.this)).build();
        userViewModel = component.provideUserViewModel();
        noteViewModel = component.provideNoteViewModel();
        //noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        /*noteViewModel.getAllNotes().observe(this,notes -> {
            adapter.addNote(notes, loadSharedValueSort());
            setTextSortVisible(notes);
        });*/

        noteViewModel.dataBase.noteDao().getAllRx()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(notes -> {
                    adapter.addNote(notes, loadSharedValueSort());
                    setTextSortVisible(notes);
                });
       /* noteViewModel.getAllRx().observe(this,notes -> {
            adapter.addNote(notes, loadSharedValueSort());
            setTextSortVisible(notes);
        });*/

        TextView subs = header.findViewById(R.id.subs);
        userViewModel.getDate().observe(this,date -> {
           if(date!=null){
              // userViewModel.operationWithDate();
               subs.setVisibility(View.VISIBLE);
               subs.setText("Подписка до: "+date);
           }
        });
        if(getIntent().getStringExtra("pullFromReloadSubs")!=null){
            if(getIntent().getStringExtra("pullFromReloadSubs").equals("true")){
                getIntent().putExtra("pullFromReloadSubs","false");
                callResponceSubscribe();
            }
        }
          if(getIntent().getStringExtra("pullFromReloadInApp")!=null){
            if(getIntent().getStringExtra("pullFromReloadInApp").equals("true")){
                getIntent().putExtra("pullFromReloadInApp","false");
                callResponceInApp();
            }
        }
        userViewModel.getIsNotifyInApp().observe(this,notify -> {
            if(notify.equals("noIsNotify")){
                userViewModel.updateIsNotify("yesIsNotify",true);
                callNotification(true);
            }
        });
        userViewModel.getIsNotifySubs().observe(this,notify -> {
            if(notify.equals("noIsNotify")){
                userViewModel.updateIsNotify("yesIsNotify",false);
                callNotification(false);
            }
        });
        userViewModel.getDateFirebaseSubs().observe(this,dateFromFirebaseSubs -> {
            if(dateFromFirebaseSubs!=null){
                User user = new User(dateFromFirebaseSubs,"noIsNotify","noSee");
                userViewModel.addForSubs(user);
            }
        });
        userViewModel.getDateFirebaseInApp().observe(this,dateFromFirebaseInApp -> {
            if(dateFromFirebaseInApp!=null){
                User user = new User(dateFromFirebaseInApp,"noIsNotify","noSee","isBuy");
                userViewModel.addForInApp(user);
            }
        });
        userViewModel.getBillingSubscribeTrue().observe(this,billingSubscribeTrue -> {
            if(billingSubscribeTrue!=null){
                callResponceSubscribe();
            }
        });
        initSwipe();

        if(getIntent().getStringExtra("update")!=null){
            noteViewModel.syncWithDataFirebase();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void asyncResponceSubscribe(){
        new asyncResponceSubscribe(this).execute();
    }

    @Override
    public void onPurchasesUpdated(@NonNull @NotNull BillingResult billingResult, @Nullable @org.jetbrains.annotations.Nullable List<Purchase> list) {

    }

    private class asyncResponceSubscribe extends AsyncTask<String, Void, Void> {
        PurchasesUpdatedListener listener;

        asyncResponceSubscribe(PurchasesUpdatedListener listener) {
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(final String... isNotify) {
            callResponceSubscribe();
            return null;
        }
    }

    private void callResponceSubscribe() {
        acknowledgePurchaseResponseListener = new AcknowledgePurchaseResponseListener() {
            @Override
            public void onAcknowledgePurchaseResponse(@NonNull @NotNull BillingResult billingResult) {
                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                }
            }
        };
        billingClient = BillingClientSetup.getInstance(getApplication().getApplicationContext(), this);
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {
            }
            @SuppressLint("LongLogTag")
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onBillingSetupFinished(@NonNull @NotNull BillingResult billingResult) {
                List<Purchase> purchases = billingClient.queryPurchases(BillingClient.SkuType.SUBS).getPurchasesList();
                if(purchases!=null){
                    if(purchases.size() > 0){
                        for(Purchase purchase:purchases){
                            if(purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED){
                                userViewModel.setTimeFromBilling(purchase.getPurchaseTime(),false);
                            }else{
                                userViewModel.updateIsSeeSubs("yesSee",false);
                            }
                        }
                    }else{
                        userViewModel.updateIsSeeSubs("yesSee",false);
                    }
                }else {
                    userViewModel.updateIsSeeSubs("yesSee",false);
                }

            }
        });
    }
    private void callResponceInApp() {
        acknowledgePurchaseResponseListener = billingResult -> {
            if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){

            }
        };
        billingClient = BillingClientSetup.getInstance(getApplication().getApplicationContext(), this);
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {
            }
            @SuppressLint("LongLogTag")
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onBillingSetupFinished(@NonNull @NotNull BillingResult billingResult) {
                List<Purchase> purchases = billingClient.queryPurchases(BillingClient.SkuType.INAPP).getPurchasesList();
                if (purchases != null) {
                    if (purchases.size() > 0) {
                        for (Purchase purchase : purchases) {
                            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                                userViewModel.setTimeFromBilling(purchase.getPurchaseTime(), true);
                            } else {
                                userViewModel.updateIsSeeSubs("yesSee", true);
                            }
                        }
                    } else {
                        userViewModel.updateIsSeeSubs("yesSee", true);
                    }
                }
            }
        });
    }

    private void callNotification(Boolean isbuy) {
        long[] vibrate = {  1000, 1000, 1000, 1000, 1000 };
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String CHANNEL_ID = "my_channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "Мои уведомления", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Описание канала");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        if(isbuy){
            builder.setContentTitle(getResources().getString(R.string.overInAp))
                    .setContentText(getResources().getString(R.string.subsOverDay))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setVibrate(vibrate);
        }else{
            builder.setContentTitle(getResources().getString(R.string.subs))
                    .setContentText(getResources().getString(R.string.subsOverDay))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setVibrate(vibrate);
            //.setSmallIcon(R.drawable.edit_reminder);
        }

        Intent intent1 = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent1, 0);
        builder.setContentIntent(pIntent);
        notificationManager.notify(1, builder.build());
    }

    private void callFunForSyncFirebase(ImageView cloud_sync) {
        cloud_sync.setOnClickListener(view -> {
            if(currentUser!=null && isOnline()){
                alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setPositiveButton(getResources().getString(R.string.yes), (dialog, which) -> {
                    DataFirebase dataFirebase = new DataFirebase();
                    dataFirebase.getDataNote(context);
                    dialog.dismiss();
                });
                alertDialog.setNegativeButton(getResources().getString(R.string.no), (dialog, which) -> dialog.dismiss());
                alertDialog.setTitle(getResources().getString(R.string.downloadFromFirebase));
                alertDialog.show();
            }else {
                alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setPositiveButton(getResources().getString(R.string.ok), (dialog, which) -> dialog.dismiss());
                alertDialog.setTitle(getResources().getString(R.string.dontAccessCloud));
                alertDialog.show();
            }
        });
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
        return value;
    }

    private void saveSharedValueSort(int position) {
        //setIconForSpinner();
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

    private String loadSharedValueAuth() {
        sharedForAuth = getPreferences(MODE_PRIVATE);
        return sharedForAuth.getString(valueSharedAuth, "");
    }

    private void saveSharedValueAuth(String value) {
        sharedForAuth = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editShareAuth = sharedForAuth.edit();
        editShareAuth.putString(valueSharedAuth, value);
        editShareAuth.apply();
    }

    private void setTextSortVisible(@NotNull List<NoteBase> notes) {
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
        getMenuInflater().inflate(R.menu.main, menu);
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
        autoComplete.setTextColor(getResources().getColor(R.color.colorTheme));
        View searchPlate = searchView.findViewById(R.id.search);
        searchPlate.setBackgroundResource(R.drawable.background_search);
        return true;
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.folder:
                Intent intent = new Intent(MainActivity.this, ActivityFold.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_settings:
                Intent intent2 = new Intent(MainActivity.this, ActivitySettingsMain.class);
                intent2.putExtra("deleteMainFolder", loadSharedSettings(sharedForDelete,valueMainDeleteSettings));
                intent2.putExtra("updateMainFolder", loadSharedSettings(sharedForUpdate,valueMainUpdateSettings));
                startActivityForResult(intent2, 2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null){
            return;
        }
        String delete = data.getStringExtra("deleteMainFolder");
        String update = data.getStringExtra("updateMainFolder");
        saveSharedValueSettings(sharedForDelete,valueMainDeleteSettings,delete);
        saveSharedValueSettings(sharedForUpdate,valueMainUpdateSettings,update);
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
                alertDialog = new AlertDialog.Builder(MainActivity.this);
                view = getLayoutInflater().inflate(R.layout.alert_dialog_delete_note, null);
                alertDialog.setView(view);
                String id = noteViewModel.getAll().get(position).getIdNoteFirebase();
                alertDialog.setPositiveButton(getResources().getString(R.string.delete), (dialog, which) -> {
                    noteViewModel.deleteByIdNote(Objects.requireNonNull(noteViewModel.getAll().get(position)).getmId());
                    adapter.notifyDataSetChanged();
                    if(currentUser!=null && isOnline() && userViewModel.getBooleanDateSubs()){
                        ref.getMyRef().child(id).removeValue();
                   }else{
                        if(!(loadSharedSettings(sharedForDelete,valueMainDeleteSettings)).equals("noDelete")){
                             noteViewModel.addChangerDeleteNoteId(new dataForFirebaseDateDelete(id));
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

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
         drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
         drawerToggle.onConfigurationChanged(newConfig);
    }

    @SuppressLint("NonConstantResourceId")
    private void setupDrawerLayout() {
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            switch (id) {
                case R.id.premium:
                    Intent i = new Intent(getApplicationContext(), ActivityPremium.class);
                    startActivity(i);
                    finish();
                    break;
                case R.id.nav_win:
                    Intent j = new Intent(getApplicationContext(), ActivityEditNote.class);
                    startActivity(j);
                    finish();
                    break;
                case R.id.exit:
                    saveSharedValueAuth("exit_auth");
                    FirebaseAuth.getInstance().signOut();
                    Intent ActivityExit = new Intent(getApplicationContext(), ActivityAuthenticate.class);
                    startActivity(ActivityExit);
                    finish();
                    break;
                case R.id.politic:
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://confidentiality.refsite.ru"));
                    startActivity(browserIntent);
                    break;
                case R.id.userAgreement:
                    Intent browserIntent1 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://useragreement.refsite.ru"));
                    startActivity(browserIntent1);
                    break;
            }
            return true;
        });

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
        if (getIntent().getStringExtra("auth")!=null){
            String auth = getIntent().getStringExtra("auth");
            saveSharedValueAuth(auth);
        }
         if((currentUser==null && ("exit_auth".equals(loadSharedValueAuth()))) || loadSharedValueAuth() == null || loadSharedValueAuth().equals("")){
            Intent ActivityAuth = new Intent(getApplicationContext(), ActivityAuthenticate.class);
            startActivity(ActivityAuth);
        }
        if("simple_auth".equals(loadSharedValueAuth())){
            TextView login = (TextView)  header.findViewById(R.id.login);
            login.setVisibility(View.VISIBLE);
            login.setText("Anymous");
        }

        if(currentUser!=null){
            TextView login = (TextView)  header.findViewById(R.id.login);
            TextView email = (TextView)  header.findViewById(R.id.email);
            ImageView photo = (ImageView)  header.findViewById(R.id.photo);
            if(currentUser.getDisplayName()!=null){
                login.setVisibility(View.VISIBLE);
                login.setText(currentUser.getDisplayName());
            }
            if(currentUser.getEmail()!=null){
                email.setVisibility(View.VISIBLE);
                email.setText(currentUser.getEmail());
            }
            if(currentUser.getPhotoUrl()!=null){
                String photoUrl = currentUser.getPhotoUrl().toString();
                Glide.with(this)
                        .load(photoUrl)
                        .circleCrop()
                        .placeholder(R.mipmap.avatar)
                        .into(photo);
            }
        }
    }

    public static class  NetworkChangeReceiver extends BroadcastReceiver {
        public NetworkChangeReceiver(){}
        @SuppressLint({"LongLogTag", "UnsafeProtectedBroadcastReceiver"})
        @Override
        public void onReceive(final Context context, final Intent intent) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
            if (cm.getActiveNetworkInfo() != null
                    && cm.getActiveNetworkInfo().isAvailable()
                    && cm.getActiveNetworkInfo().isConnected()) {
                cloud_sync.setImageResource(R.mipmap.cloud_sync_foreground);
            } else {
                cloud_sync.setImageResource(R.mipmap.not_sync_foreground);
            }
        }
    }
}