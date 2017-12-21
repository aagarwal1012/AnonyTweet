package com.ayush.anonytweet;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class Detail extends AppCompatActivity {

    public static final String EXTRA_POSITION = "position";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Set Collapsing Toolbar layout to the screen
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        // Set title of Detail page
        collapsingToolbar.setTitle(" ");

        Bundle bundle = getIntent().getExtras();

        int position = bundle.getInt(EXTRA_POSITION);
        String text, imagePath;
        text = bundle.getString("Text");
        imagePath = bundle.getString("ImagePath");

        TextView message = (TextView) findViewById(R.id.view_message);
        message.setText(text);

        ImageView image = (ImageView) findViewById(R.id.image);
        //Loading image from Glide library.
        if (imagePath != null) {
            Glide.with(Detail.this).load(imagePath).into(image);
        }
        else {
            image.setImageResource(R.color.colorPrimary);
        }

    }
}
