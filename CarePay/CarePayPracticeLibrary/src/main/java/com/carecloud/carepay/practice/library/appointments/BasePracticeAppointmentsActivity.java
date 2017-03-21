package com.carecloud.carepay.practice.library.appointments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.appointments.dialogs.PracticeAvailableHoursDialog;
import com.carecloud.carepay.practice.library.appointments.dialogs.PracticeChooseProviderDialog;
import com.carecloud.carepay.practice.library.appointments.dialogs.PracticeRequestAppointmentDialog;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.customdialog.DateRangePickerDialog;
import com.carecloud.carepay.service.library.CarePayConstants;
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
        DateRangePickerDialog.DateRangePickerDialogListener {

    private Date startDate;
    private Date endDate;

    private AppointmentResourcesDTO appointmentResourcesDTO;
    private AppointmentsResultModel appointmentsResultModel;
    private AppointmentAvailabilityDTO availabilityDTO;
    private VisitTypeDTO visitTypeDTO;

    private String patientId;
    private static final String LOG_TAG = "BasePracticeAppointment";
    /**
     * Shows Confirmation after Appointment Created
     */
    public void showAppointmentConfirmation() {
        if (isVisible()) {
                SystemUtil.showSuccessToast(getContext(), getLabels().getAppointmentRequestSuccessMessage());
            }

            onAppointmentRequestSuccess();
    }

    @Override
    public void confirmAppointment(String startTime, String endTime, AppointmentAvailabilityDTO availabilityDTO) {
        this.availabilityDTO = availabilityDTO;
        // Call Request appointment Summary dialog from here
        String cancelString = getLabels().getAvailableHoursBack();
        new PracticeRequestAppointmentDialog(
                this,
                cancelString,
                getLabels(),
                startTime,
                endTime,
                appointmentResourcesDTO,
                availabilityDTO,
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
        selectTime(startDate, endDate, visitTypeDTO, appointmentResourcesDTO.getResource(), appointmentsResultModel);
    }

    @Override
    public void requestAppointment(String startTime, String endTime, String comments) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("language", getApplicationPreferences().getUserLanguage());
        queryMap.put("practice_mgmt", getApplicationMode().getUserPracticeDTO().getPracticeMgmt());
        queryMap.put("practice_id", getApplicationMode().getUserPracticeDTO().getPracticeId());

        JsonObject appointmentJSONObj = new JsonObject();
        JsonObject patientJSONObj = new JsonObject();

        patientJSONObj.addProperty("id", patientId);
        appointmentJSONObj.addProperty("start_time", startTime);
        appointmentJSONObj.addProperty("end_time", endTime);
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
            showErrorNotification(CarePayConstants.CONNECTION_ISSUE_ERROR_MESSAGE);
            Log.e(LOG_TAG, exceptionMessage);
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
    public void selectVisitType(AppointmentResourcesDTO appointmentResourcesDTO, AppointmentsResultModel appointmentsResultModel) {
        this.appointmentResourcesDTO = appointmentResourcesDTO;
        this.appointmentsResultModel = appointmentsResultModel;

        String tag = VisitTypeFragmentDialog.class.getSimpleName();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(tag);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        VisitTypeFragmentDialog dialog = VisitTypeFragmentDialog.newInstance(appointmentResourcesDTO, appointmentsResultModel);
        dialog.show(ft, tag);
    }

    /**
     * what to do with the selected visit type and provider
     *
     * @param visitTypeDTO selected visit type from dialog
     */
    @Override
    public void selectTime(VisitTypeDTO visitTypeDTO, AppointmentResourcesDTO appointmentResourcesDTO, AppointmentsResultModel appointmentsResultModel) {
        this.visitTypeDTO = visitTypeDTO;

        String cancelString = getLabels().getAvailableHoursBack();
        new PracticeAvailableHoursDialog(getContext(), cancelString, getLabels(), appointmentResourcesDTO.getResource(), appointmentsResultModel, visitTypeDTO, getLinks().getAppointmentAvailability(), this).show();
    }

    @Override
    public void selectTime(Date startDate, Date endDate, VisitTypeDTO visitTypeDTO, AppointmentResourcesItemDTO appointmentResource, AppointmentsResultModel appointmentsResultModel) {
        String cancelString = getLabels().getAvailableHoursBack();
        new PracticeAvailableHoursDialog(getContext(), cancelString, getLabels(), appointmentResource, appointmentsResultModel, visitTypeDTO, getLinks().getAppointmentAvailability(), this, startDate, endDate).show();
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
                bundle.putString("titleLabel", getLabels().getPracticeListSelectProvider());
                bundle.putString("continueButtonLabel", getLabels().getPracticeListContinue());
                DtoHelper.bundleDto(bundle, resourcesToScheduleModel);

                PracticeChooseProviderDialog fragment = new PracticeChooseProviderDialog();
                fragment.setArguments(bundle);
                fragment.show(getSupportFragmentManager(), fragment.getClass().getSimpleName());
            }
        }

        @Override
        public void onFailure(String exceptionMessage) {
            showErrorNotification(CarePayConstants.CONNECTION_ISSUE_ERROR_MESSAGE);
            Log.e(LOG_TAG, exceptionMessage);
        }
    };

    protected abstract AppointmentLabelDTO getLabels();

    protected abstract TransitionDTO getMakeAppointmentTransition();

    protected abstract LinksDTO getLinks();
}
