package com.carecloud.carepaylibray;

import com.carecloud.carepaylibray.models.ScreenModel;
import com.carecloud.carepaylibray.models.WorkFlowModel;

/**
 * Created by Jahirul Bhuiyan on 8/23/2016.
 */
public class ApplicationWorkFlow {

    private static ApplicationWorkFlow instance;

    private WorkFlowModel workFlowModel;

    private ApplicationWorkFlow() {
        workFlowModel=new WorkFlowModel();
    }

    public static ApplicationWorkFlow Instance( ) {
        if(instance == null) {
            instance = new ApplicationWorkFlow();
        }
        return instance;
    }

    public ScreenModel getSelectLanguageScreenModel() { return workFlowModel.getSelectLanguageScreenModel(); }
    public ScreenModel getLoginScreenModel() {
        return workFlowModel.getLoginScreenModel();
    }
    public ScreenModel getSignupScreenModel() {
        return workFlowModel.getSignupScreenModel();
    }
    public ScreenModel getResponsabScreenModel() { return workFlowModel.getResponsabScreenModel(); }
    public ScreenModel getDemographicsAddressScreenModel() { return workFlowModel.getDemographicsAddressScreenModel(); }
    public ScreenModel getDemographicsDetailsScreenModel() { return workFlowModel.getDemographicsDetailsScreenModel(); }
    public ScreenModel getDemographicDetailsEthicityScreenModel() { return workFlowModel.getDemographicsDetailsEthnicityScreenModel(); }
    public ScreenModel getDemographicsDocumentsScreenModel() { return workFlowModel.getDemographicsDocumentsScreenModel(); }
    public ScreenModel getInsuranceInfoScreenModel() { return workFlowModel.getInsuranceInfoScreenModel(); }
    public ScreenModel getNewProfileScreenModel() { return workFlowModel.getNewProfileScreenModel(); }
    public ScreenModel getDemographicsMoreDetailsScreenModel() { return workFlowModel.getDemographicsMoreDetailsScreenModel(); };
    public ScreenModel getDemographicsMoreDetailsUpdatesScreenModel() { return workFlowModel.getDemographicsMoreDetailsUpdatesScreenModel(); };


}
