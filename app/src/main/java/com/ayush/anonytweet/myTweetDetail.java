package com.ayush.anonytweet;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.FirebaseDatabase;

import org.checkerframework.checker.androidresource.qual.IdRes;
import org.checkerframework.checker.androidresource.qual.LayoutRes;

public class myTweetDetail extends AppCompatActivity {
    private static final String EXTRA_POSITION = "position";

    private static final @LayoutRes int ACTIVITY_MY_TWEET_DETAIL_LAYOUT = R.layout.activity_my_tweet_detail;
    private static final @LayoutRes int DIALOGUE_LAYOUT = R.layout.dialogue;

    private static final @IdRes int MY_TWEET_DETAIL_TOOLBAR_ID = R.id.my_tweet_detail_toolbar;
    private static final @IdRes int MY_TWEET_DETAIL_COLLAPSING_TOOLBAR_ID = R.id.my_tweet_detail_collapsing_toolbar;
    private static final @IdRes int MY_TWEET_DETAIL_VIEW_MESSAGE_ID = R.id.my_tweet_detail_view_message;
    private static final @IdRes int MY_TWEET_DETAIL_CIRCULAR_PROGRESS_DETAIL_ID = R.id.my_tweet_detail_circular_progress_detail;
    private static final @IdRes int MY_TWEET_DETAIL_IMAGE_ID = R.id.my_tweet_detail_image;
    private static final @IdRes int DELETE_TWEET_ID = R.id.delete_tweet;
    private static final @IdRes int MESSAGE_ID = R.id.message;
    private static final @IdRes int DIALOG_BUTTON_OK_ID = R.id.dialogButtonOK;
    private static final @IdRes int EDIT_TWEET_ID = R.id.edit_tweet;

    private Button deleteTweet;
    private String text, imagePath, tweetId;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ACTIVITY_MY_TWEET_DETAIL_LAYOUT);

        Toolbar toolbar = findViewById(MY_TWEET_DETAIL_TOOLBAR_ID);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(view -> {
            //startActivity(new Intent(getApplicationContext(), MyTweets.class));
            onSupportNavigateUp();
        });

        // Set Collapsing Toolbar layout to the screen
        CollapsingToolbarLayout collapsingToolbar =
                findViewById(MY_TWEET_DETAIL_COLLAPSING_TOOLBAR_ID);
        // Set title of Detail page
        collapsingToolbar.setTitle(" ");

        Bundle bundle = getIntent().getExtras();

        int position = bundle.getInt(EXTRA_POSITION);
        text = bundle.getString("Text");
        imagePath = bundle.getString("ImagePath");
        tweetId = bundle.getString("TweetId");

        TextView message = findViewById(MY_TWEET_DETAIL_VIEW_MESSAGE_ID);
        message.setText(text);

        ProgressBar circular_progress = findViewById(MY_TWEET_DETAIL_CIRCULAR_PROGRESS_DETAIL_ID);

        ImageView image = findViewById(MY_TWEET_DETAIL_IMAGE_ID);
        //Progressing
        circular_progress.setVisibility(View.VISIBLE);
        //Loading image from Glide library.
        if (imagePath != null) {
            Glide.with(myTweetDetail.this).load(imagePath).into(image);
            circular_progress.setVisibility(View.INVISIBLE);
        } else {
            image.setImageResource(R.color.colorPrimary);
            circular_progress.setVisibility(View.INVISIBLE);
        }

        //delete button
        deleteTweet = findViewById(DELETE_TWEET_ID);
        deleteTweet.setOnClickListener(view -> {

            // custom dialog
            final Dialog dialog = new Dialog(myTweetDetail.this);
            dialog.setContentView(DIALOGUE_LAYOUT);

            TextView message1 = dialog.findViewById(MESSAGE_ID);
            message1.setText("Your tweet will be deleted...");

            Button ok_btn = dialog.findViewById(DIALOG_BUTTON_OK_ID);
            ok_btn.setOnClickListener(view1 -> {
                FirebaseDatabase.getInstance().getReference().child("Users").child(tweetId).removeValue();
                FirebaseDatabase.getInstance().getReference().child("Likes").child(tweetId).removeValue();
                startActivity(new Intent(getApplicationContext(), DashBoard.class));
                dialog.dismiss();
            });

            dialog.show();

        });

        //edit button
        final Button editTweet = findViewById(EDIT_TWEET_ID);
        editTweet.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), editTweet.class);
            intent.putExtra("TweetId", tweetId);
            intent.putExtra("ImageUrl", imagePath);
            intent.putExtra("Text", text);
            startActivity(intent);
        });

    }

}
