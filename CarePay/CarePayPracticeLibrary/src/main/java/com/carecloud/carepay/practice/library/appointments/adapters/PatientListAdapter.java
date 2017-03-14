package com.carecloud.carepay.practice.library.appointments.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.checkin.dtos.AppointmentDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.AppointmentPayloadDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.CheckInDTO;
import com.carecloud.carepay.practice.library.checkin.filters.FilterDataDTO;
import com.carecloud.carepay.practice.library.models.MapFilterModel;
import com.carecloud.carepay.practice.library.util.PracticeUtil;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.payments.models.LocationIndexDTO;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.ProviderIndexDTO;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by cocampo on 2/10/17.
 */

public class PatientListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int CELL_HEADER = 0;
    private static final int CELL_CARD = 1;

    private Context context;
    private List<Patient> allPatients;
    private List<Patient> filteredPatients;
    private OnItemTappedListener tapListener;
    private MapFilterModel filterModel;

    private int sizeFilteredPatients;
    private int sizeFilteredPendingPatients;

    public interface OnItemTappedListener {
        void onItemTap(Object dto);
    }

    /**
     * Constructor.
     *
     * @param context       context
     * @param paymentsModel payload
     */
    public PatientListAdapter(Context context, PaymentsModel paymentsModel) {
        this.context = context;
        loadPatients(paymentsModel);
        applyFilter();
    }

    /**
     * Constructor.
     *
     * @param context    context
     * @param checkInDTO payload
     */
    public PatientListAdapter(Context context, CheckInDTO checkInDTO) {
        this.context = context;
        loadPatients(checkInDTO);
        applyFilter();
    }

    @Override
    public int getItemViewType(int position) {
        final Patient patient = filteredPatients.get(position);

        if (null == patient.raw) {
            return CELL_HEADER;
        }

        return CELL_CARD;
    }

    /**
     * Creates view.
     *
     * @param parent   parent view.
     * @param viewType view type
     * @return created view
     */
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (CELL_HEADER == viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_list_header_layout, parent, false);
            return new HeaderViewHolder(view);
        }

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_list_item_layout, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final Patient patient = filteredPatients.get(position);

        if (null == patient.raw) {
            bindHeaderViewHolder((HeaderViewHolder) holder, patient.appointmentStartTime);
        } else {
            bindCardViewHolder((CardViewHolder) holder, patient);
        }
    }

    private void bindHeaderViewHolder(HeaderViewHolder holder, Date appointmentTime) {
        holder.setTimeView(appointmentTime);
    }

    private void bindCardViewHolder(final CardViewHolder holder, final Patient patient) {
        holder.name.setText(patient.name);
        holder.balance.setText(patient.balance);
        holder.provider.setText(patient.providerName);
        holder.initials.setText(patient.initials);
        holder.bind(patient, tapListener);
        holder.setTimeView(patient);

        if (!TextUtils.isEmpty(patient.photoUrl)) {
            Picasso.Builder builder = new Picasso.Builder(context);
            builder.listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    holder.initials.setText(patient.initials);
                }
            });

            builder.build().load(patient.photoUrl).transform(new CircleImageTransform())
                    .resize(60, 60).into(holder.profilePicture);

            holder.profilePicture.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        if (filteredPatients != null) {
            return filteredPatients.size();
        }

        return 0;
    }


    public void applyFilter(MapFilterModel filterModel) {
        this.filterModel = filterModel;
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

        sizeFilteredPatients = 0;
        sizeFilteredPendingPatients = 0;

        Date dateTime = new Date(0);
        int countDifferentDates = 0;
        int countByDay = 0;
        for (Patient patient : allPatients) {
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

            sizeFilteredPatients++;

            // Count pending and filter by pending
            if (patient.isRequested) {
                sizeFilteredPendingPatients++;
            } else if (filterModel.isFilteringByPending()) {
                continue;
            }

            if (null != patient.appointmentStartTime && !DateUtil.isSameDay(dateTime, patient.appointmentStartTime)) {
                dateTime = patient.appointmentStartTime;

                if (countByDay % 2 == 1) {
                    filteredPatients.add(new Patient());
                }

                filteredPatients.add(new Patient(dateTime));
                filteredPatients.add(new Patient());
                countDifferentDates++;
                countByDay = 0;
            }

            filteredPatients.add(patient);
            countByDay++;
        }

        if (1 == countDifferentDates) {
            filteredPatients.remove(0);
            filteredPatients.remove(0);
        }

        notifyDataSetChanged();
    }

    private void loadPatients(CheckInDTO checkInDTO) {
        List<AppointmentDTO> appointments = checkInDTO.getPayload().getAppointments();
        Map<String, String> profilePhotoMap = PracticeUtil.getProfilePhotoMap(checkInDTO.getPayload().getPatientBalances());
        this.allPatients = new ArrayList<>(appointments.size());

        for (AppointmentDTO appointmentDTO : appointments) {
            // Set profile photo
            PatientModel patientDTO = appointmentDTO.getPayload().getPatient();
            patientDTO.setProfilePhoto(profilePhotoMap.get(patientDTO.getPatientId()));

            this.allPatients.add(new Patient(appointmentDTO, appointmentDTO.getPayload()));
        }

        sortListByDate(this.allPatients);
    }

    private void loadPatients(PaymentsModel paymentsModel) {
        List<PatientBalanceDTO> dtoList = paymentsModel.getPaymentPayload().getPatientBalances();
        Map<String, ProviderIndexDTO> providerMap = getProviderMap(paymentsModel.getPaymentPayload().getProviderIndex());
        Map<String, LocationIndexDTO> locationMap = getLocationMap(paymentsModel.getPaymentPayload().getLocationIndex());
        this.allPatients = new ArrayList<>(dtoList.size());

        for (PatientBalanceDTO dto : dtoList) {
            createPatient(providerMap, locationMap, dto);
        }
    }

    private void sortListByDate(List<Patient> list) {
        Collections.sort(list, new Comparator<Patient>() {
            @Override
            public int compare(Patient lhs, Patient rhs) {
                if (lhs != null && rhs != null) {
                    return lhs.appointmentStartTime.compareTo(rhs.appointmentStartTime);
                }
                return -1;
            }
        });
    }

    private void createPatient(Map<String, ProviderIndexDTO> providerMap, Map<String, LocationIndexDTO> locationMap, PatientBalanceDTO dto) {
        List<PendingBalanceDTO> patientBalances = dto.getBalances();
        double balance = getBalance(patientBalances);
        String patientId = patientBalances.get(0).getMetadata().getPatientId();
        ProviderIndexDTO provider = providerMap.get(patientId);
        LocationIndexDTO location = locationMap.get(patientId);
        PatientModel personalDetails = dto.getDemographics().getPayload().getPersonalDetails();
        allPatients.add(new Patient(dto, patientId, provider, location, balance, personalDetails));
    }

    private double getBalance(List<PendingBalanceDTO> balances) {
        double amount = 0;

        for (int i = 0; i < balances.size(); i++) {
            List<PendingBalancePayloadDTO> payload = balances.get(i).getPayload();
            for (int j = 0; j < payload.size(); j++) {
                amount += payload.get(j).getAmount();
            }
        }

        return amount;
    }

    private Map<String, ProviderIndexDTO> getProviderMap(List<ProviderIndexDTO> providerIndex) {
        Map<String, ProviderIndexDTO> map = new HashMap<>();

        for (ProviderIndexDTO indexDTO : providerIndex) {
            List<String> patientIds = indexDTO.getPatientIds();

            for (String patientId : patientIds) {
                map.put(patientId, indexDTO);
            }
        }

        return map;
    }

    private Map<String, LocationIndexDTO> getLocationMap(List<LocationIndexDTO> locationIndex) {
        Map<String, LocationIndexDTO> map = new HashMap<>();

        for (LocationIndexDTO indexDTO: locationIndex) {
            List<String> patientIds = indexDTO.getPatientIds();

            for (String patientId : patientIds) {
                map.put(patientId, indexDTO);
            }
        }

        return map;
    }


    public void setTapListener(OnItemTappedListener tapListener) {
        this.tapListener = tapListener;
    }

    /**
     * @return number of pending and non-pending patients after filtering
     */
    public int getSizeFilteredPatients() {
        return sizeFilteredPatients;
    }

    /**
     * @return number of pending patients after filtering
     */
    public int getSizeFilteredPendingPatients() {
        return sizeFilteredPendingPatients;
    }

    class CardViewHolder extends RecyclerView.ViewHolder {

        CarePayTextView initials;
        CarePayTextView name;
        CarePayTextView provider;
        CarePayTextView balance;
        CarePayTextView timeTextView;
        ImageView profilePicture;

        /**
         * Constructor.
         *
         * @param view view
         */
        CardViewHolder(View view) {
            super(view);
            initials = (CarePayTextView) view.findViewById(R.id.patient_short_name);
            name = (CarePayTextView) view.findViewById(R.id.patient_name_text_view);
            provider = (CarePayTextView) view.findViewById(R.id.provider_name_text_view);
            balance = (CarePayTextView) view.findViewById(R.id.amount_text_view);
            timeTextView = (CarePayTextView) view.findViewById(R.id.timeTextView);
            profilePicture = (ImageView) view.findViewById(R.id.patient_pic_image_view);
        }

        public void bind(final Patient patient, final OnItemTappedListener tapListener) {
            if (null != tapListener) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tapListener.onItemTap(patient.raw);
                    }
                });
            }
        }

        void setTimeView(Patient patient) {
            if (null == patient.appointmentStartTime) {
                return;
            }

            final DateTime startDateTime = new DateTime(patient.appointmentStartTime);
            timeTextView.setText(startDateTime.toString("hh:mm a"));
            if (patient.isRequested) {
                timeTextView.setBackgroundResource(R.drawable.bg_orange_overlay);
            } else if (patient.isAppointmentOver) {
                timeTextView.setBackgroundResource(R.drawable.bg_red_overlay);
            } else {
                timeTextView.setBackgroundResource(R.drawable.bg_green_overlay);
            }

            timeTextView.setVisibility(View.VISIBLE);
        }
    }

    private class HeaderViewHolder extends CardViewHolder {
        CarePayTextView dateTextView;

        HeaderViewHolder(View view) {
            super(view);
            dateTextView = (CarePayTextView) view.findViewById(R.id.timeTextView);
        }

        void setTimeView(Date date) {
            String text = "";
            if (null != date) {
                text = DateUtil.getInstance().setDate(date).getDateAsMonthLiteralDayOrdinal();
            }

            dateTextView.setText(text);
        }
    }

    class Patient {
        private Object raw;
        private String id;
        private String initials;
        private String name;
        private String providerName;
        private String balance;
        private String photoUrl;
        private String providerId;
        private String locationId;
        private Date appointmentStartTime;
        private Boolean isAppointmentOver;
        private Boolean isRequested;

        public Patient(Object raw, String id, ProviderIndexDTO provider, LocationIndexDTO location, double balance, PatientModel dto) {
            this.raw = raw;
            this.id = id;
            this.name = dto.getFullName();
            this.initials = StringUtil.onShortName(name);
            this.photoUrl = dto.getProfilePhoto();
            this.providerName = StringUtil.getLabelForView(provider.getName());
            this.providerId = provider.getId();
            this.balance = String.format(Locale.getDefault(), "$%.2f", balance);
            this.locationId = location.getId();
            this.isRequested = false;
        }

        public Patient(Object raw, AppointmentPayloadDTO dto) {
            this.raw = raw;
            PatientModel patientModel = dto.getPatient();
            this.id = patientModel.getPatientId();
            this.name = patientModel.getFullName();
            this.initials = StringUtil.onShortName(this.name);
            this.photoUrl = patientModel.getProfilePhoto();
            this.providerId = dto.getProvider().getId().toString();
            this.providerName = dto.getProvider().getName();
            this.appointmentStartTime = DateUtil.getInstance().setDateRaw(dto.getStartTime()).getDate();
            this.isAppointmentOver = dto.isAppointmentOver();
            this.locationId = dto.getLocation().getId().toString();
            this.isRequested = dto.getAppointmentStatus().getCode().equalsIgnoreCase(CarePayConstants.REQUESTED);
        }

        public Patient(Date appointmentTime) {
            this.appointmentStartTime = appointmentTime;
        }

        public Patient() {

        }
    }
}
