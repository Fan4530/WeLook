package com.laioffer.eventreporter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.NativeContentAdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * Created by program on 7/7/2017.
 */


public class EventListAdapter extends BaseAdapter {
    private static final String ADMOB_AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110";
    private static final String ADMOB_APP_ID = "ca-app-pub-3940256099942544~3347511713";

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_ADS = 1;
    private static final int TYPE_MAX_COUNT = TYPE_ADS + 1;
    private AdLoader.Builder builder;
    private TreeSet mSeparatorsSet = new TreeSet();

    private Context context;
    private List<Event> eventList;
    private DatabaseReference databaseReference;
    private LayoutInflater inflater;
    private String username;

    public void setUserName(String username) {
        this.username = username;
    }

    public EventListAdapter(Context context) {
        this.context = context;
        eventList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public EventListAdapter(Context context, List<Event> eventList) {
        this.context = context;
        this.eventList = new ArrayList<Event>();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        int count = 0;
        for (int i = 0; i < eventList.size(); i++) {
            if (i % 6 == 1) {
                mSeparatorsSet.add(i + count);
                count++;
                this.eventList.add(new Event());
            }
            this.eventList.add(eventList.get(i));
        }

        //initialie ads
        MobileAds.initialize(context, ADMOB_APP_ID);
        builder = new AdLoader.Builder(context, ADMOB_AD_UNIT_ID);
        inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return eventList.size();
    }

    @Override
    public Event getItem(int position) {
        return eventList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        TextView title;
        TextView location;
        TextView description;
        TextView time;
        ImageView imgview;

        ImageView img_view_good;
        ImageView img_view_comment;
        ImageView img_view_repost;

        TextView good_number;
        TextView comment_number;
        TextView repost_number;

        /* ads items*/
        FrameLayout frameLayout;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        int type = getItemViewType(position);
        if (rowView == null) {
            ViewHolder viewHolder = new ViewHolder();
            switch (type) {
                case TYPE_ITEM:
                    rowView = inflater.inflate(R.layout.event_list_item, parent, false);
                    viewHolder.title = (TextView) rowView.findViewById(R.id.event_item_title);
                    viewHolder.location = (TextView) rowView.findViewById(R.id.event_item_location);
                    viewHolder.description = (TextView) rowView.findViewById(R.id.event_item_description);
                    viewHolder.time = (TextView) rowView.findViewById(R.id.event_item_time);
                    viewHolder.imgview = (ImageView) rowView.findViewById(R.id.event_item_img);
                    //comment
                    viewHolder.img_view_good = (ImageView) rowView.findViewById(R.id.event_good_img);
                    viewHolder.img_view_comment = (ImageView) rowView.findViewById(R.id.event_comment_img);
                    viewHolder.img_view_repost = (ImageView) rowView.findViewById(R.id.event_repost_img);
                    viewHolder.good_number = (TextView) rowView.findViewById(R.id.event_good_number);
                    viewHolder.comment_number = (TextView) rowView.findViewById(R.id.event_comment_number);
                    viewHolder.repost_number = (TextView) rowView.findViewById(R.id.event_repost_number);

                    break;

                case TYPE_ADS:
                    rowView = inflater.inflate(R.layout.ads_container_layout, parent, false);
                    viewHolder.frameLayout =  (FrameLayout)rowView.findViewById(R.id.native_ads_container);
                    break;
            }

            rowView.setTag(viewHolder);
        }


        final ViewHolder holder = (ViewHolder) rowView.getTag();//这离对应上面的getTag???
        if (type == TYPE_ADS) {
            builder.forContentAd(new NativeContentAd.OnContentAdLoadedListener() {
                @Override
                public void onContentAdLoaded(NativeContentAd ad) {
                    NativeContentAdView adView = (NativeContentAdView) inflater.inflate(R.layout.ads_content, null);
                    addContentView(ad, adView);
                    holder.frameLayout.removeAllViews();
                    holder.frameLayout.addView(adView);
                }
            });


            NativeAdOptions adOptions = new NativeAdOptions.Builder()
                    .build();

            builder.withNativeAdOptions(adOptions);

            //build loader and load advertisement
            AdLoader adLoader = builder.withAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(int errorCode) {
                }
            }).build();
            adLoader.loadAd(new AdRequest.Builder().build());
        } else {

            final Event event = eventList.get(position);

            holder.title.setText(event.getTitle());
            String[] locations = event.getLocation().split(",");
            holder.location.setText(locations[1] + "," + locations[2]);//location有要求
            //CA, LA
            holder.description.setText(event.getDescription());//
            holder.time.setText(Utilities.timeTransformer(event.getTime()));


            if (event.getImgUri() != "") {
                final String url = event.getImgUri();
                holder.imgview.setVisibility(View.VISIBLE);//
                new AsyncTask<Void, Void, Bitmap>() {
                    @Override
                    protected Bitmap doInBackground(Void... params) {//background thread, return Bitmap type
                        return Utilities.getBitmapFromURL(url);
                    }

                    @Override
                    protected void onPostExecute(Bitmap bitmap) {//UI thread, input Bitmap from above background
                        holder.imgview.setImageBitmap(bitmap);
                    }
                }.execute();
            }
            //评论
            holder.good_number.setText(String.valueOf(event.getGood()));
            holder.repost_number.setText(String.valueOf(event.getRepost()));
            holder.comment_number.setText(String.valueOf(event.getCommentNumber()));

            holder.img_view_good.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    databaseReference.child("events").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Event recordedevent = snapshot.getValue(Event.class);
                                if (recordedevent.getId().equals(event.getId())) {
                                    int number = recordedevent.getGood();
                                    holder.good_number.setText(String.valueOf(number + 1));//+1,而不是直接取数据， 只点一次，要有一个tail
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

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CommentActivity.class);//从这个activity转到commentActivity

                    String eventId = event.getId();
                    intent.putExtra("EventID", eventId);
                    intent.putExtra("Commenter", username);
                    context.startActivity(intent);
                }
            });


        }

        return rowView;
    }


    @Override
    public int getItemViewType(int position) {
        return mSeparatorsSet.contains(position) ? TYPE_ADS : TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }


    private void addContentView(NativeContentAd nativeContentAd,
                                NativeContentAdView adView) {
        adView.setHeadlineView(adView.findViewById(R.id.ads_headline));
        adView.setImageView(adView.findViewById(R.id.ads_image));
        adView.setBodyView(adView.findViewById(R.id.ads_body));
        adView.setAdvertiserView(adView.findViewById(R.id.ads_advertiser));

        ((TextView) adView.getHeadlineView()).setText(nativeContentAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeContentAd.getBody());
        ((TextView) adView.getAdvertiserView()).setText(nativeContentAd.getAdvertiser());

        List<NativeAd.Image> images = nativeContentAd.getImages();

        if (images.size() > 0) {
            ((ImageView) adView.getImageView()).setImageDrawable(images.get(0).getDrawable());
        }
        adView.setNativeAd(nativeContentAd);
    }


}
