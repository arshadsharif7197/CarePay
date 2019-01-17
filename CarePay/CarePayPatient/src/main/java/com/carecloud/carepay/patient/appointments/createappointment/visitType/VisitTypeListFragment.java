package com.carecloud.carepay.patient.appointments.createappointment.visitType;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.appointments.createappointment.CreateAppointmentInterface;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesItemDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsPrePaymentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSettingDTO;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.utils.DtoHelper;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author pjohnson on 1/15/19.
 */
public class VisitTypeListFragment extends BaseFragment {

    private UserPracticeDTO selectedPractice;
    private LocationDTO selectedLocation;
    private AppointmentResourcesItemDTO selectedResource;
    private CreateAppointmentInterface callback;
    private AppointmentsResultModel appointmentResultDto;

    public static VisitTypeListFragment newInstance(UserPracticeDTO selectedPractice,
                                                    LocationDTO selectedLocation,
                                                    AppointmentResourcesItemDTO selectedResource) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, selectedPractice);
        DtoHelper.bundleDto(args, selectedLocation);
        DtoHelper.bundleDto(args, selectedResource);
        VisitTypeListFragment fragment = new VisitTypeListFragment();
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
        Bundle args = getArguments();
        selectedPractice = DtoHelper.getConvertedDTO(UserPracticeDTO.class, args);
        selectedLocation = DtoHelper.getConvertedDTO(LocationDTO.class, args);
        selectedResource = DtoHelper.getConvertedDTO(AppointmentResourcesItemDTO.class, args);
        appointmentResultDto = (AppointmentsResultModel) callback.getDto();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_visit_type_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpToolbar(view);
        callVisitTypeService();
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
        title.setText(Label.getLabel("createAppointment.visitTypeList.title.label.visitType"));
        callback.displayToolbar(false, null);
    }

    private void callVisitTypeService() {
        TransitionDTO transition = appointmentResultDto.getMetadata().getLinks().getResourcesToSchedule();
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_mgmt", selectedPractice.getPracticeMgmt());
        queryMap.put("practice_id", selectedPractice.getPracticeId());
        queryMap.put("request", "visit_reasons");
        if (selectedResource != null) {
            queryMap.put("filter_resource_id", String.valueOf(selectedResource.getId()));
        }
        if (selectedLocation != null) {
            queryMap.put("filter_location_id", String.valueOf(selectedLocation.getId()));
        }
        getWorkflowServiceHelper().execute(transition, new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                showProgressDialog();
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                hideProgressDialog();
                AppointmentsResultModel resourcesDto = DtoHelper
                        .getConvertedDTO(AppointmentsResultModel.class, workflowDTO);
                if (resourcesDto.getPayload().getResourcesToSchedule().get(0).getVisitReasons().size() > 0) {
                    showVisitTypes(resourcesDto.getPayload().getResourcesToSchedule().get(0).getVisitReasons());
                } else {
                    getView().findViewById(R.id.visitTypeRecyclerView).setVisibility(View.GONE);
                    getView().findViewById(R.id.emptyStateScreen).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();
                showErrorNotification(exceptionMessage);
                Log.e(getString(R.string.alert_title_server_error), exceptionMessage);
            }
        }, queryMap);
    }

    private void showVisitTypes(List<VisitTypeDTO> visitTypes) {
        Collections.sort(visitTypes, new Comparator<VisitTypeDTO>() {
            @Override
            public int compare(final VisitTypeDTO object1, final VisitTypeDTO object2) {
                return object1.getName().compareTo(object2.getName());
            }
        });
        RecyclerView visitTypeRecyclerView = getView().findViewById(R.id.visitTypeRecyclerView);
        visitTypeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        visitTypeRecyclerView.setAdapter(new VisitTypeAdapter(visitTypes,
                hashMapPrepayments(getAppointmentPrepayments(selectedPractice.getPracticeId())),
                new VisitTypeAdapter.OnVisitTypeListItemClickListener() {
                    @Override
                    public void onVisitTypeListItemClickListener(VisitTypeDTO visitTypeDTO) {
                        callback.setVisitType(visitTypeDTO);
                    }
                }));
    }

    private HashMap<Integer, Double> hashMapPrepayments(List<AppointmentsPrePaymentDTO> appointmentPrepayments) {
        HashMap<Integer, Double> prepaymentsMap = new HashMap<>();
        for (AppointmentsPrePaymentDTO prePaymentDTO : appointmentPrepayments) {
            prepaymentsMap.put(prePaymentDTO.getVisitType(), prePaymentDTO.getAmount());
        }
        return prepaymentsMap;
    }

    private List<AppointmentsPrePaymentDTO> getAppointmentPrepayments(String practiceId) {
        List<AppointmentsSettingDTO> appointmentsSettingsList = appointmentResultDto.getPayload()
                .getAppointmentsSettings();
        for (AppointmentsSettingDTO appointmentsSettingDTO : appointmentsSettingsList) {
            if (appointmentsSettingDTO.getPracticeId().equals(practiceId)) {
                return appointmentsSettingDTO.getPrePayments();
            }
        }
        return null;
    }
}
