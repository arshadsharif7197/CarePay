package com.carecloud.carepaylibray.signinsignup;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.constants.CarePayConstants;
import com.carecloud.carepaylibray.signinsignup.fragments.SigninFragment;
import com.carecloud.carepaylibray.utils.DateUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;

/**
 * Created by harish_revuri on 9/7/2016.
 * Activity supporting Signin and Sign-up
 */
public class SigninSignupActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_signup);

        FragmentManager mFragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            mFragmentManager.beginTransaction()
                    .replace(R.id.layoutSigninSignup, new SigninFragment(), SigninFragment.class.getSimpleName())
                    .commit();
        }

        // test
        SimpleDateFormat formatter = new SimpleDateFormat(CarePayConstants.APPOINTMENT_DATE_TIME_FORMAT, Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.set(1979, 6, 20);
        Date date = calendar.getTime();
        Log.v(LOG_TAG, "DOB: " + DateUtil.getInstance().formatToDateOfBirth(this, date));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            Log.v(LOG_TAG, "home pressed");
            onBackPressed();
            return true;
        }
        return false;
    }
}