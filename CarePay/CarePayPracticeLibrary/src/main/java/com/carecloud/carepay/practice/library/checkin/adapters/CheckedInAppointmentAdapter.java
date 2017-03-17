package com.carecloud.carepay.practice.library.checkin.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.appointments.adapters.CardViewPatient;
import com.carecloud.carepay.practice.library.checkin.PracticeModePracticeCheckInActivity;
import com.carecloud.carepay.practice.library.checkin.dtos.AppointmentDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.AppointmentPayloadDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.CheckInDTO;
import com.carecloud.carepay.practice.library.checkin.filters.FilterDataDTO;
import com.carecloud.carepay.practice.library.customcomponent.AppointmentStatusCardView;
import com.carecloud.carepay.practice.library.models.FilterModel;
import com.carecloud.carepay.practice.library.models.MapFilterModel;
import com.carecloud.carepay.practice.library.util.PracticeUtil;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.utils.DateUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * Created by Jahirul Bhuiyan on 10/17/2016.
 */

public class CheckedInAppointmentAdapter extends RecyclerView.Adapter<CheckedInAppointmentAdapter.CartViewHolder> {

    private Context context;
    private List<CardViewPatient> allPatients;
    private List<CardViewPatient> filteredPatients;
    private MapFilterModel filterModel;

    private boolean isWaitingRoom;

    /**
     * Constructor.
     *
     * @param context context
     * @param checkInDTO    checkIn DTO
     */
    public CheckedInAppointmentAdapter(Context context, CheckInDTO checkInDTO, boolean isWaitingRoom) {

        this.context = context;
        this.isWaitingRoom = isWaitingRoom;
        loadPatients(checkInDTO);
    }

    private void loadPatients(CheckInDTO checkInDTO) {
        List<AppointmentDTO> appointments = checkInDTO.getPayload().getAppointments();
        Map<String, String> profilePhotoMap = PracticeUtil.getProfilePhotoMap(checkInDTO.getPayload().getPatientBalances());
        this.allPatients = new ArrayList<>(appointments.size());

        for (AppointmentDTO appointmentDTO : appointments) {
            // Set profile photo
            PatientModel patientDTO = appointmentDTO.getPayload().getPatient();
            patientDTO.setProfilePhoto(profilePhotoMap.get(patientDTO.getPatientId()));

            this.allPatients.add(new CardViewPatient(appointmentDTO, appointmentDTO.getPayload()));
        }

        sortListByDate(this.allPatients);
    }

    private void sortListByDate(List<CardViewPatient> list) {
        Collections.sort(list, new Comparator<CardViewPatient>() {
            @Override
            public int compare(CardViewPatient lhs, CardViewPatient rhs) {
                if (lhs != null && rhs != null) {
                    return lhs.appointmentStartTime.compareTo(rhs.appointmentStartTime);
                }
                return -1;
            }
        });
    }

    public void applyFilter(FilterModel filterModel) {
        this.filterModel = new MapFilterModel(filterModel);
        applyFilter();
    }

    private void applyFilter() {
        if (null == filterModel) {
            filterModel = new MapFilterModel();
        }

        filteredPatients = new LinkedList<>();

        Map<String, FilterDataDTO> patients = filterModel.getPatients();
        Map<String, FilterDataDTO> doctors = filterModel.getDoctors();
        Map<String, FilterDataDTO> locations = filterModel.getLocations();

        for (CardViewPatient patient : allPatients) {
            // Check filter by patient
            if (filterModel.isFilteringByPatients() && !patients.containsKey(patient.id)) {
                continue;
            }
            // Check filter by provider
            if (filterModel.isFilteringByDoctors() && !doctors.containsKey(patient.providerId)) {
                continue;
            }
            // Check filter by location
            if (filterModel.isFilteringByLocations() && !locations.containsKey(patient.locationId)) {
                continue;
            }

            if (isWaitingRoom) {
                if (!patient.isCheckedIn) {
                    continue;
                }
            } else {

            }

            filteredPatients.add(patient);
        }

        notifyDataSetChanged();
    }

    /**
     * Creates view.
     *
     * @param parent   parent view.
     * @param viewType view type
     * @return created view
     */
    public CheckedInAppointmentAdapter.CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.checked_in_list_item_layout, parent, false);
        CartViewHolder viewHolder = new CartViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CheckedInAppointmentAdapter.CartViewHolder holder, final int position) {
        final CardViewPatient patient = filteredPatients.get(position);
        holder.appointmentStatusCartView.setPatientName(patient.name);
        holder.appointmentStatusCartView.setAppointmentId(patient.id);

//        holder.appointmentStatusCartView.setAmount(getTotalBalance(position));
        //holder.appointmentStatusCartView.setPatientImage(patientModel.getPhoto());
        holder.appointmentStatusCartView.setProviderName(patient.providerName);
//        holder.appointmentStatusCartView.setAppointmentTime(DateUtil.getInstance().setDateRaw(appointmentItem.getStartTime()).getDate().getTime());
        holder.appointmentStatusCartView.setTag(patient);
        holder.appointmentStatusCartView.setWaitingRoom(isWaitingRoom);
        holder.appointmentStatusCartView.setShortName(patient.initials);
    }

//    private double getTotalBalance(int position) {
//        AppointmentPayloadDTO appointmentItem = appointmentArrayList.get(position);
//        String id = appointmentItem.getPatient().getPatientId();
//
//        for (PatientBalanceDTO patientBalanceDTO: patientBalances) {
//            PendingBalanceDTO pendingBalanceDTO = patientBalanceDTO.getBalances().get(0);
//            if (pendingBalanceDTO.getMetadata().getPatientId().equals(id) && !pendingBalanceDTO.getPayload().isEmpty()) {
//                return pendingBalanceDTO.getPayload().get(0).getAmount();
//            }
//        }
//        return 0;
//    }

    @Override
    public int getItemCount() {
        if (filteredPatients != null) {
            return filteredPatients.size();
        }

        return 0;
    }

    class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        AppointmentStatusCardView appointmentStatusCartView;

        /**
         * Constructor.
         * @param view view
         */

        CartViewHolder(View view) {
            super(view);
            appointmentStatusCartView= (AppointmentStatusCardView) view;
            appointmentStatusCartView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            ((PracticeModePracticeCheckInActivity)context).onCheckInItemClick((AppointmentPayloadDTO)view.getTag(), isWaitingRoom);
        }
    }
}
