package com.carecloud.carepay.practice.library.checkin.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.appointments.adapters.CardViewPatient;
import com.carecloud.carepay.practice.library.checkin.PracticeModePracticeCheckInActivity;
import com.carecloud.carepay.practice.library.checkin.dtos.CheckInDTO;
import com.carecloud.carepay.practice.library.checkin.filters.FilterDataDTO;
import com.carecloud.carepay.practice.library.customcomponent.AppointmentStatusCardView;
import com.carecloud.carepay.practice.library.models.FilterModel;
import com.carecloud.carepay.practice.library.models.MapFilterModel;
import com.carecloud.carepay.practice.library.util.PracticeUtil;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsPayloadDTO;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
        List<PatientBalanceDTO> patientBalances = checkInDTO.getPayload().getPatientBalances();
        Map<String, String> profilePhotoMap = PracticeUtil.getProfilePhotoMap(patientBalances);
        Map<String, Double> totalBalanceMap = PracticeUtil.getTotalBalanceMap(patientBalances);
        this.allPatients = new ArrayList<>(appointments.size());

        for (AppointmentDTO appointmentDTO : appointments) {
            // Set profile photo
            PatientModel patientDTO = appointmentDTO.getPayload().getPatient();
            patientDTO.setProfilePhoto(profilePhotoMap.get(patientDTO.getPatientId()));

            this.allPatients.add(new CardViewPatient(appointmentDTO.getPayload(), totalBalanceMap.get(patientDTO.getPatientId())));
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

            // Filter other statuses
            if (!patient.isPending && !patient.isCheckingIn && !patient.isCheckedIn) {
                continue;
            }

            if (isWaitingRoom) {
                if (!patient.isCheckedIn) {
                    continue;
                }
            } else if (patient.isCheckedIn) {
                continue;
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
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CheckedInAppointmentAdapter.CartViewHolder holder, final int position) {
        final CardViewPatient patient = filteredPatients.get(position);
        holder.appointmentStatusCartView.setPatientName(patient.name);
        holder.appointmentStatusCartView.setAppointmentId(patient.id);
        holder.appointmentStatusCartView.setAmount(patient.balance);
        holder.appointmentStatusCartView.setPatientImage(patient.photoUrl);
        holder.appointmentStatusCartView.setProviderName(patient.providerName);
        holder.appointmentStatusCartView.setAppointmentTime(patient.appointmentStartTime);
        holder.appointmentStatusCartView.setTag(patient.raw);
        holder.appointmentStatusCartView.setWaitingRoom(isWaitingRoom);
        holder.appointmentStatusCartView.setShortName(patient.initials);
        holder.itemView.setContentDescription(patient.name);
    }

    @Override
    public int getItemCount() {
        if (filteredPatients != null) {
            return filteredPatients.size();
        }

        return 0;
    }

    /**
     * @param id patient ID
     * @param filtered true if search have to be scoped to only displayed patients
     * @return patient
     */
    public CardViewPatient getAppointmentById(String id, boolean filtered) {
        List<CardViewPatient> list = filtered ? filteredPatients : allPatients;
        for (CardViewPatient patient : list) {
            if (patient.id.equalsIgnoreCase(id)) {
                return patient;
            }
        }
        return null;
    }

    /**
     * Flip patient between Pending / Checking-In and Checked-In states
     * @param id patient ID
     * @param filtered true if search have to be scoped to only displayed patients
     * @return true is patient was found and was successfully flipped
     */
    public boolean flipAppointmentById(String id, boolean filtered) {
        CardViewPatient patient = getAppointmentById(id, filtered);

        if (patient == null) {
            return false;
        }

        if (patient.isCheckedIn) {
            patient.isCheckedIn = false;
            patient.isPending = true;

            applyFilter();

            return true;
        }

        if (patient.isPending || patient.isCheckingIn) {
            patient.isCheckedIn = true;
            patient.isPending = false;
            patient.isCheckingIn = false;

            applyFilter();

            return true;
        }

        return false;
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
            ((PracticeModePracticeCheckInActivity)context).onCheckInItemClick((AppointmentsPayloadDTO)view.getTag(), isWaitingRoom);
        }
    }
}
