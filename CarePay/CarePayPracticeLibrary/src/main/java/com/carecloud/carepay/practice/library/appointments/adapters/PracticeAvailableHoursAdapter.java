package com.carecloud.carepay.practice.library.appointments.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.checkin.filters.FilterDataDTO;
import com.carecloud.carepay.practice.library.models.MapFilterModel;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentAvailabilityDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentAvailabilityPayloadDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentAvailableHoursDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSlotsDTO;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PracticeAvailableHoursAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int CELL_HEADER = 0;
    private static final int CELL_CARD = 1;

    public interface PracticeAvailableHoursAdapterListener {
        void onSelectAppointmentTimeSlot(AppointmentsSlotsDTO appointmentsSlotsDTO);
    }

    // The items to display in your RecyclerView
    private Context context;
    private PracticeAvailableHoursAdapterListener callback;
    private MapFilterModel filterModel;

    private List<AppointmentsSlotsDTO> allTimeSlots;
    private List<AppointmentsSlotsDTO> filteredTimeSlots;

    /**
     * Constructor.
     * @param context context
     * @param callback callback on select time slot
     */
    public PracticeAvailableHoursAdapter(Context context, PracticeAvailableHoursAdapterListener callback) {
        this.context = context;
        this.callback = callback;
    }

    private void loadAvailableHours(AppointmentAvailabilityDTO availabilityDTO) {
        allTimeSlots = new ArrayList<>();

        if (availabilityDTO == null) {
            return;
        }

        List<AppointmentAvailabilityPayloadDTO> payload = availabilityDTO.getPayload().getAppointmentAvailability().getPayload();
        if (payload.isEmpty()) {
            return;
        }

        for (AppointmentAvailabilityPayloadDTO availabilityPayloadDTO : payload) {
            LocationDTO location = availabilityPayloadDTO.getLocation();
            String locationName = location.getName();
            String locationId = location.getId().toString();
            for (AppointmentsSlotsDTO slot : availabilityPayloadDTO.getSlots()) {
                slot.setLocationName(locationName);
                slot.setLocationId(locationId);
                allTimeSlots.add(slot);
            }
        }

        sortListByDate();
    }

    //    private String getSectionHeaderTitle(String timeSlotRawDate) {
//        // Current date
//        String currentDate = DateUtil.getInstance().setToCurrent().toStringWithFormatMmDashDdDashYyyy();
//        final Date currentConvertedDate = DateUtil.getInstance().setDateRaw(currentDate).getDate();
//
//        // day after tomorrow date
//        String dayAfterTomorrowDate = DateUtil.getInstance().setToDayAfterTomorrow().toStringWithFormatMmDashDdDashYyyy();
//        final Date dayAfterTomorrowConvertedDate = DateUtil.getInstance().setDateRaw(dayAfterTomorrowDate).getDate();
//
//        // Appointment time slot date
//        String appointmentDate = DateUtil.getInstance().setDateRaw(timeSlotRawDate).toStringWithFormatMmDashDdDashYyyy();
//        final Date convertedAppointmentDate = DateUtil.getInstance().setDateRaw(appointmentDate).getDate();
//
//        String headerText;
//        if (convertedAppointmentDate.after(dayAfterTomorrowConvertedDate) ||
//                appointmentDate.equalsIgnoreCase(dayAfterTomorrowDate)) {
//            headerText = DateUtil.getInstance().getDateAsDayMonthDayOrdinal();
//        } else if (convertedAppointmentDate.after(currentConvertedDate) && convertedAppointmentDate.before(dayAfterTomorrowConvertedDate)
//                && !appointmentDate.equalsIgnoreCase(currentDate)) {
//            headerText = availabilityDTO.getMetadata().getLabel().getAddAppointmentTomorrow();
//        } else if (convertedAppointmentDate.before(currentConvertedDate)) {
//            headerText = availabilityDTO.getMetadata().getLabel().getTodayAppointmentsHeading();
//        } else {
//            headerText = availabilityDTO.getMetadata().getLabel().getTodayAppointmentsHeading();
//        }
//        return headerText;
//    }

    private void sortListByDate() {
        Collections.sort(allTimeSlots, new Comparator<AppointmentsSlotsDTO>() {
            @Override
            public int compare(AppointmentsSlotsDTO lhs, AppointmentsSlotsDTO rhs) {
                if (lhs != null && rhs != null) {
                    Date d1 = DateUtil.getInstance().setDateRaw(lhs.getStartTime()).getDate();
                    Date d2 = DateUtil.getInstance().setDateRaw(rhs.getStartTime()).getDate();

                    return d1.compareTo(d2);
                }
                return -1;
            }
        });
    }

    // Return the size of your data set (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (filteredTimeSlots == null) {
            return 0;
        }

        return filteredTimeSlots.size();
    }

    @Override
    public int getItemViewType(int position) {
        final AppointmentsSlotsDTO slot = filteredTimeSlots.get(position);

        if (null == slot.getLocationName()) {
            return CELL_HEADER;
        }

        return CELL_CARD;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        if (viewType == CELL_HEADER) {
            View view = inflater.inflate(R.layout.apt_available_hours_list_header_row, viewGroup, false);
            return new ViewHolderSectionHeader(view);
        }

        View view = inflater.inflate(R.layout.apt_available_hours_list_data_row, viewGroup, false);
        return new ViewHolderTimeSlot(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final AppointmentsSlotsDTO slot = filteredTimeSlots.get(position);

        if (viewHolder.getItemViewType() == CELL_HEADER) {
            ViewHolderSectionHeader vhSectionHeader = (ViewHolderSectionHeader) viewHolder;
//            vhSectionHeader.getTextView().setText(appointmentSlotItem.toString());
        } else {
            ViewHolderTimeSlot vhTimeSlot = (ViewHolderTimeSlot) viewHolder;
            try {
                String upcomingStartTime = slot.getStartTime();
                DateUtil.getInstance().setDateRaw(upcomingStartTime);
                String time12Hour = DateUtil.getInstance().getTime12Hour();
                vhTimeSlot.getTextView().setText(time12Hour);

                String location = slot.getLocationName();
                if (StringUtil.isNullOrEmpty(location)) {
                    vhTimeSlot.textViewLocation.setVisibility(View.GONE);
                } else {
                    vhTimeSlot.textViewLocation.setText(location);
                    vhTimeSlot.textViewLocation.setVisibility(View.VISIBLE);
                }

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // Restricted the appointment list item click if it is appointment header type.
                        if (null != slot.getLocationName()) {
                            callback.onSelectAppointmentTimeSlot(slot);
                        }
                    }
                });
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    /**
     * @param availabilityDTO Appointment Availability DTO
     */
    public void setAppointmentAvailability(AppointmentAvailabilityDTO availabilityDTO){
        loadAvailableHours(availabilityDTO);
        applyFilter();
    }

    /**
     * @param filterModel Filter Model
     */
    public void applyFilter(MapFilterModel filterModel) {
        this.filterModel = filterModel;
        applyFilter();
    }

    private void applyFilter() {
        if (null == filterModel) {
            filterModel = new MapFilterModel();
        }

        filteredTimeSlots = new LinkedList<>();

        Map<String, FilterDataDTO> locations = filterModel.getLocations();

        for (AppointmentsSlotsDTO slot: allTimeSlots) {
            // Check filter by location
            if (filterModel.isFilteringByLocations() && !locations.containsKey(slot.getLocationId())) {
                continue;
            }

            filteredTimeSlots.add(slot);
        }

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