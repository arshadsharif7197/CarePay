package com.carecloud.carepay.patient.appointments.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.appointments.adapters.ProviderAdapter;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.fragments.BaseAppointmentFragment;
import com.carecloud.carepaylibray.appointments.interfaces.ProviderInterface;
import com.carecloud.carepaylibray.appointments.models.AppointmentPayloadModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentSectionHeaderModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.ResourcesToScheduleDTO;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChooseProviderFragment extends BaseAppointmentFragment
        implements ProviderAdapter.OnProviderListItemClickListener {

    private RecyclerView providersRecyclerView;
    private AppointmentsResultModel appointmentsResultModel;
    private AppointmentsResultModel resourcesToScheduleModel;

    private ChooseProviderFragment chooseProviderFragment;
    private List<AppointmentResourcesDTO> resources;

    private ProviderInterface callback;

    /**
     * @param appointmentsResultModel the model
     * @param practiceMgmt
     * @param practiceId              @return a new instance of ChooseProviderFragment
     */
    public static ChooseProviderFragment newInstance(AppointmentsResultModel appointmentsResultModel,
                                                     String practiceMgmt, String practiceId) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, appointmentsResultModel);
        args.putString("practiceMgmt", practiceMgmt);
        args.putString("practiceId", practiceId);
        ChooseProviderFragment fragment = new ChooseProviderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        attachCallback(context);
    }

    @Override
    protected void attachCallback(Context context) {
        try {
            if (context instanceof AppointmentViewHandler) {
                callback = ((AppointmentViewHandler) context).getAppointmentPresenter();
            } else {
                callback = (ProviderInterface) context;
            }
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached context must implement ProviderInterface");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (callback == null) {
            attachCallback(getContext());
        }
    }

    @Override
    @SuppressLint("InflateParams")
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appointmentsResultModel = DtoHelper.getConvertedDTO(AppointmentsResultModel.class, getArguments());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_choose_provider, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        chooseProviderFragment = this;


        Toolbar toolbar = (Toolbar) view.findViewById(R.id.add_appointment_toolbar);
        TextView titleView = (TextView) toolbar.findViewById(R.id.add_appointment_toolbar_title);
        titleView.setText(Label.getLabel("choose_provider_heading"));
        titleView.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        toolbar.setTitle("");

        Drawable closeIcon = ContextCompat.getDrawable(getActivity(), R.drawable.icn_nav_back);
        toolbar.setNavigationIcon(closeIcon);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDefaultActionBar();
                getActivity().onBackPressed();
            }
        });

        providersRecyclerView = ((RecyclerView) view.findViewById(R.id.providers_recycler_view));

        //Fetch provider data
        getResourcesInformation();
    }

    private void getResourcesInformation() {
        TransitionDTO resourcesToSchedule = appointmentsResultModel.getMetadata()
                .getLinks().getResourcesToSchedule();
        Map<String, String> queryMap = new HashMap<>();
        String practiceId = getArguments().getString("practiceId");
        String practiceMgmt = getArguments().getString("practiceMgmt");
        if (practiceId != null) {
            queryMap.put("practice_id", practiceId);
        }
        if (practiceMgmt != null) {
            queryMap.put("practice_mgmt", practiceMgmt);
        }
        getWorkflowServiceHelper().execute(resourcesToSchedule, scheduleResourcesCallback, queryMap);
    }

    private WorkflowServiceCallback scheduleResourcesCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            Gson gson = new Gson();
            resourcesToScheduleModel = gson.fromJson(workflowDTO.toString(), AppointmentsResultModel.class);

            if (resourcesToScheduleModel != null && !resourcesToScheduleModel.getPayload()
                    .getResourcesToSchedule().isEmpty()) {
                resources = getResourcesFromPayload(resourcesToScheduleModel.getPayload());
                if (resources.size() > 0) {
                    Collections.sort(resources, new Comparator<AppointmentResourcesDTO>() {
                        @Override
                        public int compare(final AppointmentResourcesDTO object1, final AppointmentResourcesDTO object2) {
                            return object1.getResource().getProvider().getName()
                                    .compareTo(object2.getResource().getProvider().getName());
                        }
                    });
                }

                List<Object> resourcesListWithHeader = getResourcesListWithHeader();
                if (resourcesListWithHeader != null && resourcesListWithHeader.size() > 0) {
                    ProviderAdapter providerAdapter = new ProviderAdapter(
                            getActivity(), resourcesListWithHeader, ChooseProviderFragment.this,
                            chooseProviderFragment);
                    providersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    providersRecyclerView.setAdapter(providerAdapter);
                    getView().findViewById(R.id.emptyStateScreen).setVisibility(View.GONE);
                } else {
                    getView().findViewById(R.id.emptyStateScreen).setVisibility(View.VISIBLE);
                }
            }
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
            Log.e("Server Error", exceptionMessage);
        }
    };

    private List<Object> getResourcesListWithHeader() {
        List<Object> resourcesListWithHeader = new ArrayList<>();
        if (resources != null && resources.size() > 0) {
            AppointmentSectionHeaderModel appointmentSectionHeaderModel = new AppointmentSectionHeaderModel();
            appointmentSectionHeaderModel.setAppointmentHeader(Label.getLabel("choose_provider_all_header"));
            resourcesListWithHeader.add(appointmentSectionHeaderModel);

            resourcesListWithHeader.addAll(resources);
        }

        return resourcesListWithHeader;
    }

    @Override
    public void onProviderListItemClickListener(int position) {
        AppointmentResourcesDTO selectedResource = resources.get(position - 1);
        callback.onProviderSelected(selectedResource, resourcesToScheduleModel,
                getSelectedResourcesToSchedule(selectedResource));
    }

    private List<AppointmentResourcesDTO> getResourcesFromPayload(AppointmentPayloadModel payloadModel) {
        List<AppointmentResourcesDTO> resources = new ArrayList<>();
        for (ResourcesToScheduleDTO resourcesToScheduleDTO : payloadModel.getResourcesToSchedule()) {
            resources.addAll(resourcesToScheduleDTO.getResources());
        }
        return resources;
    }

    private ResourcesToScheduleDTO getSelectedResourcesToSchedule(AppointmentResourcesDTO selectedResource) {
        List<ResourcesToScheduleDTO> resourcesToScheduleDTOList = resourcesToScheduleModel
                .getPayload().getResourcesToSchedule();
        for (ResourcesToScheduleDTO resourcesToScheduleDTO : resourcesToScheduleDTOList) {
            for (AppointmentResourcesDTO appointmentResourcesDTO : resourcesToScheduleDTO.getResources()) {
                if (appointmentResourcesDTO == selectedResource) {
                    return resourcesToScheduleDTO;
                }
            }
        }
        return null;
    }
}
