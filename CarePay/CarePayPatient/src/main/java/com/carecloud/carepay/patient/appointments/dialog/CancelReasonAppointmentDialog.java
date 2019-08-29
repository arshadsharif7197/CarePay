package com.carecloud.carepay.patient.appointments.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatRadioButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentCancellationReasonDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.CancellationReasonDTO;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.List;

public class CancelReasonAppointmentDialog extends BaseDialogFragment implements View.OnClickListener {

    private AppointmentDTO appointmentDTO;
    private AppointmentsResultModel appointmentInfo;

    private RadioGroup cancelReasonRadioGroup;
    private Button cancelAppointmentButton;
    private EditText reasonEditText;

    private int selectedReasonId = -1;
    private List<CancellationReasonDTO> cancellationReasons;

    public interface CancelReasonAppointmentDialogListener {
        void onCancelReasonAppointmentDialogCancelClicked(AppointmentDTO appointmentDTO, int cancellationReason, String cancellationReasonComment);
    }

    private CancelReasonAppointmentDialogListener callback;

    /**
     * Contractor for dialog.
     *
     * @param appointmentDTO  appointment Item
     * @param appointmentInfo Appointment Info data
     */
    public static CancelReasonAppointmentDialog newInstance(AppointmentDTO appointmentDTO,
                                         AppointmentsResultModel appointmentInfo) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, appointmentDTO);
        DtoHelper.bundleDto(args, appointmentInfo);
        CancelReasonAppointmentDialog fragment = new CancelReasonAppointmentDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            appointmentDTO = DtoHelper.getConvertedDTO(AppointmentDTO.class, args);
            appointmentInfo = DtoHelper.getConvertedDTO(AppointmentsResultModel.class, args);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.from(getContext()).inflate(R.layout.dialog_cancel_reason_appointment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onInitialization();
        onSetListener();
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("InflateParams")
    private void onInitialization() {
        ((CarePayTextView) findViewById(R.id.heading_text))
                .setText(Label.getLabel("cancel_appointment_reasons_title"));
        reasonEditText = (EditText) findViewById(R.id.reasonEditText);
        reasonEditText.setHint(Label.getLabel("cancel_appointment_other_reason_hint"));
        reasonEditText.setHintTextColor(getResources().getColor(R.color.gray));

        cancelReasonRadioGroup = (RadioGroup) findViewById(R.id.cancelReasonRadioGroup);
        cancelAppointmentButton = (Button) findViewById(R.id.cancelAppointmentButton);
        cancelAppointmentButton.setText(Label.getLabel("cancel_appointments_heading"));
        SystemUtil.setProximaNovaRegularTypeface(getContext(), cancelAppointmentButton);

        cancellationReasons = appointmentInfo.getPayload().getCancellationReasons();
        if (cancellationReasons != null) {
            for (int count = 0; count < cancellationReasons.size(); count++) {
                AppointmentCancellationReasonDTO cancellationReason
                        = cancellationReasons.get(count).getAppointmentCancellationReason();
                addCancelReason(cancellationReason.getName(), cancellationReason.getId());
            }
        }
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("InflateParams")
    private void addCancelReason(String cancelReason, int id) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        AppCompatRadioButton cancelReasonView = (AppCompatRadioButton) inflater.inflate(R.layout.cancel_appointment_reason_item, null);
        RadioGroup.LayoutParams param = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                getResources().getDimensionPixelSize(R.dimen.cancel_radio_button_height));
        param.setMargins(getResources().getDimensionPixelSize(R.dimen.apt_popup_parent_padding), 0,
                getResources().getDimensionPixelSize(R.dimen.apt_popup_parent_padding), 0);
        cancelReasonView.setLayoutParams(param);
        cancelReasonView.setText(cancelReason);
        cancelReasonView.setId(id);
        SystemUtil.setProximaNovaRegularTypeface(getContext(), cancelReasonView);
        cancelReasonRadioGroup.addView(cancelReasonView);

        // Add divider
        ImageView divider = new ImageView(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                getResources().getDimensionPixelSize(R.dimen.apt_lst_img_elevation));
        divider.setLayoutParams(layoutParams);
        divider.setBackgroundColor(getResources().getColor(R.color.cadet_gray));
        cancelReasonRadioGroup.addView(divider);
    }

    private void onSetListener() {
        findViewById(R.id.dialogCloseHeaderImageView).setOnClickListener(this);

        cancelReasonRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                AppCompatRadioButton checkedRadioButton = (AppCompatRadioButton) group.findViewById(checkedId);
                selectedReasonId = group.getCheckedRadioButtonId();
                onSetColorStateForRadioButton(checkedRadioButton);
                onSelectionRadioCancel(checkedRadioButton.isChecked());
            }
        });
        cancelAppointmentButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.dialogCloseHeaderImageView) {
            cancel();
        } else if (viewId == R.id.cancelAppointmentButton) {
            onCancelAppointment();
            cancel();
        }
    }

    @SuppressWarnings("deprecation")
    private void onSelectionRadioCancel(boolean isSelected) {
        if (isSelected) {
            cancelAppointmentButton.setEnabled(true);
        }

        // Check for other cancellation reason
        if (cancellationReasons.get(cancellationReasons.size() - 1).getAppointmentCancellationReason().getId() == selectedReasonId) {
            reasonEditText.setEnabled(true);
            reasonEditText.setHintTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        } else {
            reasonEditText.setEnabled(false);
            reasonEditText.setHintTextColor(ContextCompat.getColor(getContext(), R.color.gray));
        }
    }

    private void onSetColorStateForRadioButton(AppCompatRadioButton appCompatRadioButton) {
        ColorStateList colorStateList = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_checked},
                        new int[]{android.R.attr.state_checked}
                },
                new int[]{
                        ContextCompat.getColor(getContext(), R.color.lightSlateGray),
                        ContextCompat.getColor(getContext(), R.color.colorPrimary),
                }
        );
        appCompatRadioButton.setSupportButtonTintList(colorStateList);
        appCompatRadioButton.setTextColor(colorStateList);
    }

    private int getSelectedCancellationIndex() {
        for (int index = 0; index < cancellationReasons.size(); index++) {
            AppointmentCancellationReasonDTO cancellationReason
                    = cancellationReasons.get(index).getAppointmentCancellationReason();
            if (cancellationReason.getId() == selectedReasonId) {
                return index;
            }
        }
        return -1;
    }

    /**
     * call cancel appointment api.
     */
    private void onCancelAppointment() {
        int selectedIndex = getSelectedCancellationIndex();
        if (selectedIndex != -1) {
            AppointmentCancellationReasonDTO cancellationReason
                    = cancellationReasons.get(selectedIndex).getAppointmentCancellationReason();
            callback.onCancelReasonAppointmentDialogCancelClicked(appointmentDTO, cancellationReason.getId(), reasonEditText.getText().toString());
        }
    }

    public void setsCancelReasonAppointmentDialogListener(CancelReasonAppointmentDialogListener listener) {
        this.callback = listener;
    }

}