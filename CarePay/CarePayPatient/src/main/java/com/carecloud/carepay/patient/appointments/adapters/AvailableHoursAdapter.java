package com.carecloud.carepay.patient.appointments.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentAvailableHoursDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSlotsDTO;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.List;

public class AvailableHoursAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface SelectAppointmentTimeSlotCallback{
        void onSelectAppointmentTimeSlot(AppointmentsSlotsDTO appointmentsSlotsDTO);
    }

    // The items to display in your RecyclerView
    private List<Object> items;
    private Context context;
    private final int sectionHeader = 0;
    private SelectAppointmentTimeSlotCallback selectSlotCallback;

    /**
     * Constructor.
     * @param context context
     * @param items list of occurrence
     * @param callback callback on select time slot
     */
    public AvailableHoursAdapter(Context context, List<Object> items, SelectAppointmentTimeSlotCallback callback) {
        this.context = context;
        this.items = items;
        this.selectSlotCallback = callback;
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
        final Object appointmentSlotItem = items.get(position);
        if (viewHolder.getItemViewType() == sectionHeader) {
            ViewHolderSectionHeader vhSectionHeader = (ViewHolderSectionHeader) viewHolder;
            vhSectionHeader.getTextView().setText(appointmentSlotItem.toString());
        } else {
            ViewHolderTimeSlot vhTimeSlot = (ViewHolderTimeSlot) viewHolder;
            final AppointmentsSlotsDTO appointmentsSlotsDTO = ((AppointmentsSlotsDTO) appointmentSlotItem);

            String upcomingStartTime = appointmentsSlotsDTO.getStartTime();
            DateUtil.getInstance().setDateRaw(upcomingStartTime);
            String time12Hour = DateUtil.getInstance().getTime12Hour();
            vhTimeSlot.getTextView().setText(time12Hour);

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // Restricted the appointment list item click if it is appointment header type.
                    if (appointmentSlotItem.getClass() == AppointmentsSlotsDTO.class) {
                        selectSlotCallback.onSelectAppointmentTimeSlot((AppointmentsSlotsDTO)appointmentSlotItem);
                    }
                }
            });
        }
    }

    public void setItems(List<Object> items){
        this.items = items;
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