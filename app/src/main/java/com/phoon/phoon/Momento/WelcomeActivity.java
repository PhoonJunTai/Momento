package com.phoon.phoon.Momento;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import static java.lang.Thread.sleep;


/**
 * Created by Hello on 13/4/2017.
 */

public class WelcomeActivity extends AppCompatActivity {
    protected boolean _active = true;
    protected int _splashTime = 1000;
    ImageView imageView;
    TextView title,slogan;
    Animation animSlide,animFade,animUp;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Refer the View
        imageView = (ImageView) findViewById(R.id.imageView1);
        title = (TextView) findViewById(R.id.titletextViewStartScreenText);
        slogan = (TextView) findViewById(R.id.textViewSlogan);

// Load the animation
        animFade = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fadeout);

        // Start the animation
        imageView.startAnimation(animFade);
        title.setAnimation(animFade);
        slogan.setAnimation(animFade);


        animFade.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                finish();
                Intent i = new Intent(getBaseContext(),MainActivity.class);
                startActivity(i);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });




    }
}
