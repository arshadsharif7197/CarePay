package com.carecloud.carepaylibray.appointments.createappointment.availabilityhour;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSlotsDTO;
import com.carecloud.carepaylibray.utils.DateUtil;

import java.util.List;

/**
 * @author pjohnson on 1/17/19.
 */
public class AvailabilityHourAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int CELL_HEADER = 0;
    private static final int CELL_CARD = 1;
    private final OnTimeSlotListItemClickListener listener;
    private List<AppointmentsSlotsDTO> timeSlots;


    public AvailabilityHourAdapter(List<AppointmentsSlotsDTO> timeSlots,
                                   OnTimeSlotListItemClickListener listener) {
        this.timeSlots = timeSlots;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == CELL_HEADER) {
            View availableHoursListHeaderRow = inflater
                    .inflate(R.layout.apt_available_hours_list_header_row, parent, false);
            viewHolder = new AvailabilityHourAdapter.ViewHolderSectionHeader(availableHoursListHeaderRow);
        } else {
            View availableHoursListDataRow = inflater
                    .inflate(R.layout.apt_available_hours_list_data_row, parent, false);
            viewHolder = new AvailabilityHourAdapter.ViewHolderTimeSlot(availableHoursListDataRow);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final AppointmentsSlotsDTO appointmentSlotItem = timeSlots.get(position);
        if (holder.getItemViewType() == CELL_HEADER) {
            ViewHolderSectionHeader vhSectionHeader = (ViewHolderSectionHeader) holder;
            vhSectionHeader.textViewSectionHeader.setText(appointmentSlotItem.getStartTime());
        } else {
            ViewHolderTimeSlot vhTimeSlot = (ViewHolderTimeSlot) holder;
            String upcomingStartTime = appointmentSlotItem.getStartTime();
            DateUtil.getInstance().setDateRaw(upcomingStartTime);
            String time12Hour = DateUtil.getInstance().getTime12Hour();
            vhTimeSlot.textViewTimeSlot.setText(time12Hour);
            holder.itemView.setContentDescription(time12Hour);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onTimeSlotListItemClickListener(appointmentSlotItem);
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        final AppointmentsSlotsDTO slot = timeSlots.get(position);
        if (slot.isHeader()) {
            return CELL_HEADER;
        }
        return CELL_CARD;
    }

    @Override
    public int getItemCount() {
        return timeSlots.size();
    }

    public interface OnTimeSlotListItemClickListener {
        void onTimeSlotListItemClickListener(AppointmentsSlotsDTO slot);
    }

    public class ViewHolderSectionHeader extends RecyclerView.ViewHolder {
        private final TextView textViewSectionHeader;

        public ViewHolderSectionHeader(View view) {
            super(view);
            textViewSectionHeader = view.findViewById(R.id.textview_section_header);
        }
    }

    public class ViewHolderTimeSlot extends RecyclerView.ViewHolder {
        private final TextView textViewTimeSlot;

        public ViewHolderTimeSlot(View view) {
            super(view);
            textViewTimeSlot = view.findViewById(R.id.textview_timeslot);
        }
    }
}
