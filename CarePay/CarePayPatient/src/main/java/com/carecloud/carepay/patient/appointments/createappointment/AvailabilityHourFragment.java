package com.carecloud.carepay.patient.appointments.createappointment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.createappointment.availabilityhour.BaseAvailabilityHourFragment;
import com.carecloud.carepaylibray.appointments.interfaces.DateCalendarRangeInterface;

/**
 * @author pjohnson on 1/16/19.
 */
public class AvailabilityHourFragment extends BaseAvailabilityHourFragment implements DateCalendarRangeInterface {

    public static AvailabilityHourFragment newInstance(int mode) {
        Bundle args = new Bundle();
        args.putInt("mode", mode);
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
    }

    private void setUpToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar_layout);
        toolbar.setNavigationIcon(R.drawable.icn_nav_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        toolbarTitle = toolbar.findViewById(R.id.add_appointment_toolbar_title);
        toolbarTitle.setText(Label.getLabel("next_5_days_option"));
        callback.displayToolbar(false, null);

        TextView titleOther = toolbar.findViewById(R.id.add_appointment_toolbar_other);
        titleOther.setText(Label.getLabel("appointment_select_range_button"));
        titleOther.setVisibility(View.VISIBLE);
        titleOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDateRange();
            }
        });
    }

    @Override
    protected void selectDateRange() {
        callback.addFragment(DateRangeDialogFragment.newInstance(startDate, endDate), true);
    }
}
