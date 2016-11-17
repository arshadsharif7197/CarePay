package com.carecloud.carepay.practice.library.customdialog;

/**
 * Created by prem_mourya on 11/16/2016.
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraCallback;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraPreview;

import java.io.ByteArrayOutputStream;

public class ScanDocDialogActivity extends Activity implements OnClickListener, CarePayCameraCallback {


    private Button scanImageButton;
    private Button scanClearImageButton;
    private LinearLayout closeViewLayout;
    private ImageView cameraCaptureImage;
    private CarePayCameraPreview carePayCameraPreview;
    private byte[] cameraImageBitmap;
    private boolean isCapturing = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_scan_camera_doc);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        onInitialization();
    }

    private void onInitialization() {

        carePayCameraPreview = (CarePayCameraPreview) findViewById(R.id.camera_preview);

        scanImageButton = (Button) findViewById(R.id.scan_button);
        scanImageButton.setOnClickListener(this);

        scanClearImageButton = (Button) findViewById(R.id.clear_capture_image_button);
        scanClearImageButton.setOnClickListener(this);

        closeViewLayout = (LinearLayout) findViewById(R.id.closeViewLayout);
        closeViewLayout.setOnClickListener(this);

        cameraCaptureImage = (ImageView) findViewById(R.id.cameraImageView);
        cameraCaptureImage.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if (viewId == R.id.scan_button) {

            if (isCapturing) {
                if (carePayCameraPreview != null) {
                    carePayCameraPreview.takePicturePractice();
                }
            } else {
                onSaveImage();
            }


        } else if (viewId == R.id.clear_capture_image_button) {
            setupImageCameraCapture();
        } else if (viewId == R.id.closeViewLayout) {
            finish();
        }

    }


    @Override
    public void onCapturedSuccess(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
        cameraImageBitmap = stream.toByteArray();
        setupImageDisplay();

    }

    private void setupImageCameraCapture() {
        cameraCaptureImage.setVisibility(View.INVISIBLE);
        carePayCameraPreview.setVisibility(View.VISIBLE);
        try {
            carePayCameraPreview.getCameraObject().startPreview();
        } catch (Exception excetion) {
            Log.e("CameraRND", "Error setting camera start: " + excetion.getMessage());
        }
        scanImageButton.setText("Scan");
        cameraImageBitmap = null;
        isCapturing = true;
        scanClearImageButton.setEnabled(false);
    }

    private void setupImageDisplay() {

        Bitmap bitmap = BitmapFactory.decodeByteArray(cameraImageBitmap, 0, cameraImageBitmap.length);
        cameraCaptureImage.setImageBitmap(bitmap);
        try {
            carePayCameraPreview.getCameraObject().stopPreview();
        } catch (Exception excetion) {
            Log.e("CameraRND", "Error setting camera stop: " + excetion.getMessage());
        }
        carePayCameraPreview.setVisibility(View.INVISIBLE);
        cameraCaptureImage.setVisibility(View.VISIBLE);
        scanImageButton.setText("Save");
        isCapturing = false;
        scanClearImageButton.setEnabled(true);
    }

    private void onSaveImage() {
        Intent intent = new Intent();
        intent.putExtra("data", cameraImageBitmap);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        carePayCameraPreview.getCameraObject().release();
    }
}
