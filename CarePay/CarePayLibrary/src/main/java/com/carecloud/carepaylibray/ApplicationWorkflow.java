package com.carecloud.carepaylibray;

import com.carecloud.carepaylibray.models.ScreenModel;
import com.carecloud.carepaylibray.models.WorkflowModel;

import java.util.List;

/**
 * Created by Jahirul Bhuiyan on 8/24/2016.
 */
public class ApplicationWorkflow {
    private static ApplicationWorkflow instance;

    private WorkflowModel workFlowModel;

    private ApplicationWorkflow() {
        workFlowModel = new WorkflowModel();
    }

    public static ApplicationWorkflow Instance() {
        if (instance == null) {
            instance = new ApplicationWorkflow();
        }
        return instance;
    }

    public ScreenModel getSelectLanguageScreenModel() {
        return workFlowModel.getSelectLanguageScreenModel();
    }

    public ScreenModel getLoginScreenModel() {
        return workFlowModel.getLoginScreenModel();
    }

    public ScreenModel getSignupScreenModel() {
        return workFlowModel.getSignupScreenModel();
    }

    public ScreenModel getResponsabScreenModel() {
        return workFlowModel.getResponsabScreenModel();
    }

    public ScreenModel getDemographicsAddressScreenModel() {
        return workFlowModel.getDemographicsAddressScreenModel();
    }

    public ScreenModel getDemographicsDetailsScreenModel() {
        return workFlowModel.getDemographicsDetailsScreenModel();
    }

    public ScreenModel getDemographicDetailsEthicityScreenModel() {
        return workFlowModel.getDemographicsDetailsEthnicityScreenModel();
    }

    public List<String> getRaceDataModel() {
        return workFlowModel.getRaceList();
    }
    public List<String> getethincityDataModel() {
        return workFlowModel.getEthincityList();
    }

    public List<String> getlanguageDataModel() {
        return workFlowModel.getLanguageList();
    }
    public List<String> getUpdatesDataModel() {
        return workFlowModel.getUpdateList();
    }

    public ScreenModel getDemographicsDocumentsScreenModel() {
        return workFlowModel.getDemographicsDocumentsScreenModel();
    }
    public ScreenModel getUpdatesDialogScreenModel() {
        return workFlowModel.getUpdatesDialogScreenModel();
    }

    public ScreenModel getInsuranceInfoScreenModel() {
        return workFlowModel.getInsuranceInfoScreenModel();
    }

    public ScreenModel getNewProfileScreenModel() {
        return workFlowModel.getNewProfileScreenModel();
    }

    public ScreenModel getDemographicsMoreDetailsScreenModel() {
        return workFlowModel.getDemographicsMoreDetailsScreenModel();
    }



    public ScreenModel getDemographicsMoreDetailsUpdatesScreenModel() {
        return workFlowModel.getDemographicsMoreDetailsUpdatesScreenModel();
    }

    public ScreenModel getDetailsScreenModel(){
        return workFlowModel.getDetailsScreenModel();
    }


}