package bidyourride.kurama.com.bidyourride.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import bidyourride.kurama.com.bidyourride.R;
import bidyourride.kurama.com.bidyourride.adapter.ViewPagerAdapter;
import bidyourride.kurama.com.bidyourride.fragment.RequestedRidesFragment;
import bidyourride.kurama.com.bidyourride.fragment.AvailableRidesFragment;

public class MainActivity extends BaseActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    public static String POSITION = "POSITION";
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser == null){
            Intent launchSignInActivity = new Intent(MainActivity.this, SignInActivity.class);
            launchSignInActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            launchSignInActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(launchSignInActivity);
        }

        int[] icons = {
                R.drawable.ic_directions_car_black_48dp,
                //fake center fragment, so that it creates place for raised center tab.
                //R.drawable.add_story,

                R.drawable.ic_location_on_black_48dp,
        };
        tabLayout = findViewById(R.id.tab_layout);
        viewPager =  findViewById(R.id.main_tab_content);

        setupViewPager(viewPager);


        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < icons.length; i++) {
            tabLayout.getTabAt(i).setIcon(icons[i]);
        }
        tabLayout.getTabAt(0).select();

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFrag(new RequestedRidesFragment(), "Requests");

        //fake center fragment, so that it creates place for raised center tab.
        //adapter.addFrag(new AvailableRidesFragment(), "Sample ");

        adapter.addFrag(new AvailableRidesFragment(), "Available");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.menu_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_search:
                Intent settingsIntent = new Intent(this, AddRideActivity.class);
                startActivity(settingsIntent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(POSITION,tabLayout .getSelectedTabPosition());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        viewPager.setCurrentItem(savedInstanceState.getInt(POSITION));
    }
}
