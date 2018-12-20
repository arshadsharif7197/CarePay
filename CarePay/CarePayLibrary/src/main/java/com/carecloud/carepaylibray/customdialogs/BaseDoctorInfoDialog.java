package com.carecloud.carepaylibray.customdialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsPayloadDTO;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class BaseDoctorInfoDialog extends Dialog implements View.OnClickListener {

    private static final String LOG_TAG = BaseDoctorInfoDialog.class.getSimpleName();
    private Context context;
    private AppointmentsPayloadDTO payload;
    private View addActionLayout;
    private View rootLayout;

    private String placeName;
    private String placeAddress;
    private String phoneNumber;
    private boolean isRequestAppointmentDialog;

    /**
     * Constructor.
     *
     * @param context        context
     * @param appointmentDTO appointment model
     */
    public BaseDoctorInfoDialog(Context context, AppointmentDTO appointmentDTO, boolean isRequestAppointDialog) {
        super(context);
        this.context = context;
        this.isRequestAppointmentDialog = isRequestAppointDialog;
        if(appointmentDTO != null) {
            this.payload = appointmentDTO.getPayload();
        }
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

        DateUtil.getInstance().setDateRaw(payload.getStartTime());

        CarePayTextView dateTextView = findViewById(R.id.appointDateTextView);
        CarePayTextView timeTextView = findViewById(R.id.appointTimeTextView);

        if(isRequestAppointmentDialog) {
            timeTextView.setText(DateUtil.getInstance().getDateAsDayMonthDayOrdinal());
            dateTextView.setText(DateUtil.getInstance().getTime12Hour());
        } else {
            dateTextView.setText(DateUtil.getInstance().getDateAsDayMonthDayOrdinal());
            timeTextView.setText(DateUtil.getInstance().getTime12Hour());
        }

        CarePayTextView shortNameTextView = findViewById(R.id.appointShortnameTextView);
        shortNameTextView.setText(StringUtil.getShortName(payload.getProvider().getName()));

        CarePayTextView nameTextView = findViewById(R.id.providerName);
        nameTextView.setText(payload.getProvider().getName());

        CarePayTextView typeTextView = findViewById(R.id.providerSpecialty);
        typeTextView.setText(payload.getProvider().getSpecialty().getName());

        // Appointment Place name
        final CarePayTextView addressHeaderTextView = findViewById(R.id.appointAddressHeaderTextView);
        placeName = payload.getLocation().getName();
        addressHeaderTextView.setText(placeName);

        // Appointment Place address
        final CarePayTextView addressTextView = findViewById(R.id.appointAddressTextView);
        if( payload.getLocation().getAddress() !=null) {
            placeAddress = payload.getLocation().getAddress().getPlaceAddressString();
            addressTextView.setText(placeAddress);
        }

        findViewById(R.id.dialogAppointDismiss).setOnClickListener(this);
        findViewById(R.id.appointLocationImageView).setOnClickListener(this);
        findViewById(R.id.appointDailImageView).setOnClickListener(this);
        addActionLayout = findViewById(R.id.actionAddLayout);
        rootLayout = findViewById(R.id.rootDialogAppointLayout);

        phoneNumber = payload.getProvider().getPhone();
        if (StringUtil.isNullOrEmpty(phoneNumber)) {
            Drawable originalIcon = ContextCompat.getDrawable(context, R.drawable.icn_appointment_card_call);
            originalIcon.setAlpha(40);
            ((ImageView) findViewById(R.id.appointDailImageView))
                    .setImageDrawable(originalIcon);
        }

        if (StringUtil.isNullOrEmpty(placeAddress)) {
            Drawable originalIcon = ContextCompat.getDrawable(context, R.drawable.icn_appointment_card_directions);
            originalIcon.setAlpha(40);
            ((ImageView) findViewById(R.id.appointLocationImageView))
                    .setImageDrawable(originalIcon);
        }
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.dialogAppointDismiss) {
            cancel();
        } else if (viewId == R.id.appointLocationImageView) {
            onMapView(placeName, placeAddress);
        } else if (viewId == R.id.appointDailImageView && !StringUtil.isNullOrEmpty(phoneNumber)) {
            onPhoneCall(phoneNumber);
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
            Log.e(LOG_TAG, ex.getMessage());
        }
    }

    protected View getAddActionChildView() {
        return addActionLayout;
    }

    protected void loadImage(final ImageView imageView, String url){
        int size = context.getResources().getDimensionPixelSize(R.dimen.payment_details_dialog_icon_size);
        Picasso.with(context).load(url)
                .resize(size, size)
                .centerCrop()
                .transform(new CircleImageTransform())
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        imageView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError() {
                        imageView.setVisibility(View.GONE);
                    }
                });
    }

}
