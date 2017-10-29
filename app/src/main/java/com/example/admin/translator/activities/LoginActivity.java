package com.example.admin.translator.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.transition.ChangeBounds;
import android.support.transition.Scene;
import android.support.transition.Transition;
import android.support.transition.TransitionManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.admin.translator.Constants;
import com.example.admin.translator.R;
import com.example.admin.translator.models.User;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by DELL on 31/03/2017.
 */

public class LoginActivity extends AppCompatActivity {

    Boolean backPressFlag = false;
    SharedPreferences pref;
    String email,password;

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressText;
    private View mProgressView;
    private View mLoginFormView;
    private Button mEmailSignInButton;
    private Button register;
    GoogleApiClient googleApiClient;
    SignInButton googleButton;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private ProgressDialog progressDialog;

    private static final int RC_SIGN_IN = 9001;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
                final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null){
                    showProgressDialog(getString(R.string.retrieving_user_data));
                    databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid());
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            if (user != null) {
                                hideProgressDialog();
                                Toast.makeText(LoginActivity.this,"Welcome " + user.Name,Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                SharedPreferences.Editor editor = getSharedPreferences(Constants.PREFS,MODE_PRIVATE).edit();
                                editor.putBoolean(Constants.LOGGED_IN,true);
                                editor.putString(Constants.FIREBASE_ID,firebaseUser.getUid());
                                editor.apply();
                                finish();
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }
        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(LoginActivity.this)
                .enableAutoManage(LoginActivity.this, new GoogleApiClient.OnConnectionFailedListener(){
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(LoginActivity.this,"onConnectionFailed "+ connectionResult,Toast.LENGTH_LONG).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ViewGroup sceneRoot = findViewById(R.id.sceneroot);
                Scene endScene = Scene.getSceneForLayout(sceneRoot, R.layout.activity_login, LoginActivity.this);
                Transition transition = new ChangeBounds().setDuration(750);
                TransitionManager.go(endScene, transition);

                mEmailView = findViewById(R.id.email);
                mEmailView.setDropDownBackgroundResource(android.R.color.white);
                mPasswordView = findViewById(R.id.password);
                mEmailSignInButton = findViewById(R.id.email_sign_in_button);
                register = findViewById(R.id.email_register_button);
                mLoginFormView = findViewById(R.id.login_form);
                mProgressView = findViewById(R.id.login_progress);
                mProgressText = findViewById(R.id.progress_text);
                googleButton = findViewById(R.id.googleButton);

                mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        email = mEmailView.getText().toString();
                        password = mPasswordView.getText().toString();
                        if(isValid()){
                            showProgressDialog(getString(R.string.logging));
                            login(email,password);
                        }
                    }
                });

                register.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                        finish();
                    }
                });

                googleButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                        startActivityForResult(signInIntent,RC_SIGN_IN);
                    }
                });

            }
        }, 750);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()){
                GoogleSignInAccount account = result.getSignInAccount();
                showProgressDialog(getString(R.string.logging));
                firebaseAuthWithGoogle(account);
            }
            else{
                Toast.makeText(getApplicationContext(),R.string.google_signin_failed,Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account){
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideProgressDialog();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        SharedPreferences.Editor editor = getSharedPreferences(Constants.PREFS,MODE_PRIVATE).edit();
                        editor.putBoolean(Constants.LOGGED_IN,true);
                        editor.putString(Constants.FIREBASE_ID,user.getUid());
                        editor.apply();
                        finish();
                    }
                }).addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this,R.string.error,Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean isValid(){
        if(!isEmailValid(email)){
            mEmailView.setError(getString(R.string.email_error));
            mEmailView.setFocusable(true);
            return false;
        }
        else if(password.isEmpty() || password.length() < 8){
            mPasswordView.setError(getString(R.string.email_error));
            mPasswordView.setFocusable(true);
            return false;
        }
        return true;
    }

    public void login(String email,String password){
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideProgressDialog();
                        if (!task.isSuccessful()) {
                            try {
                                String msg = task.getResult().toString();
                                Toast.makeText(getApplicationContext(), "Unsuccessful" + msg, Toast.LENGTH_SHORT).show();
                            }
                            catch(Exception e){e.printStackTrace();}
                        }
                    }
                });
    }

    private boolean isEmailValid(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth =FirebaseAuth.getInstance();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    public void onBackPressed() {
        if (backPressFlag)
            finishAffinity();
        else {
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            backPressFlag = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    backPressFlag = false;
                }
            }, 2000);
        }
    }

    private void showProgressDialog(String message){
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    private void hideProgressDialog(){
        progressDialog.dismiss();
    }
}
