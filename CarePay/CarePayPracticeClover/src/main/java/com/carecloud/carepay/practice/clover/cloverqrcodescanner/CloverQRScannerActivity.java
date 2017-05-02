package com.carecloud.carepay.practice.clover.cloverqrcodescanner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.clover.sdk.internal.util.Strings;
import com.clover.sdk.v1.Intents;
import com.clover.sdk.v3.scanner.BarcodeResult;
import com.clover.sdk.v3.scanner.BarcodeScanner;


/**
 * Created by Jahirul Bhuiyan on 11/14/2016.
 * This class using for scan QR code using Clover SDK
 * Implement BroadcastReceiver for clover BarcodeResult.INTENT_ACTION
 */

public class CloverQRScannerActivity extends AppCompatActivity {

    /**
     * BroadcastReceiver for the clover barcode result
     */
    private BroadcastReceiver barcodeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BarcodeResult barcodeResult = new BarcodeResult(intent);

            if (barcodeResult.isBarcodeAction() && !Strings.isNullOrEmpty( barcodeResult.getBarcode())) {
                String barcode = barcodeResult.getBarcode();
                intent = new Intent();
                intent.putExtra("SCAN_RESULT",barcode);
                CloverQRScannerActivity.this.setResult(RESULT_OK, intent);
                //Toast.makeText(CloverQRScannerActivity.this,"QR code result: "+barcode,Toast.LENGTH_LONG).show();
                finish();
            }
        }
    };

    /**
     * initialization BarcodeResult intent
     * @param savedInstanceState Bundle
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = new Bundle();
        extras.putBoolean(Intents.EXTRA_LED_ON, false);
        extras.putBoolean(Intents.EXTRA_SCAN_QR_CODE, true);
        extras.putBoolean(Intents.EXTRA_SCAN_1D_CODE, true);
        BarcodeScanner barcodeScanner = new BarcodeScanner(this);
        barcodeScanner.executeStartScan(extras);

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerBarcodeScanner();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterBarcodeScanner();
    }

    /**
     * register BroadcastReceiver
     */
    private void registerBarcodeScanner() {
        registerReceiver(barcodeReceiver, new IntentFilter(BarcodeResult.INTENT_ACTION));
    }

    /**
     * unregister BroadcastReceiver
     */
    private void unregisterBarcodeScanner() {
        unregisterReceiver(barcodeReceiver);
    }
}
