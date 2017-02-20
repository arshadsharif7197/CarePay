package com.carecloud.carepay.practice.library.payments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;


public class PatientResponsibilityActivity extends AppCompatActivity {
    double totalAmount, previousBalance, insuranceCoPay;

    TextView responsTotalTextView, responsPrevBalanceTextView, responsCoPayTTextView,responsPayButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_responsibility);
        responsTotalTextView = (TextView) findViewById(R.id.responsTotalTextView);
        responsPrevBalanceTextView = (TextView) findViewById(R.id.responsPrevBalanceTextView);
        responsCoPayTTextView = (TextView) findViewById(R.id.responsCoPayTTextView);
        responsPayButton= (TextView) findViewById(R.id.responsPayButton);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Intent intent = getIntent();
        if (intent.hasExtra("total_pay_balance")) {
            totalAmount = intent.getDoubleExtra("total_pay_balance", 20.00);
        }
        if (intent.hasExtra("previous_balance")) {
            previousBalance = intent.getDoubleExtra("previous_balance", 15.00);
        }
        if (intent.hasExtra("insurance_co_pay_balance")) {
            insuranceCoPay = intent.getDoubleExtra("insurance_co_pay_balance", 5.00);
        }

        responsTotalTextView.setText(String.format( "$%.2f", totalAmount ));
        responsPrevBalanceTextView.setText(String.format( "$%.2f", previousBalance ));
        responsCoPayTTextView.setText(String.format( "$%.2f", insuranceCoPay ));

        responsPayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent responsibilityIntent = new Intent(PatientResponsibilityActivity.this, CloverPaymentActivity.class);
                responsibilityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                responsibilityIntent.putExtra("total_pay",totalAmount);
                startActivity(responsibilityIntent);*/
            }
        });
    }
}
