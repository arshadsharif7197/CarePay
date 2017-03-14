package com.carecloud.carepay.practice.library.appointments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.appointments.dialogs.PracticeAvailableHoursDialog;
import com.carecloud.carepay.practice.library.appointments.dialogs.PracticeChooseProviderDialog;
import com.carecloud.carepay.practice.library.appointments.dialogs.PracticeRequestAppointmentDialog;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.customdialog.DateRangePickerDialog;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.appointments.AppointmentNavigationCallback;
import com.carecloud.carepaylibray.appointments.models.AppointmentAvailabilityDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentLabelDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesItemDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSlotsDTO;
import com.carecloud.carepaylibray.appointments.models.LinksDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.customdialogs.VisitTypeFragmentDialog;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.JsonObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by cocampo on 3/13/17.
 */

public abstract class BasePracticeAppointmentsActivity extends BasePracticeActivity
        implements AppointmentNavigationCallback,
        DateRangePickerDialog.DateRangePickerDialogListener,
        PracticeAvailableHoursDialog.PracticeAvailableHoursDialogListener,
        PracticeRequestAppointmentDialog.PracticeRequestAppointmentDialogListener {

    private Date startDate;
    private Date endDate;

    private AppointmentResourcesDTO appointmentResourcesDTO;
    private AppointmentAvailabilityDTO availabilityDTO;
    private AppointmentsSlotsDTO appointmentsSlotsDTO;
    private VisitTypeDTO visitTypeDTO;

    private String patientId;

    public void showAppointmentConfirmation() {
        if (isVisible()) {
            new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("")
                    .setContentText(getLabels().getAppointmentRequestSuccessMessage())
                    .setConfirmText(getString(R.string.alert_ok))
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            onAppointmentRequestSuccess();
                        }
                    })
                    .show();
        } else {
            onAppointmentRequestSuccess();
        }
    }

    /**
     * what to do with the selected visit type and provider
     * @param visitTypeDTO selected visit type from dialog
     */
    @Override
    public void availableTimes(VisitTypeDTO visitTypeDTO, AppointmentResourcesDTO appointmentResourcesDTO, AppointmentsResultModel appointmentsResultModel) {
        this.visitTypeDTO = visitTypeDTO;

        String cancelString = getLabels().getAvailableHoursBack();
        new PracticeAvailableHoursDialog(getContext(), cancelString, getLabels(), appointmentResourcesDTO, visitTypeDTO, getLinks().getAppointmentAvailability(), this).show();
    }

    @Override
    public void onAppointmentTimeSelected(AppointmentAvailabilityDTO availabilityDTO, AppointmentsSlotsDTO appointmentsSlotsDTO) {
        this.availabilityDTO = availabilityDTO;
        this.appointmentsSlotsDTO = appointmentsSlotsDTO;
        // Call Request appointment Summary dialog from here
        String cancelString = getLabels().getAvailableHoursBack();
        new PracticeRequestAppointmentDialog(this, cancelString, getLabels(), appointmentResourcesDTO, appointmentsSlotsDTO, availabilityDTO, visitTypeDTO, this).show();
    }

    @Override
    public void onDateRangeTapped() {
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
                getLabels().getPickDateHeading(),
                getLabels().getDatepickerCancelOption(),
                getLabels().getTodayDateOption(),
                false,
                startDate,
                endDate,
                dateUtil.getDate(),
                dateUtil.addDays(92).getDate(),
                this
        );

        dialog.show(ft, tag);
    }

    @Override
    public void onRangeSelected(Date start, Date end) {
        this.startDate = start;
        this.endDate = end;

        onDateRangeCancelled();
    }

    @Override
    public void onDateRangeCancelled() {
        String cancelString = getLabels().getAvailableHoursBack();
        new PracticeAvailableHoursDialog(getContext(), cancelString, getLabels(), appointmentResourcesDTO, visitTypeDTO, getLinks().getAppointmentAvailability(), this, startDate, endDate).show();
    }

    @Override
    public void onAppointmentRequested(String comments) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("language", getApplicationPreferences().getUserLanguage());
        queryMap.put("practice_mgmt", getApplicationMode().getUserPracticeDTO().getPracticeMgmt());
        queryMap.put("practice_id", getApplicationMode().getUserPracticeDTO().getPracticeId());

        JsonObject appointmentJSONObj = new JsonObject();
        JsonObject patientJSONObj = new JsonObject();

        patientJSONObj.addProperty("id", patientId);
        appointmentJSONObj.addProperty("start_time", appointmentsSlotsDTO.getStartTime());
        appointmentJSONObj.addProperty("end_time", appointmentsSlotsDTO.getEndTime());
        appointmentJSONObj.addProperty("appointment_status_id", "5");
        appointmentJSONObj.addProperty("location_id", availabilityDTO.getPayload().getAppointmentAvailability().getPayload().get(0).getLocation().getId());
        appointmentJSONObj.addProperty("provider_id", appointmentResourcesDTO.getResource().getProvider().getId());
        appointmentJSONObj.addProperty("visit_reason_id", visitTypeDTO.getId());
        appointmentJSONObj.addProperty("resource_id", appointmentResourcesDTO.getResource().getId());
        appointmentJSONObj.addProperty("chief_complaint", visitTypeDTO.getName());
        appointmentJSONObj.addProperty("comments", comments);
        appointmentJSONObj.add("patient", patientJSONObj);

        JsonObject makeAppointmentJSONObj = new JsonObject();
        makeAppointmentJSONObj.add("appointment", appointmentJSONObj);

        getWorkflowServiceHelper().execute(getMakeAppointmentTransition(), getMakeAppointmentCallback, makeAppointmentJSONObj
                .toString(), queryMap);
    }

    @Override
    public void onAppointmentCancelled() {
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
            SystemUtil.doDefaultFailureBehavior(getContext(), exceptionMessage);
        }
    };

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    @Override
    public void newAppointment() {
        Bundle bundle= new Bundle();
        DtoHelper.bundleDto(bundle, getLinks().getResourcesToSchedule());

        PracticeChooseProviderDialog fragment = new PracticeChooseProviderDialog();
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), fragment.getClass().getSimpleName());
    }

    @Override
    public void rescheduleAppointment(AppointmentDTO appointmentDTO) {

    }

    @Override
    public void selectVisitType(AppointmentResourcesDTO appointmentResourcesDTO, AppointmentsResultModel appointmentsResultModel) {
        this.appointmentResourcesDTO = appointmentResourcesDTO;

        String tag = VisitTypeFragmentDialog.class.getSimpleName();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(tag);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        VisitTypeFragmentDialog dialog = VisitTypeFragmentDialog.newInstance(appointmentResourcesDTO, appointmentsResultModel, getLabels().getVisitTypeHeading());
        dialog.show(ft, tag);
    }

    @Override
    public void availableTimes(Date startDate, Date endDate, VisitTypeDTO visitTypeDTO, AppointmentResourcesItemDTO appointmentResource, AppointmentsResultModel appointmentsResultModel) {

    }

    @Override
    public void selectDate(Date startDate, Date endDate, VisitTypeDTO visitTypeDTO, AppointmentResourcesItemDTO appointmentResource, AppointmentsResultModel appointmentsResultModel) {

    }

    protected abstract AppointmentLabelDTO getLabels();

    protected abstract TransitionDTO getMakeAppointmentTransition();

    protected abstract LinksDTO getLinks();
}
