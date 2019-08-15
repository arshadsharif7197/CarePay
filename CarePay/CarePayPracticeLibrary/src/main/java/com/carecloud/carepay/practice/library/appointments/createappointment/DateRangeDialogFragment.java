package com.carecloud.carepay.practice.library.appointments.createappointment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.label.Label;
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_appointment_date_range, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inflateToolbar(view);
        view.findViewById(R.id.todayButton).setOnClickListener(todayButtonClickListener);
    }

    private void inflateToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar_layout);
        TextView toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText(Label.getLabel("pick_date_heading"));

        view.findViewById(R.id.closeViewLayout).setOnClickListener(v -> dismiss());


    }
}
