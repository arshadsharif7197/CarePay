package com.carecloud.carepaylibray.customdialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentModel;
import com.carecloud.carepaylibray.utils.SystemUtil;

/**
 * Created by prem_mourya on 9/21/2016.
 */

public class BaseDoctorInfoDialog extends Dialog implements
        View.OnClickListener {
    private Context context;
    private AppointmentModel appointmentModel;
    private View addActionlayout, rootLayout;

    public BaseDoctorInfoDialog(Context context, AppointmentModel appointmentModel) {
        super(context);
        this.context = context;
        this.appointmentModel = appointmentModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_appointments);
        setCancelable(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        params.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.90);
        getWindow().setAttributes((WindowManager.LayoutParams) params);

        TextView shortNameTextView = ((TextView) findViewById(R.id.appointShortnameTextView));
        TextView nameTextView = ((TextView) findViewById(R.id.appointNameTextView));
        TextView typeTextView = ((TextView) findViewById(R.id.appointTypeTextView));
        final TextView addressTextView = ((TextView) findViewById(R.id.appointAddressTextView));
        final TextView addressHeaderTextView = ((TextView) findViewById(R.id.appointAddressHeaderTextView));
        TextView dateTextView = ((TextView) findViewById(R.id.appointDateTextView));
        TextView timeTextView = ((TextView) findViewById(R.id.appointTimeTextView));

        dateTextView.setText(SystemUtil.onDateParseToString(context, appointmentModel.getAppointmentDate())[0]);
        timeTextView.setText(SystemUtil.onDateParseToString(context, appointmentModel.getAppointmentDate())[1]);
        shortNameTextView.setText(SystemUtil.onShortDrName(appointmentModel.getDoctorName()));
        nameTextView.setText(appointmentModel.getDoctorName());
        typeTextView.setText(appointmentModel.getAppointmentType());
        addressHeaderTextView.setText(appointmentModel.getPlaceName());
        addressTextView.setText(appointmentModel.getPlaceAddress());

        SystemUtil.setProximaNovaLightTypeface(context, dateTextView);
        SystemUtil.setGothamRoundedBoldTypeface(context, timeTextView);
        SystemUtil.setProximaNovaRegularTypeface(context, shortNameTextView);
        SystemUtil.setProximaNovaSemiboldTypeface(context, nameTextView);
        SystemUtil.setProximaNovaRegularTypeface(context, typeTextView);
        SystemUtil.setProximaNovaRegularTypeface(context, addressTextView);
        SystemUtil.setProximaNovaExtraboldTypeface(context, addressHeaderTextView);

        findViewById(R.id.dialogAppointHeaderTextView).setOnClickListener(this);
        findViewById(R.id.appointLocationImageView).setOnClickListener(this);
        findViewById(R.id.appointDailImageView).setOnClickListener(this);
        addActionlayout = findViewById(R.id.actionAddLayout);
        rootLayout = findViewById(R.id.rootDialogAppointLayout);

        if (TextUtils.isEmpty(appointmentModel.getPhoneNumber())) {
            //it will change disable icon for call
            ((ImageView) findViewById(R.id.appointDailImageView)).setImageResource(R.drawable.icn_appointment_card_call);
        }
        if (!SystemUtil.isNotEmptyString(appointmentModel.getPlaceAddress())) {
            //it will change disable icon for address
            ((ImageView) findViewById(R.id.appointLocationImageView)).setImageResource(R.drawable.icn_appointment_card_directions);
        }
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.dialogAppointHeaderTextView) {
            cancel();
        } else if (viewId == R.id.appointLocationImageView) {
            onMapView(appointmentModel.getPlaceName(), appointmentModel.getPlaceAddress());
        } else if (viewId == R.id.appointDailImageView) {
            if (!TextUtils.isEmpty(appointmentModel.getPhoneNumber())) {
                onPhoneCall(appointmentModel.getPhoneNumber());
            }
        }
    }

    /**
     * show device map view based on address.
     *
     * @param address the String to evaluate
     */
    private void onMapView(final String addressName, final String address) {
        if (SystemUtil.isNotEmptyString(address)) {
            String placeName = SystemUtil.isNotEmptyString(addressName) ? addressName : "";
            String fullAddress = placeName + " " + address;
            Uri mapUri = Uri.parse("geo:0,0?q=" + Uri.encode(fullAddress));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            context.startActivity(mapIntent);
        }
    }

    /**
     * show device phone call UI based on phone number.
     *
     * @param phoneNumber the String to evaluate
     */
    private void onPhoneCall(final String phoneNumber) {
        try {
            context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
        } catch (android.content.ActivityNotFoundException ex) {

        }
    }

    protected View getAddActionChildView() {
        return addActionlayout;
    }

    protected View getRootView() {
        return rootLayout;
    }

}
