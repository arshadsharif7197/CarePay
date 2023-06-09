package com.carecloud.carepaylibray.appointments.createappointment.provider;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

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
    private ArrayList<AppointmentResourcesItemDTO> uniqueProviders;

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
            // added 2 params for https://jira.carecloud.com/browse/BREEZ-2081
            queryMap.put("video_option", String.valueOf(selectedVisitType.hasVideoOption()));
            queryMap.put("from_intelligent_scheduler", String.valueOf(selectedVisitType.isFromIntelligentScheduler()));
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
                if (!resourcesDto.getPayload().getResourcesToSchedule().isEmpty() &&
                        !resourcesDto.getPayload().getResourcesToSchedule().get(0).getResourcesV2().isEmpty()) {
                    List<AppointmentResourcesItemDTO> providers = sortProviders(resourcesDto
                            .getPayload().getResourcesToSchedule().get(0).getResourcesV2());
                    Collections.shuffle(providers, new Random()); // https://jira.carecloud.com/browse/BREEZ-1748
                    showResources(getUniqueProviders(providers));
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

    private List<AppointmentResourcesItemDTO> getUniqueProviders(List<AppointmentResourcesItemDTO> providers) {
        uniqueProviders = new ArrayList<>();
        for (AppointmentResourcesItemDTO providerInfo : providers) {
            if (uniqueProviders.size() > 0) {
                if (validateProvider(providerInfo)) {
                    uniqueProviders.add(providerInfo);
                }
            } else {
                uniqueProviders.add(providerInfo);
            }
        }
        return uniqueProviders;
    }

    private boolean validateProvider(AppointmentResourcesItemDTO providerInfo) {
        for (AppointmentResourcesItemDTO uniqueProvider : uniqueProviders) {
            if (uniqueProvider.getProvider().getName().equalsIgnoreCase(providerInfo.getProvider().getName())) {
                return false;
            }
        }
        return true;
    }

    private List<AppointmentResourcesItemDTO> sortProviders(List<AppointmentResourcesItemDTO> resourcesDto) {
        Collections.sort(resourcesDto, new Comparator<AppointmentResourcesItemDTO>() {
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
        return resourcesDto;
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
