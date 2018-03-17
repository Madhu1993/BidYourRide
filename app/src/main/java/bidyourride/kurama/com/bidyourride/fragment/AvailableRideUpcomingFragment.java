package bidyourride.kurama.com.bidyourride.fragment;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.time.LocalDate;

/**
 * Created by madhukurapati on 3/16/18.
 */

public class AvailableRideUpcomingFragment extends RidesListFragment {
    String todaysDateForFirebase, tomorrowDateForFirebase, tenDaysFromNowForFirebase;
    private static final String TAG = "AvailableRideUpcomingFragment";

    public AvailableRideUpcomingFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        todaysDateForFirebase = String.valueOf(LocalDate.now());

        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDate tenDaysFromNow = LocalDate.now().plusDays(10);

        tomorrowDateForFirebase = tomorrow.toString();
        tenDaysFromNowForFirebase = tenDaysFromNow.toString();
        Log.d(TAG, "getQuery: "+ todaysDateForFirebase);

        Query upComingAvailableRidesRequestQuery = databaseReference.child("rides-provided").startAt(tomorrowDateForFirebase).endAt(tenDaysFromNowForFirebase).orderByChild("dateOfRide").limitToFirst(100);

        return upComingAvailableRidesRequestQuery;
    }
}
