package com.carecloud.carepay.patient.intakeforms.utils;

import com.carecloud.carepaylibray.intake.models.IntakeFormModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lsoco_user on 9/12/2016.
 * Simulator for parsing the json (part) related to the intake forms
 */
public class JsonFormParseSimulator {
    private List<IntakeFormModel> intakeFormModels;

    public List<IntakeFormModel> getIntakeFormModels() {
        return intakeFormModels;
    }

    public JsonFormParseSimulator() {
        simulateJsonParsing();
    }

    private void simulateJsonParsing() {
        // mock parsing by creating dummy forms
        intakeFormModels = new ArrayList<>();
        intakeFormModels.add(new IntakeFormModel("Reason for Visit",
                                                 "Tell us a bit more about your reason for visiting today."));
        intakeFormModels.add(new IntakeFormModel("General Cardiac Symptoms",
                                                 "Please fill this form about general cardiac symptoms you may have in the past."));
        intakeFormModels.add(new IntakeFormModel("Medical History", "Please fill in the next series of forms about your medical history."));
        intakeFormModels.add(new IntakeFormModel("Medical History", "Please fill in the next series of forms about your medical history."));
        intakeFormModels.add(new IntakeFormModel("Review of symptoms", "Give us some information about the symptoms you're having."));
    }
}