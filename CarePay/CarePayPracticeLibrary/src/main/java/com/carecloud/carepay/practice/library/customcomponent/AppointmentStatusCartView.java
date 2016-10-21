package com.carecloud.carepay.practice.library.customcomponent;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.squareup.picasso.Picasso;
import org.joda.time.DateTime;


/**
 * Created by Jahirul Bhuiyan on 10/18/2016.
 */

public class AppointmentStatusCartView extends CardView {
    Context context;
    private ImageView patientPicImageView;
    private CarePayTextView patientNameTextView;
    private CarePayTextView providerNameTextView;
    private CarePayTextView amountTextView;
    private CarePayTextView timeTextView;

    public AppointmentStatusCartView(Context context) {
        super(context);
        this.context = context;
        addViews();
    }

    public AppointmentStatusCartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        addViews();
    }

    public AppointmentStatusCartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        addViews();
    }

    private void addViews() {
        inflate(context, R.layout.cardview_appointment_status, this);
        patientPicImageView = (ImageView) findViewById(R.id.patientPicImageView);
        patientNameTextView = (CarePayTextView) findViewById(R.id.patientNameTextView);
        providerNameTextView = (CarePayTextView) findViewById(R.id.providerNameTextView);
        amountTextView = (CarePayTextView) findViewById(R.id.amountTextView);
        timeTextView = (CarePayTextView) findViewById(R.id.timeTextView);
    }

    public void setPatientImage(String patientImageURL) {
        if(!StringUtil.isNullOrEmpty(patientImageURL)){
            Picasso.with(context).load(patientImageURL).transform(
                    new CircleImageTransform()).resize(160, 160).into(this.patientPicImageView);
        }
    }

    public void setPatientName(String patientName) {

        this.patientNameTextView.setText(patientName);
    }

    public void setProviderName(String providerName) {

        this.providerNameTextView.setText(providerName);
    }

    public void setAmount(double amount) {

        this.amountTextView.setText(String.format("Balance: $%.2f", amount));
    }


    public void setAppointmentTime(long appointmentTime) {
        final DateTime appointmentDateTime=new DateTime(appointmentTime);
        timeTextView.setText(appointmentDateTime.toString("hh:mm a"));
        if(appointmentDateTime.isBeforeNow()){
            timeTextView.setBackgroundResource(R.drawable.bg_red_overlay);
        }else{
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    if(appointmentDateTime.isBeforeNow()) {
                        timeTextView.setBackgroundResource(R.drawable.bg_red_overlay);
                    }else{
                        timeTextView.setBackgroundResource(R.drawable.bg_green_overlay);
                        handler.postDelayed(this, 60000);
                    }
                }
            }, 60000);
        }
    }

}
