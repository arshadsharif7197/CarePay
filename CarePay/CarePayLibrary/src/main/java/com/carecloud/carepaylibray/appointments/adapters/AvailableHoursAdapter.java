package com.carecloud.carepaylibray.appointments.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.activities.AppointmentsActivity;
import com.carecloud.carepaylibray.appointments.models.AppointmentAvailableHoursDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.customdialogs.RequestAppointmentDialog;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.List;

public class AvailableHoursAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // The items to display in your RecyclerView
    private List<Object> items;
    private Context context;
    private AppointmentDTO appointmentDTO;
    private final int sectionHeader = 0;

    /**
     * Constructor.
     * @param context context
     * @param items list of occurrence
     * @param appointmentDTO selected appointment item
     */
    public AvailableHoursAdapter(Context context, List<Object> items, AppointmentDTO appointmentDTO) {
        this.context = context;
        this.items = items;
        this.appointmentDTO = appointmentDTO;
    }

    // Return the size of your data set (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return this.items.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof AppointmentAvailableHoursDTO) {
            return 1;
        } else if (items.get(position) instanceof String) {
            return sectionHeader;
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        if (viewType == sectionHeader) {
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
        if (viewHolder.getItemViewType() == sectionHeader) {
            ViewHolderSectionHeader vhSectionHeader = (ViewHolderSectionHeader) viewHolder;
            vhSectionHeader.getTextView().setText(items.get(position).toString());
        } else {
            ViewHolderTimeSlot vhTimeSlot = (ViewHolderTimeSlot) viewHolder;
            vhTimeSlot.getTextView().setText(((AppointmentAvailableHoursDTO) items.get(position)).getAppointmentTimeSlot());
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView selectedTimeSlot = (TextView) view.findViewById(R.id.textview_timeslot);

                if (selectedTimeSlot != null) {
                    /**
                     * Need Select time slot and pass to next screen.
                     */

                    // Launch dialog of appointment request
                  /*  AppointmentsActivity baseActivity = new AppointmentsActivity();
                    baseActivity.setAppointmentModel(appointmentDTO);
                    new RequestAppointmentDialog(context, appointmentDTO,appointmentDTO).show();*/
                }
            }
        });
    }

    private class ViewHolderTimeSlot extends RecyclerView.ViewHolder {

        private TextView textViewTimeSlot;

        ViewHolderTimeSlot(View view) {
            super(view);
            textViewTimeSlot = (TextView) view.findViewById(R.id.textview_timeslot);
            SystemUtil.setProximaNovaRegularTypeface(view.getContext(), textViewTimeSlot);
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

        ViewHolderSectionHeader(View view) {
            super(view);
            textViewSectionHeader = (TextView) view.findViewById(R.id.textview_section_header);
            SystemUtil.setProximaNovaSemiboldTypeface(view.getContext(), textViewSectionHeader);
        }

        TextView getTextView() {
            return textViewSectionHeader;
        }

        public void setTextView(TextView textViewSectionHeader) {
            this.textViewSectionHeader = textViewSectionHeader;
        }
    }
}