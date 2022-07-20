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

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
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

import java.util.Arrays;
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

public class PurchaseItemActivity extends AppCompatActivity implements PurchasesUpdatedListener {

    BillingClient billingClient;
    ConsumeResponseListener listener;
    private ProgressBar progressBar;
    Button loadProduct;
    RecyclerView recyclerView;
    TextView txtPremium;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_item);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        setupBillingClient();
        init();
    }
    private void init(){
        txtPremium = (TextView) findViewById(R.id.txtPremium);
        loadProduct = (Button)  findViewById(R.id.load_product);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_purchase);
        progressBar = findViewById(R.id.progressBar);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,layoutManager.getOrientation()));

        loadProduct.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            if(billingClient.isReady()){
                SkuDetailsParams params = SkuDetailsParams.newBuilder()
                        .setSkusList(Arrays.asList("cont_id","content_id"))
                        .setType(BillingClient.SkuType.INAPP)
                        .build();
                billingClient.querySkuDetailsAsync(params, new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(@NonNull @NotNull
                        BillingResult billingResult, @Nullable @org.jetbrains.annotations.Nullable List<SkuDetails> list) {
                        if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                            loadProductToRececlerView(list);
                        }else {
                            Toast.makeText(PurchaseItemActivity.this,getResources().getString(R.string.repeatOne),Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }

    private void loadProductToRececlerView(List<SkuDetails> list) {
        MyProductAdapter adapter = new MyProductAdapter(this,list,billingClient);
        recyclerView.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
    }

    private void setupBillingClient() {
        listener = (billingResult, s) -> {
            if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                Toast.makeText(PurchaseItemActivity.this,getResources().getString(R.string.successly),Toast.LENGTH_SHORT).show();
            }
        };
        billingClient = BillingClientSetup.getInstance(this, this);
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {
                Toast.makeText(PurchaseItemActivity.this,getResources().getString(R.string.errorConnectServer),Toast.LENGTH_SHORT).show();
            }
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onBillingSetupFinished(@NonNull @NotNull BillingResult billingResult) {
                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                    Toast.makeText(PurchaseItemActivity.this,getResources().getString(R.string.successConnectServer),Toast.LENGTH_SHORT).show();
                    List<Purchase> purchases = billingClient.queryPurchases(BillingClient.SkuType.INAPP).getPurchasesList();
                    assert purchases != null;
                    handleItemAlreadyPurchase(purchases);
                }else{
                    Toast.makeText(PurchaseItemActivity.this,getResources().getString(R.string.error),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("LongLogTag")
    private void handleItemAlreadyPurchase(List<Purchase> purchases) {
        StringBuilder purchaseItem = new StringBuilder(txtPremium.getText());
        for(Purchase purchase: purchases){
            if(purchase.getSku().equals("content_id")){
                ConsumeParams consumeParams = ConsumeParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();
                billingClient.consumeAsync(consumeParams,listener);
                UserViewModel userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
                User user = new User( new Date().getTime(),"noIsNotify","noSee","isBuy");
                DataBase dataBase = DataBase.getDatabase(this.getApplication());
                List<User> userDatabase = dataBase.UserDao().getIsBuy();
                if(userDatabase.size()>0){
                    for(User user1 : userDatabase){
                        if(user1!=null){
                            if(user1.getDate()!=null){
                                userViewModel.updateNote(user,true);
                            }
                        }
                        else{
                            userViewModel.addDataForSubscribe(user,true);
                        }
                    }
                }else{
                    userViewModel.addDataForSubscribe(user,true);
                }
                Toast.makeText(PurchaseItemActivity.this,getResources().getString(R.string.productIsPurchase),Toast.LENGTH_SHORT).show();
                Intent j = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(j);
                finish();
            }
            purchaseItem.append("\n").append(purchase.isAcknowledged()).append("\n");
        }
        txtPremium.setText(purchaseItem.toString());
        txtPremium.setVisibility(View.VISIBLE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onPurchasesUpdated(@NonNull @NotNull BillingResult billingResult, @Nullable @org.jetbrains.annotations.Nullable List<Purchase> list) {
        if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
        && list != null){
            handleItemAlreadyPurchase(list);
        }else if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
            Toast.makeText(PurchaseItemActivity.this,getResources().getString(R.string.cansolePurchase),Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(PurchaseItemActivity.this,getResources().getString(R.string.error),Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
