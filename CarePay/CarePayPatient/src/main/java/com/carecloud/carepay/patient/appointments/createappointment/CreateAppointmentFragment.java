package com.carecloud.carepay.patient.appointments.createappointment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.appointments.adapters.PracticesAdapter;
import com.carecloud.carepay.patient.appointments.createappointment.location.LocationListFragment;
import com.carecloud.carepay.patient.appointments.createappointment.provider.ProviderListFragment;
import com.carecloud.carepay.patient.appointments.createappointment.visitType.VisitTypeListFragment;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesItemDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.PicassoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author pjohnson on 1/15/19.
 */
public class CreateAppointmentFragment extends BaseFragment implements CreateAppointmentFragmentInterface {

    private UserPracticeDTO selectedPractice;
    private AppointmentResourcesItemDTO selectedResource;
    private VisitTypeDTO selectedVisitType;
    private LocationDTO selectedLocation;
    private CreateAppointmentInterface callback;
    private AppointmentsResultModel appointmentsModelDto;
    private TextView providersNoDataTextView;
    private TextView visitTypeNoDataTextView;
    private TextView locationNoDataTextView;
    private View providerContainer;
    private View visitTypeContainer;
    private View locationContainer;
    private Button checkAvailabilityButton;

    public static CreateAppointmentFragment newInstance() {
        return new CreateAppointmentFragment();
    }

