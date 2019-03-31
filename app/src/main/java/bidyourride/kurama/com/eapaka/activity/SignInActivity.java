package bidyourride.kurama.com.eapaka.activity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import bidyourride.kurama.com.eapaka.R;
import bidyourride.kurama.com.eapaka.model.User;

import static org.apache.commons.lang3.text.WordUtils.capitalizeFully;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 200;

    private LoginButton fbSignIn;
    private CallbackManager callbackManager;
    private GoogleApiClient mGoogleApiClient;
    private SignInButton googleSignIn;
    private EditText mEmailField;
    private EditText mPasswordField;
    private Button mSignInButton;
    private Button mRegisterButton;
    private FirebaseUser mFirebaseUser;

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabase;
    private LottieAnimationView animationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_sign_in);

        setStatusBarColor();

        mEmailField = findViewById(R.id.field_email);
        mPasswordField = findViewById(R.id.field_password);
        mSignInButton = findViewById(R.id.button_sign_in);
        mRegisterButton = findViewById(R.id.button_sign_up);

        googleSignIn = findViewById(R.id.google_sign_in_button);
        googleSignIn.setOnClickListener(this);

        //lotte animationView
        animationView = findViewById(R.id.animation_view);

        // Click listeners
        mSignInButton.setOnClickListener(this);
        mRegisterButton.setOnClickListener(this);
        setGooglePlusButtonText(googleSignIn, "Sign In With Google");

        fbSignIn = findViewById(R.id.fb_sign_in_button);
        callbackManager = CallbackManager.Factory.create();
        fbSignIn.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Initialize FirebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        fbSignIn.setReadPermissions("email", "public_profile");
        fbSignIn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                //startCheckAnimation();
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

    public void setStatusBarColor() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.BLACK);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.google_sign_in_button:
                signInWithGoogle();
                break;
            case R.id.button_sign_in:
                signIn();
                break;
            case R.id.button_sign_up:
                signUp();
                break;
            default:
                return;
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void signIn() {
        Log.d(TAG, "signIn");
        if (!validateForm()) {
            return;
        }

        //startCheckAnimation();
        String email = mEmailField.getText().toString().trim();
        String password = mPasswordField.getText().toString();
        Log.d(TAG, "Before listener call" );

        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());

                        if (task.isSuccessful()) {
                            onAuthEmailAndPasswordSignInSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(SignInActivity.this, "Sign In Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void startCheckAnimation() {
        hideAllButtonAndTextViews();
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 0.8f).setDuration(700);
        animationView.setAnimation("progress_bar.json");
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                animationView.setProgress((Float) valueAnimator.getAnimatedValue());
            }
        });

        if (animationView.getProgress() == 0f) {
            animator.start();
        } else {
            animationView.setProgress(0f);
        }
    }

    private void hideAllButtonAndTextViews() {
        mEmailField.setVisibility(View.GONE);
        mPasswordField.setVisibility(View.GONE);
        mRegisterButton.setVisibility(View.GONE);
        mSignInButton.setVisibility(View.GONE);
        googleSignIn.setVisibility(View.GONE);
        fbSignIn.setVisibility(View.GONE);
    }

    private void onAuthEmailAndPasswordSignInSuccess(FirebaseUser user) {
        String username = usernameFromEmail(user.getEmail());

        // Write new user
        writeNewUser(user.getUid(), username, user.getEmail());

        // Go to MainActivity
        startActivity(new Intent(SignInActivity.this, MainActivity.class));
        finish();
    }

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    private void signUp() {
        Log.d(TAG, "signUp");
        if (!validateForm()) {
            return;
        }

        //startCheckAnimation();
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUser:onComplete:" + task.isSuccessful());
                        Log.d(TAG, "createUser:onComplete:" + task.getException());

                        if (task.isSuccessful()) {
                            onAuthEmailAndPasswordSignInSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(SignInActivity.this, "Sign Up Failed",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(mEmailField.getText().toString()) ) {
            mEmailField.setError("Required");
            result = false;
        } else {
            mEmailField.setError(null);
        }

        if (TextUtils.isEmpty(mPasswordField.getText().toString())) {
            mPasswordField.setError("Required");
            result = false;
        } else {
            mPasswordField.setError(null);
        }

        return result;
    }

    private void signInWithGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                //startCheckAnimation();
            } else {
                // Google Sign In failed
                Log.e(TAG, "Google Sign In failed.");
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            onAuthSuccess(task.getResult().getUser());
                            Log.d(TAG, "onComplete: " + task.getResult().getUser());
                        }
                    }
                });
    }

    public void onAuthSuccess(FirebaseUser user) {
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        String userName = "";
        String emailId = "";
        if (mFirebaseUser != null) {
            for (UserInfo profile : mFirebaseUser.getProviderData()) {
                if (profile.getProviderId().equals(getString(R.string.facebook_provider_id))) {
                    userName = profile.getDisplayName();
                    emailId = profile.getEmail();
                    Log.d(TAG, "onAuthSuccess: ");
                } else {
                    userName = mFirebaseUser.getDisplayName();
                    emailId = mFirebaseUser.getEmail();
                    // Write new user
                }
            }
        }
        writeNewUser(user.getUid(), userName, emailId);
        Log.d(TAG, "onComplete: " + user);
        Intent launchMainActivity = new Intent(SignInActivity.this, MainActivity.class);
        launchMainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        launchMainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(launchMainActivity);
        finish();
    }

    private void writeNewUser(String uid, String username, String emailId) {
        String afterEditingUsername = capitalizeFully(username);;
        User user = new User(afterEditingUsername, emailId);
        mDatabase.child("users").child(uid).setValue(user);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            //startCheckAnimation();
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            onAuthSuccess(task.getResult().getUser());
                        }

                    }
                });
    }

    protected void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                return;
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}
