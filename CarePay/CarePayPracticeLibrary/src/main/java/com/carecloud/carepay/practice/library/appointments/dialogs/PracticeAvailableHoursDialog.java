package com.carecloud.carepay.practice.library.appointments.dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.appointments.adapters.PracticeAvailableHoursAdapter;
import com.carecloud.carepay.practice.library.appointments.adapters.PracticeAvailableLocationsAdapter;
import com.carecloud.carepay.practice.library.checkin.filters.FilterDataDTO;
import com.carecloud.carepay.practice.library.customdialog.BasePracticeDialog;
import com.carecloud.carepay.practice.library.models.FilterModel;
import com.carecloud.carepay.practice.library.models.MapFilterModel;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.AppointmentNavigationCallback;
import com.carecloud.carepaylibray.appointments.models.AppointmentAvailabilityDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentAvailabilityPayloadDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentLabelDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesItemDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSlotsDTO;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.base.BaseActivity;
import com.carecloud.carepaylibray.base.ISession;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PracticeAvailableHoursDialog extends BasePracticeDialog implements PracticeAvailableHoursAdapter.PracticeAvailableHoursAdapterListener, PracticeAvailableLocationsAdapter.SelectLocationCallback {

    private final TransitionDTO appointmentAvailabilityTransitionDTO;
    private final AppointmentNavigationCallback callback;
    private Date startDate;
    private Date endDate;
    private Context context;
    private LayoutInflater inflater;
    private AppointmentAvailabilityDTO availabilityDTO;
    private final AppointmentLabelDTO appointmentLabelDTO;
    private final AppointmentResourcesItemDTO appointmentResourcesDTO;
    private final AppointmentsResultModel appointmentsResultModel;
    private VisitTypeDTO visitTypeDTO;

    private RecyclerView availableHoursRecycleView;
    private RecyclerView availableLocationsRecycleView;
    private View singleLocation;
    private TextView singleLocationText;
    private View progressView;
    private PracticeAvailableHoursAdapter availableHoursAdapter;

    private FilterModel filterModel;


    /**
     * Instantiates a new Practice available hours dialog.
     *
     * @param context      the context
     * @param cancelString the cancel string
     * @param visitTypeDTO      visit type
     * @param callback          dialog callback
     */
    public PracticeAvailableHoursDialog(Context context, String cancelString,
                                        AppointmentLabelDTO appointmentLabelDTO,
                                        AppointmentResourcesItemDTO appointmentResourcesDTO,
                                        AppointmentsResultModel appointmentsResultModel,
                                        VisitTypeDTO visitTypeDTO,
                                        TransitionDTO appointmentAvailabilityTransitionDTO,
                                        AppointmentNavigationCallback callback) {
        this(context, cancelString, appointmentLabelDTO, appointmentResourcesDTO, appointmentsResultModel, visitTypeDTO, appointmentAvailabilityTransitionDTO, callback, null, null);
    }

    /**
     * @param context           the context
     * @param cancelString      the cancel string
     * @param visitTypeDTO      visit type
     * @param callback          dialog callback
     * @param startDate         start date to pick from
     * @param endDate           end date to pick from
     */
    public PracticeAvailableHoursDialog(Context context, String cancelString,
                                        AppointmentLabelDTO appointmentLabelDTO,
                                        AppointmentResourcesItemDTO appointmentResourcesDTO,
                                        AppointmentsResultModel appointmentsResultModel,
                                        VisitTypeDTO visitTypeDTO,
                                        TransitionDTO appointmentAvailabilityTransitionDTO,
                                        AppointmentNavigationCallback callback,
                                        Date startDate,
                                        Date endDate) {
        super(context, cancelString, false);
        this.context = context;
        this.appointmentLabelDTO = appointmentLabelDTO;
        this.appointmentResourcesDTO = appointmentResourcesDTO;
        this.appointmentsResultModel = appointmentsResultModel;
        this.visitTypeDTO = visitTypeDTO;
        this.appointmentAvailabilityTransitionDTO = appointmentAvailabilityTransitionDTO;
        this.callback = callback;
        this.startDate = startDate;
        this.endDate = endDate;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onAddContentView(inflater);
        filterModel = new FilterModel();

        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                getAvailableHoursTimeSlots();
                if(progressView!=null){
                    progressView.setVisibility(View.GONE);
                }
            }
        });
    }

    @SuppressLint("InflateParams")
    @Override
    protected void onAddContentView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.dialog_available_hours_slots, null);
        ((FrameLayout) findViewById(R.id.base_dialog_content_layout)).addView(view);
        inflateUIComponents(view);
    }

    private void inflateUIComponents(View view) {
        singleLocation = view.findViewById(R.id.practice_available_single_location);
        singleLocationText = (TextView) view.findViewById(R.id.practice_single_location_text);

        LinearLayoutManager availableHoursLayoutManager = new LinearLayoutManager(context);
        availableHoursLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        availableHoursRecycleView = (RecyclerView) view.findViewById(R.id.available_hours_recycler_view);
        availableHoursRecycleView.setLayoutManager(availableHoursLayoutManager);

        LinearLayoutManager availableLocationsLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        availableLocationsRecycleView = (RecyclerView) view.findViewById(R.id.available_locations_recycler);
        availableLocationsRecycleView.setLayoutManager(availableLocationsLayoutManager);

        String range = null;
        try {
            range = appointmentLabelDTO.getAppointmentEditDateRangeButton();

            String location = appointmentLabelDTO.getAppointmentLocationsLabel();
            TextView locationsLabel = (TextView) view.findViewById(R.id.location_text);
            locationsLabel.setText(location != null ? location : context.getString(R.string.locations_label));

            setDialogTitle(appointmentLabelDTO
                    .getAvailableHoursHeading());
        }catch (NullPointerException ex){
            ex.printStackTrace();
        }

        TextView editRangeButton = (TextView) view.findViewById(R.id.edit_date_range_button);
        editRangeButton.setText(range != null ? range : context.getString(R.string.edit_date_range_button_label));
        editRangeButton.setOnClickListener(dateRangeClickListener);
        SystemUtil.setGothamRoundedBoldTypeface(context, editRangeButton);

        setCancelImage(R.drawable.icn_arrow_up);
        setCancelable(false);

        progressView = view.findViewById(R.id.progressview);
        progressView.setVisibility(View.VISIBLE);

        updateDateRange();
    }

    private void setAdapters(){
        try {
            List<FilterDataDTO> locations = extractAvailableLocations(availabilityDTO);
            filterModel.setLocations(locations);

            setPracticeAvailableHoursAdapter();
            setAvailableLocationsAdapter(locations);
        }catch (NullPointerException ex){
            ex.printStackTrace();
        }
    }

    private void setPracticeAvailableHoursAdapter() {
        availableHoursAdapter = (PracticeAvailableHoursAdapter) availableHoursRecycleView.getAdapter();
        if (availableHoursAdapter == null) {
            availableHoursAdapter = new PracticeAvailableHoursAdapter(context, this);
            availableHoursRecycleView.setAdapter(availableHoursAdapter);
        }

        availableHoursAdapter.setAppointmentAvailability(availabilityDTO);
    }

    private void setAvailableLocationsAdapter(List<FilterDataDTO> locations) {
        switch (locations.size()) {
            case 0:
                availableLocationsRecycleView.setVisibility(View.GONE);
                singleLocation.setVisibility(View.GONE);
                break;

            case 1:
                availableLocationsRecycleView.setVisibility(View.GONE);
                singleLocation.setVisibility(View.VISIBLE);
                singleLocationText.setText(locations.get(0).getDisplayText());
                break;

            default:
                availableLocationsRecycleView.setVisibility(View.VISIBLE);
                singleLocation.setVisibility(View.GONE);

                PracticeAvailableLocationsAdapter adapter = (PracticeAvailableLocationsAdapter) availableLocationsRecycleView.getAdapter();

                if (adapter == null) {
                    adapter = new PracticeAvailableLocationsAdapter(getContext(), this);
                    availableLocationsRecycleView.setAdapter(adapter);
                }

                adapter.setItems(locations);
                adapter.notifyDataSetChanged();
        }
    }

    /**
     * Click listener for edit range and edit date range button
     */
    private View.OnClickListener dateRangeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (null != callback) {
                callback.selectDateRange(startDate, endDate, visitTypeDTO, appointmentResourcesDTO, appointmentsResultModel);
            }

            dismiss();
        }
    };

    @Override
    protected void onAddFooterView(LayoutInflater inflater) {

    }

    @Override
    protected void onDialogCancel() {
        dismiss();
    }

    /**
     * Method to update date range that is selected on calendar
     */
    private void updateDateRange(){
        if(startDate == null || endDate == null){
            startDate = Calendar.getInstance().getTime();//today
            endDate = startDate;
        }

        String today = Label.getLabel("today_label");
        String tomorrow = Label.getLabel("add_appointment_tomorrow");
        String thisMonth = Label.getLabel("this_month_label");
        String nextDay = Label.getLabel("next_days_label");

        String formattedDate = DateUtil.getFormattedDate(startDate, endDate, today, tomorrow, thisMonth, nextDay);
        setDialogTitle(formattedDate);
    }

    private void getAvailableHoursTimeSlots() {
        if (null == visitTypeDTO) {
            dismiss();
            return;
        }

        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("language", ((ISession) context).getApplicationPreferences().getUserLanguage());
        queryMap.put("visit_reason_id", visitTypeDTO.getId() + "");
        queryMap.put("resource_ids", appointmentResourcesDTO.getId() + "");

        if (startDate != null) {
            DateUtil.getInstance().setDate(startDate);
            queryMap.put("start_date", DateUtil.getInstance().toStringWithFormatYyyyDashMmDashDd());
        }

        if (endDate != null) {
            DateUtil.getInstance().setDate(endDate);
            queryMap.put("end_date", DateUtil.getInstance().toStringWithFormatYyyyDashMmDashDd());
        }

        ((ISession) context).getWorkflowServiceHelper().execute(appointmentAvailabilityTransitionDTO, getAppointmentsAvailabilitySlotsCallback, queryMap);
    }

    private WorkflowServiceCallback getAppointmentsAvailabilitySlotsCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            ((ISession) context).showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            ((ISession) context).hideProgressDialog();
            Gson gson = new Gson();
            availabilityDTO = gson.fromJson(workflowDTO.toString(), AppointmentAvailabilityDTO.class);

            setAdapters();

            updateDateRange();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            SystemUtil.doDefaultFailureBehavior((BaseActivity) context, exceptionMessage);
        }
    };

    @Override
    public void onSelectAppointmentTimeSlot(AppointmentsSlotsDTO appointmentsSlotsDTO) {
        if (null != callback) {
            callback.confirmAppointment(appointmentsSlotsDTO.getStartTime(), appointmentsSlotsDTO.getEndTime(), availabilityDTO);
        }

        dismiss();
    }

    @Override
    public void onLocationSelected() {
        availableHoursAdapter.applyFilter(new MapFilterModel(filterModel));
    }

    private List<FilterDataDTO> extractAvailableLocations(AppointmentAvailabilityDTO availabilityDTO){
        List<FilterDataDTO> locations = new LinkedList<>();
        List<AppointmentAvailabilityPayloadDTO> availableAppointments = availabilityDTO.getPayload().getAppointmentAvailability().getPayload();
        for(AppointmentAvailabilityPayloadDTO availableAppointment : availableAppointments){
            LocationDTO location = availableAppointment.getLocation();

            FilterDataDTO filterDataDTO = new FilterDataDTO(location.getId(), location.getName(), FilterDataDTO.FilterDataType.LOCATION);
            filterDataDTO.setChecked(true);

            locations.add(filterDataDTO);
        }

        return locations;
    }
}