package com.carecloud.carepaylibray.models;

import java.util.ArrayList;

/**
 * Created by Jahirul Bhuiyan on 8/24/2016.
 */
public class WorkflowModel {
    private ScreenModel selectLanguageScreenModel;
    private ScreenModel loginScreenModel;
    private ScreenModel signupScreenModel;
    private ScreenModel responsabScreenModel;
    private ScreenModel demographicsAddressScreenModel;
    private ScreenModel demographicsDetailsScreenModel;
    private ScreenModel demographicsDetailsEthnicityScreenModel;
    private ScreenModel demographicsDocumentsScreenModel;
    private ScreenModel demographicsMoreDetailsScreenModel;
    private ScreenModel demographicsMoreDetailsUpdatesScreenModel;
    private ScreenModel insuranceInfoScreenModel;
    private ScreenModel newProfileScreenModel;
    private ScreenModel detailsScreenModel;


    public ScreenModel getSelectLanguageScreenModel() {
        selectLanguageScreenModel = new ScreenModel();
        ArrayList<ScreenComponentModel> componentModels = new ArrayList<ScreenComponentModel>();
        componentModels.add(new ScreenComponentModel("", "choice", true));
        componentModels.add(new ScreenComponentModel("...", "button", false));
        componentModels.add(new ScreenComponentModel("Continue", "button", false));
        selectLanguageScreenModel.setComponentModels(componentModels);
        return selectLanguageScreenModel;
    }

    public void setSelectLanguageScreenModel(ScreenModel selectLanguageScreenModel) {
        this.selectLanguageScreenModel = selectLanguageScreenModel;
    }

    public ScreenModel getLoginScreenModel() {
        loginScreenModel = new ScreenModel();
        ArrayList<ScreenComponentModel> componentModels = new ArrayList<ScreenComponentModel>();
        componentModels.add(new ScreenComponentModel("User Name", "inputtext", true));
        componentModels.add(new ScreenComponentModel("Password", "password", true));
        componentModels.add(new ScreenComponentModel("SIGN IN", "button", false));
        componentModels.add(new ScreenComponentModel("CREATE NEW ACCOUNT", "button", false));
        componentModels.add(new ScreenComponentModel("Change Language", "text", false));
        componentModels.add(new ScreenComponentModel("Forgot Password", "text", false));
        loginScreenModel.setComponentModels(componentModels);
        return loginScreenModel;
    }

    public void setLoginScreenModel(ScreenModel loginScreenModel) {
        this.loginScreenModel = loginScreenModel;
    }

    public ScreenModel getSignupScreenModel() {
        signupScreenModel = new ScreenModel();
        ArrayList<ScreenComponentModel> componentModels = new ArrayList<ScreenComponentModel>();
        componentModels.add(new ScreenComponentModel("Full Name", "inputtext", true));
        componentModels.add(new ScreenComponentModel("Email", "email", true));
        componentModels.add(new ScreenComponentModel("Create Password", "password", true));
        componentModels.add(new ScreenComponentModel("Repeat Password", "Password", true));
        componentModels.add(new ScreenComponentModel("SIGN UP", "button", false));
        componentModels.add(new ScreenComponentModel("Already Have an Account", "text", false));
        signupScreenModel.setComponentModels(componentModels);
        return signupScreenModel;
    }

    public void setSignupScreenModel(ScreenModel signupScreenModel) {
        this.signupScreenModel = signupScreenModel;
    }

    public ScreenModel getResponsabScreenModel() {
        responsabScreenModel = new ScreenModel();
        ArrayList<ScreenComponentModel> componentModels = new ArrayList<>();
        componentModels.add(new ScreenComponentModel("DR Michael Ross", "text", true));
        componentModels.add(new ScreenComponentModel("Your cost today", "text", true));
        componentModels.add(new ScreenComponentModel("$102.03", "text", true));
        componentModels.add(new ScreenComponentModel("Previous balance", "text", true));
        componentModels.add(new ScreenComponentModel("$29.00", "text", true));
        componentModels.add(new ScreenComponentModel("Insurance CoPay", "text", true));
        componentModels.add(new ScreenComponentModel("$73.03", "text", true));
        componentModels.add(new ScreenComponentModel("Sign & Pay", "button", true));
        responsabScreenModel.setComponentModels(componentModels);
        return responsabScreenModel;
    }


