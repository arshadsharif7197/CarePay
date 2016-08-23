package com.carecloud.carepaylibray.models;

import java.util.ArrayList;

/**
 * Created by Jahirul Bhuiyan on 8/23/2016.
 */
public class WorkFlowModel {

    private ScreenModel selectLanguageScreenModel;

    private ScreenModel loginScreenModel;

    private ScreenModel signupScreenModel;


    public ScreenModel getSelectLanguageScreenModel() {
        selectLanguageScreenModel=new ScreenModel();
        ArrayList<ScreenComponentModel> componentModels= new ArrayList<ScreenComponentModel>();
        componentModels.add(new ScreenComponentModel("","choice",true));
        componentModels.add(new ScreenComponentModel("...","button",false));
        componentModels.add(new ScreenComponentModel("Continue","button",false));
        selectLanguageScreenModel.setComponentModels(componentModels);
        return selectLanguageScreenModel;
    }

    public void setSelectLanguageScreenModel(ScreenModel selectLanguageScreenModel) {
        this.selectLanguageScreenModel = selectLanguageScreenModel;
    }

    public ScreenModel getLoginScreenModel() {
        loginScreenModel=new ScreenModel();
        ArrayList<ScreenComponentModel> componentModels= new ArrayList<ScreenComponentModel>();
        componentModels.add(new ScreenComponentModel("User Name","inputtext",true));
        componentModels.add(new ScreenComponentModel("Password","password",true));
        componentModels.add(new ScreenComponentModel("SIGN IN","button",false));
        componentModels.add(new ScreenComponentModel("CREATE NEW ACCOUNT","button",false));
        componentModels.add(new ScreenComponentModel("Change Language","text",false));
        componentModels.add(new ScreenComponentModel("Forgot Password","text",false));
        loginScreenModel.setComponentModels(componentModels);
        return loginScreenModel;
    }

    public void setLoginScreenModel(ScreenModel loginScreenModel) {
        this.loginScreenModel = loginScreenModel;
    }

    public ScreenModel getSignupScreenModel() {
        signupScreenModel=new ScreenModel();
        ArrayList<ScreenComponentModel> componentModels= new ArrayList<ScreenComponentModel>();
        componentModels.add(new ScreenComponentModel("Full Name","inputtext",true));
        componentModels.add(new ScreenComponentModel("Email","email",true));
        componentModels.add(new ScreenComponentModel("Create Password","password",true));
        componentModels.add(new ScreenComponentModel("Repeat Password","Password",true));
        componentModels.add(new ScreenComponentModel("SIGN UP","button",false));
        componentModels.add(new ScreenComponentModel("Already Have an Account","text",false));
        signupScreenModel.setComponentModels(componentModels);
        return signupScreenModel;
    }

    public void setSignupScreenModel(ScreenModel signupScreenModel) {
        this.signupScreenModel = signupScreenModel;
    }
}
