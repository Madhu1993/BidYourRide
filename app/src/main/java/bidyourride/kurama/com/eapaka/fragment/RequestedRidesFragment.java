package bidyourride.kurama.com.eapaka.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bidyourride.kurama.com.eapaka.R;

/**
 * Created by madhukurapati on 3/2/18.
 */

public class RequestedRidesFragment extends Fragment {

    private FragmentPagerAdapter mPagerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.fragement_requested_rides, container, false);

        mPagerAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {
            private final Fragment[] mFragments = new Fragment[]{new RequestedRidesTodayFragment(),
                    new RequestedRideUpcomingFragment()
            };
            private final String[] mFragmentNames = new String[]{getString(R.string.today),
                    getString(R.string.upcoming)
            };

            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }

            @Override
            public int getCount() {
                return mFragments.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mFragmentNames[position];
            }
        };

        ViewPager pager = result.findViewById(R.id.pager);
        pager.setAdapter(mPagerAdapter);
        TabLayout tabLayout = result.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(pager);

        return (result);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}