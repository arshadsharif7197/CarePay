package com.carecloud.carepay.practice.library.payments;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;

/*
 * Created by Jahirul Bhuiyan on 10/17/2016.
 * This activity use for flipping the screen.
 * */

public class RotateActivity extends AppCompatActivity {

    private TextView rotateTitleTextView;
    private TextView rotateSubTitleTextView;
    private TextView goBackTextview;

    private double totalAmount;
    private double  previousBalance;
    private double  insuranceCoPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotate);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        rotateTitleTextView = (TextView) findViewById(R.id.rotateTitleTextView);
        rotateSubTitleTextView = (TextView) findViewById(R.id.rotateSubTitleTextView);
        goBackTextview = (TextView) findViewById(R.id.goBackTextview);
        Intent intent = getIntent();
        if (intent.hasExtra("rotate_title")) {
            String rotateTitle = intent.getStringExtra("rotate_title");
            rotateTitleTextView.setText(rotateTitle);
        }
        if (intent.hasExtra("rotate_sub_title")) {
            String rotateSubTitle = intent.getStringExtra("rotate_sub_title");
            rotateSubTitleTextView.setText(rotateSubTitle);
        }
        if (intent.hasExtra("total_pay_balance")) {
            totalAmount = intent.getDoubleExtra("total_pay_balance", 20.00);
        }
        if (intent.hasExtra("previous_balance")) {
            previousBalance = intent.getDoubleExtra("previous_balance", 15.00);
        }
        if (intent.hasExtra("insurance_co_pay_balance")) {
            insuranceCoPay = intent.getDoubleExtra("insurance_co_pay_balance", 5.00);
        }
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            goBackTextview.setVisibility(View.GONE);
        } else {
            goBackTextview.setVisibility(View.VISIBLE);
            goBackTextview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            Intent responsibilityIntent = new Intent(RotateActivity.this, PatientResponsibilityActivity.class);
            responsibilityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            responsibilityIntent.putExtra("total_pay_balance", totalAmount);
            responsibilityIntent.putExtra("previous_balance", previousBalance);
            responsibilityIntent.putExtra("insurance_co_pay_balance", insuranceCoPay);
            startActivity(responsibilityIntent);
            this.finish();
        } else {
           /* Intent checkedInIntent = new Intent(RotateActivity.this, CloverMainActivity.class);
            checkedInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(checkedInIntent);*/
            this.finish();
        }
    }

}