    public static CreateAppointmentFragment newInstance(UserPracticeDTO userPracticeDTO,
                                                        AppointmentResourcesItemDTO selectedResource,
                                                        VisitTypeDTO selectedVisitTypeDTO,
                                                        LocationDTO selectedLocation) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, userPracticeDTO);
        DtoHelper.bundleDto(args, selectedResource);
        DtoHelper.bundleDto(args, selectedVisitTypeDTO);
        DtoHelper.bundleDto(args, selectedLocation);
        CreateAppointmentFragment fragment = new CreateAppointmentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CreateAppointmentInterface) {
            callback = (CreateAppointmentInterface) context;
        } else {
            throw new ClassCastException("context must implement CreateAppointmentInterface.");
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
        if (args != null) {
            UserPracticeDTO practice = DtoHelper.getConvertedDTO(UserPracticeDTO.class, args);
            selectedPractice = appointmentsModelDto.getPayload().getPractice(practice.getPracticeId());
            selectedResource = DtoHelper.getConvertedDTO(AppointmentResourcesItemDTO.class, args);
            selectedVisitType = DtoHelper.getConvertedDTO(VisitTypeDTO.class, args);
            selectedLocation = DtoHelper.getConvertedDTO(LocationDTO.class, args);
        } else {
            selectedPractice = appointmentsModelDto.getPayload().getUserPractices().get(0);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_appointment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpToolbar(view);
        showPracticeList(view);
        setUpUI(view);
    }

    private void setUpUI(View view) {
        providersNoDataTextView = view.findViewById(R.id.providersNoDataTextView);
        providersNoDataTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProviderList(selectedPractice, selectedVisitType, selectedLocation);
            }
        });

        visitTypeNoDataTextView = view.findViewById(R.id.visitTypeNoDataTextView);
        visitTypeNoDataTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showVisitTypeList(selectedPractice, selectedResource, selectedLocation);
            }
        });

        locationNoDataTextView = view.findViewById(R.id.locationNoDataTextView);
        locationNoDataTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLocationList(selectedPractice, selectedResource, selectedVisitType);
            }
        });
        providerContainer = view.findViewById(R.id.providerContainer);
        visitTypeContainer = view.findViewById(R.id.visitTypeContainer);
        locationContainer = view.findViewById(R.id.locationContainer);
        checkAvailabilityButton = view.findViewById(R.id.checkAvailabilityButton);
        checkAvailabilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callAvailabilityService();
            }
        });

        if (getArguments() != null) {
            setResourceProvider(selectedResource);
            setVisitType(selectedVisitType);
            setLocation(selectedLocation);
        }
    }

    private void callAvailabilityService() {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_mgmt", selectedPractice.getPracticeMgmt());
        queryMap.put("practice_id", selectedPractice.getPracticeId());
        queryMap.put("visit_reason_id", String.valueOf(selectedVisitType.getId()));
        queryMap.put("resource_ids", String.valueOf(selectedResource.getId()));
        queryMap.put("location_ids", String.valueOf(selectedLocation.getId()));
        TransitionDTO transitionDTO = appointmentsModelDto.getMetadata().getLinks().getAppointmentAvailability();
        getWorkflowServiceHelper().execute(transitionDTO, new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                showProgressDialog();
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                hideProgressDialog();
                AppointmentsResultModel availabilityDto = DtoHelper.getConvertedDTO(AppointmentsResultModel.class, workflowDTO);
                availabilityDto.getPayload().getAppointmentAvailability().getPayload().get(0)
                        .getResource().getProvider().setPhoto(selectedResource.getProvider().getPhoto());
                availabilityDto.getPayload().getAppointmentAvailability().getPayload().get(0).getVisitReason().setAmount(selectedVisitType.getAmount());
                appointmentsModelDto.getPayload().setAppointmentAvailability(availabilityDto.getPayload().getAppointmentAvailability());
                callback.showAvailabilityHourFragment();
            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();
                showErrorNotification(exceptionMessage);
                Log.e(getString(R.string.alert_title_server_error), exceptionMessage);
            }
        }, queryMap);
    }

    private void setUpToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar_layout);
        toolbar.setNavigationIcon(R.drawable.icn_nav_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        TextView title = toolbar.findViewById(R.id.respons_toolbar_title);
        title.setText(Label.getLabel("appointments_heading"));
        callback.displayToolbar(false, null);
    }

    private void showLocationList(UserPracticeDTO selectedPractice,
                                  AppointmentResourcesItemDTO selectedProvider,
                                  VisitTypeDTO selectedVisitType) {
        LocationListFragment fragment = LocationListFragment
                .newInstance(selectedPractice, selectedVisitType, selectedProvider);
        callback.showFragment(fragment);
    }

    private void showVisitTypeList(UserPracticeDTO selectedPractice,
                                   AppointmentResourcesItemDTO selectedProvider,
                                   LocationDTO selectedLocation) {
        VisitTypeListFragment fragment = VisitTypeListFragment
                .newInstance(selectedPractice, selectedLocation, selectedProvider);
        callback.showFragment(fragment);
    }

    private void showProviderList(UserPracticeDTO selectedPractice,
                                  VisitTypeDTO selectedVisitType,
                                  LocationDTO selectedLocation) {
        ProviderListFragment fragment = ProviderListFragment
                .newInstance(selectedPractice, selectedVisitType, selectedLocation);
        callback.showFragment(fragment);
    }

    private void showPracticeList(View view) {
        RecyclerView practicesRecyclerView = view.findViewById(R.id.practicesRecyclerView);
        if (appointmentsModelDto.getPayload().getUserPractices().size() > 1) {
            practicesRecyclerView.setVisibility(View.VISIBLE);
            practicesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                    LinearLayoutManager.HORIZONTAL, false));
            PracticesAdapter adapter = new PracticesAdapter(appointmentsModelDto.getPayload().getUserPractices());
            adapter.setCallback(new PracticesAdapter.PracticeSelectInterface() {
                @Override
                public void onPracticeSelected(UserPracticeDTO userPracticeDTO) {
                    if (!selectedPractice.getPracticeId().equals(userPracticeDTO.getPracticeId())) {
                        resetForm();
                    }
                    selectedPractice = userPracticeDTO;
                }
            });
            practicesRecyclerView.setAdapter(adapter);
        } else {
            practicesRecyclerView.setVisibility(View.GONE);
        }
    }

    private void resetForm() {
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
        deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetProvider();
                checkIfButtonEnabled();
            }
        });
        checkIfButtonEnabled();
    }

    @Override
    public void setVisitType(VisitTypeDTO visitType) {
        selectedVisitType = visitType;
        visitTypeNoDataTextView.setVisibility(View.GONE);
        String title = StringUtil.capitalize(visitType.getName());
        String subtitle = null;
        if (visitType.getAmount() > 0) {
            subtitle = String.format(Label
                            .getLabel("createAppointment.visitTypeList.item.label.prepaymentMessage"),
                    NumberFormat.getCurrencyInstance(Locale.US).format(visitType.getAmount()));
        }
        setCardViewContent(visitTypeContainer, title, subtitle, false, null);
        TextView subTitleTextView = visitTypeContainer.findViewById(R.id.subTitleTextView);
        subTitleTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));

        ImageView deleteImageView = visitTypeContainer.findViewById(R.id.deleteImageView);
        deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetVisitType();
                checkIfButtonEnabled();
            }
        });
        checkIfButtonEnabled();
    }

    @Override
    public void setLocation(LocationDTO locationDTO) {
        selectedLocation = locationDTO;
        locationNoDataTextView.setVisibility(View.GONE);
        String title = StringUtil.capitalize(locationDTO.getName());
        String subtitle = locationDTO.getAddress().geAddressStringWithShortZipWOCounty();
        setCardViewContent(locationContainer, title, subtitle, false, null);

        ImageView deleteImageView = locationContainer.findViewById(R.id.deleteImageView);
        deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetLocation();
                checkIfButtonEnabled();
            }
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
            PicassoHelper.get().loadImage(getContext(), picImageView,
                    shortNameTextView, imageUrl);
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
}
