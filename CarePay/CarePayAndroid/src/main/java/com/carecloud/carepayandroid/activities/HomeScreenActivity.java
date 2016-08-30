package com.carecloud.carepayandroid.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.carecloud.carepayandroid.R;
import com.carecloud.carepaylibray.fragments.HomeScreenFragment;
import com.carecloud.carepaylibray.fragments.SignInFragment;

public class HomeScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        if(savedInstanceState == null) {
          HomeScreenFragment mHomeScreenFragment = new HomeScreenFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, mHomeScreenFragment,"HomeScreen")
                    .commit();
        }
    }
}
