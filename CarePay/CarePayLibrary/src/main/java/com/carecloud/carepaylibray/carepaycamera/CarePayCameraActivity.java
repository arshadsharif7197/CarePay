package com.carecloud.carepaylibray.carepaycamera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.utils.ImageCaptureHelper;

import java.io.ByteArrayOutputStream;

public class CarePayCameraActivity extends AppCompatActivity implements CarePayCameraCallback {
    FrameLayout preview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_care_pay_camera);
        preview = (FrameLayout) findViewById(R.id.camera_preview);
        CarePayCameraView carePayCameraView = new CarePayCameraView(this);
        preview.addView(carePayCameraView);
    }

    @Override
    public void onCapturedSuccess(Bitmap bitmap) {
        setResult(RESULT_OK, null);
        ImageCaptureHelper.setImageBitmap(bitmap);
        finish();
    }
}
