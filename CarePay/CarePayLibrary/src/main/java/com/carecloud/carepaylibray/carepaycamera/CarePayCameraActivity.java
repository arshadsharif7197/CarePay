package com.carecloud.carepaylibray.carepaycamera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.OrientationEventListener;
import android.widget.FrameLayout;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.utils.ImageCaptureHelper;


public class CarePayCameraActivity extends AppCompatActivity implements CarePayCameraCallback {

    private static final String LOG_TAG = CarePayCameraActivity.class.getSimpleName();

    private FrameLayout preview;
    private OrientationEventListener orientationListener;
    private int orientation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_care_pay_camera);
        preview = (FrameLayout) findViewById(R.id.camera_preview);
        CarePayCameraView carePayCameraView = new CarePayCameraView(this);
        preview.addView(carePayCameraView);

        orientationListener = new OrientationEventListener(this,
                SensorManager.SENSOR_DELAY_NORMAL) {

            @Override
            public void onOrientationChanged(int orientation) {
                CarePayCameraActivity.this.orientation = orientation;
            }
        };

        if (orientationListener.canDetectOrientation() == true) {
            Log.v(LOG_TAG,"Can detect orientation");
            orientationListener.enable();
        } else {
            Log.v(LOG_TAG,"Cannot detect orientation");
            orientationListener.disable();
        }
    }

    @Override
    public void onCapturedSuccess(Bitmap bitmap) {
        setResult(RESULT_OK, null);
        ImageCaptureHelper.setOrientation(orientation);
        ImageCaptureHelper.setImageBitmap(bitmap);
        finish();
    }
}