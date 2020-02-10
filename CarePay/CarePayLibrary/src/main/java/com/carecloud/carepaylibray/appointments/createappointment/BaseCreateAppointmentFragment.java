package com.carecloud.carepaylibray.appointments.createappointment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.interfaces.ScheduleAppointmentInterface;
import com.carecloud.carepaylibray.appointments.models.AppointmentAvailabilityDataDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentAvailabilityMetadataDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentAvailabilityPayloadDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesItemDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.appointments.models.PracticePatientIdsDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.PicassoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * @author pjohnson on 1/15/19.
 */
public abstract class BaseCreateAppointmentFragment extends BaseDialogFragment implements CreateAppointmentFragmentInterface {

    protected UserPracticeDTO selectedPractice;
    protected AppointmentResourcesItemDTO selectedResource;
    protected VisitTypeDTO selectedVisitType;
    protected LocationDTO selectedLocation;
    protected ScheduleAppointmentInterface callback;
    protected AppointmentsResultModel appointmentsModelDto;
    protected String patientId;

    private TextView providersNoDataTextView;
    private TextView visitTypeNoDataTextView;
    private TextView locationNoDataTextView;
    private View providerContainer;
    private View visitTypeContainer;
    private View locationContainer;
    private Button checkAvailabilityButton;
    protected boolean isReschedule;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AppointmentViewHandler) {
            callback = ((AppointmentViewHandler) context).getAppointmentPresenter();
        } else if (context instanceof ScheduleAppointmentInterface) {
            callback = (ScheduleAppointmentInterface) context;
        } else {
            throw new ClassCastException("context must implement AppointmentViewHandler.");
        }
    }

    @Override
    public void onDetach() {
        callback = null;
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        appointmentsModelDto = (AppointmentsResultModel) callback.getDto();
        Bundle args = getArguments();
        if (args == null) {
            args = new Bundle();
        }
        isReschedule = args.getBoolean("isReschedule", false);
        if (isReschedule) {
            UserPracticeDTO practice = DtoHelper.getConvertedDTO(UserPracticeDTO.class, args);
            selectedPractice = appointmentsModelDto.getPayload().getPractice(practice.getPracticeId());
            selectedResource = DtoHelper.getConvertedDTO(AppointmentResourcesItemDTO.class, args);
            selectedVisitType = DtoHelper.getConvertedDTO(VisitTypeDTO.class, args);
            selectedLocation = DtoHelper.getConvertedDTO(LocationDTO.class, args);
        } else {
            selectedPractice = appointmentsModelDto.getPayload().getUserPractices().get(0);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpUI(view);
    }

    private void setUpUI(View view) {
        providersNoDataTextView = view.findViewById(R.id.providersNoDataTextView);
        providerContainer = view.findViewById(R.id.providerContainer);
        providersNoDataTextView.setOnClickListener(v -> showProviderList(selectedPractice, selectedVisitType, selectedLocation));
        providerContainer.setOnClickListener(v -> showProviderList(selectedPractice, selectedVisitType, selectedLocation));

        visitTypeNoDataTextView = view.findViewById(R.id.visitTypeNoDataTextView);
        visitTypeContainer = view.findViewById(R.id.visitTypeContainer);
        visitTypeNoDataTextView.setOnClickListener(v -> showVisitTypeList(selectedPractice, selectedResource, selectedLocation));
        visitTypeContainer.setOnClickListener(v -> showVisitTypeList(selectedPractice, selectedResource, selectedLocation));


        locationNoDataTextView = view.findViewById(R.id.locationNoDataTextView);
        locationContainer = view.findViewById(R.id.locationContainer);
        locationNoDataTextView.setOnClickListener(v -> showLocationList(selectedPractice, selectedResource, selectedVisitType));
        locationContainer.setOnClickListener(v -> showLocationList(selectedPractice, selectedResource, selectedVisitType));

        checkAvailabilityButton = view.findViewById(R.id.checkAvailabilityButton);
        checkAvailabilityButton.setOnClickListener(v -> callAvailabilityService());

        if (isReschedule) {
            setResourceProvider(selectedResource);
            setVisitType(selectedVisitType);
            setLocation(selectedLocation);
            view.findViewById(R.id.practicesRecyclerView).setVisibility(View.GONE);
        }
    }

    protected void callAvailabilityService() {
        AppointmentAvailabilityPayloadDTO payload = new AppointmentAvailabilityPayloadDTO();
        payload.setLocation(selectedLocation);
        payload.setResource(selectedResource);
        payload.setVisitReason(selectedVisitType);
        AppointmentAvailabilityDataDTO appointmentAvailabilityDataDTO = new AppointmentAvailabilityDataDTO();
        ArrayList<AppointmentAvailabilityPayloadDTO> payloadList = new ArrayList<>();
        payloadList.add(payload);
        AppointmentAvailabilityMetadataDTO metadataDTO = new AppointmentAvailabilityMetadataDTO();
        metadataDTO.setPracticeId(selectedPractice.getPracticeId());
        metadataDTO.setPracticeMgmt(selectedPractice.getPracticeMgmt());
        appointmentAvailabilityDataDTO.setMetadata(metadataDTO);
        appointmentAvailabilityDataDTO.setPayload(payloadList);
        appointmentsModelDto.getPayload().setAppointmentAvailability(appointmentAvailabilityDataDTO);
        showAvailabilityFragment();
    }

    protected abstract void showAvailabilityFragment();

    protected abstract void showLocationList(UserPracticeDTO selectedPractice,
                                             AppointmentResourcesItemDTO selectedProvider,
                                             VisitTypeDTO selectedVisitType);

    protected abstract void showVisitTypeList(UserPracticeDTO selectedPractice,
                                              AppointmentResourcesItemDTO selectedProvider,
                                              LocationDTO selectedLocation);

    protected abstract void showProviderList(UserPracticeDTO selectedPractice,
                                             VisitTypeDTO selectedVisitType,
                                             LocationDTO selectedLocation);

    protected void resetForm() {
        resetProvider();
        resetVisitType();
        resetLocation();
        checkIfButtonEnabled();
    }

    @Override
    public void setResourceProvider(AppointmentResourcesItemDTO resource) {
        selectedResource = resource;
        providersNoDataTextView.setVisibility(View.GONE);
        String providerName = resource.getProvider().getName();
        String speciality = resource.getProvider().getSpecialty().getName();
        setCardViewContent(providerContainer, providerName, speciality, true, resource.getProvider().getPhoto());

        ImageView deleteImageView = providerContainer.findViewById(R.id.deleteImageView);
        deleteImageView.setOnClickListener(v -> {
            resetProvider();
            checkIfButtonEnabled();
        });
        checkIfButtonEnabled();
    }

    @Override
    public void setVisitType(VisitTypeDTO visitType) {
        selectedVisitType = visitType;
        visitTypeNoDataTextView.setVisibility(View.GONE);
        String title = StringUtil.capitalize(visitType.getName());
        String subtitle;
        TextView subTitleTextView = visitTypeContainer.findViewById(R.id.subTitleTextView);
        if (visitType.getAmount() > 0) {
            subTitleTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            subtitle = String.format(Label
                            .getLabel("createAppointment.visitTypeList.item.label.prepaymentMessage"),
                    NumberFormat.getCurrencyInstance(Locale.US).format(visitType.getAmount()));
        } else {
            subTitleTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.lightSlateGray));
            subtitle = Label.getLabel("createAppointment.visitTypeList.item.label.noPrepaymentMessage");
        }
        setCardViewContent(visitTypeContainer, title, subtitle, false, null);

        ImageView deleteImageView = visitTypeContainer.findViewById(R.id.deleteImageView);
        deleteImageView.setOnClickListener(v -> {
            resetVisitType();
            checkIfButtonEnabled();
        });
        checkIfButtonEnabled();
    }

    @Override
    public void setLocation(LocationDTO locationDTO) {
        selectedLocation = locationDTO;
        locationNoDataTextView.setVisibility(View.GONE);
        String title = StringUtil.capitalize(locationDTO.getName());
        String subtitle = locationDTO.getAddress().geAddressStringWithShortZipWOCounty2Lines();
        setCardViewContent(locationContainer, title, subtitle, false, null);

        ImageView deleteImageView = locationContainer.findViewById(R.id.deleteImageView);
        deleteImageView.setOnClickListener(v -> {
            resetLocation();
            checkIfButtonEnabled();
        });
        checkIfButtonEnabled();
    }

    private void setCardViewContent(View view, String title, String subtitle, boolean showImage, String imageUrl) {
        view.setVisibility(View.VISIBLE);
        TextView titleTextView = view.findViewById(R.id.titleTextView);
        titleTextView.setText(title);
        TextView subTitleTextView = view.findViewById(R.id.subTitleTextView);
        subTitleTextView.setText(subtitle);
        if (StringUtil.isNullOrEmpty(subtitle)) {
            subTitleTextView.setVisibility(View.GONE);
        } else {
            subTitleTextView.setVisibility(View.VISIBLE);
        }
        TextView shortNameTextView = view.findViewById(R.id.shortNameTextView);
        ImageView picImageView = view.findViewById(R.id.picImageView);
        if (showImage) {
            shortNameTextView.setText(StringUtil.getShortName(title));
            if (!StringUtil.isNullOrEmpty(imageUrl)) {
                PicassoHelper.get().loadImage(getContext(), picImageView,
                        shortNameTextView, imageUrl);
            } else {
                shortNameTextView.setVisibility(View.VISIBLE);
                picImageView.setVisibility(View.INVISIBLE);
            }
        } else {
            shortNameTextView.setVisibility(View.GONE);
            picImageView.setVisibility(View.GONE);
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) ((LinearLayout) titleTextView.getParent())
                    .getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_START);
        }
    }

    private void resetProvider() {
        selectedResource = null;
        providerContainer.setVisibility(View.GONE);
        providersNoDataTextView.setVisibility(View.VISIBLE);
    }

    private void resetLocation() {
        selectedLocation = null;
        locationContainer.setVisibility(View.GONE);
        locationNoDataTextView.setVisibility(View.VISIBLE);
    }

    private void resetVisitType() {
        selectedVisitType = null;
        visitTypeContainer.setVisibility(View.GONE);
        visitTypeNoDataTextView.setVisibility(View.VISIBLE);
    }

    private void checkIfButtonEnabled() {
        checkAvailabilityButton.setEnabled(selectedResource != null
                && selectedVisitType != null
                && selectedLocation != null);
    }

    private String getPatientId(String practiceId) {
        PracticePatientIdsDTO[] practicePatientIdArray = ApplicationPreferences.getInstance()
                .getObjectFromSharedPreferences(CarePayConstants.KEY_PRACTICE_PATIENT_IDS,
                        PracticePatientIdsDTO[].class);
        for (PracticePatientIdsDTO practicePatientId : practicePatientIdArray) {
            if (practicePatientId.getPracticeId().equals(practiceId)) {
                return practicePatientId.getPatientId();
            }
        }
        return null;
    }
}
