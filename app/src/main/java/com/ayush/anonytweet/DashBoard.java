package com.ayush.anonytweet;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ayush.anonytweet.Adapter.RecyclerViewAdapter;
import com.ayush.anonytweet.Classes.User;
import com.ayush.anonytweet.Classes.favTweets;
import com.ayush.anonytweet.Classes.usersLiked;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DashBoard extends AppCompatActivity {

    private final String notificationTweet = "New Tweets";
    private final String notificationLikes = "New Tweet Updates";
    private final int notificationTweetId = 1;
    private final int notificationLikesId = 2;
    private RecyclerView recycle;
    private SwipeRefreshLayout refreshLayout;
    private DrawerLayout mDrawerLayout;
    private FirebaseAuth auth;
    private ProgressBar circular_progress;
    private DatabaseReference mDatabaseReference;
    private final List<User> list_user = new ArrayList<>();
    private final List<usersLiked> list_usersLiked = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        circular_progress = findViewById(R.id.circular_progress);

        auth = FirebaseAuth.getInstance();

        // Adding Toolbar to Main screen
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create Navigation drawer and inflate layout
        NavigationView navigationView = findViewById(R.id.nav_view);
        mDrawerLayout = findViewById(R.id.drawer);

        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            VectorDrawableCompat indicator
                    = VectorDrawableCompat.create(getResources(), R.drawable.ic_menu, getTheme());
            indicator.setTint(ResourcesCompat.getColor(getResources(), R.color.white, getTheme()));
            supportActionBar.setHomeAsUpIndicator(indicator);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Welcome
        View navHeader = navigationView.getHeaderView(0);
        TextView nav_header_name = navHeader.findViewById(R.id.nav_header_name);
        CircleImageView nav_header_image = navHeader.findViewById(R.id.nav_header_image);
        final FirebaseUser user = auth.getCurrentUser();
        if (auth != null) {
            nav_header_name.setText(user.getDisplayName());
        }
        if (user.getPhotoUrl() != null) {
            Glide.with(getApplicationContext()).load(user.getPhotoUrl().toString()).into(nav_header_image);
        }

        // Set behavior of Navigation drawer
        // This method will trigger on item Click of navigation menu
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    // Set item in checked state
                    menuItem.setChecked(true);

                    // handle navigation
                    int id = menuItem.getItemId();
                    if (id == R.id.nav_change_password) {
                        startActivity(new Intent(DashBoard.this, changePassword.class));
                    } else if (id == R.id.bt_logout) {
                        logoutUser();
                    } else if (id == R.id.nav_about) {
                        startActivity(new Intent(DashBoard.this, AboutFragment.class));
                        /*Fragment fragment = new AboutFragment();
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.swipe_refresh, fragment);
                        fragmentTransaction.commit();*/

                    } else if (id == R.id.nav_profile) {
                        startActivity(new Intent(DashBoard.this, userProfile.class));
                    } else if (id == R.id.nav_mytweets) {
                        startActivity(new Intent(DashBoard.this, MyTweets.class));
                    } else if (id == R.id.nav_myfavourites) {
                        startActivity(new Intent(DashBoard.this, myFavourites.class));
                    }

                    // Closing drawer on item click
                    mDrawerLayout.closeDrawers();
                    return true;
                });

        // Adding Floating Action Button to bottom right of main view
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(DashBoard.this, Tweet.class);
            startActivity(intent);
        });

        // init firebase database
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        //setup Recycler view
        recycle = findViewById(R.id.my_recycler_view);
        addEventFirebaseListener();

        //Refresh Layout
        refreshLayout = findViewById(R.id.swipe_refresh);
        refreshLayout.setOnRefreshListener(
                () -> {
                    addEventFirebaseListener();
                    refreshLayout.setRefreshing(false);
                }
        );
    }

    private void addEventFirebaseListener() {

        //Progressing
        circular_progress.setVisibility(View.VISIBLE);

        //int tweet_added = 0, new_likes = 0;

        //Read from database
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (list_user.size() > 0)
                    list_user.clear();
                if (list_usersLiked.size() > 0)
                    list_usersLiked.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.child("Users").getChildren()) {

                    User user = new User();
                    user.setText(postSnapshot.child("Text ").getValue(String.class));
                    user.setImagePath(postSnapshot.child("Image Path").getValue(String.class));
                    user.setEmail(postSnapshot.child("Email").getValue(String.class));
                    if (postSnapshot.child("Number of Likes").getValue(String.class) != null)
                        user.setNo_of_likes(Integer.parseInt(postSnapshot.child("Number of Likes").getValue(String.class)));
                    user.setData_id(postSnapshot.child("Data Id").getValue(String.class));
                    list_user.add(user);

                }

                for (DataSnapshot postSnapshot : dataSnapshot.child("Likes").getChildren()) {
                    usersLiked usersLiked = postSnapshot.getValue(com.ayush.anonytweet.Classes.usersLiked.class);
                    list_usersLiked.add(usersLiked);
                }

                favTweets favTweets = dataSnapshot.child("Favourites").child(FirebaseAuth.getInstance().getUid()).getValue(com.ayush.anonytweet.Classes.favTweets.class);

                recycle.setHasFixedSize(true);
                RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(list_user, list_usersLiked, favTweets, DashBoard.this);
                recycle.setLayoutManager(new LinearLayoutManager(DashBoard.this));
                recycle.setAdapter(recyclerViewAdapter);
                circular_progress.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseReference.child("Users").addChildEventListener(new ChildEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (!isNotificationPresent(notificationTweetId)) {
                    generateNotification(getApplicationContext(), notificationTweet, notificationTweetId);
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (!isNotificationPresent(notificationLikesId)) {
                    generateNotification(getApplicationContext(), notificationLikes, notificationLikesId);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_changePass) {
            startActivity(new Intent(DashBoard.this, changePassword.class));
            return true;
        } else if (id == R.id.action_logOut) {
            logoutUser();
            return true;
        } else if (id == R.id.menu_refresh) {
            refreshLayout.setRefreshing(true);
            addEventFirebaseListener();
            refreshLayout.setRefreshing(false);
            return true;
        } else if (id == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    private void logoutUser() {
        auth.signOut();
        if (auth.getCurrentUser() == null) {
            Intent intent = new Intent(DashBoard.this, login.class);
            intent.putExtra("logout", true);
            startActivity(intent);
            finish();
        }
    }

    private void generateNotification(Context context, String message, int notificationId) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        Intent intent = new Intent(context, DashBoard.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.drawable.twitter_bird);
        builder.setContentTitle("AnonyTweet");
        builder.setContentText(message);
        builder.setSubText("Tap to view");

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Will display the notification in the notification bar
        notificationManager.notify(notificationId, builder.build());

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isNotificationPresent(int notID) {

        boolean bool = false;
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        StatusBarNotification[] notifications = notificationManager.getActiveNotifications();
        for (StatusBarNotification notification : notifications) {
            if (notification.getId() == notID) {
                bool = true;
            }
        }

        return bool;

    }

}



