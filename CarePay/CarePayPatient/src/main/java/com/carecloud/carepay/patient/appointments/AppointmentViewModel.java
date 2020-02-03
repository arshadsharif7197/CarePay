package com.carecloud.carepay.patient.appointments;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.carecloud.carepaylibray.common.BaseViewModel;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.QueueStatusPayloadDTO;
import com.carecloud.carepaylibray.base.models.Paging;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * @author pjohnson on 2020-01-14.
 */
public class AppointmentViewModel extends BaseViewModel {

    private MutableLiveData<AppointmentsResultModel> appointmentsDtoObservable = new MutableLiveData<>();
    private MutableLiveData<AppointmentsResultModel> historicAppointmentsObservable = new MutableLiveData<>();
    private MutableLiveData<Boolean> paginationLoaderObservable = new MutableLiveData<>();
    private MutableLiveData<QueueStatusPayloadDTO> queueStatusObservable = new MutableLiveData<>();
    private PaymentsModel paymentsModel;

    public AppointmentViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<AppointmentsResultModel> getAppointmentsDtoObservable() {
        return appointmentsDtoObservable;
    }

    public MutableLiveData<AppointmentsResultModel> getHistoricAppointmentsObservable() {
        if (historicAppointmentsObservable.getValue() != null) {
            historicAppointmentsObservable = new MutableLiveData<>();
        }
        return historicAppointmentsObservable;
    }

    public MutableLiveData<Boolean> getPaginationLoaderObservable() {
        if (paginationLoaderObservable.getValue() != null) {
            paginationLoaderObservable = new MutableLiveData<>();
        }
        return paginationLoaderObservable;
    }

    public MutableLiveData<QueueStatusPayloadDTO> getQueueStatusObservable() {
        if (queueStatusObservable.getValue() != null) {
            queueStatusObservable = new MutableLiveData<>();
        }
        return queueStatusObservable;
    }

    public PaymentsModel getPaymentsModel() {
        return paymentsModel;
    }

    public void getAppointments(TransitionDTO transitionDTO, boolean showSkeleton) {
        Map<String, String> queryMap = new HashMap<>();
        getWorkflowServiceHelper().execute(transitionDTO, new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                setSkeleton(showSkeleton);
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                setSkeleton(false);
                AppointmentsResultModel appointmentDto = DtoHelper.getConvertedDTO(AppointmentsResultModel.class, workflowDTO);
                paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
                appointmentsDtoObservable.setValue(appointmentDto);
            }

            @Override
            public void onFailure(String exceptionMessage) {
                setSkeleton(false);
                setErrorMessage(exceptionMessage);
            }
        }, queryMap);
    }

    public void getHistoricAppointments(UserPracticeDTO userPracticeDTO,
                                        Paging paging,
                                        boolean refresh,
                                        boolean showShimmerLayout) {
        long currentPage;
        if (refresh) {
            currentPage = 0;
        } else {
            currentPage = paging.getCurrentPage();
        }


        Map<String, String> queryMap = new HashMap<>();
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.DATE, -1);
        queryMap.put("start_date", DateUtil.getInstance().setDate(new Date(0)).getServerFormat());
        queryMap.put("end_date", DateUtil.getInstance().setDate(calendar).getServerFormat());
        queryMap.put("page", String.valueOf(currentPage + 1));
        queryMap.put("limit", String.valueOf(paging.getResultsPerPage()));
        queryMap.put("practice_mgmt", userPracticeDTO.getPracticeMgmt());
        queryMap.put("practice_id", userPracticeDTO.getPracticeId());
        getWorkflowServiceHelper().execute(appointmentsDtoObservable.getValue().getMetadata().getLinks().getAppointments()
                , new WorkflowServiceCallback() {
                    @Override
                    public void onPreExecute() {
                        if (showShimmerLayout) {

                        } else if (!refresh) {
                            paginationLoaderObservable.postValue(true);
                        }
                    }

                    @Override
                    public void onPostExecute(WorkflowDTO workflowDTO) {
                        paginationLoaderObservable.postValue(false);
                        AppointmentsResultModel appointmentsResultModel = DtoHelper
                                .getConvertedDTO(AppointmentsResultModel.class, workflowDTO);
                        historicAppointmentsObservable.postValue(appointmentsResultModel);
                    }

                    @Override
                    public void onFailure(String exceptionMessage) {
                        paginationLoaderObservable.postValue(false);
                        setErrorMessage(exceptionMessage);
                    }
                }, queryMap);
    }

    public void getQueueStatus(AppointmentDTO appointmentDTO, TransitionDTO transitionDTO) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_mgmt", appointmentDTO.getMetadata().getPracticeMgmt());
        queryMap.put("practice_id", appointmentDTO.getMetadata().getPracticeId());
        queryMap.put("patient_id", appointmentDTO.getMetadata().getPatientId());
        queryMap.put("appointment_id", appointmentDTO.getMetadata().getAppointmentId());

        getWorkflowServiceHelper().execute(transitionDTO, new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {

            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                QueueStatusPayloadDTO queueStatusPayloadDTO = workflowDTO.getPayload(QueueStatusPayloadDTO.class);
                queueStatusObservable.postValue(queueStatusPayloadDTO);
            }

            @Override
            public void onFailure(String exceptionMessage) {

            }
        }, queryMap);
    }
}
