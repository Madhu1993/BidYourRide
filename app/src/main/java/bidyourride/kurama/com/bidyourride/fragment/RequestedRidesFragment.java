package bidyourride.kurama.com.bidyourride.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bidyourride.kurama.com.bidyourride.R;

/**
 * Created by madhukurapati on 3/2/18.
 */

public class RequestedRidesFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.requested_rides, container, false);
    }
}