package com.carecloud.carepayandroid.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.carecloud.carepayandroid.R;
import com.carecloud.carepaylibray.fragments.SignInFragment;

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);


        if(savedInstanceState == null) {

            SignInFragment fragment = new SignInFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.signin_frame_holder, fragment,"signin")
                    .commit();
        }
    }
}
