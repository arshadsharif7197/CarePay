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
import com.carecloud.carepay.practice.library.checkin.dtos.PatientDTO;
import com.carecloud.carepay.practice.library.checkin.filters.FilterDataDTO;
import com.carecloud.carepay.practice.library.models.MapFilterModel;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPersonalDetailsPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PatienceBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PatiencePayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PaymentsPatientBalancessDTO;
import com.carecloud.carepaylibray.payments.models.ProviderIndexDTO;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.joda.time.DateTime;

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
            bindHeaderViewHolder((HeaderViewHolder) holder, patient.appointmentTime);
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
        holder.setTimeView(patient.appointmentTime, patient.isPending);

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

        Date dateTime = new Date(0);
        int countDifferentDates = 0;
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

            if (null != patient.appointmentTime && !DateUtil.isSameDay(dateTime, patient.appointmentTime)) {
                dateTime = patient.appointmentTime;
                filteredPatients.add(new Patient(dateTime));
                filteredPatients.add(new Patient());
                countDifferentDates++;
            }

            filteredPatients.add(patient);
        }

        if (1 == countDifferentDates) {
            filteredPatients.remove(0);
            filteredPatients.remove(0);
        }

        notifyDataSetChanged();
    }

    private void loadPatients(CheckInDTO checkInDTO) {
        List<AppointmentDTO> appointments = checkInDTO.getPayload().getAppointments();
        this.allPatients = new ArrayList<>(appointments.size());

        for (AppointmentDTO appointmentDTO : appointments) {
            this.allPatients.add(new Patient(appointmentDTO, appointmentDTO.getPayload()));
        }
    }

    private void loadPatients(PaymentsModel paymentsModel) {
        List<PaymentsPatientBalancessDTO> dtoList = paymentsModel.getPaymentPayload().getPatientBalances();
        Map<String, ProviderIndexDTO> providerMap = getProviderMap(paymentsModel.getPaymentPayload().getProviderIndex());
        this.allPatients = new ArrayList<>(dtoList.size());

        for (PaymentsPatientBalancessDTO dto : dtoList) {
            createPatient(providerMap, dto);
        }
    }

    private void createPatient(Map<String, ProviderIndexDTO> providerMap, PaymentsPatientBalancessDTO dto) {
        List<PatienceBalanceDTO> patientBalances = dto.getBalances();
        double balance = getBalance(patientBalances);
        String patientId = patientBalances.get(0).getMetadata().getPatientId();
        ProviderIndexDTO provider = providerMap.get(patientId);
        DemographicsSettingsPersonalDetailsPayloadDTO personalDetails = dto.getDemographics().getPayload().getPersonalDetails();
        allPatients.add(new Patient(dto, patientId, provider, balance, personalDetails));
    }

    private double getBalance(List<PatienceBalanceDTO> balances) {
        double amount = 0;

        for (int i = 0; i < balances.size(); i++) {
            List<PatiencePayloadDTO> payload = balances.get(i).getPayload();
            for (int j = 0; j < payload.size(); j++) {
                amount += payload.get(j).getAmount();
            }
        }

        return amount;
    }

    private Map<String, ProviderIndexDTO> getProviderMap(List<ProviderIndexDTO> providerIndex) {
        Map<String, ProviderIndexDTO> map = new HashMap<>();

        for (ProviderIndexDTO providerIndexDTO : providerIndex) {
            List<String> patientIds = providerIndexDTO.getPatientIds();

            for (String patientId : patientIds) {
                map.put(patientId, providerIndexDTO);
            }
        }

        return map;
    }

    public void setTapListener(OnItemTappedListener tapListener) {
        this.tapListener = tapListener;
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

        void setTimeView(Date appointmentTime, boolean isAppointmentPending) {
            if (null == appointmentTime) {
                return;
            }

            final DateTime appointmentDateTime = new DateTime(appointmentTime);
            timeTextView.setText(appointmentDateTime.toString("hh:mm a"));
            if (appointmentDateTime.isBeforeNow()) {
                timeTextView.setBackgroundResource(R.drawable.bg_red_overlay);
            } else if (isAppointmentPending) {
                timeTextView.setBackgroundResource(R.drawable.bg_orange_overlay);
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
        private Date appointmentTime;
        private Boolean isPending;

        public Patient(Object raw, String id, ProviderIndexDTO provider, double balance, DemographicsSettingsPersonalDetailsPayloadDTO dto) {
            this.raw = raw;
            this.id = id;
            this.name = dto.getFirstName() + " " + dto.getLastName();
            this.initials = StringUtil.onShortDrName(name);
            this.photoUrl = dto.getProfilePhoto();
            this.providerName = StringUtil.getLabelForView(provider.getName());
            this.providerId = provider.getId();
            this.balance = String.format(Locale.getDefault(), "$%.2f", balance);
            this.isPending = false;
        }

        public Patient(Object raw, AppointmentPayloadDTO dto) {
            this.raw = raw;
            PatientDTO patientModel = dto.getPatient();
            this.id = patientModel.getId();
            this.name = patientModel.getFirstName() + " " + patientModel.getLastName();
            this.initials = StringUtil.onShortDrName(patientModel.getFirstName() + " " + patientModel.getLastName());
            this.providerId = dto.getProvider().getId().toString();
            this.providerName = dto.getProvider().getName();
            this.appointmentTime = DateUtil.getInstance().setDateRaw(dto.getStartTime()).getDate();
            this.locationId = dto.getLocation().getId().toString();
            this.isPending = dto.getAppointmentStatus().getCode().equalsIgnoreCase(CarePayConstants.PENDING);
        }

        public Patient(Date appointmentTime) {
            this.appointmentTime = appointmentTime;
        }

        public Patient() {

        }
    }
}