    public void setResponsabScreenModel(ScreenModel responsabScreenModel) {
        this.responsabScreenModel = responsabScreenModel;
    }

    public ScreenModel getDemographicsAddressScreenModel() {
        demographicsAddressScreenModel = new ScreenModel();
        ArrayList<ScreenComponentModel> componentModels = new ArrayList<>();
        componentModels.add(new ScreenComponentModel("logo", "ImageView", true));
        componentModels.add(new ScreenComponentModel("phone", "phonenumber", true));
        componentModels.add(new ScreenComponentModel("zip", "Inputtext", true));
        componentModels.add(new ScreenComponentModel("address1", "Inputtext", true));
        componentModels.add(new ScreenComponentModel("address2", "Inputtext", false));
        componentModels.add(new ScreenComponentModel("city", "Inputtext", true));
        componentModels.add(new ScreenComponentModel("state", "Inputtext", true));
        componentModels.add(new ScreenComponentModel("next", "button", true));
        demographicsAddressScreenModel.setComponentModels(componentModels);
        return demographicsAddressScreenModel;
    }

    public void setDemographicsAddressScreenModel(ScreenModel demographicsAddressScreenModel) {
        this.demographicsAddressScreenModel = demographicsAddressScreenModel;
    }


    public ScreenModel getDemographicsDetailsScreenModel() {
        demographicsDetailsScreenModel = new ScreenModel();
        ArrayList<ScreenComponentModel> componentModels = new ArrayList<>();
        componentModels.add(new ScreenComponentModel("logo", "ImageView", true));
        componentModels.add(new ScreenComponentModel("picture", "Images", true));
        componentModels.add(new ScreenComponentModel("Select or Take Photo", "button", true));
        componentModels.add(new ScreenComponentModel("label_race", "text", true));
        componentModels.add(new ScreenComponentModel("choose_race", "button", true));
        componentModels.add(new ScreenComponentModel("label_ethnicity", "text", true));
        componentModels.add(new ScreenComponentModel("choose_ethnicity", "button", true));
        componentModels.add(new ScreenComponentModel("label_pref_lang", "text", true));
        componentModels.add(new ScreenComponentModel("pref_lang", "text", true));
        demographicsDetailsScreenModel.setComponentModels(componentModels);
        return demographicsDetailsScreenModel;
    }

    public void setDemographicsDetailsScreenModel(ScreenModel demographicsDetailsScreenModel) {
        this.demographicsDetailsScreenModel = demographicsDetailsScreenModel;
    }


    public ScreenModel getDemographicsDetailsEthnicityScreenModel() {
        demographicsDetailsEthnicityScreenModel = new ScreenModel();
        ArrayList<ScreenComponentModel> componentModels = new ArrayList<>();
        componentModels.add(new ScreenComponentModel("ethnicity", "listview", true));
        return demographicsDetailsEthnicityScreenModel;
    }

    public void setDemographicsDetailsEthnicityScreenModel(ScreenModel demographicsDetailsEthnicityScreenModel) {
        this.demographicsDetailsEthnicityScreenModel = demographicsDetailsEthnicityScreenModel;
    }

    public ScreenModel getDemographicsDocumentsScreenModel() {
        demographicsDocumentsScreenModel = new ScreenModel();
        ArrayList<ScreenComponentModel> componentModels = new ArrayList<>();
        componentModels.add(new ScreenComponentModel("logo", "image", true));
        componentModels.add(new ScreenComponentModel("thumbnail_license", "Imageview", true));
        componentModels.add(new ScreenComponentModel("scan_license", "button", true));
        componentModels.add(new ScreenComponentModel("license_number", "Inputtext", true));
        componentModels.add(new ScreenComponentModel("label_license_state", "text", true));
        componentModels.add(new ScreenComponentModel("select", "button", true));
        componentModels.add(new ScreenComponentModel("label_have_insurance", "text", true));
        componentModels.add(new ScreenComponentModel("have_insurance", "togglebutton", true));
        componentModels.add(new ScreenComponentModel("next", "button", true));
        componentModels.add(new ScreenComponentModel("thumbnail_insurance", "Imageview", false));
        componentModels.add(new ScreenComponentModel("scan_insurance", "button", false));
        componentModels.add(new ScreenComponentModel("insurance_info_1", "Inputtext", false));
        componentModels.add(new ScreenComponentModel("insurance_info_2", "Inputtext", false));
        componentModels.add(new ScreenComponentModel("insurance_info_3", "Inputtext", false));
        componentModels.add(new ScreenComponentModel("check_insurance", "button", false));
        demographicsDocumentsScreenModel.setComponentModels(componentModels);
        return demographicsDocumentsScreenModel;
    }

