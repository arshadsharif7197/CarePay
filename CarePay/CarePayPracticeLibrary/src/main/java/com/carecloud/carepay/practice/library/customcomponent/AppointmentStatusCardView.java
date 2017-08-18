package com.carecloud.carepay.practice.library.customcomponent;


import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by Jahirul Bhuiyan on 10/18/2016.
 */

public class AppointmentStatusCardView extends LinearLayout {
    Context context;
    private ImageView patientPicImageView;
    private CarePayTextView patientNameTextView;
    private CarePayTextView providerNameTextView;
    private CarePayTextView amountTextView;
    private CarePayTextView timeTextView;
    private ViewGroup containerLayout;
    private String appointmentId;
    private CarePayTextView shortNameTextView;
    private boolean isWaitingRoom;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");

    /**
     * AppointmentStatusCardView with context
     *
     * @param context context
     */
    public AppointmentStatusCardView(Context context) {
        super(context);
        this.context = context;
        addViews();
    }

    /**
     * AppointmentStatusCardView with context & attrs
     *
     * @param context context
     */
    public AppointmentStatusCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        addViews();
    }

    /**
     * AppointmentStatusCardView with context, attrs & defStyleAttr
     *
     * @param context context
     */
    public AppointmentStatusCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        addViews();
    }

    /**
     * initialization view
     */
    private void addViews() {
        inflate(context, R.layout.patient_list_item_layout, this);
        patientPicImageView = (ImageView) findViewById(R.id.patient_pic_image_view);
        patientNameTextView = (CarePayTextView) findViewById(R.id.patient_name_text_view);
        providerNameTextView = (CarePayTextView) findViewById(R.id.provider_name_text_view);
        amountTextView = (CarePayTextView) findViewById(R.id.amount_text_view);
        timeTextView = (CarePayTextView) findViewById(R.id.timeTextView);
        containerLayout = (ViewGroup) findViewById(R.id.containerLayout);
        shortNameTextView = (CarePayTextView) findViewById(R.id.patient_short_name);
        this.setOnLongClickListener(onLongClickListener);
    }

    /**
     * Set Image
     *
     * @param photoUrl image Url
     */
    public void setPatientImage(String photoUrl) {
        if (!TextUtils.isEmpty(photoUrl)) {
            Picasso.with(context).load(photoUrl).transform(new CircleImageTransform())
                    .resize(60, 60).into(patientPicImageView);

            patientPicImageView.setVisibility(View.VISIBLE);
        } else {
            patientPicImageView.setVisibility(View.INVISIBLE);
        }
    }

    public void setPatientName(String patientName) {

        this.patientNameTextView.setText(patientName);
    }

    public void setProviderName(String providerName) {

        this.providerNameTextView.setText(providerName);
    }

    public void setAmount(String amount) {
        this.amountTextView.setText(amount);
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setWaitingRoom(boolean waiting) {
        isWaitingRoom = waiting;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public void setShortName(String shortName) {
        this.shortNameTextView.setText(shortName);
    }

    /**
     * Appointment time set
     *
     * @param appointmentTime appointment Time
     */
    public void setAppointmentTime(Date appointmentTime) {
        if (null == appointmentTime) {
            return;
        }

        timeTextView.setText(dateFormat.format(appointmentTime));
        if (appointmentTime.before(new Date())) {
            timeTextView.setBackgroundResource(R.drawable.bg_red_overlay);
        } else {
            timeTextView.setBackgroundResource(R.drawable.bg_green_overlay);
        }

        timeTextView.setVisibility(View.VISIBLE);
    }

    OnLongClickListener onLongClickListener = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            // Create a new ClipData.
            // This is done in two steps to provide clarity. The convenience method
            // ClipData.newPlainText() can create a plain text ClipData in one step.

            // Create a new ClipData.Item from the ImageView object's tag

            AppointmentStatusCardView appointmentStatusCartView = (AppointmentStatusCardView) view;

            ClipData.Item item = new ClipData.Item(appointmentStatusCartView.getAppointmentId());

            // Create a new ClipData using the tag as a label, the plain text MIME type, and
            // the already-created item. This will create a new ClipDescription object within the
            // ClipData, and set its MIME type entry to "text/plain"
            ClipData dragData = new ClipData(appointmentStatusCartView.getAppointmentId(), new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, item);
            double rotationRad = Math.toRadians(view.getRotation());
            int scaledWidth = (int) (view.getWidth() * view.getScaleX());
            int scaledHeight = (int) (view.getHeight() * view.getScaleY());
            double sinRotation = Math.abs(Math.sin(rotationRad));
            double cosRotation = Math.abs(Math.cos(rotationRad));
            final int width = (int) (scaledWidth * cosRotation + scaledHeight * sinRotation);
            final int height = (int) (scaledWidth * sinRotation + scaledHeight * cosRotation);

            // Instantiates the drag shadow builder.
            View.DragShadowBuilder myShadow = new View.DragShadowBuilder(view) {
                @Override
                public void onDrawShadow(Canvas canvas) {
                    if (!isWaitingRoom) {
                        canvas.rotate(3, 0, height / 2);
                    }
                    super.onDrawShadow(canvas);
                }

                @Override
                public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
                    shadowSize.set(width + 100, height + 100);
                    shadowTouchPoint.set(shadowSize.x / 2, shadowSize.y / 2);
                }
            };

            // Starts the drag

            view.startDrag(dragData,  // the data to be dragged
                    myShadow,  // the drag shadow builder
                    null,      // no need to use local data
                    0          // flags (not currently used, set to 0)
            );
            return true;
        }
    };

}
