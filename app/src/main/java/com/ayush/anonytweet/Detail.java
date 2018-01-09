package com.ayush.anonytweet;

import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class Detail extends AppCompatActivity {

    public static final String EXTRA_POSITION = "position";
    private ProgressBar circular_progress;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(getApplicationContext(), DashBoard.class));
                onSupportNavigateUp();
            }
        });

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

        circular_progress = (ProgressBar) findViewById(R.id.circular_progress_detail);

        ImageView image = (ImageView) findViewById(R.id.image);
        //Progressing
        circular_progress.setVisibility(View.VISIBLE);
        //Loading image from Glide library.
        if (imagePath != null) {
            Glide.with(Detail.this).load(imagePath).into(image);
            circular_progress.setVisibility(View.INVISIBLE);
        }
        else {
            image.setImageResource(R.color.colorPrimary);
            circular_progress.setVisibility(View.INVISIBLE);
        }

    }
}
