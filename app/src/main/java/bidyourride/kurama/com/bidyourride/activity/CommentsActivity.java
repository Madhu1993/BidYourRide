package bidyourride.kurama.com.bidyourride.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import bidyourride.kurama.com.bidyourride.R;
import bidyourride.kurama.com.bidyourride.fragment.CommentFragment;
import bidyourride.kurama.com.bidyourride.model.Comment;
import bidyourride.kurama.com.bidyourride.model.User;

/**
 * Created by madhukurapati on 3/21/18.
 */

public class CommentsActivity extends FragmentActivity {
    private CommentFragment commentFragment;
    private String mRideKey;
    private EditText mCommentField;
    private Button mPostButton;
    private String uid;
    private DatabaseReference mCommentsReference;

    private static final String TAG = "CommentsActivity";
    public static final String EXTRA_RIDE_KEY = "ride_key";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_comments);
        super.onCreate(savedInstanceState);

        if(getIntent().getExtras() != null){
            mRideKey = getIntent().getStringExtra("mRideKey");
            uid = getIntent().getStringExtra("uid");
        }else {
            return;
        }

        mCommentField = findViewById(R.id.ride_field_comment_text);
        mPostButton = findViewById(R.id.ride_post_comment);
        mPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");
                if (v != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
                Log.d(TAG, "onClick: after");
                postComment();
            }

        });

        mCommentsReference = FirebaseDatabase.getInstance().getReference()
                .child("rides-comments").child(mRideKey);

        commentFragment = new CommentFragment();
        Bundle bundle = new Bundle();
        bundle.putString("mRideKey", mRideKey);
        commentFragment.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.comments_fragment, commentFragment);
        ft.commit();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void postComment() {
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
}
