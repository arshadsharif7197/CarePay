
package com.carecloud.carepaylibray.intake.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class PayloadFindingsModel {

    @SerializedName("uuid")
    @Expose
    private String uuid;
    @SerializedName("businessEntityId")
    @Expose
    private String businessEntityId;
    @SerializedName("patientId")
    @Expose
    private String patientId;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("findings")
    @Expose
    private String findings;

    /**
     * 
     * @return
     *     The uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * 
     * @param uuid
     *     The uuid
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * 
     * @return
     *     The businessEntityId
     */
    public String getBusinessEntityId() {
        return businessEntityId;
    }

    /**
     * 
     * @param businessEntityId
     *     The businessEntityId
     */
    public void setBusinessEntityId(String businessEntityId) {
        this.businessEntityId = businessEntityId;
    }

    /**
     * 
     * @return
     *     The patientId
     */
    public String getPatientId() {
        return patientId;
    }

    /**
     * 
     * @param patientId
     *     The patientId
     */
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    /**
     * 
     * @return
     *     The type
     */
    public String getType() {
        return type;
    }

    /**
     * 
     * @param type
     *     The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 
     * @return
     *     The findings
     */
    public String getFindings() {
        return findings;
    }

    /**
     * 
     * @param findings
     *     The findings
     */
    public void setFindings(String findings) {
        this.findings = findings;
    }

}
