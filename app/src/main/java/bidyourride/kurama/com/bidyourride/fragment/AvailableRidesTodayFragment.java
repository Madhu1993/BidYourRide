package bidyourride.kurama.com.bidyourride.fragment;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by madhukurapati on 3/16/18.
 */

public class AvailableRidesTodayFragment extends RidesListFragment {
    String todaysDateForFirebase,tomorrowDateForFirebase ;
    private static final String TAG = "AvailableRidesTodayFragment";

    public AvailableRidesTodayFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        todaysDateForFirebase = String.valueOf(LocalDate.now());

        LocalDate tomorrow = LocalDate.now().plusDays(1);
        tomorrowDateForFirebase = tomorrow.toString();


        Query todaysAvailableRidesRequestQuery = databaseReference.child("rides-provided").startAt(todaysDateForFirebase).endAt(todaysDateForFirebase).orderByChild("dateOfRide").limitToFirst(100);

        return todaysAvailableRidesRequestQuery;
    }
}
