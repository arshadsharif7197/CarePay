package com.carecloud.carepaylibray.consentforms.models.transitions;

/**
 * Created by Rahul on 10/23/16.
 */


        import com.google.gson.annotations.Expose;
        import com.google.gson.annotations.SerializedName;

public class ConsentFormsPracticeMgmtValidationDTO {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("value")
    @Expose
    private Boolean value;

    /**
     *
     * @return
     * The type
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @param type
     * The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     *
     * @return
     * The value
     */
    public Boolean getValue() {
        return value;
    }

    /**
     *
     * @param value
     * The value
     */
    public void setValue(Boolean value) {
        this.value = value;
    }

}