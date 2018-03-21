package bidyourride.kurama.com.bidyourride.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import bidyourride.kurama.com.bidyourride.R;
import bidyourride.kurama.com.bidyourride.adapter.CommentAdapter;
import bidyourride.kurama.com.bidyourride.helper.GoogleMapHelper;
import bidyourride.kurama.com.bidyourride.helper.ObjectWrapperForBinder;
import bidyourride.kurama.com.bidyourride.model.Comment;
import bidyourride.kurama.com.bidyourride.model.DirectionObject;
import bidyourride.kurama.com.bidyourride.model.Distance;
import bidyourride.kurama.com.bidyourride.model.Duration;
import bidyourride.kurama.com.bidyourride.model.LegsObject;
import bidyourride.kurama.com.bidyourride.model.PolylineObject;
import bidyourride.kurama.com.bidyourride.model.RideRequest;
import bidyourride.kurama.com.bidyourride.model.RouteObject;
import bidyourride.kurama.com.bidyourride.model.StepsObject;
import bidyourride.kurama.com.bidyourride.model.User;

/**
 * Created by madhukurapati on 3/17/18.
 */

public class RideDetailsActivity extends BaseActivity implements OnMapReadyCallback, View.OnClickListener {
    private static final String TAG = "PostViewHolder";
    public static final String EXTRA_RIDE_KEY = "ride_key";
    private String mRideKey;
    private String typeOfReference;
    private GoogleMap map;
    private MapView mapView;
    private DatabaseReference mRideReference, mCommentsReference;
    RideRequest rideRequestObject;
    private Double originLat, originLong, destinationLat, destinationLong;
    private Gson gson;
    private List<LatLng> listLatLong;
    private TextView dateView;
    private TextView timeView;
    private TextView distanceView;
    private TextView durationView;
    Polyline polyline;
    PolylineOptions options;
    private EditText mCommentField;
    private Button mCommentButton;
    private RecyclerView mCommentsRecycler;
    private CommentAdapter mAdapter;

    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_ride_details);
        super.onCreate(savedInstanceState);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView = findViewById(R.id.activity_map);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        dateView = findViewById(R.id.date_tv);
        timeView = findViewById(R.id.time_tv);
        distanceView = findViewById(R.id.distace_tv);
        durationView = findViewById(R.id.duration_tv);

        rideRequestObject = ((ObjectWrapperForBinder) getIntent().getExtras().getBinder("rideObject")).getData();

        if (rideRequestObject == null) {
            return;
        }

        mRideKey = getIntent().getStringExtra(EXTRA_RIDE_KEY);
        if (mRideKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_RIDE_KEY");
        }

        /*typeOfReference = rideRequestObject.getTypeOfRequest();
        if (typeOfReference.equals("1")) {
            mRideReference = FirebaseDatabase.getInstance().getReference()
                    .child("rides-requested").child(mRideKey);
        } else {
            mRideReference = FirebaseDatabase.getInstance().getReference().child("rides-provided").child(mRideKey);
        }*/

        mCommentsReference = FirebaseDatabase.getInstance().getReference()
                .child("rides-comments").child(mRideKey);

        gson = new Gson();
        String originLatS = rideRequestObject.getOriginLat();
        String originLongS = rideRequestObject.getOriginLong();
        String destinationLatS = rideRequestObject.getDestinationLat();
        String destinationLongS = rideRequestObject.getDestinationLong();

        originLat = Double.parseDouble(originLatS);
        originLong = Double.parseDouble(originLongS);
        destinationLat = Double.parseDouble(destinationLatS);
        destinationLong = Double.parseDouble(destinationLongS);

        dateView.setText(rideRequestObject.dateOfRide);
        timeView.setText(rideRequestObject.timeOfRide);

        mCommentField = findViewById(R.id.ride_field_comment_text);
        mCommentButton = findViewById(R.id.ride_button_comment);
        mCommentsRecycler = findViewById(R.id.recycler_comments);
        mCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");
                if (v != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                Log.d(TAG, "onClick: after");
                postComment();
            }

        });
        //mCommentButton.setOnClickListener(this);
        mCommentsRecycler.setLayoutManager(new LinearLayoutManager(this));

        // Listen for comments
        mAdapter = new CommentAdapter(this, mCommentsReference);
        mCommentsRecycler.setAdapter(mAdapter);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        MapsInitializer.initialize(getApplicationContext());
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        setMapLocation();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    private void postComment() {
        final String uid = getUid();
        FirebaseDatabase.getInstance().getReference().child("users").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user information
                        User user = dataSnapshot.getValue(User.class);
                        String authorName = user.username;

                        // Create new comment object
                        String commentText = mCommentField.getText().toString();
                        Comment comment = new Comment(uid, authorName, commentText);

                        // Push the comment, it will appear in the list
                        mCommentsReference.push().setValue(comment);

                        // Clear the field
                        mCommentField.setText(null);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: ");
        int i = v.getId();
        if (i == R.id.ride_button_comment) {
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
            Log.d(TAG, "onClick: after");
            postComment();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
        //Hide keyboard on start
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
        mAdapter.cleanupListener();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private void setMapLocation() {
        String positions = rideRequestObject.getmDirections();
        DirectionObject directionObject = gson.fromJson(positions, DirectionObject.class);
        List<RouteObject> routeObject = directionObject.getRoutes();
        populateDistanceAndTimeFields(directionObject);
        GetDirectionsFromPositions getDirectionsFromPositions = new GetDirectionsFromPositions(routeObject);
        getDirectionsFromPositions.execute();
    }

    private void populateDistanceAndTimeFields(DirectionObject directionObject) {
        List<LegsObject> legsObjects = directionObject.getRoutes().get(0).getLegs();
        Distance distance = legsObjects.get(0).getDistance();
        Duration duration = legsObjects.get(0).getDuration();
        distanceView.setText(distance.getText());
        String avgTimeText = "Travel duration: " + duration.getText();
        durationView.setText(avgTimeText);
    }

    private void callAddMarkers(List<LatLng> listLatLong) {
        if (map == null) return;
        if (originLat == null | originLong == null) {
            return;
        }

        if (listLatLong == null) {
            LatLng latLng = new LatLng(originLat, originLong);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f));
            map.addMarker(new MarkerOptions().position(latLng));
        } else {
            addMarkersAndCameraAngle(listLatLong);
        }
    }

    private void addMarkersAndCameraAngle(List<LatLng> listLatLong) {
        options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        options.addAll(listLatLong);
        polyline = map.addPolyline(options);
        if (polyline.getPoints() != null) {
            GoogleMapHelper.addMarkersOfOriginDestination(map, originLat, originLong, destinationLat, destinationLong);
            GoogleMapHelper.drawRouteOnMap(polyline, map, listLatLong);
        }
    }


    public class GetDirectionsFromPositions extends AsyncTask<Void, Void, List<LatLng>> {
        List<LatLng> directionList;
        List<RouteObject> routeObjectList;

        public GetDirectionsFromPositions(List<RouteObject> routeObjectList) {
            this.routeObjectList = routeObjectList;
        }

        private List<LatLng> decodePoly(String encoded) {
            List<LatLng> poly = new ArrayList<>();
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;
            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;
                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;
                LatLng p = new LatLng((((double) lat / 1E5)),
                        (((double) lng / 1E5)));
                poly.add(p);
            }
            return poly;
        }

        private List<LatLng> getDirectionPolylines(List<RouteObject> routes) {
            directionList = new ArrayList<LatLng>();
            for (RouteObject route : routes) {
                List<LegsObject> legs = route.getLegs();
                for (LegsObject leg : legs) {
                    List<StepsObject> steps = leg.getSteps();
                    for (StepsObject step : steps) {
                        PolylineObject polyline = step.getPolyline();
                        String points = polyline.getPoints();
                        List<LatLng> singlePolyline = decodePoly(points);
                        directionList.addAll(singlePolyline);
                    }
                }
            }
            return directionList;
        }

        @Override
        protected List<LatLng> doInBackground(Void... voids) {
            directionList = getDirectionPolylines(routeObjectList);
            return directionList;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            Toast.makeText(getApplicationContext(), "LOADING", Toast.LENGTH_SHORT).show();
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(List<LatLng> latLngs) {
            listLatLong = latLngs;
            callAddMarkers(latLngs);
        }
    }
}
