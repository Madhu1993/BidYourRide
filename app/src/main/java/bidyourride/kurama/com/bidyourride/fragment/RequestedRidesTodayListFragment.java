package bidyourride.kurama.com.bidyourride.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;

import java.util.List;

import bidyourride.kurama.com.bidyourride.R;
import bidyourride.kurama.com.bidyourride.model.Location;
import bidyourride.kurama.com.bidyourride.model.RideRequest;
import bidyourride.kurama.com.bidyourride.viewholder.PostViewHolder;

/**
 * Created by madhukurapati on 11/20/17.
 */

public abstract class RequestedRidesTodayListFragment extends Fragment {

    private static final String TAG = "RequestedRidesTodayListFragment";

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
                viewHolder.bindToPost(getContext(),model, new View.OnClickListener() {
                    @Override
                    public void onClick(View starView) {
                        // Need to write to both places the post is stored
                        DatabaseReference globalPostRef = mDatabase.child("rides").child(rideRef.getKey());
                        DatabaseReference userPostRef = mDatabase.child("user-rides").child(model.uid).child(rideRef.getKey());

                        // Run two transactions
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

            /*@Override
            public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return super.onCreateViewHolder(parent, viewType);
            }*/

            @Override
            public void onViewDetachedFromWindow(PostViewHolder holder) {
                super.onViewDetachedFromWindow(holder);
                //holder.removeMapFragment(getContext());
            }

            @Override
            public void onViewAttachedToWindow(PostViewHolder holder) {
                super.onViewAttachedToWindow(holder);
                holder.getMapFragmentAndCallback(getContext(), new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        LatLng latLng = new LatLng(42.0864745, -72.62116889999999);
                        googleMap.addMarker(new MarkerOptions().position(latLng));
                        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    }

                });
            }
        };
        mRecycler.setAdapter(mAdapter);

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

