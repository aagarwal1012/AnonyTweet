package com.ayush.anonytweet;

import android.content.Intent;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.ayush.anonytweet.Adapter.myTweetAdapter;
import com.ayush.anonytweet.Classes.User;
import com.ayush.anonytweet.Classes.favTweets;
import com.ayush.anonytweet.Classes.usersLiked;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyTweets extends AppCompatActivity {

    private ProgressBar circular_progress;
    RecyclerView recycle;
    SwipeRefreshLayout refreshLayout;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private FirebaseAuth auth;

    private String userEmail;

    private List<User> list_user = new ArrayList<>();
    private List<usersLiked> list_usersLiked = new ArrayList<>();

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tweets);

        circular_progress = (ProgressBar) findViewById(R.id.circular_progress_mytweets);

        // Adding Toolbar to Main screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_mytweets);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
        //toolbar.setTitle("My Tweets");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(getApplicationContext(), DashBoard.class));
                onSupportNavigateUp();
            }
        });

        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            VectorDrawableCompat indicator
                    = VectorDrawableCompat.create(getResources(), R.drawable.ic_arrow_back_white, getTheme());
            indicator.setTint(ResourcesCompat.getColor(getResources(),R.color.white,getTheme()));
            supportActionBar.setHomeAsUpIndicator(indicator);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        // init firebase database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        //Firebase auth
        auth = FirebaseAuth.getInstance();

        //getting userEmail
        userEmail = auth.getCurrentUser().getEmail();

        //setup Recycler view
        recycle = (RecyclerView) findViewById(R.id.my_recycler_view_mytweets);
        addEventFirebaseListener();

        //Refresh Layout
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_mytweets);
        refreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        addEventFirebaseListener();
                        refreshLayout.setRefreshing(false);
                    }
                }
        );

    }

    private void addEventFirebaseListener() {

        //Progressing
        circular_progress.setVisibility(View.VISIBLE);

        //Read from database
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (list_user.size() > 0)
                    list_user.clear();
                if (list_usersLiked.size() > 0)
                    list_usersLiked.clear();

                for (DataSnapshot postSnapshot:dataSnapshot.child("Users").getChildren()) {

                    User user = new User();
                    user.setText(postSnapshot.child("Text ").getValue(String.class));
                    user.setImagePath(postSnapshot.child("Image Path").getValue(String.class));
                    user.setEmail(postSnapshot.child("Email").getValue(String.class));
                    if (postSnapshot.child("Number of Likes").getValue(String.class) != null)
                        user.setNo_of_likes(Integer.parseInt(postSnapshot.child("Number of Likes").getValue(String.class)));
                    user.setData_id(postSnapshot.child("Data Id").getValue(String.class));
                    if (user != null && user.getEmail() != null && FirebaseAuth.getInstance().getCurrentUser() != null && user.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail()) == true) {
                            list_user.add(user);
                    }

                }

                int temp = 0;
                for (DataSnapshot postSnapshot:dataSnapshot.child("Likes").getChildren())
                {
                    usersLiked usersLiked = postSnapshot.getValue(com.ayush.anonytweet.Classes.usersLiked.class);
                    if (temp < list_user.size() - 1){
                        if (list_user.size() != 0 && usersLiked != null && list_user.get(temp).getData_id().equals(usersLiked.getTweetId()) == true){
                            list_usersLiked.add(usersLiked);
                            temp++;
                        }
                    }
                    else if (temp == list_user.size() - 1){
                        try{
                            if (list_user.size() != 0 && usersLiked != null && usersLiked.getTweetId() != null && list_user.get(temp).getData_id().equals(usersLiked.getTweetId()) == true){
                                list_usersLiked.add(usersLiked);
                                temp = 0;
                            }
                        }
                        catch (Exception e){
                            Log.d("Error", "Error! " + e);
                        }
                    }
                }

                favTweets favTweets = dataSnapshot.child("Favourites").child(FirebaseAuth.getInstance().getUid()).getValue(com.ayush.anonytweet.Classes.favTweets.class);

                recycle.setHasFixedSize(true);
                myTweetAdapter recyclerViewAdapter = new myTweetAdapter(list_user, list_usersLiked, favTweets, MyTweets.this);
                recycle.setLayoutManager(new LinearLayoutManager(MyTweets.this));
                recycle.setAdapter(recyclerViewAdapter);
                circular_progress.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        mDatabaseReference.child("Users").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                if (list_user.size() > 0)
//                    list_user.clear();
//                for (DataSnapshot postSnapshot:dataSnapshot.getChildren()) {
//
//                    User user = new User();            //postSnapshot.getValue(User.class);
//                    user.setText(postSnapshot.child("Text ").getValue(String.class));
//                    user.setImagePath(postSnapshot.child("Image Path").getValue(String.class));
//                    user.setEmail(postSnapshot.child("Email").getValue(String.class));
//                    user.setData_id(postSnapshot.child("Data Id").getValue(String.class));
//                    if (postSnapshot.child("Number of Likes").getValue(String.class) != null)
//                        user.setNo_of_likes(Integer.parseInt(postSnapshot.child("Number of Likes").getValue(String.class)));
//                    user.setData_id(postSnapshot.child("Data Id").getValue(String.class));
//                    if (user != null && user.getEmail().equals(userEmail) == true)
//                        list_user.add(user);
//
//                }
//
//                recycle.setHasFixedSize(true);
//                myTweetAdapter recyclerViewAdapter = new myTweetAdapter(list_user, MyTweets.this);
//                recycle.setLayoutManager(new LinearLayoutManager(MyTweets.this));
//                recycle.setAdapter(recyclerViewAdapter);
//                circular_progress.setVisibility(View.INVISIBLE);
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

    }
}
