package com.github.jc.cryptopea.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.transition.Slide;
import android.support.transition.TransitionInflater;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.github.jc.cryptopea.Models.ProfileDetails;
import com.github.jc.cryptopea.R;
import com.github.jc.cryptopea.Utils.Constants;
import com.github.jc.cryptopea.Utils.Loading;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_SIGN_IN = 123;

    private static final String TABLE_USERS = "users";
    private static final String TAG = "LOGIN";

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase mDatabase;
    private DatabaseReference usersRef;
    private CallbackManager mCallbackManager;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions mGoogleSignInOptions;
    private Dialog mProgressDialog;

    private LoginButton loginButton;
    private Button btnSignIn;
    private ImageButton btnFacebookSignIn, btnTwitterSignIn, btnGithubSignIn;
    private SignInButton btnGoogleSignIn;
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
        mCallbackManager = CallbackManager.Factory.create();
        usersRef = mDatabase.getReference(TABLE_USERS);
        profileDetails = ProfileDetails.getInstance();
        mProgressDialog = Loading.getLoadingDialog(this);

        mGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions);

        //initializations
        etUserName = findViewById(R.id.etUserName);
        etPassword = findViewById(R.id.etPassword);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignin);
        btnFacebookSignIn = findViewById(R.id.btnFacebookSignIn);
        btnTwitterSignIn = findViewById(R.id.btnTwitterSignIn);
        btnGithubSignIn = findViewById(R.id.btnGithubSignIn);

        btnSignIn.setOnClickListener(this);
        btnGoogleSignIn.setOnClickListener(this);
        btnFacebookSignIn.setOnClickListener(this);
        btnTwitterSignIn.setOnClickListener(this);
        btnGithubSignIn.setOnClickListener(this);

        initializeFacebookLogin();
    }

    @Override
    protected void onStart() {
        currentUser = mAuth.getCurrentUser();
        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                mConstants.showLongSnackbar("Google sign in failed. Exception: " + e.getMessage());
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSignIn:
                mProgressDialog.show();
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
                                        updateProfileDetails();
                                    } else {
                                        mProgressDialog.dismiss();
                                        mConstants.showLongSnackbar("Invalid credentials. Please try again");
                                        etPassword.setText("");
                                    }
                                }
                            });
                }
                break;
            case R.id.btnGoogleSignin:
                mProgressDialog.show();
                googleSignIn();
                break;
            case R.id.btnFacebookSignIn:
                mProgressDialog.show();
                loginButton.performClick();
                break;
            case R.id.btnTwitterSignIn:
                break;
            case R.id.btnGithubSignIn:
                mConstants.showLongSnackbar("Sorry, this is still under maintenance");
                break;
        }
    }

    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            currentUser = mAuth.getCurrentUser();
                            updateProfileDetails();
                        } else {
                            mProgressDialog.dismiss();
                            mConstants.showLongSnackbar("Authentication failed.");
                        }
                    }
                });
    }

    private void updateProfileDetails() {
        if (profileDetails != null) {
            if (profileDetails.getUser_id().equals(currentUser.getUid())) {
                mConstants.showLongSnackbar("Same as current user.");
            } else {
                profileDetails.setUser_id(currentUser.getUid());
                profileDetails.setEmail(currentUser.getEmail());
                profileDetails.setDisplay_name(currentUser.getDisplayName());
                profileDetails.setEmail(currentUser.getEmail());
                profileDetails.setPhoto_url(String.valueOf(currentUser.getPhotoUrl()));
                usersRef.child(currentUser.getUid()).setValue(profileDetails);

                Intent intent = new Intent(Login.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        } else {
            mConstants.showLongSnackbar("Profile details is not yet initialized");
        }
        mProgressDialog.dismiss();
    }

    private void initializeFacebookLogin() {
        mCallbackManager = CallbackManager.Factory.create();
        loginButton = new LoginButton(this);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // ...
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken accessToken) {
        Log.d(TAG, "handleFacebookAccessToken:" + accessToken);

        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            currentUser = mAuth.getCurrentUser();
                            updateProfileDetails();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Constants.showSnackMessage(Login.this, "Facebook sign in failed.", false);
                            currentUser = null;
                            updateProfileDetails();
                        }
                    }
                });
    }
}
