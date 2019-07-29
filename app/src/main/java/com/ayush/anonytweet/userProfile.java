package com.ayush.anonytweet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.checkerframework.checker.androidresource.qual.IdRes;
import org.checkerframework.checker.androidresource.qual.LayoutRes;

import java.io.IOException;
import java.util.UUID;

public class userProfile extends AppCompatActivity {

    private final static int PICK_IMAGE_REQUEST = 10;

    private static final @LayoutRes int ACTIVITY_USER_PROFILE_LAYOUT = R.layout.activity_user_profile;

    private static final @IdRes int PROFILE_IMAGE_ID = R.id.profile_image;
    private static final @IdRes int PROFILE_IMAGE_BTN_ID = R.id.profile_image_btn;
    private static final @IdRes int NAME_BTN_ID = R.id.name_btn;
    private static final @IdRes int EMAIL_ID = R.id.email;
    private static final @IdRes int NAME_ID = R.id.name;
    private static final @IdRes int BTN_SAVE_ID = R.id.btn_save;

    private ImageView profile_image;
    private ImageView profile_image_btn;
    private ImageView edit_name_btn;
    private TextView email_view;
    private EditText name;
    private Button save;
    private FirebaseUser user;
    private StorageReference storageReference;
    private Uri filepath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ACTIVITY_USER_PROFILE_LAYOUT);

        profile_image = findViewById(PROFILE_IMAGE_ID);
        profile_image_btn = findViewById(PROFILE_IMAGE_BTN_ID);
        edit_name_btn = findViewById(NAME_BTN_ID);

        email_view = findViewById(EMAIL_ID);

        name = findViewById(NAME_ID);

        save = findViewById(BTN_SAVE_ID);

        //Firebase storage init
        //Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //Firebase database init
        //Firebase Database
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference();

        user = FirebaseAuth.getInstance().getCurrentUser();

        String user_name = null, email = null;
        Uri photoUrl = null;

        if (user != null) {
            user_name = user.getDisplayName();
            photoUrl = user.getPhotoUrl();
            email = user.getEmail();
        }

        if (user_name != null) {
            name.setText("" + user_name, TextView.BufferType.EDITABLE);
            name.setClickable(false);
        } else {
            name.setText("", TextView.BufferType.EDITABLE);
            name.setClickable(false);
        }

        if (photoUrl != null) {
            Glide.with(getApplicationContext()).load(photoUrl.toString()).into(profile_image);
        } else {
            profile_image.setImageResource(R.drawable.no_profile_pic);
        }

        email_view.setText("" + email);

        profile_image_btn.setOnClickListener(view -> chooseImage());

        edit_name_btn.setOnClickListener(view -> name.setClickable(true));

        save.setOnClickListener(view -> {
            uploadPic();
            updateName(name.getText().toString());
            Toast.makeText(getApplicationContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(userProfile.this, DashBoard.class));
        });

    }

    private void chooseImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }

    private void uploadPic() {

        if (filepath != null) {
            final String temp = UUID.randomUUID().toString();
            StorageReference ref = storageReference.child("images/profilePics/" + user.getEmail() + "/" + temp);
            ref.putFile(filepath).addOnSuccessListener(taskSnapshot -> {

                // Get url to the uploaded content;
                Uri imageUrl = taskSnapshot.getDownloadUrl();
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setPhotoUri(imageUrl)
                        .build();
                user.updateProfile(profileUpdates).addOnCompleteListener(task -> Log.d("up", "User profile updated."));

            }).addOnFailureListener(e -> Log.d(" ", "onFailure: ", e));

        }

    }

    private void updateName(String name) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();
        user.updateProfile(profileUpdates).addOnCompleteListener(task -> Log.d("up", "User profile updated."));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Image set to imageView
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            filepath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                profile_image.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
