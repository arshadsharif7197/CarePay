package com.carecloud.carepaylibray.appointments.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.activities.AppointmentsActivity;
import com.carecloud.carepaylibray.appointments.models.AppointmentModel;
import com.carecloud.carepaylibray.appointments.models.AvailableHoursModel;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.List;

public class AvailableHoursAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // The items to display in your RecyclerView
    private List<Object> items;
    private Context context;
    private AppointmentModel model;
    private final int SECTION_HEADER = 0;

    // Provide a suitable constructor (depends on the kind of data set)
    public AvailableHoursAdapter(Context context, List<Object> items, AppointmentModel model) {
        this.context = context;
        this.items = items;
        this.model = model;
    }

    // Return the size of your data set (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return this.items.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof AvailableHoursModel) {
            return 1;
        } else if (items.get(position) instanceof String) {
            return SECTION_HEADER;
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        if (viewType == SECTION_HEADER) {
            View availableHoursListHeaderRow = inflater.inflate(R.layout.apt_available_hours_list_header_row, viewGroup, false);
            viewHolder = new ViewHolderSectionHeader(availableHoursListHeaderRow);
        } else {
            View availableHoursListDataRow = inflater.inflate(R.layout.apt_available_hours_list_data_row, viewGroup, false);
            viewHolder = new ViewHolderTimeSlot(availableHoursListDataRow);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder.getItemViewType() == SECTION_HEADER) {
            ViewHolderSectionHeader vhSectionHeader = (ViewHolderSectionHeader) viewHolder;
            vhSectionHeader.getTextView().setText(items.get(position).toString());
        } else {
            ViewHolderTimeSlot vhTimeSlot = (ViewHolderTimeSlot) viewHolder;
            vhTimeSlot.getTextView().setText(((AvailableHoursModel) items.get(position)).getmTimeSlot());
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView selTime = (TextView) v.findViewById(R.id.textview_timeslot);

                // Set dummy data for now
                model.setButtonTitle("REQUEST APPOINTMENT");
                model.setAppointmentDate("09/22/16 " + selTime.getText().toString() + " UTC");
                model.setAppointmentTime(selTime.getText().toString());

                // Launch dialog of appointment request
                AppointmentsActivity baseActivity = new AppointmentsActivity();
                baseActivity.setAppointmentModel(model);
                baseActivity.showAppointmentsDialog(context, model, 6);
            }
        });
    }

    private class ViewHolderTimeSlot extends RecyclerView.ViewHolder {

        private TextView textViewTimeSlot;

        ViewHolderTimeSlot(View v) {
            super(v);
            textViewTimeSlot = (TextView) v.findViewById(R.id.textview_timeslot);
            SystemUtil.setProximaNovaRegularTypeface(v.getContext(), textViewTimeSlot);
        }

        TextView getTextView() {
            return textViewTimeSlot;
        }

        public void setTextView(TextView textViewTimeSlot) {
            this.textViewTimeSlot = textViewTimeSlot;
        }
    }

    private class ViewHolderSectionHeader extends RecyclerView.ViewHolder {

        private TextView textViewSectionHeader;

        ViewHolderSectionHeader(View v) {
            super(v);
            textViewSectionHeader = (TextView) v.findViewById(R.id.textview_section_header);
            SystemUtil.setProximaNovaSemiboldTypeface(v.getContext(), textViewSectionHeader);
        }

        TextView getTextView() {
            return textViewSectionHeader;
        }

        public void setTextView(TextView textViewSectionHeader) {
            this.textViewSectionHeader = textViewSectionHeader;
        }
    }
}