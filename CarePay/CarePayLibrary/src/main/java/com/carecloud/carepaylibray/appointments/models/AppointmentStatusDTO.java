
package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.SerializedName;

/**
 * Model for appointment status.
 */
public class AppointmentStatusDTO {

    @SerializedName("id")
    private Integer id;

    @SerializedName("code")
    private String code;

    @SerializedName("name")
    private String name;

    @SerializedName("updated_at")
    private String lastUpdated;

    @SerializedName(value = "status", alternate = "payload")
    private CheckinStatusDTO checkinStatusDTO = new CheckinStatusDTO();

    @SerializedName("original_name")
    private String originalName;

    @SerializedName("original_code")
    private String originalCode;

    /**
     * @return The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return The code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code The code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public CheckinStatusDTO getCheckinStatusDTO() {
        return checkinStatusDTO;
    }

    public void setCheckinStatusDTO(CheckinStatusDTO checkinStatusDTO) {
        this.checkinStatusDTO = checkinStatusDTO;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getOriginalCode() {
        return originalCode;
    }

    public void setOriginalCode(String originalCode) {
        this.originalCode = originalCode;
    }
}
