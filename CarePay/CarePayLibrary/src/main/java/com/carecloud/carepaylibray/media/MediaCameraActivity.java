package com.carecloud.carepaylibray.media;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.CarePayLayoutInflaterFactory;

/**
 * Created by lmenendez on 6/2/17
 */

public class MediaCameraActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle icicle){
        LayoutInflater inflater = LayoutInflater.from(this);
        inflater.setFactory2(new CarePayLayoutInflaterFactory(this));
        super.onCreate(icicle);
        setContentView(R.layout.activity_scanner);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, new MediaCameraFragment());
        transaction.commit();
    }

}
