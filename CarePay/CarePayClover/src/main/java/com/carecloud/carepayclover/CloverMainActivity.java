package com.carecloud.carepayclover;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import com.carecloud.carepaylibray.activities.LibraryMainActivity;

public class CloverMainActivity extends AppCompatActivity {

    LinearLayout mainLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_clover);
        mainLayout= (LinearLayout) findViewById(R.id.mainLayout);
    }

}
