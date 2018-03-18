package com.carecloud.carepaylibray.intake.models;

/**
 * Created by lsoco_user on 9/11/2016.
 * Model class for intake form
 */
public class IntakeFormModel {

    private String intakeModelTitle;
    private String intakeModelCaption;

    public IntakeFormModel(String intakeModelTile, String intakeModelCaption) {
        this.intakeModelTitle = intakeModelTile;
        this.intakeModelCaption = intakeModelCaption;
    }

    public String getIntakeModelTitle() {
        return intakeModelTitle;
    }

    public void setIntakeModelTitle(String intakeModelTitle) {
        this.intakeModelTitle = intakeModelTitle;
    }

    public String getIntakeModelCaption() {
        return intakeModelCaption;
    }

    public void setIntakeModelCaption(String intakeModelCaption) {
        this.intakeModelCaption = intakeModelCaption;
    }
}