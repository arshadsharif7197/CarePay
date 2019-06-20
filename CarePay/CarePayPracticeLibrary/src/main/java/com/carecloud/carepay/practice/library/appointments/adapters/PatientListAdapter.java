package com.carecloud.carepay.practice.library.appointments.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.checkin.dtos.CheckInDTO;
import com.carecloud.carepay.practice.library.checkin.filters.FilterDataDTO;
import com.carecloud.carepay.practice.library.models.MapFilterModel;
import com.carecloud.carepay.practice.library.util.PracticeUtil;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.AppointmentDisplayStyle;
import com.carecloud.carepaylibray.appointments.AppointmentDisplayUtil;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsPayloadDTO;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.payments.models.LocationIndexDTO;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.payments.models.ProviderIndexDTO;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.PicassoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by cocampo on 2/10/17
 */

public class PatientListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int CELL_HEADER = 0;
    private static final int CELL_CARD = 1;

    private Context context;
    private List<CardViewPatient> allPatients;
    private List<CardViewPatient> filteredPatients;
    private OnItemTappedListener tapListener;
    private MapFilterModel filterModel;

    private int sizeFilteredPatients;
    private int sizeFilteredPendingPatients;
    private Section currentSection;
    private enum Section {
        APPOINTMENTS,
        PAYMENTS
    }

    private SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");

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
        this.currentSection = Section.PAYMENTS;
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
        this.currentSection = Section.APPOINTMENTS;
        loadPatients(checkInDTO);
        applyFilter();
    }

    @Override
    public int getItemViewType(int position) {
        final CardViewPatient patient = filteredPatients.get(position);
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
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.patient_list_header_layout, parent, false);
            return new HeaderViewHolder(view);
        }
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.patient_list_item_layout, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final CardViewPatient patient = filteredPatients.get(position);
        if (patient.raw == null) {
            bindHeaderViewHolder((HeaderViewHolder) holder, patient);
        } else {
            bindCardViewHolder((CardViewHolder) holder, patient);
        }
    }

    private void bindHeaderViewHolder(HeaderViewHolder holder, CardViewPatient patient) {
        holder.setTimeView(patient);
    }

    private void bindCardViewHolder(final CardViewHolder holder, final CardViewPatient patient) {
        holder.name.setText(StringUtil.captialize(patient.name));
        holder.balance.setText(patient.balance);
        holder.provider.setText(patient.providerName);
        holder.initials.setText(patient.initials);
        holder.bind(patient, tapListener);
        holder.setTimeView(patient);
        holder.itemView.setContentDescription(patient.name);
        holder.videoVisitIndicator.setVisibility(patient.isVideoVisit ? View.VISIBLE : View.GONE);
        if (this.currentSection == Section.APPOINTMENTS) { holder.setCellAvatar(patient); }

        PicassoHelper.get().loadImage(context, holder.profilePicture, holder.initials,
                this.currentSection == Section.APPOINTMENTS ? holder.cellAvatar : null, patient.photoUrl, 60);
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
        CardViewPatient header = null;
        for (CardViewPatient cardViewPatient : allPatients) {
            // Check filter by patient
            if (filterModel.isFilteringByPatients() && !patients.containsKey(cardViewPatient.id)) {
                continue;
            }
            // Check filter by provider
            if (filterModel.isFilteringByDoctors() && !doctors.containsKey(cardViewPatient.providerId)) {
                continue;
            }
            // Check filter by location
            if (filterModel.isFilteringByLocations() && !locations.containsKey(cardViewPatient.locationId)) {
                continue;
            }

            sizeFilteredPatients++;

            // Count pending and filter by pending
            if (cardViewPatient.isRequested) {
                sizeFilteredPendingPatients++;
            } else if (filterModel.isFilteringByPending()) {
                continue;
            }

            if (cardViewPatient.appointmentStartTime != null
                    && !DateUtil.isSameDay(dateTime, cardViewPatient.appointmentStartTime)) {
                dateTime = cardViewPatient.appointmentStartTime;
                if (countByDay % 2 == 1) {
                    filteredPatients.add(new CardViewPatient());
                }
                header = new CardViewPatient(dateTime);
                filteredPatients.add(header);
                filteredPatients.add(new CardViewPatient());
                countDifferentDates++;
                countByDay = 0;
            }

            if (header != null) {
                header.headCount++;
            }

            filteredPatients.add(cardViewPatient);
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
        List<PatientBalanceDTO> patientBalances = checkInDTO.getPayload().getPatientBalances();
        Map<String, String> profilePhotoMap = PracticeUtil.getProfilePhotoMap(patientBalances);
        Map<String, Double> totalBalanceMap = PracticeUtil.getTotalBalanceMap(patientBalances);
        this.allPatients = new ArrayList<>(appointments.size());

        for (AppointmentDTO appointmentDTO : appointments) {
            if (!appointmentDTO.getPayload().getAppointmentStatus().getCode().equals("C")) {

                // Set profile photo
                PatientModel patientDTO = appointmentDTO.getPayload().getPatient();
                patientDTO.setProfilePhoto(profilePhotoMap.get(patientDTO.getPatientId()));

                this.allPatients.add(new CardViewPatient(appointmentDTO, appointmentDTO.getPayload(),
                        totalBalanceMap.get(patientDTO.getPatientId())));
            }
        }

        sortListByDate(this.allPatients);
    }

    private void loadPatients(PaymentsModel paymentsModel) {
        List<PatientBalanceDTO> dtoList = paymentsModel.getPaymentPayload().getPatientBalances();
        Map<String, ProviderIndexDTO> providerMap = getProviderMap(paymentsModel.getPaymentPayload()
                .getProviderIndex());
        Map<String, LocationIndexDTO> locationMap = getLocationMap(paymentsModel.getPaymentPayload()
                .getLocationIndex());
        this.allPatients = new ArrayList<>(dtoList.size());

        for (PatientBalanceDTO dto : dtoList) {
            createPatient(providerMap, locationMap, dto);
        }
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

    private void createPatient(Map<String, ProviderIndexDTO> providerMap,
                               Map<String, LocationIndexDTO> locationMap,
                               PatientBalanceDTO dto) {
        List<PendingBalanceDTO> patientBalances = dto.getBalances();
        double balance = getBalance(patientBalances);
        String patientId = patientBalances.get(0).getMetadata().getPatientId();
        ProviderIndexDTO provider = providerMap.get(patientId);
        LocationIndexDTO location = locationMap.get(patientId);
        PatientModel personalDetails = dto.getDemographics().getPayload().getPersonalDetails();
        allPatients.add(new CardViewPatient(dto, patientId, provider, location, balance, personalDetails));
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

        for (LocationIndexDTO indexDTO : locationIndex) {
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

    /**
     * @return position of the nearest previous appointment time slot to the current time
     */
    public int getNearestTimeItemPosition() {
        int position = 0;
        Date bestTime = null;
        Date now = new Date();
        for (int i = 0; i < filteredPatients.size(); i++) {
            CardViewPatient patient = filteredPatients.get(i);
            if (patient.appointmentStartTime != null) {
                if (patient.appointmentStartTime.compareTo(now) > 0) {
                    break;
                }

                if (bestTime == null || bestTime.compareTo(patient.appointmentStartTime) <= 0) {
                    bestTime = patient.appointmentStartTime;
                    position = i;
                }
            }
        }

        return position;
    }

    class CardViewHolder extends RecyclerView.ViewHolder {

        TextView initials;
        TextView name;
        TextView provider;
        TextView balance;
        TextView timeTextView;
        ImageView profilePicture;
        View videoVisitIndicator;
        ImageView cellAvatar;

        /**
         * Constructor.
         *
         * @param view view
         */
        CardViewHolder(View view) {
            super(view);
            initials = view.findViewById(R.id.patient_short_name);
            name = view.findViewById(R.id.patient_name_text_view);
            provider = view.findViewById(R.id.provider_name_text_view);
            balance = view.findViewById(R.id.amount_text_view);
            timeTextView = view.findViewById(R.id.timeTextView);
            profilePicture = view.findViewById(R.id.patient_pic_image_view);
            cellAvatar = view.findViewById(R.id.cellAvatarImageView);
            videoVisitIndicator = view.findViewById(R.id.visit_type_video);
        }

        void bind(final CardViewPatient patient, final OnItemTappedListener tapListener) {
            if (null != tapListener) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tapListener.onItemTap(patient.raw);
                    }
                });
            }
        }

        void setTimeView(CardViewPatient patient) {
            if (null == patient.appointmentStartTime) {
                return;
            }

            timeTextView.setText(dateFormat.format(patient.appointmentStartTime));
            if (patient.isRequested) {
                timeTextView.setBackgroundResource(R.drawable.bg_orange_overlay);
            } else if (patient.isAppointmentOver) {
                timeTextView.setBackgroundResource(R.drawable.bg_red_overlay);
            } else {
                timeTextView.setBackgroundResource(R.drawable.bg_green_overlay);
            }

            timeTextView.setVisibility(View.VISIBLE);
        }

        void setCellAvatar(CardViewPatient patient) {
            AppointmentDisplayStyle style = AppointmentDisplayUtil.determineDisplayStyle(((AppointmentDTO) patient.raw).getPayload());
            switch (style) {
                case CHECKED_IN: {
                    initials.setTextColor(ContextCompat.getColor(context, R.color.white));
                    initials.setBackgroundResource(R.drawable.round_list_tv_green);
                    cellAvatar.setImageResource(R.drawable.icn_cell_avatar_badge_checked_in);
                    break;
                }
                case PENDING: {
                    initials.setTextColor(ContextCompat.getColor(context, R.color.emerald));
                    initials.setBackgroundResource(R.drawable.round_list_tv_green_border);
                    cellAvatar.setImageResource(R.drawable.icn_cell_avatar_badge_upcoming);
                    break;
                }
                case REQUESTED: {
                    initials.setTextColor(ContextCompat.getColor(context, R.color.white));
                    initials.setBackgroundResource(R.drawable.round_list_tv_orange);
                    cellAvatar.setImageResource(R.drawable.icn_cell_avatar_badge_pending);
                    break;
                }
                case MISSED: {
                    initials.setTextColor(ContextCompat.getColor(context, R.color.white));
                    initials.setBackgroundResource(R.drawable.round_list_tv_red);
                    cellAvatar.setImageResource(R.drawable.icn_cell_avatar_badge_missed);
                    break;
                }
                case CANCELED: {
                    initials.setTextColor(ContextCompat.getColor(context, R.color.lightSlateGray));
                    initials.setBackgroundResource(R.drawable.round_list_tv);
                    cellAvatar.setImageResource(R.drawable.icn_cell_avatar_badge_canceled);
                    break;
                }
                case PENDING_UPCOMING: {
                    initials.setTextColor(ContextCompat.getColor(context, R.color.emerald));
                    initials.setBackgroundResource(R.drawable.round_list_tv_green_border);
                    cellAvatar.setImageResource(R.drawable.icn_cell_avatar_badge_upcoming);
                    break;
                }
                case REQUESTED_UPCOMING: {
                    initials.setTextColor(ContextCompat.getColor(context, R.color.white));
                    initials.setBackgroundResource(R.drawable.round_list_tv_orange);
                    cellAvatar.setImageResource(R.drawable.icn_cell_avatar_badge_pending);
                    break;
                }
                case CANCELED_UPCOMING: {
                    initials.setTextColor(ContextCompat.getColor(context, R.color.lightSlateGray));
                    initials.setBackgroundResource(R.drawable.round_list_tv);
                    cellAvatar.setImageResource(R.drawable.icn_cell_avatar_badge_canceled);
                    break;
                }
                case CHECKED_OUT: {
                    initials.setTextColor(ContextCompat.getColor(context, R.color.white));
                    initials.setBackgroundResource(R.drawable.round_tv);
                    cellAvatar.setImageResource(R.drawable.icn_cell_avatar_badge_checked_out);
                    break;
                }
                default: {
                    cellAvatar.setVisibility(View.GONE);
                }
            }

        }
    }

    private class HeaderViewHolder extends CardViewHolder {
        CarePayTextView dateTextView;

        HeaderViewHolder(View view) {
            super(view);
            dateTextView = (CarePayTextView) view.findViewById(R.id.timeTextView);
        }

        void setTimeView(CardViewPatient patient) {
            String text = "";
            if (null != patient.appointmentStartTime) {
                text = DateUtil.getInstance().setDate(patient.appointmentStartTime).getDateAsMonthLiteralDayOrdinal();

                if (patient.headCount > 0) {
                    text += " (" + patient.headCount + ")";
                }
            }

            dateTextView.setText(text);
        }
    }
}
