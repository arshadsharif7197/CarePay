package com.carecloud.carepay.practice.library.payments.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.payments.fragments.PartialPaymentDialog;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;


/**
 * Created by prem_mourya on 10/4/2016.
 */
public class PracticePartialPaymentDialog extends PartialPaymentDialog {
    private EditText enterPartialAmountEditText;
    private Context context;
    private PaymentsModel paymentsDTO;

    /**
     *
     * @param context the context
     * @param paymentsDTO the paymentDTO
     */
    public PracticePartialPaymentDialog(Context context, PaymentsModel paymentsDTO) {
        super(context, paymentsDTO);
        this.context = context;
        this.paymentsDTO = paymentsDTO;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Gets linearlayout
        LinearLayout containerLayout = (LinearLayout)findViewById(R.id.containerLayout);
        ViewGroup.LayoutParams containerLayoutParams = (ViewGroup.LayoutParams)containerLayout.getLayoutParams();
        containerLayoutParams.height = 700;
        containerLayoutParams.width = 600;
        containerLayout.setOrientation(LinearLayout.HORIZONTAL);
        containerLayout.setLayoutParams(containerLayoutParams);
        onSetListener();

        LinearLayout keyBoardLinearLayout = (LinearLayout) findViewById(com.carecloud.carepaylibrary.R.id.keyBoardLayout);
        keyBoardLinearLayout.setVisibility(View.VISIBLE);
        LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        buttonLayoutParams.setMargins(0, 30, 0, 30);
        Button payPartialButton = (Button) findViewById(R.id.payPartialButton);
        payPartialButton.setPadding(5,0,5,0);
        payPartialButton.setBackground(context.getResources().getDrawable(R.drawable.bg_yellow_overlay));
        payPartialButton.setLayoutParams(buttonLayoutParams);
    }

    /**
     * set listner for keypad components .
     */
    private void onSetListener() {
        (findViewById(com.carecloud.carepaylibrary.R.id.key_one)).setOnClickListener(this);
        (findViewById(com.carecloud.carepaylibrary.R.id.key_two)).setOnClickListener(this);
        (findViewById(com.carecloud.carepaylibrary.R.id.key_three)).setOnClickListener(this);
        (findViewById(com.carecloud.carepaylibrary.R.id.key_four)).setOnClickListener(this);
        (findViewById(com.carecloud.carepaylibrary.R.id.key_five)).setOnClickListener(this);
        (findViewById(com.carecloud.carepaylibrary.R.id.key_six)).setOnClickListener(this);
        (findViewById(com.carecloud.carepaylibrary.R.id.key_seven)).setOnClickListener(this);
        (findViewById(com.carecloud.carepaylibrary.R.id.key_eighth)).setOnClickListener(this);
        (findViewById(com.carecloud.carepaylibrary.R.id.key_nine)).setOnClickListener(this);
        (findViewById(com.carecloud.carepaylibrary.R.id.key_zero)).setOnClickListener(this);
        (findViewById(com.carecloud.carepaylibrary.R.id.key_blank)).setOnClickListener(this);
        (findViewById(com.carecloud.carepaylibrary.R.id.key_clear)).setOnClickListener(this);
        (findViewById(com.carecloud.carepaylibrary.R.id.closeViewLayout)).setOnClickListener(this);
    }
 }