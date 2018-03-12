package bidyourride.kurama.com.bidyourride.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;

import java.util.ArrayList;
import java.util.List;

import bidyourride.kurama.com.bidyourride.R;
import bidyourride.kurama.com.bidyourride.model.DirectionObject;
import bidyourride.kurama.com.bidyourride.model.GsonRequest;
import bidyourride.kurama.com.bidyourride.model.LegsObject;
import bidyourride.kurama.com.bidyourride.model.PolylineObject;
import bidyourride.kurama.com.bidyourride.model.RideRequest;
import bidyourride.kurama.com.bidyourride.model.RouteObject;
import bidyourride.kurama.com.bidyourride.model.StepsObject;
import bidyourride.kurama.com.bidyourride.rest.Helper;
import bidyourride.kurama.com.bidyourride.rest.VolleySingleton;
import bidyourride.kurama.com.bidyourride.viewholder.PostViewHolder;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by madhukurapati on 11/20/17.
 */

public abstract class RequestedRidesTodayListFragment extends Fragment {

    private static final String TAG = "RequestedRidesTodayListFragment";
    private Handler mHandler;

    // [START define_database_reference]
    private DatabaseReference mDatabase;
    // [END define_database_reference]

    private FirebaseRecyclerAdapter<RideRequest, PostViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;

