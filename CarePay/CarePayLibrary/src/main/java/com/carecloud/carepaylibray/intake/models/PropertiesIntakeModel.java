
package com.carecloud.carepaylibray.intake.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class PropertiesIntakeModel {

    @SerializedName("intake_config_uuid")
    @Expose
    private IntakeConfigUuidModel intakeConfigUuid = new IntakeConfigUuidModel();
    @SerializedName("findings")
    @Expose
    private FindingsModel findings = new FindingsModel();

    /**
     * 
     * @return
     *     The intakeConfigUuid
     */
    public IntakeConfigUuidModel getIntakeConfigUuid() {
        return intakeConfigUuid;
    }

    /**
     * 
     * @param intakeConfigUuid
     *     The intake_config_uuid
     */
    public void setIntakeConfigUuid(IntakeConfigUuidModel intakeConfigUuid) {
        this.intakeConfigUuid = intakeConfigUuid;
    }

    /**
     * 
     * @return
     *     The findings
     */
    public FindingsModel getFindings() {
        return findings;
    }

    /**
     * 
     * @param findings
     *     The findings
     */
    public void setFindings(FindingsModel findings) {
        this.findings = findings;
    }

}
