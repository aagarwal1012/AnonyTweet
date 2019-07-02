package com.ayush.anonytweet;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import org.checkerframework.checker.androidresource.qual.LayoutRes;

public class AboutFragment extends AppCompatActivity {

    @LayoutRes
    private static final int FRAGMENT_ABOUT_LAYOUT = R.layout.fragment_about;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(FRAGMENT_ABOUT_LAYOUT);
    }
}
