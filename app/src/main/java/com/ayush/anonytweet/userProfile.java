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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class userProfile extends AppCompatActivity {

    ImageView profile_image, profile_image_btn, edit_name_btn;
    TextView email_view;
    EditText name;
    Button save;

    //Firebase Database
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    //Firebase Storage
    private FirebaseStorage storage;
    private StorageReference storageReference;

    FirebaseUser user;

    private Uri filepath;

    final static int  PICK_IMAGE_REQUEST = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        profile_image = (ImageView) findViewById(R.id.profile_image);
        profile_image_btn = (ImageView) findViewById(R.id.profile_image_btn);
        edit_name_btn = (ImageView) findViewById(R.id.name_btn);

        email_view = (TextView) findViewById(R.id.email);

        name = (EditText) findViewById(R.id.name);

        save = (Button) findViewById(R.id.btn_save);

        //Firebase storage init
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //Firebase database init
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        user = FirebaseAuth.getInstance().getCurrentUser();

        String user_name = null, email = null;
        Uri photoUrl = null;

        if(user != null){
            user_name = user.getDisplayName();
            photoUrl = user.getPhotoUrl();
            email = user.getEmail();
        }

        if (user_name != null){
            name.setText("" + user_name, TextView.BufferType.EDITABLE);
            name.setClickable(false);
        }
        else {
            name.setText("", TextView.BufferType.EDITABLE);
            name.setClickable(false);
        }

        if (photoUrl != null){
            Glide.with(getApplicationContext()).load(photoUrl.toString()).into(profile_image);
        }
        else {
            profile_image.setImageResource(R.drawable.no_profile_pic);
        }

        email_view.setText("" + email);

        profile_image_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        edit_name_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name.setClickable(true);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadPic();
                updateName(name.getText().toString());
                Toast.makeText(getApplicationContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(userProfile.this, DashBoard.class));
            }
        });

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
            StorageReference ref = storageReference.child("images/profilePics/" + user.getEmail() + "/" + temp);
            ref.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    // Get url to the uploaded content;
                    Uri imageUrl = taskSnapshot.getDownloadUrl();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(imageUrl)
                            .build();
                    user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d("up", "User profile updated.");
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(" ", "onFailure: ", e);
                }
            });

        }

    }

    private void updateName(String name){
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();
        user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("up", "User profile updated.");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Image set to imageView
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){

            filepath = data.getData();
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                profile_image.setImageBitmap(bitmap);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }

}
