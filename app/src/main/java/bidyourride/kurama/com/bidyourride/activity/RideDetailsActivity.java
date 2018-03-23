package bidyourride.kurama.com.bidyourride.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.diegodobelo.expandingview.ExpandingItem;
import com.diegodobelo.expandingview.ExpandingList;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.lyft.lyftbutton.LyftButton;
import com.lyft.lyftbutton.RideParams;
import com.lyft.lyftbutton.RideTypeEnum;
import com.lyft.networking.ApiConfig;
import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.android.rides.RideParameters;
import com.uber.sdk.android.rides.RideRequestButton;
import com.uber.sdk.android.rides.RideRequestButtonCallback;
import com.uber.sdk.rides.client.ServerTokenSession;
import com.uber.sdk.rides.client.SessionConfiguration;
import com.uber.sdk.rides.client.error.ApiError;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import bidyourride.kurama.com.bidyourride.R;
import bidyourride.kurama.com.bidyourride.fragment.CommentFragment;
import bidyourride.kurama.com.bidyourride.helper.GoogleMapHelper;
import bidyourride.kurama.com.bidyourride.helper.ObjectWrapperForBinder;
import bidyourride.kurama.com.bidyourride.model.DirectionObject;
import bidyourride.kurama.com.bidyourride.model.Distance;
import bidyourride.kurama.com.bidyourride.model.Duration;
import bidyourride.kurama.com.bidyourride.model.LegsObject;
import bidyourride.kurama.com.bidyourride.model.PolylineObject;
import bidyourride.kurama.com.bidyourride.model.RequestBid;
import bidyourride.kurama.com.bidyourride.model.RideRequest;
import bidyourride.kurama.com.bidyourride.model.RouteObject;
import bidyourride.kurama.com.bidyourride.model.StepsObject;
import bidyourride.kurama.com.bidyourride.model.User;
import bidyourride.kurama.com.bidyourride.view.CircleImageView;
import bidyourride.kurama.com.bidyourride.viewholder.RideRequestBidViewHolder;

/**
 * Created by madhukurapati on 3/17/18.
 */

public class RideDetailsActivity extends BaseActivity implements OnMapReadyCallback, View.OnClickListener, View.OnTouchListener {
    private static final String TAG = "RideDetailsActivity";
    public static final String EXTRA_RIDE_KEY = "ride_key";
    private static final String UBER_CLIENT_ID = "uL0KuF3x2SmbSwjUuFHeHHs0R2oPBL4J";
    private static final String UBER_SERVER_TOKEN = "-OB_NAjPpMEhKtGfTZrFQaRQYlPgJNCPZseSll4a";
    private static final String LYFT_CLIENT_ID = "QctvgJmtka9e";
    private static final String LYFT_SERVER_TOKEN = "S+ARfeuLjNcoinNBML28M56YbX13Q43uCXJoS9QKpsGTlMyG10l6/C3YLNEj+Fgf7eEU++Zx4yZQ7YrZnDrig101J8sIe2RUq232EDpgk/y0JW0YpE6TRYc=";
    private String mRideKey;
    private String typeOfReference;
    FirebaseDatabase firebaseDatabase;
    private DatabaseReference mBidValueReference;
    private GoogleMap map;
    private MapView mapView;
    RideRequest rideRequestObject;
    private Double originLat, originLong, destinationLat, destinationLong;
    private Gson gson;
    //private List<LatLng> listLatLong;
    private TextView dateView;
    private TextView timeView;
    private TextView distanceView;
    private TextView durationView;
    Polyline polyline;
    PolylineOptions options;
    private Button mCommentButton;
    private TextView originLocation;
    private TextView destinationLocation;
    private TextView priceView;
    private ImageButton decButton;
    private ImageButton incButton;
    private Button bidButton;
    private CircleImageView originImage, destinationImage, dateImage, timeImage, distanceImage, durationImage;
    private boolean mAutoIncrement = false;
    private boolean mAutoDecrement = false;
    private Handler repeatUpdateHandler = new Handler();
    public double mValue;
    public double minValueAllowed;
    CommentFragment commentFragment;
    private ChildEventListener mChildEventListener;
    private RideRequestBid mBidAdapter;

