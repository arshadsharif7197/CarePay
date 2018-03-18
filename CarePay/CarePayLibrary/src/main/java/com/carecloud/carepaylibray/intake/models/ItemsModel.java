
package com.carecloud.carepaylibray.intake.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class ItemsModel {

    @SerializedName("intake")
    @Expose
    private IntakeModel intake = new IntakeModel();

    /**
     * 
     * @return
     *     The intake
     */
    public IntakeModel getIntake() {
        return intake;
    }

    /**
     * 
     * @param intake
     *     The intake
     */
    public void setIntake(IntakeModel intake) {
        this.intake = intake;
    }

}
