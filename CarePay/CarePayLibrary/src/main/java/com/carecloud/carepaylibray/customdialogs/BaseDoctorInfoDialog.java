package com.carecloud.carepaylibray.customdialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
import com.carecloud.carepaylibray.appointments.models.Appointment;
import com.carecloud.carepaylibray.appointments.models.AppointmentsPayloadModel;
import com.carecloud.carepaylibray.constants.CarePayConstants;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

public class BaseDoctorInfoDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private AppointmentsPayloadModel payload;
    private View addActionLayout, rootLayout;

    private String placeName;
    private String placeAddress;
    private String phoneNumber;

    public BaseDoctorInfoDialog(Context context, Appointment appointmentModel) {
        super(context);
        this.context = context;
        this.payload = appointmentModel.getPayload();
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
        getWindow().setAttributes(params);

        TextView shortNameTextView = ((TextView) findViewById(R.id.appointShortnameTextView));
        TextView nameTextView = ((TextView) findViewById(R.id.appointNameTextView));
        TextView typeTextView = ((TextView) findViewById(R.id.appointTypeTextView));
        final TextView addressTextView = ((TextView) findViewById(R.id.appointAddressTextView));
        final TextView addressHeaderTextView = ((TextView) findViewById(R.id.appointAddressHeaderTextView));
        TextView dateTextView = ((TextView) findViewById(R.id.appointDateTextView));
        TextView timeTextView = ((TextView) findViewById(R.id.appointTimeTextView));

        DateUtil.getInstance().setFormat(CarePayConstants.APPOINTMENT_DATE_TIME_FORMAT);
        DateUtil.getInstance().setDateRaw(payload.getStartTime());
        dateTextView.setText(DateUtil.getInstance().getDateAsDayMonthDayOrdinal());
        timeTextView.setText(DateUtil.getInstance().getTime12Hour());

        shortNameTextView.setText(StringUtil.onShortDrName(payload.getProvider().getName()));
        nameTextView.setText(payload.getProvider().getName());
        typeTextView.setText(payload.getProvider().getSpecialty());

        // Appointment Place name
        placeName = payload.getLocation().getName();
        addressHeaderTextView.setText(placeName);

        // Appointment Place address
        placeAddress = payload.getLocation().getAddress().getPlaceAddressString();
        addressTextView.setText(placeAddress);

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
        addActionLayout = findViewById(R.id.actionAddLayout);
        rootLayout = findViewById(R.id.rootDialogAppointLayout);

        phoneNumber = payload.getProvider().getPhone();
        if (TextUtils.isEmpty(phoneNumber)) {
            Drawable originalIcon = context.getResources().getDrawable(R.drawable.icn_appointment_card_call);
            ((ImageView) findViewById(R.id.appointDailImageView))
                    .setImageDrawable(SystemUtil.convertDrawableToGrayScale(originalIcon));
        }

        if (!SystemUtil.isNotEmptyString(placeAddress)) {
            Drawable originalIcon = context.getResources().getDrawable(R.drawable.icn_appointment_card_directions);
            ((ImageView) findViewById(R.id.appointLocationImageView))
                    .setImageDrawable(SystemUtil.convertDrawableToGrayScale(originalIcon));
        }
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.dialogAppointHeaderTextView) {
            cancel();
        } else if (viewId == R.id.appointLocationImageView) {
            onMapView(placeName, placeAddress);
        } else if (viewId == R.id.appointDailImageView) {
            if (!TextUtils.isEmpty(phoneNumber)) {
                onPhoneCall(phoneNumber);
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
            Uri mapUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));
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
        return addActionLayout;
    }

    protected View getRootView() {
        return rootLayout;
    }

}
