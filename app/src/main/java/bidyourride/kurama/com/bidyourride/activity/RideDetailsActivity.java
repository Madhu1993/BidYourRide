package bidyourride.kurama.com.bidyourride.activity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import bidyourride.kurama.com.bidyourride.R;
import bidyourride.kurama.com.bidyourride.helper.GoogleMapHelper;
import bidyourride.kurama.com.bidyourride.helper.ObjectWrapperForBinder;
import bidyourride.kurama.com.bidyourride.model.DirectionObject;
import bidyourride.kurama.com.bidyourride.model.LegsObject;
import bidyourride.kurama.com.bidyourride.model.PolylineObject;
import bidyourride.kurama.com.bidyourride.model.RideRequest;
import bidyourride.kurama.com.bidyourride.model.RouteObject;
import bidyourride.kurama.com.bidyourride.model.StepsObject;

/**
 * Created by madhukurapati on 3/17/18.
 */

public class RideDetailsActivity extends BaseActivity implements OnMapReadyCallback {
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
    Polyline polyline;
    PolylineOptions options;

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

        rideRequestObject = ((ObjectWrapperForBinder) getIntent().getExtras().getBinder("rideObject")).getData();

        if (rideRequestObject == null) {
            return;
        }

        mRideKey = getIntent().getStringExtra(EXTRA_RIDE_KEY);
        if (mRideKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_RIDE_KEY");
        }

        typeOfReference = rideRequestObject.getTypeOfRequest();
        if (typeOfReference.equals("1")) {
            mRideReference = FirebaseDatabase.getInstance().getReference()
                    .child("rides-requested").child(mRideKey);
        } else {
            mRideReference = FirebaseDatabase.getInstance().getReference().child("rides-provided").child(mRideKey);
        }

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
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
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
        GetDirectionsFromPositions getDirectionsFromPositions = new GetDirectionsFromPositions(routeObject);
        getDirectionsFromPositions.execute();
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
