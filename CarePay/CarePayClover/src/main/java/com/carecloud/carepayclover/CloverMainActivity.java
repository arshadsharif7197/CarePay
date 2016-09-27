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

    LinearLayout queueListLinearLayout,mainLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_clover);
        queueListLinearLayout= (LinearLayout) findViewById(R.id.queueListLinearLayout);
        mainLayout= (LinearLayout) findViewById(R.id.mainLayout);
        queueListLinearLayout.setVisibility(View.GONE);
    }

    public void onQueueClick(View view) {
        if(queueListLinearLayout.getVisibility()==View.VISIBLE){
            TranslateAnimation anim = new TranslateAnimation(0f,queueListLinearLayout.getWidth(), 0f, 0f);  // might need to review the docs
            anim.setDuration(1000);
            queueListLinearLayout.setVisibility( View.GONE);
            queueListLinearLayout.setAnimation(anim);

        }else{
            TranslateAnimation anim = new TranslateAnimation( queueListLinearLayout.getWidth(),0f, 0f, 0f);  // might need to review the docs
            anim.setDuration(1000);
            queueListLinearLayout.setVisibility(View.VISIBLE);
            queueListLinearLayout.setAnimation(anim);

        }
    }
}
