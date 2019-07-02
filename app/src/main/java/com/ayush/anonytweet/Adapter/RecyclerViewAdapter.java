package com.ayush.anonytweet.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ayush.anonytweet.Classes.User;
import com.ayush.anonytweet.Classes.favTweets;
import com.ayush.anonytweet.Classes.usersLiked;
import com.ayush.anonytweet.Detail;
import com.ayush.anonytweet.myTweetDetail;
import com.ayush.anonytweet.postComments;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;
import static com.ayush.anonytweet.Adapter.myTweetAdapter.CARD_COMMENTS_ID;
import static com.ayush.anonytweet.Adapter.myTweetAdapter.CARD_FAV_BUTTON_ID;
import static com.ayush.anonytweet.Adapter.myTweetAdapter.CARD_IMAGE_ID;
import static com.ayush.anonytweet.Adapter.myTweetAdapter.CARD_TEXT_ID;
import static com.ayush.anonytweet.Adapter.myTweetAdapter.CARD_USER_NAME_ID;
import static com.ayush.anonytweet.Adapter.myTweetAdapter.ITEM_CARD_ID;
import static com.ayush.anonytweet.Adapter.myTweetAdapter.LIKE_VIEW_ID;
import static com.ayush.anonytweet.Adapter.myTweetAdapter.THUMB_BUTTON_ID;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyHolder> {

    private List<User> list;
    private final List<usersLiked> usersLiked;
    private final favTweets favTweets;
    private final Context context;

    public RecyclerViewAdapter(List<User> list, List<com.ayush.anonytweet.Classes.usersLiked> usersLiked, favTweets favTweets, Context context) {
        this.list = list;
        this.usersLiked = usersLiked;
        this.context = context;
        this.favTweets = favTweets;
    }

    private static long longHash(String string) {
        long h = 0;
        int l = string.length();
        char[] chars = string.toCharArray();

        for (int i = 0; i < l; i++) {
            h = 2 * h + chars[i];
        }
        return h;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(ITEM_CARD_ID, parent, false);


        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, int position) {

        final User mylist = list.get(list.size() - position - 1);
        final usersLiked myusersLiked = usersLiked.get(usersLiked.size() - position - 1);
        int user_liked_position = 0;
        final List<String> liked_users;

        if (myusersLiked != null && myusersLiked.getTweetId().equals(mylist.getData_id())) {
            liked_users = myusersLiked.getLiked_users();
            if (liked_users != null) {
                for (int j = 0; j < liked_users.size(); j++) {
                    if (liked_users.get(j).equals(holder.currentUserEmail)) {
                        holder.thumb_button.setLiked(true);
                        break;
                    } else {
                        holder.thumb_button.setLiked(false);
                    }
                }
            }
        }

        if (favTweets != null && favTweets.getTweetIds() != null) {
            for (int i = 0; i < favTweets.getTweetIds().size(); i++) {
                try {
                    if (mylist != null && favTweets != null && mylist.getData_id().equals(favTweets.getTweetIds().get(i))) {
                        holder.fav_button.setLiked(true);
                        break;
                    } else {
                        holder.fav_button.setLiked(false);
                    }
                } catch (Exception e) {
                    Log.d("Error", "Error! " + e);
                }
            }
        }

        //Loading image from Glide library.
        if (mylist.getImagePath() != null) {
            float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, Resources.getSystem().getDisplayMetrics());
            holder.image.getLayoutParams().height = (int) px;
            Glide.with(context).load(mylist.getImagePath()).into(holder.image);
        } else {
            holder.image.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            Glide.with(context).load(mylist.getImagePath()).into(holder.image);
        }

        //setting message textView
        holder.text.setText(mylist.getText());

        //setting textView of no of likes
        if (myusersLiked != null && myusersLiked.getLiked_users() != null)
            holder.no_of_likes.setText(myusersLiked.getLiked_users().size() + " Likes");

        //setting userName
        if (mylist != null && mylist.getEmail().equals(holder.currentUserEmail)) {
            holder.userName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        } else if (mylist != null && holder.currentUserEmail != null && !mylist.getEmail().equals(holder.currentUserEmail)) {
            long userId = longHash(mylist.getEmail());
            holder.userName.setText("Anonymous " + userId);
        }

        //setting thumb-button
        holder.thumb_button.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                Toast.makeText(context, "Liked!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                Toast.makeText(context, "Disliked!", Toast.LENGTH_SHORT).show();
                List<String> liked_user;
                if (myusersLiked.getLiked_users() != null) {
                    liked_user = myusersLiked.getLiked_users();
                } else {
                    liked_user = new ArrayList<>();
                }
                int user_posn = 0;
                for (int i = 0; i < liked_user.size(); i++) {
                    if (liked_user.get(i).equals(holder.currentUserEmail))
                        user_posn = i + 1;
                }
                if (user_posn != 0) {
                    liked_user.remove(user_posn - 1);
                }
                usersLiked usersLiked = new usersLiked(liked_user, mylist.getData_id());
                FirebaseDatabase.getInstance().getReference().child("Likes").child(mylist.getData_id()).setValue(usersLiked);

                FirebaseDatabase.getInstance().getReference().child("Users").child(mylist.getData_id()).child("Number of Likes").setValue("" + (mylist.getNo_of_likes() - 1));
            }
        });
        holder.thumb_button.setOnAnimationEndListener(likeButton -> {
            List<String> liked_user;
            if (myusersLiked.getLiked_users() != null) {
                liked_user = myusersLiked.getLiked_users();
            } else {
                liked_user = new ArrayList<>();
            }
            liked_user.add(holder.currentUserEmail);
            usersLiked usersLiked = new usersLiked(liked_user, mylist.getData_id());
            FirebaseDatabase.getInstance().getReference().child("Likes").child(mylist.getData_id()).setValue(usersLiked);

            FirebaseDatabase.getInstance().getReference().child("Users").child(mylist.getData_id()).child("Number of Likes").setValue("" + (mylist.getNo_of_likes() + 1));
            Log.d(TAG, "Animation End for %s" + likeButton);
            holder.thumb_button.setLiked(true);
        });

        //setting favourite button
        holder.fav_button.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                Toast.makeText(context, "Added to your Favourites", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                Toast.makeText(context, "Removed form your Favourites", Toast.LENGTH_SHORT).show();
                List<String> fav;
                if (favTweets != null && favTweets.getTweetIds() != null) {
                    fav = favTweets.getTweetIds();
                } else {
                    fav = new ArrayList<>();
                }
                for (int i = 0; i < fav.size(); i++) {
                    if (mylist != null && mylist.getData_id() != null && fav != null && mylist.getData_id().equals(fav.get(i))) {
                        fav.remove(i);
                        favTweets updt = new favTweets(fav);
                        FirebaseDatabase.getInstance().getReference().child("Favourites").child(FirebaseAuth.getInstance().getUid()).setValue(updt);
                    }
                }
            }
        });
        holder.fav_button.setOnAnimationEndListener(likeButton -> {
            List<String> fav;
            if (favTweets != null && favTweets.getTweetIds() != null) {
                fav = favTweets.getTweetIds();
            } else {
                fav = new ArrayList<>();
            }

            if (mylist != null && mylist.getData_id() != null) {
                fav.add(mylist.getData_id());
                favTweets updt = new favTweets(fav);
                FirebaseDatabase.getInstance().getReference().child("Favourites").child(FirebaseAuth.getInstance().getUid()).setValue(updt);
            }

        });

    }

    @Override
    public int getItemCount() {

        int arr = 0;

        try {
            if (list.size() == 0) {

                arr = 0;

            } else {

                arr = list.size();
            }


        } catch (Exception e) {


        }

        return arr;

    }

    public void clear() {
        this.list = null;
    }

    public void addAll(List<User> userList) {
        this.list = userList;
    }

    class MyHolder extends RecyclerView.ViewHolder {

        final ImageView image;
        final TextView text;
        final TextView no_of_likes;
        final TextView userName;
        final TextView postComments;
        final LikeButton thumb_button;
        final LikeButton fav_button;

        final String currentUserEmail;

        MyHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(CARD_IMAGE_ID);
            text = itemView.findViewById(CARD_TEXT_ID);
            no_of_likes = itemView.findViewById(LIKE_VIEW_ID);
            thumb_button = itemView.findViewById(THUMB_BUTTON_ID);
            fav_button = itemView.findViewById(CARD_FAV_BUTTON_ID);
            userName = itemView.findViewById(CARD_USER_NAME_ID);
            postComments = itemView.findViewById(CARD_COMMENTS_ID);

            image.requestLayout();
            text.requestLayout();

            currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                Context context = view.getContext();
                if (list.get(list.size() - position - 1).getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                    Intent intent = new Intent(context, myTweetDetail.class);
                    intent.putExtra(Detail.EXTRA_POSITION, (list.size() - position - 1));
                    intent.putExtra("Text", list.get(list.size() - position - 1).getText());
                    intent.putExtra("ImagePath", list.get(list.size() - position - 1).getImagePath());
                    intent.putExtra("TweetId", list.get(list.size() - position - 1).getData_id());
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent(context, Detail.class);
                    intent.putExtra(Detail.EXTRA_POSITION, (list.size() - position - 1));
                    intent.putExtra("Text", list.get(list.size() - position - 1).getText());
                    intent.putExtra("ImagePath", list.get(list.size() - position - 1).getImagePath());
                    context.startActivity(intent);
                }

            });

            postComments.setOnClickListener(view -> {
                Context context = view.getContext();
                int position = getAdapterPosition();
                Intent intent = new Intent(context, postComments.class);
                intent.putExtra("TweetId", list.get(list.size() - position - 1).getData_id());
                context.startActivity(intent);
            });

        }
    }

}
