package com.carecloud.carepaylibray.appointments.createappointment.visittype;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.interfaces.ScheduleAppointmentInterface;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesItemDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsPrePaymentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSettingDTO;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.utils.DtoHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author pjohnson on 1/15/19.
 */
public abstract class BaseVisitTypeListFragment extends BaseDialogFragment {

    private UserPracticeDTO selectedPractice;
    private LocationDTO selectedLocation;
    private AppointmentResourcesItemDTO selectedResource;
    protected ScheduleAppointmentInterface callback;
    private AppointmentsResultModel appointmentResultDto;
    private boolean alreadyCalled;

    @Override
    public void onAttach(Context context) {
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
        Bundle args = getArguments();
        selectedPractice = DtoHelper.getConvertedDTO(UserPracticeDTO.class, args);
        selectedLocation = DtoHelper.getConvertedDTO(LocationDTO.class, args);
        selectedResource = DtoHelper.getConvertedDTO(AppointmentResourcesItemDTO.class, args);
        appointmentResultDto = (AppointmentsResultModel) callback.getDto();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!alreadyCalled) {
            callVisitTypeService();
            alreadyCalled = true;
        }
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
                        dismiss();
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
        return new ArrayList<>();
    }
}
