package com.writesimple.simplenote.activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.writesimple.simplenote.R;
import java.util.Objects;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.AppCompatEditText;

public class ActivityRegister extends BaseActivity {

    private AppCompatEditText userName,emailAddress,password;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getResources().getString(R.string.register));

        firebaseAuth = FirebaseAuth.getInstance();

        userName = findViewById(R.id.username);
        emailAddress = findViewById(R.id.email);
        password = findViewById(R.id.password);
        Button registerBtn;
        registerBtn = findViewById(R.id.register);
        progressBar = findViewById(R.id.progressBar);
        registerBtn.setOnClickListener(v -> {
            final String user_name = Objects.requireNonNull(userName.getText()).toString();
            final String email = Objects.requireNonNull(emailAddress.getText()).toString();
            final String txt_password = Objects.requireNonNull(password.getText()).toString();
            if(TextUtils.isEmpty(user_name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(txt_password)){
                Toast.makeText(ActivityRegister.this, "All fields are required", Toast.LENGTH_SHORT).show();
            }else{
                register(user_name,email,txt_password);
            }
        });
    }

    private void register(final String user_name, final String email, String txt_password) {
        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.createUserWithEmailAndPassword(email,txt_password).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                FirebaseUser rUser = firebaseAuth.getCurrentUser();
                assert rUser != null;
                rUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Intent intent = new Intent(ActivityRegister.this,MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }else{
                            Toast.makeText(ActivityRegister.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }else{
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ActivityRegister.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
