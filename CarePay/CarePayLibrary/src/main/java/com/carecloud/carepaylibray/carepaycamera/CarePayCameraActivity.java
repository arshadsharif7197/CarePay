package com.carecloud.carepaylibray.carepaycamera;

import android.graphics.Bitmap;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.OrientationEventListener;
import android.widget.FrameLayout;

import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseActivity;
import com.carecloud.carepaylibray.utils.ImageCaptureHelper;


public class CarePayCameraActivity extends BaseActivity implements CarePayCameraCallback {

    private static final String LOG_TAG = CarePayCameraActivity.class.getSimpleName();

    private int orientation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_care_pay_camera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        CarePayCameraView carePayCameraView = new CarePayCameraView(this,
                (CarePayCameraPreview.CameraType) getIntent().getSerializableExtra("cameraType"));
        preview.addView(carePayCameraView);

        OrientationEventListener orientationListener = new OrientationEventListener(this,
                SensorManager.SENSOR_DELAY_NORMAL) {

            @Override
            public void onOrientationChanged(int orientation) {
                CarePayCameraActivity.this.orientation = orientation;
            }
        };

        if (orientationListener.canDetectOrientation()) {
            Log.v(LOG_TAG, "Can detect orientation");
            orientationListener.enable();
        } else {
            Log.v(LOG_TAG, "Cannot detect orientation");
            orientationListener.disable();
        }
    }

    @Override
    public void navigateToWorkflow(WorkflowDTO workflowDTO) {

    }

    @Override
    public void onCapturedSuccess(Bitmap bitmap) {
        setResult(RESULT_OK, null);
        ImageCaptureHelper.setImageBitmap(bitmap);
        finish();
    }

    @Override
    public void onCaptureFail() {
        setResult(RESULT_CANCELED);
        finish();
    }
}
