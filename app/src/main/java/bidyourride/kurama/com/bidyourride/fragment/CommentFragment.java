package bidyourride.kurama.com.bidyourride.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import bidyourride.kurama.com.bidyourride.R;
import bidyourride.kurama.com.bidyourride.adapter.CommentAdapter;

/**
 * Created by madhukurapati on 3/21/18.
 */

public class CommentFragment extends Fragment {
    private static final String TAG = "CommentFragment";
    View rootView;
    private RecyclerView mCommentsRecycler;
    private LinearLayoutManager mManager;
    private CommentAdapter mAdapter;
    private DatabaseReference mCommentsReference;
    String mRideKey;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_comments, container, false);
        mCommentsRecycler = rootView.findViewById(R.id.comment_recycler);
        mManager = new LinearLayoutManager(getActivity());
        mCommentsRecycler.setHasFixedSize(true);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mRideKey = bundle.getString("mRideKey");
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCommentsRecycler.setLayoutManager(mManager);
        mCommentsReference = FirebaseDatabase.getInstance().getReference()
                .child("rides-comments").child(mRideKey);

        // Listen for comments
        mAdapter = new CommentAdapter(getActivity(), mCommentsReference);
        mCommentsRecycler.setAdapter(mAdapter);
    }
}
