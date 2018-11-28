package com.carecloud.carepay.practice.library.appointments.dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.customdialog.BasePracticeDialog;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.interfaces.AppointmentNavigationCallback;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSlotsDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Locale;

public class PatientModeRequestAppointmentDialog extends BasePracticeDialog {

    private final AppointmentsSlotsDTO appointmentSlot;
    private final LayoutInflater inflater;
    protected final AppointmentResourcesDTO appointmentResourcesDTO;
    private VisitTypeDTO visitTypeDTO;
    protected final AppointmentNavigationCallback callback;

    /**
     * Constructor.
     *
     * @param context         activity context
     * @param cancelString    String
     * @param appointmentSlot The appointment data
     * @param callback        for dialog actions
     */
    public PatientModeRequestAppointmentDialog(Context context, String cancelString,
                                               AppointmentsSlotsDTO appointmentSlot,
                                               AppointmentResourcesDTO appointmentResourcesDTO,
                                               VisitTypeDTO visitTypeDTO,
                                               AppointmentNavigationCallback callback) {

        super(context, cancelString, false);
        Context context1 = context;
        this.appointmentSlot = appointmentSlot;
        this.appointmentResourcesDTO = appointmentResourcesDTO;
        this.visitTypeDTO = visitTypeDTO;
        this.callback = callback;

        inflater = (LayoutInflater) context1.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        onAddContentView(inflater);
    }

    @SuppressLint("InflateParams")
    @Override
    protected void onAddContentView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.dialog_request_appointment_patient_mode, null);
        ((FrameLayout) findViewById(R.id.base_dialog_content_layout)).addView(view);
        inflateUIComponents(view);
    }

    @Override
    protected void onAddFooterView(LayoutInflater inflater) {

    }

    protected void inflateUIComponents(final View view) {
        Button requestAppointmentButton = view.findViewById(R.id.requestAppointmentButton);
        ApplicationMode.ApplicationType applicationType = callback.getApplicationMode().getApplicationType();
        boolean autoScheduleAppointments = callback.getAppointmentsSettings().getRequests().getAutomaticallyApproveRequests();

        final EditText reasonForVisitEditText = view.findViewById(R.id.reasonForVisitEditText);
        requestAppointmentButton.setText(Label.getLabel(applicationType == ApplicationMode.ApplicationType.PRACTICE ||
                autoScheduleAppointments ? "schedule_appointment_button_label" : "appointments_request_heading"));
        requestAppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.requestAppointment(appointmentSlot, reasonForVisitEditText.getText().toString());
                }
                dismiss();
            }
        });

        DateUtil dateUtil = DateUtil.getInstance().setDateRaw(appointmentSlot.getStartTime());

        setDialogTitle(dateUtil.getDateAsDayMonthDayOrdinalYear(Label.getLabel("appointments_web_today_heading")));

        TextView appointmentTimeTextView = view.findViewById(R.id.appointment_time);
        appointmentTimeTextView.setText(dateUtil.getTime12Hour());

        TextView providerImageTextView = view.findViewById(R.id.provider_short_name);
        providerImageTextView.setText(StringUtil.getShortName(appointmentResourcesDTO.getResource().getProvider().getName()));

        TextView appointmentDoctorNameTextView = view.findViewById(R.id.provider_doctor_name);
        appointmentDoctorNameTextView.setText(String
                .format("%s | %s", appointmentResourcesDTO.getResource().getProvider().getName(),
                        appointmentResourcesDTO.getResource().getProvider().getSpecialty().getName()));

        TextView appointmentPlaceNameTextView = view.findViewById(R.id.provider_place_name);
        appointmentPlaceNameTextView.setText(appointmentSlot.getLocation().getName());

        TextView appointmentAddressTextView = view.findViewById(R.id.provider_place_address);
        appointmentAddressTextView.setText(appointmentSlot.getLocation().getAddress().getPlaceAddressString());

        initializeVisitTypeTextView(view);

        setCancelImage(R.drawable.icn_arrow_left);
        setCancelable(false);

        View prepaidLayout = findViewById(R.id.prepaymentLayout);
        if (visitTypeDTO.getAmount() > 0) {
            prepaidLayout.setVisibility(View.VISIBLE);
            TextView prepaidAmount = findViewById(R.id.prepaymentAmount);
            prepaidAmount.setText(NumberFormat.getCurrencyInstance(Locale.US).format(visitTypeDTO.getAmount()));
            requestAppointmentButton.setText(Label.getLabel("appointments_prepayment_button"));
        } else {
            prepaidLayout.setVisibility(View.GONE);
        }

        final ScrollView scrollContainer = view.findViewById(R.id.scrollContainer);
        scrollContainer.post(new Runnable() {
            @Override
            public void run() {
                scrollContainer.fullScroll(View.FOCUS_UP);
                SystemUtil.hideSoftKeyboard(getContext(), view);
            }
        });

        ImageView picImageView = findViewById(R.id.picImageView);
        loadImage(picImageView, appointmentResourcesDTO.getResource().getProvider().getPhoto());

    }

    private void initializeVisitTypeTextView(View view) {
        TextView visitTypeTextView = view.findViewById(R.id.reasonTextView);
        if (null == visitTypeDTO) {
            dismiss();
            return;
        }
        String visitReason = visitTypeDTO.getName();
        if (SystemUtil.isNotEmptyString(visitReason)) {
            visitTypeTextView.setText(visitReason);
        }
    }

    @Override
    protected void onDialogCancel() {
        super.onDialogCancel();
        if (null != callback) {
            callback.onAppointmentUnconfirmed();
        }
        dismiss();
    }

    protected void loadImage(final ImageView imageView, String url) {
        int size = getContext().getResources().getDimensionPixelSize(R.dimen.provider_card_avatar_size);
        Picasso.with(getContext()).load(url)
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
