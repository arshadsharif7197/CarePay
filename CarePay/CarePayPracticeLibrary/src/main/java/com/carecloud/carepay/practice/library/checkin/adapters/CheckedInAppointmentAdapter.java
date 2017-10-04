package com.carecloud.carepay.practice.library.checkin.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.appointments.adapters.CardViewPatient;
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

    public interface CheckinItemCallback {
        void onCheckInItemClick(AppointmentsPayloadDTO appointmentsPayloadDTO, int isWaitingRoom);
    }

    private Context context;
    private List<CardViewPatient> allPatients;
    private List<CardViewPatient> filteredPatients;
    private MapFilterModel filterModel;
    private CheckinItemCallback callback;

    public static int CHECKING_IN = 0;
    public static int CHECKED_IN = 1;
    public static int CHECKING_OUT = 2;
    public static int CHECKED_OUT = 3;

    private int theRoom;

    /**
     * Constructor.
     *
     * @param context    context
     * @param checkInDTO checkIn DTO
     */
    public CheckedInAppointmentAdapter(Context context, CheckInDTO checkInDTO,
                                       CheckinItemCallback callback,
                                       int room) {

        this.context = context;
        this.theRoom = room;
        this.callback = callback;
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
            CardViewPatient patientCardView = new CardViewPatient(appointmentDTO.getPayload(),
                    totalBalanceMap.get(patientDTO.getPatientId()));
            patientCardView.appointmentId = appointmentDTO.getPayload().getId();

            this.allPatients.add(patientCardView);
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
            if (!patient.isPending && !patient.isCheckingIn && !patient.isCheckedIn
                    && !patient.isCheckedOut && !patient.isCheckingOut) {
                continue;
            }

            if ((theRoom == CHECKING_IN && patient.isCheckingIn)
                    || (theRoom == CHECKED_IN && patient.isCheckedIn)
                    || (theRoom == CHECKING_OUT && patient.isCheckingOut)
                    || (theRoom == CHECKED_OUT && patient.isCheckedOut)) {

                filteredPatients.add(patient);
            }

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
        View view = LayoutInflater.from(context).inflate(R.layout.checked_in_list_item_layout, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CheckedInAppointmentAdapter.CartViewHolder holder, final int position) {
        final CardViewPatient patient = filteredPatients.get(position);
        holder.appointmentStatusCartView.setPatientName(patient.name);
        holder.appointmentStatusCartView.setAppointmentId(patient.appointmentId);
        holder.appointmentStatusCartView.setAmount(patient.balance);
        holder.appointmentStatusCartView.setPatientImage(patient.photoUrl);
        holder.appointmentStatusCartView.setProviderName(patient.providerName);
        holder.appointmentStatusCartView.setAppointmentTime(patient.appointmentStartTime);
        holder.appointmentStatusCartView.setTag(patient.raw);
        holder.appointmentStatusCartView.setWaitingRoom(patient.isCheckedIn);
        holder.appointmentStatusCartView.setShortName(patient.initials);
        holder.itemView.setContentDescription(patient.name);
//        holder.appointmentStatusCartView.containerLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                callback.onCheckInItemClick((AppointmentsPayloadDTO) patient.raw, theRoom);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        if (filteredPatients != null) {
            return filteredPatients.size();
        }

        return 0;
    }

    /**
     * @param id       patient ID
     * @param filtered true if search have to be scoped to only displayed patients
     * @return patient
     */
    public CardViewPatient getAppointmentById(String id, boolean filtered) {
        List<CardViewPatient> list = filtered ? filteredPatients : allPatients;
        for (CardViewPatient patient : list) {
            if (patient.appointmentId.equalsIgnoreCase(id)) {
                return patient;
            }
        }
        return null;
    }

    /**
     * Flip patient between Pending / Checking-In and Checked-In states
     *
     * @param id       patient ID
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
            patient.isPending = false;
            patient.isCheckingIn = true;
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

    class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        AppointmentStatusCardView appointmentStatusCartView;

        /**
         * Constructor.
         *
         * @param view view
         */

        CartViewHolder(View view) {
            super(view);
            appointmentStatusCartView = (AppointmentStatusCardView) view;
            appointmentStatusCartView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            callback.onCheckInItemClick((AppointmentsPayloadDTO) view.getTag(), theRoom);
        }
    }
}
