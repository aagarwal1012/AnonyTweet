package com.ayush.anonytweet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ayush.anonytweet.Classes.usersLiked;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.checkerframework.checker.androidresource.qual.IdRes;
import org.checkerframework.checker.androidresource.qual.LayoutRes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Tweet extends AppCompatActivity {

    private static final @LayoutRes int ACTIVITY_TWEET_LAYOUT = R.layout.activity_tweet;

    private static final @IdRes int SELECTED_IMAGE_ID = R.id.selectedImage;
    private static final @IdRes int INPUT_TEXT_ID = R.id.input_text;
    private static final @IdRes int CHOOSE_IMAGE_ID = R.id.chooseImage;
    private static final @IdRes int BTN_TWEET_ID = R.id.btn_tweet;
    private static final @IdRes int CIRCULAR_PROGRESS_ID = R.id.circular_progress;

    private final int PICK_IMAGE_REQUEST = 7;
    private final String TAG = "Tweet";
    private final List<String> likedUsers = new ArrayList<>();
    private ImageView selectedImage;
    private TextView text;
    private Button chooseImage;
    private Button tweet;
    private String dataId;
    private Uri imageUrl;
    private FirebaseUser user;
    private com.ayush.anonytweet.Classes.usersLiked usersLiked;
    //Progress bar
    private ProgressBar circularProgressBar;
    private DatabaseReference mDatabaseReference;
    private StorageReference storageReference;
    private Uri filepath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ACTIVITY_TWEET_LAYOUT);

        selectedImage = findViewById(SELECTED_IMAGE_ID);
        text = findViewById(INPUT_TEXT_ID);
        chooseImage = findViewById(CHOOSE_IMAGE_ID);
        tweet = findViewById(BTN_TWEET_ID);

        //Progress Bar inti
        circularProgressBar = findViewById(CIRCULAR_PROGRESS_ID);

        //Firebase storage init
        //Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //Firebase database init
        //Firebase Database
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        dataId = mDatabaseReference.child("Users").push().getKey();

        //Get user email id and User-id;
        user = FirebaseAuth.getInstance().getCurrentUser();

        //usersLiked
        //likedUsers.add("0") ;
        usersLiked = new usersLiked(likedUsers, dataId);

        chooseImage.setOnClickListener(view -> chooseImage());

        tweet.setOnClickListener(view -> {
            circularProgressBar.setVisibility(View.VISIBLE);

            tweet();

            Intent intent = new Intent(Tweet.this, DashBoard.class);

            circularProgressBar.setVisibility(View.INVISIBLE);

            startActivity(intent);


        });

    }

    private void tweet() {

        if (filepath != null) {
            final String temp = UUID.randomUUID().toString();
            StorageReference ref = storageReference.child("images/" + user.getEmail() + "/" + temp);
            ref.putFile(filepath).addOnSuccessListener(taskSnapshot -> {

                // Get url to the uploaded content;
                imageUrl = taskSnapshot.getDownloadUrl();

                if (imageUrl != null) {
                    mDatabaseReference.child("Users").child(dataId).child("Image Path").setValue(imageUrl.toString());
                }

                mDatabaseReference.child("Users").child(dataId).child("Email").setValue(user.getEmail());
                mDatabaseReference.child("Users").child(dataId).child("Data Id").setValue(dataId);
                mDatabaseReference.child("Users").child(dataId).child("Number of Likes").setValue("0");

                if (text.getText() != null) {
                    mDatabaseReference.child("Users").child(dataId).child("Text ").setValue(text.getText().toString());
                } else {
                    mDatabaseReference.child("Users").child(dataId).child("Text ").setValue(null);
                }

                // String to uri   uri = uri.parse(Stringuri);

            }).addOnFailureListener(e -> Log.d(TAG, "onFailure: ", e));

            mDatabaseReference.child("Likes").child(dataId).setValue(usersLiked);

        } else {
            if (text.getText() != null && !text.getText().toString().equals("")) {
                mDatabaseReference.child("Likes").child(dataId).setValue(usersLiked);
                mDatabaseReference.child("Users").child(dataId).child("Image Path").setValue(null);
                mDatabaseReference.child("Users").child(dataId).child("Email").setValue(user.getEmail());
                mDatabaseReference.child("Users").child(dataId).child("Data Id").setValue(dataId);
                mDatabaseReference.child("Users").child(dataId).child("Text ").setValue(text.getText().toString());
                mDatabaseReference.child("Users").child(dataId).child("Number of Likes").setValue("0");
            }
        }

    }

    private void chooseImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Image set to imageView
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            filepath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                chooseImage.setVisibility(View.INVISIBLE);
                selectedImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
