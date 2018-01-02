package com.carecloud.carepaylibray.practice;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowCallback;
import com.carecloud.carepaylibray.interfaces.IcicleInterface;
import com.carecloud.carepaylibray.utils.DtoHelper;

import java.util.List;

/**
 * Created by lsoco_user on 11/29/2016
 */

public abstract class BaseCheckinFragment extends BaseFragment implements IcicleInterface {

    protected FlowStateInfo flowStateInfo;

    public boolean navigateBack() {
        return false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        attachCallback(context);
    }

    public abstract void attachCallback(Context context);

    /**
     * On update.
     *
     * @param callback    the callback
     * @param workflowDTO the workflow dto
     */
    public void onUpdate(CheckinFlowCallback callback, WorkflowDTO workflowDTO) {
        try {
            CheckInWorkFlowDTO checkInWorkflowDTO = DtoHelper
                    .getConvertedDTO(CheckInWorkFlowDTO.class, workflowDTO);
            AppointmentDTO appointmentDTO = getAppointmentById(checkInWorkflowDTO.getPayload()
                    .getAppointments(), callback.getAppointmentId());
            if ((appointmentDTO != null && appointmentDTO.getPayload().getAppointmentStatus().getCode()
                    .equalsIgnoreCase(CarePayConstants.CHECKED_IN)) || "patient_home".equals(workflowDTO.getState())) {
                callback.displayCheckInSuccess(workflowDTO);
            } else {
                callback.navigateToWorkflow(workflowDTO);
            }
        } catch (Exception e) {
            Log.e("WorkflowUpdate", e.getMessage());
        }
    }

    /**
     * Gets appointment by id.
     *
     * @param appointmentsList the appointments list
     * @param appointmentId    the appointment id
     * @return the appointment by id
     */
    private AppointmentDTO getAppointmentById(List<AppointmentDTO> appointmentsList, String appointmentId) {
        for (AppointmentDTO appointment : appointmentsList) {
            if (appointment.getMetadata().getAppointmentId().equalsIgnoreCase(appointmentId)) {
                return appointment;
            }
        }
        return null;
    }

    private Bundle frozenIcicle = null;

    @Override
    public IcicleInterface pushData(Bundle icicle) {
        if (frozenIcicle == null) {
            frozenIcicle = icicle;
        } else {
            frozenIcicle.putAll(icicle);
        }
        return this;
    }

    @Override
    public Bundle popData() {
        if (frozenIcicle == null) {
            frozenIcicle = new Bundle();
        }
        Bundle icicle = (Bundle) frozenIcicle.clone();
        frozenIcicle = null;
        return icicle;
    }

}
