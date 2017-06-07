package com.carecloud.carepaylibray.media;

import android.content.Intent;
import android.os.Bundle;

import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseActivity;

import org.apache.commons.lang3.NotImplementedException;

import java.io.File;

/**
 * Created by lmenendez on 6/2/17
 */

public class MediaCameraActivity extends BaseActivity implements MediaCameraFragment.MediaCameraCallback {

    @Override
    protected void onCreate(Bundle icicle){
        super.onCreate(icicle);
        setContentView(R.layout.activity_scanner);

        displayCameraFragment("img_"+System.currentTimeMillis());
    }

    @Override
    public void navigateToWorkflow(WorkflowDTO workflowDTO) {
        throw new NotImplementedException("Media Camera Activity does not handle navigating to work flows");
    }

    @Override
    public void onMediaFileCreated(File file) {
        Intent data = new Intent();
        data.putExtra(MediaScannerPresenter.DATA_CAPTURED_IMAGE_KEY, file.getAbsolutePath());
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void displayCameraFragment(String tempFile) {
        MediaCameraFragment cameraFragment = MediaCameraFragment.newInstance(tempFile);
        replaceFragment(R.id.content_frame, cameraFragment, false);
    }
}
