package com.carecloud.carepay.patient.appointments;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.carecloud.carepay.patient.myhealth.BaseViewModel;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
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
    private PaymentsModel paymentsModel;

    public AppointmentViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<AppointmentsResultModel> getAppointmentsDtoObservable() {
        return appointmentsDtoObservable;
    }

    public MutableLiveData<AppointmentsResultModel> getHistoricAppointmentsObservable() {
        return historicAppointmentsObservable;
    }

    public MutableLiveData<Boolean> getPaginationLoaderObservable() {
        return paginationLoaderObservable;
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
                appointmentsDtoObservable.setValue(appointmentDto);
                paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
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
                            paginationLoaderObservable.setValue(true);
                        }
//                        if (getView() != null) {
//                            getView().findViewById(R.id.fakeView).setVisibility(View.VISIBLE);
//                        }
                    }

                    @Override
                    public void onPostExecute(WorkflowDTO workflowDTO) {
                        paginationLoaderObservable.setValue(false);
                        AppointmentsResultModel appointmentsResultModel = DtoHelper
                                .getConvertedDTO(AppointmentsResultModel.class, workflowDTO);
                        historicAppointmentsObservable.setValue(appointmentsResultModel);
//                        if (isVisible()) {
//                            getView().findViewById(R.id.fakeView).setVisibility(View.GONE);
//                        }
//                        if (showShimmerLayout) {
//                            hideShimmerEffect();
//                        } else if (!refresh) {
//                            adapter.setLoading(true);
//                        }
//                        List<UserPracticeDTO> userPractices = appointmentsResultModel.getPayload().getUserPractices();

//                        appointmentsResultModel.getPayload().setUserPractices(userPractices);
//                        isPaging = false;
//                        adapter.setLoading(false);


                    }

                    @Override
                    public void onFailure(String exceptionMessage) {
                        paginationLoaderObservable.setValue(false);
                        setErrorMessage(exceptionMessage);
//                        if (isVisible()) {
//                            getView().findViewById(R.id.fakeView).setVisibility(View.GONE);
//                        }
//                        isPaging = false;
                    }
                }, queryMap);
    }
}
