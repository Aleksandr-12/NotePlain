package com.writesimple.simplenote.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.writesimple.simplenote.BillingModels.BillingClientSetup;
import com.writesimple.simplenote.R;
import com.writesimple.simplenote.adapter.MyProductAdapter;
import com.writesimple.simplenote.model.DataBase;
import com.writesimple.simplenote.model.Tables.User;
import com.writesimple.simplenote.model.ViewModel.UserViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SubscribeActivity extends AppCompatActivity implements PurchasesUpdatedListener {

    BillingClient billingClient;
    AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener;
    private ProgressBar progressBar;
    Button loadProduct;
    RecyclerView recyclerView;
    TextView txtPremium;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);
        setupBillingClient();
        init();
    }

    private void init(){
        txtPremium = (TextView) findViewById(R.id.txtPremium);
        loadProduct = (Button)  findViewById(R.id.load_product);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        progressBar = findViewById(R.id.progressBar);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_purchase);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,layoutManager.getOrientation()));

       loadProduct.setOnClickListener(v -> {
           progressBar.setVisibility(View.VISIBLE);
           if(billingClient.isReady()){
               SkuDetailsParams params = SkuDetailsParams.newBuilder()
                       .setSkusList(Collections.singletonList("product_id"))
                       .setType(BillingClient.SkuType.SUBS)
                       .build();
               billingClient.querySkuDetailsAsync(params, (billingResult, list) -> {
                   if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                       loadProductToRececlerView(list);
                       progressBar.setVisibility(View.GONE);
                   }else {
                       progressBar.setVisibility(View.GONE);
                       Toast.makeText(SubscribeActivity.this,getResources().getString(R.string.repeatOne),Toast.LENGTH_SHORT).show();
                   }
               });
           }
       });
    }

    private void loadProductToRececlerView(List<SkuDetails> list) {
        MyProductAdapter adapter = new MyProductAdapter(this,list,billingClient);
        recyclerView.setAdapter(adapter);
    }

    private void setupBillingClient() {
        acknowledgePurchaseResponseListener = billingResult -> {
            if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                txtPremium.setVisibility(View.VISIBLE);
            }
        };
        billingClient = BillingClientSetup.getInstance(this, this);
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {
                Toast.makeText(SubscribeActivity.this,getResources().getString(R.string.errorConnect),Toast.LENGTH_SHORT).show();
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onBillingSetupFinished(@NonNull @NotNull BillingResult billingResult) {
                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                    Toast.makeText(SubscribeActivity.this,getResources().getString(R.string.succesConnect),Toast.LENGTH_SHORT).show();
                    List<Purchase> purchases = billingClient.queryPurchases(BillingClient.SkuType.SUBS).getPurchasesList();
                    assert purchases != null;
                    if(purchases.size() > 0){
                       recyclerView.setVisibility(View.VISIBLE);
                       for(Purchase purchase:purchases){
                           handleItemAlreadyPurchase(purchase);
                       }
                   }else {
                       txtPremium.setVisibility(View.GONE);
                       recyclerView.setVisibility(View.VISIBLE);
                       loadAllSubscribePackage();
                   }
                }else{
                    Toast.makeText(SubscribeActivity.this,getResources().getString(R.string.error),Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void loadAllSubscribePackage() {
        if(billingClient.isReady()){
            SkuDetailsParams params = SkuDetailsParams.newBuilder()
                    .setSkusList(Collections.singletonList("product_id"))
                    .setType(BillingClient.SkuType.SUBS)
                    .build();
            billingClient.querySkuDetailsAsync(params, new SkuDetailsResponseListener() {
                @Override
                public void onSkuDetailsResponse(@NonNull @NotNull BillingResult billingResult, @Nullable @org.jetbrains.annotations.Nullable List<SkuDetails> list) {
                    if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                        MyProductAdapter adapter = new MyProductAdapter(SubscribeActivity.this,list,billingClient);
                        recyclerView.setAdapter(adapter);
                    }else{
                        Toast.makeText(SubscribeActivity.this,getResources().getString(R.string.erroLoad),Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            Toast.makeText(SubscribeActivity.this,getResources().getString(R.string.noReadSubs),Toast.LENGTH_SHORT).show();

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("LongLogTag")
    private void handleItemAlreadyPurchase(Purchase purchase) {
       if(purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED){
           if(!purchase.isAcknowledged()){
               AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                       .setPurchaseToken(purchase.getPurchaseToken())
                       .build();
               billingClient.acknowledgePurchase(acknowledgePurchaseParams,acknowledgePurchaseResponseListener);
               UserViewModel userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
               User user = new User( new Date().getTime(),"noIsNotify","noSee");
               DataBase dataBase = DataBase.getDatabase(this.getApplication());
               List<User> userDatabase = dataBase.UserDao().getDate();
               if(userDatabase.size()>0){
                   for(User user1 : userDatabase){
                       int i = 0;
                       i++;
                       if(i==1){
                           if(user1!=null){
                               if(user1.getDate()!=null){
                                   userViewModel.updateNote(user,false);
                               }
                           }
                           else{
                               userViewModel.addDataForSubscribe(user,false);
                           }
                       }
                   }
               }else{
                   userViewModel.addDataForSubscribe(user,false);
               }
               Toast.makeText(SubscribeActivity.this,getResources().getString(R.string.subsIssued),Toast.LENGTH_SHORT).show();
               Intent j = new Intent(getApplicationContext(), MainActivity.class);
               startActivity(j);
               finish();
           }else {
               recyclerView.setVisibility(View.GONE);
               txtPremium.setVisibility(View.VISIBLE);
               txtPremium.setText(getResources().getString(R.string.subsIs));
               progressBar.setVisibility(View.GONE);
           }
       }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("LongLogTag")
    @Override
    public void onPurchasesUpdated(@NonNull @NotNull BillingResult billingResult, @Nullable @org.jetbrains.annotations.Nullable List<Purchase> list) {
        if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                && list != null){

          for(Purchase purchase: list){
              handleItemAlreadyPurchase(purchase);
          }
        }else if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED){
            Toast.makeText(SubscribeActivity.this,getResources().getString(R.string.canselInApp),Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(SubscribeActivity.this,getResources().getString(R.string.error),Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(SubscribeActivity.this, ActivityPremium.class);
            startActivity(intent);
            finish();
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}