package bidyourride.kurama.com.bidyourride.fragment;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.time.LocalDate;

/**
 * Created by madhukurapati on 3/16/18.
 */

public class RequestedRideUpcomingFragment extends RidesListFragment {
    String todaysDateForFirebase, tomorrowDateForFirebase, tenDaysFromNowForFirebase;
    private static final String TAG = "RequestedRidesTodayFragment";

    public RequestedRideUpcomingFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        todaysDateForFirebase = String.valueOf(LocalDate.now());

        LocalDate tomorrow = LocalDate.now().plusDays(1);
        tomorrowDateForFirebase = tomorrow.toString();
        LocalDate tenDaysFromNow = LocalDate.now().plusDays(10);

        tenDaysFromNowForFirebase = tenDaysFromNow.toString();

        Query upcomingRidesRequestQuery = databaseReference.child("rides-requested").startAt(tomorrowDateForFirebase).endAt(tenDaysFromNowForFirebase).orderByChild("dateOfRide").limitToFirst(100);


        return upcomingRidesRequestQuery;
    }
}
