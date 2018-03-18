package com.carecloud.carepay.patient.appointments.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.fragments.BaseAvailableHoursFragment;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesItemDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;

import java.util.Date;

public class AvailableHoursFragment extends BaseAvailableHoursFragment {


    public static AvailableHoursFragment newInstance(AppointmentsResultModel appointmentsResultModel,
                                                     AppointmentDTO appointmentDTO) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, appointmentsResultModel);
        DtoHelper.bundleDto(args, appointmentDTO);
        AvailableHoursFragment availableHoursFragment = new AvailableHoursFragment();
        availableHoursFragment.setArguments(args);
        return availableHoursFragment;
    }

    public static AvailableHoursFragment newInstance(AppointmentsResultModel appointmentsResultModel,
                                                     AppointmentResourcesItemDTO appointmentResource, Date startDate, Date endDate,
                                                     VisitTypeDTO visitTypeDTO) {
        Bundle args = new Bundle();
        args.putSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_START_DATE_BUNDLE, startDate);
        args.putSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_END_DATE_BUNDLE, endDate);
        DtoHelper.bundleDto(args, appointmentResource);
        DtoHelper.bundleDto(args, visitTypeDTO);
        DtoHelper.bundleDto(args, appointmentsResultModel);
        AvailableHoursFragment availableHoursFragment = new AvailableHoursFragment();
        availableHoursFragment.setArguments(args);
        return availableHoursFragment;
    }


    @Override
    protected void setupEditDateButton(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.add_appointment_toolbar);

        TextView titleOther = (TextView) toolbar.findViewById(R.id.add_appointment_toolbar_other);
        titleOther.setText(Label.getLabel("appointment_select_range_button"));
        titleOther.setVisibility(View.VISIBLE);
        titleOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDateRange();
            }
        });


        Drawable closeIcon = ContextCompat.getDrawable(getActivity(),
                R.drawable.icn_nav_back);
        toolbar.setNavigationIcon(closeIcon);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(navigationOnClickListener);
    }

    /**
     * Click listener for toolbar navigation
     */
    private View.OnClickListener navigationOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getActivity().onBackPressed();
        }
    };




}