package com.carecloud.carepay.practice.library.appointments.createappointment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.createappointment.availabilityhour.BaseAvailabilityHourFragment;
import com.carecloud.carepaylibray.appointments.interfaces.DateCalendarRangeInterface;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSlotsDTO;

/**
 * @author pjohnson on 1/16/19.
 */
public class AvailabilityHourFragment extends BaseAvailabilityHourFragment implements DateCalendarRangeInterface {

    public static AvailabilityHourFragment newInstance(int selectMode) {
        Bundle args = new Bundle();
        args.putInt("mode", selectMode);
        AvailabilityHourFragment fragment = new AvailabilityHourFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_availability_hours_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpToolbar(view);
        Button changeDateRangeButton = view.findViewById(R.id.changeDateRangeButton);
        changeDateRangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDateRange();
            }
        });
    }

    private void setUpToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar_layout);
        toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText(Label.getLabel("next_5_days_option"));

        view.findViewById(R.id.closeViewLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    protected void selectDateRange() {
        callback.showFragment(DateRangeDialogFragment.newInstance(startDate, endDate));
    }

    @Override
    protected void showAppointmentConfirmationFragment(AppointmentsSlotsDTO slot) {
        super.showAppointmentConfirmationFragment(slot);
        hideDialog();
    }
}
