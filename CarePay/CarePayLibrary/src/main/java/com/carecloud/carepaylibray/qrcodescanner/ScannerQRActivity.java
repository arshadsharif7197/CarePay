package com.carecloud.carepaylibray.qrcodescanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import com.carecloud.carepaylibrary.R;
import com.google.zxing.Result;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Reads a qr code and returned to activity
 * @since 1.0
 * */
public class ScannerQRActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView zXingScannerView;
    public static final String EXTRA_QR_CODE = "QR_CODE";
    private String headerTitle;

    public ScannerQRActivity() {
    }

    public ScannerQRActivity(String title) {
        headerTitle = title;
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(com.carecloud.carepaylibrary.R.layout.activity_qr_scanner);
        setupToolbar();
        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        zXingScannerView = new ZXingScannerView(this);
        contentFrame.addView(zXingScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        zXingScannerView.setResultHandler(this);
        zXingScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        zXingScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        Intent intent = new Intent();
        intent.putExtra("SCAN_RESULT",rawResult.getText());
        ScannerQRActivity.this.setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Initializes toolbar of layout
     *
     * */
    public void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarScanner);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        if(ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
