package com.carecloud.carepay.patient.payment.dialogs;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.customdialogs.BaseAmountInfoDialog;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;

/**
 * Created by prem_mourya on 10/6/2016.
 */

public class PaymentPartialModeDialog extends BaseAmountInfoDialog {

    private Context context;
    private View rootView;
    private CardView paymentPartialModeCardView;
    private LinearLayout addChildDyanmicLayout;
    private CarePayTextView paymentModeTitlelabel;
    private CarePayTextView paymentModeValuelabel;
    private CarePayTextView nextPaymentTitleLabel;
    private CarePayTextView nextPaymentValueLabel;
    private CarePayTextView totalPaymentTitleLabel;
    private CarePayTextView totalPaymentValueLabel;
    private LinearLayout dialogHeaderlayout;
    private Button payNowButton;

    /**
     * show custom dialog for partial mode payments.
     *
     * @param context the activity context to evaluate
     * @param paymentsModel paymentsModel
     */
    public PaymentPartialModeDialog(Context context, PatientBalanceDTO paymentsModel) {
        super(context, paymentsModel);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.rootView = getRootView();
        this.addChildDyanmicLayout = (LinearLayout) this.rootView.findViewById(R.id.addChildDynamicLayout);
        onInitialization();
    }

    private void onInitialization() {
        paymentPartialModeCardView = (CardView) this.rootView.findViewById(R.id.paymentPartialModeCardView);
        paymentPartialModeCardView.setVisibility(View.VISIBLE);
        LayoutInflater inflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View childActionView = inflater.inflate(R.layout.dialog_payment_partial_mode, null);
        paymentModeTitlelabel = (CarePayTextView) childActionView.findViewById(R.id.paymentModeTitlelabel);
        paymentModeValuelabel = (CarePayTextView) childActionView.findViewById(R.id.paymentModeValuelabel);
        nextPaymentTitleLabel = (CarePayTextView) childActionView.findViewById(R.id.nextPaymentTitleLabel);
        nextPaymentValueLabel = (CarePayTextView) childActionView.findViewById(R.id.nextPaymentValueLabel);
        totalPaymentTitleLabel = (CarePayTextView) childActionView.findViewById(R.id.totalPaymentTitleLabel);
        totalPaymentValueLabel = (CarePayTextView) childActionView.findViewById(R.id.totalPaymentValueLabel);
        dialogHeaderlayout = (LinearLayout) this.rootView.findViewById(R.id.dialogHeaderLayout);
        payNowButton = (Button) childActionView.findViewById(R.id.payNowButton);
        onSettingStyle();
        onSetListener();
        onSetValuesView();
        updatePaymentsProgress(25);//Need to pass actual progress here
        this.addChildDyanmicLayout.addView(childActionView);
    }

    private void onSettingStyle() {
        paymentModeTitlelabel.setTextColor(ContextCompat.getColor(context, R.color.textview_default_textcolor));
        paymentModeValuelabel.setTextColor(ContextCompat.getColor(context, R.color.textview_default_textcolor));
        nextPaymentTitleLabel.setTextColor(ContextCompat.getColor(context, R.color.textview_default_textcolor));
        nextPaymentValueLabel.setTextColor(ContextCompat.getColor(context, R.color.textview_default_textcolor));
        totalPaymentTitleLabel.setTextColor(ContextCompat.getColor(context, R.color.textview_default_textcolor));
        totalPaymentValueLabel.setTextColor(ContextCompat.getColor(context, R.color.textview_default_textcolor));
        dialogHeaderlayout.setBackgroundResource(R.color.grey_400);
    }

    private void onSetListener() {
        payNowButton.setOnClickListener(this);

    }

    private void onSetValuesView() {
        //changes are needed when model will come
        String amountStr = paymentAmountTextView.getText().toString().trim();
        String amountMonthStr = "/month";
        SpannableString span1 = new SpannableString(amountStr);
        span1.setSpan(new AbsoluteSizeSpan(context.getResources().getDimensionPixelSize(R.dimen.payment_amount_size)), 0, amountStr.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        SpannableString span2 = new SpannableString(amountMonthStr);
        span2.setSpan(new AbsoluteSizeSpan(context.getResources().getDimensionPixelSize(R.dimen.payment_header_amount_month_size)), 0, amountMonthStr.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        CharSequence finalText = TextUtils.concat(span1, span2);
        paymentAmountTextView.setText(finalText);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int viewId = view.getId();
        if (viewId == R.id.payNowButton) {
            onPayNow();
        }
    }

    private void onPayNow() {

    }

    private void updatePaymentsProgress(int paymentsProgress){
        Resources res = getContext().getResources();
        Drawable drawable = res.getDrawable((R.drawable.circular));
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.circularProgressbar);
        progressBar.setProgress(paymentsProgress);
        progressBar.setSecondaryProgress(100);
        progressBar.setMax(100);
        progressBar.setProgressDrawable(drawable);
    }
}
