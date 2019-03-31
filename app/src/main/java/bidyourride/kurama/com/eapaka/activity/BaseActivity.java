package bidyourride.kurama.com.eapaka.activity;

/**
 * Created by madhukurapati on 2/21/18.
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import bidyourride.kurama.com.eapaka.R;

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;
    private MenuItem mAuthenticateMenuItem;
    private ImageView mHeaderLogo;
    private TextView mHeaderTitle;
    private TextView mHeaderSubTitle;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private GoogleApiClient mGoogleApiClient;
    private String mPhotoURL ;
    static boolean isInitialized = false;
    private static String TAG = "BaseActivity";


    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // R.id.drawer_layout should be in every activity with exactly the same id.
        drawerLayout = findViewById(R.id.drawer_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        setStatusBarColor();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        try{
            if(!isInitialized){
                FirebaseDatabase.getInstance().setPersistenceEnabled(true);
                isInitialized = true;
            }else {
                Log.d(TAG,"Already Initialized");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(R.string.app_name);
                int size = navigationView.getMenu().size();
                for (int i = 0; i < size; i++) {
                    navigationView.getMenu().getItem(i).setChecked(false);
                }
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(R.string.profile);
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        drawerLayout.addDrawerListener(drawerToggle);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View hView = navigationView.getHeaderView(0);
        hView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                drawerLayout.closeDrawer(GravityCompat.START);
//                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));

            }
        });

        Menu menu = navigationView.getMenu();
        mAuthenticateMenuItem = menu.findItem(R.id.logout);

        mHeaderLogo = hView.findViewById(R.id.profile_logo);
        mHeaderTitle = hView.findViewById(R.id.profile_name);
        mHeaderSubTitle = hView.findViewById(R.id.profile_email);
    }

    public void setStatusBarColor() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.BLACK);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        populateNavigationView();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                drawerLayout.closeDrawer(GravityCompat.START);
                Intent mainActivity = new Intent(BaseActivity.this, MainActivity.class);
                mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mainActivity);
                return true;
            case R.id.referToAFriend:
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.settings:
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.logout:
                drawerLayout.closeDrawer(GravityCompat.START);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.app_name);
                builder.setMessage(R.string.confirm_logout);
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            navigationView.getMenu().findItem(R.id.logout).setChecked(false);
                            AsyncTask.execute(new Runnable() {
                                                  @Override
                                                  public void run() {
                                                      firebaseAuth.signOut();
                                                      LoginManager.getInstance().logOut();
                                                      //Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                                                  }
                                              }
                            );
                        } catch (Exception e) {

                        }
                        firebaseUser = null;
                        Intent launchSingInActivity = new Intent(BaseActivity.this, SignInActivity.class);
                        launchSingInActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        launchSingInActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(launchSingInActivity);
                    }
                });
                builder.setNegativeButton(android.R.string.no, null);
                builder.show();
                return true;
        }
        return true;
    }

    private void populateNavigationView() {
        if (firebaseUser == null) {

        } else {
            String facebookUserId ;
            String userName ;
            String emailId ;
            for (UserInfo profile : firebaseUser.getProviderData()) {
                if (profile.getProviderId().equals(getString(R.string.facebook_provider_id))) {
                    facebookUserId = profile.getUid();
                    userName = profile.getDisplayName();
                    emailId = profile.getEmail();
                    mPhotoURL = "https://graph.facebook.com/" + facebookUserId + "/picture?type=large";
                } else {
                    if (firebaseUser.getPhotoUrl() != null) {
                        mPhotoURL = firebaseUser.getPhotoUrl().toString();
                        userName = firebaseUser.getDisplayName();
                        emailId = firebaseUser.getEmail();
                    } else {
                        mPhotoURL = "www.google.com/image/1";
                        userName = usernameFromEmail(firebaseUser.getEmail());
                        emailId = firebaseUser.getEmail();
                    }
                }
                Picasso.with(getApplicationContext()).load(mPhotoURL).into(mHeaderLogo);
                mHeaderTitle.setText(userName);
                mHeaderSubTitle.setText(emailId);
            }
        }
    }

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

}
