package com.carecloud.carepay.practice.library.models;

import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.payments.models.ProviderIndexDTO;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.io.Serializable;
import java.util.List;

/**
 * Created by pjohnson on 23/03/17.
 */
public class ResponsibilityHeaderModel implements Serializable {

    private String headerFullTitle;
    private String headerShortTitle;
    private String headerSubtitle;
    private String headerPhotoUrl;

    private ResponsibilityHeaderModel() {
    }

    public String getHeaderFullTitle() {
        return headerFullTitle;
    }

    public void setHeaderFullTitle(String headerFullTitle) {
        this.headerFullTitle = headerFullTitle;
    }

    public String getHeaderSubtitle() {
        return headerSubtitle;
    }

    public String getHeaderShortTitle() {
        return headerShortTitle;
    }

    public void setHeaderShortTitle(String headerShortTitle) {
        this.headerShortTitle = headerShortTitle;
    }

    public void setHeaderSubtitle(String headerSubtitle) {
        this.headerSubtitle = headerSubtitle;
    }

    public String getHeaderPhotoUrl() {
        return headerPhotoUrl;
    }

    public void setHeaderPhotoUrl(String headerPhotoUrl) {
        this.headerPhotoUrl = headerPhotoUrl;
    }

    /**
     *
     * @param paymentsModel the payment model
     * @return Returns a ResponsibilityHeaderModel instance containing patient data
     */
    public static ResponsibilityHeaderModel newPatientHeader(PaymentsModel paymentsModel) {
        PatientModel patientModel = getPatientModel(paymentsModel.getPaymentPayload().getPatientBalances().get(0));
        ResponsibilityHeaderModel headerModel = new ResponsibilityHeaderModel();
        headerModel.setHeaderFullTitle(patientModel.getFullName());
        headerModel.setHeaderShortTitle(patientModel.getShortName());
        headerModel.setHeaderPhotoUrl(patientModel.getProfilePhoto());
        headerModel.setHeaderSubtitle(null);
        return headerModel;
    }

    /**
     *
     * @param paymentsModel the payment model
     * @return Returns a ResponsibilityHeaderModel instance containing clinic data
     */
    public static ResponsibilityHeaderModel newClinicHeader(PaymentsModel paymentsModel) {
        ResponsibilityHeaderModel headerModel = new ResponsibilityHeaderModel();
        headerModel.setHeaderFullTitle(paymentsModel.getPaymentPayload().getUserPractices().get(0).getPracticeName());
        headerModel.setHeaderShortTitle(StringUtil.getShortName(headerModel.getHeaderFullTitle()));
        headerModel.setHeaderPhotoUrl(paymentsModel.getPaymentPayload().getUserPractices().get(0).getPracticePhoto());
        return headerModel;
    }

    private static PatientModel getPatientModel(PatientBalanceDTO patientBalance) {
        return patientBalance.getDemographics().getPayload().getPersonalDetails();
    }

    private static String initializePatientProvider(PaymentsModel paymentsModel) {
        List<PendingBalanceDTO> balances = paymentsModel.getPaymentPayload().getPatientBalances().get(0).getBalances();
        String provider;
        List<PendingBalancePayloadDTO> pendingBalancePayloadDTOList = balances.get(0).getPayload();
        if (!pendingBalancePayloadDTOList.isEmpty() && !pendingBalancePayloadDTOList.get(0).getDetails().isEmpty()) {
            provider = pendingBalancePayloadDTOList.get(0).getDetails().get(0).getProvider().getName();
        } else {
            provider = getProviderName(paymentsModel, balances.get(0).getMetadata().getPatientId());
        }
        return provider;
    }

    private static String getProviderName(PaymentsModel paymentsModel, String patientId) {
        if (!StringUtil.isNullOrEmpty(patientId)) {
            List<ProviderIndexDTO> providerIndex = paymentsModel.getPaymentPayload().getProviderIndex();
            for (ProviderIndexDTO providerIndexDTO : providerIndex) {
                List<String> patientIds = providerIndexDTO.getPatientIds();
                for (String id : patientIds) {
                    if (id.equalsIgnoreCase(patientId)) {
                        return providerIndexDTO.getName();
                    }
                }
            }
        }
        return "";
    }
}
