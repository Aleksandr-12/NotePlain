package com.writesimple.simplenote.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.writesimple.simplenote.DI.DaggerViewModelComponent;
import com.writesimple.simplenote.DI.ViewModelComponent;
import com.writesimple.simplenote.DI.ViewModelModule;
import com.writesimple.simplenote.R;
import com.writesimple.simplenote.FireBase.DataFirebase;
import com.writesimple.simplenote.model.ViewModel.UserViewModel;

import androidx.appcompat.app.ActionBar;

public class ActivitySettingsMain extends BaseActivity {
    String delete;
    String update;
    UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_settings);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getResources().getString(R.string.settings));
        ViewModelComponent component = DaggerViewModelComponent.builder().viewModelModule(new ViewModelModule(ActivitySettingsMain.this)).build();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        userViewModel = component.provideUserViewModel();
        Button save = findViewById(R.id.saveSettings);
        Button cleanFirebase = findViewById(R.id.cleanFirebase);
        CheckBox checkBoxDelete = findViewById(R.id.checkBoxDelete);
        CheckBox checkBoxUpdate = findViewById(R.id.checkBoxUpdate);


        if(getIntent().getStringExtra("updateMainFolder")!=null){
            if((getIntent().getStringExtra("updateMainFolder")).equals("update")){
                checkBoxUpdate.setChecked(false);
            }
            if(getIntent().getStringExtra("updateMainFolder").equals("noUpdate")){
                checkBoxUpdate.setChecked(true);
            }
        }

        if(getIntent().getStringExtra("deleteMainFolder")!=null){
            if((getIntent().getStringExtra("deleteMainFolder")).equals("delete")){
                checkBoxDelete.setChecked(false);
            }
            if(getIntent().getStringExtra("deleteMainFolder").equals("noDelete")){
                checkBoxDelete.setChecked(true);
            }
        }

        checkBoxDelete.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked)  {
                delete = "noDelete";
            } else {
                delete = "delete";
            }
        });
        checkBoxUpdate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)  {
                    update = "noUpdate";
                } else {
                    update = "update";
                }
            }
        });
        save.setOnClickListener(view -> {
            if(userViewModel.getBooleanDateSubs()){
                Intent intent = new Intent();
                if(!checkBoxDelete.isChecked()){
                    delete = "delete";
                }else{
                    delete = "noDelete";
                }
                if(!checkBoxUpdate.isChecked()){
                    update = "update";
                }else{
                    update = "noUpdate";
                }

                intent.putExtra("updateMainFolder", update);
                intent.putExtra("deleteMainFolder", delete);

                setResult(RESULT_OK, intent);
                finish();
            }else{
                Intent intent = new Intent(ActivitySettingsMain.this, ActivityPremium.class);
                startActivity(intent);
                finish();
            }
        });


        cleanFirebase.setOnClickListener(view -> {
            AlertDialog.Builder alertDialog;
            if(currentUser!=null && isOnline() && userViewModel.getBooleanDateSubs()){
                alertDialog = new AlertDialog.Builder(ActivitySettingsMain.this);
                alertDialog.setPositiveButton(getResources().getString(R.string.yes), (dialog, which) -> {
                    DataFirebase dataFirebase = new DataFirebase();
                    dataFirebase.getMyRef().removeValue();
                    dialog.dismiss();
                });
                alertDialog.setNegativeButton(getResources().getString(R.string.no), (dialog, which) -> dialog.dismiss());
                alertDialog.setTitle(getResources().getString(R.string.isDeleteCloudOnlyNote));
                alertDialog.show();
            }else {
                alertDialog = new AlertDialog.Builder(ActivitySettingsMain.this);
                alertDialog.setPositiveButton(getResources().getString(R.string.ok), (dialog, which) -> dialog.dismiss());
                alertDialog.setTitle(getResources().getString(R.string.dontAccessCloud));
                alertDialog.show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(ActivitySettingsMain.this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
