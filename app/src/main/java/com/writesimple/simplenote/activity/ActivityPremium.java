package com.writesimple.simplenote.activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import com.writesimple.simplenote.R;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class ActivityPremium extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.premium_layout);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getResources().getString(R.string.premium));
        Button subscribe = findViewById(R.id.subscribe);
        Button reloadInApp = findViewById(R.id.reloadInApp);
        Button inApp = findViewById(R.id.inApp);
        Button reloadSubs = findViewById(R.id.reloadSubs);
        inApp.setOnClickListener(view -> {
            Intent i = new Intent(getApplicationContext(), PurchaseItemActivity.class);
            startActivity(i);
        });

        subscribe.setOnClickListener(view -> {
            Intent i = new Intent(getApplicationContext(), SubscribeActivity.class);
            startActivity(i);
        });
        reloadSubs.setOnClickListener(view -> {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.putExtra("pullFromReloadSubs","true");
            startActivity(i);
        });
        reloadInApp.setOnClickListener(view -> {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.putExtra("pullFromReloadInApp","true");
            startActivity(i);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(ActivityPremium.this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
