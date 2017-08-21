package com.carecloud.carepay.practice.library.appointments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.carecloud.carepay.practice.library.appointments.dialogs.PracticeAvailableHoursDialogFragment;
import com.carecloud.carepay.practice.library.appointments.dialogs.PracticeChooseProviderDialog;
import com.carecloud.carepay.practice.library.appointments.dialogs.PracticeRequestAppointmentDialog;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.customdialog.DateRangePickerDialog;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.interfaces.AppointmentNavigationCallback;
import com.carecloud.carepaylibray.appointments.models.AppointmentAvailabilityDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesItemDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSlotsDTO;
import com.carecloud.carepaylibray.appointments.models.LinksDTO;
import com.carecloud.carepaylibray.appointments.models.ScheduleAppointmentRequestDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.base.BaseActivity;
import com.carecloud.carepaylibray.customdialogs.VisitTypeFragmentDialog;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cocampo on 3/13/17
 */

public abstract class BasePracticeAppointmentsActivity extends BasePracticeActivity
        implements AppointmentNavigationCallback,
        DateRangePickerDialog.DateRangePickerDialogListener {

    private Date startDate;
    private Date endDate;

    private AppointmentResourcesDTO appointmentResourcesDTO;
    private AppointmentsResultModel appointmentsResultModel;
    private VisitTypeDTO visitTypeDTO;

    private String patientId;

    /**
     * Shows Confirmation after Appointment Created
     */
    public void showAppointmentConfirmation() {

        if (isVisible()) {
            SystemUtil.showSuccessToast(getContext(), Label.getLabel("appointment_request_success_message_HTML"));
        }

        onAppointmentRequestSuccess();
    }

    @Override
    public void onHoursAndLocationSelected(AppointmentsSlotsDTO appointmentsSlot, AppointmentAvailabilityDTO availabilityDTO) {
        // Call Request appointment Summary dialog from here
        String cancelString = Label.getLabel("available_hours_back");
        new PracticeRequestAppointmentDialog(
                this,
                cancelString,
                appointmentsSlot,
                appointmentResourcesDTO,
                visitTypeDTO,
                this
        ).show();
    }

    @Override
    public void onRangeSelected(Date start, Date end) {
        this.startDate = start;
        this.endDate = end;

        onDateRangeCancelled();
    }

    @Override
    public void onDateRangeCancelled() {
        onDateRangeSelected(startDate, endDate, visitTypeDTO, appointmentResourcesDTO.getResource(), appointmentsResultModel);
    }

    @Override
    public void requestAppointment(AppointmentsSlotsDTO appointmentSlot, String comments) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_mgmt", getApplicationMode().getUserPracticeDTO().getPracticeMgmt());
        queryMap.put("practice_id", getApplicationMode().getUserPracticeDTO().getPracticeId());


        ScheduleAppointmentRequestDTO scheduleAppointmentRequestDTO = new ScheduleAppointmentRequestDTO();
        ScheduleAppointmentRequestDTO.Appointment appointment = scheduleAppointmentRequestDTO.getAppointment();
        appointment.setStartTime(appointmentSlot.getStartTime());
        appointment.setEndTime(appointmentSlot.getEndTime());
        appointment.setLocationId(appointmentSlot.getLocation().getId());
        appointment.setProviderId(appointmentResourcesDTO.getResource().getProvider().getId());
        appointment.setVisitReasonId(visitTypeDTO.getId());
        appointment.setResourceId(appointmentResourcesDTO.getResource().getId());
        appointment.setComplaint(visitTypeDTO.getName());
        appointment.setComments(comments);

        appointment.getPatient().setId(patientId);

        Gson gson = new Gson();
        getWorkflowServiceHelper().execute(getMakeAppointmentTransition(), getMakeAppointmentCallback, gson.toJson(scheduleAppointmentRequestDTO), queryMap);
    }

    @Override
    public void onAppointmentUnconfirmed() {
        onDateRangeCancelled();
    }

    private WorkflowServiceCallback getMakeAppointmentCallback = new WorkflowServiceCallback() {

        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            showAppointmentConfirmation();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            SystemUtil.doDefaultFailureBehavior((BaseActivity) getContext(), exceptionMessage);
        }
    };

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    @Override
    public void newAppointment() {
        getWorkflowServiceHelper().execute(getLinks().getResourcesToSchedule(), scheduleResourcesCallback, null, null, null);
    }

    @Override
    public void rescheduleAppointment(AppointmentDTO appointmentDTO) {

    }

    @Override
    public void onProviderSelected(AppointmentResourcesDTO appointmentResourcesDTO, AppointmentsResultModel appointmentsResultModel) {
        this.appointmentResourcesDTO = appointmentResourcesDTO;
        this.appointmentsResultModel = appointmentsResultModel;

        String tag = VisitTypeFragmentDialog.class.getSimpleName();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(tag);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        VisitTypeFragmentDialog dialog = VisitTypeFragmentDialog.newInstance(appointmentResourcesDTO, appointmentsResultModel, appointmentsResultModel.getPayload().getAppointmentsSettings().get(0));
        dialog.show(ft, tag);
    }

    /**
     * what to do with the selected visit type and provider
     *
     * @param visitTypeDTO selected visit type from dialog
     */
    @Override
    public void onVisitTypeSelected(VisitTypeDTO visitTypeDTO, AppointmentResourcesDTO appointmentResourcesDTO, AppointmentsResultModel appointmentsResultModel) {
        this.visitTypeDTO = visitTypeDTO;
        PracticeAvailableHoursDialogFragment fragment = PracticeAvailableHoursDialogFragment.newInstance(appointmentsResultModel, appointmentResourcesDTO.getResource(), null, null, visitTypeDTO);
        fragment.show(getSupportFragmentManager(), fragment.getClass().getName());
    }

    @Override
    public void onDateRangeSelected(Date startDate, Date endDate, VisitTypeDTO visitTypeDTO, AppointmentResourcesItemDTO appointmentResource, AppointmentsResultModel appointmentsResultModel) {
        PracticeAvailableHoursDialogFragment fragment = PracticeAvailableHoursDialogFragment.newInstance(appointmentsResultModel, appointmentResourcesDTO.getResource(), startDate, endDate, visitTypeDTO);
        fragment.show(getSupportFragmentManager(), fragment.getClass().getName());
    }

    @Override
    public void selectDateRange(Date startDate, Date endDate, VisitTypeDTO visitTypeDTO, AppointmentResourcesItemDTO appointmentResource, AppointmentsResultModel appointmentsResultModel) {
        String tag = DateRangePickerDialog.class.getSimpleName();

        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(tag);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        DateUtil dateUtil = DateUtil.getInstance().setToCurrent();
        DateRangePickerDialog dialog = DateRangePickerDialog.newInstance(
                Label.getLabel("date_range_picker_dialog_title"),
                Label.getLabel("datepicker_cancel_option"),
                false,
                startDate,
                endDate,
                dateUtil.getDate(),
                dateUtil.addDays(92).getDate(),
                this
        );

        dialog.show(ft, tag);
    }

    private WorkflowServiceCallback scheduleResourcesCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            AppointmentsResultModel resourcesToScheduleModel = DtoHelper.getConvertedDTO(AppointmentsResultModel.class, workflowDTO);

            if (resourcesToScheduleModel != null && resourcesToScheduleModel.getPayload() != null
                    && resourcesToScheduleModel.getPayload().getResourcesToSchedule() != null
                    && resourcesToScheduleModel.getPayload().getResourcesToSchedule().size() > 0) {

                Bundle bundle = new Bundle();
                bundle.putString("titleLabel", Label.getLabel("practice_list_select_a_provider"));
                bundle.putString("continueButtonLabel", Label.getLabel("practice_list_continue"));
                DtoHelper.bundleDto(bundle, resourcesToScheduleModel);

                PracticeChooseProviderDialog fragment = new PracticeChooseProviderDialog();
                fragment.setArguments(bundle);
                fragment.show(getSupportFragmentManager(), fragment.getClass().getSimpleName());
            }
        }

        @Override
        public void onFailure(String exceptionMessage) {
            SystemUtil.doDefaultFailureBehavior((BaseActivity) getContext(), exceptionMessage);
        }
    };

    protected abstract TransitionDTO getMakeAppointmentTransition();

    protected abstract LinksDTO getLinks();
}