    public RequestedRidesTodayListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_all_rides_today, container, false);

        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END create_database_reference]

        mRecycler = rootView.findViewById(R.id.rides_messages_list);
        mRecycler.setHasFixedSize(true);

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        Query ridesQuery = getQuery(mDatabase);

        mAdapter = new FirebaseRecyclerAdapter<RideRequest, PostViewHolder>(RideRequest.class, R.layout.trip_details_card_view,
                PostViewHolder.class, ridesQuery) {
            SupportMapFragment mapFragment;
            PolylineOptions polylineOptions = new PolylineOptions();

            @Override
            public DatabaseReference getRef(int position) {
                return super.getRef(position);
            }

            @Override
            public long getItemId(int position) {
                return super.getItemId(position);
            }

            @Override
            public int getItemCount() {
                return super.getItemCount();
            }

            @Override
            public RideRequest getItem(int position) {
                return super.getItem(position);
            }

            @Override
            protected void populateViewHolder(final PostViewHolder viewHolder, final RideRequest model, final int position) {
                final DatabaseReference rideRef = getRef(position);
                // Set click listener for the whole post view
                final String rideKey = rideRef.getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });

                // Determine if the current user has liked this post and set UI accordingly
                if (model.stars.containsKey(getUid())) {
                    viewHolder.starView.setImageResource(R.drawable.ic_toggle_star_24);
                } else {
                    viewHolder.starView.setImageResource(R.drawable.ic_toggle_star_outline_24);
                }

                // Bind Ride to ViewHolder, setting OnClickListener for the star button
                viewHolder.bindToPost(getContext(), model, new View.OnClickListener() {
                    @Override
                    public void onClick(View starView) {
                        DatabaseReference globalPostRef = mDatabase.child("rides").child(rideRef.getKey());
                        DatabaseReference userPostRef = mDatabase.child("user-rides").child(model.uid).child(rideRef.getKey());
                        onStarClicked(globalPostRef);
                        onStarClicked(userPostRef);
                    }
                });

            }

            @Override
            public void onAttachedToRecyclerView(RecyclerView recyclerView) {
                super.onAttachedToRecyclerView(recyclerView);
            }

            @Override
            public boolean onFailedToRecycleView(PostViewHolder holder) {
                return super.onFailedToRecycleView(holder);
            }

            @Override
            public void onBindViewHolder(PostViewHolder viewHolder, int position) {
                /*
                App crashes when moved from last fragment to first fragment on enabling this
                Sideeffect is that 1st view is not populated with map on removing this
                if (mapFragment == null) {
                    mapFragment = SupportMapFragment.newInstance();
                }
                FragmentManager fragmentManager = getChildFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.map, mapFragment).commit();
                viewHolder.getMapFragmentAndCallback(getContext(), new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        LatLng latLng = new LatLng(42.0864745, -72.62116889999999);
                        googleMap.addMarker(new MarkerOptions().position(latLng));
                        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    }
                });*/
                super.onBindViewHolder(viewHolder, position);
            }


            @Override
            public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View inflatedView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.trip_details_card_view, parent, false);
                /*if(mapFragment == null) {
                    mapFragment = SupportMapFragment.newInstance();
                }
                mapFragment = SupportMapFragment.newInstance();
                FragmentManager fragmentManager = getChildFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.map, mapFragment).commit();
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                    }
                });*/

                return new PostViewHolder(inflatedView);
            }

            @Override
            public void onViewDetachedFromWindow(PostViewHolder holder) {
                super.onViewDetachedFromWindow(holder);
                //holder.removeMapFragment(getContext());
            }


            @Override
            public void onViewAttachedToWindow(final PostViewHolder holder) {
                super.onViewAttachedToWindow(holder);
                holder.getMapFragmentAndCallback(getContext(), new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        holder.getAdapterPosition();
                        Double originLat = Double.parseDouble(getItem(holder.getAdapterPosition()).getOriginLat());
                        Double originLong = Double.parseDouble(getItem(holder.getAdapterPosition()).getOriginLong());
                        Double destinationLat = Double.parseDouble(getItem(holder.getAdapterPosition()).getDestinationLat());
                        Double destinationLong = Double.parseDouble(getItem(holder.getAdapterPosition()).getDestinationLong());

                        addMarkersOfOriginDestination(googleMap, originLat, originLong, destinationLat, destinationLong);

                        String directionApiPath = Helper.getUrl(String.valueOf(originLat), String.valueOf(originLong),
                                String.valueOf(destinationLat), String.valueOf(destinationLong));
                        getDirectionFromDirectionApiServer(directionApiPath, googleMap);
                    }

                });
            }
        };
        mRecycler.setAdapter(mAdapter);

    }

    private void addMarkersOfOriginDestination(GoogleMap map, Double originLat, Double originLong, Double destinationLat, Double destinationLong) {
        map.addMarker(new MarkerOptions()
                .position(new LatLng(originLat, originLong))
                .title("Origin"));

        map.addMarker(new MarkerOptions()
                .position(new LatLng(destinationLat, destinationLong))
                .title("Destination"));
    }

    private void getDirectionFromDirectionApiServer(String url, GoogleMap googleMap) {
        GsonRequest<DirectionObject> serverRequest = new GsonRequest<DirectionObject>(
                Request.Method.GET,
                url,
                DirectionObject.class,
                createRequestSuccessListener(googleMap),
                createRequestErrorListener());
        serverRequest.setRetryPolicy(new DefaultRetryPolicy(
                Helper.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(serverRequest);
    }

    private Response.Listener<DirectionObject> createRequestSuccessListener(final GoogleMap googleMap) {
        return new Response.Listener<DirectionObject>() {
            @Override
            public void onResponse(DirectionObject response) {
                try {
                    Log.d("JSON Response", response.toString());
                    if (response.getStatus().equals("OK")) {
                        List<LatLng> mDirections = getDirectionPolylines(response.getRoutes());
                        drawRouteOnMap(googleMap, mDirections);
                    } else {
                        Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            ;
        };
    }

    private Response.ErrorListener createRequestErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        };
    }

    private void drawRouteOnMap(GoogleMap map, List<LatLng> positions) {
        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        options.addAll(positions);
        Polyline polyline = map.addPolyline(options);
        if(!getCameraPositionAndDrawMap(polyline, map)) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(positions.get(1).latitude, positions.get(1).longitude))
                .zoom(8)
                .build();
        if (!map.getUiSettings().isZoomControlsEnabled()) {
            map.getUiSettings().setZoomControlsEnabled(true);
        }
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    private Boolean getCameraPositionAndDrawMap(final Polyline polyline, GoogleMap map) {
        boolean isCameraPositionSet = false;
        boolean hasPoints = false;
        Double maxLat = null, minLat = null, minLon = null, maxLon = null;
        if (polyline != null && polyline.getPoints() != null) {
            List<LatLng> pts = polyline.getPoints();
            for (LatLng coordinate : pts) {
                // Find out the maximum and minimum latitudes & longitudes
                // Latitude
                maxLat = maxLat != null ? Math.max(coordinate.latitude, maxLat) : coordinate.latitude;
                minLat = minLat != null ? Math.min(coordinate.latitude, minLat) : coordinate.latitude;

                // Longitude
                maxLon = maxLon != null ? Math.max(coordinate.longitude, maxLon) : coordinate.longitude;
                minLon = minLon != null ? Math.min(coordinate.longitude, minLon) : coordinate.longitude;
                hasPoints = true;
            }
        }
        if (hasPoints) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(new LatLng(maxLat, maxLon));
            builder.include(new LatLng(minLat, minLon));
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 48));
            isCameraPositionSet = true;
        }
        return isCameraPositionSet;

    }

    private List<LatLng> getDirectionPolylines(List<RouteObject> routes) {
        List<LatLng> directionList = new ArrayList<LatLng>();
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


    // [START post_stars_transaction]
    private void onStarClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                RideRequest p = mutableData.getValue(RideRequest.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                if (p.stars.containsKey(getUid())) {
                    // Unstar the post and remove self from stars
                    p.starCount = p.starCount - 1;
                    p.stars.remove(getUid());
                } else {
                    // Star the post and add self to stars
                    p.starCount = p.starCount + 1;
                    p.stars.put(getUid(), true);
                }

                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }
    // [END post_stars_transaction]

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public abstract Query getQuery(DatabaseReference databaseReference);

}

