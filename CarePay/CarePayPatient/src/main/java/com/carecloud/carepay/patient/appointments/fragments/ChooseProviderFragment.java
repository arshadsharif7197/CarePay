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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.carecloud.carepay.patient.appointments.AppointmentNavigationCallback;
import com.carecloud.carepay.patient.appointments.adapters.ProviderAdapter;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentSectionHeaderModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.customdialogs.VisitTypeDialog;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChooseProviderFragment extends BaseFragment implements ProviderAdapter.OnProviderListItemClickListener,
        VisitTypeDialog.OnDialogListItemClickListener {

    private RecyclerView providersRecyclerView;
    private ProgressBar appointmentProgressBar;
    private AppointmentsResultModel appointmentsResultModel;
    private AppointmentsResultModel resourcesToScheduleModel;

    private ChooseProviderFragment chooseProviderFragment;
    private List<AppointmentResourcesDTO> resources;
    private AppointmentResourcesDTO selectedResource;

    private AppointmentNavigationCallback callback;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            callback = (AppointmentNavigationCallback) context;
        }catch (ClassCastException cce){
            throw new ClassCastException("Attached context must implement AppointmentNavigationCallback");
        }
    }

    @Override
    @SuppressLint("InflateParams")
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        final View chooseProviderView = inflater.inflate(R.layout.fragment_choose_provider, container, false);
        chooseProviderFragment = this;

        Bundle args = getArguments();
        if (args != null && args.containsKey(CarePayConstants.ADD_APPOINTMENT_PROVIDERS_BUNDLE)) {
            Gson gson = new Gson();
            String appointmentInfoString = args.getString(CarePayConstants.ADD_APPOINTMENT_PROVIDERS_BUNDLE);
            appointmentsResultModel = gson.fromJson(appointmentInfoString, AppointmentsResultModel.class);
        }

        // set the toolbar
        hideDefaultActionBar();

        Toolbar toolbar = (Toolbar) chooseProviderView.findViewById(R.id.add_appointment_toolbar);
        TextView titleView = (TextView) toolbar.findViewById(R.id.add_appointment_toolbar_title);
        titleView.setText(appointmentsResultModel.getMetadata().getLabel().getChooseProviderHeading());
        titleView.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        toolbar.setTitle("");

        TextView otherView = (TextView) toolbar.findViewById(R.id.add_appointment_toolbar_other);
        otherView.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        otherView.setVisibility(View.GONE);
        otherView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                OtherProviderFragment otherProviderFragment = (OtherProviderFragment)
//                        fragmentManager.findFragmentByTag(AvailableHoursFragment.class.getSimpleName());
//
//                if (otherProviderFragment == null) {
//                    otherProviderFragment = new OtherProviderFragment();
//                }
//
//                fragmentManager.beginTransaction().replace(R.id.add_appointments_frag_holder, otherProviderFragment,
//                        AvailableHoursFragment.class.getSimpleName()).commit();
            }
        });

        Drawable closeIcon = ContextCompat.getDrawable(getActivity(), R.drawable.icn_nav_back);
        toolbar.setNavigationIcon(closeIcon);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        providersRecyclerView = ((RecyclerView) chooseProviderView.findViewById(R.id.providers_recycler_view));
        appointmentProgressBar = (ProgressBar) chooseProviderView.findViewById(R.id.providers_progress_bar);
        appointmentProgressBar.setVisibility(View.GONE);

        //Fetch provider data
        getResourcesInformation();

        return chooseProviderView;
    }

    private void getResourcesInformation() {
        Map<String, String> queryMap = new HashMap<>();
        //TODO this will need to be updated once multiple practice support has been implemented
        queryMap.put("practice_mgmt", appointmentsResultModel.getPayload().getPracticePatientIds().get(0).getPracticeManagement());
        queryMap.put("practice_id", appointmentsResultModel.getPayload().getPracticePatientIds().get(0).getPracticeId());


        TransitionDTO resourcesToSchedule = appointmentsResultModel.getMetadata().getLinks().getResourcesToSchedule();
        getWorkflowServiceHelper().execute(resourcesToSchedule, scheduleResourcesCallback, queryMap);
    }

    private WorkflowServiceCallback scheduleResourcesCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
            appointmentProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            Gson gson = new Gson();
            resourcesToScheduleModel = gson.fromJson(workflowDTO.toString(), AppointmentsResultModel.class);

            if (resourcesToScheduleModel != null && resourcesToScheduleModel.getPayload() != null
                    && resourcesToScheduleModel.getPayload().getResourcesToSchedule() != null
                    && resourcesToScheduleModel.getPayload().getResourcesToSchedule().size() > 0) {

                resources = resourcesToScheduleModel.getPayload().getResourcesToSchedule().get(0).getResources();
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
                }
            }

            appointmentProgressBar.setVisibility(View.GONE);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            SystemUtil.showDefaultFailureDialog(getActivity());
            appointmentProgressBar.setVisibility(View.GONE);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private List<Object> getResourcesListWithHeader() {
        List<Object> resourcesListWithHeader = new ArrayList<>();
        if (resources != null && resources.size() > 0) {
            AppointmentSectionHeaderModel appointmentSectionHeaderModel = new AppointmentSectionHeaderModel();
            appointmentSectionHeaderModel.setAppointmentHeader(
                    appointmentsResultModel.getMetadata().getLabel().getChooseProviderAllHeader());
            resourcesListWithHeader.add(appointmentSectionHeaderModel);

            for (AppointmentResourcesDTO resourcesScheduleItem : resources) {
                resourcesListWithHeader.add(resourcesScheduleItem);
            }
        }

        return resourcesListWithHeader;
    }

    private void loadVisitTypeScreen(AppointmentResourcesDTO model) {
        VisitTypeDialog visitTypeDialog = new VisitTypeDialog(this.getContext(), model, this, appointmentsResultModel);
        visitTypeDialog.show();
    }

    @Override
    public void onProviderListItemClickListener(int position) {
        selectedResource = resources.get(position - 1);
        loadVisitTypeScreen(selectedResource);
    }

    /**
     * what to do with the selected visit type and provider
     * @param selectedVisitType selected visit type from dialog
     */
    public void onDialogListItemClickListener(VisitTypeDTO selectedVisitType) {
        callback.availableTimes(selectedVisitType, selectedResource);
    }
}
