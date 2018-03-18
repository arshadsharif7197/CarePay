package com.carecloud.carepay.practice.library.checkin.adapters;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.appointments.adapters.CardViewPatient;
import com.carecloud.carepay.practice.library.checkin.dtos.CheckInDTO;
import com.carecloud.carepay.practice.library.checkin.filters.FilterDataDTO;
import com.carecloud.carepay.practice.library.models.FilterModel;
import com.carecloud.carepay.practice.library.models.MapFilterModel;
import com.carecloud.carepay.practice.library.util.PracticeUtil;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsPayloadDTO;
import com.carecloud.carepaylibray.appointments.models.CheckinStatusDTO;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.IllegalFormatException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CheckedInAppointmentAdapter extends RecyclerView.Adapter<CheckedInAppointmentAdapter.CartViewHolder> {

    public interface CheckinItemCallback {
        void onCheckInItemClick(AppointmentsPayloadDTO appointmentsPayloadDTO, int theRoom);
    }

    private static final int MAX_CHECKIN_PROGRESS = 6;
    private static final int MAX_CHECKOUT_PROGRESS = 4;

    private Context context;
    private List<CardViewPatient> allPatients;
    private List<CardViewPatient> filteredPatients;
    private MapFilterModel filterModel;
    private CheckinItemCallback callback;

    public static final int CHECKING_IN = 0;
    public static final int CHECKED_IN = 1;
    public static final int CHECKING_OUT = 2;
    public static final int CHECKED_OUT = 3;

    private int theRoom;
    private static final String timeElapsedLabel = Label.getLabel("practice_checkin_complete_elapsed");

    /**
     * Constructor
     * @param context context
     * @param checkInDTO checkInDTO
     * @param callback select card callback
     * @param room room
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
        View view = LayoutInflater.from(context).inflate(R.layout.card_queue, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CheckedInAppointmentAdapter.CartViewHolder holder, final int position) {
        final CardViewPatient patient = filteredPatients.get(position);

        holder.patientName.setText(StringUtil.captialize(patient.name));
        holder.shortName.setText(StringUtil.getShortName(patient.name));
        holder.providerName.setText(StringUtil.captialize(patient.providerName));

        DateUtil dateInstance = DateUtil.getInstance().setDate(patient.appointmentStartTime);
        holder.appointmentTime.setText(dateInstance.getTime12Hour());
        holder.appointmentTime.setTextColor(ContextCompat.getColor(context, R.color.optional_gray));

        Picasso.with(context)
                .load(patient.photoUrl)
                .resize(60, 60)
                .centerCrop()
                .transform(new CircleImageTransform())
                .into(holder.imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.imageView.setVisibility(View.VISIBLE);
                        holder.shortName.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        holder.imageView.setVisibility(View.GONE);
                        holder.shortName.setVisibility(View.VISIBLE);
                    }
                });

        StatusView statusView;
        switch (theRoom){
            case CHECKING_IN:
                holder.itemView.setOnLongClickListener(holder);
                holder.appointmentTime.setTextColor(ContextCompat.getColor(context, R.color.emerald));
                holder.progressIndicator.setProgressDrawable(ContextCompat.getDrawable(context, R.drawable.queue_checkin_progress_background));
                holder.elapsedTime.setText(DateUtil.getTimeElapsed(patient.lastUpdate, new Date()));

                statusView = getLastCheckinProgress(patient.checkinStatus);
                holder.progressIndicator.setMax(MAX_CHECKIN_PROGRESS);
                holder.progressIndicator.setProgress(statusView.progress);
                holder.additionalInfo.setText(statusView.getStatus());
                break;
            case CHECKED_IN:
                holder.itemView.setOnLongClickListener(holder);
                holder.progressIndicator.setProgressDrawable(ContextCompat.getDrawable(context, R.drawable.queue_checkin_progress_background));
                holder.progressIndicator.setMax(MAX_CHECKIN_PROGRESS);
                holder.progressIndicator.setProgress(MAX_CHECKIN_PROGRESS);
                holder.additionalInfo.setText(patient.balance);
                holder.elapsedTime.setText(getFormattedTimeElapsed(DateUtil.getContextualTimeElapsed(patient.lastUpdate, new Date())));
                break;
            case CHECKING_OUT:
                holder.progressIndicator.setProgressDrawable(ContextCompat.getDrawable(context, R.drawable.queue_checkout_progress_background));
                holder.elapsedTime.setText(DateUtil.getTimeElapsed(patient.lastUpdate, new Date()));

                statusView = getLastCheckoutProgress(patient.checkinStatus);
                holder.progressIndicator.setMax(MAX_CHECKOUT_PROGRESS);
                holder.progressIndicator.setProgress(statusView.progress);
                holder.additionalInfo.setText(patient.balance);
                break;
            case CHECKED_OUT:
            default:
                holder.progressIndicator.setProgressDrawable(ContextCompat.getDrawable(context, R.drawable.queue_checkout_progress_background));
                holder.progressIndicator.setMax(MAX_CHECKOUT_PROGRESS);
                holder.progressIndicator.setProgress(MAX_CHECKOUT_PROGRESS);
                holder.additionalInfo.setText(patient.balance);
                holder.elapsedTime.setText(null);
                break;
        }

        holder.appointmentId = patient.appointmentId;
        holder.isWaitingRoom = patient.isCheckedIn;
        holder.appointmentsPayloadDTO = (AppointmentsPayloadDTO) patient.raw;
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

    private static String getFormattedTimeElapsed(String timeElapsed){
        try{
            return String.format(timeElapsedLabel, timeElapsed);
        }catch (IllegalFormatException ife){
            return null;
        }
    }


    private static StatusView getLastCheckinProgress(CheckinStatusDTO status) {
        StatusView statusView = new StatusView();
        if (status != null) {
            final boolean demographicsComplete = CarePayConstants.APPOINTMENTS_STATUS_COMPLETED
                    .equalsIgnoreCase(status.getDemographicsStatus());
            final boolean consentComplete = CarePayConstants.APPOINTMENTS_STATUS_COMPLETED
                    .equalsIgnoreCase(status.getConsentFormsStatus());
            final boolean medicationsComplete = CarePayConstants.APPOINTMENTS_STATUS_COMPLETED
                    .equalsIgnoreCase(status.getMedicationsStatus());
            final boolean intakeComplete = CarePayConstants.APPOINTMENTS_STATUS_COMPLETED
                    .equalsIgnoreCase(status.getIntakeStatus());

            if(intakeComplete){
                statusView.status = Label.getLabel("practice_checkin_detail_dialog_payment");
                statusView.progress = 5;
            }else if(medicationsComplete){
                statusView.status = Label.getLabel("practice_checkin_detail_dialog_intake");
                statusView.progress = 4;
            }else if(consentComplete){
                statusView.status = Label.getLabel("practice_checkin_detail_dialog_medications");
                statusView.progress = 3;
            }else if(demographicsComplete){
                statusView.status = Label.getLabel("practice_checkin_detail_dialog_consent_forms");
                statusView.progress = 2;
            }else{
                statusView.status = Label.getLabel("practice_checkin_detail_dialog_demographics");
                statusView.progress = 1;
            }

        }
        return statusView;
    }

    private static StatusView getLastCheckoutProgress(CheckinStatusDTO status) {
        StatusView statusView = new StatusView();
        if (status != null) {
            final boolean appointmentComplete = CarePayConstants.APPOINTMENTS_STATUS_COMPLETED
                    .equalsIgnoreCase(status.getCheckoutAppointmentStatus());
            final boolean formsComplete = CarePayConstants.APPOINTMENTS_STATUS_COMPLETED
                    .equalsIgnoreCase(status.getCheckoutFormsStatus());
            final boolean paymentComplete = CarePayConstants.APPOINTMENTS_STATUS_COMPLETED
                    .equalsIgnoreCase(status.getCheckoutPaymentStatus());

            if(paymentComplete){
                statusView.progress = 4;
            }else if(formsComplete){
                statusView.progress = 3;
            }else if(appointmentComplete){
                statusView.progress = 2;
            }else{
                statusView.progress = 1;
            }

        }
        return statusView;
    }


    class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        ProgressBar progressIndicator;
        TextView shortName;
        ImageView imageView;
        TextView patientName;
        TextView providerName;
        TextView additionalInfo;
        TextView elapsedTime;
        TextView appointmentTime;

        String appointmentId;
        boolean isWaitingRoom;
        AppointmentsPayloadDTO appointmentsPayloadDTO;

        /**
         * Constructor.
         *
         * @param view view
         */

        CartViewHolder(View view) {
            super(view);
            progressIndicator = (ProgressBar) view.findViewById(R.id.progress_indicator);
            shortName = (TextView) view.findViewById(R.id.patient_short_name);
            imageView = (ImageView) view.findViewById(R.id.patient_pic_image_view);
            patientName = (TextView) view.findViewById(R.id.patient_name_text_view);
            providerName = (TextView) view.findViewById(R.id.provider_name_text_view);
            additionalInfo = (TextView) view.findViewById(R.id.infoTextView);
            elapsedTime = (TextView) view.findViewById(R.id.timeElapsedTextView);
            appointmentTime = (TextView) view.findViewById(R.id.timeTextView);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            callback.onCheckInItemClick(appointmentsPayloadDTO, theRoom);
        }

        @Override
        public boolean onLongClick(View view) {
            // Create a new ClipData.
            // This is done in two steps to provide clarity. The convenience method
            // ClipData.newPlainText() can create a plain text ClipData in one step.

            // Create a new ClipData.Item from the ImageView object's tag

            ClipData.Item item = new ClipData.Item(appointmentId);

            // Create a new ClipData using the tag as a label, the plain text MIME type, and
            // the already-created item. This will create a new ClipDescription object within the
            // ClipData, and set its MIME type entry to "text/plain"
            ClipData dragData = new ClipData(appointmentId, new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, item);
            double rotationRad = Math.toRadians(view.getRotation());
            int scaledWidth = (int) (view.getWidth() * view.getScaleX());
            int scaledHeight = (int) (view.getHeight() * view.getScaleY());
            double sinRotation = Math.abs(Math.sin(rotationRad));
            double cosRotation = Math.abs(Math.cos(rotationRad));
            final int width = (int) (scaledWidth * cosRotation + scaledHeight * sinRotation);
            final int height = (int) (scaledWidth * sinRotation + scaledHeight * cosRotation);

            // Instantiates the drag shadow builder.
            View.DragShadowBuilder myShadow = new View.DragShadowBuilder(view) {
                @Override
                public void onDrawShadow(Canvas canvas) {
                    if (!isWaitingRoom) {
                        canvas.rotate(3, 0, height / 2);
                    }
                    super.onDrawShadow(canvas);
                }

                @Override
                public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
                    shadowSize.set(width + 100, height + 100);
                    shadowTouchPoint.set(shadowSize.x / 2, shadowSize.y / 2);
                }
            };

            // Starts the drag

            view.startDrag(dragData,  // the data to be dragged
                    myShadow,  // the drag shadow builder
                    null,      // no need to use local data
                    0          // flags (not currently used, set to 0)
            );
            return true;
        }

    }

    private static class StatusView {
        String status = null;
        int progress = -1;

        String getStatus(){
            return "- " + status;
        }
    }
}
