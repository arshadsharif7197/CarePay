package com.carecloud.carepaylibray.appointments.createappointment.provider;

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
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author pjohnson on 1/15/19.
 */
public abstract class BaseProviderListFragment extends BaseDialogFragment {

    private UserPracticeDTO selectedPractice;
    private VisitTypeDTO selectedVisitType;
    private LocationDTO selectedLocation;
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
        selectedVisitType = DtoHelper.getConvertedDTO(VisitTypeDTO.class, args);
        selectedLocation = DtoHelper.getConvertedDTO(LocationDTO.class, args);
        appointmentResultDto = (AppointmentsResultModel) callback.getDto();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!alreadyCalled) {
            callResourceService();
            alreadyCalled = true;
        }
    }

    private void callResourceService() {
        TransitionDTO transition = appointmentResultDto.getMetadata().getLinks().getResourcesToSchedule();
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_mgmt", selectedPractice.getPracticeMgmt());
        queryMap.put("practice_id", selectedPractice.getPracticeId());
        queryMap.put("request", "resources");
        if (selectedVisitType != null) {
            queryMap.put("filter_nature_of_visit_id", selectedVisitType.getId());
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
                if (resourcesDto.getPayload().getResourcesToSchedule().get(0).getResourcesV2().size() > 0) {
                    List<AppointmentResourcesItemDTO> providers = sortProviders(resourcesDto);
                    showResources(providers);
                } else {
                    getView().findViewById(R.id.providers_recycler_view).setVisibility(View.GONE);
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

    private List<AppointmentResourcesItemDTO> sortProviders(AppointmentsResultModel resourcesDto) {
        Collections.sort(resourcesDto.getPayload().getResourcesToSchedule().get(0).getResourcesV2(),
                new Comparator<AppointmentResourcesItemDTO>() {
                    @Override
                    public int compare(AppointmentResourcesItemDTO o1, AppointmentResourcesItemDTO o2) {
                        if (StringUtil.isNullOrEmpty(o2.getProvider().getLastName())) {
                            o2.getProvider().setLastName("");
                        }
                        if (StringUtil.isNullOrEmpty(o1.getProvider().getLastName())) {
                            o1.getProvider().setLastName("");
                        }
                        return o1.getProvider().getLastName().compareTo(o2.getProvider().getLastName());
                    }
                });
        return resourcesDto.getPayload().getResourcesToSchedule().get(0).getResourcesV2();
    }

    private void showResources(List<AppointmentResourcesItemDTO> resources) {
        RecyclerView providersRecyclerView = getView().findViewById(R.id.providers_recycler_view);
        providersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        providersRecyclerView.setAdapter(new ProviderAdapter(resources, new ProviderAdapter.OnProviderListItemClickListener() {
            @Override
            public void onProviderListItemClickListener(AppointmentResourcesItemDTO resource) {
                dismiss();
                callback.setResourceProvider(resource);
            }
        }));
    }
}
