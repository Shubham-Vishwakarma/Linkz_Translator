package com.example.admin.translator.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.admin.translator.Constants;
import com.example.admin.translator.R;
import com.example.admin.translator.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by DELL on 31/03/2017.
 */

public class SignUpActivity extends AppCompatActivity{
    private static final String TAG = "SignUpActivity";
    EditText emailEdiText ,passwordEditText,nameEditText;
    Button signupButton;//loginButton,;
    private FirebaseAuth firebaseAuth;
    private  FirebaseAuth.AuthStateListener authStateListener;
    private String name,email,password;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;

    //Button signout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailEdiText = findViewById(R.id.email_editText);
        passwordEditText = findViewById(R.id.password_editText);
        signupButton= findViewById(R.id.signinButton);
        nameEditText = findViewById(R.id.name_editText);

        firebaseAuth =FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    Toast.makeText(SignUpActivity.this,"Welcome " + name,Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = getSharedPreferences(Constants.PREFS,MODE_PRIVATE).edit();
                    editor.putBoolean(Constants.LOGGED_IN,true);
                    editor.putString(Constants.FIREBASE_ID,user.getUid());
                    editor.apply();
                    startActivity(new Intent(SignUpActivity.this,MainActivity.class));
                    finish();
                }
            }
        };

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = nameEditText.getText().toString();
                email = emailEdiText.getText().toString();
                password = passwordEditText.getText().toString();
                if(isValid()){
                    showProgressDialog();
                    signup(email,password);
                }
            }
        });

    }

    public void signup(String email,String password){
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideProgressDialog();
                        if (!task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this,R.string.error,Toast.LENGTH_SHORT).show();
                        }
                        else{
                            uploadData();
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(authStateListener != null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    private boolean isValid(){
        if(name.isEmpty()){
            nameEditText.setError(getString(R.string.name_error));
            nameEditText.setFocusable(true);
            return false;
        }
        else if(!isValidEmail(email)){
            emailEdiText.setError(getString(R.string.email_error));
            emailEdiText.setFocusable(true);
            return false;
        }
        else if(password.isEmpty() || password.length()<8){
            passwordEditText.setError(getString(R.string.password_error));
            passwordEditText.setFocusable(true);
            return  false;
        }
        return true;
    }

    public boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void showProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.registering));
        progressDialog.show();
    }

    private void hideProgressDialog(){
        progressDialog.dismiss();
    }

    public void uploadData()
    {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null){
            Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
            String userId = user.getUid();
            writeNewUser(userId,name,email);
        }
        else
            Log.d(TAG, "onAuthStateChanged:signed_out");
    }

    public void writeNewUser(String userId,String Name,String Email)
    {
        User user = new User(Name,Email);
        databaseReference.child("users").child(userId).setValue(user);
    }
}
