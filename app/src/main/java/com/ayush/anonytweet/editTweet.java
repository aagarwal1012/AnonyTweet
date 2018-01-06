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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class editTweet extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    FirebaseAuth auth;

    String tweetId, imagePath, text;

    ImageView tweet_image, tweet_image_btn;
    EditText tweet_message;
    Button save;

    private Uri filepath;

    final static int  PICK_IMAGE_REQUEST = 106;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tweet);

        //setting firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        auth = FirebaseAuth.getInstance();

        tweet_image = (ImageView) findViewById(R.id.tweet_image);
        tweet_image_btn = (ImageView) findViewById(R.id.edit_image_btn);

        tweet_message = (EditText) findViewById(R.id.edit_tweet);

        save = (Button) findViewById(R.id.btn_save);

        Bundle bundle = getIntent().getExtras();

        tweetId = bundle.getString("TweetId");
        imagePath = bundle.getString("ImageUrl");
        text = bundle.getString("Text");

        if (imagePath != null) {
            Glide.with(editTweet.this).load(imagePath).into(tweet_image);
        }

        if(text != null){
            tweet_message.setText(text, TextView.BufferType.EDITABLE);
        }

        tweet_image_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadPic();
                uploadText();
                startActivity(new Intent(getApplicationContext(), MyTweets.class));
            }
        });

    }

    private void uploadText() {
        databaseReference.child("Users").child(tweetId).child("Text ").setValue(tweet_message.getText().toString());
    }

    private void chooseImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent , "Select Picture") , PICK_IMAGE_REQUEST);

    }

    private void uploadPic() {

        if (filepath != null){
            final String temp = UUID.randomUUID().toString();
            StorageReference ref = storageReference.child("images/" + auth.getCurrentUser().getEmail() + "/" + temp);
            ref.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    // Get url to the uploaded content;
                    Uri imageUrl = taskSnapshot.getDownloadUrl();

                    if (imageUrl != null) {
                        databaseReference.child("Users").child(tweetId).child("Image Path").setValue(imageUrl.toString());
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(" ", "onFailure: ", e);
                }
            });

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Image set to imageView
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){

            filepath = data.getData();
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                tweet_image.setImageBitmap(bitmap);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }

}
