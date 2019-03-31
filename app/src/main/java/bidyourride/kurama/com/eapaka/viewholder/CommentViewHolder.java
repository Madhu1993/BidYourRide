package bidyourride.kurama.com.eapaka.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import bidyourride.kurama.com.eapaka.R;

/**
 * Created by madhukurapati on 3/18/18.
 */

public class CommentViewHolder extends RecyclerView.ViewHolder {

    public TextView authorView;
    public TextView bodyView;
    public RelativeLayout relativeLayout;

    public CommentViewHolder(View itemView) {
        super(itemView);
        relativeLayout = itemView.findViewById(R.id.myRelativeLaout);
        authorView = itemView.findViewById(R.id.comment_author);
        bodyView = itemView.findViewById(R.id.comment_body);
    }
}
