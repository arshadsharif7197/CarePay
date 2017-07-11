package com.carecloud.carepaylibray.appointments.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSlotsDTO;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.utils.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lmenendez on 4/21/17.
 */

public class FilterableAvailableHoursAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int CELL_HEADER = 0;
    private static final int CELL_CARD = 1;

    public interface SelectAppointmentTimeSlotCallback {
        void onSelectAppointmentTimeSlot(AppointmentsSlotsDTO appointmentsSlotsDTO);
    }

    public enum LocationMode {
        SINGLE, MULTI;
    }

    private Context context;
    private SelectAppointmentTimeSlotCallback callback;
    private List<AppointmentsSlotsDTO> allTimeSlots = new ArrayList<>();
    private Map<String, LocationDTO> selectedLocations = new HashMap<>();
    private List<AppointmentsSlotsDTO> filteredTimeSlots = new ArrayList<>();
    private LocationMode mode;

    private String today = Label.getLabel("today_label");
    private String tomorrow = Label.getLabel("add_appointment_tomorrow");

    /**
     * Constructor
     * @param context Context
     * @param allTimeSlots all Appoitnment Slots
     * @param selectedLocations Selected Locations
     * @param callback Selected Slot callback
     */
    public FilterableAvailableHoursAdapter(Context context, List<AppointmentsSlotsDTO> allTimeSlots, Map<String, LocationDTO> selectedLocations, SelectAppointmentTimeSlotCallback callback, LocationMode mode){
        this.context = context;
        this.allTimeSlots = allTimeSlots;
        this.selectedLocations = selectedLocations;
        this.callback = callback;
        this.mode = mode;
        updateFilteredSlots();
    }

    public void setAllTimeSlots(List<AppointmentsSlotsDTO> allTimeSlots) {
        this.allTimeSlots = allTimeSlots;
        updateFilteredSlots();
    }

    public void setMode(LocationMode mode){
        this.mode = mode;
    }

    @Override
    public int getItemViewType(int position) {
        final AppointmentsSlotsDTO slot = filteredTimeSlots.get(position);

        if (slot.getLocation() == null) {
            return CELL_HEADER;
        }

        return CELL_CARD;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == CELL_HEADER) {
            View availableHoursListHeaderRow = inflater.inflate(R.layout.apt_available_hours_list_header_row, parent, false);
            viewHolder = new ViewHolderSectionHeader(availableHoursListHeaderRow);
        } else {
            View availableHoursListDataRow = inflater.inflate(R.layout.apt_available_hours_list_data_row, parent, false);
            viewHolder = new ViewHolderTimeSlot(availableHoursListDataRow);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final AppointmentsSlotsDTO appointmentSlotItem = filteredTimeSlots.get(position);

        if (viewHolder.getItemViewType() == CELL_HEADER) {
            ViewHolderSectionHeader vhSectionHeader = (ViewHolderSectionHeader) viewHolder;
            vhSectionHeader.getTextView().setText(appointmentSlotItem.getStartTime());
        } else {
            ViewHolderTimeSlot vhTimeSlot = (ViewHolderTimeSlot) viewHolder;
            final AppointmentsSlotsDTO appointmentsSlotsDTO = ((AppointmentsSlotsDTO) appointmentSlotItem);

            String upcomingStartTime = appointmentsSlotsDTO.getStartTime();
            DateUtil.getInstance().setDateRaw(upcomingStartTime);
            String time12Hour = DateUtil.getInstance().getTime12Hour();
            vhTimeSlot.getTextView().setText(time12Hour);
            viewHolder.itemView.setContentDescription(time12Hour);
            
            if (selectedLocations.size() == 1 || mode == LocationMode.SINGLE) {
                //Single location selected no need to show Location in each Slot
                vhTimeSlot.getTextViewLocation().setVisibility(View.GONE);
            } else {
                vhTimeSlot.getTextViewLocation().setText(appointmentsSlotsDTO.getLocation().getName());
                vhTimeSlot.getTextViewLocation().setVisibility(View.VISIBLE);
            }

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                     callback.onSelectAppointmentTimeSlot(appointmentSlotItem);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return filteredTimeSlots.size();
    }

    /**
     * Update Slots
     */
    public void updateFilteredSlots(){
        filteredTimeSlots.clear();

        Date lastDate = new Date(0);
        AppointmentsSlotsDTO headerTemplate;
        int headerCount = 0;
        for(AppointmentsSlotsDTO slot : allTimeSlots){
            //Check if this is a filtered Location
            if(slot.getLocation() == null || (!selectedLocations.isEmpty() && !selectedLocations.containsKey(slot.getLocation().getGuid()))){
                continue;
            }

            Date slotDate = DateUtil.getInstance().setDateRaw(slot.getStartTime()).getDate();
            if(slotDate!=null && !DateUtil.isSameDay(lastDate, slotDate)){
                //add this slot object to the list
                headerTemplate = new AppointmentsSlotsDTO();
                headerTemplate.setStartTime(DateUtil.getFormattedDate(slotDate, today, tomorrow));
                filteredTimeSlots.add(headerTemplate);
                headerCount++;
                lastDate = slotDate;
            }

            filteredTimeSlots.add(slot);

        }

//        if(headerCount == 1){
//            filteredTimeSlots.remove(0);
//        }

        notifyDataSetChanged();
    }


    private class ViewHolderTimeSlot extends RecyclerView.ViewHolder {

        private TextView textViewTimeSlot;
        private TextView textViewLocation;

        ViewHolderTimeSlot(View view) {
            super(view);
            textViewTimeSlot = (TextView) view.findViewById(R.id.textview_timeslot);
            textViewLocation = (TextView) view.findViewById(R.id.textview_location);
        }

        public TextView getTextView() {
            return textViewTimeSlot;
        }

        public void setTextView(TextView textViewTimeSlot) {
            this.textViewTimeSlot = textViewTimeSlot;
        }

        public TextView getTextViewLocation() {
            return textViewLocation;
        }

        public void setTextViewLocation(TextView textViewLocation) {
            this.textViewLocation = textViewLocation;
        }
    }

    private class ViewHolderSectionHeader extends RecyclerView.ViewHolder {

        private TextView textViewSectionHeader;

        ViewHolderSectionHeader(View view) {
            super(view);
            textViewSectionHeader = (TextView) view.findViewById(R.id.textview_section_header);
        }

        TextView getTextView() {
            return textViewSectionHeader;
        }

        public void setTextView(TextView textViewSectionHeader) {
            this.textViewSectionHeader = textViewSectionHeader;
        }
    }

}
