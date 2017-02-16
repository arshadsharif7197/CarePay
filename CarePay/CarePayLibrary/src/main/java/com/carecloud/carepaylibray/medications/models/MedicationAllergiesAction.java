package com.carecloud.carepaylibray.medications.models;

/**
 * Created by lmenendez on 2/15/17.
 */

public enum MedicationAllergiesAction {
    ADD("add"),
    DELETE("delete"),
    CURRENT(null);


    private final String maaString;

    private MedicationAllergiesAction(String maaString){
        this.maaString = maaString;
    }

    @Override
    public String toString(){
        return maaString;
    }
}