    public void setDemographicsDocumentsScreenModel(ScreenModel demographicsDocumentsScreenModel) {
        this.demographicsDocumentsScreenModel = demographicsDocumentsScreenModel;
    }

    public ScreenModel getDemographicsMoreDetailsScreenModel() {
        demographicsMoreDetailsScreenModel = new ScreenModel();
        ArrayList<ScreenComponentModel> componentModels = new ArrayList<>();
        componentModels.add(new ScreenComponentModel("logo", "ImageView", true));
        componentModels.add(new ScreenComponentModel("sub-logo", "text", true));
        componentModels.add(new ScreenComponentModel("label_want_updates", "text", true));
        componentModels.add(new ScreenComponentModel("want_updates", "togglebutton", true));
        componentModels.add(new ScreenComponentModel("add", "button", true));
        componentModels.add(new ScreenComponentModel("do_later", "button", true));
        demographicsMoreDetailsScreenModel.setComponentModels(componentModels);
        return demographicsMoreDetailsScreenModel;
    }

    public void setDemographicsMoreDetailsScreenModel(ScreenModel demographicsMoreDetailsScreenModel) {
        this.demographicsMoreDetailsScreenModel = demographicsMoreDetailsScreenModel;
    }

    public ScreenModel getDemographicsMoreDetailsUpdatesScreenModel() {
        demographicsMoreDetailsUpdatesScreenModel = new ScreenModel();
        ArrayList<ScreenComponentModel> componentModels = new ArrayList<>();
        componentModels.add(new ScreenComponentModel("updates", "listview", true));
        demographicsMoreDetailsUpdatesScreenModel.setComponentModels(componentModels);
        return demographicsMoreDetailsUpdatesScreenModel;
    }

    public void setDemographicsMoreDetailsUpdatesScreenModel(ScreenModel demographicsMoreDetailsUpdatesScreenModel) {
        this.demographicsMoreDetailsUpdatesScreenModel = demographicsMoreDetailsUpdatesScreenModel;
    }

    public ScreenModel getInsuranceInfoScreenModel() {
        insuranceInfoScreenModel = new ScreenModel();
        ArrayList<ScreenComponentModel> componentModels = new ArrayList<>();
        // todo add components when mock-up available
        insuranceInfoScreenModel.setComponentModels(componentModels);
        return insuranceInfoScreenModel;
    }

    public void setInsuranceInfoScreenModel(ScreenModel insuranceInfoScreenModel) {
        this.insuranceInfoScreenModel = insuranceInfoScreenModel;
    }

    public ScreenModel getNewProfileScreenModel() {
        newProfileScreenModel = new ScreenModel();
        ArrayList<ScreenComponentModel> componentModels = new ArrayList<>();
        // todo add components when mock-up available
        newProfileScreenModel.setComponentModels(componentModels);
        return newProfileScreenModel;
    }

    public void setNewProfileScreenModel(ScreenModel newProfileScreenModel) {
        this.newProfileScreenModel = newProfileScreenModel;
    }

    public ScreenModel getDetailsScreenModel() {
        detailsScreenModel = new ScreenModel();
        ArrayList<ScreenComponentModel> componentModels = new ArrayList<>();
        componentModels.add(new ScreenComponentModel("picture", "imageview", true));
        componentModels.add(new ScreenComponentModel("Take Photo", "button", true));
        detailsScreenModel.setComponentModels(componentModels);
        return detailsScreenModel;
    }

}