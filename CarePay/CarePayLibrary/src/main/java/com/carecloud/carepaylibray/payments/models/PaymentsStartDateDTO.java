
package com.carecloud.carepaylibray.payments.models;

import com.carecloud.carepaylibray.base.models.BaseFieldValidationModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PaymentsStartDateDTO {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("validations")
    @Expose
    private List<BaseFieldValidationModel> paymentValidations = new ArrayList<>();

    /**
     * 
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return
     *     The label
     */
    public String getLabel() {
        return label;
    }

    /**
     * 
     * @param label
     *     The label
     */
    public void setLabel(String label) {
        this.label = label;
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
     *     The paymentValidations
     */
    public List<BaseFieldValidationModel> getPaymentValidations() {
        return paymentValidations;
    }

    /**
     * 
     * @param paymentValidations
     *     The paymentValidations
     */
    public void setPaymentValidations(List<BaseFieldValidationModel> paymentValidations) {
        this.paymentValidations = paymentValidations;
    }

}
