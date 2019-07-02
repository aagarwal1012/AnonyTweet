package com.ayush.anonytweet;

import android.content.Intent;

import com.daimajia.androidanimations.library.Techniques;
import com.google.firebase.database.FirebaseDatabase;
import com.viksaa.sssplash.lib.activity.AwesomeSplash;
import com.viksaa.sssplash.lib.cnst.Flags;
import com.viksaa.sssplash.lib.model.ConfigSplash;

public class splashScreen extends AwesomeSplash {

    private static final int LOGO_DRAWABLE = R.drawable.cutmypic3;

    private static final int SPLASH_SCREEN_BACKGROUND_COLOR = R.color.splash_screen_background;
    private static final int WHITE_COLOR = R.color.white;
    private static final int FACEBOOK_BLUE_COLOR = R.color.com_facebook_blue;

    @Override
    public void initSplash(ConfigSplash configSplash) {
        /* you don't have to override every property */

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        //Customize Circular Reveal
        configSplash.setBackgroundColor(SPLASH_SCREEN_BACKGROUND_COLOR); //any color you want form colors.xml
        configSplash.setAnimCircularRevealDuration(2000); //int ms
        configSplash.setRevealFlagX(Flags.REVEAL_RIGHT);  //or Flags.REVEAL_LEFT
        configSplash.setRevealFlagY(Flags.REVEAL_BOTTOM); //or Flags.REVEAL_TOP

        //Choose LOGO OR PATH; if you don't provide String value for path it's logo by default

        //Customize Logo
        configSplash.setLogoSplash(LOGO_DRAWABLE);//or any other drawable
        configSplash.setAnimLogoSplashDuration(3000); //int ms
        configSplash.setAnimLogoSplashTechnique(Techniques.FadeIn); //choose one form Techniques (ref: https://github.com/daimajia/AndroidViewAnimations)


        //Customize Path
        //configSplash.setPathSplash(Constants.DROID_LOGO); //set path String
        configSplash.setOriginalHeight(400); //in relation to your svg (path) resource
        configSplash.setOriginalWidth(400); //in relation to your svg (path) resource
        configSplash.setAnimPathStrokeDrawingDuration(3000);
        configSplash.setPathSplashStrokeSize(3); //I advise value be <5
        configSplash.setPathSplashStrokeColor(WHITE_COLOR); //any color you want form colors.xml
        configSplash.setAnimPathFillingDuration(3000);
        configSplash.setPathSplashFillColor(FACEBOOK_BLUE_COLOR); //path object filling color


        //Customize Title
        configSplash.setTitleSplash("AnonyTweet");
        configSplash.setTitleTextColor(WHITE_COLOR);
        configSplash.setTitleTextSize(30f); //float value
        configSplash.setAnimTitleDuration(3000);
        configSplash.setAnimTitleTechnique(Techniques.FlipInX);
        //configSplash.setTitleFont("fonts/myfont.ttf"); //provide string to your font located in assets/fonts/
    }

    @Override
    public void animationsFinished() {
        startActivity(new Intent(getApplicationContext(), login.class));
    }
}
