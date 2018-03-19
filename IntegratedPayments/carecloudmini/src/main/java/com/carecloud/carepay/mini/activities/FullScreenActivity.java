package com.carecloud.carepay.mini.activities;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.carecloud.carepay.mini.interfaces.ApplicationHelper;
import com.carecloud.carepay.mini.services.carepay.RestCallServiceHelper;
import com.clover.sdk.util.CustomerMode;

/**
 * Created by lmenendez on 7/20/17
 */

public abstract class FullScreenActivity extends AppCompatActivity {
    private static final int FULLSCREEN_VALUE = 0x10000000;
    private boolean customerMode;

    @Override
    protected void onCreate(Bundle icicle){
        super.onCreate(icicle);
        if(isScreenLarge()) {
            // width > height, better to use Landscape
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        setSystemUiFullscreen();
    }

    @Override
    protected void onResume(){
        super.onResume();
        setSystemUiFullscreen();
        CustomerMode.enable(this);
        customerMode = true;
    }

    private void setSystemUiFullscreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.KEEP_SCREEN_ON
                | FULLSCREEN_VALUE;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void setSystemUiShowNavbar(){
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.KEEP_SCREEN_ON;
        decorView.setSystemUiVisibility(uiOptions);
    }

    protected ApplicationHelper getApplicationHelper(){
        return (ApplicationHelper) getApplication();
    }

    protected RestCallServiceHelper getRestHelper(){
        return getApplicationHelper().getRestHelper();
    }


    public boolean isCustomerMode() {
        return customerMode;
    }

    protected void toggleCustomerMode(){
        if(isCustomerMode()){
            setSystemUiShowNavbar();
            CustomerMode.disable(this);
            customerMode = false;
        }else{
            setSystemUiFullscreen();
            CustomerMode.enable(this);
            customerMode = true;
        }
    }

    private boolean isScreenLarge() {
        final int screenSize = getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK;
        return screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE
                || screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

}
