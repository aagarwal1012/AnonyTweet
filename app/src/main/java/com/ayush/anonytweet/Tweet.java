package com.ayush.anonytweet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.IOException;
import java.util.UUID;

public class Tweet extends AppCompatActivity {

    ImageView selectedImage;
    TextView text;
    Button chooseImage, tweet;

    //Progress bar
    private ProgressBar circularProgressBar;

    //Firebase Database
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    //Firebase Storage
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private Uri filepath;

    private final int PICK_IMAGE_REQUEST = 7;
    private final String TAG = "Tweet";

    //private User currentUser; //save the data in current user

    String dataId;

    Uri imageUrl;

    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);

        selectedImage = (ImageView) findViewById(R.id.selectedImage);
        text = (TextView) findViewById(R.id.input_text);
        chooseImage = (Button) findViewById(R.id.chooseImage);
        tweet = (Button) findViewById(R.id.btn_tweet);

        //Progress Bar inti
        circularProgressBar = (ProgressBar) findViewById(R.id.circular_progress);

        //Firebase storage init
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //Firebase database init
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        dataId = mDatabaseReference.child("Users").push().getKey();

        //Get user email id and User-id;
        user = FirebaseAuth.getInstance().getCurrentUser();

        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        tweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                circularProgressBar.setVisibility(View.VISIBLE);

                tweet();

                Intent intent = new Intent(Tweet.this, DashBoard.class);

                circularProgressBar.setVisibility(View.INVISIBLE);

                startActivity(intent);


            }
        });

    }

    private void tweet() {

        if (filepath != null){
            final String temp = UUID.randomUUID().toString();
            StorageReference ref = storageReference.child("images/" + user.getEmail() + "/" + temp);
            ref.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    // Get url to the uploaded content;
                    imageUrl = taskSnapshot.getDownloadUrl();

                    if (imageUrl != null) {
                        mDatabaseReference.child("Users").child(dataId).child("Image Path").setValue(imageUrl.toString());
                    }

                    mDatabaseReference.child("Users").child(dataId).child("Email").setValue(user.getEmail());
                    mDatabaseReference.child("Users").child(dataId).child("Data Id").setValue(dataId);
                    mDatabaseReference.child("Users").child(dataId).child("Number of Likes").setValue("0");

                    if (text.getText() != null){
                        mDatabaseReference.child("Users").child(dataId).child("Text ").setValue(text.getText().toString());
                    }
                    else {
                        mDatabaseReference.child("Users").child(dataId).child("Text ").setValue(null);
                    }

                    // String to uri   uri = uri.parse(Stringuri);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: ", e);
                }
            });

        }
        else {
            if (text.getText() != null && text.getText().toString().equals("") == false){
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
        startActivityForResult(Intent.createChooser(intent , "Select Picture") , PICK_IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Image set to imageView
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){

            filepath = data.getData();
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                chooseImage.setVisibility(View.INVISIBLE);
                selectedImage.setImageBitmap(bitmap);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
