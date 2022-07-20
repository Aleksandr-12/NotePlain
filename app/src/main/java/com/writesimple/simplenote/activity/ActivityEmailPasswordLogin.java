package com.writesimple.simplenote.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.writesimple.simplenote.R;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

public class ActivityEmailPasswordLogin extends AppCompatActivity {
    private AppCompatEditText email,password;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_password_login);

        Button registerBtn = findViewById(R.id.register);
        final Button loginBtn = findViewById(R.id.login);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);
        firebaseAuth = FirebaseAuth.getInstance();
        registerBtn.setOnClickListener(v -> startActivity(new Intent(ActivityEmailPasswordLogin.this, ActivityRegister.class)));

        loginBtn.setOnClickListener(v -> {
            String tex_email = Objects.requireNonNull(email.getText()).toString();
            String tex_password = Objects.requireNonNull(password.getText()).toString();
            if (TextUtils.isEmpty(tex_email) || TextUtils.isEmpty(tex_password)){
                Toast.makeText(ActivityEmailPasswordLogin.this, "All Fields Required", Toast.LENGTH_SHORT).show();
            }
            else{
                login(tex_email,tex_password);
            }
        });
    }

    private void login(String tex_email, String tex_password) {
        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.signInWithEmailAndPassword(tex_email,tex_password).addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);
            if (task.isSuccessful()){
                Intent intent = new Intent(ActivityEmailPasswordLogin.this,MainActivity.class);
                intent.putExtra("auth", "simple_auth");
                intent.putExtra("update", "update");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

            }else{
                Toast.makeText(ActivityEmailPasswordLogin.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}