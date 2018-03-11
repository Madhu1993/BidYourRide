package bidyourride.kurama.com.bidyourride.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * Created by madhukurapati on 3/10/18.
 */

public class RequestedRidesTodayFragment extends RequestedRidesTodayListFragment{

    public RequestedRidesTodayFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {

        Query todaysRidesRequestQuery = databaseReference.child("rides")
                .limitToFirst(100);

        return todaysRidesRequestQuery;
    }
}
