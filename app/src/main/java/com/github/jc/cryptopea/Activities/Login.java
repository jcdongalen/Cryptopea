package com.github.jc.cryptopea.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.jc.cryptopea.Models.ProfileDetails;
import com.github.jc.cryptopea.R;
import com.github.jc.cryptopea.Utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.Arrays;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_SIGN_IN = 123;

    private static final String TABLE_USERS = "users";

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase mDatabase;
    private DatabaseReference usersRef;

    private Button btnSignIn;
    private EditText etUserName, etPassword;
    private TextView tvForgotPassword;

    private Gson gson;
    private Constants mConstants;
    private ProfileDetails profileDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        gson = new Gson();
        mConstants = new Constants(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        usersRef = mDatabase.getReference(TABLE_USERS);
        profileDetails = ProfileDetails.getInstance();

        //initializations
        etUserName = findViewById(R.id.etUserName);
        etPassword = findViewById(R.id.etPassword);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        btnSignIn = findViewById(R.id.btnSignIn);

        btnSignIn.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSignIn:
                String username = etUserName.getText().toString();
                String password = etPassword.getText().toString();
                if (!username.isEmpty() && !password.isEmpty()) {
                    mConstants.hideSoftKeyboard(this.getCurrentFocus());
                    mAuth.signInWithEmailAndPassword(username, password)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        currentUser = mAuth.getCurrentUser();
                                        profileDetails.setUser_id(currentUser.getUid());
                                        profileDetails.setEmail(currentUser.getEmail());
                                        usersRef.child(currentUser.getUid()).setValue(profileDetails);
                                        if (currentUser.isEmailVerified()) {
                                            Intent intent = new Intent(Login.this, MainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            mConstants.showLongSnackbar("Please verify your account.");
                                        }
                                    } else {
                                        mConstants.showLongSnackbar("Invalid credentials. Please try again");
                                        etPassword.setText("");
                                    }
                                }
                            });
                }
                break;
        }
    }
}
