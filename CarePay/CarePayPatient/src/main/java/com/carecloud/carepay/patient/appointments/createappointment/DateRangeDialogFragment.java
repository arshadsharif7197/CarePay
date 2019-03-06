package com.carecloud.carepay.patient.appointments.createappointment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.appointments.createappointment.BaseDateRangeDialogFragment;

import java.util.Date;

/**
 * @author pjohnson on 1/17/19.
 */
public class DateRangeDialogFragment extends BaseDateRangeDialogFragment {

    public static DateRangeDialogFragment newInstance(Date startDate, Date endDate) {
        Bundle args = new Bundle();
        DateRangeDialogFragment fragment = new DateRangeDialogFragment();
        if (startDate == null) {
            startDate = new Date();
        }
        args.putSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_START_DATE_BUNDLE, startDate);
        if (endDate == null) {
            endDate = new Date();
        }
        args.putSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_END_DATE_BUNDLE, endDate);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_appointment_date_range, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inflateToolbar(view);
    }

    private void inflateToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.add_appointment_toolbar);
        toolbar.setTitle("");

        Drawable closeIcon = ContextCompat.getDrawable(getActivity(),
                R.drawable.icn_patient_mode_nav_close);
        toolbar.setNavigationIcon(closeIcon);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        View todayButton = toolbar.findViewById(R.id.today_button);
        todayButton.setOnClickListener(todayButtonClickListener);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }
}
