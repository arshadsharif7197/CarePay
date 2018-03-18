package com.carecloud.carepaylibray.qrcodescanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;

import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseActivity;
import com.google.zxing.Result;

public class ScannerActivity extends BaseActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView zxingcannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_scanner);
        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        zxingcannerView = new ZXingScannerView(this);
        contentFrame.addView(zxingcannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        zxingcannerView.setResultHandler(this);
        zxingcannerView.startCamera();
    }

    @Override
    public void navigateToWorkflow(WorkflowDTO workflowDTO) {

    }

    @Override
    public void onPause() {
        super.onPause();
        zxingcannerView.stopCamera();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void handleResult(Result rawResult) {
        Intent intent = new Intent();
        intent.putExtra("SCAN_RESULT",rawResult.getText());
        ScannerActivity.this.setResult(RESULT_OK, intent);
        finish();
    }
}
