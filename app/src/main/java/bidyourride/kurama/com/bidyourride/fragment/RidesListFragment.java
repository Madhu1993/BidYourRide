package bidyourride.kurama.com.bidyourride.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.gson.Gson;

import java.util.List;

import bidyourride.kurama.com.bidyourride.R;
import bidyourride.kurama.com.bidyourride.activity.AddRideActivity;
import bidyourride.kurama.com.bidyourride.activity.BaseActivity;
import bidyourride.kurama.com.bidyourride.activity.RideDetailsActivity;
import bidyourride.kurama.com.bidyourride.helper.ObjectWrapperForBinder;
import bidyourride.kurama.com.bidyourride.model.DirectionObject;
import bidyourride.kurama.com.bidyourride.model.RideRequest;
import bidyourride.kurama.com.bidyourride.viewholder.RidesViewHolder;

/**
 * Created by madhukurapati on 11/20/17.
 */

public abstract class RidesListFragment extends Fragment {

    private static final String TAG = "RidesListFragment";

    private DatabaseReference mDatabase;

    private FirebaseRecyclerAdapter<RideRequest, RidesViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    private Gson gson;
    View rootView;
    public String rideJson;

    public RidesListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_all_rides_today, container, false);

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
        mManager.setItemPrefetchEnabled(true);
        mRecycler.setLayoutManager(mManager);
        gson = new Gson();


        // Set up FirebaseRecyclerAdapter with the Query
        final Query ridesQuery = getQuery(mDatabase);

        mAdapter = new FirebaseRecyclerAdapter<RideRequest, RidesViewHolder>(RideRequest.class, R.layout.rides_details_card_view,
                RidesViewHolder.class, ridesQuery) {

            @Override
            public void onBindViewHolder(RidesViewHolder holder, int position, List<Object> payloads) {
                super.onBindViewHolder(holder, position, payloads);
            }

            @Override
            protected void populateViewHolder(final RidesViewHolder viewHolder, final RideRequest model, final int position) {
                rideJson = gson.toJson(model);
                if (viewHolder == null) {
                    return;
                }
                final DatabaseReference rideRef = getRef(position);
                final String rideKey = rideRef.getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*String gsonForRide = gson.toJson(model);
                        String typeOfReferenceToOpen = model.typeOfRequest;*/
                        
                        final Bundle bundle = new Bundle();
                        bundle.putBinder("rideObject", new ObjectWrapperForBinder(model));
                        Intent rideDetailsIntent = new Intent(rootView.getContext(), RideDetailsActivity.class);
                        rideDetailsIntent.putExtras(bundle);
                        rideDetailsIntent.putExtra(RideDetailsActivity.EXTRA_RIDE_KEY, rideKey);
                        startActivity(rideDetailsIntent);

                    }
                });

                setStarImage(viewHolder, model);

                // Bind Ride to ViewHolder, setting OnClickListener for the star button
                viewHolder.bindToPost( model, new View.OnClickListener() {
                    @Override
                    public void onClick(View starView) {
                        if(model.typeOfRequest.equals("1")){
                            DatabaseReference globalPostRef = mDatabase.child("rides-requested").child(rideRef.getKey());                                               DatabaseReference userPostRef = mDatabase.child("user-rides").child(model.uid).child(rideRef.getKey());
                            onStarClicked(globalPostRef);
                            onStarClicked(userPostRef);
                        }else{
                            DatabaseReference globalPostRef = mDatabase.child("rides-provided").child(rideRef.getKey());                        DatabaseReference userPostRef = mDatabase.child("user-rides").child(model.uid).child(rideRef.getKey());
                            onStarClicked(globalPostRef);
                            onStarClicked(userPostRef);
                        }
                    }
                });

            }

            private void setStarImage(RidesViewHolder viewHolder, RideRequest model) {
                // Determine if the current user has liked this post and set UI accordingly
                if (model.stars.containsKey(getUid())) {
                    viewHolder.starView.setImageResource(R.drawable.ic_toggle_star_24);
                } else {
                    viewHolder.starView.setImageResource(R.drawable.ic_toggle_star_outline_24);
                }
            }

            @Override
            public void onAttachedToRecyclerView(RecyclerView recyclerView) {
                super.onAttachedToRecyclerView(recyclerView);
            }

            @Override
            public boolean onFailedToRecycleView(RidesViewHolder holder) {
                return super.onFailedToRecycleView(holder);
            }

            @Override
            public RideRequest getItem(int position) {
                return super.getItem(position);
            }

            @Override
            public RidesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View inflatedView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.rides_details_card_view, parent, false);
                return new RidesViewHolder(inflatedView);
            }

        };
        mRecycler.setAdapter(mAdapter);
    }

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

