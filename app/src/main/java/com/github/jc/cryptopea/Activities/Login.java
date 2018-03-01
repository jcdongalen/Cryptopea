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

import com.github.jc.cryptopea.R;
import com.github.jc.cryptopea.Utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_SIGN_IN = 123;

    private FirebaseAuth mAuth;

    private Button btnSignIn;
    private EditText etUserName, etPassword;
    private TextView tvForgotPassword;

    private Constants mConstants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mConstants = new Constants(this);
        mAuth = FirebaseAuth.getInstance();

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
                                        FirebaseUser user = mAuth.getCurrentUser();
//                                        if (user.isEmailVerified()) {
                                            Intent intent = new Intent(Login.this, MainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
//                                        } else {
//                                            mConstants.showLongSnackbar("Please verify your account.");
//                                        }
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
