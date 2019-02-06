package com.carecloud.carepaylibray.appointments.createappointment.location;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author pjohnson on 1/15/19.
 */
public abstract class BaseLocationListFragment extends BaseDialogFragment {

    private UserPracticeDTO selectedPractice;
    private VisitTypeDTO selectedVisitType;
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
        selectedVisitType = DtoHelper.getConvertedDTO(VisitTypeDTO.class, args);
        selectedResource = DtoHelper.getConvertedDTO(AppointmentResourcesItemDTO.class, args);
        appointmentResultDto = (AppointmentsResultModel) callback.getDto();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!alreadyCalled) {
            callLocationService();
            alreadyCalled = true;
        }
    }

    private void callLocationService() {
        TransitionDTO transition = appointmentResultDto.getMetadata().getLinks().getResourcesToSchedule();
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_mgmt", selectedPractice.getPracticeMgmt());
        queryMap.put("practice_id", selectedPractice.getPracticeId());
        queryMap.put("request", "locations");
        if (selectedResource != null) {
            queryMap.put("filter_resource_id", String.valueOf(selectedResource.getId()));
        }
        if (selectedVisitType != null) {
            queryMap.put("filter_nature_of_visit_id", selectedVisitType.getId());
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
                if (resourcesDto.getPayload().getResourcesToSchedule().get(0).getLocations().size() > 0) {
                    showLocations(resourcesDto.getPayload().getResourcesToSchedule().get(0).getLocations());
                } else {
                    getView().findViewById(R.id.locationRecyclerView).setVisibility(View.GONE);
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

    private void showLocations(List<LocationDTO> locations) {
        Collections.sort(locations, new Comparator<LocationDTO>() {
            @Override
            public int compare(final LocationDTO object1, final LocationDTO object2) {
                return object1.getName().compareTo(object2.getName());
            }
        });
        RecyclerView locationRecyclerView = getView().findViewById(R.id.locationRecyclerView);
        locationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        locationRecyclerView.setAdapter(new LocationAdapter(filterLocations(locations),
                new LocationAdapter.OnLocationListItemClickListener() {
                    @Override
                    public void onLocationListItemClickListener(LocationDTO locationDTO) {
                        dismiss();
                        callback.setLocation(locationDTO);
                    }
                }));
    }

    private List<LocationDTO> filterLocations(List<LocationDTO> locations) {
        List<LocationDTO> filteredLocations = new ArrayList<>();
        for (LocationDTO location : locations) {
            if (location.getVisibleAppointmentScheduler()) {
                filteredLocations.add(location);
            }
        }
        return filteredLocations;
    }
}
