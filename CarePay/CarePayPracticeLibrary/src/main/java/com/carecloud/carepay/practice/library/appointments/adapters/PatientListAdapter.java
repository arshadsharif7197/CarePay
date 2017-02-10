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
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by cocampo on 2/10/17.
 */

public class PatientListAdapter extends RecyclerView.Adapter<PatientListAdapter.CartViewHolder> {
    private Context context;
    private List<Patient> patients;
    private OnItemTappedListener tapListener;

    public interface OnItemTappedListener {
        void onItemTap(Object dto);
    }

    /**
     * Constructor.
     *
     * @param context         context
     * @param paymentsModel list of patients
     */
    public PatientListAdapter(Context context, PaymentsModel paymentsModel) {
        this.context = context;
        loadPatients(paymentsModel);
    }

    public PatientListAdapter(Context context, CheckInDTO checkInDTO) {
        this.context = context;
        loadPatients(checkInDTO);
    }

    /**
     * Creates view.
     *
     * @param parent   parent view.
     * @param viewType view type
     * @return created view
     */
    public PatientListAdapter.CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_list_item_layout, parent, false);
        return new PatientListAdapter.CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PatientListAdapter.CartViewHolder holder, final int position) {
        final Patient patient = patients.get(position);
        holder.name.setText(patient.name);
        holder.balance.setText(patient.balance);
        holder.provider.setText(patient.provider);
        holder.initials.setText(patient.initials);
        holder.bind(patient, tapListener);

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
        if (patients != null) {
            return patients.size();
        }

        return 0;
    }

    private void loadPatients(CheckInDTO checkInDTO) {
        List<AppointmentDTO> appointments = checkInDTO.getPayload().getAppointments();
        this.patients = new ArrayList<>(appointments.size());

        for (AppointmentDTO appointmentDTO : appointments) {
            this.patients.add(new Patient(appointmentDTO, appointmentDTO.getPayload()));
        }
    }

    private void loadPatients(PaymentsModel paymentsModel) {
        List<PaymentsPatientBalancessDTO> dtoList = paymentsModel.getPaymentPayload().getPatientBalances();
        Map<String, String> providerMap = getProviderMap(paymentsModel.getPaymentPayload().getProviderIndex());
        this.patients = new ArrayList<>(dtoList.size());

        for (PaymentsPatientBalancessDTO dto: dtoList) {
            createPatient(providerMap, dto);
        }
    }

    private void createPatient(Map<String, String> providerMap, PaymentsPatientBalancessDTO dto) {
        List<PatienceBalanceDTO> patientBalances = dto.getBalances();
        double balance = getBalance(patientBalances);
        String provider = getProviderName(providerMap, patientBalances.get(0).getMetadata().getPatientId());
        DemographicsSettingsPersonalDetailsPayloadDTO personalDetails = dto.getDemographics().getPayload().getPersonalDetails();
        patients.add(new Patient(dto, provider, balance, personalDetails));
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

    private String getProviderName(Map<String, String> providerMap, String patientId) {
        if (!StringUtil.isNullOrEmpty(patientId) && providerMap.containsKey(patientId)) {
            return providerMap.get(patientId);
        }

        return StringUtil.getLabelForView("");
    }

    private Map<String, String> getProviderMap(List<ProviderIndexDTO> providerIndex) {
        Map<String, String> map = new HashMap<>();

        for (ProviderIndexDTO providerIndexDTO : providerIndex) {
            List<String> patientIds = providerIndexDTO.getPatientIds();

            for (String patientId : patientIds) {
                map.put(patientId, providerIndexDTO.getName());
            }
        }

        return map;
    }

    public void setTapListener(OnItemTappedListener tapListener) {
        this.tapListener = tapListener;
    }

    class CartViewHolder extends RecyclerView.ViewHolder {

        CarePayTextView initials;
        CarePayTextView name;
        CarePayTextView provider;
        CarePayTextView balance;
        ImageView profilePicture;

        /**
         * Constructor.
         *
         * @param view view
         */
        CartViewHolder(View view) {
            super(view);
            initials = (CarePayTextView) view.findViewById(R.id.patient_short_name);
            name = (CarePayTextView) view.findViewById(R.id.patient_name_text_view);
            provider = (CarePayTextView) view.findViewById(R.id.provider_name_text_view);
            balance = (CarePayTextView) view.findViewById(R.id.amount_text_view);
            profilePicture = (ImageView) view.findViewById(R.id.patient_pic_image_view);
        }

        public void bind(final Patient patient, final OnItemTappedListener tapListener) {
            if (null != tapListener) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        tapListener.onItemTap(patient.raw);
                    }
                });
            }
        }
    }

    class Patient {
        private Object raw;
        private String initials;
        private String name;
        private String provider;
        private String balance;
        private String photoUrl;
        private Date appointmentDate;

        public Patient(Object raw, String provider, double balance, DemographicsSettingsPersonalDetailsPayloadDTO dto) {
            this.raw = raw;
            this.name = dto.getFirstName() + " " + dto.getLastName();
            this.initials = StringUtil.onShortDrName(name);
            this.photoUrl = dto.getProfilePhoto();
            this.provider = provider;
            this.balance = String.format(Locale.getDefault(), "$%.2f", balance);
        }

        public Patient(Object raw, AppointmentPayloadDTO dto) {
            this.raw = raw;
            PatientDTO patientModel = dto.getPatient();
            this.name = patientModel.getFirstName() + " " + patientModel.getLastName();
            this.initials = StringUtil.onShortDrName(patientModel.getFirstName() + " " + patientModel.getLastName());
            this.provider = dto.getProvider().getName();
            this.appointmentDate = DateUtil.getInstance().setDateRaw(dto.getStartTime()).getDate();
        }
    }
}
