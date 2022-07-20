package com.writesimple.simplenote.activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.writesimple.simplenote.R;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityAuthenticate extends AppCompatActivity {

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticate);
        Objects.requireNonNull(getSupportActionBar()).hide();
     /*   ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.enterAuth));*/

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
       //FirebaseAuth.getInstance().signOut();

        TextView enterText = findViewById(R.id.enterText);

        enterText.setText(Html.fromHtml("Продолжая, вы соглашаетесь <a style=\" color: #fff\" href=\"https://useragreement.refsite.ru\" ><b>Условия Использования</b></a> и <a href=\"https://confidentiality.refsite.ru\"><b>Политика Конфиденциальности</b></a>", null, null));
        enterText.setLinksClickable(true);
        enterText.setMovementMethod(LinkMovementMethod.getInstance());
        CharSequence text = enterText.getText();
        if (text instanceof Spannable)
        {
            enterText.setText(reformatText(text));
        }

        SignInButton sign_in_button = findViewById(R.id.sign_in_button);
        sign_in_button.setOnClickListener(view -> signIn());

        View email_password_login_button = findViewById(R.id.email_assword_login_button);
        View simple_enter = findViewById(R.id.simple_enter);

        email_password_login_button.setOnClickListener(view -> {
            Intent intent = new Intent(ActivityAuthenticate.this, ActivityEmailPasswordLogin.class);
            startActivity(intent);
       });
        simple_enter.setOnClickListener(view -> {
            Intent intent = new Intent(ActivityAuthenticate.this,MainActivity.class);
            intent.putExtra("auth", "simple_auth");
            startActivity(intent);
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
           /* {
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    Toast.makeText(ActivityAuthenticate.this, "Успешно", Toast.LENGTH_SHORT).show();
                    firebaseAuthWithGoogle(account.getIdToken());
                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
                    Log.w(TAG, "Google sign in failed", e);
                    Toast.makeText(ActivityAuthenticate.this, "Ошибка", Toast.LENGTH_SHORT).show();
                }
            }else {
                Log.w(TAG, "Google sign in failed", e);
                Toast.makeText(ActivityAuthenticate.this, R.string.authError, Toast.LENGTH_SHORT).show();
            }*/
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Toast.makeText(ActivityAuthenticate.this, R.string.successHandleGoogle, Toast.LENGTH_SHORT).show();
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(ActivityAuthenticate.this, R.string.errorHandleGoogle, Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Google sign in failed", e);
            }
        }

    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);
                        Toast.makeText(ActivityAuthenticate.this, R.string.successSingInGoogle, Toast.LENGTH_SHORT).show();
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(ActivityAuthenticate.this, R.string.errorSingInGoogle, Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart(); }

    public static class CustomerTextClick extends ClickableSpan {
            String mUrl;

            public CustomerTextClick(String url) {
                mUrl = url;
            }
            @Override
            public void onClick(View widget) {
                //Тут можно как-то обработать нажатие на ссылку
                //Сейчас же мы просто открываем браузер с ней
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(mUrl));
                widget.getContext().startActivity(i);
            }
        }
        public SpannableStringBuilder reformatText(CharSequence text) {
            int end = text.length();
            Spannable sp = (Spannable) text;
            URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
            SpannableStringBuilder style = new SpannableStringBuilder(text);
            for (URLSpan url : urls) {
                style.removeSpan(url);
                CustomerTextClick click = new CustomerTextClick(url.getURL());
                style.setSpan(click, sp.getSpanStart(url), sp.getSpanEnd(url),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            return style;
        }
}
