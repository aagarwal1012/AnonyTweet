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

import com.ayush.anonytweet.Detail;
import com.ayush.anonytweet.R;
import com.ayush.anonytweet.User;
import com.ayush.anonytweet.myTweetDetail;
import com.bumptech.glide.Glide;
import com.google.firebase.database.FirebaseDatabase;
import com.like.LikeButton;
import com.like.OnAnimationEndListener;
import com.like.OnLikeListener;

import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by Ayush on 05-Jan-18.
 */

public class myTweetAdapter extends RecyclerView.Adapter<myTweetAdapter.MyHolder> {
    List<User> list;
    Context context;

    public myTweetAdapter(List<User> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public myTweetAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_card,parent,false);
        myTweetAdapter.MyHolder myHolder = new myTweetAdapter.MyHolder(view);


        return myHolder;
    }

    @Override
    public void onBindViewHolder(final myTweetAdapter.MyHolder holder, int position) {

        final User mylist = list.get(list.size() - position - 1);

        //setting thumb-button
        holder.thumb_button.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                Toast.makeText(context, "Liked!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                Toast.makeText(context, "Disliked!", Toast.LENGTH_SHORT).show();
            }
        });
        holder.thumb_button.setOnAnimationEndListener(new OnAnimationEndListener() {
            @Override
            public void onAnimationEnd(LikeButton likeButton) {
                FirebaseDatabase.getInstance().getReference().child("Users").child(mylist.getData_id()).child("Number of Likes").setValue("" + (mylist.getNo_of_likes() + 1));
                Log.d(TAG, "Animation End for %s" + likeButton);
                holder.thumb_button.setLiked(true);
            }
        });

        //Loading image from Glide library.
        if (mylist.getImagePath() != null){
            float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, Resources.getSystem().getDisplayMetrics());
            holder.image.getLayoutParams().height = (int)px;
            Glide.with(context).load(mylist.getImagePath()).into(holder.image);
        }
        else {
            holder.image.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            Glide.with(context).load(mylist.getImagePath()).into(holder.image);
        }

        //setting message textView
        holder.text.setText(mylist.getText());

        //setting textView of no of likes
        holder.no_of_likes.setText(mylist.getNo_of_likes() + " Likes");

    }

    @Override
    public int getItemCount() {

        int arr = 0;

        try{
            if(list.size()==0){

                arr = 0;

            }
            else{

                arr=list.size();
            }



        }catch (Exception e){



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

        public ImageView image;
        public TextView text, no_of_likes;
        LikeButton thumb_button;


        public MyHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.card_image);
            text = (TextView) itemView.findViewById(R.id.card_text);
            no_of_likes = (TextView) itemView.findViewById(R.id.like_view);
            thumb_button = (LikeButton) itemView.findViewById(R.id.thumb_button);

            image.requestLayout();
            text.requestLayout();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, myTweetDetail.class);
                    int position = getAdapterPosition();
                    intent.putExtra(Detail.EXTRA_POSITION, (list.size() - position - 1));
                    intent.putExtra("Text", list.get(list.size() - position - 1).getText());
                    intent.putExtra("ImagePath", list.get(list.size() - position - 1).getImagePath());
                    intent.putExtra("TweetId", list.get(list.size() - position - 1).getData_id());
                    context.startActivity(intent);

                }
            });

        }
    }

}
