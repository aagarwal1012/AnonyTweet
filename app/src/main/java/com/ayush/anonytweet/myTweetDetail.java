package com.ayush.anonytweet;

import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.FirebaseDatabase;

public class myTweetDetail extends AppCompatActivity {
    public static final String EXTRA_POSITION = "position";
    private ProgressBar circular_progress;

    Button deleteTweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_tweet_detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MyTweets.class));
            }
        });

        // Set Collapsing Toolbar layout to the screen
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.my_tweet_detail_collapsing_toolbar);
        // Set title of Detail page
        collapsingToolbar.setTitle(" ");

        Bundle bundle = getIntent().getExtras();

        int position = bundle.getInt(EXTRA_POSITION);
        final String text, imagePath, tweetId;
        text = bundle.getString("Text");
        imagePath = bundle.getString("ImagePath");
        tweetId = bundle.getString("TweetId");

        TextView message = (TextView) findViewById(R.id.my_tweet_detail_view_message);
        message.setText(text);

        circular_progress = (ProgressBar) findViewById(R.id.my_tweet_detail_circular_progress_detail);

        ImageView image = (ImageView) findViewById(R.id.my_tweet_detail_image);
        //Progressing
        circular_progress.setVisibility(View.VISIBLE);
        //Loading image from Glide library.
        if (imagePath != null) {
            Glide.with(myTweetDetail.this).load(imagePath).into(image);
            circular_progress.setVisibility(View.INVISIBLE);
        }
        else {
            image.setImageResource(R.color.colorPrimary);
            circular_progress.setVisibility(View.INVISIBLE);
        }

        deleteTweet = (Button) findViewById(R.id.delete_tweet);
        deleteTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("Users").child(tweetId).removeValue();
                startActivity(new Intent(getApplicationContext(), MyTweets.class));
            }
        });

    }
}
