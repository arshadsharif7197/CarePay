package com.carecloud.carepay.practice.library.customcomponent;


import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;

import static com.carecloud.carepay.practice.library.R.styleable.AppointmentStatusCartViewAttrs;
import static com.carecloud.carepay.practice.library.R.styleable.AppointmentStatusCartViewAttrs_appointmentListType;


/**
 * Created by Jahirul Bhuiyan on 10/18/2016.
 */

public class AppointmentStatusCartView extends LinearLayout {
    private static final int APPOINTMENT_CARD_TYPE_CHECKING_IN_ATTRIBUTE = 0;
    private static final int APPOINTMENT_CARD_TYPE_WAITING_ROOM_ATTRIBUTE = 1;
    public static final String APPOINTMENT_CARD_TYPE_CHECKING_IN = "appointment_card_type_checking_in";
    public static final String APPOINTMENT_CARD_TYPE_WAITING_ROOM = "appointment_card_type_waiting_room";

    Context context;
    private ImageView patientPicImageView;
    private CarePayTextView patientNameTextView;
    private CarePayTextView providerNameTextView;
    private CarePayTextView amountTextView;
    private CarePayTextView timeTextView;
    private RelativeLayout containerLayout;
    private String appointmentId;
    private String appointmentListType;

    public AppointmentStatusCartView(Context context) {
        super(context);
        this.context = context;
        addViews(null);
    }

    public AppointmentStatusCartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        addViews(attrs);
    }

    public AppointmentStatusCartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        addViews(attrs);
    }

    private void addViews(AttributeSet attrs) {
        inflate(context, R.layout.cardview_appointment_status, this);
        patientPicImageView = (ImageView) findViewById(R.id.patientPicImageView);
        patientNameTextView = (CarePayTextView) findViewById(R.id.patientNameTextView);
        providerNameTextView = (CarePayTextView) findViewById(R.id.providerNameTextView);
        amountTextView = (CarePayTextView) findViewById(R.id.amountTextView);
        timeTextView = (CarePayTextView) findViewById(R.id.timeTextView);
        containerLayout = (RelativeLayout) findViewById(R.id.containerLayout);
        this.setOnLongClickListener(onLongClickListener);
        //this.setOnDragListener(onDragListener);
        try {
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                    attrs,
                    AppointmentStatusCartViewAttrs,
                    0, 0);
            int appointmentListTypeAttr = typedArray.getInt(AppointmentStatusCartViewAttrs_appointmentListType,0 );
            switch (appointmentListTypeAttr) {
                case APPOINTMENT_CARD_TYPE_CHECKING_IN_ATTRIBUTE: {
                    appointmentListType = APPOINTMENT_CARD_TYPE_CHECKING_IN;
                    break;
                }
                case APPOINTMENT_CARD_TYPE_WAITING_ROOM_ATTRIBUTE: {
                    appointmentListType = APPOINTMENT_CARD_TYPE_WAITING_ROOM;
                    break;
                }
                default:
                    appointmentListType = APPOINTMENT_CARD_TYPE_CHECKING_IN;
            }
            typedArray.recycle();
        } catch (Exception exception){
            appointmentListType = APPOINTMENT_CARD_TYPE_CHECKING_IN;
        }

    }

    public void setPatientImage(String patientImageURL) {
        if (!StringUtil.isNullOrEmpty(patientImageURL)) {
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

    public String getAppointmentId() {
        return appointmentId;
    }



    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getAppointmentListType() {
        return appointmentListType;
    }

    public void setAppointmentTime(long appointmentTime) {
        final DateTime appointmentDateTime = new DateTime(appointmentTime);
        timeTextView.setText(appointmentDateTime.toString("hh:mm a"));
        if (appointmentDateTime.isBeforeNow()) {
            timeTextView.setBackgroundResource(R.drawable.bg_red_overlay);
        } else {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    if (appointmentDateTime.isBeforeNow()) {
                        timeTextView.setBackgroundResource(R.drawable.bg_red_overlay);
                    } else {
                        timeTextView.setBackgroundResource(R.drawable.bg_green_overlay);
                        handler.postDelayed(this, 60000);
                    }
                }
            }, 60000);
        }
    }

    OnDragListener onDragListener = new OnDragListener() {
        @Override
        public boolean onDrag(View view, DragEvent dragEvent) {
            AppointmentStatusCartView appointmentStatusCartView = (AppointmentStatusCartView) view;
            switch (dragEvent.getAction()) {
                //signal for the start of a drag and drop operation.
                case DragEvent.ACTION_DRAG_STARTED:
                    // do nothing
                    break;
                //the drag point has entered the bounding box of the View
                case DragEvent.ACTION_DRAG_ENTERED:
                    appointmentStatusCartView.containerLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.light_gray));
                    break;
                //the user has moved the drag shadow outside the bounding box of the View
                case DragEvent.ACTION_DRAG_EXITED:
                    appointmentStatusCartView.containerLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                    break;
                //drag shadow has been released,the drag point is within the bounding box of the View
                case DragEvent.ACTION_DROP:
                    appointmentStatusCartView.containerLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                    break;
                //the drag and drop operation has concluded.
                case DragEvent.ACTION_DRAG_ENDED:
                    //rv.updateState(customState);
                    break;
                default:
                    break;
            }
            return true;
        }
    };

    OnLongClickListener onLongClickListener = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            // Create a new ClipData.
            // This is done in two steps to provide clarity. The convenience method
            // ClipData.newPlainText() can create a plain text ClipData in one step.

            // Create a new ClipData.Item from the ImageView object's tag

            AppointmentStatusCartView appointmentStatusCartView = (AppointmentStatusCartView) view;

            ClipData.Item item = new ClipData.Item(appointmentStatusCartView.getAppointmentId());

            // Create a new ClipData using the tag as a label, the plain text MIME type, and
            // the already-created item. This will create a new ClipDescription object within the
            // ClipData, and set its MIME type entry to "text/plain"
            ClipData dragData = new ClipData((CharSequence) appointmentStatusCartView.getAppointmentId(), new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, item);

            // Instantiates the drag shadow builder.
            View.DragShadowBuilder myShadow = new AppDragShadowBuilder(appointmentStatusCartView);

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
