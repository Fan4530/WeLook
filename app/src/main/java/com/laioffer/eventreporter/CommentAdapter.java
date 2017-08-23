package com.laioffer.eventreporter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by program on 7/14/2017.
 */

public class CommentAdapter extends BaseAdapter {
    private Context context;
    private final static int TYPE_EVENT = 0;
    private final static int TYPE_COMMENT = 1;
    private final static int TYPE_MAX_COUNT = 2;

    private List<Comment> commentList;
    private Event event;

    private DatabaseReference databaseReference;
    private LayoutInflater inflater;

    public CommentAdapter(Context context) {
        this.context = context;
        commentList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    public CommentAdapter(Context context, final Event event, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
        this.event = event;
        databaseReference = FirebaseDatabase.getInstance().getReference();
        inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setEvent(final Event event) {
        this.event = event;
    }

    public void setComments(final List<Comment> comments) {
        this.commentList = comments;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_EVENT: TYPE_COMMENT;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }

    @Override
    public int getCount() {
        return commentList.size() + 1;
    }

    @Override
    public Comment getItem(int position) {
        return commentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        /* event items */
        TextView event_username;
        TextView event_location;
        TextView event_description;
        TextView event_time;
        TextView event_title;

        ImageView event_img;

        ImageView event_img_view_like;
        ImageView event_img_view_comment;
        ImageView event_img_view_repost;

        TextView event_like_number;
        TextView event_comment_number;
        TextView event_repost_number;

        /* comment items */
        TextView comment_user;
        TextView comment_description;
        TextView comment_time;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        int type = getItemViewType(position);

        if (rowView == null) {
            ViewHolder viewHolder = new ViewHolder();
            switch (type) {
                case TYPE_EVENT:
                    rowView = inflater.inflate(R.layout.comment_main, parent, false);
                    viewHolder.event_username = (TextView)rowView.findViewById(R.id.comment_main_user);
                    viewHolder.event_location = (TextView)rowView.findViewById(R.id.comment_main_location);
                    viewHolder.event_description = (TextView)rowView.findViewById(R.id.comment_main_description);
                    viewHolder.event_time = (TextView)rowView.findViewById(R.id.comment_main_time);
                    viewHolder.event_img = (ImageView) rowView.findViewById(R.id.comment_main_image);
                    viewHolder.event_img_view_like = (ImageView) rowView.findViewById(R.id.comment_main_like_img);
                    viewHolder.event_img_view_comment = (ImageView) rowView.findViewById(R.id.comment_main_comment_img);
                    viewHolder.event_img_view_repost = (ImageView) rowView.findViewById(R.id.comment_main_repost_img);
                    viewHolder.event_like_number = (TextView) rowView.findViewById(R.id.comment_main_like_num);
                    viewHolder.event_comment_number = (TextView) rowView.findViewById(R.id.comment_main_comment_num);
                    viewHolder.event_repost_number = (TextView) rowView.findViewById(R.id.comment_main_repost_num);
                    viewHolder.event_title = (TextView) rowView.findViewById(R.id.comment_main_title);
                    break;
                case TYPE_COMMENT:
                    rowView = inflater.inflate(R.layout.comment_item, parent, false);
                    viewHolder.comment_user = (TextView) rowView.findViewById(R.id.comment_item_user);
                    viewHolder.comment_description = (TextView)rowView.findViewById(R.id.comment_item_description);
                    viewHolder.comment_time = (TextView)rowView.findViewById(R.id.comment_item_time);
                    break;
            }
            rowView.setTag(viewHolder);
        }

        final ViewHolder holder = (ViewHolder) rowView.getTag();
        if (type == TYPE_EVENT) {
            String[] locations = event.getLocation().split(",");
            try {
                holder.event_location.setText(locations[1] + "," + locations[2]);
            } catch (Exception ex) {
                holder.event_location.setText("Wrong Location");
            }
            holder.event_description.setText(event.getDescription());
            holder.event_username.setText(event.getUser());
            holder.event_title.setText(event.getTitle());
            holder.event_time.setText(Utilities.timeTransformer(event.getTime()));
            if (!event.getImgUri().equals("")) {
                final String url = event.getImgUri();
                new AsyncTask<Void, Void, Bitmap>() {
                    @Override
                    protected void onPreExecute() {
                        holder.event_img.setImageBitmap(null);
                        holder.event_img.setVisibility(View.VISIBLE);
                    }

                    @Override
                    protected Bitmap doInBackground(Void... params) {
                        return Utilities.getBitmapFromURL(url);
                    }

                    @Override
                    protected void onPostExecute(Bitmap bitmap) {
                        holder.event_img.setImageBitmap(bitmap);
                    }
                }.execute();
            } else {
                holder.event_img.setVisibility(View.GONE);
            }

            holder.event_like_number.setText(String.valueOf(event.getGood()));
            holder.event_repost_number.setText(String.valueOf(event.getRepost()));
            holder.event_comment_number.setText(String.valueOf(event.getCommentNumber()));
            holder.event_img_view_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    databaseReference.child("events").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Event recordedevent = snapshot.getValue(Event.class);
                                if (recordedevent.getId().equals(event.getId())) {
                                    int number = recordedevent.getGood();
                                    holder.event_like_number.setText(String.valueOf(number + 1));
                                    snapshot.getRef().child("good").setValue(number + 1);
                                    break;
                                }

                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });
        } else {
            final Comment comment = commentList.get(position - 1);
            holder.comment_user.setText(comment.getCommenter());
            holder.comment_description.setText(comment.getDescription());
            holder.comment_time.setText(Utilities.timeTransformer(comment.getTime()));
        }

        return rowView;
    }

}
