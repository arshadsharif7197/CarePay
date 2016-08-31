package com.carecloud.carepaylibray.models;

import java.util.ArrayList;
import java.util.List;

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
    private ScreenModel homeScreenModel;
    private ScreenModel updatesDialogScreenModel;
    private ScreenModel signatureScreenModel;

    public ScreenModel getSelectLanguageScreenModel() {
        selectLanguageScreenModel = new ScreenModel();
        ArrayList<ScreenComponentModel> componentModels = new ArrayList<ScreenComponentModel>();
        componentModels.add(new ScreenComponentModel("","imageview",true));
        componentModels.add(new ScreenComponentModel("", "choice", true));
        componentModels.add(new ScreenComponentModel("Continue", "button", false));
        selectLanguageScreenModel.setComponentModels(componentModels);
        return selectLanguageScreenModel;
    }

    public void setSelectLanguageScreenModel(ScreenModel selectLanguageScreenModel) {
        this.selectLanguageScreenModel = selectLanguageScreenModel;
    }
    public ScreenModel getHomeScreenModel() {
        homeScreenModel=new ScreenModel();
        ArrayList<ScreenComponentModel> componentModels= new ArrayList<ScreenComponentModel>();
       // componentModels.add(new ScreenComponentModel("How can we help?","textview",true));
        componentModels.add(new ScreenComponentModel("Profile Meter","textview",true));
        componentModels.add(new ScreenComponentModel("Add Details","textview",true));
        componentModels.add(new ScreenComponentModel("","progressbar",true));
        componentModels.add(new ScreenComponentModel("","gridview",true));
        homeScreenModel.setComponentModels(componentModels);
        return homeScreenModel;
    }
    public void setHomeScreenModel(ScreenModel homeScreenModel) {
        this.homeScreenModel=homeScreenModel;
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
        componentModels.add(new ScreenComponentModel("FULL NAME", "inputtext", true));
        componentModels.add(new ScreenComponentModel("EMAIL", "email", true));
        componentModels.add(new ScreenComponentModel("CREATE PASSWORD", "password", true));
        componentModels.add(new ScreenComponentModel("REPEAT PASSWORD", "password", true));
        componentModels.add(new ScreenComponentModel("SIGN UP", "button", false));
        componentModels.add(new ScreenComponentModel("Already Have an Account?", "text", false));
        signupScreenModel.setComponentModels(componentModels);
        return signupScreenModel;
    }

    public void setSignupScreenModel(ScreenModel signupScreenModel) {
        this.signupScreenModel = signupScreenModel;
    }

    public ScreenModel getResponsabScreenModel() {
        responsabScreenModel = new ScreenModel();
        ArrayList<ScreenComponentModel> componentModels = new ArrayList<>();
        componentModels.add(new ScreenComponentModel("Amount for today's visit", "text", true));
        componentModels.add(new ScreenComponentModel("$120.00", "textValue", true));
        componentModels.add(new ScreenComponentModel("Previous balance", "text", true));
        componentModels.add(new ScreenComponentModel("$29.00", "textValue", true));
        componentModels.add(new ScreenComponentModel("Insurance CoPay", "text", true));
        componentModels.add(new ScreenComponentModel("$79.00", "textValue", true));
        componentModels.add(new ScreenComponentModel("GO TO PAYMENT PROCESS", "button", true));
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
        componentModels.add(new ScreenComponentModel("Let's get you set up!", "heading", true));
        componentModels.add(new ScreenComponentModel("First, tell us your address & contact information", "subHeading", true));
        componentModels.add(new ScreenComponentModel("PHONE NUMBER", "inputtext", true));
        componentModels.add(new ScreenComponentModel("ZIP CODE", "inputtext", true));
        componentModels.add(new ScreenComponentModel("ADDRESS1", "inputtext", true));
        componentModels.add(new ScreenComponentModel("ADDRESS2", "inputtext", false));
        componentModels.add(new ScreenComponentModel("CITY", "inputtext", true));
        componentModels.add(new ScreenComponentModel("STATE", "inputtext", true));
        componentModels.add(new ScreenComponentModel("NEXT", "button", true));
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
        componentModels.add(new ScreenComponentModel("Some details about you", "heading", true));
        componentModels.add(new ScreenComponentModel("How about uploading your photo?", "subHeading", true));
        componentModels.add(new ScreenComponentModel("Select or Take Photo", "buttonWithImage", true));
        componentModels.add(new ScreenComponentModel("Race", "selector", true));
        componentModels.add(new ScreenComponentModel("Ethnicity", "selector", true));
        componentModels.add(new ScreenComponentModel("label_pref_lang", "text", true));
        componentModels.add(new ScreenComponentModel("Preferred Language", "selector", true));
        componentModels.add(new ScreenComponentModel("NEXT", "button", true));
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
        componentModels.add(new ScreenComponentModel("Documents, please", "heading", true));
        componentModels.add(new ScreenComponentModel("The scan process is straight forward", "subHeading", true));
        componentModels.add(new ScreenComponentModel("SCAN DRIVER’S LICENSE", "buttonWithImage", true));
        componentModels.add(new ScreenComponentModel("Driver License Number", "inputtext", true));
        componentModels.add(new ScreenComponentModel("Driver License State", "selector", true));
        componentModels.add(new ScreenComponentModel("Do You have insurance", "text", true));
        componentModels.add(new ScreenComponentModel("Do You have health insurance?", "togglebutton", true));
        componentModels.add(new ScreenComponentModel("thumbnail_insurance", "imageview", false));
        componentModels.add(new ScreenComponentModel("SCAN INSURANCE CARD", "scanButton", false));
        componentModels.add(new ScreenComponentModel("Insurance Card Info 1", "inputtext", false));
        componentModels.add(new ScreenComponentModel("Insurance Card Info 2", "inputtext", false));
        componentModels.add(new ScreenComponentModel("insurance Card Info 3", "inputtext", false));
        componentModels.add(new ScreenComponentModel("NEXT", "button", true));
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
        componentModels.add(new ScreenComponentModel("Got some time?", "heading", true));
        componentModels.add(new ScreenComponentModel("Share some of your health information now and save some precious time during your next appointment", "subHeading", true));
        componentModels.add(new ScreenComponentModel("Do you want to get updates?", "togglebutton", true));
        componentModels.add(new ScreenComponentModel("ADD MORE DETAILS", "button", true));
        componentModels.add(new ScreenComponentModel("I'LL DO THIS LATER", "button", true));
        demographicsMoreDetailsScreenModel.setComponentModels(componentModels);
        return demographicsMoreDetailsScreenModel;
    }
    public List<String> getRaceList(){
        List<String> raceItems = new ArrayList<String>();
        raceItems.add("American or Indian");
        raceItems.add("Asian");
        raceItems.add("Black or African American");
        raceItems.add("Other");
        raceItems.add("Decline to Answer");
        return raceItems;
    }

    public List<String> getStatesList(){
        List<String> statesItems = new ArrayList<String>();
        statesItems.add("AZ");
        statesItems.add("FL");
        statesItems.add("CA");
        statesItems.add("GA");
        statesItems.add("MO");

        return statesItems;
    }


    public List<String> getEthincityList(){
        List<String> ethinicityItems = new ArrayList<String>();
        ethinicityItems.add("Hispanic");
        ethinicityItems.add("White Americans");
        ethinicityItems.add("Other");
        ethinicityItems.add("Decline to Answer");
        return ethinicityItems;
    }

    public List<String> getLanguageList(){
        List<String> languageItems = new ArrayList<String>();
        languageItems.add("English");
        languageItems.add("Spanish");
        languageItems.add("Decline to Answer");
        return languageItems;
    }



    public List<String> getUpdateList(){
        List<String> raceItems = new ArrayList<String>();
        raceItems.add("Mobile Notifications");
        raceItems.add("Email Notifications");
        raceItems.add("Newsletter");
        raceItems.add("Monthly coupons");
        raceItems.add("Marketing Material");
        return raceItems;
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


    public ScreenModel getUpdatesDialogScreenModel() {
        updatesDialogScreenModel = new ScreenModel();
        ArrayList<ScreenComponentModel> componentModels = new ArrayList<ScreenComponentModel>();
        componentModels.add(new ScreenComponentModel("Select Updates", "text", true));
        componentModels.add(new ScreenComponentModel("Mobile Notifications", "checkbox", true));
        componentModels.add(new ScreenComponentModel("Email Notifications", "checkbox", true));
        componentModels.add(new ScreenComponentModel("Newsletter", "checkbox", true));
        componentModels.add(new ScreenComponentModel("Monthly Coupons", "checkbox", true));
        componentModels.add(new ScreenComponentModel("Marketing Material", "checkbox", true));
        componentModels.add(new ScreenComponentModel("CANCEL", "button", false));
        componentModels.add(new ScreenComponentModel("SELECT", "button", false));

        updatesDialogScreenModel.setComponentModels(componentModels);
        return updatesDialogScreenModel;
    }

    public ScreenModel getSignatureScreenModel() {
        signatureScreenModel = new ScreenModel();
        ArrayList<ScreenComponentModel> componentModels = new ArrayList<>();
        componentModels.add(new ScreenComponentModel("Sign HIPAA Confidentiality Agreement", "text", true));
        componentModels.add(new ScreenComponentModel("Sign Consent for Medical Care", "text", true));
        componentModels.add(new ScreenComponentModel("Make sure you’ve read the whole consent form and sign in the box below. If you're unable to sign, you can ask for a legal representative to sign for you.", "text", true));
        componentModels.add(new ScreenComponentModel("I'm unable to sign", "switch", true));
        componentModels.add(new ScreenComponentModel("PATIENT SIGNATURE", "text", true));
        componentModels.add(new ScreenComponentModel("Legal Representative Signature", "text", true));
        componentModels.add(new ScreenComponentModel("Signature", "signaturepad", true));
        componentModels.add(new ScreenComponentModel("Yes, This is my signature", "button", true));
        signatureScreenModel.setComponentModels(componentModels);
        return signatureScreenModel;
    }

}