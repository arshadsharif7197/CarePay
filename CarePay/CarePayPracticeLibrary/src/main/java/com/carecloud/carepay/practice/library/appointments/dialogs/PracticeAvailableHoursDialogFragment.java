package com.carecloud.carepay.practice.library.appointments.dialogs;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.fragments.BaseAvailableHoursFragment;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesItemDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSlotsDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;

import java.util.Date;

/**
 * Created by lmenendez on 4/17/17
 */

public class PracticeAvailableHoursDialogFragment extends BaseAvailableHoursFragment {

    private TextView editRangeButton;

    /**
     * Create Instance with Data
     * @param appointmentsResultModel appointment Model
     * @param appointmentDTO Appointment DTO
     * @return new PracticeAvailableHoursDialogFragment
     */
    public static PracticeAvailableHoursDialogFragment newInstance(AppointmentsResultModel appointmentsResultModel,
                                                     AppointmentDTO appointmentDTO) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, appointmentsResultModel);
        DtoHelper.bundleDto(args, appointmentDTO);
        PracticeAvailableHoursDialogFragment availableHoursFragment = new PracticeAvailableHoursDialogFragment();
        availableHoursFragment.setArguments(args);
        return availableHoursFragment;
    }

    /**
     * Create Instance with Data
     * @param appointmentsResultModel appointmenr Model
     * @param appointmentResource appointment resource
     * @param startDate appt start date
     * @param endDate appt end date
     * @param visitTypeDTO visit type
     * @return new PracticeAvailableHoursDialogFragment
     */
    public static PracticeAvailableHoursDialogFragment newInstance(AppointmentsResultModel appointmentsResultModel,
                                                     AppointmentResourcesItemDTO appointmentResource, Date startDate, Date endDate,
                                                     VisitTypeDTO visitTypeDTO) {
        Bundle args = new Bundle();
        args.putSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_START_DATE_BUNDLE, startDate);
        args.putSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_END_DATE_BUNDLE, endDate);
        DtoHelper.bundleDto(args, appointmentResource);
        DtoHelper.bundleDto(args, visitTypeDTO);
        DtoHelper.bundleDto(args, appointmentsResultModel);
        PracticeAvailableHoursDialogFragment availableHoursFragment = new PracticeAvailableHoursDialogFragment();
        availableHoursFragment.setArguments(args);
        return availableHoursFragment;
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        super.onViewCreated(view, icicle);

        //setup close button
        View closeButton = findViewById(R.id.closeViewLayout);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

    }


    @Override
    protected void setupEditDateButton(View view) {
        editRangeButton = (TextView) view.findViewById(R.id.edit_date_range_button);
        String range = Label.getLabel("appoitment_edit_date_range_button");
        editRangeButton.setText(range != null ? range : getString(R.string.edit_date_range_button_label));
        editRangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDateRange();
            }
        });

    }

    @Override
    protected void onAdapterRefresh(int count){
        if(count < 1){
            editRangeButton.setText(Label.getLabel("change_date_range_label"));
        }else{
            editRangeButton.setText(Label.getLabel("appoitment_edit_date_range_button"));
        }
    }

    @Override
    protected void selectDateRange(){
        super.selectDateRange();
        dismiss();
    }

    @Override
    public void onSelectAppointmentTimeSlot(AppointmentsSlotsDTO appointmentsSlotsDTO) {
        super.onSelectAppointmentTimeSlot(appointmentsSlotsDTO);
        dismiss();
    }

}