    public static final int REP_DELAY = 50;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private RecyclerView mBidValueRecycler;
    ExpandingList expandingList, expandingListRecyclerBidValues;
    LyftButton lyftButton;
    RideRequestButton uberRideRequestButton;
    ExpandingItem priceItem, bidValueItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_ride_details);
        super.onCreate(savedInstanceState);

        commentFragment = new CommentFragment();

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView = findViewById(R.id.activity_map);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        firebaseDatabase = FirebaseDatabase.getInstance();

        rideRequestObject = ((ObjectWrapperForBinder) getIntent().getExtras().getBinder("rideObject")).getData();

        if (rideRequestObject == null) {
            return;
        }

        mRideKey = getIntent().getStringExtra(EXTRA_RIDE_KEY);
        if (mRideKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_RIDE_KEY");
        }

        mBidValueReference = firebaseDatabase.getReference()
                .child("rides-requestBid").child(mRideKey);

        dateView = findViewById(R.id.date_tv);
        timeView = findViewById(R.id.time_tv);
        distanceView = findViewById(R.id.distace_tv);
        durationView = findViewById(R.id.duration_tv);
        priceView = findViewById(R.id.price_tv);
        incButton = findViewById(R.id.inc_button);
        decButton = findViewById(R.id.dec_button);
        mCommentButton = findViewById(R.id.comment_title_tv);

        bidButton = findViewById(R.id.bid_button);
        expandingList = findViewById(R.id.expanding_list_main);
        expandingListRecyclerBidValues = findViewById(R.id.expanding_list_recylcer_bid_value);
        priceItem = expandingList.createNewItem(R.layout.expanding_lyft_uber_layout);
        priceItem.createSubItems(1);
        setPriceValuesExpandImage();
        priceItem.setStateChangedListener(new ExpandingItem.OnItemStateChanged() {
            @Override
            public void itemCollapseStateChanged(boolean expanded) {
                if (expanded) {
                    setPriceValuesCollapseImage();
                } else {
                    setPriceValuesExpandImage();
                }
            }
        });
        View subPriceItemZero = priceItem.getSubItemView(0);
        lyftButton = subPriceItemZero.findViewById(R.id.lyft_button);
        uberRideRequestButton = subPriceItemZero.findViewById(R.id.uber_button);

        bidValueItem = expandingListRecyclerBidValues.createNewItem(R.layout.expanding_top_bids_layout);
        bidValueItem.createSubItems(1);
        setBidValuesExpandImage();

        bidValueItem.setStateChangedListener(new ExpandingItem.OnItemStateChanged() {
            @Override
            public void itemCollapseStateChanged(boolean expanded) {
                if (expanded) {
                    setBidValuesCollapseImage();
                } else {
                    setBidValuesExpandImage();
                }
            }
        });

        View subBidValueItemZero = bidValueItem.getSubItemView(0);
        mBidValueRecycler = subBidValueItemZero.findViewById(R.id.recylcer_bid_value);

        if (mValue == 0) {
            decButton.setVisibility(View.GONE);
        }

        gson = new Gson();
        String originLatS = rideRequestObject.getOriginLat();
        String originLongS = rideRequestObject.getOriginLong();
        String destinationLatS = rideRequestObject.getDestinationLat();
        String destinationLongS = rideRequestObject.getDestinationLong();

        originLat = Double.parseDouble(originLatS);
        originLong = Double.parseDouble(originLongS);
        destinationLat = Double.parseDouble(destinationLatS);
        destinationLong = Double.parseDouble(destinationLongS);
        handleUberButtonAndGetDetails();
        handleLyftButtonAndGetDeails();

        dateView.setText(rideRequestObject.dateOfRide);
        timeView.setText(rideRequestObject.timeOfRide);

        originImage = findViewById(R.id.origin_image);
        originImage.getLayoutParams().height = 60;
        originImage.getLayoutParams().width = 60;
        originImage.requestLayout();
        gson = new Gson();

        dateImage = findViewById(R.id.date_image);
        dateImage.getLayoutParams().height = 60;
        dateImage.getLayoutParams().width = 60;
        dateImage.requestLayout();

        timeImage = findViewById(R.id.time_image);
        timeImage.getLayoutParams().height = 60;
        timeImage.getLayoutParams().width = 60;
        timeImage.requestLayout();

        destinationImage = findViewById(R.id.destination_image);
        destinationImage.getLayoutParams().height = 60;
        destinationImage.getLayoutParams().width = 60;
        destinationImage.requestLayout();

        distanceImage = findViewById(R.id.distance_image);
        distanceImage.getLayoutParams().height = 60;
        distanceImage.getLayoutParams().width = 60;
        distanceImage.requestLayout();

        durationImage = findViewById(R.id.duration_image);
        durationImage.getLayoutParams().height = 60;
        durationImage.getLayoutParams().width = 60;
        durationImage.requestLayout();

        originLocation = findViewById(R.id.origin_location);
        destinationLocation = findViewById(R.id.destination_location);

        originLocation.setText(rideRequestObject.getOrigin());
        destinationLocation.setText(rideRequestObject.getDestination());

        incButton.setOnLongClickListener(
                new View.OnLongClickListener() {
                    public boolean onLongClick(View arg0) {
                        mAutoIncrement = true;
                        repeatUpdateHandler.post(new RptUpdater());
                        return false;
                    }
                }
        );

        decButton.setOnLongClickListener(
                new View.OnLongClickListener() {
                    public boolean onLongClick(View arg0) {
                        mAutoDecrement = true;
                        repeatUpdateHandler.post(new RptUpdater());
                        return false;
                    }
                }
        );

        incButton.setOnTouchListener(this);
        decButton.setOnTouchListener(this);
        incButton.setOnClickListener(this);
        decButton.setOnClickListener(this);
        mCommentButton.setOnClickListener(this);
        bidButton.setOnClickListener(this);
    }

    private void setBidValuesExpandImage() {
        bidValueItem.setIndicatorIconRes(R.drawable.ic_arrow_drop_down_black_48dp);
    }

    private void setBidValuesCollapseImage() {
        bidValueItem.setIndicatorIconRes(R.drawable.ic_arrow_drop_up_black_48dp);
    }

    private void setPriceValuesExpandImage() {
        priceItem.setIndicatorIconRes(R.drawable.ic_arrow_drop_down_black_48dp);
    }

    private void setPriceValuesCollapseImage() {
        priceItem.setIndicatorIconRes(R.drawable.ic_arrow_drop_up_black_48dp);
    }

    private void handleLyftButtonAndGetDeails() {
        ApiConfig apiConfig = new ApiConfig.Builder()
                .setClientId(LYFT_CLIENT_ID)
                .setClientToken(LYFT_SERVER_TOKEN)
                .build();

        /*lyftButton = findViewById(R.id.lyft_button);*/
        lyftButton.setApiConfig(apiConfig);

        RideParams.Builder rideParamsBuilder = new RideParams.Builder()
                .setPickupLocation(originLat, originLong)
                .setDropoffLocation(destinationLat, destinationLong);
        rideParamsBuilder.setRideTypeEnum(RideTypeEnum.CLASSIC);

        lyftButton.setRideParams(rideParamsBuilder.build());
        lyftButton.load();
    }

    private void handleUberButtonAndGetDetails() {
        RideParameters rideParams = new RideParameters.Builder()
                .setDropoffLocation(
                        destinationLat, destinationLong, rideRequestObject.getDestinationCityName(), rideRequestObject.getDestination())
                .setPickupLocation(originLat, originLong, rideRequestObject.getOriginCityName(), rideRequestObject.getOrigin())
                .build();

        SessionConfiguration config = new SessionConfiguration.Builder()
                .setClientId(UBER_CLIENT_ID)
                .setServerToken(UBER_SERVER_TOKEN)
                .setEnvironment(SessionConfiguration.Environment.SANDBOX)
                .build();

        UberSdk.initialize(config);
        ServerTokenSession session = new ServerTokenSession(config);
        RideRequestButtonCallback callback = new RideRequestButtonCallback() {

            @Override
            public void onRideInformationLoaded() {

            }

            @Override
            public void onError(ApiError apiError) {

            }

            @Override
            public void onError(Throwable throwable) {

            }
        };
        uberRideRequestButton.setRideParameters(rideParams);
        uberRideRequestButton.setSession(session);
        uberRideRequestButton.setCallback(callback);
        uberRideRequestButton.loadRideInformation();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        populateMaXBidAmountViewAndListenForChildValueChanges();
        //getMaxBiddedValueSoFar();
    }

    /*private void getMaxBiddedValueSoFar() {
        Query minValueToSetForPriceBidView = mBidValueReference.orderByChild("bidValue").limitToLast(1);
        minValueToSetForPriceBidView.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                    RequestBid requestBid = childSnapshot.getValue(RequestBid.class);
                    if (requestBid != null) {
                        minValueAllowed = requestBid.bidValue;
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException(); // don't swallow errors
            }
        });
    }*/

    private void populateMaXBidAmountViewAndListenForChildValueChanges() {
        // Listen for bidValues

        mBidAdapter = new RideRequestBid(this, mBidValueReference);
        mBidValueRecycler.setAdapter(mBidAdapter);
    }

    private class RideRequestBid extends RecyclerView.Adapter<RideRequestBidViewHolder> {

        private static final String TAG = "RideDetailsActivity";
        private Context mContext;
        private DatabaseReference mDatabaseReference;
        private ChildEventListener mChildEventListener;

        private List<String> mRequestBidIDs = new ArrayList<>();
        private List<RequestBid> requestBids = new ArrayList<>();


        RideRequestBid(Context context, DatabaseReference ref) {
            mContext = context;
            mDatabaseReference = ref;

            // Create child event listener
            // [START child_event_listener_recycler]
            ChildEventListener childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
                    // A new comment has been added, add it to the displayed list
                    RequestBid requestBid = dataSnapshot.getValue(RequestBid.class);

                    // [START_EXCLUDE]
                    // Update RecyclerView
                    mRequestBidIDs.add(dataSnapshot.getKey());
                    requestBids.add(requestBid);
                    notifyItemInserted(mRequestBidIDs.size() - 1);
                    // [END_EXCLUDE]

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so displayed the changed comment.
                    RequestBid requestBid = dataSnapshot.getValue(RequestBid.class);
                    String requestKey = dataSnapshot.getKey();

                    // [START_EXCLUDE]
                    int RequestBidIndex = mRequestBidIDs.indexOf(requestKey);
                    if (RequestBidIndex > -1) {
                        // Replace with the new data
                        requestBids.set(RequestBidIndex, requestBid);

                        // Update the RecyclerView
                        notifyItemChanged(RequestBidIndex);
                    } else {
                    }
                    // [END_EXCLUDE]
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so remove it.
                    String requestKey = dataSnapshot.getKey();

                    // [START_EXCLUDE]
                    int RequestBidIndex = mRequestBidIDs.indexOf(requestKey);
                    if (RequestBidIndex > -1) {
                        // Remove data from the list
                        mRequestBidIDs.remove(RequestBidIndex);
                        requestBids.remove(RequestBidIndex);

                        // Update the RecyclerView
                        notifyItemRemoved(RequestBidIndex);
                    } else {
                        Log.w(TAG, "onChildRemoved:unknown_child:" + requestKey);
                    }
                    // [END_EXCLUDE]
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            };
            ref.orderByChild("bidValue").limitToFirst(5).addChildEventListener(childEventListener);
            // [END child_event_listener_recycler]

            // Store reference to listener so it can be removed on app stop
            mChildEventListener = childEventListener;
        }

        ;

        @Override
        public RideRequestBidViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.item_bid_value, parent, false);
            return new RideRequestBidViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RideRequestBidViewHolder holder, int position) {
            RequestBid requestBid = requestBids.get(position);
            holder.authorView.setText(requestBid.author);
            String bidString = "$ " + requestBid.bidValue;
            holder.bidValue.setText(bidString);
            holder.dateAndTime.setText(requestBid.dateAndTime);
        }

        @Override
        public int getItemCount() {
            return requestBids.size();
        }

        public void cleanupListener() {
            if (mChildEventListener != null) {
                if (mDatabaseReference != null) {
                    mDatabaseReference.removeEventListener(mChildEventListener);
                }
            }
        }

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

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i) {
            case R.id.inc_button:
                increment();
                return;
            case R.id.dec_button:
                decrement();
                return;
            case R.id.comment_title_tv:
                Intent intent = new Intent(this, CommentsActivity.class);
                intent.putExtra("mRideKey", mRideKey);
                intent.putExtra("uid", getUid());
                startActivity(intent);
            case R.id.bid_button:
                showBidValueAndSaveOnFirebase();

        }
    }

    private void showBidValueAndSaveOnFirebase() {
        final String uid = getUid();
        firebaseDatabase.getReference().child("users").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user information
                        User user = dataSnapshot.getValue(User.class);
                        String authorName = user.username;

                        String todaysDateAndTimeForBidValue = String.valueOf(LocalTime.now());

                        // Create new comment object
                        double bidValue = mValue;
                        RequestBid requestBid = new RequestBid(uid, authorName, todaysDateAndTimeForBidValue, bidValue);
                        // Push the comment, it will appear in the list
                        mBidValueReference.push().setValue(requestBid);
                        String setPrice = "$ " + requestBid.bidValue;
                        // Clear the field
                        priceView.setText(setPrice);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


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
        mBidAdapter.cleanupListener();
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
        durationView.setText(duration.getText());
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

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if ((event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
                && mAutoIncrement) {
            mAutoIncrement = false;
        }

        if ((event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
                && mAutoDecrement) {
            mAutoDecrement = false;
        }
        return false;
    }

    class RptUpdater implements Runnable {
        public void run() {
            if (mAutoIncrement) {
                increment();
                repeatUpdateHandler.postDelayed(new RptUpdater(), REP_DELAY);
            } else if (mAutoDecrement) {
                decrement();
                repeatUpdateHandler.postDelayed(new RptUpdater(), REP_DELAY);
            }
        }
    }

    public void decrement() {
        if (mValue == 0) {
            decButton.setVisibility(View.GONE);
        } else {
            mValue = mValue - 0.5;
            if (mValue == 0) {
                decButton.setVisibility(View.GONE);
            }
            String value = "$ " + String.valueOf(mValue);
            priceView.setText(value);
        }
    }

    public void increment() {
        if (decButton.getVisibility() != View.VISIBLE) {
            decButton.setVisibility(View.VISIBLE);
        }

        mValue = mValue + 0.5;
        String value = "$ " + String.valueOf(mValue);
        priceView.setText(value);
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
            //listLatLong = latLngs;
            callAddMarkers(latLngs);
        }
    }
}
