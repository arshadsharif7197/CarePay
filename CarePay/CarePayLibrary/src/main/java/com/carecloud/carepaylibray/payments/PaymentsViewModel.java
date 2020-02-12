package com.carecloud.carepaylibray.payments;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.common.BaseViewModel;
import com.carecloud.carepaylibray.payments.models.IntegratedPatientPaymentPayload;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceMetadataDTO;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * @author pjohnson on 2020-02-12.
 */
public class PaymentsViewModel extends BaseViewModel {

    private MutableLiveData<PaymentsModel> makePaymentObservable = new MutableLiveData<>();
    private MutableLiveData<Void> makePaymentErrorObservable = new MutableLiveData<>();
    private PaymentsModel paymentsModel;
    private AppointmentDTO appointment;

    public PaymentsViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<PaymentsModel> getMakePaymentObservable() {
        if (makePaymentObservable.getValue() != null) {
            makePaymentObservable = new MutableLiveData<>();
        }
        return makePaymentObservable;
    }

    public MutableLiveData<Void> getMakePaymentErrorObservable() {
        if (makePaymentErrorObservable.getValue() != null) {
            makePaymentErrorObservable = new MutableLiveData<>();
        }
        return makePaymentErrorObservable;
    }

    public PaymentsModel getPaymentsModel() {
        return paymentsModel;
    }

    public void setPaymentsModel(PaymentsModel paymentsModel) {
        this.paymentsModel = paymentsModel;
    }

    public UserPracticeDTO getPracticeInfo(String practiceId) {
        return paymentsModel.getPaymentPayload().getUserPractice(practiceId);
    }

    public void setAppointment(AppointmentDTO appointment) {
        this.appointment = appointment;
    }

    public AppointmentDTO getAppointment() {
        return appointment;
    }

    public void postPayment(IntegratedPaymentPostModel paymentModelJson, String practiceId) {
        UserPracticeDTO userPracticeDTO = getPracticeInfo(practiceId);
        Map<String, String> queries = getQuery(paymentModelJson, userPracticeDTO);

        Map<String, String> header = new HashMap<>();
        header.put("transition", "true");

        TransitionDTO transitionDTO = paymentsModel.getPaymentsMetadata().getPaymentsTransitions().getMakePayment();
        Gson gson = new Gson();
        getWorkflowServiceHelper().execute(transitionDTO, new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                setLoading(true);
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                setLoading(false);
                PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
                makePaymentObservable.postValue(paymentsModel);
            }

            @Override
            public void onFailure(String exceptionMessage) {
                setLoading(false);
                setErrorMessage(exceptionMessage);
                makePaymentErrorObservable.postValue(null);
            }
        }, gson.toJson(paymentModelJson), queries, header);
    }

    @NonNull
    private Map<String, String> getQuery(IntegratedPaymentPostModel paymentModelJson, UserPracticeDTO userPracticeDTO) {
        Map<String, String> queries = new HashMap<>();

        if (paymentModelJson.getQueryMetadata() != null) {
            queries.put("practice_mgmt", paymentModelJson.getQueryMetadata().getPracticeMgmt());
            queries.put("practice_id", paymentModelJson.getQueryMetadata().getPracticeId());
            queries.put("patient_id", paymentModelJson.getQueryMetadata().getPatientId());
            paymentModelJson.setQueryMetadata(null);
        } else if (userPracticeDTO != null) {
            queries.put("practice_mgmt", userPracticeDTO.getPracticeMgmt());
            queries.put("practice_id", userPracticeDTO.getPracticeId());
            queries.put("patient_id", userPracticeDTO.getPatientId());
        } else if (!paymentsModel.getPaymentPayload().getPatientBalances().isEmpty()) {
            PendingBalanceMetadataDTO metadata = paymentsModel.getPaymentPayload().getPatientBalances().get(0).getBalances().get(0).getMetadata();
            queries.put("practice_mgmt", metadata.getPracticeMgmt());
            queries.put("practice_id", metadata.getPracticeId());
            queries.put("patient_id", metadata.getPatientId());
        }

        if (paymentsModel.getPaymentPayload().getPaymentPostModel() != null &&
                !StringUtil.isNullOrEmpty(paymentsModel.getPaymentPayload().getPaymentPostModel().getOrderId())) {
            IntegratedPaymentPostModel paymentPostModel = paymentsModel.getPaymentPayload().getPaymentPostModel();
            queries.put("store_id", paymentPostModel.getStoreId());
            queries.put("transaction_id", paymentPostModel.getOrderId());
        }

        if (getAppointment().getPayload().getId() != null) {
            queries.put("appointment_id", getAppointment().getPayload().getId());
        }

        if (queries.get("patient_id") == null) {
            queries.remove("patient_id");
            if (getAppointment() != null) {
                queries.put("patient_id", getAppointment().getMetadata().getPatientId());
            } else {
                for (PatientBalanceDTO patientBalanceDTO : paymentsModel.getPaymentPayload().getPatientBalances()) {
                    if (patientBalanceDTO.getBalances().get(0).getMetadata().getPracticeId().equals(queries.get("practice_id"))) {
                        queries.put("patient_id", patientBalanceDTO.getBalances().get(0).getMetadata().getPatientId());
                    }
                }
            }
        }
        return queries;
    }
}
