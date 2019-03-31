package bidyourride.kurama.com.eapaka.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.time.LocalDate;

/**
 * Created by madhukurapati on 3/10/18.
 */

public class RequestedRidesTodayFragment extends RidesListFragment {
    String todaysDateForFirebase, tomorrowDateForFirebase;
    private static final String TAG = "RequestedRidesTodayFragment";


    public RequestedRidesTodayFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        todaysDateForFirebase = String.valueOf(LocalDate.now());
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        tomorrowDateForFirebase = tomorrow.toString();

        Query todaysRidesRequestQuery = databaseReference.child("rides-requested").startAt(todaysDateForFirebase).endAt(todaysDateForFirebase).orderByChild("dateOfRide").limitToFirst(100);

        return todaysRidesRequestQuery;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

}
